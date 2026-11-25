package fr.iglee42.evolvedmekanism.items;

import fr.iglee42.evolvedmekanism.blocks.BlockTieredPersonalStorage;
import fr.iglee42.evolvedmekanism.inventory.personalstorage.AbstractTieredPersonalStorageItemInventory;
import fr.iglee42.evolvedmekanism.inventory.personalstorage.TieredPersonalStorageItemContainer;
import fr.iglee42.evolvedmekanism.inventory.personalstorage.TieredPersonalStorageManager;
import fr.iglee42.evolvedmekanism.registries.EMContainerTypes;
import fr.iglee42.evolvedmekanism.tiers.PersonalStorageTier;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.resource.LargeResourceStack;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import java.util.ArrayList;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.item.interfaces.IDroppableContents;
import mekanism.common.item.interfaces.IGuiItem;
import mekanism.common.lib.security.ItemSecurityUtils;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.List;

public class ItemBlockTieredPersonalStorage extends ItemBlockTooltip implements IDroppableContents, IGuiItem {

    private final Identifier openStat;
    private final PersonalStorageTier tier;

    public ItemBlockTieredPersonalStorage(BlockTieredPersonalStorage<?, ?> block, Item.Properties properties, Identifier openStat, PersonalStorageTier tier) {
        super(block, properties);
        this.openStat = openStat;
        this.tier = tier;
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        return ItemSecurityUtils.get().claimOrOpenGui(world, player, hand, (p, h, itemAccess, transaction) -> {
            if (!world.isClientSide()) {
                TieredPersonalStorageManager.getInventoryFor(p.getItemInHand(h));
            }
            getContainerType().tryOpenGui(p, h, itemAccess);
            p.awardStat(Stats.CUSTOM.get(openStat));
        });
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        //Like super.useOn, except we validate the player is not null and pass to use regardless (the storage is never consumable)
        InteractionResult result = place(new BlockPlaceContext(context));
        Player player = context.getPlayer();
        return result.consumesAction() || player == null ? result : use(context.getLevel(), player, context.getHand());
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        Player player = context.getPlayer();
        //Only allow placing if there is no player, it is a fake player, or the player is sneaking
        return (player == null || player.isFakePlayer() || player.isShiftKeyDown()) && super.canPlace(context, state);
    }

    @Override
    public ContainerTypeRegistryObject<TieredPersonalStorageItemContainer> getContainerType() {
        return EMContainerTypes.TIERED_PERSONAL_STORAGE_ITEM;
    }

    @Override
    public void onDestroyed(ItemEntity item, DamageSource damageSource) {
        super.onDestroyed(item, damageSource);
        if (!item.level().isClientSide()) {
            ItemStack stack = item.getItem();
            AbstractTieredPersonalStorageItemInventory inventory = TieredPersonalStorageManager.getInventoryIfPresent(stack).orElse(null);
            if (inventory != null && inventory.isInventoryEmpty()) {
                //If the inventory was actually empty we can prune the data from the storage manager
                // (if it isn't empty we want to persist it so that server admins can recover their items)
                TieredPersonalStorageManager.deleteInventory(stack);
            }
        }
    }

    @Override
    public List<LargeResourceStack<ItemResource>> getDroppedSlots(ItemAccess itemAccess, TransactionContext transaction) {
        ItemStack stack = itemAccess.getResource().toStack(1);
        List<LargeResourceStack<ItemResource>> contents = new ArrayList<>();
        TieredPersonalStorageManager.getInventoryIfPresent(stack).ifPresent(inventory -> {
            for (IInventorySlot slot : inventory.getInventorySlots(null)) {
                LargeResourceStack<ItemResource> slotStack = slot.asStack();
                if (!slotStack.isEmpty()) {
                    contents.add(slotStack);
                }
            }
        });
        return contents;
    }

    @Override
    public int getScalar(ItemAccess itemAccess) {
        return 1;
    }

    public PersonalStorageTier getTier() {
        return tier;
    }
}
