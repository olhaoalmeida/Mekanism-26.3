package fr.iglee42.evolvedmekanism.interfaces;

import fr.iglee42.evolvedmekanism.recipes.MeltingRecipe;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipeHelper;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.ingredients.InputIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.ITripleRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.DoubleInputRecipeCache;
import mekanism.common.recipe.lookup.cache.TripleInputRecipeCache;
import mekanism.common.recipe.lookup.cache.type.ChemicalInputCache;
import mekanism.common.recipe.lookup.cache.type.FluidInputCache;
import mekanism.common.recipe.lookup.cache.type.IInputCache;
import mekanism.common.recipe.lookup.cache.type.ItemInputCache;
import net.minecraft.core.TypedInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.util.TriPredicate;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

public class EMInputRecipeCache {

    public static class TripleItem<RECIPE extends MekanismRecipe<?, ?, ?> & TriPredicate<ItemStack, ItemStack, ItemStack>>
          extends TripleSameInputRecipeCache<Item, ItemStack, ItemStackIngredient, RECIPE, ItemInputCache<RECIPE>> {

        public TripleItem(MekanismRecipeType<?, ?, ?, RECIPE, ?> recipeType, Function<RECIPE, ItemStackIngredient> inputAExtractor,
              Function<RECIPE, ItemStackIngredient> inputBExtractor, Function<RECIPE, ItemStackIngredient> inputCExtractor) {
            super(recipeType, inputAExtractor, inputBExtractor, inputCExtractor, ItemInputCache::new);
        }
    }

    /**
     * Helper expansion class for {@link TripleInputRecipeCache} to simplify the generics when all inputs are of the same type.
     */
    public abstract static class TripleSameInputRecipeCache<HOLDER, INPUT extends TypedInstance<HOLDER>, INGREDIENT extends InputIngredient<HOLDER, INPUT>,
          RECIPE extends MekanismRecipe<?, ?, ?> & TriPredicate<INPUT, INPUT, INPUT>, CACHE extends IInputCache<HOLDER, INPUT, INGREDIENT, RECIPE>>
          extends TripleInputRecipeCache<HOLDER, INPUT, INGREDIENT, HOLDER, INPUT, INGREDIENT, HOLDER, INPUT, INGREDIENT, RECIPE, CACHE, CACHE, CACHE> {

        protected TripleSameInputRecipeCache(MekanismRecipeType<?, ?, ?, RECIPE, ?> recipeType, Function<RECIPE, INGREDIENT> inputAExtractor,
              Function<RECIPE, INGREDIENT> inputBExtractor, Function<RECIPE, INGREDIENT> inputCExtractor, Supplier<CACHE> cacheSupplier) {
            super(recipeType, inputAExtractor, cacheSupplier.get(), inputBExtractor, cacheSupplier.get(), inputCExtractor, cacheSupplier.get());
        }
    }

    /**
     * Implemented (via mixin) by {@link TripleInputRecipeCache} to allow finding a recipe based on all three inputs.
     */
    public interface IFindRecipes<HOLDER_A, INPUT_A extends TypedInstance<HOLDER_A>, INGREDIENT_A extends InputIngredient<HOLDER_A, INPUT_A>,
          HOLDER_B, INPUT_B extends TypedInstance<HOLDER_B>, INGREDIENT_B extends InputIngredient<HOLDER_B, INPUT_B>,
          HOLDER_C, INPUT_C extends TypedInstance<HOLDER_C>, INGREDIENT_C extends InputIngredient<HOLDER_C, INPUT_C>,
          RECIPE extends MekanismRecipe<?, ?, ?> & TriPredicate<INPUT_A, INPUT_B, INPUT_C>,
          CACHE_A extends IInputCache<HOLDER_A, INPUT_A, INGREDIENT_A, RECIPE>, CACHE_B extends IInputCache<HOLDER_B, INPUT_B, INGREDIENT_B, RECIPE>,
          CACHE_C extends IInputCache<HOLDER_C, INPUT_C, INGREDIENT_C, RECIPE>> {

        @Nullable
        RecipeHolder<RECIPE> findTypeBasedRecipe(@Nullable Level world, INPUT_A inputA, INPUT_B inputB, INPUT_C inputC, Predicate<RECIPE> matchCriteria);
    }

