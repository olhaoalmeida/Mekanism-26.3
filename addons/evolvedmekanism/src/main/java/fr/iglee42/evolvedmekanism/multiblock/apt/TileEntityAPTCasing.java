package fr.iglee42.evolvedmekanism.multiblock.apt;

import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import fr.iglee42.evolvedmekanism.registries.EMBlocks;
import mekanism.api.SerializationConstants;
import mekanism.common.lib.multiblock.MultiblockType;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class TileEntityAPTCasing extends TileEntityMultiblock<APTMultiblockData> {

    private static final boolean DEFAULT_HANDLE_SOUND = false;

    private boolean handleSound = DEFAULT_HANDLE_SOUND;
    private boolean prevActive;

    public TileEntityAPTCasing(BlockPos pos, BlockState state) {
        this(EMBlocks.APT_CASING, pos, state);
    }

    public TileEntityAPTCasing(Holder<Block> provider, BlockPos pos, BlockState state) {
        super(provider, pos, state);
    }

    @Override
    protected boolean onUpdateServer(ServerLevel level, APTMultiblockData multiblock) {
        boolean needsPacket = super.onUpdateServer(level, multiblock);
        boolean active = multiblock.isFormed() && multiblock.handlesSound(this) && multiblock.progress > 0;
        if (active != prevActive) {
            prevActive = active;
            needsPacket = true;
        }
        return needsPacket;
    }

    @Override
    public APTMultiblockData createMultiblock() {
        return new APTMultiblockData(this);
    }

    @Override
    public MultiblockType<APTMultiblockData> getMultiblockType() {
        return EvolvedMekanism.APT_MULTIBLOCK;
    }

    @Override
    protected boolean canPlaySound() {
        APTMultiblockData multiblock = getMultiblock();
        return multiblock.isFormed() && handleSound && multiblock.progress > 0;
    }

    @Override
    public void writeReducedUpdatedTag(ValueOutput output) {
        super.writeReducedUpdatedTag(output);
        APTMultiblockData multiblock = getMultiblock();
        output.putBoolean(SerializationConstants.HANDLE_SOUND, multiblock.isFormed() && multiblock.handlesSound(this));
    }

    @Override
    public void handleUpdateTag(ValueInput input) {
        super.handleUpdateTag(input);
        handleSound = input.getBooleanOr(SerializationConstants.HANDLE_SOUND, DEFAULT_HANDLE_SOUND);
    }
}
