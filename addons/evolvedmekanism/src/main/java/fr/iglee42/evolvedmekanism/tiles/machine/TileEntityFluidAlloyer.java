package fr.iglee42.evolvedmekanism.tiles.machine;

import fr.iglee42.evolvedmekanism.interfaces.EMInputRecipeCache;
import fr.iglee42.evolvedmekanism.interfaces.ThreeInputCachedRecipe;
import fr.iglee42.evolvedmekanism.recipes.FluidAlloyingRecipe;
import fr.iglee42.evolvedmekanism.recipes.vanilla_input.TriFluidRecipeInput;
import fr.iglee42.evolvedmekanism.recipeviewers.EMRecipeViewersTypes;
import fr.iglee42.evolvedmekanism.registries.EMBlocks;
import fr.iglee42.evolvedmekanism.registries.EMRecipeType;
import fr.iglee42.evolvedmekanism.utils.FluidBucketHelper;
import java.util.List;
import mekanism.api.IContentsListener;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.fluid.IFluidTank;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.recipes.book.CombiningBookCategory;
import mekanism.api.recipes.book.CombiningBookCategory.CombiningBookInfo;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.holder.container.IContainerHolder;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
import mekanism.common.capabilities.holder.single.ISingleContainerHolder;
import mekanism.common.capabilities.holder.single.SingleConfigHolder;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerFluidTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.FluidInventorySlot;
import mekanism.common.inventory.warning.WarningTracker.WarningType;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.lookup.ITripleRecipeLookupHandler;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.FluidSlotInfo;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.tile.prefab.TileEntityProgressMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;

