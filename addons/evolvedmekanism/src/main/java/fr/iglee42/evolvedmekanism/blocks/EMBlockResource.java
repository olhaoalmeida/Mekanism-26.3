package fr.iglee42.evolvedmekanism.blocks;

import fr.iglee42.evolvedmekanism.registries.EMBlockResourceInfo;
import mekanism.common.block.BlockMekanism;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class EMBlockResource extends BlockMekanism {

    private final EMBlockResourceInfo resource;

    public EMBlockResource(EMBlockResourceInfo resource, BlockBehaviour.Properties properties) {
        super(resource.modifyProperties(properties.requiresCorrectToolForDrops()));
        this.resource = resource;
    }

    public EMBlockResourceInfo getResourceInfo() {
        return resource;
    }
}
