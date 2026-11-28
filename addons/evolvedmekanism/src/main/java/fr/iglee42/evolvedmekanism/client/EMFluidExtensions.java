package fr.iglee42.evolvedmekanism.client;

import fr.iglee42.evolvedmekanism.registries.EMFluids;
import fr.iglee42.evolvedmekanism.registries.MoltenFluidType;
import mekanism.common.registration.impl.FluidDeferredRegister.MekanismFluidType;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidType;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;

/**
 * Molten fluids keep Mekanism's tinted textures (now provided through the fluid models, see
 * {@link ClientRegistration#fluidModels}), but the in-fluid fog matches lava's distances
 * and uses the molten color. Other fluids stay on Mekanism's water fog.
 */
public final class EMFluidExtensions {

    private EMFluidExtensions() {
    }

    public static void register(RegisterClientExtensionsEvent event) {
        for (Holder<FluidType> entry : EMFluids.FLUIDS.getFluidTypeEntries()) {
            if (entry.value() instanceof MekanismFluidType fluidType) {
                event.registerFluidType(fluidType instanceof MoltenFluidType ? molten(fluidType) : chemical(fluidType), fluidType);
            }
        }
    }

    private static void applyFogColor(MekanismFluidType fluidType, Vector4f fluidFogColor) {
        //Note: Similarly to vanilla we don't bother applying a different value for alpha
        fluidFogColor.set(ARGB.redFloat(fluidType.color), ARGB.greenFloat(fluidType.color), ARGB.blueFloat(fluidType.color));
    }

    private static IClientFluidTypeExtensions molten(MekanismFluidType fluidType) {
        return new IClientFluidTypeExtensions() {
            @Nullable
            @Override
            public Identifier getRenderOverlayTexture(Minecraft mc) {
                return null;
            }

            @Override
            public void modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor) {
                applyFogColor(fluidType, fluidFogColor);
            }

            @Override
            public void modifyFogRender(Camera camera, @Nullable FogEnvironment environment, float renderDistance, float partialTick, FogData fog) {
                //Mirrors vanilla's lava fog
                Entity entity = camera.entity();
                float start = 0.25F;
                float end = 1.0F;
                if (entity.isSpectator()) {
                    start = -8.0F;
                    end = renderDistance * 0.5F;
                } else if (entity instanceof LivingEntity living && living.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                    start = 0.0F;
                    end = 3.0F;
                }
                end = Math.min(end, renderDistance);
                fog.environmentalStart = start;
                fog.environmentalEnd = end;
                fog.skyEnd = end;
                fog.cloudEnd = end;
            }
        };
    }

    private static IClientFluidTypeExtensions chemical(MekanismFluidType fluidType) {
        return new IClientFluidTypeExtensions() {
            @Override
            public Identifier getRenderOverlayTexture(Minecraft mc) {
                return fluidType.renderOverlayTexture;
            }

            @Override
            public void modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor) {
                applyFogColor(fluidType, fluidFogColor);
            }

            @Override
            public void modifyFogRender(Camera camera, @Nullable FogEnvironment environment, float renderDistance, float partialTick, FogData fog) {
                //Same as Mekanism's own fluids: water fog distances without any biome/player adjustments
                fog.environmentalStart = camera.attributeProbe().getValue(EnvironmentAttributes.WATER_FOG_START_DISTANCE, partialTick);
                fog.environmentalEnd = camera.attributeProbe().getValue(EnvironmentAttributes.WATER_FOG_END_DISTANCE, partialTick);
                fog.skyEnd = fog.environmentalEnd;
                fog.cloudEnd = fog.environmentalEnd;
            }
        };
    }
}
