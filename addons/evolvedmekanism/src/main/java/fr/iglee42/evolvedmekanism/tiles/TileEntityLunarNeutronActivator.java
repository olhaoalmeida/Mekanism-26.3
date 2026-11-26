package fr.iglee42.evolvedmekanism.tiles;

import java.util.List;

import fr.iglee42.evolvedmekanism.registries.EMBlocks;
import fr.iglee42.evolvedmekanism.config.EMConfig;
import fr.iglee42.evolvedmekanism.utils.EMNight;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.ChemicalAttributeValidator;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStackTemplate;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.recipes.book.ActivatingBookCategory;
import mekanism.api.recipes.book.ActivatingBookCategory.ActivatingBookInfo;
import mekanism.common.capabilities.holder.container.IContainerHolder;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
import mekanism.common.component.containers.type.IContainerType;
import mekanism.common.recipe.lookup.monitor.RecipeCacheLookupMonitor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.RecipeHolder;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.recipes.ChemicalToChemicalRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.OneInputCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.api.recipes.vanilla_input.SingleChemicalRecipeInput;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.client.recipe_viewer.type.RecipeViewerRecipeType;
import mekanism.common.component.containers.type.ContainerType;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.SyntheticComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.slot.ChemicalInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.ISingleRecipeLookupHandler.ChemicalRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.SingleChemical;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import mekanism.common.util.LightLevelUtils;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;

