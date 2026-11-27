package fr.iglee42.evolvedmekanism.tiers.storage;

import fr.iglee42.evolvedmekanism.tiers.EMBaseTier;
import java.util.function.Supplier;
import mekanism.api.tier.BaseTier;

/**
 * QIO drive tiers no longer exist as an enum in Mekanism 26.3 (drives are driven by data components),
 * so this is a plain holder for the capacity of each Evolved Mekanism drive. The base tier is lazy because
 * the EM base tiers are only created once the BaseTier enum extension has been initialized.
 */
public record EMQIODriveTier(Supplier<BaseTier> baseTier, long count, int types) {

    public static final EMQIODriveTier BOOSTED = new EMQIODriveTier(() -> EMBaseTier.OVERCLOCKED, 32_000_000_000L, 16_384);
    public static final EMQIODriveTier SINGULARITY = new EMQIODriveTier(() -> EMBaseTier.QUANTUM, 64_000_000_000L, 32_768);
    public static final EMQIODriveTier HYPRA_SOLIDIFIED = new EMQIODriveTier(() -> EMBaseTier.DENSE, 128_000_000_000L, 65_536);
    public static final EMQIODriveTier BLACK_HOLE = new EMQIODriveTier(() -> EMBaseTier.MULTIVERSAL, 256_000_000_000L, 131_072);
    public static final EMQIODriveTier CREATIVE = new EMQIODriveTier(() -> BaseTier.CREATIVE, Long.MAX_VALUE, Integer.MAX_VALUE);
}
