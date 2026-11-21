package com.jerry.mekmm.common;


import com.jerry.mekmm.Mekmm;
import mekanism.api.chemical.Chemical;
import mekanism.api.MekanismRegistries;
import mekanism.common.base.IChemicalConstant;
import net.minecraft.resources.ResourceKey;

public enum MoreMachineChemicalConstants implements IChemicalConstant {

    NUTRITIONAL_PASTE("nutritional_paste", 0xFFEB6CA3, 0, 284F, 1_254F, 1_000),
    NUTRIENT_SOLUTION("nutrient_solution", 0xFF265F0C, 0, 300F, 1_513F, 1_000),
    UU_MATTER("uu_matter", 0xFF530570, 0, 300F, 1000F, 1_000),
    UNSTABLE_DIMENSIONAL_GAS("unstable_dimensional_gas", 0xFFE19427, 0, 10.24F, 84.65F, 10);

    private final ResourceKey<Chemical> key;
    private final int color;
    private final int lightLevel;
    private final float temperature;
    private final float density;
    private final int viscosity;

    MoreMachineChemicalConstants(String name, int color, int lightLevel, float temperature, float density, int viscosity) {
        this.key = ResourceKey.create(MekanismRegistries.Keys.CHEMICAL, Mekmm.rl(name));
        this.color = color;
        this.lightLevel = lightLevel;
        this.temperature = temperature;
        this.density = density;
        this.viscosity = viscosity;
    }

    @Override
    public ResourceKey<Chemical> key() {
        return key;
    }

    @Override
    public int color() {
        return color;
    }

    @Override
    public int lightLevel() {
        return lightLevel;
    }

    @Override
    public float temperature() {
        return temperature;
    }

    @Override
    public float density() {
        return density;
    }

    @Override
    public int viscosity() {
        return viscosity;
    }
}
