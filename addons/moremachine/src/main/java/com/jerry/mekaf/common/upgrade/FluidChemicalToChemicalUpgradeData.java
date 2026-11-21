package com.jerry.mekaf.common.upgrade;

import mekanism.common.recipe.lookup.monitor.ProgressRecipeCacheLookupMonitor;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.FluidInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.interfaces.IRedstoneControl;

import net.minecraft.core.HolderLookup;
import net.minecraft.util.ProblemReporter.PathElement;

import java.util.Collections;
import java.util.List;

public class FluidChemicalToChemicalUpgradeData extends ChemicalToChemicalUpgradeData {

    public final FluidInventorySlot fluidInputSlot;
    public final OutputInventorySlot fluidOutputSlot;
    public final BasicFluidTank inputTank;
    public final long[] usedSoFar;

    public FluidChemicalToChemicalUpgradeData(HolderLookup.Provider provider, boolean redstone, IRedstoneControl.RedstoneControl controlType,
                                              IEnergyContainer energyContainer, ProgressRecipeCacheLookupMonitor<?> cacheLookupMonitor, long usedSoFar, EnergyInventorySlot energySlot,
                                              FluidInventorySlot fluidInputSlot, OutputInventorySlot fluidOutputSlot, IChemicalTank inputSlot,
                                              BasicFluidTank inputTank, IChemicalTank outputTank, List<ITileComponent> components, PathElement problemPath) {
        this(provider, redstone, controlType, energyContainer, new ProgressRecipeCacheLookupMonitor<?>[] { cacheLookupMonitor }, new long[] { usedSoFar }, energySlot, fluidInputSlot, fluidOutputSlot,
                Collections.singletonList(inputSlot), inputTank, Collections.singletonList(outputTank), false, components, problemPath);
    }

    public FluidChemicalToChemicalUpgradeData(HolderLookup.Provider provider, boolean redstone, IRedstoneControl.RedstoneControl controlType,
                                              IEnergyContainer energyContainer, ProgressRecipeCacheLookupMonitor<?>[] cacheLookupMonitors, long[] usedSoFar, EnergyInventorySlot energySlot,
                                              FluidInventorySlot fluidInputSlot, OutputInventorySlot fluidOutputSlot, List<IChemicalTank> inputSlots,
                                              BasicFluidTank inputTank, List<IChemicalTank> outputTanks, boolean sorting, List<ITileComponent> components, PathElement problemPath) {
        super(provider, redstone, controlType, energyContainer, cacheLookupMonitors, energySlot, inputSlots, outputTanks, sorting, components, problemPath);
        this.fluidInputSlot = fluidInputSlot;
        this.fluidOutputSlot = fluidOutputSlot;
        this.inputTank = inputTank;
        this.usedSoFar = usedSoFar;
    }
}