public class TileEntityFluidAlloyer extends TileEntityProgressMachine<FluidAlloyingRecipe> implements
      ITripleRecipeLookupHandler<Fluid, FluidStack, Fluid, FluidStack, Fluid, FluidStack, FluidAlloyingRecipe, EMInputRecipeCache.TripleFluid<FluidAlloyingRecipe>> {

    public static final int MAX_FLUID = 16_000;
    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
          RecipeError.NOT_ENOUGH_ENERGY,
          RecipeError.NOT_ENOUGH_INPUT,
          RecipeError.NOT_ENOUGH_SECONDARY_INPUT,
          RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
          RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT
    );

    @UnknownNullability//Initialized via getInitialFluidTanks
    @WrappingComputerMethod(wrapper = ComputerFluidTankWrapper.class, methodNames = {"getInput", "getInputCapacity", "getInputNeeded", "getInputFilledPercentage"}, docPlaceholder = "main input tank")
    public BasicFluidTank inputTank;
    @UnknownNullability//Initialized via getInitialFluidTanks
    @WrappingComputerMethod(wrapper = ComputerFluidTankWrapper.class, methodNames = {"getSecondaryInput", "getSecondaryInputCapacity", "getSecondaryInputNeeded", "getSecondaryInputFilledPercentage"}, docPlaceholder = "secondary input tank")
    public BasicFluidTank extraTank;
    @UnknownNullability//Initialized via getInitialFluidTanks
    @WrappingComputerMethod(wrapper = ComputerFluidTankWrapper.class, methodNames = {"getTertiaryInput", "getTertiaryInputCapacity", "getTertiaryInputNeeded", "getTertiaryInputFilledPercentage"}, docPlaceholder = "tertiary input tank")
    public BasicFluidTank tertiaryTank;
    @UnknownNullability//Initialized via getInitialFluidTanks
    @WrappingComputerMethod(wrapper = ComputerFluidTankWrapper.class, methodNames = {"getOutput", "getOutputCapacity", "getOutputNeeded", "getOutputFilledPercentage"}, docPlaceholder = "output tank")
    public BasicFluidTank outputTank;

    private final IInputHandler<Fluid, FluidStack> inputHandler;
    private final IInputHandler<Fluid, FluidStack> extraHandler;
    private final IInputHandler<Fluid, FluidStack> tertiaryHandler;
    private final IOutputHandler<FluidStackTemplate> outputHandler;
    private final RecipeContainers recipeContainers;

    @UnknownNullability//Initialized via getInitialEnergyContainer
    private MachineEnergyContainer<TileEntityFluidAlloyer> energyContainer;
    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getInputItem", docPlaceholder = "main fill slot")
    FluidInventorySlot inputSlot;
    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getSecondaryInputItem", docPlaceholder = "secondary fill slot")
    FluidInventorySlot extraSlot;
    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getTertiaryInputItem", docPlaceholder = "tertiary fill slot")
    FluidInventorySlot tertiarySlot;
    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getOutputItem", docPlaceholder = "output drain slot")
    FluidInventorySlot outputSlot;
    @UnknownNullability//Initialized via getInitialInventory
    EnergyInventorySlot energySlot;

    public TileEntityFluidAlloyer(BlockPos pos, BlockState state) {
        super(EMBlocks.FLUID_ALLOYER, pos, state, TRACKED_ERROR_TYPES, 100);
        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.INPUT, new InventorySlotInfo(true, false, inputSlot));
            itemConfig.addSlotInfo(DataType.EXTRA, new InventorySlotInfo(true, false, extraSlot));
            itemConfig.addSlotInfo(DataType.INPUT_2, new InventorySlotInfo(true, false, tertiarySlot));
            itemConfig.addSlotInfo(DataType.ENERGY, new InventorySlotInfo(true, true, energySlot));
        }
        ConfigInfo fluidConfig = configComponent.getConfig(TransmissionType.FLUID);
        if (fluidConfig != null) {
            fluidConfig.addSlotInfo(DataType.INPUT, new FluidSlotInfo(true, false, inputTank));
            fluidConfig.addSlotInfo(DataType.EXTRA, new FluidSlotInfo(true, false, extraTank));
            fluidConfig.addSlotInfo(DataType.INPUT_2, new FluidSlotInfo(true, false, tertiaryTank));
            fluidConfig.addSlotInfo(DataType.OUTPUT, new FluidSlotInfo(false, true, outputTank));
        }
        configComponent.setupInputConfig(TransmissionType.ENERGY, energyContainer);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.FLUID);
        inputHandler = InputHelper.getInputHandler(inputTank, RecipeError.NOT_ENOUGH_INPUT);
        extraHandler = InputHelper.getInputHandler(extraTank, RecipeError.NOT_ENOUGH_SECONDARY_INPUT);
        tertiaryHandler = InputHelper.getInputHandler(tertiaryTank, RecipeError.NOT_ENOUGH_SECONDARY_INPUT);
        outputHandler = OutputHelper.getOutputHandler(outputTank, RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
        recipeContainers = new RecipeContainers(List.of(inputTank, extraTank, tertiaryTank), outputTank);
    }

    @Override
    protected boolean hasItemOutput() {
        return false;
    }

    @Override
    protected IContainerHolder<IFluidTank> getInitialFluidTanks(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        MekContainerHelper<IFluidTank> builder = MekContainerHelper.forSideWithFluidConfig(this);
        builder.addContainer(inputTank = BasicFluidTank.input(MAX_FLUID, (fluid, _) -> containsRecipeA(fluid), this::containsRecipeA, recipeCacheListener));
        builder.addContainer(extraTank = BasicFluidTank.input(MAX_FLUID, (fluid, _) -> containsRecipeB(fluid), this::containsRecipeB, recipeCacheListener));
        builder.addContainer(tertiaryTank = BasicFluidTank.input(MAX_FLUID, (fluid, _) -> containsRecipeC(fluid), this::containsRecipeC, recipeCacheListener));
        builder.addContainer(outputTank = BasicFluidTank.output(MAX_FLUID, recipeCacheUnpauseListener));
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
        builder.addContainer(inputSlot = FluidInventorySlot.fill(inputTank, listener, 8, 79));
        builder.addContainer(extraSlot = FluidInventorySlot.fill(extraTank, listener, 30, 79));
        builder.addContainer(tertiarySlot = FluidInventorySlot.fill(tertiaryTank, listener, 52, 79));
        builder.addContainer(outputSlot = FluidInventorySlot.drain(outputTank, listener, 132, 79));
        builder.addContainer(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 153, 79));
        inputSlot.setSlotOverlay(SlotOverlay.MINUS);
        extraSlot.setSlotOverlay(SlotOverlay.MINUS);
        tertiarySlot.setSlotOverlay(SlotOverlay.MINUS);
        outputSlot.setSlotOverlay(SlotOverlay.PLUS);
        inputSlot.tracksWarnings(slot -> slot.warning(WarningType.NO_MATCHING_RECIPE, getWarningCheck(RecipeError.NOT_ENOUGH_INPUT)));
        extraSlot.tracksWarnings(slot -> slot.warning(WarningType.NO_MATCHING_RECIPE, getWarningCheck(RecipeError.NOT_ENOUGH_SECONDARY_INPUT)));
        tertiarySlot.tracksWarnings(slot -> slot.warning(WarningType.NO_MATCHING_RECIPE, getWarningCheck(RecipeError.NOT_ENOUGH_SECONDARY_INPUT)));
        return builder.build();
    }

    @Override
    protected boolean onUpdateServer(ServerLevel level) {
        boolean send = super.onUpdateServer(level);
        FluidBucketHelper.fillInPlace(inputSlot);
        FluidBucketHelper.fillInPlace(extraSlot);
        FluidBucketHelper.fillInPlace(tertiarySlot);
        FluidBucketHelper.drainInPlace(outputSlot);
        energySlot.fillContainerOrConvert(null);
        recipeCacheLookupMonitor.updateAndProcess(level);
        return send;
    }

    @Override
    public IMekanismRecipeTypeProvider<TriFluidRecipeInput, CombiningBookCategory, CombiningBookInfo, FluidAlloyingRecipe, EMInputRecipeCache.TripleFluid<FluidAlloyingRecipe>> getRecipeType() {
        return EMRecipeType.FLUID_ALLOYING;
    }

    @Nullable
    @Override
    public RecipeHolder<FluidAlloyingRecipe> getRecipe(int cacheIndex) {
        return findFirstRecipe(inputHandler, extraHandler, tertiaryHandler);
    }

    @Override
    public CachedRecipe<FluidAlloyingRecipe> createNewCachedRecipe(FluidAlloyingRecipe recipe, int cacheIndex, ContextMap recipeContext) {
        return setCommonSettings(ThreeInputCachedRecipe.fluidAlloyer(recipe, recheckAllRecipeErrors, inputHandler, extraHandler, tertiaryHandler, outputHandler));
    }

    @Override
    public RecipeContainers getRecipeContainers(int cacheIndex) {
        return recipeContainers;
    }

    @Override
    public MachineEnergyContainer<TileEntityFluidAlloyer> energyContainer() {
        return energyContainer;
    }

    public MachineEnergyContainer<TileEntityFluidAlloyer> getEnergyContainer() {
        return energyContainer;
    }

    @Override
    public @Nullable IRecipeViewerRecipeType<FluidAlloyingRecipe> recipeViewerType() {
        return EMRecipeViewersTypes.FLUID_ALLOYING;
    }
}
