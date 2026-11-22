package com.jerry.mekmm.common.block;

import com.jerry.mekmm.common.resource.MoreMachineBlockResourceInfo;

import mekanism.common.block.BlockMekanism;

import net.minecraft.world.level.block.state.BlockBehaviour;

import org.jetbrains.annotations.NotNull;

public class BlockMoreMachineResource extends BlockMekanism {

    @NotNull
    private final MoreMachineBlockResourceInfo resource;

    public BlockMoreMachineResource(BlockBehaviour.Properties properties, @NotNull MoreMachineBlockResourceInfo resource) {
        super(resource.modifyProperties(properties.requiresCorrectToolForDrops()));
        this.resource = resource;
    }

    @NotNull
    public MoreMachineBlockResourceInfo getResourceInfo() {
        return resource;
    }
}
