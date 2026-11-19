package com.jerry.mekmm.api.recipes;

import com.jerry.mekmm.Mekmm;

import mekanism.api.recipes.SingleInputRecipe;
import mekanism.api.recipes.book.SawingBookCategory;
import mekanism.api.recipes.book.SawingBookCategory.SawingBookInfo;
import mekanism.api.recipes.display.recipe.SawingRecipeDisplay;
import net.minecraft.core.TypedInstance;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import java.util.Collections;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Predicate;

@NullMarked
public abstract class RecyclerRecipe extends SingleInputRecipe.ItemInputRecipe<RecyclerRecipe.ChanceOutput, SawingBookCategory, SawingBookInfo> {

    protected static final RandomSource RANDOM = RandomSource.create();
    private static final Holder<Item> RECYCLER = DeferredHolder.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Mekmm.MOD_ID, "recycler"));

    protected RecyclerRecipe(Recipe.CommonInfo commonInfo, SawingBookInfo bookInfo) {
        super(commonInfo, bookInfo, 0F);
    }

    /**
     * Gets a new chance output based on the given input.
     */
    @Override
    @Contract(value = "_ -> new")
    public abstract ChanceOutput getOutput(TypedInstance<Item> input);

    /**
     * For recipe viewers, gets the chance output representations to display.
     */
    public abstract List<ItemStack> getChanceOutputDefinition();

    @Override
    @Deprecated
    public List<ChanceOutput> getOutputDefinition(ContextMap contextMap) {
        return Collections.emptyList();
    }

    /**
     * Gets the chance (between 0 and 1) of the chance output being produced.
     */
    public abstract double getOutputChance();

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new SawingRecipeDisplay(getInput().display(), getOutputDisplay(), new SlotDisplay.ItemSlotDisplay(RECYCLER), experience()));
    }

    @Override
    public final RecipeType<RecyclerRecipe> getType() {
        return MoreMachineRecipeTypes.TYPE_RECYCLER.value();
    }

    /**
     * Represents a precalculated chance based output. This output keeps track of what random value was calculated for
     * use in comparing if the chance output should be
     * created.
     */
    public interface ChanceOutput {

        /**
         * Gets a copy of the chance output ignoring the random chance of it happening. This is mostly used for checking
         * the maximum amount we can get as a chance
         * output for purposes of seeing if we have space to process.
         *
         * @implNote return a new copy or ItemStack.EMPTY
         */
        ItemStack getMaxChanceOutput();

        /**
         * Gets a copy of the chance output if the random number generated for this output matches the chance of a
         * secondary output being produced, otherwise returns
         * an empty stack.
         *
         * @implNote return a new copy or ItemStack.EMPTY
         */
        ItemStack getChanceOutput();

        /**
         * Similar to {@link #getChanceOutput()} except that this calculates a new random number to act as if this was
         * another chance output for purposes of handling
         * multiple operations at once.
         *
         * @implNote return a new copy or ItemStack.EMPTY
         */
        ItemStack nextChanceOutput();
    }
}
