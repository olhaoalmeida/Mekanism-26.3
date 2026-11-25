package fr.iglee42.evolvedmekanism.impl;

import fr.iglee42.evolvedmekanism.recipes.ChemixerRecipe;
import fr.iglee42.evolvedmekanism.registries.EMRecipeSerializers;
import java.util.List;
import java.util.Objects;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.book.InjectingBookCategory.InjectingBookInfo;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BasicChemixerRecipe extends ChemixerRecipe {

    private final ItemStackIngredient mainInput;
    private final ItemStackIngredient extraInput;
    private final ChemicalStackIngredient gasInput;
    private final ItemStackTemplate output;

    public BasicChemixerRecipe(Recipe.CommonInfo commonInfo, InjectingBookInfo bookInfo, ItemStackIngredient mainInput, ItemStackIngredient extraInput,
          ChemicalStackIngredient gasInput, ItemStackTemplate output) {
        super(commonInfo, bookInfo);
        this.mainInput = Objects.requireNonNull(mainInput, "Item input cannot be null.");
        this.extraInput = Objects.requireNonNull(extraInput, "Extra input cannot be null.");
        this.gasInput = Objects.requireNonNull(gasInput, "Gas input cannot be null.");
        this.output = Objects.requireNonNull(output, "Item output cannot be null.");
    }

    @Override
    public ItemStackIngredient getInputMain() {
        return mainInput;
    }

    @Override
    public ItemStackIngredient getInputExtra() {
        return extraInput;
    }

    @Override
    public ChemicalStackIngredient getInputGas() {
        return gasInput;
    }

    @Override
    public ItemStackTemplate getOutput(ItemStack input, ItemStack extra, ChemicalStack gas) {
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
    public RecipeSerializer<BasicChemixerRecipe> getSerializer() {
        return EMRecipeSerializers.CHEMIXER.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicChemixerRecipe other = (BasicChemixerRecipe) o;
        return mainInput.equals(other.mainInput) && extraInput.equals(other.extraInput) && gasInput.equals(other.gasInput) && output.equals(other.output);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainInput, extraInput, gasInput, output);
    }
}
