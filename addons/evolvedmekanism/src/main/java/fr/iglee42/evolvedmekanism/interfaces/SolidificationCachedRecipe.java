package fr.iglee42.evolvedmekanism.interfaces;

import fr.iglee42.evolvedmekanism.recipes.SolidificationRecipe;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Base class to help implement handling of solidification recipes (item + two fluids to item).
 */
@NullMarked
public class SolidificationCachedRecipe extends CachedRecipe<SolidificationRecipe> {

    private final IOutputHandler<ItemStackTemplate> outputHandler;
    private final IInputHandler<Item, ItemStack> itemInputHandler;
    private final IInputHandler<Fluid, FluidStack> fluidInputHandler;
    private final IInputHandler<Fluid, FluidStack> extraFluidInputHandler;

    private ItemStack recipeItem = ItemStack.EMPTY;
    private FluidStack recipeFluid = FluidStack.EMPTY;
    private FluidStack recipeExtraFluid = FluidStack.EMPTY;
    @Nullable
    private ItemStackTemplate output;

    public SolidificationCachedRecipe(SolidificationRecipe recipe, BooleanSupplier recheckAllErrors, IInputHandler<Item, ItemStack> itemInputHandler,
          IInputHandler<Fluid, FluidStack> fluidInputHandler, IInputHandler<Fluid, FluidStack> extraFluidInputHandler, IOutputHandler<ItemStackTemplate> outputHandler) {
        super(recipe, recheckAllErrors);
        this.itemInputHandler = Objects.requireNonNull(itemInputHandler, "Item input handler cannot be null.");
        this.fluidInputHandler = Objects.requireNonNull(fluidInputHandler, "Fluid input handler cannot be null.");
        this.extraFluidInputHandler = Objects.requireNonNull(extraFluidInputHandler, "Extra fluid input handler cannot be null.");
        this.outputHandler = Objects.requireNonNull(outputHandler, "Output handler cannot be null.");
    }

    @Override
    protected void calculateOperationsThisTick(OperationTracker tracker) {
        super.calculateOperationsThisTick(tracker);
        if (tracker.shouldContinueChecking()) {
            recipeItem = itemInputHandler.getRecipeInput(recipe.getInputSolid());
            if (recipeItem.isEmpty()) {
                tracker.mismatchedRecipe();
                return;
            }
            recipeFluid = fluidInputHandler.getRecipeInput(recipe.getInputFluid());
            if (recipeFluid.isEmpty()) {
                tracker.mismatchedRecipe();
                return;
            }
            recipeExtraFluid = extraFluidInputHandler.getRecipeInput(recipe.getFluidInputExtra());
            if (recipeExtraFluid.isEmpty()) {
                tracker.mismatchedRecipe();
                return;
            }
            itemInputHandler.calculateOperationsCanSupport(tracker, recipeItem);
            if (tracker.shouldContinueChecking()) {
                fluidInputHandler.calculateOperationsCanSupport(tracker, recipeFluid);
                if (tracker.shouldContinueChecking()) {
                    extraFluidInputHandler.calculateOperationsCanSupport(tracker, recipeExtraFluid);
                    if (tracker.shouldContinueChecking()) {
                        output = recipe.getOutput(recipeItem, recipeFluid, recipeExtraFluid);
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
        FluidStack extraFluid = extraFluidInputHandler.getInput();
        if (extraFluid.isEmpty()) {
            return false;
        }
        FluidStack fluid = fluidInputHandler.getInput();
        return !fluid.isEmpty() && recipe.test(item, fluid, extraFluid);
    }

    @Override
    protected boolean finishProcessing(int operations, TransactionContext transaction) {
        if (output != null && !recipeItem.isEmpty() && !recipeFluid.isEmpty() && !recipeExtraFluid.isEmpty()) {
            boolean used = recipe.shouldKeepItem() || itemInputHandler.use(recipeItem, operations, transaction);
            return used && fluidInputHandler.use(recipeFluid, operations, transaction) &&
                   extraFluidInputHandler.use(recipeExtraFluid, operations, transaction) &&
                   outputHandler.handleOutput(output, operations, transaction);
        }
        return false;
    }
}
