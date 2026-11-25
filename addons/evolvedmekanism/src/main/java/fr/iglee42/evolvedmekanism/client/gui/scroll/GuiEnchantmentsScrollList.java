package fr.iglee42.evolvedmekanism.client.gui.scroll;

import com.mojang.datafixers.util.Pair;
import fr.iglee42.evolvedmekanism.utils.EMEnchantmentHelper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiElementHolder;
import mekanism.client.gui.element.scroll.GuiInstallableScrollList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;


public class GuiEnchantmentsScrollList extends GuiInstallableScrollList<Pair<Holder<Enchantment>, Integer>> {

    private final Supplier<ItemStack> itemSupplier;
    private ItemStack currentItem = ItemStack.EMPTY;
    private final Map<Holder<Enchantment>, Integer> originalLevels = new HashMap<>();
    //Pending levels, only applied to the item when the player presses "set"
    private final Map<Holder<Enchantment>, Integer> pendingLevels = new HashMap<>();
    private final List<Pair<Holder<Enchantment>, Integer>> currentList = new ArrayList<>();
    @Nullable
    private Holder<Enchantment> selectedEnchantment;

    public GuiEnchantmentsScrollList(IGuiWrapper gui, int x, int y, int height, Supplier<ItemStack> itemSupplier) {
        super(gui, x, y, 120, height, GuiElementHolder.HOLDER);
        this.itemSupplier = itemSupplier;
        updateItemAndList(itemSupplier.get());
    }

    public void updateItemAndList(ItemStack stack) {
        currentItem = stack;
        originalLevels.clear();
        pendingLevels.clear();
        if (!stack.isEmpty() && Minecraft.getInstance().level != null) {
            Map<Holder<Enchantment>, Integer> levels = EMEnchantmentHelper.getEnchantmentsForItem(Minecraft.getInstance().level.registryAccess(), stack);
            originalLevels.putAll(levels);
            pendingLevels.putAll(levels);
        }
        if (selectedEnchantment != null && !pendingLevels.containsKey(selectedEnchantment)) {
            selectedEnchantment = null;
        }
        rebuild();
    }

    private void rebuild() {
        currentList.clear();
        pendingLevels.entrySet().stream()
                .sorted(Comparator.<Map.Entry<Holder<Enchantment>, Integer>>comparingInt(e -> e.getValue() > 0 ? 0 : (isBlocked(e.getKey()) ? 2 : 1))
                        .thenComparing(e -> e.getKey().value().description().getString()))
                .forEach(e -> currentList.add(Pair.of(e.getKey(), e.getValue())));
        selectedType = null;
        if (selectedEnchantment != null) {
            for (int i = 0; i < currentList.size(); i++) {
                Pair<Holder<Enchantment>, Integer> pair = currentList.get(i);
                if (pair.getFirst().equals(selectedEnchantment)) {
                    selectedType = pair;
                    if (needsScrollBars()) {
                        int firstVisible = getCurrentSelection();
                        int focusedElements = getFocusedElements();
                        if (i < firstVisible || i >= firstVisible + focusedElements) {
                            int maxFirstVisible = getElements();
                            int newFirstVisible = Math.min(i < firstVisible ? i : i - focusedElements + 1, maxFirstVisible);
                            scroll = newFirstVisible / (maxFirstVisible + 0.5);
                        }
                    }
                    break;
                }
            }
        }
    }

    private void recheckItem() {
        ItemStack stack = itemSupplier.get();
        if (!ItemStack.matches(currentItem, stack)) {
            updateItemAndList(stack.copy());
        }
    }

    public Map<Holder<Enchantment>, Integer> getPendingLevels() {
        return pendingLevels;
    }

    public boolean hasChanges() {
        return !pendingLevels.equals(originalLevels);
    }

