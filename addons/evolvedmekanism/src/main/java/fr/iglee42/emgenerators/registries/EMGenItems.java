package fr.iglee42.emgenerators.registries;

import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import mekanism.common.item.ItemUpgrade;
import mekanism.common.registration.impl.ItemDeferredRegister;
import mekanism.common.registration.impl.ItemRegistryObject;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;

public class EMGenItems {
    public static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(EvolvedMekanism.MODID);

    public static final ItemRegistryObject<Item> LUNAR_PANEL = ITEMS.register("lunar_panel");
    //Upgrades are datapack driven in Mekanism 26.3 (a single ItemUpgrade carries the upgrade as a data component),
    // these items are kept so the recipes/aliases that reference them keep compiling. TODO: migrate to data-driven upgrades.
    public static final ItemRegistryObject<ItemUpgrade> SOLAR_UPGRADE = ITEMS.registerItem("upgrade_solar", ItemUpgrade::new);
    public static final ItemRegistryObject<ItemUpgrade> LUNAR_UPGRADE = ITEMS.registerItem("upgrade_lunar", ItemUpgrade::new);

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
