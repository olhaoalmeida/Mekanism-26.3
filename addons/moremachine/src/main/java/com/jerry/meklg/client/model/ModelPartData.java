package com.jerry.meklg.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;

import java.util.List;

/**
 * Local replacement for the removed Mekanism helper: describes a named model part with optional children.
 */
public class ModelPartData {

    private final String name;
    private final CubeListBuilder cubes;
    private final PartPose pose;
    private final List<ModelPartData> children;

    public ModelPartData(String name, CubeListBuilder cubes, PartPose pose, ModelPartData... children) {
        this.name = name;
        this.cubes = cubes;
        this.pose = pose;
        this.children = List.of(children);
    }

    public String name() {
        return name;
    }

    public void addToDefinition(PartDefinition parent) {
        PartDefinition self = parent.addOrReplaceChild(name, cubes, pose);
        for (ModelPartData child : children) {
            child.addToDefinition(self);
        }
    }

    public ModelPart getFromRoot(ModelPart root) {
        return root.getChild(name);
    }
}
