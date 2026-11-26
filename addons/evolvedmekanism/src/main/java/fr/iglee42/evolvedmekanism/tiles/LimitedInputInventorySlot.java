package fr.iglee42.evolvedmekanism.tiles;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.functions.ConstantPredicates;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.slot.BasicInventorySlot;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class LimitedInputInventorySlot extends BasicInventorySlot {

    public static LimitedInputInventorySlot at(@Nullable IContentsListener listener, int x, int y) {
        return at(ConstantPredicates.alwaysTrue(), listener, x, y);
    }

    public static LimitedInputInventorySlot at(Predicate<ItemResource> isItemValid, @Nullable IContentsListener listener, int x, int y) {
        return at(ConstantPredicates.alwaysTrueBi(), isItemValid, listener, x, y);
    }

    public static LimitedInputInventorySlot at(BiPredicate<ItemResource, AutomationType> insertPredicate, Predicate<ItemResource> isItemValid, @Nullable IContentsListener listener,
          int x, int y) {
        return at(Item.ABSOLUTE_MAX_STACK_SIZE, insertPredicate, isItemValid, listener, x, y);
    }

    public static LimitedInputInventorySlot at(int limit, @Nullable IContentsListener listener, int x, int y) {
        return at(limit, ConstantPredicates.alwaysTrue(), listener, x, y);
    }

    public static LimitedInputInventorySlot at(int limit, Predicate<ItemResource> isItemValid, @Nullable IContentsListener listener, int x, int y) {
        return at(limit, ConstantPredicates.alwaysTrueBi(), isItemValid, listener, x, y);
    }

    public static LimitedInputInventorySlot at(int limit, BiPredicate<ItemResource, AutomationType> insertPredicate, Predicate<ItemResource> isItemValid,
          @Nullable IContentsListener listener, int x, int y) {
        Objects.requireNonNull(insertPredicate, "Insertion check cannot be null");
        Objects.requireNonNull(isItemValid, "Item validity check cannot be null");
        return new LimitedInputInventorySlot(limit, insertPredicate, isItemValid, listener, x, y);
    }

    protected LimitedInputInventorySlot(int limit, BiPredicate<ItemResource, AutomationType> insertPredicate, Predicate<ItemResource> isItemValid,
          @Nullable IContentsListener listener, int x, int y) {
        super(limit, ConstantPredicates.notExternal(), insertPredicate, isItemValid, null, null, listener, x, y);
        obeyStackLimit = false;
        setSlotType(ContainerSlotType.INPUT);
    }
}
