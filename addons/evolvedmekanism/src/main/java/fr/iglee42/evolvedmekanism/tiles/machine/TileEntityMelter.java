package fr.iglee42.evolvedmekanism.tiles.machine;

import fr.iglee42.evolvedmekanism.config.EMConfig;
import fr.iglee42.evolvedmekanism.interfaces.EMInputRecipeCache;
import fr.iglee42.evolvedmekanism.recipes.MeltingRecipe;
import fr.iglee42.evolvedmekanism.recipeviewers.EMRecipeViewersTypes;
import fr.iglee42.evolvedmekanism.registries.EMBlocks;
import fr.iglee42.evolvedmekanism.registries.EMRecipeType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mekanism.api.IContentsListener;
import mekanism.api.fluid.IFluidTank;
import mekanism.api.heat.HeatAPI;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.upgrade.Upgrade;
import mekanism.api.upgrade.UpgradeIds;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.api.recipes.book.LiquificationBookCategory;
import mekanism.api.recipes.book.LiquificationBookCategory.LiquificationBookInfo;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.heat.BasicHeatCapacitor;
import mekanism.common.capabilities.heat.CachedAmbientTemperature;
import mekanism.common.capabilities.holder.container.IContainerHolder;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
import mekanism.common.capabilities.holder.single.ISingleContainerHolder;
import mekanism.common.capabilities.holder.single.SingleConfigHolder;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerFluidTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerHeatCapacitorWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.SyntheticComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.container.sync.SyncableInt;
import mekanism.common.inventory.container.sync.dynamic.ContainerSync;
import mekanism.common.inventory.slot.FluidInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.warning.WarningTracker.WarningType;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.lookup.ISingleRecipeLookupHandler.ItemRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.SingleItem;
import mekanism.common.recipe.lookup.monitor.ProgressRecipeCacheLookupMonitor;
import mekanism.common.util.UpgradeUtils;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.tile.interfaces.IMultiTickProcessing;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;

