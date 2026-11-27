package fr.iglee42.emtools.registries;

import fr.iglee42.emtools.config.EvolvedMekanismToolsConfig;
import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import mekanism.common.registration.impl.ItemDeferredRegister;
import mekanism.common.registration.impl.ItemRegistryObject;
import mekanism.tools.common.material.MaterialCreator;
import mekanism.tools.common.registries.ToolsItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.neoforged.bus.api.IEventBus;

public class EMToolsItems {
    public static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(EvolvedMekanism.MODID);

    public static final ItemRegistryObject<Item> BETTER_GOLD_PICKAXE = registerPickaxe(EvolvedMekanismToolsConfig.materials.betterGold);
    public static final ItemRegistryObject<Item> BETTER_GOLD_AXE = registerAxe(EvolvedMekanismToolsConfig.materials.betterGold);
    public static final ItemRegistryObject<Item> BETTER_GOLD_SHOVEL = registerShovel(EvolvedMekanismToolsConfig.materials.betterGold);
    public static final ItemRegistryObject<Item> BETTER_GOLD_HOE = registerHoe(EvolvedMekanismToolsConfig.materials.betterGold);
    public static final ItemRegistryObject<Item> BETTER_GOLD_SWORD = registerSword(EvolvedMekanismToolsConfig.materials.betterGold);
    public static final ItemRegistryObject<Item> BETTER_GOLD_PAXEL = registerPaxel(EvolvedMekanismToolsConfig.materials.betterGold);
    public static final ItemRegistryObject<Item> BETTER_GOLD_HELMET = registerArmor(EvolvedMekanismToolsConfig.materials.betterGold, ArmorType.HELMET);
    public static final ItemRegistryObject<Item> BETTER_GOLD_CHESTPLATE = registerArmor(EvolvedMekanismToolsConfig.materials.betterGold, ArmorType.CHESTPLATE);
    public static final ItemRegistryObject<Item> BETTER_GOLD_LEGGINGS = registerArmor(EvolvedMekanismToolsConfig.materials.betterGold, ArmorType.LEGGINGS);
    public static final ItemRegistryObject<Item> BETTER_GOLD_BOOTS = registerArmor(EvolvedMekanismToolsConfig.materials.betterGold, ArmorType.BOOTS);
    public static final ItemRegistryObject<ShieldItem> BETTER_GOLD_SHIELD = registerShield(EvolvedMekanismToolsConfig.materials.betterGold);

    public static final ItemRegistryObject<Item> PLASLITHERITE_PICKAXE = registerPickaxe(EvolvedMekanismToolsConfig.materials.plaslitherite);
    public static final ItemRegistryObject<Item> PLASLITHERITE_AXE = registerAxe(EvolvedMekanismToolsConfig.materials.plaslitherite);
    public static final ItemRegistryObject<Item> PLASLITHERITE_SHOVEL = registerShovel(EvolvedMekanismToolsConfig.materials.plaslitherite);
    public static final ItemRegistryObject<Item> PLASLITHERITE_HOE = registerHoe(EvolvedMekanismToolsConfig.materials.plaslitherite);
    public static final ItemRegistryObject<Item> PLASLITHERITE_SWORD = registerSword(EvolvedMekanismToolsConfig.materials.plaslitherite);
    public static final ItemRegistryObject<Item> PLASLITHERITE_PAXEL = registerPaxel(EvolvedMekanismToolsConfig.materials.plaslitherite);
    public static final ItemRegistryObject<Item> PLASLITHERITE_HELMET = registerArmor(EvolvedMekanismToolsConfig.materials.plaslitherite, ArmorType.HELMET);
    public static final ItemRegistryObject<Item> PLASLITHERITE_CHESTPLATE = registerArmor(EvolvedMekanismToolsConfig.materials.plaslitherite, ArmorType.CHESTPLATE);
    public static final ItemRegistryObject<Item> PLASLITHERITE_LEGGINGS = registerArmor(EvolvedMekanismToolsConfig.materials.plaslitherite, ArmorType.LEGGINGS);
    public static final ItemRegistryObject<Item> PLASLITHERITE_BOOTS = registerArmor(EvolvedMekanismToolsConfig.materials.plaslitherite, ArmorType.BOOTS);
    public static final ItemRegistryObject<ShieldItem> PLASLITHERITE_SHIELD = registerShield(EvolvedMekanismToolsConfig.materials.plaslitherite);

