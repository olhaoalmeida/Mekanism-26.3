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
import org.jetbrains.annotations.Nullable;

public class RefinedRedstoneMaterialDefaults implements BaseMekanismMaterial {

    @Override
    public int shieldDurability() {
        return 448;
    }

    @Override
    public float axeDamage() {
        return 7;
    }

    @Override
    public float axeAtkSpeed() {
        return -3.0F;
    }

    @Override
    public int durability() {
        return 500;
    }

    @Override
    public float speed() {
        return 8;
    }

    @Override
    public float attackDamageBonus() {
        return 0;
    }

    @Override
    public int enchantmentValue() {
        return 16;
    }

    @Override
    public float toughness() {
        return 2;
    }

    @Override
    public int durability(@NotNull ArmorType armorType) {
        return switch (armorType) {
            case BOOTS -> 260;
            case LEGGINGS -> 300;
            case CHESTPLATE -> 320;
            case HELMET -> 220;
            case BODY -> 0;
        };
    }

    @Override
    public int defense(@NotNull ArmorType armorType) {
        return switch (armorType) {
            case BOOTS -> 3;
            case LEGGINGS -> 6;
            case CHESTPLATE -> 8;
            case HELMET -> 3;
            case BODY -> 0;
        };
    }


    @NotNull
    @Override
    public String registryPrefix() {
        return "refined_redstone";
    }

    @Override
    public TagKey<Block> incorrectBlocksForDrops() {
        return EMToolsTags.Blocks.INCORRECT_FOR_REFINED_REDSTONE_TOOL;
    }

    @NotNull
    @Override
    public Holder<SoundEvent> equipSound() {
        return SoundEvents.ARMOR_EQUIP_NETHERITE;
    }

    @Override
    public TagKey<Item> repairItems() {
        return EMTags.Items.INGOTS_REFINED_REDSTONE;
    }

    @Override
    public float knockbackResistance() {
        return 0;
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
