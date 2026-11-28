package fr.iglee42.evolvedmekanism.tiles.factory;

import fr.iglee42.evolvedmekanism.interfaces.EMInputRecipeCache;
import fr.iglee42.evolvedmekanism.interfaces.IGetEnergySlot;
import fr.iglee42.evolvedmekanism.interfaces.ThreeInputCachedRecipe;
import fr.iglee42.evolvedmekanism.interfaces.TripleItemRecipeLookupHandler;
import fr.iglee42.evolvedmekanism.recipes.AlloyerRecipe;
import fr.iglee42.evolvedmekanism.recipes.vanilla_input.TriItemRecipeInput;
import fr.iglee42.evolvedmekanism.registries.EMRecipeType;
import fr.iglee42.evolvedmekanism.tiers.EMFactoryTier;
import fr.iglee42.evolvedmekanism.tiles.LimitedInputInventorySlot;
import fr.iglee42.evolvedmekanism.tiles.upgrade.AlloyerUpgradeData;
import java.util.List;
import java.util.Set;
import mekanism.api.IContentsListener;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.recipes.book.CombiningBookCategory;
import mekanism.api.recipes.book.CombiningBookCategory.CombiningBookInfo;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.lookup.cache.type.ItemInputCache;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.tile.factory.TileEntityItemToItemFactory;
import mekanism.common.upgrade.IUpgradeData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;

