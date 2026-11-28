package fr.iglee42.emgenerators.client;

import fr.iglee42.emgenerators.tiers.AdvancedWindGeneratorTier;
import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import net.minecraft.resources.Identifier;

public final class WindModelTexture {

    private static Identifier current;

    private WindModelTexture() {
    }

    public static Identifier forTier(AdvancedWindGeneratorTier tier) {
        return Identifier.fromNamespaceAndPath(EvolvedMekanism.MODID, "render/wind_generators/" + tier.getBaseTier().getLowerName() + "_wind_generator.png");
    }

    public static void set(Identifier texture) {
        current = texture;
    }

    public static void clear() {
        current = null;
    }

    public static Identifier current() {
        return current;
    }
}
