package com.jerry.mekaf.common.tile.factory;

import mekanism.api.upgrade.UpgradeIds;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.util.context.ContextMap;
import com.jerry.mekaf.common.tile.factory.base.TileEntityChemicalToChemicalFactory;
import com.jerry.mekaf.common.upgrade.ChemicalToChemicalUpgradeData;

import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.upgrade.Upgrade;
import mekanism.api.chemical.ChemicalResource;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.MathUtils;
import mekanism.api.recipes.ChemicalToChemicalRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.OneInputCachedRecipe;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.client.recipe_viewer.type.RecipeViewerRecipeType;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.ISingleRecipeLookupHandler.ChemicalRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.SingleChemical;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.util.UpgradeUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.TriPredicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class TileEntityCentrifugingFactory extends TileEntityChemicalToChemicalFactory<ChemicalToChemicalRecipe<?, ?>> implements IBoundingBlock, ChemicalRecipeLookupHandler<ChemicalToChemicalRecipe<?, ?>> {

    protected static final TriPredicate<ChemicalToChemicalRecipe<?, ?>, ChemicalResource, ChemicalResource> OUTPUT_CHECK = (recipe, input, output) -> output.isEmpty() || output.is(recipe.getOutput(input).typeHolder());
    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_ENERGY_REDUCED_RATE,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    private static final Set<RecipeError> GLOBAL_ERROR_TYPES = Set.of(RecipeError.NOT_ENOUGH_ENERGY);

    public TileEntityCentrifugingFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES);
        configComponent.addDisabledSides(RelativeSide.TOP);

        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.CHEMICAL)
                .setCanChemicalTankEject((_, tank) -> !inputChemicalTanks.contains(tank));
    }

    @Override
    protected void addSlots(MekContainerHelper<IInventorySlot> builder, IContentsListener listener, IContentsListener updateSortingListener) {}

    @Override
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<ChemicalToChemicalRecipe<?, ?>> cached, @NotNull ChemicalResource stack) {
        return cached != null && cached.getRecipe().getInput().testType(stack);
    }

    @Override
    protected @Nullable RecipeHolder<ChemicalToChemicalRecipe<?, ?>> findRecipe(int process, @NotNull ChemicalResource fallbackInput, @NotNull IChemicalTank outputTank) {
        return getRecipeType().getInputCache().findTypeBasedRecipe(level, fallbackInput, outputTank.resource(), OUTPUT_CHECK);
    }

    @Override
    public boolean isChemicalValidForTank(@NotNull ChemicalResource stack) {
        return containsRecipe(stack);
    }

    @Override
    public boolean isValidInputChemical(@NotNull ChemicalResource stack) {
        return containsRecipe(stack);
    }

    @Override
    protected int getNeededInput(ChemicalToChemicalRecipe<?, ?> recipe, ChemicalResource inputStack) {
        return MathUtils.clampToInt(recipe.getInput().getNeededAmount(inputStack));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public @NotNull IMekanismRecipeTypeProvider<?, ?, ?, ChemicalToChemicalRecipe<?, ?>, SingleChemical<ChemicalToChemicalRecipe<?, ?>>> getRecipeType() {
        return (IMekanismRecipeTypeProvider) MekanismRecipeType.CENTRIFUGING;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public @Nullable IRecipeViewerRecipeType<ChemicalToChemicalRecipe<?, ?>> recipeViewerType() {
        return (IRecipeViewerRecipeType) RecipeViewerRecipeType.CENTRIFUGING;
    }

    @Override
    public @Nullable RecipeHolder<ChemicalToChemicalRecipe<?, ?>> getRecipe(int cacheIndex) {
        return findFirstRecipe(chemicalInputHandlers[cacheIndex]);
    }

    @Override
    public @NotNull CachedRecipe<ChemicalToChemicalRecipe<?, ?>> createNewCachedRecipe(@NotNull ChemicalToChemicalRecipe<?, ?> recipe, int cacheIndex, ContextMap recipeContext) {
        return setCommonSettings(new OneInputCachedRecipe<>(recipe, recheckAllRecipeErrors[cacheIndex], chemicalInputHandlers[cacheIndex], chemicalOutputHandlers[cacheIndex]), cacheIndex);
    }

    @Override
    public @Nullable ChemicalToChemicalUpgradeData getUpgradeData(HolderLookup.Provider provider) {
        return new ChemicalToChemicalUpgradeData(provider, redstone, getControlType(), energyContainer,
                recipeCacheLookupMonitors, energySlot, inputChemicalTanks, outputChemicalTanks, isSorting(), getComponents(), problemPath());
    }
}
