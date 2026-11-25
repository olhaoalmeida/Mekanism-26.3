package fr.iglee42.evolvedmekanism.impl;

import fr.iglee42.evolvedmekanism.recipes.MeltingRecipe;
import fr.iglee42.evolvedmekanism.registries.EMRecipeSerializers;
import mekanism.api.recipes.book.LiquificationBookCategory.LiquificationBookInfo;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BasicMelterRecipe extends MeltingRecipe {

    public BasicMelterRecipe(Recipe.CommonInfo commonInfo, LiquificationBookInfo bookInfo, ItemStackIngredient input, FluidStackTemplate output) {
        super(commonInfo, bookInfo, input, output);
    }

    @Override
    public RecipeSerializer<BasicMelterRecipe> getSerializer() {
        return EMRecipeSerializers.MELTER.get();
    }
}
