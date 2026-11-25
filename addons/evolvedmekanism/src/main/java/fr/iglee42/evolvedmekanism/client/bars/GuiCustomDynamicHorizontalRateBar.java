package fr.iglee42.evolvedmekanism.client.bars;

import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.bar.GuiBar;
import mekanism.client.gui.element.bar.GuiBar.IBarInfoHandler;
import mekanism.client.render.MekanismRenderPipelines;
import mekanism.common.Mekanism;
import mekanism.common.lib.Color.ColorFunction;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

//Copied from mekanism's code but removed the final modifier on color function
public class GuiCustomDynamicHorizontalRateBar extends GuiBar<IBarInfoHandler> {

    private static final Identifier RATE_BAR = Mekanism.rl("bar/dynamic_rate");

    private ColorFunction colorFunction;

    public GuiCustomDynamicHorizontalRateBar(IGuiWrapper gui, IBarInfoHandler handler, int x, int y, int width) {
        this(gui, handler, x, y, width, ColorFunction.HEAT);
    }

    public GuiCustomDynamicHorizontalRateBar(IGuiWrapper gui, IBarInfoHandler handler, int x, int y, int width, ColorFunction colorFunction) {
        super(gui, handler, x, y, width, 8, true);
        this.colorFunction = colorFunction;
    }

    @Override
    protected void renderBarContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks, double handlerLevel) {
        int barWidth = width - 2;
        int barHeight = height - 2;
        int targetWidth = calculateSize(handlerLevel, barWidth);
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, RATE_BAR, relativeX + 1, relativeY + 1, targetWidth, barHeight);
        for (int i = 0; i < targetWidth; i++) {
            int x0 = relativeX + 1 + i;
            int y0 = relativeY + 1;
            guiGraphics.fill(MekanismRenderPipelines.GUI_DST_COLOR, x0, y0, x0 + 1, y0 + barHeight, colorFunction.getColor(i / (float) barWidth).argb());
        }
    }

    public void setColorFunction(ColorFunction colorFunction) {
        this.colorFunction = colorFunction;
    }
}
