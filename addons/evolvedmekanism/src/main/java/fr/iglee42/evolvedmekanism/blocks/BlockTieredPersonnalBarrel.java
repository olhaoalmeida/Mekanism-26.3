package fr.iglee42.evolvedmekanism.blocks;

import fr.iglee42.evolvedmekanism.items.ItemBlockTieredPersonalStorage;
import fr.iglee42.evolvedmekanism.registries.EMBlocks;
import fr.iglee42.evolvedmekanism.tiers.PersonalStorageTier;
import fr.iglee42.evolvedmekanism.tiles.TileEntityTieredPersonalBarrel;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.tile.TileEntityPersonalBarrel;
import mekanism.common.tile.TileEntityPersonalChest;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BlockTieredPersonnalBarrel extends BlockTieredPersonalStorage<TileEntityTieredPersonalBarrel,BlockTypeTile<TileEntityTieredPersonalBarrel>>{
    public BlockTieredPersonnalBarrel(BlockTypeTile<TileEntityTieredPersonalBarrel> type, PersonalStorageTier tier, BlockBehaviour.Properties properties) {
        super(type, tier, properties);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        TileEntityPersonalBarrel barrel = WorldUtils.getTileEntity(TileEntityPersonalBarrel.class, level, pos);
        if (barrel != null) {
            barrel.recheckOpen();
        }
    }

    public static BlockRegistryObject<BlockTieredPersonnalBarrel, ItemBlockTieredPersonalStorage> getUpgrade(PersonalStorageTier tier){
        return switch (tier){
            case BASIC -> EMBlocks.ADVANCED_PERSONAL_BARREL;
            case ADVANCED -> EMBlocks.ELITE_PERSONAL_BARREL;
            case ELITE -> EMBlocks.ULTIMATE_PERSONAL_BARREL;
            case ULTIMATE -> EMBlocks.OVERCLOCKED_PERSONAL_BARREL;
            case OVERCLOCKED -> EMBlocks.QUANTUM_PERSONAL_BARREL;
            case QUANTUM -> EMBlocks.DENSE_PERSONAL_BARREL;
            case DENSE -> EMBlocks.MULTIVERSAL_PERSONAL_BARREL;
            case MULTIVERSAL -> EMBlocks.CREATIVE_PERSONAL_BARREL;
            case CREATIVE -> null;
        };
    }
}
