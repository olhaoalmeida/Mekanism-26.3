package fr.iglee42.evolvedmekanism.tiles.machine;

import fr.iglee42.evolvedmekanism.interfaces.EMInputRecipeCache;
import fr.iglee42.evolvedmekanism.interfaces.ThreeInputCachedRecipe;
import fr.iglee42.evolvedmekanism.interfaces.TripleItemRecipeLookupHandler;
import fr.iglee42.evolvedmekanism.recipes.AlloyerRecipe;
import fr.iglee42.evolvedmekanism.recipeviewers.EMRecipeViewersTypes;
import fr.iglee42.evolvedmekanism.registries.EMBlocks;
import fr.iglee42.evolvedmekanism.registries.EMRecipeType;
import fr.iglee42.evolvedmekanism.tiles.LimitedInputInventorySlot;
import fr.iglee42.evolvedmekanism.tiles.upgrade.AlloyerUpgradeData;
import mekanism.api.IContentsListener;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.api.energy.IEnergyContainer;
import mekanism.common.capabilities.holder.container.IContainerHolder;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
import mekanism.common.capabilities.holder.single.ISingleContainerHolder;
import mekanism.common.capabilities.holder.single.SingleConfigHolder;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.integration.computer.ComputerConstants;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.warning.WarningTracker.WarningType;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.tile.prefab.TileEntityProgressMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeHolder;
import mekanism.api.recipes.book.CombiningBookCategory;
import mekanism.api.recipes.book.CombiningBookCategory.CombiningBookInfo;
import fr.iglee42.evolvedmekanism.recipes.vanilla_input.TriItemRecipeInput;
import org.jetbrains.annotations.UnknownNullability;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class TileEntityAlloyer extends TileEntityProgressMachine<AlloyerRecipe> implements TripleItemRecipeLookupHandler<AlloyerRecipe> {

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
          RecipeError.NOT_ENOUGH_ENERGY,
          RecipeError.NOT_ENOUGH_INPUT,
          RecipeError.NOT_ENOUGH_SECONDARY_INPUT,
          RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
          RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT
    );
    private final IOutputHandler<ItemStackTemplate> outputHandler;
    private final IInputHandler<Item, ItemStack> inputHandler;
    private final IInputHandler<Item, ItemStack> extraInputHandler;
    private final IInputHandler<Item, ItemStack> secondExtraInputHandler;
    private final RecipeContainers recipeContainers;

    @UnknownNullability//Initialized via getInitialEnergyContainer
    private MachineEnergyContainer<TileEntityAlloyer> energyContainer;
    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getMainInput", docPlaceholder = "item input slot")
    InputInventorySlot mainInputSlot;
    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getSecondaryInput", docPlaceholder = "secondary input slot")
    LimitedInputInventorySlot extraInputSlot;
    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getTertiaryInput", docPlaceholder = "tertiary input slot")
    LimitedInputInventorySlot secondExtraInputSlot;
    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getOutput", docPlaceholder = "output slot")
    OutputInventorySlot outputSlot;
    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy slot")
    EnergyInventorySlot energySlot;

    public TileEntityAlloyer(BlockPos pos, BlockState state) {
        super(EMBlocks.ALLOYER, pos, state, TRACKED_ERROR_TYPES, 200);
        setupItemIOExtraConfig(configComponent,mainInputSlot, outputSlot, extraInputSlot,secondExtraInputSlot, energySlot);
        configComponent.setupInputConfig(TransmissionType.ENERGY, energyContainer);

        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM);

        inputHandler = InputHelper.getInputHandler(mainInputSlot, RecipeError.NOT_ENOUGH_INPUT);
        extraInputHandler = InputHelper.getInputHandler(extraInputSlot, RecipeError.NOT_ENOUGH_SECONDARY_INPUT);
        secondExtraInputHandler = InputHelper.getInputHandler(secondExtraInputSlot, RecipeError.NOT_ENOUGH_SECONDARY_INPUT);
        outputHandler = OutputHelper.getOutputHandler(outputSlot, RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
        recipeContainers = new RecipeContainers(List.of(mainInputSlot, extraInputSlot, secondExtraInputSlot), outputSlot);
    }

    @Override
    protected boolean hasItemOutput() {
        return true;
    }

    private static ConfigInfo setupItemIOExtraConfig(TileComponentConfig config,IInventorySlot inputSlot, IInventorySlot outputSlot, IInventorySlot extraSlot, IInventorySlot secondaryExtraSlot, IInventorySlot energySlot) {
        ConfigInfo itemConfig = config.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.INPUT, new InventorySlotInfo(true, false, inputSlot));
            itemConfig.addSlotInfo(DataType.OUTPUT, new InventorySlotInfo(false, true, outputSlot));
            itemConfig.addSlotInfo(DataType.INPUT_OUTPUT, new InventorySlotInfo(true, true, inputSlot, outputSlot));
            itemConfig.addSlotInfo(DataType.EXTRA, new InventorySlotInfo(true, true, extraSlot, secondaryExtraSlot));
            itemConfig.addSlotInfo(DataType.ENERGY, new InventorySlotInfo(true, true, energySlot));
            //Set default config directions
        }
        return itemConfig;
    }

    @Override
    protected ISingleContainerHolder<IEnergyContainer> getInitialEnergyContainer(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        energyContainer = MachineEnergyContainer.input(this, recipeCacheUnpauseListener);
        return SingleConfigHolder.energy(energyContainer, this);
    }

    @Override
    protected IContainerHolder<IInventorySlot> getInitialInventory(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        MekContainerHelper<IInventorySlot> builder = MekContainerHelper.forSideWithItemConfig(this);
        builder.addContainer(mainInputSlot = InputInventorySlot.at((itemType, _) -> containsRecipeABC(itemType, extraInputSlot.resource(), secondExtraInputSlot.resource()), this::containsRecipeA,
              recipeCacheListener, 64, 17)
        ).tracksWarnings(slot -> slot.warning(WarningType.NO_MATCHING_RECIPE, getWarningCheck(RecipeError.NOT_ENOUGH_INPUT)));
        builder.addContainer(extraInputSlot = LimitedInputInventorySlot.at((itemType, _) -> containsRecipeBAC(mainInputSlot.resource(), itemType, secondExtraInputSlot.resource()), this::containsRecipeB,
              recipeCacheListener, 55, 53)
        ).tracksWarnings(slot -> slot.warning(WarningType.NO_MATCHING_RECIPE, getWarningCheck(RecipeError.NOT_ENOUGH_SECONDARY_INPUT)));
        builder.addContainer(secondExtraInputSlot = LimitedInputInventorySlot.at((itemType, _) -> containsRecipeCAB(mainInputSlot.resource(), extraInputSlot.resource(), itemType), this::containsRecipeC,
              recipeCacheListener, 75, 53)
        ).tracksWarnings(slot -> slot.warning(WarningType.NO_MATCHING_RECIPE, getWarningCheck(RecipeError.NOT_ENOUGH_SECONDARY_INPUT)));
        builder.addContainer(outputSlot = OutputInventorySlot.at(recipeCacheUnpauseListener, 116, 35))
              .setOnTakeCheck(recipeCacheLookupMonitor)
              .tracksWarnings(slot -> slot.warning(WarningType.NO_SPACE_IN_OUTPUT, getWarningCheck(RecipeError.NOT_ENOUGH_OUTPUT_SPACE)));
        builder.addContainer(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 141, 35));
        extraInputSlot.setSlotType(ContainerSlotType.EXTRA);
        secondExtraInputSlot.setSlotType(ContainerSlotType.EXTRA);
        return builder.build();
    }

    @Override
    protected boolean onUpdateServer(ServerLevel level) {
        boolean send = super.onUpdateServer(level);
        energySlot.fillContainerOrConvert(null);
        recipeCacheLookupMonitor.updateAndProcess(level);
        return send;
    }

    @Override
    public IMekanismRecipeTypeProvider<TriItemRecipeInput, CombiningBookCategory, CombiningBookInfo, AlloyerRecipe, EMInputRecipeCache.TripleItem<AlloyerRecipe>> getRecipeType() {
        return EMRecipeType.ALLOYING;
    }

    @Nullable
    @Override
    public RecipeHolder<AlloyerRecipe> getRecipe(int cacheIndex) {
        return findFirstRecipe(inputHandler, extraInputHandler, secondExtraInputHandler);
    }

    @Override
    public CachedRecipe<AlloyerRecipe> createNewCachedRecipe(AlloyerRecipe recipe, int cacheIndex, ContextMap recipeContext) {
        return setCommonSettings(ThreeInputCachedRecipe.alloyer(recipe, recheckAllRecipeErrors, inputHandler, extraInputHandler, secondExtraInputHandler, outputHandler));
    }

    @Override
    public RecipeContainers getRecipeContainers(int cacheIndex) {
        return recipeContainers;
    }

    @Override
    public AlloyerUpgradeData getUpgradeData(HolderLookup.Provider provider) {
        return new AlloyerUpgradeData(provider, redstone, getControlType(), energyContainer, recipeCacheLookupMonitor, energySlot, extraInputSlot, secondExtraInputSlot, mainInputSlot, outputSlot, getComponents(), problemPath());
    }

    @Override
    public MachineEnergyContainer<TileEntityAlloyer> energyContainer() {
        return energyContainer;
    }

    public MachineEnergyContainer<TileEntityAlloyer> getEnergyContainer() {
        return energyContainer;
    }



    //Methods relating to IComputerTile
    @ComputerMethod(methodDescription = ComputerConstants.DESCRIPTION_GET_ENERGY_USAGE)
    long getEnergyUsage() {
        return getActive() ? energyContainer.getEnergyPerTick() : 0;
    }
    //End methods IComputerTile


    @Override
    public @Nullable IRecipeViewerRecipeType<AlloyerRecipe> recipeViewerType() {
        return EMRecipeViewersTypes.ALLOYING;
    }
}
