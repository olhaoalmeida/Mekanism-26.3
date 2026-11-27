package fr.iglee42.emtools.materials;

import fr.iglee42.emtools.registries.EMToolsTags;
import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import fr.iglee42.evolvedmekanism.registries.EMTags;
import mekanism.tools.common.material.BaseMekanismMaterial;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class PlaslitheriteMaterialDefaults implements BaseMekanismMaterial {

    @Override
    public int shieldDurability() {
        return 3360;
    }

    @Override
    public float axeDamage() {
        return 18;
    }

    @Override
    public float swordDamage() {
        return 16;
    }
    @Override
    public float pickaxeDamage() {
        return 12;
    }

    @Override
    public float shovelDamage() {
        return 12.5f;
    }

    @Override
    public float hoeDamage() {
        return 2;
    }


    @Override
    public float axeAtkSpeed() {
        return -3.0F;
    }

    @Override
    public int durability() {
        return 16384;
    }

    @Override
    public float speed() {
        return 8;
    }

    @Override
    public float attackDamageBonus() {
        return 2;
    }

    @Override
    public TagKey<Block> incorrectBlocksForDrops() {
        return EMToolsTags.Blocks.INCORRECT_FOR_PLASLITHERITE_TOOL;
    }

    @Override
    public int enchantmentValue() {
        return 16;
    }

    @Override
    public TagKey<Item> repairItems() {
        return EMTags.Items.INGOTS_PLASLITHERITE;
    }

    @Override
    public float toughness() {
        return 5;
    }

    @Override
    public int durability(@NotNull ArmorType armorType) {
        return switch (armorType) {
            case BOOTS -> 3300;
            case LEGGINGS -> 4500;
            case CHESTPLATE -> 4800;
            case HELMET -> 3900;
            case BODY -> 0;
        };
    }

    @Override
    public int defense(@NotNull ArmorType armorType) {
        return switch (armorType) {
            case BOOTS -> 9;
            case LEGGINGS -> 12;
            case CHESTPLATE -> 14;
            case HELMET -> 10;
            case BODY -> 0;
        };
    }

    @NotNull
    @Override
    public String registryPrefix() {
        return "plaslitherite";
    }

    @Override
    public float knockbackResistance() {
        return 0.25f;
    }

    @Override
    public Holder<SoundEvent> equipSound() {
        return SoundEvents.ARMOR_EQUIP_NETHERITE;
    }

    @Override
    public float spearAttackDuration() {
        return 1.0F;
    }

    @Override
    public float spearDamageMultiplier() {
        return 1.0F;
    }

    @Override
    public float spearDelay() {
        return 0.6F;
    }

    @Override
    public float spearDismountTime() {
        return 3.0F;
    }

    @Override
    public float spearDismountThreshold() {
        return 11.0F;
    }

    @Override
    public float spearKnockbackTime() {
        return 7.0F;
    }

    @Override
    public float spearDamageTime() {
        return 11.0F;
    }
}
