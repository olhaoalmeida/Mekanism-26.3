package com.jerry.mekmm.api.recipes;

import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.TwoInputMekRecipe;
import mekanism.api.recipes.book.WashingBookCategory;
import mekanism.api.recipes.book.WashingBookCategory.WashingBookInfo;
import mekanism.api.recipes.display.recipe.WashingRecipeDisplay;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.vanilla_input.SingleFluidChemicalRecipeInput;

import net.minecraft.core.TypedInstance;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Base class for defining fluid chemical to fluid recipes.
 * <br>
 * Input: FluidStack, Input: Chemical, Output: FluidStackTemplate
 */
@NullMarked
public abstract class FluidChemicalToFluidRecipe extends TwoInputMekRecipe<Fluid, FluidStack, FluidStackIngredient, Chemical, ChemicalStack, ChemicalStackIngredient, SingleFluidChemicalRecipeInput, FluidStackTemplate, WashingBookCategory, WashingBookInfo> {

    protected FluidChemicalToFluidRecipe(Recipe.CommonInfo commonInfo, WashingBookInfo bookInfo) {
        super(commonInfo, bookInfo, 0F);
    }

    @Override
    public boolean matches(SingleFluidChemicalRecipeInput input, Level level) {
        return !isIncomplete() && test(input.fluid(), input.chemical());
    }

    @Override
    public ItemStack assemble(SingleFluidChemicalRecipeInput input) {
        return ItemStack.EMPTY;
    }

    /** Gets the input fluid ingredient. */
    public abstract FluidStackIngredient getFluidInput();

    /** Gets the input chemical ingredient. */
    public abstract ChemicalStackIngredient getChemicalInput();

    @Override
    public final FluidStackIngredient getInputA() {
        return getFluidInput();
    }

    @Override
    public final ChemicalStackIngredient getInputB() {
        return getChemicalInput();
    }

    @Override
    @Contract(value = "_, _ -> new", pure = true)
    public abstract FluidStackTemplate getOutput(TypedInstance<Fluid> fluidStack, TypedInstance<Chemical> chemicalStack);

    @Override
    public List<FluidStackTemplate> getOutputDefinition(ContextMap contextMap) {
        return List.of();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new WashingRecipeDisplay(getFluidInput().display(), getChemicalInput().display(), getOutputDisplay(), SlotDisplay.Empty.INSTANCE, experience()));
    }

    public boolean isIncomplete() {
        return getFluidInput().hasNoMatchingInstances() || getChemicalInput().hasNoMatchingInstances();
    }
}
