package fr.iglee42.evolvedmekanism.recipes;

import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import fr.iglee42.evolvedmekanism.registries.EMRecipeType;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.book.LiquificationBookCategory;
import mekanism.api.recipes.book.LiquificationBookCategory.LiquificationBookInfo;
import mekanism.api.recipes.display.recipe.LiquificationRecipeDisplay;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.crafting.display.FluidStackSlotDisplay;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * Input: ItemStack
 * <br>
 * Output: FluidStack
 *
 * @apiNote Thermalizer can process this recipe type.
 */
@NullMarked
public abstract class MeltingRecipe extends MekanismRecipe<SingleRecipeInput, LiquificationBookCategory, LiquificationBookInfo> implements Predicate<ItemStack> {

    private static final Holder<Item> STATION = DeferredHolder.create(Registries.ITEM, Identifier.fromNamespaceAndPath(EvolvedMekanism.MODID, "thermalizer"));

    private final ItemStackIngredient input;
    private final FluidStackTemplate output;

    protected MeltingRecipe(Recipe.CommonInfo commonInfo, LiquificationBookInfo bookInfo, ItemStackIngredient input, FluidStackTemplate output) {
        super(commonInfo, bookInfo, 0F);
        this.input = Objects.requireNonNull(input, "Item input cannot be null.");
        this.output = Objects.requireNonNull(output, "Fluid output cannot be null.");
    }

    /**
     * Gets the item input ingredient.
     */
    public ItemStackIngredient getInput() {
        return input;
    }

    @Override
    public boolean test(ItemStack input) {
        return this.input.test(input);
    }

    /**
     * For recipe viewers, gets the output to display.
     */
    public List<FluidStackTemplate> getOutputDefinition() {
        return List.of(output);
    }

    public SlotDisplay getOutputDisplay() {
        return new FluidStackSlotDisplay(output);
    }

    /**
     * Gets a new output based on the given inputs.
     *
     * @implNote The passed in inputs should <strong>NOT</strong> be modified.
     */
    @Contract(pure = true)
    public FluidStackTemplate getOutput(ItemStack input) {
        return output;
    }

    public FluidStackTemplate getOutputRaw() {
        return output;
    }

    public boolean isIncomplete() {
        return input.hasNoMatchingInstances();
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return !isIncomplete() && test(input.item());
    }

    @Override
    public void logMissingTags() {
        input.logMissingTags();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeType<MeltingRecipe> getType() {
        return EMRecipeType.MELTING.value();
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new LiquificationRecipeDisplay(input.display(), getOutputDisplay(), new SlotDisplay.ItemSlotDisplay(STATION), experience()));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MeltingRecipe other = (MeltingRecipe) o;
        return input.equals(other.input) && output.equals(other.output);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, output);
    }
}
