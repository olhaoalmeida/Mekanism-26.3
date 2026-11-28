package fr.iglee42.evolvedmekanism;

import com.mojang.logging.LogUtils;
import fr.iglee42.emgenerators.client.EMGenClientRegistration;
import fr.iglee42.emgenerators.registries.*;
import fr.iglee42.emtools.client.EMToolsClientRegistration;
import fr.iglee42.emtools.config.EvolvedMekanismToolsConfig;
import fr.iglee42.emtools.registries.EMToolsArmorMaterials;
import fr.iglee42.emtools.registries.EMToolsItems;
import fr.iglee42.emtools.registries.EMToolsTags;
import fr.iglee42.emtools.utils.EMMobEquipmentHelper;
import fr.iglee42.evolvedmekanism.config.EMConfig;
import fr.iglee42.evolvedmekanism.interfaces.InitializableEnum;
import fr.iglee42.evolvedmekanism.inventory.personalstorage.TieredPersonalStorageManager;
import fr.iglee42.evolvedmekanism.multiblock.EMBuilders;
import fr.iglee42.evolvedmekanism.multiblock.apt.APTCache;
import fr.iglee42.evolvedmekanism.multiblock.apt.APTMultiblockData;
import fr.iglee42.evolvedmekanism.multiblock.apt.APTValidator;
import fr.iglee42.evolvedmekanism.network.EMPacketHandler;
import fr.iglee42.evolvedmekanism.registries.*;
import fr.iglee42.evolvedmekanism.tiers.EMBaseTier;
import fr.iglee42.evolvedmekanism.utils.ModsCompats;
import fr.iglee42.evolvedmekanism.utils.MoltenAvailability;
import mekanism.api.text.EnumColor;
import mekanism.api.tier.AlloyTier;
import mekanism.api.tier.BaseTier;
import mekanism.common.MekanismLang;
import mekanism.common.command.builders.BuildCommand;
import mekanism.common.content.blocktype.FactoryType;
import mekanism.common.lib.Version;
import mekanism.common.lib.multiblock.MekanismMultiblockRegistry;
import mekanism.common.lib.multiblock.MultiblockType;
import mekanism.common.registration.impl.ItemRegistryObject;
import mekanism.common.registries.MekanismItems;
import mekanism.common.tier.*;
import mekanism.common.util.MekanismUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.util.StorageUtils;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.slf4j.Logger;

@Mod(EvolvedMekanism.MODID)
public class EvolvedMekanism {

    public static final String MODID = "evolvedmekanism";
    public static final Logger logger = LogUtils.getLogger();
    public static final String MOD_NAME = "EvolvedMekanism";

    public static EvolvedMekanism instance;

    private final EMPacketHandler packetHandler;

    public final Version versionNumber;

    private static final MekanismMultiblockRegistry MULTIBLOCKS = new MekanismMultiblockRegistry(MODID);
    public static final MultiblockType<APTMultiblockData> APT_MULTIBLOCK = MULTIBLOCKS.registerMultiblock("apt", APTCache::new, APTValidator::new);

    public EvolvedMekanism(IEventBus modEventBus, ModContainer container) {
        logger.info("Evolved Mekanism Launched");
        instance = this;
        initEnums();
        MULTIBLOCKS.register(modEventBus);
        EMConfig.registerConfigs(container);
        if (ModsCompats.MEKANISMTOOLS.isLoaded()) EvolvedMekanismToolsConfig.registerConfigs(container);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::enqueueIMC);
        modEventBus.addListener(EventPriority.LOWEST, this::hideUnavailableMoltenTanks);

        EMBlocks.BLOCKS.register(modEventBus);
        EMBlocks.BLOCKS_NO_ITEMS.register(modEventBus);
        EMItems.ITEMS.register(modEventBus);
        EMCreativeTabs.CREATIVE_TABS.register(modEventBus);
        EMTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        EMChemicals.CHEMICALS.register(modEventBus);
        EMContainerTypes.CONTAINER_TYPES.register(modEventBus);
        EMLootFunctions.REGISTER.register(modEventBus);
        EMRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        EMModules.MODULES.register(modEventBus);
        EMDataComponents.DATA_COMPONENTS.register(modEventBus);
        EMFluids.FLUIDS.register(modEventBus);
        EMParticleTypes.PARTICLES.register(modEventBus);
        EMHeightProviderTypes.HEIGHT_PROVIDER_TYPES.register(modEventBus);
        EMIntProviderTypes.INT_PROVIDER_TYPES.register(modEventBus);
        EMPlacementModifiers.PLACEMENT_MODIFIERS.register(modEventBus);
        EMFeatures.FEATURES.register(modEventBus);
        if (ModsCompats.MEKANISMTOOLS.isLoaded())EMToolsArmorMaterials.ARMOR_MATERIALS.register(modEventBus);

        registerCompats(modEventBus);

        //NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.addListener(this::serverStopped);

        modEventBus.addListener(EMConfig::onConfigLoad);
        if (ModsCompats.MEKANISMTOOLS.isLoaded())modEventBus.addListener(EvolvedMekanismToolsConfig::onConfigLoad);

