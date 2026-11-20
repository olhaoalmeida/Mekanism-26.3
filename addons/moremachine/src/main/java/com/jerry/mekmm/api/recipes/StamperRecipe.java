package com.jerry.mekmm.api.recipes;

import com.jerry.mekmm.Mekmm;

import mekanism.api.recipes.SimpleTwoInputRecipe;
import mekanism.api.recipes.book.CombiningBookCategory;
import mekanism.api.recipes.book.CombiningBookCategory.CombiningBookInfo;
import mekanism.api.recipes.display.recipe.CombiningRecipeDisplay;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.api.recipes.vanilla_input.BiItemRecipeInput;

import net.minecraft.core.Holder;
import net.minecraft.core.TypedInstance;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Input: two ItemStacks (input + mold). Output: ItemStack.
 */
public abstract class StamperRecipe extends SimpleTwoInputRecipe<Item, ItemStack, ItemStackIngredient, BiItemRecipeInput, ItemStackTemplate, CombiningBookCategory, CombiningBookInfo> {

    private static final Holder<Item> STAMPER = DeferredHolder.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Mekmm.MOD_ID, "stamper"));

    @Nullable
    private PlacementInfo placementInfo;

    protected StamperRecipe(Recipe.CommonInfo commonInfo, CombiningBookInfo bookInfo) {
        super(commonInfo, bookInfo, 0F);
    }

    public abstract ItemStackIngredient getInput();

    public abstract ItemStackIngredient getMold();

    @Override
    public final ItemStackIngredient getInputA() {
        return getInput();
    }

    @Override
    public final ItemStackIngredient getInputB() {
        return getMold();
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(List.of(getInput().ingredient().ingredient(), getMold().ingredient().ingredient()));
        }
        return this.placementInfo;
    }

    @Override
    public ItemStack assemble(BiItemRecipeInput input) {
        if (!getInputA().hasNoMatchingInstances() && !getInputB().hasNoMatchingInstances()) {
            ItemStack main = input.main();
            ItemStack mold = input.secondary();
            if (test(main, mold)) {
                return getOutput(main, mold).create();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(BiItemRecipeInput input, Level level) {
        return test(input.main(), input.secondary());
    }

    @Override
    public abstract ItemStackTemplate getOutput(TypedInstance<Item> input, TypedInstance<Item> extra);

    @Override
    public final RecipeType<StamperRecipe> getType() {
        return MoreMachineRecipeTypes.TYPE_STAMPING.value();
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new CombiningRecipeDisplay(getInput().display(), getMold().display(), getOutputDisplay(), new SlotDisplay.ItemSlotDisplay(STAMPER), experience()));
    }

    @FunctionalInterface
    public interface Factory<RECIPE extends StamperRecipe> {

        RECIPE create(Recipe.CommonInfo commonInfo, CombiningBookInfo bookInfo, ItemStackIngredient input, ItemStackIngredient mold, ItemStackTemplate output);
    }
}
