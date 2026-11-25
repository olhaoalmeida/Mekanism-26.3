package fr.iglee42.evolvedmekanism.impl;

import fr.iglee42.evolvedmekanism.recipes.FluidCombiningRecipe;
import fr.iglee42.evolvedmekanism.registries.EMRecipeSerializers;
import mekanism.api.recipes.book.CombiningBookCategory.CombiningBookInfo;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BasicFluidCombiningRecipe extends FluidCombiningRecipe {

    public BasicFluidCombiningRecipe(Recipe.CommonInfo commonInfo, CombiningBookInfo bookInfo, FluidStackIngredient mainInput, FluidStackIngredient extraInput,
          FluidStackTemplate output) {
        super(commonInfo, bookInfo, mainInput, extraInput, output);
    }

    @Override
    public RecipeSerializer<BasicFluidCombiningRecipe> getSerializer() {
        return EMRecipeSerializers.FLUID_COMBINING.get();
    }
}
