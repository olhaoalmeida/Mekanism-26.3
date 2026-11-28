package fr.iglee42.evolvedmekanism.utils;

import mekanism.api.AutomationType;
import mekanism.api.fluid.IFluidTank;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.inventory.slot.FluidInventorySlot;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

/**
 * Fills/drains the single container (e.g. a bucket) that sits in a {@link FluidInventorySlot} without moving it to another slot.
 */
public final class FluidBucketHelper {

    private FluidBucketHelper() {
    }

    /**
     * Moves the contents of the container in the slot into the slot's tank (the container is left in the slot, now emptied).
     */
    public static void fillInPlace(FluidInventorySlot slot) {
        if (slot.isEmpty() || slot.getCount() != 1) {
            return;
        }
        slot.fillTankFromSlot(null);
    }

    /**
     * Moves the contents of the slot's tank into the container in the slot (the container is left in the slot, now filled).
     */
    public static void drainInPlace(FluidInventorySlot slot) {
        IFluidTank tank = slot.getFluidTank();
        if (slot.isEmpty() || slot.getCount() != 1 || tank.isEmpty()) {
            return;
        }
        ResourceHandler<FluidResource> handler = Capabilities.FLUID.item().getCapability(slot.asItemAccess());
        if (handler == null) {
            return;
        }
        FluidResource resource = tank.resource();
        try (Transaction transaction = Transaction.openRoot()) {
            int available = tank.extract(resource, tank.amountAsInt(), transaction, AutomationType.INTERNAL);
            if (available <= 0) {
                return;
            }
            int inserted = handler.insert(resource, available, transaction);
            if (inserted > 0 && tank.extract(resource, inserted, transaction, AutomationType.INTERNAL) == inserted) {
                transaction.commit();
            }
        }
    }
}
