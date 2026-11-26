package fr.iglee42.evolvedmekanism.inventory.personalstorage;

import com.mojang.serialization.Codec;
import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import fr.iglee42.evolvedmekanism.tiers.PersonalStorageTier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import mekanism.api.IContentsListener;
import mekanism.api.inventory.IInventorySlot;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jspecify.annotations.NullMarked;

@NullMarked
class TieredPersonalStorageData extends SavedData {

    private static final Codec<Map<UUID, TieredPersonalStorageItemInventory>> MAP_CODEC = Codec.unboundedMap(
          UUIDUtil.STRING_CODEC,
          TieredPersonalStorageItemInventory.CODEC
    ).promotePartial(error -> EvolvedMekanism.logger.error("Some Tiered Personal Storage items failed: {}", error));

    static final Codec<TieredPersonalStorageData> CODEC = MAP_CODEC.xmap(TieredPersonalStorageData::new, data -> data.inventoriesById);

    private final Map<UUID, TieredPersonalStorageItemInventory> inventoriesById = new HashMap<>();
    private final IContentsListener contentsListener = this::setDirty;

    TieredPersonalStorageData() {
    }

    private TieredPersonalStorageData(Map<UUID, TieredPersonalStorageItemInventory> loadedMap) {
        for (Map.Entry<UUID, TieredPersonalStorageItemInventory> entry : loadedMap.entrySet()) {
            TieredPersonalStorageItemInventory value = entry.getValue();
            value.setParent(contentsListener);
            inventoriesById.put(entry.getKey(), value);
        }
    }

    TieredPersonalStorageItemInventory getOrAddInventory(UUID id, PersonalStorageTier tier) {
        return inventoriesById.computeIfAbsent(id, unused -> createInventory(tier));
    }

    TieredPersonalStorageItemInventory addInventory(UUID id, List<IInventorySlot> contents, PersonalStorageTier tier) {
        TieredPersonalStorageItemInventory inventory = inventoriesById.get(id);
        if (inventory == null) {
            inventory = createInventory(tier);
            inventoriesById.put(id, inventory);
            List<IInventorySlot> inventorySlots = inventory.getContainers();
            for (int i = 0, slots = Math.min(contents.size(), inventorySlots.size()); i < slots; i++) {
                inventorySlots.get(i).copyContents(contents.get(i), null);
            }
            setDirty();
        }
        return inventory;
    }

    void removeInventory(UUID id) {
        if (this.inventoriesById.remove(id) != null) {
            setDirty();
        }
    }

    private TieredPersonalStorageItemInventory createInventory(PersonalStorageTier tier) {
        return new TieredPersonalStorageItemInventory(tier, contentsListener);
    }

    static SavedDataType<TieredPersonalStorageData> createType(UUID id) {
        return new SavedDataType<>(Identifier.fromNamespaceAndPath(EvolvedMekanism.MODID, "tiered_personal_storage/" + id), TieredPersonalStorageData::new, CODEC);
    }
}
