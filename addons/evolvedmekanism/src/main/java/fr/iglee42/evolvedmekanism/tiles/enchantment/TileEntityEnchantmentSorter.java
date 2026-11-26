package fr.iglee42.evolvedmekanism.tiles.enchantment;

import fr.iglee42.evolvedmekanism.registries.EMBlocks;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.capabilities.holder.container.IContainerHolder;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableBoolean;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.UnknownNullability;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityEnchantmentSorter extends TileEntityConfigurableMachine {

    public boolean didProcess;

    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getInputItem", docPlaceholder = "input slot")
    InputInventorySlot inputSlot;
    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getOutputItem1", docPlaceholder = "output slot 1")
    OutputInventorySlot outputSlot1;
    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getOutputItem2", docPlaceholder = "output slot 2")
    OutputInventorySlot outputSlot2;

    public TileEntityEnchantmentSorter(BlockPos pos, BlockState state) {
        super(EMBlocks.SORTIFICHANTOR, pos, state);
        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.INPUT, new InventorySlotInfo(true, false,inputSlot));
            itemConfig.addSlotInfo(DataType.OUTPUT_1, new InventorySlotInfo(false, true,outputSlot1));
            itemConfig.addSlotInfo(DataType.OUTPUT_2, new InventorySlotInfo(false, true,outputSlot2));
        }

        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM);
    }

    @Override
    protected IContainerHolder<IInventorySlot> getInitialInventory(IContentsListener listener) {
        MekContainerHelper<IInventorySlot> builder = MekContainerHelper.forSideWithItemConfig(this);
        builder.addContainer(inputSlot = InputInventorySlot.at(listener, 40, 35));
        builder.addContainer(outputSlot1 = OutputInventorySlot.at(listener, 100, 35));
        builder.addContainer(outputSlot2 = OutputInventorySlot.at(listener, 120, 35));
        return builder.build();
    }

    @Override
    protected boolean onUpdateServer(ServerLevel level) {
        boolean sendUpdatePacket = super.onUpdateServer(level);
        didProcess = false;
        if (canFunction() && !inputSlot.isEmpty()) {
            ItemResource inputType = inputSlot.resource();
            ItemStack result = inputType.toStack(1);
            OutputInventorySlot outputSlot = result.isEnchanted() || !result.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).isEmpty() ? outputSlot1 : outputSlot2;
            try (Transaction transaction = Transaction.openRoot()) {
                int extracted = inputSlot.extract(inputType, 1, transaction, AutomationType.INTERNAL);
                if (extracted == 1 && outputSlot.insert(inputType, 1, transaction, AutomationType.INTERNAL) == 1) {
                    transaction.commit();
                    didProcess = true;
                }
            }
        }
        return sendUpdatePacket;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableBoolean.create(() -> didProcess, value -> didProcess = value));
    }

}
