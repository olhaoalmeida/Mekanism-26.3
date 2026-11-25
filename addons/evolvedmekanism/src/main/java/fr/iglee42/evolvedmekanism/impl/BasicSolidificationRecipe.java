package fr.iglee42.evolvedmekanism.impl;

import fr.iglee42.evolvedmekanism.recipes.SolidificationRecipe;
import fr.iglee42.evolvedmekanism.registries.EMRecipeSerializers;
import mekanism.api.recipes.book.CombiningBookCategory.CombiningBookInfo;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BasicSolidificationRecipe extends SolidificationRecipe {

    public BasicSolidificationRecipe(Recipe.CommonInfo commonInfo, CombiningBookInfo bookInfo, ItemStackIngredient inputSolid, FluidStackIngredient inputFluid,
          FluidStackIngredient inputFluidExtra, long energyRequired, int duration, ItemStackTemplate output, boolean keepItem) {
        super(commonInfo, bookInfo, inputSolid, inputFluid, inputFluidExtra, energyRequired, duration, output, keepItem);
    }

    @Override
    public RecipeSerializer<BasicSolidificationRecipe> getSerializer() {
        return EMRecipeSerializers.SOLIDIFICATION.get();
    }
}
