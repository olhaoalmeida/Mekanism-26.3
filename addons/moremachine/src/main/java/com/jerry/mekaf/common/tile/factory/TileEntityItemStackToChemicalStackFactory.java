package com.jerry.mekaf.common.tile.factory;

import mekanism.api.upgrade.UpgradeIds;
import mekanism.api.upgrade.Upgrade;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.util.context.ContextMap;
import com.jerry.mekaf.common.tile.factory.base.TileEntityItemToChemicalFactory;
import com.jerry.mekaf.common.upgrade.ItemToChemicalUpgradeData;

import mekanism.api.chemical.ChemicalResource;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.math.MathUtils;
import mekanism.api.recipes.ItemStackToChemicalRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.OneInputCachedRecipe;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.client.recipe_viewer.type.RecipeViewerRecipeType;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.ISingleRecipeLookupHandler.ItemRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.SingleItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.TriPredicate;
import net.neoforged.neoforge.transfer.item.ItemResource;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class TileEntityItemStackToChemicalStackFactory extends TileEntityItemToChemicalFactory<ItemStackToChemicalRecipe<?, ?>> implements ItemRecipeLookupHandler<ItemStackToChemicalRecipe<?, ?>> {

    private static final TriPredicate<ItemStackToChemicalRecipe<?, ?>, ItemResource, ChemicalResource> OUTPUT_CHECK = (recipe, input, output) -> output.isEmpty() || output.is(recipe.getOutput(input).typeHolder());
    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    private static final Set<RecipeError> GLOBAL_ERROR_TYPES = Set.of(RecipeError.NOT_ENOUGH_ENERGY);

    public TileEntityItemStackToChemicalStackFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES);

        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.CHEMICAL);
    }

    @Override
    @Contract("null, _ -> false")
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<ItemStackToChemicalRecipe<?, ?>> cached, @NotNull ItemResource stack) {
        return cached != null && cached.getRecipe().getInput().testType(stack);
    }

    @Override
    @Nullable
    protected RecipeHolder<ItemStackToChemicalRecipe<?, ?>> findRecipe(int process, @NotNull ItemResource fallbackInput, @NotNull IChemicalTank outputSlot) {
        return getRecipeType().getInputCache().findTypeBasedRecipe(level, fallbackInput, outputSlot.resource(), OUTPUT_CHECK);
    }

    @Override
    protected int getNeededInput(ItemStackToChemicalRecipe<?, ?> recipe, ItemResource inputStack) {
        return MathUtils.clampToInt(recipe.getInput().getNeededAmount(inputStack));
    }

    // 物品能否放进槽位
    @Override
    public boolean isItemValidForSlot(@NotNull ItemResource stack) {
        return containsRecipe(stack);
    }

    @Override
    public boolean isValidInputItem(@NotNull ItemResource stack) {
        return containsRecipe(stack);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public @NotNull IMekanismRecipeTypeProvider<?, ?, ?, ItemStackToChemicalRecipe<?, ?>, SingleItem<ItemStackToChemicalRecipe<?, ?>>> getRecipeType() {
        return switch (getAdvancedFactoryType()) {
            case OXIDIZING -> (IMekanismRecipeTypeProvider) MekanismRecipeType.OXIDIZING;
            case PIGMENT_EXTRACTING -> (IMekanismRecipeTypeProvider) MekanismRecipeType.PIGMENT_EXTRACTING;
            default -> throw new IllegalStateException("Unhandled factory type");
        };
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public IRecipeViewerRecipeType<ItemStackToChemicalRecipe<?, ?>> recipeViewerType() {
        return switch (getAdvancedFactoryType()) {
            case OXIDIZING -> (IRecipeViewerRecipeType) RecipeViewerRecipeType.OXIDIZING;
            case PIGMENT_EXTRACTING -> (IRecipeViewerRecipeType) RecipeViewerRecipeType.PIGMENT_EXTRACTING;
            default -> throw new IllegalStateException("Unhandled factory type");
        };
    }

    @Override
    public @Nullable RecipeHolder<ItemStackToChemicalRecipe<?, ?>> getRecipe(int cacheIndex) {
        return findFirstRecipe(itemInputHandlers[cacheIndex]);
    }

    @Override
    public @NotNull CachedRecipe<ItemStackToChemicalRecipe<?, ?>> createNewCachedRecipe(@NotNull ItemStackToChemicalRecipe<?, ?> recipe, int cacheIndex, ContextMap recipeContext) {
        return setCommonSettings(new OneInputCachedRecipe<>(recipe, recheckAllRecipeErrors[cacheIndex], itemInputHandlers[cacheIndex], chemicalOutputHandlers[cacheIndex]), cacheIndex);
    }

    @Override
    public @Nullable ItemToChemicalUpgradeData getUpgradeData(HolderLookup.Provider provider) {
        return new ItemToChemicalUpgradeData(provider, redstone, getControlType(), energyContainer,
                recipeCacheLookupMonitors, energySlot, inputItemSlots, outputChemicalTanks, isSorting(), getComponents(), problemPath());
    }
}
