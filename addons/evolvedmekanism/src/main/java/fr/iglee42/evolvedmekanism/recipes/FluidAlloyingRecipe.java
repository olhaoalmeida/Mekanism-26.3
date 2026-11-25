package fr.iglee42.evolvedmekanism.recipes;

import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import fr.iglee42.evolvedmekanism.recipes.vanilla_input.TriFluidRecipeInput;
import fr.iglee42.evolvedmekanism.registries.EMRecipeType;
import java.util.List;
import java.util.Objects;
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
import net.neoforged.neoforge.common.util.TriPredicate;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.crafting.display.FluidStackSlotDisplay;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class FluidAlloyingRecipe extends MekanismRecipe<TriFluidRecipeInput, CombiningBookCategory, CombiningBookInfo> implements TriPredicate<FluidStack, FluidStack, FluidStack> {

    private static final Holder<Item> STATION = DeferredHolder.create(Registries.ITEM, Identifier.fromNamespaceAndPath(EvolvedMekanism.MODID, "fluid_alloyer"));

    private final FluidStackIngredient mainInput;
    private final FluidStackIngredient extraInput;
    private final FluidStackIngredient tertiaryInput;
    private final FluidStackTemplate output;

    protected FluidAlloyingRecipe(Recipe.CommonInfo commonInfo, CombiningBookInfo bookInfo, FluidStackIngredient mainInput, FluidStackIngredient extraInput,
          FluidStackIngredient tertiaryInput, FluidStackTemplate output) {
        super(commonInfo, bookInfo, 0F);
        this.mainInput = Objects.requireNonNull(mainInput, "Main input cannot be null.");
        this.extraInput = Objects.requireNonNull(extraInput, "Secondary input cannot be null.");
        this.tertiaryInput = Objects.requireNonNull(tertiaryInput, "Tertiary input cannot be null.");
        this.output = Objects.requireNonNull(output, "Output cannot be null.");
    }

    @Override
    public boolean test(FluidStack main, FluidStack extra, FluidStack tertiary) {
        return mainInput.test(main) && extraInput.test(extra) && tertiaryInput.test(tertiary);
    }

    public FluidStackIngredient getMainInput() {
        return mainInput;
    }

    public FluidStackIngredient getExtraInput() {
        return extraInput;
    }

    public FluidStackIngredient getTertiaryExtraInput() {
        return tertiaryInput;
    }

    public FluidStackTemplate getOutputRaw() {
        return output;
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public FluidStackTemplate getOutput(FluidStack main, FluidStack extra, FluidStack tertiary) {
        return output;
    }

    public List<FluidStackTemplate> getOutputDefinition() {
        return List.of(output);
    }

    public SlotDisplay getOutputDisplay() {
        return new FluidStackSlotDisplay(output);
    }

    public boolean isIncomplete() {
        return mainInput.hasNoMatchingInstances() || extraInput.hasNoMatchingInstances() || tertiaryInput.hasNoMatchingInstances();
    }

    @Override
    public boolean matches(TriFluidRecipeInput input, Level level) {
        return !isIncomplete() && test(input.main(), input.extra(), input.secondExtra());
    }

    @Override
    public void logMissingTags() {
        mainInput.logMissingTags();
        extraInput.logMissingTags();
        tertiaryInput.logMissingTags();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeType<FluidAlloyingRecipe> getType() {
        return EMRecipeType.FLUID_ALLOYING.value();
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
        FluidAlloyingRecipe other = (FluidAlloyingRecipe) o;
        return mainInput.equals(other.mainInput) && extraInput.equals(other.extraInput) && tertiaryInput.equals(other.tertiaryInput) && output.equals(other.output);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainInput, extraInput, tertiaryInput, output);
    }
}
