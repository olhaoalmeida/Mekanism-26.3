package com.jerry.mekmm.mixin.recipe;

import com.jerry.mekmm.api.recipes.MoreMachineRecipeTypes;
import com.jerry.mekmm.api.recipes.PlantingRecipe;
import com.jerry.mekmm.api.recipes.RecyclerRecipe;
import com.jerry.mekmm.api.recipes.StamperRecipe;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;
import com.jerry.mekmm.common.recipe.lookup.cache.MoreMachineInputRecipeCache;

import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.book.MekanismBookCategory;
import mekanism.common.registries.MekanismRecipeBookCategories;
import mekanism.common.registration.impl.RecipeBookCategoryDeferredRegister;
import net.minecraft.world.item.crafting.Recipe;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.cache.IInputRecipeCache;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.registration.impl.RecipeTypeRegistryObject;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeInput;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(value = MekanismRecipeType.class, remap = false)
public class MixinMekanismRecipeType {

    @Shadow
    private static <VANILLA_INPUT extends RecipeInput, BOOK_CATEGORY extends MekanismBookCategory, BOOK_INFO extends Recipe.BookInfo<BOOK_CATEGORY>, RECIPE extends MekanismRecipe<VANILLA_INPUT, BOOK_CATEGORY, BOOK_INFO>, INPUT_CACHE extends IInputRecipeCache> RecipeTypeRegistryObject<VANILLA_INPUT, BOOK_CATEGORY, BOOK_INFO, RECIPE, INPUT_CACHE> register(
            Identifier name,
            RecipeBookCategoryDeferredRegister.MekanismRecipeBookTypes recipeBookTypes,
            Function<MekanismRecipeType<VANILLA_INPUT, BOOK_CATEGORY, BOOK_INFO, RECIPE, INPUT_CACHE>, INPUT_CACHE> inputCacheCreator) {
        return null;
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void mekmm$initRecipe(CallbackInfo ci) {
        MoreMachineRecipeType.RECYCLING = register(MoreMachineRecipeTypes.NAME_RECYCLING, MekanismRecipeBookCategories.SAWING, recipeType -> new InputRecipeCache.SingleItem<>(recipeType, RecyclerRecipe::getInput));

        MoreMachineRecipeType.PLANTING_STATION = register(MoreMachineRecipeTypes.NAME_PLANTING, MekanismRecipeBookCategories.INJECTING, recipeType -> new InputRecipeCache.ItemChemical<>(recipeType, PlantingRecipe::getItemInput, PlantingRecipe::getChemicalInput));

        MoreMachineRecipeType.STAMPING = register(MoreMachineRecipeTypes.NAME_STAMPING, MekanismRecipeBookCategories.COMBINING, recipeType -> new InputRecipeCache.DoubleItem<>(recipeType, StamperRecipe::getInput, StamperRecipe::getMold));

        MoreMachineRecipeType.LATHING = register(MoreMachineRecipeTypes.NAME_LATHING, MekanismRecipeBookCategories.CRUSHING, recipeType -> new InputRecipeCache.SingleItem<>(recipeType, ItemStackToItemStackRecipe::getInput));

        MoreMachineRecipeType.ROLLING_MILL = register(MoreMachineRecipeTypes.NAME_ROLLING_MILL, MekanismRecipeBookCategories.CRUSHING, recipeType -> new InputRecipeCache.SingleItem<>(recipeType, ItemStackToItemStackRecipe::getInput));

        MoreMachineRecipeType.PRESSING = register(MoreMachineRecipeTypes.NAME_PRESSING, MekanismRecipeBookCategories.COMBINING, MoreMachineInputRecipeCache.Pressing::new);
    }

    /**
     * NeoForge requires enum extension names to start with the mod id, so our book types are MEKMM_* while the recipe types are registered in the mekanism namespace.
     */
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/RecipeBookType;valueOf(Ljava/lang/String;)Lnet/minecraft/world/inventory/RecipeBookType;"))
    private net.minecraft.world.inventory.RecipeBookType mekmm$bookType(String name) {
        return switch (name) {
            case "MEKANISM_RECYCLING", "MEKANISM_PLANTING", "MEKANISM_STAMPING", "MEKANISM_LATHING", "MEKANISM_ROLLING_MILL", "MEKANISM_PRESSING" ->
                    net.minecraft.world.inventory.RecipeBookType.valueOf("MEKMM_" + name.substring("MEKANISM_".length()));
            default -> net.minecraft.world.inventory.RecipeBookType.valueOf(name);
        };
    }
}
