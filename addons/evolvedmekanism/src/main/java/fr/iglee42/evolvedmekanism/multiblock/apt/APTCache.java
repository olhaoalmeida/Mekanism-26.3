package fr.iglee42.evolvedmekanism.multiblock.apt;

import mekanism.api.SerializationConstants;
import mekanism.common.lib.multiblock.MultiblockCache;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class APTCache extends MultiblockCache<APTMultiblockData> {

    private int progress;

    @Override
    public void merge(MultiblockCache<APTMultiblockData> mergeCache, RejectContents rejectContents) {
        super.merge(mergeCache, rejectContents);
        progress += ((APTCache) mergeCache).progress;
    }

    @Override
    public void apply(APTMultiblockData data, TransactionContext transaction) {
        super.apply(data, transaction);
        data.progress = progress;
    }

    @Override
    public void sync(APTMultiblockData data, TransactionContext transaction) {
        super.sync(data, transaction);
        progress = data.progress;
    }

    @Override
    public void load(ValueInput input) {
        super.load(input);
        progress = input.getIntOr(SerializationConstants.PROGRESS, 0);
    }

    @Override
    public void save(ValueOutput output) {
        super.save(output);
        output.putInt(SerializationConstants.PROGRESS, progress);
    }
}