    public static class ItemItemChemical<RECIPE extends MekanismRecipe<?, ?, ?> & TriPredicate<ItemStack, ItemStack, ChemicalStack>>
          extends TripleInputRecipeCache<Item, ItemStack, ItemStackIngredient, Item, ItemStack, ItemStackIngredient, Chemical, ChemicalStack, ChemicalStackIngredient,
          RECIPE, ItemInputCache<RECIPE>, ItemInputCache<RECIPE>, ChemicalInputCache<RECIPE>> {

        public ItemItemChemical(MekanismRecipeType<?, ?, ?, RECIPE, ?> recipeType, Function<RECIPE, ItemStackIngredient> inputAExtractor,
              Function<RECIPE, ItemStackIngredient> inputBExtractor, Function<RECIPE, ChemicalStackIngredient> inputCExtractor) {
            super(recipeType, inputAExtractor, new ItemInputCache<>(), inputBExtractor, new ItemInputCache<>(), inputCExtractor, new ChemicalInputCache<>());
        }
    }

    public static class DoubleFluid<RECIPE extends MekanismRecipe<?, ?, ?> & BiPredicate<FluidStack, FluidStack>>
          extends DoubleInputRecipeCache.DoubleSameInputRecipeCache<Fluid, FluidStack, FluidStackIngredient, RECIPE, FluidInputCache<RECIPE>> {

        public DoubleFluid(MekanismRecipeType<?, ?, ?, RECIPE, ?> recipeType, Function<RECIPE, FluidStackIngredient> inputAExtractor,
              Function<RECIPE, FluidStackIngredient> inputBExtractor) {
            super(recipeType, inputAExtractor, inputBExtractor, FluidInputCache::new);
        }
    }

    public static class TripleFluid<RECIPE extends MekanismRecipe<?, ?, ?> & TriPredicate<FluidStack, FluidStack, FluidStack>>
          extends TripleSameInputRecipeCache<Fluid, FluidStack, FluidStackIngredient, RECIPE, FluidInputCache<RECIPE>> {

        public TripleFluid(MekanismRecipeType<?, ?, ?, RECIPE, ?> recipeType, Function<RECIPE, FluidStackIngredient> inputAExtractor,
              Function<RECIPE, FluidStackIngredient> inputBExtractor, Function<RECIPE, FluidStackIngredient> inputCExtractor) {
            super(recipeType, inputAExtractor, inputBExtractor, inputCExtractor, FluidInputCache::new);
        }
    }

    public static class ItemFluidFluid<RECIPE extends MekanismRecipe<?, ?, ?> & TriPredicate<ItemStack, FluidStack, FluidStack>>
          extends TripleInputRecipeCache<Item, ItemStack, ItemStackIngredient, Fluid, FluidStack, FluidStackIngredient, Fluid, FluidStack, FluidStackIngredient,
          RECIPE, ItemInputCache<RECIPE>, FluidInputCache<RECIPE>, FluidInputCache<RECIPE>> {

        public ItemFluidFluid(MekanismRecipeType<?, ?, ?, RECIPE, ?> recipeType, Function<RECIPE, ItemStackIngredient> inputAExtractor,
              Function<RECIPE, FluidStackIngredient> inputBExtractor, Function<RECIPE, FluidStackIngredient> inputCExtractor) {
            super(recipeType, inputAExtractor, new ItemInputCache<>(), inputBExtractor, new FluidInputCache<>(), inputCExtractor, new FluidInputCache<>());
        }
    }

