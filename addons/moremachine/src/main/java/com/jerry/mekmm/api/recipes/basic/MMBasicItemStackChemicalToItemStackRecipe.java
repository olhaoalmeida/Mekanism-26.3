package com.jerry.mekmm.api.recipes.basic;

import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.Recipe;
import mekanism.api.recipes.book.InjectingBookCategory.InjectingBookInfo;
import mekanism.api.recipes.book.InjectingBookCategory;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.ItemStackChemicalToItemStackRecipe;
import mekanism.api.recipes.basic.IBasicItemStackOutput;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.core.TypedInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@NullMarked
public abstract class MMBasicItemStackChemicalToItemStackRecipe extends ItemStackChemicalToItemStackRecipe<InjectingBookCategory, InjectingBookInfo> implements IBasicItemStackOutput {

    protected final ItemStackIngredient itemInput;
    protected final ChemicalStackIngredient chemicalInput;
    protected final ItemStackTemplate output;

    /**
     * @param itemInput     Item input.
     * @param chemicalInput Chemical input.
     * @param output        Output.
     */
    public MMBasicItemStackChemicalToItemStackRecipe(Recipe.CommonInfo commonInfo, InjectingBookInfo bookInfo, ItemStackIngredient itemInput, ChemicalStackIngredient chemicalInput, ItemStackTemplate output) {
        super(commonInfo, bookInfo, 0F);
        this.itemInput = Objects.requireNonNull(itemInput, "Item input cannot be null.");
        this.chemicalInput = Objects.requireNonNull(chemicalInput, "Chemical input cannot be null.");
        Objects.requireNonNull(output, "Output cannot be null.");
        this.output = output;
    }

    @Override
    public ItemStackIngredient getItemInput() {
        return itemInput;
    }

    @Override
    public ChemicalStackIngredient getChemicalInput() {
        return chemicalInput;
    }

    @Override
    @Contract(pure = true)
    public ItemStackTemplate getOutput(TypedInstance<Item> inputItem, TypedInstance<Chemical> inputChemical) {
        return output;
    }

    @Override
    public final boolean perTickUsage() {
        return true;
    }

    @Override
    public SlotDisplay getOutputDisplay() {
        return new SlotDisplay.ItemStackSlotDisplay(output);
    }

    @Override
    public List<@NotNull ItemStackTemplate> getOutputDefinition(ContextMap contextMap) {
        return Collections.singletonList(output);
    }

    @Override
    public ItemStackTemplate getOutputRaw() {
        return output;
    }
}
