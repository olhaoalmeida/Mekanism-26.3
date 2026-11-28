package fr.iglee42.evolvedmekanism.blocks;

import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import fr.iglee42.evolvedmekanism.registries.EMOreType;
import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.block.states.BlockStateHelper;
import mekanism.common.resource.ore.OreType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import org.jspecify.annotations.Nullable;

public class EMBlockOre extends Block implements IHasDescription {

    private final EMOreType ore;
    @Nullable
    private String descriptionTranslationKey;

    public static BlockBehaviour.Properties defaultProperties(BlockBehaviour.Properties properties) {
        return BlockStateHelper.applyLightLevelAdjustments(properties).strength(3, 3).requiresCorrectToolForDrops()
              .mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM);
    }

    public EMBlockOre(EMOreType ore, BlockBehaviour.Properties properties) {
        super(properties);
        this.ore = ore;
    }

    @Override
    public String getDescription() {
        if (descriptionTranslationKey == null) {
            descriptionTranslationKey = EvolvedMekanism.rl(ore.getResource().getRegistrySuffix() + "_ore").toLanguageKey("description");
        }
        return descriptionTranslationKey;
    }

    @Override
    public int getExpDrop(BlockState state, LevelAccessor level, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity breaker,
          ItemStack tool) {
        //Note: If min exp = max exp = 0, then this will just return zero, similar to what super does
        return Mth.nextInt(level.getRandom(), ore.getMinExp(), ore.getMaxExp());
    }
}