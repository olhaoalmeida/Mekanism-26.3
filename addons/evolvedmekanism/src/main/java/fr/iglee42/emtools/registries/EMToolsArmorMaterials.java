package fr.iglee42.emtools.registries;

import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * In 26.x armor materials are no longer a registry; they are built per item by MaterialCreator#toArmorMaterial.
 * This empty register is kept only so existing callers (EvolvedMekanism) keep compiling.
 */
public class EMToolsArmorMaterials {

    private EMToolsArmorMaterials() {
    }

    @SuppressWarnings("unused")
    public static final DeferredRegister<Item> ARMOR_MATERIALS = DeferredRegister.create(Registries.ITEM, EvolvedMekanism.MODID);
}
