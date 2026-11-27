package fr.iglee42.evolvedmekanism.registries;

import mekanism.common.resource.IResource;
import mekanism.common.resource.ResourceType;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.Nullable;

public enum EMResources implements IResource {
    BETTER_GOLD("better_gold"),
    PLASLITHERITE("plaslitherite"),
    REFINED_REDSTONE("refined_redstone"),
    NOCTIS_ROZULI("noctis_rozuli"),
    ;

    private final String registrySuffix;

    EMResources(String registrySuffix) {
        this.registrySuffix = registrySuffix;
    }

    @Override
    public String getRegistrySuffix() {
        return registrySuffix;
    }

    @Override
    public Item.Properties modifyProperties(Item.Properties properties, @Nullable ResourceType resourceType) {
        return properties;
    }
}
