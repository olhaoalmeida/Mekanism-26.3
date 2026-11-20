package com.jerry.mekmm.common.upgrade;

import mekanism.common.recipe.lookup.monitor.ProgressRecipeCacheLookupMonitor;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.slot.ChemicalInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.interfaces.IRedstoneControl.RedstoneControl;
import mekanism.common.upgrade.MachineUpgradeData;

import net.minecraft.core.HolderLookup;
import net.minecraft.util.ProblemReporter.PathElement;

import java.util.Collections;
import java.util.List;

public class PlantingUpgradeData extends MachineUpgradeData {

    public final IChemicalTank chemicalTank;
    public final ChemicalInventorySlot chemicalSlot;
    public final int[] usedSoFar;

    // Planting Station Constructor
    public PlantingUpgradeData(HolderLookup.Provider provider, boolean redstone, RedstoneControl controlType, IEnergyContainer energyContainer, ProgressRecipeCacheLookupMonitor<?> cacheLookupMonitor,
                               int usedSoFar, IChemicalTank chemicalTank, EnergyInventorySlot energySlot, ChemicalInventorySlot chemicalSlot, InputInventorySlot inputSlot,
                               OutputInventorySlot outputSlot, OutputInventorySlot secondaryOutputSlot, List<ITileComponent> components, PathElement problemPath) {
        this(provider, redstone, controlType, energyContainer, new ProgressRecipeCacheLookupMonitor<?>[] { cacheLookupMonitor }, new int[] { usedSoFar }, chemicalTank, energySlot,
                chemicalSlot, Collections.singletonList(inputSlot), List.of(outputSlot, secondaryOutputSlot), false, components, problemPath);
    }

    // Planting Factory Constructor
    public PlantingUpgradeData(HolderLookup.Provider provider, boolean redstone, RedstoneControl controlType, IEnergyContainer energyContainer, ProgressRecipeCacheLookupMonitor<?>[] cacheLookupMonitors,
                               int[] usedSoFar, IChemicalTank chemicalTank, EnergyInventorySlot energySlot, ChemicalInventorySlot chemicalSlot, List<IInventorySlot> inputSlots,
                               List<IInventorySlot> outputSlots, boolean sorting, List<ITileComponent> components, PathElement problemPath) {
        super(provider, redstone, controlType, energyContainer, energySlot, cacheLookupMonitors, inputSlots, outputSlots, sorting, components, problemPath);
        this.chemicalTank = chemicalTank;
        this.chemicalSlot = chemicalSlot;
        this.usedSoFar = usedSoFar;
    }
}
