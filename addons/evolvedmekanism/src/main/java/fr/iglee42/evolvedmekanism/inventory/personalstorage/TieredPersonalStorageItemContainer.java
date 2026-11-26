package fr.iglee42.evolvedmekanism.inventory.personalstorage;

import fr.iglee42.evolvedmekanism.items.ItemBlockTieredPersonalStorage;
import fr.iglee42.evolvedmekanism.registries.EMContainerTypes;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.container.item.MekanismItemContainer;
import mekanism.common.inventory.container.slot.HotBarSlot;
import mekanism.common.util.ItemAccessUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class TieredPersonalStorageItemContainer extends MekanismItemContainer {

    private final AbstractTieredPersonalStorageItemInventory itemInventory;

    public TieredPersonalStorageItemContainer(int id, Inventory inv, InteractionHand hand, ItemAccess itemAccess, boolean isClientSide) {
        //We have to initialize this before actually adding the slots
        itemInventory = isClientSide ? new ClientSideTieredPersonalStorageInventory(getTier(itemAccess))
                                     : TieredPersonalStorageManager.getInventoryFor(ItemAccessUtils.asStack(itemAccess))
                                           .orElseThrow(() -> new IllegalStateException("Inventory not available"));
        super(EMContainerTypes.TIERED_PERSONAL_STORAGE_ITEM, id, inv, hand, itemAccess);
    }

    private static fr.iglee42.evolvedmekanism.tiers.PersonalStorageTier getTier(ItemAccess itemAccess) {
        return ((ItemBlockTieredPersonalStorage) itemAccess.getResource().getItem()).getTier();
    }

    @Override
    protected boolean isValidType(ItemResource itemType) {
        return super.isValidType(itemType) && itemType.getItem() instanceof ItemBlockTieredPersonalStorage;
    }

    @Override
    protected void addSlots(Player player) {
        super.addSlots(player);
        //Get all the inventory slots the item has
        for (IInventorySlot inventorySlot : itemInventory.getContainers()) {
            Slot containerSlot = inventorySlot.createContainerSlot(player);
            if (containerSlot != null) {
                addSlot(containerSlot);
            }
        }
    }

    public InteractionHand getHand() {
        return hand;
    }

    @Override
    protected int getInventoryYOffset() {
        int moreRows = getTier(itemAccess).rows - 6;
        return 140 + moreRows * 18;
    }

    @Override
    protected int getInventoryXOffset() {
        return super.getInventoryXOffset() + (getTier(itemAccess).columns - 9) / 2 * 18;
    }

    @Override
    protected HotBarSlot createHotBarSlot(Inventory inv, int index, int x, int y) {
        // special handling to prevent removing the personal chest from the player's inventory slot
        if (index == inv.getSelectedSlot() && hand == InteractionHand.MAIN_HAND) {
            return new HotBarSlot(inv, index, x, y) {
                @Override
                public boolean mayPickup(Player player) {
                    return false;
                }
            };
        }
        return super.createHotBarSlot(inv, index, x, y);
    }

    @Override
    public void clicked(int slotId, int dragType, ContainerInput containerInput, Player player) {
        if (containerInput == ContainerInput.SWAP) {
            if (hand == InteractionHand.OFF_HAND && dragType == Inventory.SLOT_OFFHAND) {
                //Block pressing f to swap it when it is in the offhand
                return;
            } else if (hand == InteractionHand.MAIN_HAND && Inventory.isHotbarSlot(dragType)) {
                //Block taking out of the selected slot (we don't validate we have a hotbar slot as we always should for this container)
                if (!hotBarSlots.get(dragType).mayPickup(player)) {
                    return;
                }
            }
        }
        super.clicked(slotId, dragType, containerInput, player);
    }

    public ItemStack getStack() {
        return ItemAccessUtils.asStack(itemAccess);
    }
}
