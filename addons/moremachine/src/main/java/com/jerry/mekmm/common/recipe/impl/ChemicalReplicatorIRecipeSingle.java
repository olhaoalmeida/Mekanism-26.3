package com.jerry.mekmm.common.recipe.impl;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import java.util.List;
import mekanism.api.recipes.book.ChemicalInfusingBookCategory.ChemicalInfusingBookInfo;
import mekanism.api.recipes.book.ChemicalInfusingBookCategory;
import com.jerry.mekmm.api.recipes.basic.MMBasicChemicalChemicalToChemicalRecipe;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import mekanism.api.chemical.ChemicalStackTemplate;
import mekanism.api.recipes.ChemicalChemicalToChemicalRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ChemicalReplicatorIRecipeSingle extends MMBasicChemicalChemicalToChemicalRecipe {

    /**
     * @param leftInput  Left input.
     * @param rightInput Right input.
     * @param output     Output.
     * @apiNote The order of the inputs does not matter.
     */
    public ChemicalReplicatorIRecipeSingle(ChemicalStackIngredient leftInput, ChemicalStackIngredient rightInput, ChemicalStackTemplate output) {
        super(new Recipe.CommonInfo(false), new ChemicalInfusingBookInfo(ChemicalInfusingBookCategory.MISC, ""), leftInput, rightInput, output);
    }

    @Override
    public RecipeSerializer<ChemicalReplicatorIRecipeSingle> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<ChemicalReplicatorIRecipeSingle> getType() {
        return null;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of();
    }
}
