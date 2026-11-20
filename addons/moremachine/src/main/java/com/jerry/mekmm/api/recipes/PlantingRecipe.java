package com.jerry.mekmm.api.recipes;

import com.jerry.mekmm.Mekmm;

import mekanism.api.recipes.ItemStackChemicalToObjectRecipe;
import mekanism.api.recipes.SawmillRecipe.ChanceOutput;
import mekanism.api.recipes.book.InjectingBookCategory;
import mekanism.api.recipes.book.InjectingBookCategory.InjectingBookInfo;
import mekanism.api.recipes.display.recipe.InjectingRecipeDisplay;

import net.minecraft.core.Holder;
import net.minecraft.core.TypedInstance;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.neoforge.registries.DeferredHolder;

import mekanism.api.chemical.Chemical;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

import java.util.Collections;
import java.util.List;

/**
 * Represents a recipe that can be used in the Planting Station.
 *
 * @author Jerry
 */
@NullMarked
public abstract class PlantingRecipe extends ItemStackChemicalToObjectRecipe<ChanceOutput, InjectingBookCategory, InjectingBookInfo> {

    protected static final RandomSource RANDOM = RandomSource.create();
    private static final Holder<Item> PLANTING_STATION = DeferredHolder.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Mekmm.MOD_ID, "planting_station"));

    protected PlantingRecipe(Recipe.CommonInfo commonInfo, InjectingBookInfo bookInfo) {
        super(commonInfo, bookInfo, 0F);
    }

    /**
     * Gets a new output based on the given inputs.
     *
     * @implNote The passed in inputs should <strong>NOT</strong> be modified.
     */
    @Override
    @Contract(value = "_, _ -> new", pure = true)
    public abstract ChanceOutput getOutput(TypedInstance<Item> inputItem, TypedInstance<Chemical> inputChemical);

    /**
     * For recipe viewers, gets the main output representations to display.
     */
    public abstract List<ItemStackTemplate> getMainOutputDefinition();

    /**
     * For recipe viewers, gets the secondary output representations to display.
     */
    public abstract List<ItemStackTemplate> getSecondaryOutputDefinition();

    /**
     * Gets the chance (between 0 and 1) of the secondary output being produced.
     */
    public abstract double getSecondaryChance();

    @Override
    public ItemStack assemble(mekanism.api.recipes.vanilla_input.SingleItemChemicalRecipeInput input) {
        if (test(input.item(), input.chemical())) {
            ItemStackTemplate main = getOutput(input.item(), input.chemical()).getMainOutput();
            return main == null ? ItemStack.EMPTY : main.create();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public List<ChanceOutput> getOutputDefinition(ContextMap contextMap) {
        return Collections.emptyList();
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new InjectingRecipeDisplay(getItemInput().display(), getChemicalInput().display(), perTickUsage(), experience(), getOutputDisplay(),
              new SlotDisplay.ItemSlotDisplay(PLANTING_STATION)));
    }

    @Override
    public final RecipeType<PlantingRecipe> getType() {
        return MoreMachineRecipeTypes.TYPE_PLANTING.value();
    }
}
