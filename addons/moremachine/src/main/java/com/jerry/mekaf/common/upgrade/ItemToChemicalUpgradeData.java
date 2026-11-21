package com.jerry.mekaf.common.upgrade;

import mekanism.common.recipe.lookup.monitor.ProgressRecipeCacheLookupMonitor;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.interfaces.IRedstoneControl;
import mekanism.common.upgrade.IUpgradeData;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter.PathElement;

import java.util.Collections;
import java.util.List;

public class ItemToChemicalUpgradeData implements IUpgradeData {

    public final boolean redstone;
    public final IRedstoneControl.RedstoneControl controlType;
    public final IEnergyContainer energyContainer;
    public final ProgressRecipeCacheLookupMonitor<?>[] cacheLookupMonitors;
    public final boolean sorting;
    public final EnergyInventorySlot energySlot;
    public final List<IInventorySlot> inputSlots;
    public final List<IChemicalTank> outputTanks;
    public final CompoundTag components;

    public ItemToChemicalUpgradeData(HolderLookup.Provider provider, boolean redstone, IRedstoneControl.RedstoneControl controlType,
                                     IEnergyContainer energyContainer, ProgressRecipeCacheLookupMonitor<?> cacheLookupMonitor, EnergyInventorySlot energySlot,
                                     InputInventorySlot inputSlot, IChemicalTank outputTank, List<ITileComponent> components, PathElement problemPath) {
        this(provider, redstone, controlType, energyContainer, new ProgressRecipeCacheLookupMonitor<?>[] { cacheLookupMonitor }, energySlot, Collections.singletonList(inputSlot),
                Collections.singletonList(outputTank), false, components, problemPath);
    }

    public ItemToChemicalUpgradeData(HolderLookup.Provider provider, boolean redstone, IRedstoneControl.RedstoneControl controlType,
                                     IEnergyContainer energyContainer, ProgressRecipeCacheLookupMonitor<?>[] cacheLookupMonitors, EnergyInventorySlot energySlot,
                                     List<IInventorySlot> inputSlots, List<IChemicalTank> outputTanks, boolean sorting, List<ITileComponent> components, PathElement problemPath) {
        this.redstone = redstone;
        this.controlType = controlType;
        this.energyContainer = energyContainer;
        this.cacheLookupMonitors = cacheLookupMonitors;
        this.energySlot = energySlot;
        this.inputSlots = inputSlots;
        this.outputTanks = outputTanks;
        this.sorting = sorting;
        this.components = IUpgradeData.readComponents(provider, components, problemPath);
    }
}
