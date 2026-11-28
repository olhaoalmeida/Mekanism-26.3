package fr.iglee42.evolvedmekanism.blocks;

import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import fr.iglee42.evolvedmekanism.registries.EMBlocks;
import fr.iglee42.evolvedmekanism.utils.EMNight;
import fr.iglee42.evolvedmekanism.registries.EMOreType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jspecify.annotations.Nullable;

@EventBusSubscriber(modid = EvolvedMekanism.MODID)
public class BlockNoctisRozuliOre extends EMBlockOre{

    public static final BooleanProperty UNCOVERED = BooleanProperty.create("uncovered");

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        boolean isDeepslate = BuiltInRegistries.BLOCK.getKey(this).getPath().contains("deepslate");
        return isDeepslate ? EMBlocks.ORES.get(EMOreType.NOCTIS_ROZULI).deepslate().asItem().getDefaultInstance() : EMBlocks.ORES.get(EMOreType.NOCTIS_ROZULI).stone().asItem().getDefaultInstance();
    }

    @Override
    public String getDescriptionId() {
        boolean isDeepslate = BuiltInRegistries.BLOCK.getKey(this).getPath().contains("deepslate");
        return Identifier.withDefaultNamespace((isDeepslate ? "deepslate_":"")+"lapis_ore").toLanguageKey("block");
    }

    public BlockNoctisRozuliOre(EMOreType ore, Properties properties) {
        super(ore, properties);
        this.registerDefaultState(this.defaultBlockState().setValue(UNCOVERED,false));
    }

    @Override
    public void playerDestroy(ServerLevel worldIn, ServerPlayer player, BlockPos pos, BlockState state,
                              @Nullable BlockEntity te, ItemStack stack) {
        super.playerDestroy(worldIn, player, pos, state.getValue(UNCOVERED) ? state : Blocks.LAPIS_ORE.defaultBlockState(), te,
                stack);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UNCOVERED);
        super.createBlockStateDefinition(builder);
    }

    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Post event){
        Level level = event.getEntity().level();
        if (level.isClientSide) return;
        if (level.getGameTime() % 20 != 0) return;
        BlockPos origin = event.getEntity().blockPosition();
        BlockPos.betweenClosedStream(origin.offset(-15, -15, -15),
                origin.offset(15, 15, 15)).filter(level::isLoaded).forEach(pos -> {
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof BlockNoctisRozuliOre block) {
                boolean isNight = EMNight.isNight(level);
                if (state.getValue(UNCOVERED) != isNight){
                    level.setBlock(pos, state.setValue(UNCOVERED, isNight), Block.UPDATE_ALL);
                }
            }
        });
    }

}
