package fr.iglee42.evolvedmekanism.inventory.personalstorage;

import fr.iglee42.evolvedmekanism.tiers.PersonalStorageTier;
import java.util.ArrayList;
import java.util.List;
import mekanism.api.IContentsListener;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.resource.IMekanismResourceHandler;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public abstract class AbstractTieredPersonalStorageItemInventory implements IMekanismResourceHandler<ItemResource, IInventorySlot>, IContentsListener {

    protected final List<IInventorySlot> slots;
    protected final PersonalStorageTier tier;

    public AbstractTieredPersonalStorageItemInventory(PersonalStorageTier tier) {
        this.tier = tier;
        this.slots = Util.make(new ArrayList<>(), lst -> TieredPersonalStorageManager.createSlots(lst::add, ConstantPredicates.alwaysTrueBi(), this, tier));
    }

    @Override
    public List<IInventorySlot> getContainers() {
        return slots;
    }

    /**
     * Compatibility helper for callers that used the old side-aware inventory API.
     */
    public List<IInventorySlot> getInventorySlots(@Nullable Direction side) {
        return slots;
    }

    public boolean isInventoryEmpty() {
        for (IInventorySlot slot : slots) {
            if (!slot.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compatibility helper to set the contents of a slot from an {@link ItemStack}.
     */
    public void setStackInSlot(int index, ItemStack stack) {
        slots.get(index).setContents(ItemResource.of(stack), stack.getCount(), null);
    }

    public PersonalStorageTier getTier() {
        return tier;
    }
}
