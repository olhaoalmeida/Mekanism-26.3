package com.jerry.mekmm.common.recipe;

import com.jerry.mekmm.api.recipes.PlantingRecipe;
import com.jerry.mekmm.api.recipes.RecyclerRecipe;
import mekanism.api.recipes.book.*;
import mekanism.api.recipes.book.SawingBookCategory.SawingBookInfo;
import mekanism.api.recipes.book.InjectingBookCategory.InjectingBookInfo;
import mekanism.api.recipes.book.CombiningBookCategory.CombiningBookInfo;
import mekanism.api.recipes.book.CrushingBookCategory.CrushingBookInfo;
import com.jerry.mekmm.api.recipes.StamperRecipe;
import com.jerry.mekmm.api.recipes.TripleItemToItemRecipe;
import com.jerry.mekmm.common.recipe.lookup.cache.MoreMachineInputRecipeCache;

import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.api.recipes.vanilla_input.SingleItemChemicalRecipeInput;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.registration.impl.RecipeTypeRegistryObject;

import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public class MoreMachineRecipeType {

    public static RecipeTypeRegistryObject<SingleRecipeInput, SawingBookCategory, SawingBookInfo, RecyclerRecipe, InputRecipeCache.SingleItem<RecyclerRecipe>> RECYCLING;

    public static RecipeTypeRegistryObject<SingleItemChemicalRecipeInput, InjectingBookCategory, InjectingBookInfo, PlantingRecipe, InputRecipeCache.ItemChemical<PlantingRecipe>> PLANTING_STATION;

    public static RecipeTypeRegistryObject<RecipeInput, CombiningBookCategory, CombiningBookInfo, StamperRecipe, InputRecipeCache.DoubleItem<StamperRecipe>> STAMPING;

    public static RecipeTypeRegistryObject<SingleRecipeInput, CrushingBookCategory, CrushingBookInfo, ItemStackToItemStackRecipe<CrushingBookCategory, CrushingBookInfo>, InputRecipeCache.SingleItem<ItemStackToItemStackRecipe<CrushingBookCategory, CrushingBookInfo>>> LATHING;

    public static RecipeTypeRegistryObject<SingleRecipeInput, CrushingBookCategory, CrushingBookInfo, ItemStackToItemStackRecipe<CrushingBookCategory, CrushingBookInfo>, InputRecipeCache.SingleItem<ItemStackToItemStackRecipe<CrushingBookCategory, CrushingBookInfo>>> ROLLING_MILL;

    public static RecipeTypeRegistryObject<RecipeInput, CombiningBookCategory, CombiningBookInfo, TripleItemToItemRecipe, MoreMachineInputRecipeCache.TripleItem<TripleItemToItemRecipe>> PRESSING;
}
