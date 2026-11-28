package fr.iglee42.evolvedmekanism.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;

public class RisingBubbleParticle extends SingleQuadParticle {

    protected RisingBubbleParticle(ClientLevel level, double x, double y, double z,
                                   double vx, double vy, double vz, float r, float g, float b, TextureAtlasSprite sprite) {
        super(level, x, y, z, vx, vy, vz, sprite);
        this.lifetime = 20;
        this.gravity = -0.02F;
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        this.scale(0.2F + level.random.nextFloat());
        this.setColor(lighten(r, 0.05f), lighten(g, 0.05f), lighten(b, 0.05f));
    }

    public static float lighten(float value, float factor) {
        return value + (1.0f - value) * factor;
    }

    @Override
    public void tick() {
        super.tick();
        this.yd += 0.002;
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Override
    public int getLightCoords(float partialTick) {
        return LightCoordsUtil.pack(15, 15);
    }

    public static class Factory implements ParticleProvider<ColoredRisingBubbleOptions> {
        private final SpriteSet sprites;

        public Factory(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(ColoredRisingBubbleOptions type, ClientLevel level,
                                       double x, double y, double z,
                                       double vx, double vy, double vz, RandomSource random) {
            return new RisingBubbleParticle(level, x, y, z, vx, vy, vz, type.red(), type.green(), type.blue(), sprites.get(random));
        }
    }
}
