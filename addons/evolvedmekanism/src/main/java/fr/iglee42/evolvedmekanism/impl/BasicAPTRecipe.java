package fr.iglee42.evolvedmekanism.impl;

import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import fr.iglee42.evolvedmekanism.registries.EMRecipeSerializers;
import fr.iglee42.evolvedmekanism.registries.EMRecipeType;
import java.util.List;
import mekanism.api.recipes.basic.BasicItemStackChemicalToItemStackRecipe;
import mekanism.api.recipes.book.CompressingBookCategory;
import mekanism.api.recipes.book.CompressingBookCategory.CompressingBookInfo;
import mekanism.api.recipes.display.recipe.CompressingRecipeDisplay;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BasicAPTRecipe extends BasicItemStackChemicalToItemStackRecipe<CompressingBookCategory, CompressingBookInfo> {

    private static final Holder<Item> STATION = DeferredHolder.create(Registries.ITEM, Identifier.fromNamespaceAndPath(EvolvedMekanism.MODID, "apt_casing"));

    public BasicAPTRecipe(Recipe.CommonInfo commonInfo, CompressingBookInfo bookInfo, ItemStackIngredient itemInput, ChemicalStackIngredient chemicalInput,
          boolean perTickUsage, float experience, ItemStackTemplate output) {
        super(EMRecipeType.APT.getRecipeType(), commonInfo, bookInfo, itemInput, chemicalInput, perTickUsage, experience, output);
    }

    @Override
    public RecipeSerializer<BasicAPTRecipe> getSerializer() {
        return EMRecipeSerializers.APT.get();
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new CompressingRecipeDisplay(getItemInput().display(), getChemicalInput().display(), perTickUsage(), experience(), getOutputDisplay(),
              new SlotDisplay.ItemSlotDisplay(STATION)));
    }
}