public class TileEntityMelter extends TileEntityRecipeMachine<MeltingRecipe, ProgressRecipeCacheLookupMonitor<MeltingRecipe>>
      implements ItemRecipeLookupHandler<MeltingRecipe>, IMultiTickProcessing {

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
          RecipeError.NOT_ENOUGH_INPUT,
          RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
          RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT
    );

    public static final double MAX_MULTIPLIER_TEMP = 1500;
    public static final int MAX_FLUID = 16_000;

    private double biomeAmbientTemp;

    @UnknownNullability//Initialized via getInitialFluidTanks
    @WrappingComputerMethod(wrapper = ComputerFluidTankWrapper.class, methodNames = {"getOutput", "getOutputCapacity", "getOutputNeeded", "getOutputFilledPercentage"}, docPlaceholder = "output tank")
    public BasicFluidTank fluidTank;
    @UnknownNullability//Initialized via getInitialHeatCapacitor
    @WrappingComputerMethod(wrapper = ComputerHeatCapacitorWrapper.class, methodNames = "getTemperature", docPlaceholder = "generator")
    BasicHeatCapacitor heatCapacitor;

    private final IOutputHandler<FluidStackTemplate> outputHandler;
    private final IInputHandler<Item, ItemStack> inputHandler;
    private final RecipeContainers recipeContainers;

    private final int baseTicksRequired = 100;
    public int ticksRequired = baseTicksRequired;
    private int operationsPerTick = 1;

    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getInput", docPlaceholder = "input slot")
    InputInventorySlot inputSlot;
    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getContainerFillItem", docPlaceholder = "fillable container slot")
    FluidInventorySlot containerFillSlot;
    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getOutputItem", docPlaceholder = "filled container output slot")
    OutputInventorySlot outputSlot;

    @ContainerSync
    @SyntheticComputerMethod(getter = "getEnvironmentalLoss")
    public double lastEnvironmentLoss;

    public TileEntityMelter(BlockPos pos, BlockState state) {
        super(EMBlocks.MELTER, pos, state, TRACKED_ERROR_TYPES);
        List<IInventorySlot> inputSlots = List.of(inputSlot, containerFillSlot);
        List<IInventorySlot> outputSlots = Collections.singletonList(outputSlot);
        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.INPUT, new InventorySlotInfo(true, false, inputSlots));
            itemConfig.addSlotInfo(DataType.OUTPUT, new InventorySlotInfo(false, true, outputSlots));
            List<IInventorySlot> ioSlots = new ArrayList<>(inputSlots);
            ioSlots.addAll(outputSlots);
            itemConfig.addSlotInfo(DataType.INPUT_OUTPUT, new InventorySlotInfo(true, true, ioSlots));
        }
        configComponent.setupOutputConfig(TransmissionType.FLUID, fluidTank);
        configComponent.setupInputConfig(TransmissionType.HEAT, heatCapacitor);

        ejectorComponent.setOutputData(configComponent, TransmissionType.FLUID);

        inputHandler = InputHelper.getInputHandler(inputSlot, RecipeError.NOT_ENOUGH_INPUT);
        outputHandler = OutputHelper.getOutputHandler(fluidTank, RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
        recipeContainers = new RecipeContainers(inputSlot, fluidTank);
    }

    @Override
    protected boolean hasItemOutput() {
        return false;
    }

    @Override
    protected ProgressRecipeCacheLookupMonitor<MeltingRecipe> createNewCacheMonitor() {
        return new ProgressRecipeCacheLookupMonitor<>(this, hasItemOutput());
    }

    public float getScaledProgress() {
        return getRecipeProgress() / (float) getTicksRequired();
    }

    @ComputerMethod
    public int getRecipeProgress() {
        return recipeCacheLookupMonitor.getSavedOperatingTicks(0);
    }

    @Override
    public int getTicksRequired() {
        return ticksRequired;
    }

    @Override
    public int getOperationsPerTick() {
        return operationsPerTick;
    }

    @Override
    public void recalculateUpgrades(HolderGetter<Upgrade> upgrades, Holder<Upgrade> upgrade, int totalInstalled) {
        if (upgrade.is(UpgradeIds.SPEED)) {
            ticksRequired = UpgradeUtils.getTicks(baseTicksRequired, upgrade, totalInstalled);
            operationsPerTick = UpgradeUtils.getOperationsPerTick(baseTicksRequired, 1, upgrade, totalInstalled);
        }
        super.recalculateUpgrades(upgrades, upgrade, totalInstalled);
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(recipeCacheLookupMonitor.operatingTicksAsSyncable());
        container.track(SyncableInt.create(this::getTicksRequired, value -> ticksRequired = value));
    }

    @Override
    protected @Nullable IContainerHolder<IFluidTank> getInitialFluidTanks(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        MekContainerHelper<IFluidTank> builder = MekContainerHelper.forSideWithFluidConfig(this);
        builder.addContainer(fluidTank = BasicFluidTank.output(MAX_FLUID, recipeCacheUnpauseListener));
        return builder.build();
    }

    @Override
    protected @Nullable ISingleContainerHolder<IHeatCapacitor> getInitialHeatCapacitor(IContentsListener listener, IContentsListener recipeCacheListener,
          IContentsListener recipeCacheUnpauseListener, CachedAmbientTemperature ambientTemperature) {
        heatCapacitor = BasicHeatCapacitor.create(10, 5, 100, ambientTemperature, listener);
        return SingleConfigHolder.heat(heatCapacitor, this);
    }

    @Override
    protected IContainerHolder<IInventorySlot> getInitialInventory(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        MekContainerHelper<IInventorySlot> builder = MekContainerHelper.forSideWithItemConfig(this);
        builder.addContainer(inputSlot = InputInventorySlot.at(this::containsRecipe, recipeCacheListener, 26, 36))
              .tracksWarnings(slot -> slot.warning(WarningType.NO_MATCHING_RECIPE, getWarningCheck(RecipeError.NOT_ENOUGH_INPUT)));
        builder.addContainer(containerFillSlot = FluidInventorySlot.drain(fluidTank, listener, 155, 25));
        builder.addContainer(outputSlot = OutputInventorySlot.at(recipeCacheUnpauseListener, 155, 56));
        containerFillSlot.setSlotOverlay(SlotOverlay.PLUS);
        return builder.build();
    }

    @Override
    protected boolean onUpdateServer(ServerLevel level) {
        boolean send = super.onUpdateServer(level);
        biomeAmbientTemp = HeatAPI.getAmbientTemp(level, getBlockPos());
        containerFillSlot.drainTankIntoSlot(outputSlot, null);
        recipeCacheLookupMonitor.updateAndProcess(level);
        try (Transaction transaction = Transaction.openRoot()) {
            // update temperature
            lastEnvironmentLoss = simulate(transaction).environmentTransfer();
            transaction.commit();
        }
        return send;
    }

    @Override
    public IMekanismRecipeTypeProvider<SingleRecipeInput, LiquificationBookCategory, LiquificationBookInfo, MeltingRecipe, SingleItem<MeltingRecipe>> getRecipeType() {
        return EMRecipeType.MELTING;
    }

    @Nullable
    @Override
    public RecipeHolder<MeltingRecipe> getRecipe(int cacheIndex) {
        return findFirstRecipe(inputHandler);
    }

    @Override
    public CachedRecipe<MeltingRecipe> createNewCachedRecipe(MeltingRecipe recipe, int cacheIndex, ContextMap recipeContext) {
        //Note: The melter is powered by heat, so unlike other progress machines it has no energy requirements
        return setCommonSettings(EMInputRecipeCache.itemToFluid(recipe, recheckAllRecipeErrors, inputHandler, outputHandler))
              .setCanHolderFunction(() -> this.canFunction() && hasMachineEnoughHeat())
              .setRequiredTicks(this::getTicksRequired)
              .setBaselineMaxOperations(this::getOperationsPerTick);
    }

    @Override
    public RecipeContainers getRecipeContainers(int cacheIndex) {
        return recipeContainers;
    }

    /**
     * The melter is heat powered and has no energy container.
     */
    @Nullable
    public MachineEnergyContainer<TileEntityMelter> getEnergyContainer() {
        return null;
    }

    public boolean hasMachineEnoughHeat() {
        return heatCapacitor.getTemperature() > 1273;
    }

    @Override
    public double simulateEnvironment(TransactionContext transaction) {
        double currentTemperature = heatCapacitor.getTemperature();
        double heatCapacity = heatCapacitor.getHeatCapacity();
        if (Math.abs(currentTemperature - biomeAmbientTemp) < 0.001) {
            heatCapacitor.handleHeat(biomeAmbientTemp * heatCapacity - heatCapacitor.getHeat(), transaction);
        } else {
            double incr = EMConfig.general.melterHeatDissipation.get() * Math.sqrt(Math.abs(currentTemperature - biomeAmbientTemp));
            if (currentTemperature > biomeAmbientTemp) {
                incr = -incr;
            }
            heatCapacitor.handleHeat(heatCapacity * incr, transaction);
            if (incr < 0) {
                return -incr;
            }
        }
        return 0;
    }

    @Override
    public @Nullable IRecipeViewerRecipeType<MeltingRecipe> recipeViewerType() {
        return EMRecipeViewersTypes.MELTING;
    }
}
