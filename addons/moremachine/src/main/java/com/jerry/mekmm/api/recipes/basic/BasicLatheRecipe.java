package com.jerry.mekmm.api.recipes.basic;

import com.jerry.mekmm.Mekmm;
import java.util.List;
import mekanism.api.recipes.book.CrushingBookCategory;
import mekanism.api.recipes.book.CrushingBookCategory.CrushingBookInfo;
import mekanism.api.recipes.display.recipe.CrushingRecipeDisplay;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import com.jerry.mekmm.api.recipes.MoreMachineRecipeSerializers;
import com.jerry.mekmm.api.recipes.MoreMachineRecipeTypes;

import mekanism.api.recipes.basic.BasicItemStackToItemStackRecipe;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class BasicLatheRecipe extends BasicItemStackToItemStackRecipe<CrushingBookCategory, CrushingBookInfo> {

    private static final Holder<Item> CNC_LATHE = DeferredHolder.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Mekmm.MOD_ID, "cnc_lathe"));

    /**
     * @param input  Input.
     * @param output Output.
     */
    public BasicLatheRecipe(Recipe.CommonInfo commonInfo, CrushingBookInfo bookInfo, ItemStackIngredient input, float experience, ItemStackTemplate output) {
        super(MoreMachineRecipeTypes.TYPE_LATHING.value(), commonInfo, bookInfo, input, experience, output);
    }

    @Override
    public RecipeSerializer<BasicLatheRecipe> getSerializer() {
        return MoreMachineRecipeSerializers.LATHING.value();
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new CrushingRecipeDisplay(getInput().display(), getOutputDisplay(), new SlotDisplay.ItemSlotDisplay(CNC_LATHE), experience()));
    }
}
