package com.jerry.mekaf.common.upgrade;

import mekanism.common.recipe.lookup.monitor.ProgressRecipeCacheLookupMonitor;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.interfaces.IRedstoneControl;
import mekanism.common.upgrade.IUpgradeData;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter.PathElement;

import java.util.Collections;
import java.util.List;

public class ChemicalToChemicalUpgradeData implements IUpgradeData {

    public final boolean redstone;
    public final IRedstoneControl.RedstoneControl controlType;
    public final IEnergyContainer energyContainer;
    public final ProgressRecipeCacheLookupMonitor<?>[] cacheLookupMonitors;
    public final boolean sorting;
    public final EnergyInventorySlot energySlot;
    public final List<IChemicalTank> inputTanks;
    public final List<IChemicalTank> outputTanks;
    public final CompoundTag components;

    public ChemicalToChemicalUpgradeData(HolderLookup.Provider provider, boolean redstone, IRedstoneControl.RedstoneControl controlType,
                                         IEnergyContainer energyContainer, ProgressRecipeCacheLookupMonitor<?> cacheLookupMonitor, EnergyInventorySlot energySlot,
                                         IChemicalTank inputTank, IChemicalTank outputTank, List<ITileComponent> components, PathElement problemPath) {
        this(provider, redstone, controlType, energyContainer, new ProgressRecipeCacheLookupMonitor<?>[] { cacheLookupMonitor }, energySlot, Collections.singletonList(inputTank),
                Collections.singletonList(outputTank), false, components, problemPath);
    }

    public ChemicalToChemicalUpgradeData(HolderLookup.Provider provider, boolean redstone, IRedstoneControl.RedstoneControl controlType,
                                         IEnergyContainer energyContainer, ProgressRecipeCacheLookupMonitor<?>[] cacheLookupMonitors, EnergyInventorySlot energySlot,
                                         List<IChemicalTank> inputTanks, List<IChemicalTank> outputTanks, boolean sorting, List<ITileComponent> components, PathElement problemPath) {
        this.redstone = redstone;
        this.controlType = controlType;
        this.energyContainer = energyContainer;
        this.cacheLookupMonitors = cacheLookupMonitors;
        this.energySlot = energySlot;
        this.inputTanks = inputTanks;
        this.outputTanks = outputTanks;
        this.sorting = sorting;
        this.components = IUpgradeData.readComponents(provider, components, problemPath);
    }
}
