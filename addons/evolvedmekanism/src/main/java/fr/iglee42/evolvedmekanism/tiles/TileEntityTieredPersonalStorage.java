package fr.iglee42.evolvedmekanism.tiles;

import fr.iglee42.evolvedmekanism.blocks.BlockTieredPersonalStorage;
import fr.iglee42.evolvedmekanism.inventory.personalstorage.AbstractTieredPersonalStorageItemInventory;
import fr.iglee42.evolvedmekanism.inventory.personalstorage.TieredPersonalStorageManager;
import fr.iglee42.evolvedmekanism.tiers.PersonalStorageTier;
import java.util.List;
import java.util.UUID;
import java.util.function.BiPredicate;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.security.ISecurityUtils;
import mekanism.api.security.SecurityMode;
import mekanism.common.capabilities.holder.container.IContainerHolder;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
import mekanism.common.registries.MekanismDataComponents;
import mekanism.common.tile.TileEntityPersonalStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.item.ItemResource;

public abstract class TileEntityTieredPersonalStorage extends TileEntityPersonalStorage {

    private final PersonalStorageTier tier;

    protected TileEntityTieredPersonalStorage(Holder<Block> blockProvider, BlockPos pos, BlockState state, PersonalStorageTier tier) {
        super(blockProvider, pos, state);
        this.tier = tier;
    }

    public PersonalStorageTier getTier() {
        return tier;
    }

    @Override
    protected IContainerHolder<IInventorySlot> getInitialInventory(IContentsListener listener) {
        MekContainerHelper<IInventorySlot> builder = MekContainerHelper.forSide(facingSupplier);
        BiPredicate<ItemResource, AutomationType> canInteract = (_, automationType) ->
              automationType.isManual() || level != null && ISecurityUtils.INSTANCE.getEffectiveSecurityMode(this, level.isClientSide()) == SecurityMode.PUBLIC;
        BlockTieredPersonalStorage<?, ?> block = (BlockTieredPersonalStorage<?, ?>) getBlockState().getBlock();
        TieredPersonalStorageManager.createSlots(builder::addContainer, canInteract, listener, block.getTier());
        return builder.build();
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter input) {
        //Note: We intentionally skip the base personal storage handling as it assumes the basic (6x9) inventory layout
        if (level != null && !level.isClientSide()) {
            UUID owner = input.get(MekanismDataComponents.OWNER);
            if (owner != null) {
                AbstractTieredPersonalStorageItemInventory storageItemInventory = TieredPersonalStorageManager.getInventoryForUnchecked(
                      getTier(), input.get(MekanismDataComponents.PERSONAL_STORAGE_ID), owner).orElse(null);
                if (storageItemInventory != null) {
                    List<IInventorySlot> inventorySlots = storageItemInventory.getContainers();
                    List<IInventorySlot> tileSlots = getInventorySlots();
                    int size = Math.min(tileSlots.size(), inventorySlots.size());
                    for (int i = 0; i < size; i++) {
                        tileSlots.get(i).copyContents(inventorySlots.get(i), null);
                    }
                }
            }
        }
    }
}
