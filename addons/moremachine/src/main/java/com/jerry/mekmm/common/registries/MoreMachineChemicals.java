package com.jerry.mekmm.common.registries;

import com.jerry.mekmm.common.MoreMachineChemicalConstants;
import com.jerry.mekmm.common.resource.MoreMachineResource;
import com.jerry.mekmm.common.util.MoreMachineEnumUtils;
import com.jerry.mekmm.Mekmm;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import mekanism.api.MekanismRegistries;
import mekanism.api.chemical.Chemical;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;

/**
 * Chemicals are datapack entries in Mekanism 10.8+: their definitions live in
 * {@code data/mekmm/mekanism/chemical/*.json}, code only references them by key.
 */
public class MoreMachineChemicals {

    private MoreMachineChemicals() {}

    public static final ResourceKey<Chemical> NUTRITIONAL_PASTE = MoreMachineChemicalConstants.NUTRITIONAL_PASTE.key();
    public static final ResourceKey<Chemical> NUTRIENT_SOLUTION = MoreMachineChemicalConstants.NUTRIENT_SOLUTION.key();
    public static final ResourceKey<Chemical> UU_MATTER = MoreMachineChemicalConstants.UU_MATTER.key();
    public static final ResourceKey<Chemical> UNSTABLE_DIMENSIONAL_GAS = MoreMachineChemicalConstants.UNSTABLE_DIMENSIONAL_GAS.key();

    public record CleanDirtySlurryKeys(ResourceKey<Chemical> clean, ResourceKey<Chemical> dirty) {}

    public static final Map<MoreMachineResource, CleanDirtySlurryKeys> PROCESSED_RESOURCES;

    static {
        Map<MoreMachineResource, CleanDirtySlurryKeys> slurries = new LinkedHashMap<>();
        for (MoreMachineResource resource : MoreMachineEnumUtils.MM_RESOURCES) {
            String name = resource.getRegistrySuffix();
            slurries.put(resource, new CleanDirtySlurryKeys(key("clean_" + name), key("dirty_" + name)));
        }
        PROCESSED_RESOURCES = Collections.unmodifiableMap(slurries);
    }

    private static ResourceKey<Chemical> key(String name) {
        return ResourceKey.create(MekanismRegistries.Keys.CHEMICAL, Mekmm.rl(name));
    }

    public static mekanism.api.chemical.ChemicalStack stack(HolderLookup.Provider registries, ResourceKey<Chemical> key, int amount) {
        return new mekanism.api.chemical.ChemicalStack(holder(registries, key), amount);
    }

    public static Holder<Chemical> holder(HolderLookup.Provider registries, ResourceKey<Chemical> key) {
        return registries.lookupOrThrow(MekanismRegistries.Keys.CHEMICAL).getOrThrow(key);
    }
}
