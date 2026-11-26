package fr.iglee42.evolvedmekanism.inventory.personalstorage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import fr.iglee42.evolvedmekanism.tiers.PersonalStorageTier;
import java.util.ArrayList;
import java.util.List;
import mekanism.api.IContentsListener;
import mekanism.api.SerializationConstants;
import mekanism.api.inventory.IInventorySlot;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Inventory for Personal Storages when an item. Handled by the Block when placed in world.
 */
@NullMarked
public class TieredPersonalStorageItemInventory extends AbstractTieredPersonalStorageItemInventory {

    private static final String TIER = "tier";

    public static final Codec<TieredPersonalStorageItemInventory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
          Codec.INT.fieldOf(TIER).forGetter(inv -> inv.getTier().ordinal()),
          SlotData.CODEC.listOf().promotePartial(err -> EvolvedMekanism.logger.error("Failed to parse some Tiered Personal Storage slots: {}", err))
                .fieldOf(SerializationConstants.DATA).forGetter(TieredPersonalStorageItemInventory::toSlotData)
    ).apply(instance, TieredPersonalStorageItemInventory::new));

    @Nullable
    private IContentsListener parent;

    private TieredPersonalStorageItemInventory(int tierOrdinal, List<SlotData> loadedData) {
        super(PersonalStorageTier.values()[tierOrdinal]);
        this.parent = null;
        for (SlotData slotData : loadedData) {
            if (slotData.slot() >= 0 && slotData.slot() < slots.size()) {
                IInventorySlot slot = slots.get(slotData.slot());
                slot.setContents(slotData.itemType(), slotData.amount(), null);
            }
        }
    }

    public TieredPersonalStorageItemInventory(PersonalStorageTier tier, @Nullable IContentsListener parent) {
        super(tier);
        this.parent = parent;
    }

    protected void setParent(@Nullable IContentsListener newParent) {
        this.parent = newParent;
    }

    private List<SlotData> toSlotData() {
        List<SlotData> out = new ArrayList<>();
        for (int i = 0; i < slots.size(); i++) {
            IInventorySlot slot = slots.get(i);
            if (!slot.isEmpty()) {
                out.add(new SlotData(i, slot.resource(), slot.amountAsLong()));
            }
        }
        return out;
    }

    @Override
    public void onContentsChanged() {
        if (parent != null) {
            parent.onContentsChanged();
        }
    }

    record SlotData(int slot, ItemResource itemType, long amount) {

        static final Codec<SlotData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
              Codec.INT.fieldOf(SerializationConstants.SLOT).forGetter(SlotData::slot),
              ItemResource.OPTIONAL_CODEC.fieldOf(SerializationConstants.TYPE).forGetter(SlotData::itemType),
              ExtraCodecs.NON_NEGATIVE_LONG.fieldOf(SerializationConstants.AMOUNT).forGetter(SlotData::amount)
        ).apply(instance, SlotData::new));
    }
}
