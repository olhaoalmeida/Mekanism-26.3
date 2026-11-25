package fr.iglee42.evolvedmekanism.recipes;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.ingredients.InputIngredient;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;
import net.minecraft.core.TypedInstance;
import org.apache.commons.lang3.function.TriConsumer;
import org.apache.commons.lang3.function.TriFunction;

public class EMCachedRecipeHelper {

    private EMCachedRecipeHelper() {
    }

    public static <HOLDER_A, INPUT_A extends TypedInstance<HOLDER_A>, HOLDER_B, INPUT_B extends TypedInstance<HOLDER_B>, HOLDER_C, INPUT_C extends TypedInstance<HOLDER_C>,
          OUTPUT> void threeInputCalculateOperationsThisTick(CachedRecipe.OperationTracker tracker, IInputHandler<HOLDER_A, INPUT_A> inputAHandler,
          Supplier<? extends InputIngredient<HOLDER_A, INPUT_A>> inputAIngredient, IInputHandler<HOLDER_B, INPUT_B> inputBHandler,
          Supplier<? extends InputIngredient<HOLDER_B, INPUT_B>> inputBIngredient, IInputHandler<HOLDER_C, INPUT_C> inputCHandler,
          Supplier<? extends InputIngredient<HOLDER_C, INPUT_C>> inputCIngredient, TriConsumer<INPUT_A, INPUT_B, INPUT_C> inputsSetter,
          IOutputHandler<OUTPUT> outputHandler, TriFunction<INPUT_A, INPUT_B, INPUT_C, OUTPUT> outputGetter, Consumer<OUTPUT> outputSetter,
          Predicate<INPUT_A> emptyCheckA, Predicate<INPUT_B> emptyCheckB, Predicate<INPUT_C> emptyCheckC) {
        if (tracker.shouldContinueChecking()) {
            INPUT_A inputA = inputAHandler.getRecipeInput(inputAIngredient.get());
            if (emptyCheckA.test(inputA)) {
                tracker.mismatchedRecipe();
                return;
            }
            INPUT_B inputB = inputBHandler.getRecipeInput(inputBIngredient.get());
            if (emptyCheckB.test(inputB)) {
                tracker.mismatchedRecipe();
                return;
            }
            INPUT_C inputC = inputCHandler.getRecipeInput(inputCIngredient.get());
            if (emptyCheckC.test(inputC)) {
                tracker.mismatchedRecipe();
                return;
            }
            inputsSetter.accept(inputA, inputB, inputC);
            inputAHandler.calculateOperationsCanSupport(tracker, inputA);
            if (tracker.shouldContinueChecking()) {
                inputBHandler.calculateOperationsCanSupport(tracker, inputB);
                if (tracker.shouldContinueChecking()) {
                    inputCHandler.calculateOperationsCanSupport(tracker, inputC);
                    if (tracker.shouldContinueChecking()) {
                        OUTPUT output = outputGetter.apply(inputA, inputB, inputC);
                        outputSetter.accept(output);
                        outputHandler.calculateOperationsCanSupport(tracker, output);
                    }
                }
            }
        }
    }
}
