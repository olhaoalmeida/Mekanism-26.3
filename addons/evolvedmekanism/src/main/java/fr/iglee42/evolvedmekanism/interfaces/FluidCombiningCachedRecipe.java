package fr.iglee42.evolvedmekanism.interfaces;

import fr.iglee42.evolvedmekanism.recipes.FluidCombiningRecipe;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipeHelper;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class FluidCombiningCachedRecipe extends CachedRecipe<FluidCombiningRecipe> {

    private final IInputHandler<Fluid, FluidStack> inputHandler;
    private final IInputHandler<Fluid, FluidStack> extraInputHandler;
    private final IOutputHandler<FluidStackTemplate> outputHandler;

    @Nullable
    private FluidStack input;
    @Nullable
    private FluidStack extraInput;
    @Nullable
    private FluidStackTemplate output;

    public static FluidCombiningCachedRecipe create(FluidCombiningRecipe recipe, BooleanSupplier recheckAllErrors, IInputHandler<Fluid, FluidStack> inputHandler,
          IInputHandler<Fluid, FluidStack> extraInputHandler, IOutputHandler<FluidStackTemplate> outputHandler) {
        return new FluidCombiningCachedRecipe(recipe, recheckAllErrors, inputHandler, extraInputHandler, outputHandler);
    }

    private FluidCombiningCachedRecipe(FluidCombiningRecipe recipe, BooleanSupplier recheckAllErrors, IInputHandler<Fluid, FluidStack> inputHandler,
          IInputHandler<Fluid, FluidStack> extraInputHandler, IOutputHandler<FluidStackTemplate> outputHandler) {
        super(recipe, recheckAllErrors);
        this.inputHandler = Objects.requireNonNull(inputHandler, "Input handler cannot be null.");
        this.extraInputHandler = Objects.requireNonNull(extraInputHandler, "Extra input handler cannot be null.");
        this.outputHandler = Objects.requireNonNull(outputHandler, "Output handler cannot be null.");
    }

    @Override
    protected void calculateOperationsThisTick(OperationTracker tracker) {
        super.calculateOperationsThisTick(tracker);
        CachedRecipeHelper.twoInputCalculateOperationsThisTick(tracker, inputHandler, recipe::getMainInput, extraInputHandler, recipe::getExtraInput, (a, b) -> {
            this.input = a;
            this.extraInput = b;
        }, outputHandler, recipe::getOutput, out -> this.output = out);
    }

    @Override
    public boolean isInputValid() {
        FluidStack main = inputHandler.getInput();
        if (main.isEmpty()) {
            return false;
        }
        FluidStack extra = extraInputHandler.getInput();
        return !extra.isEmpty() && recipe.test(main, extra);
    }

    @Override
    protected boolean finishProcessing(int operations, TransactionContext transaction) {
        if (input != null && extraInput != null && output != null && !input.isEmpty() && !extraInput.isEmpty()) {
            return inputHandler.use(input, operations, transaction) &&
                   extraInputHandler.use(extraInput, operations, transaction) &&
                   outputHandler.handleOutput(output, operations, transaction);
        }
        return false;
    }
}
