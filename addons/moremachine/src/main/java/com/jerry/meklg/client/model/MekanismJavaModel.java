package com.jerry.meklg.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Local replacement for the Mekanism helper that was removed in 26.3. Wraps a baked {@link ModelPart} tree and submits it via the node collector.
 */
public abstract class MekanismJavaModel<STATE> {

    protected final ModelPart root;

    protected MekanismJavaModel(ModelPart root) {
        this.root = root;
    }

    protected static LayerDefinition createLayerDefinition(int textureWidth, int textureHeight, ModelPartData... parts) {
        MeshDefinition mesh = new MeshDefinition();
        for (ModelPartData part : parts) {
            part.addToDefinition(mesh.getRoot());
        }
        return LayerDefinition.create(mesh, textureWidth, textureHeight);
    }

    protected static List<ModelPart> getRenderableParts(ModelPart root, ModelPartData... parts) {
        List<ModelPart> list = new ArrayList<>(parts.length);
        for (ModelPartData part : parts) {
            list.add(part.getFromRoot(root));
        }
        return list;
    }

    public void setupAnim(STATE state) {
    }

    public abstract void collect(STATE state, PoseStack poseStack, SubmitNodeCollector collector, int light, int overlayLight, boolean hasEffect);

    protected void collectParts(List<ModelPart> parts, PoseStack poseStack, RenderType renderType, SubmitNodeCollector collector, int light, int overlayLight, int color,
          ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling, boolean hasEffect) {
        for (ModelPart part : parts) {
            collector.submitModelPart(part, poseStack, renderType, light, overlayLight, null, color);
        }
    }

    protected void renderPartsAsWireFrame(List<ModelPart> parts, PoseStack poseStack, VertexConsumer buffer, boolean isHighContrast) {
        float red = isHighContrast ? 1F : 0F;
        float green = isHighContrast ? 1F : 0.8F;
        float blue = isHighContrast ? 1F : 1F;
        for (ModelPart part : parts) {
            part.visit(poseStack, (pose, path, index, cube) -> {
                for (ModelPart.Polygon polygon : cube.polygons) {
                    var vertices = polygon.vertices();
                    for (int i = 0; i < vertices.length; i++) {
                        var a = vertices[i];
                        var b = vertices[(i + 1) % vertices.length];
                        float nx = b.worldX() - a.worldX();
                        float ny = b.worldY() - a.worldY();
                        float nz = b.worldZ() - a.worldZ();
                        buffer.addVertex(pose, a.worldX(), a.worldY(), a.worldZ()).setColor(red, green, blue, 0.6F).setNormal(pose, nx, ny, nz);
                        buffer.addVertex(pose, b.worldX(), b.worldY(), b.worldZ()).setColor(red, green, blue, 0.6F).setNormal(pose, nx, ny, nz);
                    }
                }
            });
        }
    }
}
