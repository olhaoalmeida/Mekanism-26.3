package fr.iglee42.evolvedmekanism.impl;

import fr.iglee42.evolvedmekanism.recipes.AlloyerRecipe;
import fr.iglee42.evolvedmekanism.registries.EMRecipeSerializers;
import java.util.List;
import java.util.Objects;
import mekanism.api.recipes.book.CombiningBookCategory.CombiningBookInfo;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BasicAlloyerRecipe extends AlloyerRecipe {

    private final ItemStackIngredient mainInput;
    private final ItemStackIngredient extraInput;
    private final ItemStackIngredient tertiaryExtraInput;
    private final ItemStackTemplate output;

    public BasicAlloyerRecipe(Recipe.CommonInfo commonInfo, CombiningBookInfo bookInfo, ItemStackIngredient mainInput, ItemStackIngredient extraInput,
          ItemStackIngredient secondaryExtraInput, ItemStackTemplate output) {
        super(commonInfo, bookInfo);
        this.mainInput = Objects.requireNonNull(mainInput, "Main input cannot be null.");
        this.extraInput = Objects.requireNonNull(extraInput, "Secondary input cannot be null.");
        this.tertiaryExtraInput = Objects.requireNonNull(secondaryExtraInput, "Tertiary input cannot be null.");
        this.output = Objects.requireNonNull(output, "Output cannot be null.");
    }

    @Override
    public ItemStackIngredient getMainInput() {
        return mainInput;
    }

    @Override
    public ItemStackIngredient getExtraInput() {
        return extraInput;
    }

    @Override
    public ItemStackIngredient getTertiaryExtraInput() {
        return tertiaryExtraInput;
    }

    @Override
    public ItemStackTemplate getOutput(ItemStack input, ItemStack extra, ItemStack secondExtra) {
        return output;
    }

    @Override
    public List<ItemStackTemplate> getOutputDefinition() {
        return List.of(output);
    }

    @Override
    public SlotDisplay getOutputDisplay() {
        return new SlotDisplay.ItemStackSlotDisplay(output);
    }

    /**
     * For Serializer use. DO NOT MODIFY RETURN VALUE.
     */
    public ItemStackTemplate getOutputRaw() {
        return output;
    }

    @Override
    public RecipeSerializer<BasicAlloyerRecipe> getSerializer() {
        return EMRecipeSerializers.ALLOYER.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicAlloyerRecipe other = (BasicAlloyerRecipe) o;
        return mainInput.equals(other.mainInput) && extraInput.equals(other.extraInput) && tertiaryExtraInput.equals(other.tertiaryExtraInput) && output.equals(other.output);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainInput, extraInput, tertiaryExtraInput, output);
    }
}
