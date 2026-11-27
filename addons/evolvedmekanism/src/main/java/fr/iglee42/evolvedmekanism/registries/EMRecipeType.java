package fr.iglee42.evolvedmekanism.registries;

import fr.iglee42.evolvedmekanism.interfaces.EMInputRecipeCache;
import fr.iglee42.evolvedmekanism.recipes.AlloyerRecipe;
import fr.iglee42.evolvedmekanism.recipes.ChemixerRecipe;
import fr.iglee42.evolvedmekanism.recipes.FluidAlloyingRecipe;
import fr.iglee42.evolvedmekanism.recipes.FluidCombiningRecipe;
import fr.iglee42.evolvedmekanism.recipes.MeltingRecipe;
import fr.iglee42.evolvedmekanism.recipes.SolidificationRecipe;
import fr.iglee42.evolvedmekanism.recipes.vanilla_input.BiFluidRecipeInput;
import fr.iglee42.evolvedmekanism.recipes.vanilla_input.BiItemChemicalRecipeInput;
import fr.iglee42.evolvedmekanism.recipes.vanilla_input.SingleItemBiFluidRecipeInput;
import fr.iglee42.evolvedmekanism.recipes.vanilla_input.TriFluidRecipeInput;
import fr.iglee42.evolvedmekanism.recipes.vanilla_input.TriItemRecipeInput;
import mekanism.api.recipes.ItemStackChemicalToItemStackRecipe;
import mekanism.api.recipes.book.CombiningBookCategory;
import mekanism.api.recipes.book.CombiningBookCategory.CombiningBookInfo;
import mekanism.api.recipes.book.CompressingBookCategory;
import mekanism.api.recipes.book.CompressingBookCategory.CompressingBookInfo;
import mekanism.api.recipes.book.InjectingBookCategory;
import mekanism.api.recipes.book.InjectingBookCategory.InjectingBookInfo;
import mekanism.api.recipes.book.LiquificationBookCategory;
import mekanism.api.recipes.book.LiquificationBookCategory.LiquificationBookInfo;
import mekanism.api.recipes.vanilla_input.SingleItemChemicalRecipeInput;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.registration.impl.RecipeTypeRegistryObject;
import net.minecraft.world.item.crafting.SingleRecipeInput;

/**
 * Populated by {@link fr.iglee42.evolvedmekanism.mixins.MekanismRecipeTypeMixin} (the types are registered under the mekanism namespace).
 */
public class EMRecipeType {

    public static RecipeTypeRegistryObject<TriItemRecipeInput, CombiningBookCategory, CombiningBookInfo, AlloyerRecipe, EMInputRecipeCache.TripleItem<AlloyerRecipe>> ALLOYING;
    public static RecipeTypeRegistryObject<BiItemChemicalRecipeInput, InjectingBookCategory, InjectingBookInfo, ChemixerRecipe, EMInputRecipeCache.ItemItemChemical<ChemixerRecipe>> CHEMIXING;
    public static RecipeTypeRegistryObject<SingleItemChemicalRecipeInput, CompressingBookCategory, CompressingBookInfo, ItemStackChemicalToItemStackRecipe<CompressingBookCategory, CompressingBookInfo>,
          InputRecipeCache.ItemChemical<ItemStackChemicalToItemStackRecipe<CompressingBookCategory, CompressingBookInfo>>> APT;
    public static RecipeTypeRegistryObject<SingleRecipeInput, LiquificationBookCategory, LiquificationBookInfo, MeltingRecipe, InputRecipeCache.SingleItem<MeltingRecipe>> MELTING;
    public static RecipeTypeRegistryObject<SingleItemBiFluidRecipeInput, CombiningBookCategory, CombiningBookInfo, SolidificationRecipe, EMInputRecipeCache.ItemFluidFluid<SolidificationRecipe>> SOLIDIFICATION;
    public static RecipeTypeRegistryObject<BiFluidRecipeInput, CombiningBookCategory, CombiningBookInfo, FluidCombiningRecipe, EMInputRecipeCache.DoubleFluid<FluidCombiningRecipe>> FLUID_COMBINING;
    public static RecipeTypeRegistryObject<TriFluidRecipeInput, CombiningBookCategory, CombiningBookInfo, FluidAlloyingRecipe, EMInputRecipeCache.TripleFluid<FluidAlloyingRecipe>> FLUID_ALLOYING;
}
