package fr.iglee42.evolvedmekanism.interfaces;

import fr.iglee42.evolvedmekanism.recipes.ChemixerRecipe;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ChemixerCachedRecipe extends CachedRecipe<ChemixerRecipe> {

    private final IOutputHandler<ItemStackTemplate> outputHandler;
    private final IInputHandler<Item, ItemStack> itemInputHandler;
    private final IInputHandler<Item, ItemStack> extraInputHandler;
    private final IInputHandler<Chemical, ChemicalStack> gasInputHandler;

    private ItemStack recipeItem = ItemStack.EMPTY;
    private ItemStack recipeExtra = ItemStack.EMPTY;
    private ChemicalStack recipeGas = ChemicalStack.EMPTY;
    @Nullable
    private ItemStackTemplate output;

    public ChemixerCachedRecipe(ChemixerRecipe recipe, BooleanSupplier recheckAllErrors, IInputHandler<Item, ItemStack> itemInputHandler,
          IInputHandler<Item, ItemStack> extraInputHandler, IInputHandler<Chemical, ChemicalStack> gasInputHandler, IOutputHandler<ItemStackTemplate> outputHandler) {
        super(recipe, recheckAllErrors);
        this.itemInputHandler = Objects.requireNonNull(itemInputHandler, "Item input handler cannot be null.");
        this.extraInputHandler = Objects.requireNonNull(extraInputHandler, "Extra input handler cannot be null.");
        this.gasInputHandler = Objects.requireNonNull(gasInputHandler, "Gas input handler cannot be null.");
        this.outputHandler = Objects.requireNonNull(outputHandler, "Output handler cannot be null.");
    }

    @Override
    protected void calculateOperationsThisTick(OperationTracker tracker) {
        super.calculateOperationsThisTick(tracker);
        if (tracker.shouldContinueChecking()) {
            recipeItem = itemInputHandler.getRecipeInput(recipe.getInputMain());
            if (recipeItem.isEmpty()) {
                tracker.mismatchedRecipe();
                return;
            }
            recipeExtra = extraInputHandler.getRecipeInput(recipe.getInputExtra());
            if (recipeExtra.isEmpty()) {
                tracker.mismatchedRecipe();
                return;
            }
            recipeGas = gasInputHandler.getRecipeInput(recipe.getInputGas());
            if (recipeGas.isEmpty()) {
                tracker.mismatchedRecipe();
                return;
            }
            itemInputHandler.calculateOperationsCanSupport(tracker, recipeItem);
            if (tracker.shouldContinueChecking()) {
                extraInputHandler.calculateOperationsCanSupport(tracker, recipeExtra);
                if (tracker.shouldContinueChecking()) {
                    gasInputHandler.calculateOperationsCanSupport(tracker, recipeGas);
                    if (tracker.shouldContinueChecking()) {
                        output = recipe.getOutput(recipeItem, recipeExtra, recipeGas);
                        outputHandler.calculateOperationsCanSupport(tracker, output);
                    }
                }
            }
        }
    }

    @Override
    public boolean isInputValid() {
        ItemStack item = itemInputHandler.getInput();
        if (item.isEmpty()) {
            return false;
        }
        ChemicalStack gas = gasInputHandler.getInput();
        if (gas.isEmpty()) {
            return false;
        }
        ItemStack extra = extraInputHandler.getInput();
        return !extra.isEmpty() && recipe.test(item, extra, gas);
    }

    @Override
    protected boolean finishProcessing(int operations, TransactionContext transaction) {
        if (output != null && !recipeItem.isEmpty() && !recipeExtra.isEmpty() && !recipeGas.isEmpty()) {
            return itemInputHandler.use(recipeItem, operations, transaction) &&
                   extraInputHandler.use(recipeExtra, operations, transaction) &&
                   gasInputHandler.use(recipeGas, operations, transaction) &&
                   outputHandler.handleOutput(output, operations, transaction);
        }
        return false;
    }
}
