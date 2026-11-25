package fr.iglee42.evolvedmekanism.inventory.personalstorage;

import fr.iglee42.evolvedmekanism.tiers.PersonalStorageTier;
import mekanism.api.IContentsListener;
import org.jspecify.annotations.NullMarked;

/**
 * Inventory for Personal Storages when an item. Handled by the Block when placed in world.
 */
@NullMarked
public class TieredPersonalStorageItemInventory extends AbstractTieredPersonalStorageItemInventory {

    private final IContentsListener parent;

    public TieredPersonalStorageItemInventory(PersonalStorageTier tier, IContentsListener parent) {
        super(tier);
        this.parent = parent;
    }

    @Override
    public void onContentsChanged() {
        parent.onContentsChanged();
    }
}
