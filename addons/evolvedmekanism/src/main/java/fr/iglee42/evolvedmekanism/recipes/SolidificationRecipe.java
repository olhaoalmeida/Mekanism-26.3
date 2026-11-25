package fr.iglee42.evolvedmekanism.recipes;

import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import fr.iglee42.evolvedmekanism.recipes.vanilla_input.SingleItemBiFluidRecipeInput;
import fr.iglee42.evolvedmekanism.registries.EMRecipeType;
import java.util.List;
import java.util.Objects;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.book.CombiningBookCategory;
import mekanism.api.recipes.book.CombiningBookCategory.CombiningBookInfo;
import mekanism.api.recipes.display.recipe.CombiningRecipeDisplay;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * Input: ItemStack, FluidStack, FluidStack
 * <br>
 * Output: ItemStack
 *
 * @apiNote Solidification Chambers can process this recipe type.
 */
@NullMarked
public abstract class SolidificationRecipe extends MekanismRecipe<SingleItemBiFluidRecipeInput, CombiningBookCategory, CombiningBookInfo> implements TriPredicate<ItemStack, FluidStack, FluidStack> {

    private static final Holder<Item> STATION = DeferredHolder.create(Registries.ITEM, Identifier.fromNamespaceAndPath(EvolvedMekanism.MODID, "solidification_chamber"));

    private final ItemStackIngredient inputSolid;
    private final FluidStackIngredient inputFluid;
    private final FluidStackIngredient fluidInputExtra;
    private final long energyRequired;
    private final int duration;
    private final ItemStackTemplate output;
    private final boolean keepItem;

    /**
     * @param energyRequired Amount of "extra" energy this recipe requires, compared to the base energy requirements of the machine performing the recipe.
     * @param duration       Base duration in ticks that this recipe takes to complete. Must be greater than zero.
     * @param keepItem       Should the input item not be consumed
     */
    protected SolidificationRecipe(Recipe.CommonInfo commonInfo, CombiningBookInfo bookInfo, ItemStackIngredient inputSolid, FluidStackIngredient inputFluid,
          FluidStackIngredient inputFluidExtra, long energyRequired, int duration, ItemStackTemplate output, boolean keepItem) {
        super(commonInfo, bookInfo, 0F);
        this.inputSolid = Objects.requireNonNull(inputSolid, "Item input cannot be null.");
        this.inputFluid = Objects.requireNonNull(inputFluid, "Fluid input cannot be null.");
        this.fluidInputExtra = Objects.requireNonNull(inputFluidExtra, "Extra fluid input cannot be null.");
        this.energyRequired = energyRequired;
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be positive.");
        }
        this.duration = duration;
        this.output = Objects.requireNonNull(output, "Item output cannot be null.");
        this.keepItem = keepItem;
    }

    public ItemStackIngredient getInputSolid() {
        return inputSolid;
    }

    public FluidStackIngredient getInputFluid() {
        return inputFluid;
    }

    public FluidStackIngredient getFluidInputExtra() {
        return fluidInputExtra;
    }

    /**
     * Gets the amount of "extra" energy this recipe requires, compared to the base energy requirements of the machine performing the recipe.
     */
    public long getEnergyRequired() {
        return energyRequired;
    }

    /**
     * Gets the base duration in ticks that this recipe takes to complete.
     */
    public int getDuration() {
        return duration;
    }

    public boolean shouldKeepItem() {
        return keepItem;
    }

    @Override
    public boolean test(ItemStack solid, FluidStack liquid, FluidStack extra) {
        return inputSolid.test(solid) && inputFluid.test(liquid) && fluidInputExtra.test(extra);
    }

    public List<ItemStackTemplate> getOutputDefinition() {
        return List.of(output);
    }

    public SlotDisplay getOutputDisplay() {
        return new SlotDisplay.ItemStackSlotDisplay(output);
    }

    /**
     * @implNote The passed in inputs should <strong>NOT</strong> be modified.
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public ItemStackTemplate getOutput(ItemStack solid, FluidStack liquid, FluidStack extra) {
        return output;
    }

    public ItemStackTemplate getOutputRaw() {
        return output;
    }

    public boolean isIncomplete() {
        return inputSolid.hasNoMatchingInstances() || inputFluid.hasNoMatchingInstances() || fluidInputExtra.hasNoMatchingInstances();
    }

    @Override
    public boolean matches(SingleItemBiFluidRecipeInput input, Level level) {
        return !isIncomplete() && test(input.item(), input.fluid(), input.fluidExtra());
    }

    @Override
    public void logMissingTags() {
        inputSolid.logMissingTags();
        inputFluid.logMissingTags();
        fluidInputExtra.logMissingTags();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeType<SolidificationRecipe> getType() {
        return EMRecipeType.SOLIDIFICATION.value();
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new CombiningRecipeDisplay(inputSolid.display(), inputFluid.display(), getOutputDisplay(), new SlotDisplay.ItemSlotDisplay(STATION), experience()));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SolidificationRecipe other = (SolidificationRecipe) o;
        return inputSolid.equals(other.inputSolid) && inputFluid.equals(other.inputFluid) && fluidInputExtra.equals(other.fluidInputExtra) &&
               energyRequired == other.energyRequired && duration == other.duration && keepItem == other.keepItem && output.equals(other.output);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputSolid, inputFluid, fluidInputExtra, energyRequired, duration, output, keepItem);
    }
}
