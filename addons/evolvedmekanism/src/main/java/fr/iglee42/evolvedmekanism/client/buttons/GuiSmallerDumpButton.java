package fr.iglee42.evolvedmekanism.client.buttons;

import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.button.MekanismImageButton;
import mekanism.common.network.to_server.PacketGuiInteract;
import mekanism.common.network.to_server.PacketGuiInteract.GuiInteraction;
import mekanism.common.tile.interfaces.IHasDumpButton;
import net.minecraft.world.level.block.entity.BlockEntity;
import mekanism.common.network.PacketUtils;

public class GuiSmallerDumpButton<TILE extends BlockEntity & IHasDumpButton> extends MekanismImageButton {

    protected final TILE tile;

    public GuiSmallerDumpButton(IGuiWrapper gui, TILE tile, int x, int y) {
        super(gui, x, y, 21, 8, EvolvedMekanism.rl("button/dump_small"), (_, _, _) -> PacketUtils.sendToServer(new PacketGuiInteract(GuiInteraction.DUMP_BUTTON, tile)));
        this.tile = tile;
        setButtonBackground(ButtonBackground.NONE);
    }
}
