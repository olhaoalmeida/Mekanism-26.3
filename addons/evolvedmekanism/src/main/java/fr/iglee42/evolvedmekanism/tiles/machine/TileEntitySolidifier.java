package fr.iglee42.evolvedmekanism.tiles.machine;

import fr.iglee42.evolvedmekanism.interfaces.EMInputRecipeCache;
import fr.iglee42.evolvedmekanism.interfaces.SolidificationCachedRecipe;
import fr.iglee42.evolvedmekanism.recipes.SolidificationRecipe;
import fr.iglee42.evolvedmekanism.recipes.vanilla_input.SingleItemBiFluidRecipeInput;
import fr.iglee42.evolvedmekanism.recipeviewers.EMRecipeViewersTypes;
import fr.iglee42.evolvedmekanism.registries.EMBlocks;
import fr.iglee42.evolvedmekanism.registries.EMRecipeType;
import java.util.List;
import java.util.Optional;
import mekanism.api.IContentsListener;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.fluid.IFluidTank;
import mekanism.api.functions.IntObjectToIntFunction;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.recipes.book.CombiningBookCategory;
import mekanism.api.recipes.book.CombiningBookCategory.CombiningBookInfo;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.api.upgrade.Upgrade;
import mekanism.api.upgrade.UpgradeIds;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.holder.container.IContainerHolder;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
import mekanism.common.capabilities.holder.single.ISingleContainerHolder;
import mekanism.common.capabilities.holder.single.SingleConfigHolder;
import mekanism.common.integration.computer.ComputerConstants;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerFluidTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.warning.WarningTracker.WarningType;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.FluidSlotInfo;
import mekanism.common.tile.prefab.TileEntityProgressMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderGetter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;

