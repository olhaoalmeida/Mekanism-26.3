package fr.iglee42.evolvedmekanism.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;

/**
 * Lava's pop, drawn with the flame sprite and tinted by the molten color.
 */
public class MoltenPopParticle extends SingleQuadParticle {

    protected MoltenPopParticle(ClientLevel level, double x, double y, double z, float r, float g, float b, TextureAtlasSprite sprite) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D, sprite);
        this.gravity = 0.75F;
        this.friction = 0.999F;
        this.xd *= 0.8D;
        this.yd *= 0.8D;
        this.zd *= 0.8D;
        this.yd = this.random.nextFloat() * 0.4F + 0.05F;
        this.quadSize *= this.random.nextFloat() * 2.0F + 0.2F;
        this.lifetime = (int) (16.0D / (Math.random() * 0.8D + 0.2D));
        this.setColor(r, g, b);
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Override
    public int getLightCoords(float partialTick) {
        return LightCoordsUtil.withBlock(super.getLightCoords(partialTick), 15);
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        float progress = ((float) this.age + scaleFactor) / (float) this.lifetime;
        return this.quadSize * (1.0F - progress * progress);
    }

    public static class Factory implements ParticleProvider<ColoredMoltenOptions> {
        private final SpriteSet sprites;

        public Factory(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(ColoredMoltenOptions type, ClientLevel level, double x, double y, double z, double vx, double vy, double vz, RandomSource random) {
            return new MoltenPopParticle(level, x, y, z, type.red(), type.green(), type.blue(), this.sprites.get(random));
        }
    }
}