public class TileEntityAlloyingFactory extends TileEntityItemToItemFactory<CombiningBookCategory, CombiningBookInfo, AlloyerRecipe> implements TripleItemRecipeLookupHandler<AlloyerRecipe> {

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
          RecipeError.NOT_ENOUGH_ENERGY,
          RecipeError.NOT_ENOUGH_INPUT,
          RecipeError.NOT_ENOUGH_SECONDARY_INPUT,
          RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
          RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT
    );
    private static final Set<RecipeError> GLOBAL_ERROR_TYPES = Set.of(
          RecipeError.NOT_ENOUGH_ENERGY,
          RecipeError.NOT_ENOUGH_SECONDARY_INPUT
    );

    private final IInputHandler<Item, ItemStack> extraInputHandler;
    private final IInputHandler<Item, ItemStack> secondExtraInputHandler;

    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getSecondaryInput", docPlaceholder = "secondary input slot")
    LimitedInputInventorySlot extraSlot;
    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getTertiaryInput", docPlaceholder = "tertiary input slot")
    LimitedInputInventorySlot secondExtraSlot;

    public TileEntityAlloyingFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES);
        extraInputHandler = InputHelper.getInputHandler(extraSlot, RecipeError.NOT_ENOUGH_SECONDARY_INPUT);
        secondExtraInputHandler = InputHelper.getInputHandler(secondExtraSlot, RecipeError.NOT_ENOUGH_SECONDARY_INPUT);
        for (int i = 0; i < recipeContainers.length; i++) {
            //Add the extra slots to the input recipe containers
            recipeContainers[i] = recipeContainers[i].withExtraInput(extraSlot).withExtraInput(secondExtraSlot);
        }
        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.EXTRA, new InventorySlotInfo(true, true, extraSlot, secondExtraSlot));
        }
    }

    @Override
    protected void addSlots(MekContainerHelper<IInventorySlot> builder, IContentsListener listener, IContentsListener updateSortingListener) {
        super.addSlots(builder, listener, updateSortingListener);
        int imageWidth = 176 + (38 * (tier.ordinal() - EMFactoryTier.OVERCLOCKED.ordinal() + 1)) + 9;
        int inventorySize = 9 * 20;
        int endInventory = (imageWidth / 2 + inventorySize / 2) - 10;
        int extraSlotX = tier.ordinal() > FactoryTier.ULTIMATE.ordinal() ? endInventory + 4 : 7;
        int extraSlotY = tier.ordinal() > FactoryTier.ULTIMATE.ordinal() ? 143 : 57;
        builder.addContainer(extraSlot = LimitedInputInventorySlot.at((tier.ordinal() + 1) * 64, this::containsRecipeB, markAllMonitorsChanged(listener), extraSlotX, extraSlotY));
        builder.addContainer(secondExtraSlot = LimitedInputInventorySlot.at((tier.ordinal() + 1) * 64, this::containsRecipeC, markAllMonitorsChanged(listener), extraSlotX, extraSlotY - 22));
        extraSlot.setSlotType(ContainerSlotType.EXTRA);
        secondExtraSlot.setSlotType(ContainerSlotType.EXTRA);
    }

    @Override
    protected LimitedInputInventorySlot getExtraSlot() {
        return extraSlot;
    }

    public LimitedInputInventorySlot getSecondExtraSlot() {
        return secondExtraSlot;
    }

    @Override
    public boolean isValidInputItem(ItemResource itemType) {
        return containsRecipeA(itemType);
    }

    @Override
    protected int getNeededInput(AlloyerRecipe recipe, ItemResource inputType) {
        return recipe.getMainInput().getNeededAmount(inputType);
    }

    @Override
    public boolean isItemValidForSlot(ItemResource itemType) {
        return containsRecipeA(itemType);
    }

    @Override
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<AlloyerRecipe> cached, ItemResource itemType) {
        if (cached != null) {
            AlloyerRecipe cachedRecipe = cached.getRecipe();
            return cachedRecipe.getMainInput().testType(itemType) && (extraSlot.isEmpty() || cachedRecipe.getExtraInput().testType(extraSlot.resource()));
        }
        return false;
    }

    @Nullable
    @Override
    protected RecipeHolder<AlloyerRecipe> findRecipe(ItemResource fallbackInput, IInventorySlot outputSlot, @Nullable IInventorySlot secondaryOutputSlot) {
        ItemStack input = fallbackInput.toStack(1);
        ItemStack extra = extraSlot.asItemStack();
        ItemStack secondExtra = secondExtraSlot.asItemStack();
        ItemResource outputContents = outputSlot.resource();
        return ((EMInputRecipeCache.IFindRecipes<Item, ItemStack, ItemStackIngredient, Item, ItemStack, ItemStackIngredient, Item, ItemStack, ItemStackIngredient,
              AlloyerRecipe, ItemInputCache<AlloyerRecipe>, ItemInputCache<AlloyerRecipe>, ItemInputCache<AlloyerRecipe>>) getRecipeType().getInputCache())
              .findTypeBasedRecipe(level, input, extra, secondExtra,
                    recipe -> outputContents.isEmpty() || outputContents.matches(recipe.getOutput(input, extra, secondExtra)));
    }

    @Override
    public IMekanismRecipeTypeProvider<TriItemRecipeInput, CombiningBookCategory, CombiningBookInfo, AlloyerRecipe, EMInputRecipeCache.TripleItem<AlloyerRecipe>> getRecipeType() {
        return EMRecipeType.ALLOYING;
    }

    @Nullable
    @Override
    public RecipeHolder<AlloyerRecipe> getRecipe(int cacheIndex) {
        return findFirstRecipe(inputHandlers[cacheIndex], extraInputHandler, secondExtraInputHandler);
    }

    @Override
    public CachedRecipe<AlloyerRecipe> createNewCachedRecipe(AlloyerRecipe recipe, int cacheIndex, ContextMap recipeContext) {
        return setCommonSettings(ThreeInputCachedRecipe.alloyer(recipe, recheckAllRecipeErrors[cacheIndex], inputHandlers[cacheIndex], extraInputHandler,
              secondExtraInputHandler, outputHandlers[cacheIndex]), cacheIndex);
    }

    @Override
    public void parseUpgradeData(IUpgradeData upgradeData, Provider provider, TransactionContext transaction) {
        if (upgradeData instanceof AlloyerUpgradeData data) {
            //Generic factory upgrade data handling
            super.parseUpgradeData(upgradeData, provider, transaction);
            extraSlot.copyContents(data.extraSlot, transaction);
            secondExtraSlot.copyContents(data.secondaryExtraSlot, transaction);
        } else {
            Mekanism.logger.warn("Unhandled upgrade data.", new Throwable());
        }
    }

    @Override
    public AlloyerUpgradeData getUpgradeData(HolderLookup.Provider provider) {
        return new AlloyerUpgradeData(provider, redstone, getControlType(), energyContainer, ((IGetEnergySlot) this).getEnergySlot(), recipeCacheLookupMonitors,
              extraSlot, secondExtraSlot, inputSlots, outputSlots, isSorting(), getComponents(), problemPath());
    }
}
