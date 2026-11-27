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

public class BetterGoldMaterialDefaults implements BaseMekanismMaterial {

    @Override
    public int shieldDurability() {
        return 3360;
    }

    @Override
    public float axeDamage() {
        return 16;
    }

    @Override
    public float axeAtkSpeed() {
        return -3.0F;
    }

    @Override
    public float swordDamage() {
        return 14;
    }

    @Override
    public float pickaxeDamage() {
        return 10;
    }

    @Override
    public float shovelDamage() {
        return 10.5f;
    }

    @Override
    public float hoeDamage() {
        return 0;
    }

    @Override
    public int durability() {
        return 8192;
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
        return EMToolsTags.Blocks.INCORRECT_FOR_NEEDS_BETTER_GOLD_TOOL;
    }

    @Override
    public int enchantmentValue() {
        return 16;
    }

    @Override
    public TagKey<Item> repairItems() {
        return EMTags.Items.INGOTS_BETTER_GOLD;
    }

    @Override
    public float toughness() {
        return 5;
        }

    @Override
    public int durability(@NotNull ArmorType armorType) {
        return switch (armorType) {
            case BODY -> 0;
            case BOOTS -> 1650;
            case LEGGINGS -> 2250;
            case CHESTPLATE -> 2400;
            case HELMET -> 1950;
        };
    }

    @Override
    public int defense(@NotNull ArmorType armorType) {
        return switch (armorType) {
            case BODY -> 0;
            case BOOTS -> 7;
            case LEGGINGS -> 10;
            case CHESTPLATE -> 12;
            case HELMET -> 8;
        };
    }

    @NotNull
    @Override
    public String registryPrefix() {
        return "better_gold";
    }

    @Override
    public float knockbackResistance() {
        return 0.2f;
    }

    @Override
    public Holder<SoundEvent> equipSound() {
        return SoundEvents.ARMOR_EQUIP_GOLD;
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
