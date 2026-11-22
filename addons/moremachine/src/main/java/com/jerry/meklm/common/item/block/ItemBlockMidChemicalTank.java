package com.jerry.meklm.common.item.block;

import com.jerry.meklm.common.tier.MidChemicalTankTier;
import com.jerry.meklm.common.tile.TileEntityMidChemicalTank;

import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.util.ItemAccessUtils;
import mekanism.common.util.StorageUtils;
import mekanism.common.util.text.TextUtils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemBlockMidChemicalTank extends ItemBlockLargeChemicalTank<TileEntityMidChemicalTank> {

    public ItemBlockMidChemicalTank(BlockTileModel<TileEntityMidChemicalTank, Machine<TileEntityMidChemicalTank>> block, Properties properties) {
        super(block, properties);
    }

    @Override
    public MidChemicalTankTier getTier() {
        return Attribute.getTierNN(getBlock(), MidChemicalTankTier.class);
    }

    // TODO(26.3 port): appendHoverText removed; stored-substance/capacity tooltip needs a TooltipProvider component
}
