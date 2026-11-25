package fr.iglee42.evolvedmekanism.recipes;

import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import fr.iglee42.evolvedmekanism.recipes.vanilla_input.BiItemChemicalRecipeInput;
import fr.iglee42.evolvedmekanism.registries.EMRecipeType;
import java.util.List;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.book.InjectingBookCategory;
import mekanism.api.recipes.book.InjectingBookCategory.InjectingBookInfo;
import mekanism.api.recipes.display.recipe.InjectingRecipeDisplay;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
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
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * Main Input: ItemStack
 * <br>
 * Secondary Input: ItemStack
 * <br>
 * Chemical Input: Chemical
 * <br>
 * Output: ItemStack
 *
 * @apiNote Chemixers can process this recipe type.
 */
@NullMarked
public abstract class ChemixerRecipe extends MekanismRecipe<BiItemChemicalRecipeInput, InjectingBookCategory, InjectingBookInfo> implements TriPredicate<ItemStack, ItemStack, ChemicalStack> {

    private static final Holder<Item> STATION = DeferredHolder.create(Registries.ITEM, Identifier.fromNamespaceAndPath(EvolvedMekanism.MODID, "chemixer"));

    protected ChemixerRecipe(Recipe.CommonInfo commonInfo, InjectingBookInfo bookInfo) {
        super(commonInfo, bookInfo, 0F);
    }

    public abstract ItemStackIngredient getInputMain();

    public abstract ItemStackIngredient getInputExtra();

    public abstract ChemicalStackIngredient getInputGas();

    @Override
    public boolean test(ItemStack solid, ItemStack extra, ChemicalStack gas) {
        return getInputMain().test(solid) && getInputExtra().test(extra) && getInputGas().test(gas);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public abstract ItemStackTemplate getOutput(ItemStack input, ItemStack extra, ChemicalStack gas);

    public abstract List<ItemStackTemplate> getOutputDefinition();

    public abstract SlotDisplay getOutputDisplay();

    @Override
    public ItemStack assemble(BiItemChemicalRecipeInput input) {
        if (!isIncomplete() && test(input.item(), input.extra(), input.chemical())) {
            return getOutput(input.item(), input.extra(), input.chemical()).create();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(BiItemChemicalRecipeInput input, Level level) {
        return !isIncomplete() && test(input.item(), input.extra(), input.chemical());
    }

    public boolean isIncomplete() {
        return getInputMain().hasNoMatchingInstances() || getInputExtra().hasNoMatchingInstances() || getInputGas().hasNoMatchingInstances();
    }

    @Override
    public void logMissingTags() {
        getInputMain().logMissingTags();
        getInputExtra().logMissingTags();
        getInputGas().logMissingTags();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeType<ChemixerRecipe> getType() {
        return EMRecipeType.CHEMIXING.value();
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new InjectingRecipeDisplay(getInputMain().display(), getInputGas().display(), false, experience(), getOutputDisplay(),
              new SlotDisplay.ItemSlotDisplay(STATION)));
    }
}
