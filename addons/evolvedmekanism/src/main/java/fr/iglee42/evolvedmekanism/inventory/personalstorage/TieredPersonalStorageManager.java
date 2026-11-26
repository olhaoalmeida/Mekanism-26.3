package fr.iglee42.evolvedmekanism.inventory.personalstorage;

import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import fr.iglee42.evolvedmekanism.items.ItemBlockTieredPersonalStorage;
import fr.iglee42.evolvedmekanism.tiers.PersonalStorageTier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.security.IItemSecurityUtils;
import mekanism.common.Mekanism;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.registries.MekanismDataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.SavedDataStorage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@EventBusSubscriber(modid = EvolvedMekanism.MODID)
public class TieredPersonalStorageManager {

    @Nullable
    private static SavedDataStorage DATA_STORAGE;
    private static final Map<UUID, TieredPersonalStorageData> STORAGE_BY_PLAYER_UUID = new HashMap<>();

    private static Optional<TieredPersonalStorageData> forOwner(UUID playerUUID) {
        if (EffectiveSide.get().isClient() || DATA_STORAGE == null) {
            return Optional.empty();
        }
        SavedDataStorage storage = DATA_STORAGE;
        return Optional.of(STORAGE_BY_PLAYER_UUID.computeIfAbsent(playerUUID,
              uuid -> Objects.requireNonNull(storage.computeIfAbsent(TieredPersonalStorageData.createType(uuid)), "Failed to create data")));
    }

    @Nullable
    private static UUID getOwner(ItemStack stack) {
        return IItemSecurityUtils.INSTANCE.getOwnerUUID(ItemAccess.forStack(stack));
    }

    /**
     * Only call on the server. Gets or creates an inventory for the supplied stack
     *
     * @param stack Personal storage ItemStack (type not checked) - will be modified if it didn't have an inventory id
     *
     * @return the existing or new inventory
     */
    public static Optional<AbstractTieredPersonalStorageItemInventory> getInventoryFor(ItemStack stack) {
        UUID owner = getOwner(stack);
        if (owner == null) {
            Mekanism.logger.error("Storage inventory asked for but stack has no owner! {}", stack, new Exception());
            return Optional.empty();
        }
        return getInventoryFor(stack, owner);
    }

    /**
     * Only call on the server. Gets or creates an inventory for the supplied stack
     *
     * @param stack Personal storage ItemStack (type not checked) - will be modified if it didn't have an inventory id
     * @param owner The owner of the stack
     *
     * @return the existing or new inventory
     */
    public static Optional<AbstractTieredPersonalStorageItemInventory> getInventoryFor(ItemStack stack, UUID owner) {
        UUID invId = getInventoryId(stack);
        if (!(stack.getItem() instanceof ItemBlockTieredPersonalStorage item)) {
            throw new IllegalStateException("Item isn't ItemBlockTieredPersonalStorage");
        }
        return getInventoryForUnchecked(item.getTier(), invId, owner);
    }

    /**
     * Only call on the server. Gets an inventory for the supplied stack
     *
     * @param tier        Tier of the storage
     * @param inventoryId Personal storage inventory id
     * @param owner       The owner of the stack
     *
     * @return the existing or new inventory
     */
    public static Optional<AbstractTieredPersonalStorageItemInventory> getInventoryForUnchecked(PersonalStorageTier tier, @Nullable UUID inventoryId, UUID owner) {
        if (inventoryId == null) {
            return Optional.empty();
        }
        return forOwner(owner).map(data -> data.getOrAddInventory(inventoryId, tier));
    }

    public static boolean createInventoryFor(PersonalStorageTier tier, HolderLookup.Provider provider, ItemStack stack, List<IInventorySlot> contents) {
        UUID owner = getOwner(stack);
        if (owner == null || contents.size() != tier.getSlotCount()) {
            //No owner or wrong number of slots, something went wrong
            return false;
        }
        //Get a new inventory id
        UUID invId = getInventoryId(stack);
        forOwner(owner).ifPresent(data -> data.addInventory(invId, contents, tier));
        return true;
    }

    /**
     * Only call on the server
     * <p>
     * Version of {@link #getInventoryFor(ItemStack)} which will NOT create an inventory if none exists already.
     *
     * @param stack Personal storage ItemStack
     *
     * @return the existing inventory, or an empty optional if none exists in saved data
     */
    public static Optional<AbstractTieredPersonalStorageItemInventory> getInventoryIfPresent(ItemStack stack) {
        UUID owner = getOwner(stack);
        return owner != null && stack.has(MekanismDataComponents.PERSONAL_STORAGE_ID) ? getInventoryFor(stack, owner) : Optional.empty();
    }

    public static void deleteInventory(ItemStack stack) {
        UUID owner = getOwner(stack);
        if (owner != null) {
            UUID storageId = stack.remove(MekanismDataComponents.PERSONAL_STORAGE_ID);
            if (storageId != null) {
                //If there actually was an id stored then remove the corresponding inventory
                forOwner(owner).ifPresent(data -> data.removeInventory(storageId));
            }
        }
    }

    private static UUID getInventoryId(ItemStack stack) {
        UUID invId = stack.get(MekanismDataComponents.PERSONAL_STORAGE_ID);
        if (invId == null) {
            invId = UUID.randomUUID();
            stack.set(MekanismDataComponents.PERSONAL_STORAGE_ID, invId);
        }
        return invId;
    }

    public static void reset() {
        STORAGE_BY_PLAYER_UUID.clear();
    }

    public static void createSlots(Consumer<IInventorySlot> slotConsumer, BiPredicate<ItemResource, AutomationType> canInteract, @Nullable IContentsListener listener,
          PersonalStorageTier tier) {
        for (int slotY = 0; slotY < tier.rows; slotY++) {
            for (int slotX = 0; slotX < tier.columns; slotX++) {
                slotConsumer.accept(BasicInventorySlot.at(canInteract, canInteract, listener, 8 + slotX * 18, 18 + slotY * 18));
            }
        }
    }

    public static void transferFromBasic(mekanism.common.lib.inventory.personalstorage.AbstractPersonalStorageItemInventory oldInventory, ItemStack stack) {
        getInventoryFor(stack).ifPresent(inventory -> copySlots(oldInventory.getContainers(), inventory.getContainers()));
    }

    public static void transferToNew(ItemStack oldItem, ItemStack stack) {
        getInventoryFor(oldItem).ifPresent(oldInventory -> getInventoryFor(stack).ifPresent(inventory -> copySlots(oldInventory.getContainers(), inventory.getContainers())));
    }

    private static void copySlots(List<IInventorySlot> from, List<IInventorySlot> to) {
        for (int i = 0, size = Math.min(from.size(), to.size()); i < size; i++) {
            to.get(i).copyContents(from.get(i), null);
        }
    }

    @SubscribeEvent
    public static void serverStarted(ServerStartedEvent event) {
        DATA_STORAGE = event.getServer().getDataStorage();
    }

    @SubscribeEvent
    static void serverStopped(ServerStoppedEvent ignored) {
        DATA_STORAGE = null;
        reset();
    }
}
