package com.jerry.mekmm.api.recipes.basic;

import mekanism.api.chemical.Chemical;
import mekanism.api.recipes.book.WashingBookCategory.WashingBookInfo;
import net.minecraft.core.TypedInstance;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.crafting.display.FluidStackSlotDisplay;
import com.jerry.mekmm.api.recipes.FluidChemicalToFluidRecipe;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@NullMarked
public abstract class BasicFluidChemicalToFluidRecipe extends FluidChemicalToFluidRecipe {

    protected final FluidStackIngredient fluidInput;
    protected final ChemicalStackIngredient chemicalInput;
    protected final FluidStackTemplate output;

    /**
     * @param fluidInput    Fluid input.
     * @param chemicalInput Chemical input.
     * @param output        Output.
     */
    public BasicFluidChemicalToFluidRecipe(Recipe.CommonInfo commonInfo, WashingBookInfo bookInfo, FluidStackIngredient fluidInput, ChemicalStackIngredient chemicalInput, FluidStackTemplate output) {
        super(commonInfo, bookInfo);
        this.fluidInput = Objects.requireNonNull(fluidInput, "Fluid input cannot be null.");
        this.chemicalInput = Objects.requireNonNull(chemicalInput, "Chemical input cannot be null.");
        Objects.requireNonNull(output, "Output cannot be null.");
        this.output = output;
    }

    @Override
    public FluidStackIngredient getFluidInput() {
        return fluidInput;
    }

    @Override
    public ChemicalStackIngredient getChemicalInput() {
        return chemicalInput;
    }

    @Override
    public List<FluidStackTemplate> getOutputDefinition(ContextMap contextMap) {
        return Collections.singletonList(output);
    }

    @Override
    public SlotDisplay getOutputDisplay() {
        return new FluidStackSlotDisplay(output);
    }

    @Override
    @Contract(value = "_, _ -> new", pure = true)
    public FluidStackTemplate getOutput(TypedInstance<Fluid> fluidStack, TypedInstance<Chemical> chemicalStack) {
        return output;
    }

    public FluidStackTemplate getOutputRaw() {
        return output;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicFluidChemicalToFluidRecipe other = (BasicFluidChemicalToFluidRecipe) o;
        return fluidInput.equals(other.fluidInput) && chemicalInput.equals(other.chemicalInput) && output.equals(other.output);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fluidInput, chemicalInput, output);
    }

    @Override
    public void logMissingTags() {
        getFluidInput().logMissingTags();
        getChemicalInput().logMissingTags();
    }
}