    public static final ItemRegistryObject<Item> REFINED_REDSTONE_PICKAXE = registerPickaxe(EvolvedMekanismToolsConfig.materials.refinedRedstone);
    public static final ItemRegistryObject<Item> REFINED_REDSTONE_AXE = registerAxe(EvolvedMekanismToolsConfig.materials.refinedRedstone);
    public static final ItemRegistryObject<Item> REFINED_REDSTONE_SHOVEL = registerShovel(EvolvedMekanismToolsConfig.materials.refinedRedstone);
    public static final ItemRegistryObject<Item> REFINED_REDSTONE_HOE = registerHoe(EvolvedMekanismToolsConfig.materials.refinedRedstone);
    public static final ItemRegistryObject<Item> REFINED_REDSTONE_SWORD = registerSword(EvolvedMekanismToolsConfig.materials.refinedRedstone);
    public static final ItemRegistryObject<Item> REFINED_REDSTONE_PAXEL = registerPaxel(EvolvedMekanismToolsConfig.materials.refinedRedstone);
    public static final ItemRegistryObject<Item> REFINED_REDSTONE_HELMET = registerArmor(EvolvedMekanismToolsConfig.materials.refinedRedstone, ArmorType.HELMET);
    public static final ItemRegistryObject<Item> REFINED_REDSTONE_CHESTPLATE = registerArmor(EvolvedMekanismToolsConfig.materials.refinedRedstone, ArmorType.CHESTPLATE);
    public static final ItemRegistryObject<Item> REFINED_REDSTONE_LEGGINGS = registerArmor(EvolvedMekanismToolsConfig.materials.refinedRedstone, ArmorType.LEGGINGS);
    public static final ItemRegistryObject<Item> REFINED_REDSTONE_BOOTS = registerArmor(EvolvedMekanismToolsConfig.materials.refinedRedstone, ArmorType.BOOTS);
    public static final ItemRegistryObject<ShieldItem> REFINED_REDSTONE_SHIELD = registerShield(EvolvedMekanismToolsConfig.materials.refinedRedstone);

    public static final ItemRegistryObject<Item> NOCTIS_ROZULI_PICKAXE = registerPickaxe(EvolvedMekanismToolsConfig.materials.noctisRozuli);
    public static final ItemRegistryObject<Item> NOCTIS_ROZULI_AXE = registerAxe(EvolvedMekanismToolsConfig.materials.noctisRozuli);
    public static final ItemRegistryObject<Item> NOCTIS_ROZULI_SHOVEL = registerShovel(EvolvedMekanismToolsConfig.materials.noctisRozuli);
    public static final ItemRegistryObject<Item> NOCTIS_ROZULI_HOE = registerHoe(EvolvedMekanismToolsConfig.materials.noctisRozuli);
    public static final ItemRegistryObject<Item> NOCTIS_ROZULI_SWORD = registerSword(EvolvedMekanismToolsConfig.materials.noctisRozuli);
    public static final ItemRegistryObject<Item> NOCTIS_ROZULI_PAXEL = registerPaxel(EvolvedMekanismToolsConfig.materials.noctisRozuli);
    public static final ItemRegistryObject<Item> NOCTIS_ROZULI_HELMET = registerArmor(EvolvedMekanismToolsConfig.materials.noctisRozuli, ArmorType.HELMET);
    public static final ItemRegistryObject<Item> NOCTIS_ROZULI_CHESTPLATE = registerArmor(EvolvedMekanismToolsConfig.materials.noctisRozuli, ArmorType.CHESTPLATE);
    public static final ItemRegistryObject<Item> NOCTIS_ROZULI_LEGGINGS = registerArmor(EvolvedMekanismToolsConfig.materials.noctisRozuli, ArmorType.LEGGINGS);
    public static final ItemRegistryObject<Item> NOCTIS_ROZULI_BOOTS = registerArmor(EvolvedMekanismToolsConfig.materials.noctisRozuli, ArmorType.BOOTS);
    public static final ItemRegistryObject<ShieldItem> NOCTIS_ROZULI_SHIELD = registerShield(EvolvedMekanismToolsConfig.materials.noctisRozuli);

