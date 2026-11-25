package fr.iglee42.evolvedmekanism.recipes;

import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import fr.iglee42.evolvedmekanism.recipes.vanilla_input.TriItemRecipeInput;
import java.util.List;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.book.CombiningBookCategory;
import mekanism.api.recipes.book.CombiningBookCategory.CombiningBookInfo;
import mekanism.api.recipes.display.recipe.CombiningRecipeDisplay;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.TriPredicate;
import net.neoforged.neoforge.registries.DeferredHolder;
import fr.iglee42.evolvedmekanism.registries.EMRecipeType;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * Main Input: ItemStack
 * <br>
 * Secondary Input: ItemStack
 * <br>
 * Tertiary Input: ItemStack
 * <br>
 * Output: ItemStack
 *
 * @apiNote Alloyers and Alloying Factories can process this recipe type.
 */
@NullMarked
public abstract class AlloyerRecipe extends MekanismRecipe<TriItemRecipeInput, CombiningBookCategory, CombiningBookInfo> implements TriPredicate<ItemStack, ItemStack, ItemStack> {

    private static final Holder<Item> STATION = DeferredHolder.create(Registries.ITEM, Identifier.fromNamespaceAndPath(EvolvedMekanism.MODID, "alloyer"));

    protected AlloyerRecipe(Recipe.CommonInfo commonInfo, CombiningBookInfo bookInfo) {
        super(commonInfo, bookInfo, 0F);
    }

    /**
     * Gets the main item input ingredient.
     */
    public abstract ItemStackIngredient getMainInput();

    /**
     * Gets the secondary input ingredient.
     */
    public abstract ItemStackIngredient getExtraInput();

    /**
     * Gets the tertiary input ingredient.
     */
    public abstract ItemStackIngredient getTertiaryExtraInput();

    @Override
    public boolean test(ItemStack input, ItemStack extra, ItemStack secondExtra) {
        return getMainInput().test(input) && getExtraInput().test(extra) && getTertiaryExtraInput().test(secondExtra);
    }

    /**
     * Gets a new output based on the given inputs.
     *
     * @implNote The passed in inputs should <strong>NOT</strong> be modified.
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public abstract ItemStackTemplate getOutput(ItemStack input, ItemStack extra, ItemStack secondExtra);

    /**
     * For recipe viewers, gets the output representations to display.
     *
     * @return Representation of the output, <strong>MUST NOT</strong> be modified.
     */
    public abstract List<ItemStackTemplate> getOutputDefinition();

    public abstract SlotDisplay getOutputDisplay();

    @Override
    public ItemStack assemble(TriItemRecipeInput input) {
        if (!isIncomplete() && test(input.main(), input.extra(), input.secondExtra())) {
            return getOutput(input.main(), input.extra(), input.secondExtra()).create();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(TriItemRecipeInput input, Level level) {
        return !isIncomplete() && test(input.main(), input.extra(), input.secondExtra());
    }

    public boolean isIncomplete() {
        return getMainInput().hasNoMatchingInstances() || getExtraInput().hasNoMatchingInstances() || getTertiaryExtraInput().hasNoMatchingInstances();
    }

    @Override
    public void logMissingTags() {
        getMainInput().logMissingTags();
        getExtraInput().logMissingTags();
        getTertiaryExtraInput().logMissingTags();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeType<AlloyerRecipe> getType() {
        return EMRecipeType.ALLOYING.value();
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new CombiningRecipeDisplay(getMainInput().display(), getExtraInput().display(), getOutputDisplay(), new SlotDisplay.ItemSlotDisplay(STATION), experience()));
    }
}
