package com.jerry.mekmm.client.render;

import com.jerry.mekmm.client.renderer.MoreMachineRenderType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;

public class RenderTickHandler {

    @SubscribeEvent
    public void submitCustomGeometry(SubmitCustomGeometryEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        LevelRenderState levelState = event.getLevelRenderState();
        PoseStack poseStack = event.getPoseStack();
        SubmitNodeCollector nodeCollector = event.getSubmitNodeCollector();
        Vec3 cameraPos = levelState.cameraRenderState.pos;
        long gameTime = levelState.gameTime;

        for (BlockPos pos : BlockHighlightManager.getInstance().getHighlightedPositions()) {
            BlockHighlightManager.HighlightData data = BlockHighlightManager.getInstance().getHighlightData(pos);
            if (data != null) {
                poseStack.pushPose();
                poseStack.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);
                float alpha = data.getAlpha(gameTime);
                nodeCollector.submitCustomGeometry(poseStack, MoreMachineRenderType.LINES_NO_DEPTH,
                        (pose, consumer) -> renderLineBox(pose, consumer, data.getRed(), data.getGreen(), data.getBlue(), alpha));
                poseStack.popPose();
            }
        }
    }

    private static void renderLineBox(Pose pose, VertexConsumer consumer, float red, float green, float blue, float alpha) {
        line(pose, consumer, 0, 0, 0, 1, 0, 0, red, green, blue, alpha);
        line(pose, consumer, 1, 0, 0, 1, 0, 1, red, green, blue, alpha);
        line(pose, consumer, 1, 0, 1, 0, 0, 1, red, green, blue, alpha);
        line(pose, consumer, 0, 0, 1, 0, 0, 0, red, green, blue, alpha);
        line(pose, consumer, 0, 1, 0, 1, 1, 0, red, green, blue, alpha);
        line(pose, consumer, 1, 1, 0, 1, 1, 1, red, green, blue, alpha);
        line(pose, consumer, 1, 1, 1, 0, 1, 1, red, green, blue, alpha);
        line(pose, consumer, 0, 1, 1, 0, 1, 0, red, green, blue, alpha);
        line(pose, consumer, 0, 0, 0, 0, 1, 0, red, green, blue, alpha);
        line(pose, consumer, 1, 0, 0, 1, 1, 0, red, green, blue, alpha);
        line(pose, consumer, 1, 0, 1, 1, 1, 1, red, green, blue, alpha);
        line(pose, consumer, 0, 0, 1, 0, 1, 1, red, green, blue, alpha);
    }

    private static void line(Pose pose, VertexConsumer consumer, float x1, float y1, float z1, float x2, float y2, float z2, float red, float green, float blue, float alpha) {
        Matrix4f matrix = pose.pose();
        float normalX = x2 - x1;
        float normalY = y2 - y1;
        float normalZ = z2 - z1;
        float length = (float) Math.sqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
        if (length != 0) {
            normalX /= length;
            normalY /= length;
            normalZ /= length;
        }
        consumer.addVertex(matrix, x1, y1, z1).setColor(red, green, blue, alpha).setNormal(pose, normalX, normalY, normalZ).setLineWidth(2F);
        consumer.addVertex(matrix, x2, y2, z2).setColor(red, green, blue, alpha).setNormal(pose, normalX, normalY, normalZ).setLineWidth(2F);
    }
}
