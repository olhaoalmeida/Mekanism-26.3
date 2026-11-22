package com.jerry.mekmm.client.renderer;

import com.jerry.mekmm.Mekmm;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;

public class MoreMachineRenderType {

    private static final RenderPipeline LINES_NO_DEPTH_PIPELINE = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
            .withLocation(Mekmm.rl("pipeline/lines_no_depth"))
            // TODO(26.3 port): original disabled depth testing (withDepthStencilState(Optional.empty())); renderpearl DepthStencilState API not verified, so the LINES_SNIPPET default is kept.
            .build();

    public static final RenderType LINES_NO_DEPTH = RenderType.create(
            "lines_no_depth",
            RenderSetup.builder(LINES_NO_DEPTH_PIPELINE)
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .createRenderSetup());
}
