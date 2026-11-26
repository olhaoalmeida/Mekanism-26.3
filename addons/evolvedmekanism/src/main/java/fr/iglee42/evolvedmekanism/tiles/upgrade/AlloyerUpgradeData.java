package fr.iglee42.evolvedmekanism.tiles.upgrade;

import fr.iglee42.evolvedmekanism.tiles.LimitedInputInventorySlot;
import java.util.List;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.recipe.lookup.monitor.ProgressRecipeCacheLookupMonitor;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.interfaces.IRedstoneControl.RedstoneControl;
import mekanism.common.upgrade.MachineUpgradeData;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.ProblemReporter.PathElement;

public class AlloyerUpgradeData extends MachineUpgradeData {

    public final LimitedInputInventorySlot extraSlot;
    public final LimitedInputInventorySlot secondaryExtraSlot;

    //Alloyer Constructor
    public AlloyerUpgradeData(HolderLookup.Provider provider, boolean redstone, RedstoneControl controlType, IEnergyContainer energyContainer,
          ProgressRecipeCacheLookupMonitor<?> cacheLookupMonitor, EnergyInventorySlot energySlot, LimitedInputInventorySlot extraSlot,
          LimitedInputInventorySlot secondaryExtraSlot, InputInventorySlot inputSlot, OutputInventorySlot outputSlot, List<ITileComponent> components, PathElement problemPath) {
        super(provider, redstone, controlType, energyContainer, energySlot, cacheLookupMonitor, inputSlot, outputSlot, components, problemPath);
        this.extraSlot = extraSlot;
        this.secondaryExtraSlot = secondaryExtraSlot;
    }

    //Alloying Factory Constructor
    public AlloyerUpgradeData(HolderLookup.Provider provider, boolean redstone, RedstoneControl controlType, IEnergyContainer energyContainer,
          EnergyInventorySlot energySlot, ProgressRecipeCacheLookupMonitor<?>[] cacheLookupMonitors, LimitedInputInventorySlot extraSlot,
          LimitedInputInventorySlot secondaryExtraSlot, List<IInventorySlot> inputSlots, List<IInventorySlot> outputSlots, boolean sorting,
          List<ITileComponent> components, PathElement problemPath) {
        super(provider, redstone, controlType, energyContainer, energySlot, cacheLookupMonitors, inputSlots, outputSlots, sorting, components, problemPath);
        this.extraSlot = extraSlot;
        this.secondaryExtraSlot = secondaryExtraSlot;
    }
}