    /** Total number of levels that will be added compared to the item's current enchantments. */
    public int getAddedLevels() {
        int added = 0;
        for (Map.Entry<Holder<Enchantment>, Integer> entry : pendingLevels.entrySet()) {
            added += Math.max(0, entry.getValue() - originalLevels.getOrDefault(entry.getKey(), 0));
        }
        return added;
    }

    /** An enchantment is blocked if it is currently disabled and incompatible with another enabled one. */
    private boolean isBlocked(Holder<Enchantment> enchantment) {
        if (pendingLevels.getOrDefault(enchantment, 0) > 0) {
            return false;
        }
        for (Map.Entry<Holder<Enchantment>, Integer> entry : pendingLevels.entrySet()) {
            if (entry.getValue() > 0 && !entry.getKey().equals(enchantment) && !Enchantment.areCompatible(enchantment, entry.getKey())) {
                return true;
            }
        }
        return false;
    }

    public boolean canChangeSelected(int delta) {
        if (selectedEnchantment == null) {
            return false;
        }
        int level = pendingLevels.getOrDefault(selectedEnchantment, 0);
        int newLevel = level + delta;
        if (newLevel < minimumLevel(selectedEnchantment) || newLevel > selectedEnchantment.value().getMaxLevel()) {
            return false;
        }
        return delta <= 0 || level > 0 || !isBlocked(selectedEnchantment);
    }

    /** Curses already on the item cannot be lowered. A curse added in this edit can still be cancelled. */
    private int minimumLevel(Holder<Enchantment> enchantment) {
        if (EMEnchantmentHelper.isCurse(enchantment)) {
            return originalLevels.getOrDefault(enchantment, 0);
        }
        return 0;
    }

    /** Moves one level in the given direction, or straight to the max or minimum level when full is set. */
    public void stepSelected(int direction, boolean full) {
        if (selectedEnchantment == null) {
            return;
        }
        int level = pendingLevels.getOrDefault(selectedEnchantment, 0);
        int delta = full ? (direction > 0 ? selectedEnchantment.value().getMaxLevel() - level : minimumLevel(selectedEnchantment) - level) : direction;
        if (delta != 0) {
            changeSelected(delta);
        }
    }

    public void changeSelected(int delta) {
        if (canChangeSelected(delta)) {
            pendingLevels.merge(selectedEnchantment, delta, Integer::sum);
            rebuild();
        }
    }

    @Override
    protected void setSelected(@Nullable Pair<Holder<Enchantment>, Integer> newData) {
        selectedEnchantment = newData == null ? null : newData.getFirst();
        selectedType = newData;
    }

    @Override
    protected List<Pair<Holder<Enchantment>, Integer>> getCurrentInstalled() {
        return currentList;
    }

    @Override
    protected void drawName(GuiGraphicsExtractor guiGraphics, Pair<Holder<Enchantment>, Integer> enchantment, int y) {
        boolean enabled = enchantment.getSecond() > 0;
        boolean blocked = isBlocked(enchantment.getFirst());
        boolean curse = EMEnchantmentHelper.isCurse(enchantment.getFirst());
        int color = curse ? (enabled ? 0xFFFF5555 : 0xFFAA4444) : (blocked ? 0xFF5E1D1D : (enabled ? titleTextColor() : 0xFF7F7F7F));
        Component name = enabled ? Enchantment.getFullname(enchantment.getFirst(), enchantment.getSecond()).copy().withStyle(ChatFormatting.RESET)
                : enchantment.getFirst().value().description().copy().withStyle(ChatFormatting.RESET);
        drawNameText(guiGraphics, y, name, color, 0.7F);
    }

    @Override
    protected ItemStack getRenderStack(Pair<Holder<Enchantment>, Integer> enchantment) {
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchantments.set(enchantment.getFirst(), Math.max(1, enchantment.getSecond()));
        book.set(DataComponents.STORED_ENCHANTMENTS, enchantments.toImmutable());
        return book;
    }

    @Override
    public void renderForeground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        recheckItem();
        super.renderForeground(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateTooltip(int mouseX, int mouseY) {
    }
}
