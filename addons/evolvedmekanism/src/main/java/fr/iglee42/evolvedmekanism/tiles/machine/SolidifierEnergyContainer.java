package fr.iglee42.evolvedmekanism.tiles.machine;

import mekanism.api.IContentsListener;
import mekanism.api.functions.IntObjectToIntFunction;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import org.jspecify.annotations.Nullable;

/**
 * Kept for API compatibility. In 26.3 the extra per-recipe energy is expressed through the base energy calculator of {@link MachineEnergyContainer}, so this is a thin
 * factory around it.
 */
public final class SolidifierEnergyContainer {

    private SolidifierEnergyContainer() {
    }

    public static MachineEnergyContainer<TileEntitySolidifier> input(TileEntitySolidifier tile, @Nullable IContentsListener listener) {
        IntObjectToIntFunction<TileEntitySolidifier> calculator = (base, t) -> base + t.getRecipeEnergyRequired();
        return MachineEnergyContainer.input(tile, calculator, listener);
    }
}
