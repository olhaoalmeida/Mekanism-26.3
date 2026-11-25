package fr.iglee42.evolvedmekanism.impl;

import fr.iglee42.evolvedmekanism.recipes.FluidAlloyingRecipe;
import fr.iglee42.evolvedmekanism.registries.EMRecipeSerializers;
import mekanism.api.recipes.book.CombiningBookCategory.CombiningBookInfo;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BasicFluidAlloyingRecipe extends FluidAlloyingRecipe {

    public BasicFluidAlloyingRecipe(Recipe.CommonInfo commonInfo, CombiningBookInfo bookInfo, FluidStackIngredient mainInput, FluidStackIngredient extraInput,
          FluidStackIngredient tertiaryInput, FluidStackTemplate output) {
        super(commonInfo, bookInfo, mainInput, extraInput, tertiaryInput, output);
    }

    @Override
    public RecipeSerializer<BasicFluidAlloyingRecipe> getSerializer() {
        return EMRecipeSerializers.FLUID_ALLOYING.get();
    }
}
