package fr.iglee42.evolvedmekanism.recipes;

import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import fr.iglee42.evolvedmekanism.recipes.vanilla_input.BiFluidRecipeInput;
import fr.iglee42.evolvedmekanism.registries.EMRecipeType;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.book.CombiningBookCategory;
import mekanism.api.recipes.book.CombiningBookCategory.CombiningBookInfo;
import mekanism.api.recipes.display.recipe.CombiningRecipeDisplay;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.crafting.display.FluidStackSlotDisplay;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * Main Input: FluidStack, Secondary Input: FluidStack, Output: FluidStack
 */
@NullMarked
public abstract class FluidCombiningRecipe extends MekanismRecipe<BiFluidRecipeInput, CombiningBookCategory, CombiningBookInfo> implements BiPredicate<FluidStack, FluidStack> {

    private static final Holder<Item> STATION = DeferredHolder.create(Registries.ITEM, Identifier.fromNamespaceAndPath(EvolvedMekanism.MODID, "fluid_combiner"));

    private final FluidStackIngredient mainInput;
    private final FluidStackIngredient extraInput;
    private final FluidStackTemplate output;

    protected FluidCombiningRecipe(Recipe.CommonInfo commonInfo, CombiningBookInfo bookInfo, FluidStackIngredient mainInput, FluidStackIngredient extraInput,
          FluidStackTemplate output) {
        super(commonInfo, bookInfo, 0F);
        this.mainInput = Objects.requireNonNull(mainInput, "Main input cannot be null.");
        this.extraInput = Objects.requireNonNull(extraInput, "Secondary input cannot be null.");
        this.output = Objects.requireNonNull(output, "Output cannot be null.");
    }

    @Override
    public boolean test(FluidStack main, FluidStack extra) {
        return mainInput.test(main) && extraInput.test(extra);
    }

    public FluidStackIngredient getMainInput() {
        return mainInput;
    }

    public FluidStackIngredient getExtraInput() {
        return extraInput;
    }

    public FluidStackTemplate getOutputRaw() {
        return output;
    }

    @Contract(value = "_, _ -> new", pure = true)
    public FluidStackTemplate getOutput(FluidStack main, FluidStack extra) {
        return output;
    }

    public List<FluidStackTemplate> getOutputDefinition() {
        return List.of(output);
    }

    public SlotDisplay getOutputDisplay() {
        return new FluidStackSlotDisplay(output);
    }

    public boolean isIncomplete() {
        return mainInput.hasNoMatchingInstances() || extraInput.hasNoMatchingInstances();
    }

    @Override
    public boolean matches(BiFluidRecipeInput input, Level level) {
        return !isIncomplete() && test(input.main(), input.extra());
    }

    @Override
    public void logMissingTags() {
        mainInput.logMissingTags();
        extraInput.logMissingTags();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeType<FluidCombiningRecipe> getType() {
        return EMRecipeType.FLUID_COMBINING.value();
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new CombiningRecipeDisplay(mainInput.display(), extraInput.display(), getOutputDisplay(), new SlotDisplay.ItemSlotDisplay(STATION), experience()));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FluidCombiningRecipe other = (FluidCombiningRecipe) o;
        return mainInput.equals(other.mainInput) && extraInput.equals(other.extraInput) && output.equals(other.output);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainInput, extraInput, output);
    }
}
