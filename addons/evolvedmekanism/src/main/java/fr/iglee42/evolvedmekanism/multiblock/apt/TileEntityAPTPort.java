package fr.iglee42.evolvedmekanism.multiblock.apt;

import fr.iglee42.evolvedmekanism.EvolvedMekanismLang;
import fr.iglee42.evolvedmekanism.registries.EMBlocks;
import mekanism.api.IContentsListener;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.text.EnumColor;
import mekanism.common.capabilities.holder.container.IContainerHolder;
import mekanism.common.capabilities.holder.container.ProxiedContainerHolder;
import mekanism.common.capabilities.holder.single.ISingleContainerHolder;
import mekanism.common.capabilities.holder.single.ProxiedSingleContainerHolder;
import mekanism.common.component.containers.type.ContainerType;
import mekanism.common.component.containers.type.IContainerType;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.util.text.BooleanStateDisplay.InputOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityAPTPort extends TileEntityAPTCasing {

    public TileEntityAPTPort(BlockPos pos, BlockState state) {
        super(EMBlocks.APT_PORT, pos, state);
        delaySupplier = NO_DELAY;
    }

    @Override
    protected ISingleContainerHolder<IEnergyContainer> getInitialEnergyContainer(IContentsListener listener) {
        return ProxiedSingleContainerHolder.energy(_ -> true, _ -> true, _ -> getMultiblock().getEnergyContainer());
    }

    @Override
    public IContainerHolder<IChemicalTank> getInitialChemicalTanks(IContentsListener listener) {
        return new ProxiedContainerHolder<>(_ -> true, _ -> true, _ -> getMultiblock().getChemicalTanks());
    }

    //Note: Inventory slots are handled by the default TileEntityMultiblock implementation

    @Override
    public boolean persists(IContainerType<?, ?> type) {
        // Item type is not required because it is supported by the default
        if (type == ContainerType.CHEMICAL || type == ContainerType.ENERGY) {
            return false;
        }
        return super.persists(type);
    }

    @Override
    public InteractionResult onSneakRightClick(Level level, Player player) {
        boolean oldMode = getActive();
        setActive(!oldMode);
        player.sendOverlayMessage(EvolvedMekanismLang.APT_PORT_MODE.translateColored(EnumColor.GRAY, InputOutput.of(oldMode, true)));
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public int getRedstoneLevel() {
        return getMultiblock().getCurrentRedstoneLevel();
    }

    //Methods relating to IComputerTile
    @ComputerMethod(methodDescription = "true -> output, false -> input.")
    boolean getMode() {
        return getActive();
    }

    @ComputerMethod(methodDescription = "true -> output, false -> input.")
    void setMode(boolean output) {
        setActive(output);
    }
    //End methods IComputerTile
}