public class TileEntitySolidifier extends TileEntityProgressMachine<SolidificationRecipe> implements
      EMInputRecipeCache.ItemFluidFluidRecipeLookupHandler<SolidificationRecipe> {

    public static final RecipeError NOT_ENOUGH_ITEM_INPUT_ERROR = RecipeError.create();
    public static final RecipeError NOT_ENOUGH_FLUID_INPUT_ERROR = RecipeError.create();
    public static final RecipeError NOT_ENOUGH_EXTRA_FLUID_INPUT_ERROR = RecipeError.create();
    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
          RecipeError.NOT_ENOUGH_ENERGY,
          NOT_ENOUGH_ITEM_INPUT_ERROR,
          NOT_ENOUGH_FLUID_INPUT_ERROR,
          NOT_ENOUGH_EXTRA_FLUID_INPUT_ERROR,
          RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
          RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT
    );
    private static final int BASE_DURATION = 100;
    private static final IntObjectToIntFunction<TileEntitySolidifier> BASE_ENERGY_CALCULATOR = (base, tile) -> base + tile.getRecipeEnergyRequired();

    @UnknownNullability//Initialized via getInitialFluidTanks
    @WrappingComputerMethod(wrapper = ComputerFluidTankWrapper.class, methodNames = {"getInputFluid", "getInputFluidCapacity", "getInputFluidNeeded",
                                                                                     "getInputFluidFilledPercentage"}, docPlaceholder = "fluid input")
    public BasicFluidTank inputFluidTank;
    @UnknownNullability//Initialized via getInitialFluidTanks
    @WrappingComputerMethod(wrapper = ComputerFluidTankWrapper.class, methodNames = {"getExtraInputFluid", "getExtraInputFluidCapacity", "getExtraInputFluidNeeded",
                                                                                     "getExtraInputFluidFilledPercentage"}, docPlaceholder = "fluid extra input")
    public BasicFluidTank inputFluidExtraTank;

    private int recipeEnergyRequired = 0;
    private final IOutputHandler<ItemStackTemplate> outputHandler;
    private final IInputHandler<Item, ItemStack> itemInputHandler;
    private final IInputHandler<Fluid, FluidStack> fluidInputHandler;
    private final IInputHandler<Fluid, FluidStack> fluidExtraInputHandler;
    private final RecipeContainers recipeContainers;

    @UnknownNullability//Initialized via getInitialEnergyContainer
    private MachineEnergyContainer<TileEntitySolidifier> energyContainer;
    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getInputItem", docPlaceholder = "item input slot")
    InputInventorySlot inputSlot;
    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getOutputItem", docPlaceholder = "item output slot")
    OutputInventorySlot outputSlot;
    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy slot")
    EnergyInventorySlot energySlot;

    public TileEntitySolidifier(BlockPos pos, BlockState state) {
        super(EMBlocks.SOLIDIFIER, pos, state, TRACKED_ERROR_TYPES, BASE_DURATION);
        configComponent.setupItemIOConfig(inputSlot, outputSlot, energySlot);
        ConfigInfo fluidConfig = configComponent.getConfig(TransmissionType.FLUID);
        if (fluidConfig != null) {
            fluidConfig.addSlotInfo(DataType.INPUT_1, new FluidSlotInfo(true, false, inputFluidExtraTank));
            fluidConfig.addSlotInfo(DataType.INPUT_2, new FluidSlotInfo(true, false, inputFluidTank));
            fluidConfig.setCanEject(false);
        }
        configComponent.setupInputConfig(TransmissionType.ENERGY, energyContainer);

        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM);

        itemInputHandler = InputHelper.getInputHandler(inputSlot, NOT_ENOUGH_ITEM_INPUT_ERROR);
        fluidInputHandler = InputHelper.getInputHandler(inputFluidTank, NOT_ENOUGH_FLUID_INPUT_ERROR);
        fluidExtraInputHandler = InputHelper.getInputHandler(inputFluidExtraTank, NOT_ENOUGH_EXTRA_FLUID_INPUT_ERROR);
        outputHandler = OutputHelper.getOutputHandler(outputSlot, RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
        recipeContainers = new RecipeContainers(List.of(inputSlot, inputFluidTank, inputFluidExtraTank), outputSlot);
    }

    @Override
    protected boolean hasItemOutput() {
        return true;
    }

    @Override
    protected IContainerHolder<IFluidTank> getInitialFluidTanks(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        MekContainerHelper<IFluidTank> builder = MekContainerHelper.forSideWithFluidConfig(this);
        builder.addContainer(inputFluidTank = BasicFluidTank.input(10_000, (fluid, _) -> containsRecipeBAC(inputSlot.resource(), fluid, inputFluidExtraTank.resource()),
              this::containsRecipeB, recipeCacheListener));
        builder.addContainer(inputFluidExtraTank = BasicFluidTank.input(10_000, (fluid, _) -> containsRecipeCAB(inputSlot.resource(), inputFluidTank.resource(), fluid),
              this::containsRecipeC, recipeCacheListener));
        return builder.build();
    }

    @Override
    protected ISingleContainerHolder<IEnergyContainer> getInitialEnergyContainer(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        energyContainer = MachineEnergyContainer.input(this, BASE_ENERGY_CALCULATOR, recipeCacheUnpauseListener);
        return SingleConfigHolder.energy(energyContainer, this);
    }

    @Override
    protected IContainerHolder<IInventorySlot> getInitialInventory(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        MekContainerHelper<IInventorySlot> builder = MekContainerHelper.forSideWithItemConfig(this);
        builder.addContainer(inputSlot = InputInventorySlot.at((item, _) -> containsRecipeABC(item, inputFluidTank.resource(), inputFluidExtraTank.resource()), this::containsRecipeA,
                    recipeCacheListener, 54, 35))
              .tracksWarnings(slot -> slot.warning(WarningType.NO_MATCHING_RECIPE, getWarningCheck(NOT_ENOUGH_ITEM_INPUT_ERROR)));
        builder.addContainer(outputSlot = OutputInventorySlot.at(recipeCacheUnpauseListener, 116, 35))
              .setOnTakeCheck(recipeCacheLookupMonitor)
              .tracksWarnings(slot -> slot.warning(WarningType.NO_SPACE_IN_OUTPUT, getWarningCheck(RecipeError.NOT_ENOUGH_OUTPUT_SPACE)));
        builder.addContainer(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 141, 35));
        return builder.build();
    }

    @Override
    public void onCachedRecipeChanged(@Nullable CachedRecipe<SolidificationRecipe> cachedRecipe, int cacheIndex, ContextMap recipeContext) {
        super.onCachedRecipeChanged(cachedRecipe, cacheIndex, recipeContext);
        int recipeDuration;
        if (cachedRecipe == null) {
            recipeDuration = BASE_DURATION;
            recipeEnergyRequired = 0;
        } else {
            SolidificationRecipe recipe = cachedRecipe.getRecipe();
            recipeDuration = recipe.getDuration();
            recipeEnergyRequired = (int) recipe.getEnergyRequired();
        }
        boolean update = baseTicksRequired != recipeDuration;
        baseTicksRequired = recipeDuration;
        HolderGetter<Upgrade> upgrades = getUpgradeLookup(recipeContext);
        if (update) {
            Optional<Reference<Upgrade>> upgrade = upgrades.get(UpgradeIds.SPEED);
            if (upgrade.isPresent()) {
                Reference<Upgrade> speedUpgrade = upgrade.get();
                recalculateUpgrades(upgrades, speedUpgrade, getUpgrades(speedUpgrade));
                //Note: We can early exit if we end up recalculating the upgrades, as that will handle updating the energy container's energy
                return;
            }
        }
        //Ensure we take our recipe's energy per tick into account
        energyContainer.updateEnergy(upgrades);
    }

    @Override
    protected boolean onUpdateServer(ServerLevel level) {
        boolean send = super.onUpdateServer(level);
        energySlot.fillContainerOrConvert(null);
        recipeCacheLookupMonitor.updateAndProcess(level);
        return send;
    }

    public int getRecipeEnergyRequired() {
        return recipeEnergyRequired;
    }

    @Override
    public IMekanismRecipeTypeProvider<SingleItemBiFluidRecipeInput, CombiningBookCategory, CombiningBookInfo, SolidificationRecipe, EMInputRecipeCache.ItemFluidFluid<SolidificationRecipe>> getRecipeType() {
        return EMRecipeType.SOLIDIFICATION;
    }

    @Nullable
    @Override
    public RecipeHolder<SolidificationRecipe> getRecipe(int cacheIndex) {
        return findFirstRecipe(itemInputHandler, fluidInputHandler, fluidExtraInputHandler);
    }

    @Override
    public CachedRecipe<SolidificationRecipe> createNewCachedRecipe(SolidificationRecipe recipe, int cacheIndex, ContextMap recipeContext) {
        return setCommonSettings(new SolidificationCachedRecipe(recipe, recheckAllRecipeErrors, itemInputHandler, fluidInputHandler, fluidExtraInputHandler, outputHandler));
    }

    @Override
    public RecipeContainers getRecipeContainers(int cacheIndex) {
        return recipeContainers;
    }

    @Override
    public MachineEnergyContainer<TileEntitySolidifier> energyContainer() {
        return energyContainer;
    }

    public MachineEnergyContainer<TileEntitySolidifier> getEnergyContainer() {
        return energyContainer;
    }

    //Methods relating to IComputerTile
    @ComputerMethod(methodDescription = ComputerConstants.DESCRIPTION_GET_ENERGY_USAGE)
    long getEnergyUsage() {
        return getActive() ? energyContainer.getEnergyPerTick() : 0;
    }
    //End methods IComputerTile

    @Override
    public @Nullable IRecipeViewerRecipeType<SolidificationRecipe> recipeViewerType() {
        return EMRecipeViewersTypes.SOLIDIFICATION;
    }
}
