package fr.iglee42.evolvedmekanism.tiles.enchantment;

import fr.iglee42.evolvedmekanism.registries.EMBlocks;
import fr.iglee42.evolvedmekanism.registries.EMChemicals;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.SerializationConstants;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.resource.IMekanismResourceHandler;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.energy.VariableCapacityEnergyContainer;
import mekanism.common.capabilities.holder.container.IContainerHolder;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
import mekanism.common.capabilities.holder.single.ISingleContainerHolder;
import mekanism.common.component.containers.type.ContainerType;
import mekanism.common.component.containers.type.IContainerType;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.container.sync.SyncableEnum;
import mekanism.common.inventory.slot.ChemicalInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.registries.MekanismDataComponents;
import mekanism.common.tile.interfaces.IHasMode;
import mekanism.common.tile.laser.TileEntityLaserAmplifier;
import mekanism.common.tile.laser.TileEntityLaserReceptor;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.WorldUtils;
import mekanism.common.util.WorldUtils.DropsWithXp;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Redstone;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.UnknownNullability;

public class TileEntityLaserDisenchanter extends TileEntityLaserReceptor implements IHasMode {

    public static final long MAX_GAS = 10000;

    @UnknownNullability//Initialized via getInitialChemicalTanks
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.class, methodNames = {"getChemical", "getChemicalCapacity", "getChemicalNeeded",
            "getChemicalFilledPercentage"}, docPlaceholder = "chemical")
    public IChemicalTank chemicalTank;

    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getGasItemInput", docPlaceholder = "gas item input slot")
    ChemicalInventorySlot gasInputSlot;
    @UnknownNullability//Initialized via getInitialInventory
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getGasItemOutput", docPlaceholder = "gas item output slot")
    ChemicalInventorySlot gasOutputSlot;

    private boolean emittingRedstone;
    private TileEntityLaserAmplifier.RedstoneOutput outputMode = TileEntityLaserAmplifier.RedstoneOutput.OFF;


    public TileEntityLaserDisenchanter(BlockPos pos, BlockState state) {
        super(EMBlocks.LASER_DISENCHANTER, pos, state);
    }

    @Override
    protected ISingleContainerHolder<IEnergyContainer> getInitialEnergyContainer(IContentsListener listener) {
        energyContainer = VariableCapacityEnergyContainer.create(MachineEnergyContainer.validateBlock(this)::getStorage, BasicEnergyContainer.notExternal,
              ConstantPredicates.alwaysTrue(), listener);
        return _ -> energyContainer;
    }

    @Override
    public IContainerHolder<IChemicalTank> getInitialChemicalTanks(IContentsListener listener) {
        MekContainerHelper<IChemicalTank> builder = MekContainerHelper.forSide(facingSupplier);
        builder.addContainer(chemicalTank = BasicChemicalTank.create(MAX_GAS, chemical -> chemical.typeHolder().is(EMChemicals.CRYONOCTIS), listener));
        return builder.build();
    }

    @Override
    protected IContainerHolder<IInventorySlot> getInitialInventory(IContentsListener listener) {
        MekContainerHelper<IInventorySlot> builder = MekContainerHelper.forSide(facingSupplier);

        for (int slotX = 0; slotX < 6; slotX++) {
            for (int slotY = 0; slotY < 2; slotY++) {
                OutputInventorySlot slot = OutputInventorySlot.at(listener, 43 + slotX * 18, 30 + slotY * 18);
                builder.addContainer(slot);
                slot.setSlotType(ContainerSlotType.NORMAL);
            }
        }

        builder.addContainer(gasInputSlot = ChemicalInventorySlot.drain(chemicalTank, listener, 16, 16));
        builder.addContainer(gasOutputSlot = ChemicalInventorySlot.fill(chemicalTank, listener, 16, 48));
        gasInputSlot.setSlotType(ContainerSlotType.INPUT);
        gasInputSlot.setSlotOverlay(SlotOverlay.PLUS);
        gasOutputSlot.setSlotType(ContainerSlotType.OUTPUT);
        gasOutputSlot.setSlotOverlay(SlotOverlay.MINUS);
        return builder.build();
    }

    @Override
    protected void handleBreakBlock(BlockState state, ServerLevel level, BlockPos hitPos, Player player, ItemStack tool, TransactionContext transaction) {
        DropsWithXp<ItemStack> dropsWithXp = WorldUtils.getDrops(state, level, hitPos, WorldUtils.getTileEntity(level, hitPos), player, tool);
        List<ItemStack> drops = dropsWithXp.items();
        Mekanism.worldTickHandler.fallbackItemCollector = drops::add;
        breakBlock(state, level, hitPos, tool, dropsWithXp.experience());
        Mekanism.worldTickHandler.fallbackItemCollector = null;

        for (int i = 0; i < drops.size(); i++) {
            ItemStack stack = drops.get(i);
            if (stack.isEnchanted()) {
                ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
                for (Object2IntMap.Entry<Holder<Enchantment>> enchantment : enchantments.entrySet()) {
                    ItemEnchantments.Mutable bookEnchants = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
                    bookEnchants.set(enchantment.getKey(), enchantment.getIntValue());
                    int cost = getEnchantmentXp(enchantment) * 100;
                    if (drops.stream().anyMatch(it -> it.is(Items.BOOK)) && chemicalTank.amountAsLong() >= cost
                        && chemicalTank.extract(chemicalTank.resource(), cost, transaction, AutomationType.INTERNAL) == cost) {
                        drops.stream().filter(it -> it.is(Items.BOOK)).findFirst().ifPresent(it -> it.shrink(1));
                        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                        EnchantmentHelper.setEnchantments(book, bookEnchants.toImmutable());
                        drops.addLast(book);
                        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchantments);
                        mutable.removeIf(h -> h.equals(enchantment.getKey()));
                        enchantments = mutable.toImmutable();
                        EnchantmentHelper.setEnchantments(stack, enchantments);
                    }
                }
            }
        }

        if (!drops.isEmpty()) {
            BlockPos dropPos = null;
            Direction opposite = null;
            IMekanismResourceHandler<ItemResource, IInventorySlot> inventoryHandler = this::getInventorySlots;
            for (ItemStack drop : drops) {
                if (drop.isEmpty()) {
                    continue;
                }
                int toInsert = drop.count();
                int inserted = inventoryHandler.insert(ItemResource.of(drop), toInsert, transaction, AutomationType.INTERNAL);
                if (inserted < toInsert) {
                    if (dropPos == null) {
                        Direction direction = getDirection();
                        dropPos = worldPosition.relative(direction, 2);
                        opposite = direction.getOpposite();
                    }
                    Block.popResourceFromFace(level, dropPos, opposite, drop.copyWithCount(toInsert - inserted));
                }
            }
        }
    }

    @Override
    protected boolean handleHitItem(ItemEntity entity, TransactionContext transaction) {
        ItemStack stack = entity.getItem();
        IMekanismResourceHandler<ItemResource, IInventorySlot> inventoryHandler = this::getInventorySlots;
        int inserted = inventoryHandler.insert(ItemResource.of(stack), stack.count(), transaction, AutomationType.INTERNAL);
        if (inserted == stack.count()) {
            entity.discard();
            return true;
        }
        stack.shrink(inserted);
        return super.handleHitItem(entity, transaction);
    }

    @Override
    protected boolean onUpdateServer(ServerLevel level) {
        gasInputSlot.drainTankIntoSlot(null);
        gasOutputSlot.fillTankFromSlot(null);
        setEmittingRedstone(false);

        boolean sendUpdatePacket = super.onUpdateServer(level);
        if (outputMode != TileEntityLaserAmplifier.RedstoneOutput.ENTITY_DETECTION) {
            setEmittingRedstone(false);
        }
        return sendUpdatePacket;
    }

    @Override
    protected void setEmittingRedstone(boolean foundEntity) {
        this.emittingRedstone = foundEntity;
    }


    @Override
    protected int toFire() {
        return canFunction() ? super.toFire() : 0;
    }


    @Override
    public int getRedstoneLevel() {
        if (outputMode == TileEntityLaserAmplifier.RedstoneOutput.ENERGY_CONTENTS) {
            return MekanismUtils.redstoneLevelFromContents(energyContainer);
        }
        return emittingRedstone ? Redstone.SIGNAL_MAX : Redstone.SIGNAL_NONE;
    }

    @Override
    protected boolean makesComparatorDirty(IContainerType<?, ?> type) {
        return type == ContainerType.ENERGY;
    }

    private int getEnchantmentXp(Object2IntMap.Entry<Holder<Enchantment>> enchant) {
        Holder<Enchantment> enchantment = enchant.getKey();
        int integer = enchant.getIntValue();
        return enchantment.value().getMinCost(integer);
    }

    public IChemicalTank getChemicalTank() {
        return chemicalTank;
    }

    @Override
    public void nextMode() {
        outputMode = outputMode.getNext();
        setChanged();
    }

    @Override
    public void previousMode() {
        outputMode = outputMode.getPrevious();
        setChanged();
    }

    @Override
    public void readSustainedData(ValueInput input) {
        super.readSustainedData(input);
        outputMode = input.read(SerializationConstants.OUTPUT_MODE, TileEntityLaserAmplifier.RedstoneOutput.CODEC).orElse(TileEntityLaserAmplifier.RedstoneOutput.OFF);
    }

    @Override
    public void writeSustainedData(ValueOutput output) {
        super.writeSustainedData(output);
        output.store(SerializationConstants.OUTPUT_MODE, TileEntityLaserAmplifier.RedstoneOutput.CODEC, outputMode);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter input) {
        super.applyImplicitComponents(input);
        outputMode = input.getOrDefault(MekanismDataComponents.REDSTONE_OUTPUT, outputMode);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(MekanismDataComponents.REDSTONE_OUTPUT, outputMode);
    }

    @Override
    public boolean supportsMode(RedstoneControl mode) {
        return true;
    }

    @ComputerMethod(nameOverride = "getRedstoneOutputMode")
    public TileEntityLaserAmplifier.RedstoneOutput getOutputMode() {
        return outputMode;
    }


    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableEnum.create(TileEntityLaserAmplifier.RedstoneOutput.BY_ID, TileEntityLaserAmplifier.RedstoneOutput.OFF, this::getOutputMode, value -> outputMode = value));
    }


}
