package fr.iglee42.evolvedmekanism.interfaces;

import fr.iglee42.evolvedmekanism.recipes.AlloyerRecipe;
import fr.iglee42.evolvedmekanism.recipes.EMCachedRecipeHelper;
import fr.iglee42.evolvedmekanism.recipes.FluidAlloyingRecipe;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.ingredients.InputIngredient;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;
import net.minecraft.core.TypedInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.util.TriPredicate;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.apache.commons.lang3.function.TriFunction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Base class to help implement handling of recipes with three inputs.
 */
@NullMarked
public class ThreeInputCachedRecipe<HOLDER_A, INPUT_A extends TypedInstance<HOLDER_A>, HOLDER_B, INPUT_B extends TypedInstance<HOLDER_B>,
      HOLDER_C, INPUT_C extends TypedInstance<HOLDER_C>, OUTPUT, RECIPE extends MekanismRecipe<?, ?, ?> & TriPredicate<INPUT_A, INPUT_B, INPUT_C>> extends CachedRecipe<RECIPE> {

    private final IInputHandler<HOLDER_A, INPUT_A> inputHandler;
    private final IInputHandler<HOLDER_B, INPUT_B> secondaryInputHandler;
    private final IInputHandler<HOLDER_C, INPUT_C> tertiaryInputHandler;
    private final IOutputHandler<OUTPUT> outputHandler;
    private final Predicate<INPUT_A> inputEmptyCheck;
    private final Predicate<INPUT_B> secondaryInputEmptyCheck;
    private final Predicate<INPUT_C> tertiaryInputEmptyCheck;
    private final Supplier<? extends InputIngredient<HOLDER_A, INPUT_A>> inputSupplier;
    private final Supplier<? extends InputIngredient<HOLDER_B, INPUT_B>> secondaryInputSupplier;
    private final Supplier<? extends InputIngredient<HOLDER_C, INPUT_C>> tertiaryInputSupplier;
    private final TriFunction<INPUT_A, INPUT_B, INPUT_C, OUTPUT> outputGetter;
    private final Predicate<OUTPUT> outputEmptyCheck;

    @Nullable
    private INPUT_A input;
    @Nullable
    private INPUT_B secondaryInput;
    @Nullable
    private INPUT_C tertiaryInput;
    @Nullable
    private OUTPUT output;

    protected ThreeInputCachedRecipe(RECIPE recipe, BooleanSupplier recheckAllErrors, IInputHandler<HOLDER_A, INPUT_A> inputHandler,
          IInputHandler<HOLDER_B, INPUT_B> secondaryInputHandler, IInputHandler<HOLDER_C, INPUT_C> tertiaryInputHandler, IOutputHandler<OUTPUT> outputHandler,
          Supplier<? extends InputIngredient<HOLDER_A, INPUT_A>> inputSupplier, Supplier<? extends InputIngredient<HOLDER_B, INPUT_B>> secondaryInputSupplier,
          Supplier<? extends InputIngredient<HOLDER_C, INPUT_C>> tertiaryInputSupplier, TriFunction<INPUT_A, INPUT_B, INPUT_C, OUTPUT> outputGetter,
          Predicate<INPUT_A> inputEmptyCheck, Predicate<INPUT_B> secondaryInputEmptyCheck, Predicate<INPUT_C> tertiaryInputEmptyCheck,
          Predicate<OUTPUT> outputEmptyCheck) {
        super(recipe, recheckAllErrors);
        this.inputHandler = Objects.requireNonNull(inputHandler, "Input handler cannot be null.");
        this.secondaryInputHandler = Objects.requireNonNull(secondaryInputHandler, "Secondary input handler cannot be null.");
        this.tertiaryInputHandler = Objects.requireNonNull(tertiaryInputHandler, "Tertiary input handler cannot be null.");
        this.outputHandler = Objects.requireNonNull(outputHandler, "Output handler cannot be null.");
        this.inputSupplier = Objects.requireNonNull(inputSupplier, "Input ingredient supplier cannot be null.");
        this.secondaryInputSupplier = Objects.requireNonNull(secondaryInputSupplier, "Secondary input ingredient supplier cannot be null.");
        this.tertiaryInputSupplier = Objects.requireNonNull(tertiaryInputSupplier, "Tertiary input ingredient supplier cannot be null.");
        this.outputGetter = Objects.requireNonNull(outputGetter, "Output getter cannot be null.");
        this.inputEmptyCheck = Objects.requireNonNull(inputEmptyCheck, "Input empty check cannot be null.");
        this.secondaryInputEmptyCheck = Objects.requireNonNull(secondaryInputEmptyCheck, "Secondary input empty check cannot be null.");
        this.tertiaryInputEmptyCheck = Objects.requireNonNull(tertiaryInputEmptyCheck, "Tertiary input empty check cannot be null.");
        this.outputEmptyCheck = Objects.requireNonNull(outputEmptyCheck, "Output empty check cannot be null.");
    }

    @Override
    protected void calculateOperationsThisTick(OperationTracker tracker) {
        super.calculateOperationsThisTick(tracker);
        EMCachedRecipeHelper.threeInputCalculateOperationsThisTick(tracker, inputHandler, inputSupplier, secondaryInputHandler, secondaryInputSupplier,
              tertiaryInputHandler, tertiaryInputSupplier, (input, secondary, tertiary) -> {
                  this.input = input;
                  this.secondaryInput = secondary;
                  this.tertiaryInput = tertiary;
              }, outputHandler, outputGetter, output -> this.output = output, inputEmptyCheck, secondaryInputEmptyCheck, tertiaryInputEmptyCheck);
    }

    @Override
    public boolean isInputValid() {
        INPUT_A input = inputHandler.getInput();
        if (inputEmptyCheck.test(input)) {
            return false;
        }
        INPUT_B secondaryInput = secondaryInputHandler.getInput();
        if (secondaryInputEmptyCheck.test(secondaryInput)) {
            return false;
        }
        INPUT_C tertiaryInput = tertiaryInputHandler.getInput();
        return !tertiaryInputEmptyCheck.test(tertiaryInput) && recipe.test(input, secondaryInput, tertiaryInput);
    }

    @Override
    protected boolean finishProcessing(int operations, TransactionContext transaction) {
        //Validate something didn't go horribly wrong
        if (input != null && secondaryInput != null && tertiaryInput != null && output != null && !inputEmptyCheck.test(input) &&
            !secondaryInputEmptyCheck.test(secondaryInput) && !tertiaryInputEmptyCheck.test(tertiaryInput) && !outputEmptyCheck.test(output)) {
            return inputHandler.use(input, operations, transaction) &&
                   secondaryInputHandler.use(secondaryInput, operations, transaction) &&
                   tertiaryInputHandler.use(tertiaryInput, operations, transaction) &&
                   outputHandler.handleOutput(output, operations, transaction);
        }
        return false;
    }

    public static ThreeInputCachedRecipe<Item, ItemStack, Item, ItemStack, Item, ItemStack, ItemStackTemplate, AlloyerRecipe> alloyer(AlloyerRecipe recipe,
          BooleanSupplier recheckAllErrors, IInputHandler<Item, ItemStack> inputHandler, IInputHandler<Item, ItemStack> extraInputHandler,
          IInputHandler<Item, ItemStack> secondExtraInputHandler, IOutputHandler<ItemStackTemplate> outputHandler) {
        return new ThreeInputCachedRecipe<>(recipe, recheckAllErrors, inputHandler, extraInputHandler, secondExtraInputHandler, outputHandler, recipe::getMainInput,
              recipe::getExtraInput, recipe::getTertiaryExtraInput, recipe::getOutput, ItemStack::isEmpty, ItemStack::isEmpty, ItemStack::isEmpty, Objects::isNull);
    }

    public static ThreeInputCachedRecipe<Fluid, FluidStack, Fluid, FluidStack, Fluid, FluidStack, FluidStackTemplate, FluidAlloyingRecipe> fluidAlloyer(
          FluidAlloyingRecipe recipe, BooleanSupplier recheckAllErrors, IInputHandler<Fluid, FluidStack> inputHandler, IInputHandler<Fluid, FluidStack> extraInputHandler,
          IInputHandler<Fluid, FluidStack> secondExtraInputHandler, IOutputHandler<FluidStackTemplate> outputHandler) {
        return new ThreeInputCachedRecipe<>(recipe, recheckAllErrors, inputHandler, extraInputHandler, secondExtraInputHandler, outputHandler, recipe::getMainInput,
              recipe::getExtraInput, recipe::getTertiaryExtraInput, recipe::getOutput, FluidStack::isEmpty, FluidStack::isEmpty, FluidStack::isEmpty, Objects::isNull);
    }
}