public class TileEntityLunarNeutronActivator extends TileEntityRecipeMachine<ChemicalToChemicalRecipe<ActivatingBookCategory, ActivatingBookInfo>,
      RecipeCacheLookupMonitor<ChemicalToChemicalRecipe<ActivatingBookCategory, ActivatingBookInfo>>> implements IBoundingBlock,
      ChemicalRecipeLookupHandler<ChemicalToChemicalRecipe<ActivatingBookCategory, ActivatingBookInfo>> {

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT
    );
    public static final long MAX_GAS = 10L * FluidType.BUCKET_VOLUME;

    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class, methodNames = {"getInput", "getInputCapacity", "getInputNeeded",
            "getInputFilledPercentage"}, docPlaceholder = "input tank")
    public IChemicalTank inputTank;
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class, methodNames = {"getOutput", "getOutputCapacity", "getOutputNeeded",
            "getOutputFilledPercentage"}, docPlaceholder = "output tank")
    public IChemicalTank outputTank;

    @SyntheticComputerMethod(getter = "getPeakProductionRate")
    private float peakProductionRate;
    @SyntheticComputerMethod(getter = "getProductionRate")
    private float productionRate;
    private boolean settingsChecked;
    private boolean needsRainCheck;

    private final IOutputHandler<ChemicalStackTemplate> outputHandler;
    private final IInputHandler<Chemical, ChemicalStack> inputHandler;
    private final RecipeContainers recipeContainers;

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getInputItem", docPlaceholder = "input slot")
    ChemicalInventorySlot inputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getOutputItem", docPlaceholder = "output slot")
    ChemicalInventorySlot outputSlot;

    public TileEntityLunarNeutronActivator(BlockPos pos, BlockState state) {
        super(EMBlocks.LUNAR_NEUTRON_ACTIVATOR, pos, state, TRACKED_ERROR_TYPES);
        configComponent.setupIOConfig(TransmissionType.ITEM, inputSlot, outputSlot);
        configComponent.setupIOConfig(TransmissionType.CHEMICAL, inputTank, outputTank, false, true);
        configComponent.addDisabledSides(RelativeSide.TOP);

        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.CHEMICAL)
                .setCanChemicalTankEject((_, tank) -> tank != inputTank);
        inputHandler = InputHelper.getInputHandler(inputTank, RecipeError.NOT_ENOUGH_INPUT);
        outputHandler = OutputHelper.getOutputHandler(outputTank, RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
        recipeContainers = new RecipeContainers(inputTank, outputTank);
    }

    @Override
    protected boolean hasItemOutput() {
        return false;
    }

    @Override
    public IContainerHolder<IChemicalTank> getInitialChemicalTanks(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        MekContainerHelper<IChemicalTank> builder = MekContainerHelper.forSideWithChemicalConfig(this);
        //Allow extracting out of the input gas tank if it isn't external OR the output tank is empty AND the input is radioactive
        builder.addContainer(inputTank = BasicChemicalTank.create(MAX_GAS, MekContainerHelper.radioactiveInputTankPredicate(() -> outputTank, this::getLevel),
                ConstantPredicates.alwaysTrueBi(), this::containsRecipe, ChemicalAttributeValidator.ALWAYS_ALLOW, recipeCacheListener));
        builder.addContainer(outputTank = BasicChemicalTank.output(MAX_GAS, recipeCacheUnpauseListener));
        return builder.build();
    }

    @Override
    protected IContainerHolder<IInventorySlot> getInitialInventory(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        MekContainerHelper<IInventorySlot> builder = MekContainerHelper.forSideWithItemConfig(this);
        builder.addContainer(inputSlot = ChemicalInventorySlot.fill(inputTank, listener, 5, 56));
        builder.addContainer(outputSlot = ChemicalInventorySlot.drain(outputTank, listener, 155, 56));
        inputSlot.setSlotType(ContainerSlotType.INPUT);
        inputSlot.setSlotOverlay(SlotOverlay.MINUS);
        outputSlot.setSlotType(ContainerSlotType.OUTPUT);
        outputSlot.setSlotOverlay(SlotOverlay.PLUS);
        return builder.build();
    }

    private void recheckSettings(ServerLevel world) {
        int seaLevel = world.getSeaLevel();
        BlockPos pos = getBlockPos();
        Biome b = world.getBiomeManager().getBiome(pos).value();
        needsRainCheck = b.getPrecipitationAt(pos, seaLevel) != Precipitation.NONE;
        // Consider the best temperature to be 0.8; biomes that are higher than that
        // will suffer an efficiency loss (semiconductors don't like heat); biomes that are cooler
        // get a boost. We scale the efficiency to around 30% so that it doesn't totally dominate
        float tempEff = 0.3F * (0.8F - b.getTemperature(pos, seaLevel));

        // Treat rainfall as a proxy for humidity; any humidity works as a drag on overall efficiency.
        // As with temperature, we scale it so that it doesn't overwhelm production. Note the signedness
        // on the scaling factor. Also note that we only use rainfall as a proxy if it CAN rain; some dimensions
        // (like the End) have rainfall set, but can't actually support rain.
        float humidityEff = needsRainCheck ? -0.3F * b.getModifiedClimateSettings().downfall() : 0.0F;
        peakProductionRate = EMConfig.general.lunarNeutronActivatorProduction.get() * (1.0F + tempEff + humidityEff);
        settingsChecked = true;
    }

    @Override
    protected boolean onUpdateServer(ServerLevel level) {
        boolean sendUpdatePacket = super.onUpdateServer(level);
        if (!settingsChecked) {
            recheckSettings(level);
        }
        inputSlot.fillTankFromSlot(null);
        outputSlot.drainTankIntoSlot(null);
        productionRate = recalculateProductionRate(level);
        recipeCacheLookupMonitor.updateAndProcess(level);
        return sendUpdatePacket;
    }

    @Override
    public IMekanismRecipeTypeProvider<SingleChemicalRecipeInput, ActivatingBookCategory, ActivatingBookInfo, ChemicalToChemicalRecipe<ActivatingBookCategory, ActivatingBookInfo>,
          SingleChemical<ChemicalToChemicalRecipe<ActivatingBookCategory, ActivatingBookInfo>>> getRecipeType() {
        return MekanismRecipeType.ACTIVATING;
    }

    @Override
    public IRecipeViewerRecipeType<ChemicalToChemicalRecipe<ActivatingBookCategory, ActivatingBookInfo>> recipeViewerType() {
        return RecipeViewerRecipeType.ACTIVATING;
    }

    @Nullable
    @Override
    public RecipeHolder<ChemicalToChemicalRecipe<ActivatingBookCategory, ActivatingBookInfo>> getRecipe(int cacheIndex) {
        return findFirstRecipe(inputHandler);
    }

    @ComputerMethod
    boolean canSeeSun() {
        return level != null && level.dimensionType().hasSkyLight() && level.canSeeSky(worldPosition.above()) && EMNight.isNight(level);
    }

    @Override
    public boolean canFunction() {
        // Sort out if the solar neutron activator can see the sun; we no longer check if it's raining here,
        // since under the new rules, we can still function when it's raining, albeit at a significant penalty.
        return super.canFunction() && canSeeSun();
    }

    private float recalculateProductionRate(ServerLevel world) {
        if (!canFunction()) {
            return 0;
        }
        //Get the brightness of the sun; note that there are some implementations that depend on the base
        // brightness function which doesn't take into account the fact that rain can't occur in some biomes.
        float brightness = 1.0F - LightLevelUtils.getSunBrightness(world, worldPosition);
        //Production is a function of the peak possible output in this biome and sun's current brightness
        float production = peakProductionRate * brightness;
        //If the solar neutron activator is in a biome where it can rain, and it's raining penalize production by 80%
        if (needsRainCheck && (world.isRaining() || world.isThundering())) {
            production *= 0.2F;
        }
        return production;
    }

    @Override
    public CachedRecipe<ChemicalToChemicalRecipe<ActivatingBookCategory, ActivatingBookInfo>> createNewCachedRecipe(ChemicalToChemicalRecipe<ActivatingBookCategory, ActivatingBookInfo> recipe, int cacheIndex, ContextMap recipeContext) {
        return setCommonSettings(new OneInputCachedRecipe<>(recipe, recheckAllRecipeErrors, inputHandler, outputHandler))
                //Edge case handling, this should almost always end up being 1
                .setRequiredTicks(() -> productionRate > 0 && productionRate < 1 ? Mth.ceil(1 / productionRate) : 1)
                .setBaselineMaxOperations(() -> productionRate > 0 && productionRate < 1 ? 1 : (int) productionRate);
    }

    @Override
    public RecipeContainers getRecipeContainers(int cacheIndex) {
        return recipeContainers;
    }

    @Override
    public int getRedstoneLevel() {
        return ContainerType.CHEMICAL.getRedstoneSignalFromContainer(inputTank);
    }

    @Override
    protected boolean makesComparatorDirty(IContainerType<?, ?> type) {
        return type == ContainerType.CHEMICAL;
    }
}
