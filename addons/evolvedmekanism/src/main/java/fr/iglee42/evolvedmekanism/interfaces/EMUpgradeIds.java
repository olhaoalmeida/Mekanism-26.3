package fr.iglee42.evolvedmekanism.interfaces;

import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import mekanism.api.MekanismRegistries;
import mekanism.api.upgrade.Upgrade;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

/// Data-driven upgrade keys for the Evolved Mekanism upgrades (the upgrades themselves must be provided by data packs).
public final class EMUpgradeIds {

    private EMUpgradeIds() {
    }

    public static final ResourceKey<Upgrade> RADIOACTIVE = key("radioactive");
    public static final ResourceKey<Upgrade> SOLAR = key("solar");
    public static final ResourceKey<Upgrade> LUNAR = key("lunar");

    private static ResourceKey<Upgrade> key(String name) {
        return ResourceKey.create(MekanismRegistries.Keys.UPGRADES, Identifier.fromNamespaceAndPath(EvolvedMekanism.MODID, name));
    }

    /// @return how many of the given upgrade are installed in the tile (0 if upgrades are unsupported or the upgrade is not registered).
    public static int getUpgrades(TileEntityMekanism tile, ResourceKey<Upgrade> key) {
        if (!tile.supportsUpgrades()) {
            return 0;
        }
        Registry<Upgrade> registry = tile.getUpgradeRegistry();
        if (registry == null) {
            return 0;
        }
        return registry.get(key).map(holder -> tile.getUpgrades(holder)).orElse(0);
    }
}
