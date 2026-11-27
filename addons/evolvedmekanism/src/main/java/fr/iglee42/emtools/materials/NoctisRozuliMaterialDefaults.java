package fr.iglee42.emtools.materials;

import fr.iglee42.emtools.registries.EMToolsTags;
import fr.iglee42.evolvedmekanism.registries.EMTags;
import mekanism.tools.common.material.BaseMekanismMaterial;
import mekanism.tools.common.material.impl.LapisLazuliMaterialDefaults;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NoctisRozuliMaterialDefaults extends LapisLazuliMaterialDefaults {

    @NotNull
    @Override
    public String registryPrefix() {
        return "noctis_rozuli";
    }

    @Override
    public TagKey<Block> incorrectBlocksForDrops() {
        return EMToolsTags.Blocks.INCORRECT_FOR_NOCTIS_ROZULI_TOOL;
    }


    @Override
    public TagKey<Item> repairItems() {
        return EMTags.Items.GEMS_NOCTIS_ROZULI;
    }

}