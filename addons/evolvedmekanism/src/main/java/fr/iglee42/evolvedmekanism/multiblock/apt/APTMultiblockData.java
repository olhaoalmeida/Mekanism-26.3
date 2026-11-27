package fr.iglee42.evolvedmekanism.multiblock.apt;

import fr.iglee42.evolvedmekanism.config.EMConfig;
import fr.iglee42.evolvedmekanism.registries.EMRecipeType;
import mekanism.api.AutomationType;
import mekanism.api.SerializationConstants;
import mekanism.api.chemical.ChemicalAttributeValidator;
import mekanism.api.chemical.ChemicalResource;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.math.MathUtils;
import mekanism.api.recipes.ItemStackChemicalToItemStackRecipe;
import mekanism.api.recipes.book.CompressingBookCategory;
import mekanism.api.recipes.book.CompressingBookCategory.CompressingBookInfo;
import mekanism.common.capabilities.chemical.VariableCapacityChemicalTank;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.annotation.SyntheticComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.sync.dynamic.ContainerSync;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.lib.Color;
import mekanism.common.lib.multiblock.IValveHandler;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.util.MekanismUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

public class APTMultiblockData extends MultiblockData implements IValveHandler{

    @ContainerSync
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class, methodNames = {"getInput", "getInputCapacity", "getInputNeeded", "getInputFilledPercentage"}, docPlaceholder = "input tank")
    public IChemicalTank inputTank;

    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getInputSlot", docPlaceholder = "input slot")
    InputInventorySlot inputSlot;
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getOutputSlot", docPlaceholder = "output slot")
    OutputInventorySlot outputSlot;

    @ContainerSync
    public int progress;
    @ContainerSync
    public int defaultRecipeProgress = -1;
    public int lastProgress;

    @ContainerSync
    @SyntheticComputerMethod(getter = "getSuperchargers", getterDescription = "How many superchargers this APT has")
    public int superchargingElements;


    @ContainerSync
    public IEnergyContainer energyContainer;

    @Nullable
    public RecipeHolder<ItemStackChemicalToItemStackRecipe<CompressingBookCategory, CompressingBookInfo>> currentRecipe;

    public float prevGasScale;

    public APTMultiblockData(TileEntityAPTCasing tile) {
        super(tile);
        chemicalTanks.add(inputTank = VariableCapacityChemicalTank.input(this, EMConfig.general.aptInputStorage::getOrDefault, this::hasRecipeWithChemical,
              ChemicalAttributeValidator.ALWAYS_ALLOW, createSaveAndComparator()));
        inventorySlots.add(inputSlot = InputInventorySlot.at((item, _) -> hasRecipeForInputs(item, inputTank.resource()), this::hasRecipeWithItem,
              createSaveAndComparator(), 28, 40));
        inventorySlots.add(outputSlot = OutputInventorySlot.at(createSaveAndComparator(), 132, 40));
        energyContainer = BasicEnergyContainer.create(EMConfig.general.aptEnergyStorage.getOrDefault(), _ -> isFormed(), _ -> isFormed(), createSaveAndComparator());
    }

    @Override
    protected IEnergyContainer energyContainer() {
        return energyContainer;
    }

    private List<RecipeHolder<ItemStackChemicalToItemStackRecipe<CompressingBookCategory, CompressingBookInfo>>> getAptRecipes() {
        return EMRecipeType.APT.getRecipeType().getRecipes(getLevel());
    }

    private boolean hasRecipeWithItem(ItemResource item) {
        return getAptRecipes().stream().anyMatch(r -> r.value().getItemInput().testType(item));
    }

    private boolean hasRecipeWithChemical(ChemicalResource chemical) {
        return getAptRecipes().stream().anyMatch(r -> r.value().getChemicalInput().testType(chemical));
    }

    private boolean hasRecipeForInputs(ItemResource item, ChemicalResource chemical) {
        if (chemical.isEmpty()) {
            return hasRecipeWithItem(item);
        }
        return getAptRecipes().stream().anyMatch(r -> r.value().getItemInput().testType(item) && r.value().getChemicalInput().testType(chemical));
    }

    private ChemicalStack getInputChemical() {
        return inputTank.resource().toStack(inputTank.amountAsInt());
    }

    @Override
    public boolean tick(ServerLevel world) {
        boolean needsPacket = super.tick(world);

        ItemStack inputStack = inputSlot.asItemStack();
        ChemicalStack inputChemical = getInputChemical();
        if (currentRecipe == null) {
            for (RecipeHolder<ItemStackChemicalToItemStackRecipe<CompressingBookCategory, CompressingBookInfo>> r : getAptRecipes()) {
                if (r.value().test(inputStack, inputChemical)) {
                    currentRecipe = r;
                    markForSave();
                    progress = (int) ((EMConfig.general.aptDefaultDuration.getOrDefault() * (r.value().getChemicalInput().getNeededAmount(inputChemical) / 100)) / (superchargingElements + 1));
                    defaultRecipeProgress = progress;
                    break;
                }
            }
        } else {
            ItemStackChemicalToItemStackRecipe<CompressingBookCategory, CompressingBookInfo> recipe = currentRecipe.value();
            if (!recipe.test(inputStack, inputChemical)) {
                currentRecipe = null;
                progress = 0;
                defaultRecipeProgress = -1;
                markForSave();
                return true;
            }
            if (canProcess(recipe, inputStack, inputChemical)) {
                try (Transaction transaction = Transaction.openRoot()) {
                    energyContainer.extract(consumption(), transaction, AutomationType.INTERNAL);
                    progress--;
                    if (progress == 0) {
                        ItemStack output = recipe.getOutput(inputStack, inputChemical).create();
                        inputSlot.extract(inputSlot.resource(), recipe.getItemInput().getNeededAmount(inputStack), transaction, AutomationType.INTERNAL);
                        inputTank.extract(inputTank.resource(), recipe.getChemicalInput().getNeededAmount(inputChemical), transaction, AutomationType.INTERNAL);
                        outputSlot.insert(ItemResource.of(output), output.getCount(), transaction, AutomationType.INTERNAL);
                        currentRecipe = null;
                        defaultRecipeProgress = -1;
                    }
                    transaction.commit();
                }
                needsPacket = true;
            }
        }

        if (lastProgress != progress) {
            lastProgress = progress;
            markForSave();
            needsPacket = true;
        }
        float gasScale = MekanismUtils.getScale(prevGasScale, inputTank);
        if (gasScale != prevGasScale) {
            needsPacket = true;
            prevGasScale = gasScale;
        }

        return needsPacket;
    }

    private int consumption() {
        return MathUtils.clampToInt(EMConfig.general.aptEnergyConsumption.getOrDefault());
    }

    private boolean canProcess(ItemStackChemicalToItemStackRecipe<CompressingBookCategory, CompressingBookInfo> recipe, ItemStack inputStack, ChemicalStack inputChemical) {
        if (energyContainer.getAmountAsLong() < EMConfig.general.aptEnergyConsumption.getOrDefault()) {
            return false;
        }
        if (outputSlot.isEmpty()) {
            return true;
        }
        ItemStack output = recipe.getOutput(inputStack, inputChemical).create();
        //Simulate the insertion by never committing the transaction
        try (Transaction transaction = Transaction.openRoot()) {
            return outputSlot.insert(ItemResource.of(output), output.getCount(), transaction, AutomationType.INTERNAL) == output.getCount();
        }
    }

    @Override
    public void writeUpdateTag(ValueOutput output) {
        super.writeUpdateTag(output);
        output.putInt(SerializationConstants.PROGRESS, progress);
        output.putInt("defaultRecipeProgress", defaultRecipeProgress);
        output.putFloat(SerializationConstants.SCALE, prevGasScale);
        inputTank.serialize(output.child(SerializationConstants.CHEMICAL));
    }

    @Override
    public void readUpdateTag(ValueInput input) {
        super.readUpdateTag(input);
        prevGasScale = input.getFloatOr(SerializationConstants.SCALE, 0F);
        progress = input.getIntOr(SerializationConstants.PROGRESS, 0);
        defaultRecipeProgress = input.getIntOr("defaultRecipeProgress", -1);
        inputTank.deserialize(input.childOrEmpty(SerializationConstants.CHEMICAL));
    }

    @Override
    protected int getMultiblockRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(inputTank.amountAsLong(), inputTank.capacityAsLong(inputTank.resource()));
    }

    public boolean handlesSound(TileEntityAPTCasing tile) {
        return tile.getBlockPos().equals(getMinPos().offset(3, 0, 0)) ||
               tile.getBlockPos().equals(getMaxPos().offset(-3, 0, 0));
    }

    public double getScaledProgress() {
        return defaultRecipeProgress == -1 ? 0 : (double) (defaultRecipeProgress - progress) / defaultRecipeProgress;
    }

    public Color getColor() {
        return inputTank.isEmpty() ? Color.WHITE : Color.rgb(inputTank.resource().value().tint());
    }
}