    private static ItemRegistryObject<ShieldItem> registerShield(MaterialCreator material) {
        return ITEMS.registerItem(material.registryPrefix() + "_shield", properties -> new ShieldItem(ToolsItems.setCommonProperties(properties, material)
              .durability(material.shieldDurability())
              .component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
              .repairable(material.repairItems())
              .equippableUnswappable(EquipmentSlot.OFFHAND)
              .delayedComponent(DataComponents.BLOCKS_ATTACKS, context -> new BlocksAttacks(
                    material.shieldBlockDelay(),
                    material.shieldDisableCooldownScale(),
                    List.of(new BlocksAttacks.DamageReduction(
                          material.shieldHorizontalBlockingAngle(),
                          Optional.empty(),
                          material.shieldDamageReductionBase(),
                          material.shieldDamageReductionFactor()
                    )),
                    new BlocksAttacks.ItemDamageFunction(material.shieldDamageThreshold(), material.shieldItemDamageBase(), material.shieldItemDamageFactor()),
                    Optional.of(context.getOrThrow(DamageTypeTags.BYPASSES_SHIELD)),
                    Optional.of(SoundEvents.SHIELD_BLOCK),
                    Optional.of(SoundEvents.SHIELD_BREAK)
              ))
              .component(DataComponents.BREAK_SOUND, SoundEvents.SHIELD_BREAK)
        ));
    }

    private static ItemRegistryObject<Item> registerSimple(MaterialCreator material, String suffix, UnaryOperator<Item.Properties> modifier) {
        return ITEMS.registerSimple(material.registryPrefix() + suffix, properties -> modifier.apply(ToolsItems.setCommonProperties(properties, material)));
    }

    private static ItemRegistryObject<Item> registerPickaxe(MaterialCreator material) {
        return registerSimple(material, "_pickaxe", p -> p.pickaxe(material.toToolMaterial(), material.pickaxeDamage(), material.pickaxeAtkSpeed()));
    }

    private static ItemRegistryObject<Item> registerAxe(MaterialCreator material) {
        return registerSimple(material, "_axe", p -> p.axe(material.toToolMaterial(), material.axeDamage(), material.axeAtkSpeed()));
    }

    private static ItemRegistryObject<Item> registerShovel(MaterialCreator material) {
        return registerSimple(material, "_shovel", p -> p.shovel(material.toToolMaterial(), material.shovelDamage(), material.shovelAtkSpeed()));
    }

    private static ItemRegistryObject<Item> registerHoe(MaterialCreator material) {
        return registerSimple(material, "_hoe", p -> p.hoe(material.toToolMaterial(), material.hoeDamage(), material.hoeAtkSpeed()));
    }

    private static ItemRegistryObject<Item> registerSword(MaterialCreator material) {
        return registerSimple(material, "_sword", p -> p.sword(material.toToolMaterial(), material.swordDamage(), material.swordAtkSpeed()));
    }

    private static ItemRegistryObject<Item> registerPaxel(MaterialCreator material) {
        return ITEMS.registerItem(material.registryPrefix() + "_paxel", properties -> ToolsItems.paxel(ToolsItems.setCommonProperties(properties, material), material));
    }

    private static ItemRegistryObject<Item> registerArmor(MaterialCreator material, ArmorType armorType) {
        return registerSimple(material, "_" + armorType.getName(), p -> p
              .humanoidArmor(material.toArmorMaterial(armorType), armorType)
              //Durability must go after humanoidArmor to set it to the correct value
              .durability(material.durability(armorType)));
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
