package com.jerry.mekmm.common.recipe.impl;

import com.jerry.mekmm.api.recipes.FluidChemicalToFluidRecipe;
import com.jerry.mekmm.api.recipes.basic.BasicFluidChemicalToFluidRecipe;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStackTemplate;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FluidReplicatorIRecipeSingle extends BasicFluidChemicalToFluidRecipe {

    /**
     * @param fluidInput    Fluid input.
     * @param chemicalInput Chemical input.
     * @param output        Output.
     */
    public FluidReplicatorIRecipeSingle(FluidStackIngredient fluidInput, ChemicalStackIngredient chemicalInput, FluidStackTemplate output) {
        super(new net.minecraft.world.item.crafting.Recipe.CommonInfo(false), new mekanism.api.recipes.book.WashingBookCategory.WashingBookInfo(mekanism.api.recipes.book.WashingBookCategory.MISC, ""), fluidInput, chemicalInput, output);
    }

    @Override
    public RecipeSerializer<FluidReplicatorIRecipeSingle> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<FluidReplicatorIRecipeSingle> getType() {
        return null;
    }

}