        if (ModsCompats.MEKANISMTOOLS.isLoaded()) NeoForge.EVENT_BUS.addListener(EMMobEquipmentHelper::onLivingSpecialSpawn);

        versionNumber = new Version(container);
        packetHandler = new EMPacketHandler(modEventBus);
        // TODO(port): datagen disabled
    }

    private void registerCompats(IEventBus modEventBus) {

        if (ModsCompats.MEKANISMGENERATORS.isLoaded()) {
           EMGenItems.register(modEventBus);
           EMGenBlocks.register(modEventBus);
           EMGenTileEntityTypes.register(modEventBus);
           EMGenContainerTypes.register(modEventBus);
           EMGenBlockTypes.register();
           if (FMLEnvironment.getDist() == Dist.CLIENT) modEventBus.register(new EMGenClientRegistration());
        }

        if (ModsCompats.MEKANISMTOOLS.isLoaded()) {
            EMToolsItems.register(modEventBus);
            if (FMLEnvironment.getDist() == Dist.CLIENT) modEventBus.register(new EMToolsClientRegistration());
        }

        if (ModsCompats.CURIOS.isLoaded()) {
            // TODO(port): curios compat disabled
        }
    }

    private void serverStopped(ServerStoppedEvent event) {
        TieredPersonalStorageManager.reset();
    }

    public static EMPacketHandler packetHandler() {
        return instance.packetHandler;
    }

    private void initEnums() {
        // TODO(port): enum injection mixins disabled until core compiles

    }

    private void hideUnavailableMoltenTanks(BuildCreativeModeTabContentsEvent event) {
        java.util.ArrayList<ItemStack> hidden = new java.util.ArrayList<>();
        for (ItemStack stack : event.getParentEntries()) {
            if (unavailableMoltenTank(stack, event)) {
                hidden.add(stack);
            }
        }
        for (ItemStack stack : event.getSearchEntries()) {
            if (unavailableMoltenTank(stack, event)) {
                hidden.add(stack);
            }
        }
        for (ItemStack stack : hidden) {
            event.remove(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    private static boolean unavailableMoltenTank(ItemStack stack, BuildCreativeModeTabContentsEvent event) {
        if (!stack.is(MekanismBlocks.CREATIVE_FLUID_TANK.getItemHolder())) {
            return false;
        }
        // TODO(port): reimplement with 26.3 fluid attachment API
        return false;
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        initEnums();
        event.enqueueWork(() -> {
            EMTags.init();
            if (ModsCompats.MEKANISMTOOLS.isLoaded()) EMToolsTags.init();
            BuildCommand.register("apt", EvolvedMekanismLang.APT, new EMBuilders.APTBuilder());
        });
    }

    public static Identifier rl(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    public static Identifier getResource(MekanismUtils.ResourceType type, String name) {
        return rl(type.getPrefix() + name);
    }

    public static boolean isEvolvedMekanismTier(BaseTier tier) {
        return tier.equals(EMBaseTier.OVERCLOCKED) || tier.equals(EMBaseTier.QUANTUM) || tier.equals(EMBaseTier.DENSE)
                || tier.equals(EMBaseTier.MULTIVERSAL) || tier.equals(BaseTier.CREATIVE);
    }

    public static ItemRegistryObject<Item> getCircuitByTier(BaseTier tier) {
        if (tier.equals(BaseTier.BASIC))
            return MekanismItems.BASIC_CONTROL_CIRCUIT;
        if (tier.equals(BaseTier.ADVANCED))
            return MekanismItems.ADVANCED_CONTROL_CIRCUIT;
        if (tier.equals(BaseTier.ELITE))
            return MekanismItems.ELITE_CONTROL_CIRCUIT;
        if (tier.equals(BaseTier.ULTIMATE))
            return MekanismItems.ULTIMATE_CONTROL_CIRCUIT;
        if (tier.equals(EMBaseTier.OVERCLOCKED))
            return EMItems.OVERCLOCKED_CONTROL_CIRCUIT;
        if (tier.equals(EMBaseTier.QUANTUM))
            return EMItems.QUANTUM_CONTROL_CIRCUIT;
        if (tier.equals(EMBaseTier.DENSE))
            return EMItems.DENSE_CONTROL_CIRCUIT;
        if (tier.equals(EMBaseTier.MULTIVERSAL))
            return EMItems.MULTIVERSAL_CONTROL_CIRCUIT;
        if (tier.equals(BaseTier.CREATIVE))
            return EMItems.CREATIVE_CONTROL_CIRCUIT;
        return null;
    }

    private void enqueueIMC(InterModEnqueueEvent event) {
        // TODO(port): MekanismIMC removed in 10.8 (modules are datapack entries now)

    }

    public static Component logFormat(Object message) {
        return logFormat(EnumColor.GRAY,message);
    }

    public static Component logFormat(EnumColor messageColor, Object message) {
        return MekanismLang.LOG_FORMAT.translateColored(EnumColor.DARK_GREEN, EvolvedMekanismLang.MEKANISM_EVOLVED, messageColor, message);
    }
}
