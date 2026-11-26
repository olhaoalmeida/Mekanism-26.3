package fr.iglee42.evolvedmekanism.tiles;

import java.awt.*;
import java.util.Random;

import fr.iglee42.evolvedmekanism.multiblock.apt.APTMultiblockData;
import fr.iglee42.evolvedmekanism.particles.ColoredRisingBubbleOptions;
import fr.iglee42.evolvedmekanism.registries.EMBlocks;
import mekanism.api.chemical.ChemicalStack;
import mekanism.common.tile.prefab.TileEntityInternalMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class TileEntitySuperchargingElement extends TileEntityInternalMultiblock {

    @Nullable
    private Color chemicalColor;
    @Nullable
    private Color oChemicalColor;

    public TileEntitySuperchargingElement(BlockPos pos, BlockState state) {
        super(EMBlocks.SUPERCHARGING_ELEMENT, pos, state);
    }

    @Override
    protected boolean onUpdateServer(ServerLevel level) {
        boolean send = super.onUpdateServer(level);
        setActive(getMultiblock() != null && getMultiblock().isFormed() && getMultiblock() instanceof APTMultiblockData data && data.getScaledProgress() > 0);
        if (!(getMultiblock() instanceof APTMultiblockData data)) return send;
        ChemicalStack stack = data.inputTank.getStack();
        chemicalColor = new Color(stack.isEmpty() ? 0xFFFFFF : stack.getChemical().tint());
        if (!chemicalColor.equals(oChemicalColor)) {
            oChemicalColor = chemicalColor;
            send = true;
        }
        return send;
    }

    @Override
    protected void onUpdateClient(Level level) {
        super.onUpdateClient(level);
        if (getActive()){
            if (chemicalColor == null) return;
            {
                double x = getBlockPos().getX();
                double y = getBlockPos().getY()  + 1;
                double z = getBlockPos().getZ();
                if (level.random.nextFloat() > 0.5)level.addAlwaysVisibleParticle(new ColoredRisingBubbleOptions(chemicalColor.getRed() / 255f,chemicalColor.getGreen() /255f,chemicalColor.getBlue()/255f), x + (new Random().nextFloat()), y /*+ 3.25d*/, z + (new Random().nextFloat()), (double)0.0F, (double)0.1, (double)0.0F);


            }
        }
    }

    @Override
    public void writeReducedUpdatedTag(ValueOutput output) {
        super.writeReducedUpdatedTag(output);
        if (chemicalColor != null) {
            output.putInt("chemicalColor", chemicalColor.getRGB());
        }
    }

    @Override
    public void handleUpdateTag(ValueInput input) {
        super.handleUpdateTag(input);
        input.getInt("chemicalColor").ifPresent(rgb -> chemicalColor = new Color(rgb));
    }
}
