package fr.iglee42.evolvedmekanism.tiles.machine;

import fr.iglee42.evolvedmekanism.interfaces.ChemixerCachedRecipe;
import fr.iglee42.evolvedmekanism.interfaces.EMInputRecipeCache;
import fr.iglee42.evolvedmekanism.recipes.ChemixerRecipe;
import fr.iglee42.evolvedmekanism.recipes.vanilla_input.BiItemChemicalRecipeInput;
import fr.iglee42.evolvedmekanism.registries.EMBlocks;
import fr.iglee42.evolvedmekanism.registries.EMRecipeType;
import fr.iglee42.evolvedmekanism.interfaces.EMUpgradeIds;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.ChemicalAttributeValidator;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalResource;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.capabilities.holder.container.IContainerHolder;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
import mekanism.common.capabilities.holder.single.ISingleContainerHolder;
import mekanism.common.capabilities.holder.single.SingleConfigHolder;
import mekanism.common.util.MekanismUtils;
import mekanism.api.recipes.book.InjectingBookCategory;
import mekanism.api.recipes.book.InjectingBookCategory.InjectingBookInfo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.core.RegistryAccess;
import java.util.function.Supplier;
import org.jetbrains.annotations.UnknownNullability;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.integration.computer.ComputerConstants;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.warning.WarningTracker.WarningType;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.lookup.ITripleRecipeLookupHandler;
import mekanism.common.tile.prefab.TileEntityProgressMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class TileEntityChemixer extends TileEntityProgressMachine<ChemixerRecipe> implements
      ITripleRecipeLookupHandler<Item, ItemStack, Item, ItemStack, Chemical, ChemicalStack, ChemixerRecipe, EMInputRecipeCache.ItemItemChemical<ChemixerRecipe>> {

    public static final RecipeError NOT_ENOUGH_ITEM_INPUT_ERROR = RecipeError.create();
    public static final RecipeError NOT_ENOUGH_SECONDARY_INPUT = RecipeError.create();
    public static final RecipeError NOT_ENOUGH_GAS_INPUT_ERROR = RecipeError.create();
    public static final RecipeError NOT_ENOUGH_SPACE_ITEM_OUTPUT_ERROR = RecipeError.create();
    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
          RecipeError.NOT_ENOUGH_ENERGY,
          NOT_ENOUGH_ITEM_INPUT_ERROR,
          NOT_ENOUGH_SECONDARY_INPUT,
          NOT_ENOUGH_GAS_INPUT_ERROR,
          NOT_ENOUGH_SPACE_ITEM_OUTPUT_ERROR,
          RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT
    );
    private static final int BASE_DURATION = 100;
    public static final long MAX_GAS = 10_000;

    @UnknownNullability//Initialized via getInitialChemicalTanks
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class, methodNames = {"getInputChemical", "getInputChemicalCapacity", "getInputChemicalNeeded",
                                                                                        "getInputChemicalFilledPercentage"}, docPlaceholder = "chemical input")
    public IChemicalTank inputChemicalTank;
    private final IOutputHandler<ItemStackTemplate> outputHandler;
    private final IInputHandler<Item, ItemStack> itemInputHandler;
    private final IInputHandler<Item, ItemStack> extraInputHandler;
    private final IInputHandler<Chemical, ChemicalStack> chemicalInputHandler;
    private final RecipeContainers recipeContainers;

    @UnknownNullability//Initialized via getInitialEnergyContainer
    private MachineEnergyContainer<TileEntityChemixer> energyContainer;
    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getInputItem", docPlaceholder = "item input slot")
    InputInventorySlot mainInputSlot;
    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getSecondaryInput", docPlaceholder = "secondary input slot")
    InputInventorySlot extraInputSlot;
    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getOutputItem", docPlaceholder = "item output slot")
    OutputInventorySlot outputSlot;
    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy slot")
    EnergyInventorySlot energySlot;

    public TileEntityChemixer(BlockPos pos, BlockState state) {
        super(EMBlocks.CHEMIXER, pos, state, TRACKED_ERROR_TYPES, BASE_DURATION);
        configComponent.setupItemIOExtraConfig(mainInputSlot, outputSlot,extraInputSlot, energySlot);
        configComponent.setupInputConfig(TransmissionType.CHEMICAL, inputChemicalTank);
        configComponent.setupInputConfig(TransmissionType.ENERGY, energyContainer);

        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.CHEMICAL)
              .setCanChemicalTankEject((_, tank) -> tank != inputChemicalTank);

        itemInputHandler = InputHelper.getInputHandler(mainInputSlot, NOT_ENOUGH_ITEM_INPUT_ERROR);
        extraInputHandler = InputHelper.getInputHandler(extraInputSlot, RecipeError.NOT_ENOUGH_SECONDARY_INPUT);
        chemicalInputHandler = InputHelper.getInputHandler(inputChemicalTank, NOT_ENOUGH_GAS_INPUT_ERROR);
        outputHandler = OutputHelper.getOutputHandler(outputSlot, NOT_ENOUGH_SPACE_ITEM_OUTPUT_ERROR);
        recipeContainers = new RecipeContainers(List.of(mainInputSlot, extraInputSlot, inputChemicalTank), outputSlot);
    }

    @Override
    protected boolean hasItemOutput() {
        return true;
    }

    @Override
    public IContainerHolder<IChemicalTank> getInitialChemicalTanks(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        MekContainerHelper<IChemicalTank> builder = MekContainerHelper.forSideWithChemicalConfig(this);
        Supplier<RegistryAccess> registryAccess = MekanismUtils.registryAccess(this::getLevel);
        //Allow extracting out of the input chemical tank if it isn't external
        builder.addContainer(inputChemicalTank = BasicChemicalTank.create(MAX_GAS, (_, type) -> type != AutomationType.EXTERNAL,
              (chemical, _) -> {
                  boolean flag = EMUpgradeIds.getUpgrades(this, EMUpgradeIds.RADIOACTIVE) > 0;
                  if (!flag && chemical.isRadioactive(registryAccess.get())) {
                      return false;
                  }
                  return containsRecipeCAB(mainInputSlot.resource(), extraInputSlot.resource(), chemical);
              }, this::containsRecipeC, ChemicalAttributeValidator.ALWAYS_ALLOW, recipeCacheListener));
        return builder.build();
    }

    @Override
    protected ISingleContainerHolder<IEnergyContainer> getInitialEnergyContainer(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        energyContainer = MachineEnergyContainer.input(this, recipeCacheUnpauseListener);
        return SingleConfigHolder.energy(energyContainer, this);
    }

    @Override
    protected IContainerHolder<IInventorySlot> getInitialInventory(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        MekContainerHelper<IInventorySlot> builder = MekContainerHelper.forSideWithItemConfig(this);
        builder.addContainer(mainInputSlot = InputInventorySlot.at((item, _) -> containsRecipeABC(item, extraInputSlot.resource(), inputChemicalTank.resource()), this::containsRecipeA,
                    recipeCacheListener, 64, 17))
              .tracksWarnings(slot -> slot.warning(WarningType.NO_MATCHING_RECIPE, getWarningCheck(NOT_ENOUGH_ITEM_INPUT_ERROR)));
        builder.addContainer(extraInputSlot = InputInventorySlot.at((item, _) -> containsRecipeBAC(mainInputSlot.resource(), item, inputChemicalTank.resource()), this::containsRecipeB,
                    recipeCacheListener, 64, 53))
              .tracksWarnings(slot -> slot.warning(WarningType.NO_MATCHING_RECIPE, getWarningCheck(RecipeError.NOT_ENOUGH_SECONDARY_INPUT)));
        builder.addContainer(outputSlot = OutputInventorySlot.at(recipeCacheUnpauseListener, 116, 35))
              .setOnTakeCheck(recipeCacheLookupMonitor)
              .tracksWarnings(slot -> slot.warning(WarningType.NO_SPACE_IN_OUTPUT, getWarningCheck(NOT_ENOUGH_SPACE_ITEM_OUTPUT_ERROR)));
        builder.addContainer(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 141, 35));
        return builder.build();
    }

    @Override
    protected boolean onUpdateServer(ServerLevel level) {
        boolean sendUpdatePacket = super.onUpdateServer(level);
        energySlot.fillContainerOrConvert(null);
        recipeCacheLookupMonitor.updateAndProcess(level);
        return sendUpdatePacket;
    }

    @Override
    public IMekanismRecipeTypeProvider<BiItemChemicalRecipeInput, InjectingBookCategory, InjectingBookInfo, ChemixerRecipe, EMInputRecipeCache.ItemItemChemical<ChemixerRecipe>> getRecipeType() {
        return EMRecipeType.CHEMIXING;
    }

    @Nullable
    @Override
    public RecipeHolder<ChemixerRecipe> getRecipe(int cacheIndex) {
        return findFirstRecipe(itemInputHandler, extraInputHandler, chemicalInputHandler);
    }

    @Override
    public CachedRecipe<ChemixerRecipe> createNewCachedRecipe(ChemixerRecipe recipe, int cacheIndex, ContextMap recipeContext) {
        return setCommonSettings(new ChemixerCachedRecipe(recipe, recheckAllRecipeErrors, itemInputHandler, extraInputHandler, chemicalInputHandler, outputHandler));
    }

    @Override
    public RecipeContainers getRecipeContainers(int cacheIndex) {
        return recipeContainers;
    }

    @Override
    public MachineEnergyContainer<TileEntityChemixer> energyContainer() {
        return energyContainer;
    }


    //Methods relating to IComputerTile
    @ComputerMethod(methodDescription = ComputerConstants.DESCRIPTION_GET_ENERGY_USAGE)
    long getEnergyUsage() {
        return getActive() ? energyContainer.getEnergyPerTick() : 0;
    }
    //End methods IComputerTile

}