    /**
     * Helper interface to make the generics that we have to pass to {@link ITripleRecipeLookupHandler} not as messy.
     */
    public interface ItemFluidFluidRecipeLookupHandler<RECIPE extends MekanismRecipe<?, ?, ?> & TriPredicate<ItemStack, FluidStack, FluidStack>>
          extends ITripleRecipeLookupHandler<Item, ItemStack, Fluid, FluidStack, Fluid, FluidStack, RECIPE, ItemFluidFluid<RECIPE>> {
    }

    /**
     * Base implementation for handling ItemStack to Fluid Recipes.
     *
     * @param recipe           Recipe.
     * @param recheckAllErrors Returns {@code true} if processing should be continued even if an error is hit in order to gather all the errors. It is recommended to not
     *                         do this every tick or if there is no one viewing recipes.
     * @param inputHandler     Input handler.
     * @param outputHandler    Output handler.
     */
    public static OneInputCachedRecipe<Item, ItemStack, FluidStackTemplate, MeltingRecipe> itemToFluid(MeltingRecipe recipe, BooleanSupplier recheckAllErrors,
          IInputHandler<Item, ItemStack> inputHandler, IOutputHandler<FluidStackTemplate> outputHandler) {
        return new OneInputCachedRecipe<>(recipe, recheckAllErrors, inputHandler, outputHandler, recipe::getInput, recipe::getOutput, Objects::isNull);
    }

    @NullMarked
    public static class OneInputCachedRecipe<HOLDER, INPUT extends TypedInstance<HOLDER>, OUTPUT, RECIPE extends MekanismRecipe<?, ?, ?> & Predicate<INPUT>>
          extends CachedRecipe<RECIPE> {

        private final IInputHandler<HOLDER, INPUT> inputHandler;
        private final IOutputHandler<OUTPUT> outputHandler;
        private final Supplier<? extends InputIngredient<HOLDER, INPUT>> inputSupplier;
        private final Function<INPUT, OUTPUT> outputGetter;
        private final Predicate<OUTPUT> outputEmptyCheck;
        private final Consumer<INPUT> inputSetter;
        private final Consumer<OUTPUT> outputSetter;

        @Nullable
        private INPUT input;
        @Nullable
        private OUTPUT output;

        protected OneInputCachedRecipe(RECIPE recipe, BooleanSupplier recheckAllErrors, IInputHandler<HOLDER, INPUT> inputHandler, IOutputHandler<OUTPUT> outputHandler,
              Supplier<? extends InputIngredient<HOLDER, INPUT>> inputSupplier, Function<INPUT, OUTPUT> outputGetter, Predicate<OUTPUT> outputEmptyCheck) {
            super(recipe, recheckAllErrors);
            this.inputHandler = Objects.requireNonNull(inputHandler, "Input handler cannot be null.");
            this.outputHandler = Objects.requireNonNull(outputHandler, "Output handler cannot be null.");
            this.inputSupplier = Objects.requireNonNull(inputSupplier, "Input ingredient supplier cannot be null.");
            this.outputGetter = Objects.requireNonNull(outputGetter, "Output getter cannot be null.");
            this.outputEmptyCheck = Objects.requireNonNull(outputEmptyCheck, "Output empty check cannot be null.");
            this.inputSetter = input -> this.input = input;
            this.outputSetter = output -> this.output = output;
        }

        @Override
        protected void calculateOperationsThisTick(OperationTracker tracker) {
            super.calculateOperationsThisTick(tracker);
            CachedRecipeHelper.oneInputCalculateOperationsThisTick(tracker, inputHandler, inputSupplier, inputSetter, outputHandler, outputGetter, outputSetter);
        }

        @Override
        public boolean isInputValid() {
            INPUT input = inputHandler.getInput();
            return !inputHandler.isEmpty(input) && recipe.test(input);
        }

        @Override
        protected boolean finishProcessing(int operations, TransactionContext transaction) {
            //Validate something didn't go horribly wrong
            if (input != null && output != null && !inputHandler.isEmpty(input) && !outputEmptyCheck.test(output)) {
                return inputHandler.use(input, operations, transaction) &&
                       outputHandler.handleOutput(output, operations, transaction);
            }
            return false;
        }
    }
}
