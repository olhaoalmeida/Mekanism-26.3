package fr.iglee42.evolvedmekanism.client;

import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import fr.iglee42.evolvedmekanism.client.gui.*;
import fr.iglee42.evolvedmekanism.client.renderers.RenderAPT;
import fr.iglee42.evolvedmekanism.client.renderers.RenderTieredPersonalChest;
import fr.iglee42.evolvedmekanism.client.renderers.datas.MultipleCustomRenderData;
import fr.iglee42.evolvedmekanism.particles.MoltenPopParticle;
import fr.iglee42.evolvedmekanism.particles.RisingBubbleParticle;
import fr.iglee42.evolvedmekanism.registries.EMBlocks;
import fr.iglee42.evolvedmekanism.registries.EMContainerTypes;
import fr.iglee42.evolvedmekanism.registries.EMFluids;
import fr.iglee42.evolvedmekanism.registries.EMParticleTypes;
import fr.iglee42.evolvedmekanism.registries.EMTileEntityTypes;
import mekanism.client.ClientRegistrationUtil;
import mekanism.client.gui.machine.GuiSolarNeutronActivator;
import mekanism.client.render.tileentity.RenderBin;
import mekanism.client.render.tileentity.RenderEnergyCube;
import mekanism.client.render.tileentity.RenderFluidTank;
import mekanism.client.render.transmitter.*;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.tile.transmitter.LogisticalTransporterBlockTintSource;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;

@EventBusSubscriber(modid = EvolvedMekanism.MODID, value = Dist.CLIENT)
public class ClientRegistration {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(new EMClientTickHandler());
    }

    @SubscribeEvent
    public static void fluidModels(RegisterFluidModelsEvent event) {
        ClientRegistrationUtil.registerFluidModels(event, EMFluids.FLUIDS);
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(EMParticleTypes.RISING_BUBBLE.get(),
                RisingBubbleParticle.Factory::new);
        event.registerSpriteSet(EMParticleTypes.MOLTEN_POP.get(),
                MoltenPopParticle.Factory::new);
    }

    @SubscribeEvent
    public static void registerKeybindings(RegisterKeyMappingsEvent event) {
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiLayersEvent event) {
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        //Register entity rendering handlers

        //Register TileEntityRenderers

        ClientRegistrationUtil.bindTileEntityRenderer(event, RenderBin::new, EMTileEntityTypes.OVERCLOCKED_BIN, EMTileEntityTypes.QUANTUM_BIN, EMTileEntityTypes.DENSE_BIN,
              EMTileEntityTypes.MULTIVERSAL_BIN);
       ClientRegistrationUtil.bindTileEntityRenderer(event, _ -> new RenderEnergyCube(), EMTileEntityTypes.OVERCLOCKED_ENERGY_CUBE, EMTileEntityTypes.QUANTUM_ENERGY_CUBE,
             EMTileEntityTypes.DENSE_ENERGY_CUBE, EMTileEntityTypes.MULTIVERSAL_ENERGY_CUBE);
       ClientRegistrationUtil.bindTileEntityRenderer(event, _ -> new RenderFluidTank(), EMTileEntityTypes.OVERCLOCKED_FLUID_TANK, EMTileEntityTypes.QUANTUM_FLUID_TANK,
               EMTileEntityTypes.DENSE_FLUID_TANK, EMTileEntityTypes.MULTIVERSAL_FLUID_TANK);
        //Transmitters
       ClientRegistrationUtil.bindTileEntityRenderer(event, RenderLogisticalTransporter::new, EMTileEntityTypes.OVERCLOCKED_LOGISTICAL_TRANSPORTER, EMTileEntityTypes.QUANTUM_LOGISTICAL_TRANSPORTER,
             EMTileEntityTypes.DENSE_LOGISTICAL_TRANSPORTER, EMTileEntityTypes.MULTIVERSAL_LOGISTICAL_TRANSPORTER,EMTileEntityTypes.CREATIVE_LOGISTICAL_TRANSPORTER);
       ClientRegistrationUtil.bindTileEntityRenderer(event, _ -> new RenderMechanicalPipe(), EMTileEntityTypes.OVERCLOCKED_MECHANICAL_PIPE,
             EMTileEntityTypes.QUANTUM_MECHANICAL_PIPE, EMTileEntityTypes.DENSE_MECHANICAL_PIPE, EMTileEntityTypes.MULTIVERSAL_MECHANICAL_PIPE,EMTileEntityTypes.CREATIVE_MECHANICAL_PIPE);
       ClientRegistrationUtil.bindTileEntityRenderer(event, _ -> new RenderPressurizedTube(), EMTileEntityTypes.OVERCLOCKED_PRESSURIZED_TUBE,
             EMTileEntityTypes.QUANTUM_PRESSURIZED_TUBE, EMTileEntityTypes.DENSE_PRESSURIZED_TUBE, EMTileEntityTypes.MULTIVERSAL_PRESSURIZED_TUBE,EMTileEntityTypes.CREATIVE_PRESSURIZED_TUBE);
       ClientRegistrationUtil.bindTileEntityRenderer(event, _ -> new RenderUniversalCable(), EMTileEntityTypes.OVERCLOCKED_UNIVERSAL_CABLE,
             EMTileEntityTypes.QUANTUM_UNIVERSAL_CABLE, EMTileEntityTypes.DENSE_UNIVERSAL_CABLE, EMTileEntityTypes.MULTIVERSAL_UNIVERSAL_CABLE,EMTileEntityTypes.CREATIVE_UNIVERSAL_CABLE);
       ClientRegistrationUtil.bindTileEntityRenderer(event, _ -> new RenderThermodynamicConductor(), EMTileEntityTypes.OVERCLOCKED_THERMODYNAMIC_CONDUCTOR,
             EMTileEntityTypes.QUANTUM_THERMODYNAMIC_CONDUCTOR, EMTileEntityTypes.DENSE_THERMODYNAMIC_CONDUCTOR, EMTileEntityTypes.MULTIVERSAL_THERMODYNAMIC_CONDUCTOR, EMTileEntityTypes.CREATIVE_THERMODYNAMIC_CONDUCTOR);
        ClientRegistrationUtil.bindTileEntityRenderer(event,RenderTieredPersonalChest::new, EMTileEntityTypes.ADVANCED_PERSONAL_CHEST,
                EMTileEntityTypes.ELITE_PERSONAL_CHEST, EMTileEntityTypes.ULTIMATE_PERSONAL_CHEST, EMTileEntityTypes.OVERCLOCKED_PERSONAL_CHEST,
                EMTileEntityTypes.QUANTUM_PERSONAL_CHEST, EMTileEntityTypes.DENSE_PERSONAL_CHEST, EMTileEntityTypes.MULTIVERSAL_PERSONAL_CHEST,EMTileEntityTypes.CREATIVE_PERSONAL_CHEST);

        ClientRegistrationUtil.bindTileEntityRenderer(event, _ -> new RenderAPT(), EMTileEntityTypes.APT_CASING, EMTileEntityTypes.APT_PORT);
    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {

    }


    @SubscribeEvent(priority = EventPriority.LOW)
    public static void registerContainers(RegisterMenuScreensEvent event) {
            ClientRegistrationUtil.registerScreen(event,EMContainerTypes.TIERED_PERSONAL_STORAGE_ITEM, GuiTieredPersonalStorageItem::new);
            ClientRegistrationUtil.registerScreen(event,EMContainerTypes.TIERED_PERSONAL_STORAGE_BLOCK, GuiTieredPersonalStorageTile::new);
            ClientRegistrationUtil.registerScreen(event,EMContainerTypes.ALLOYER, GuiAlloyer::new);
            ClientRegistrationUtil.registerScreen(event,EMContainerTypes.FLUID_COMBINER, GuiFluidCombiner::new);
            ClientRegistrationUtil.registerScreen(event,EMContainerTypes.FLUID_ALLOYER, GuiFluidAlloyer::new);
            ClientRegistrationUtil.registerScreen(event,EMContainerTypes.CHEMIXER, GuiChemixer::new);
            ClientRegistrationUtil.registerScreen(event,EMContainerTypes.APT, GuiAPT::new);
            ClientRegistrationUtil.registerScreen(event,EMContainerTypes.MELTER, GuiMelter::new);
            ClientRegistrationUtil.registerScreen(event,EMContainerTypes.SOLIDIFIER, GuiSolidifier::new);
            ClientRegistrationUtil.registerScreen(event, EMContainerTypes.LUNAR_NEUTRON_ACTIVATOR, GuiLunarNeutronActivator::new);
            ClientRegistrationUtil.registerScreen(event, EMContainerTypes.LASER_DISENCHANTER, GuiLaserDisenchanter::new);
            ClientRegistrationUtil.registerScreen(event, EMContainerTypes.ENCHANTING_STATION, GuiEnchantingStation::new);
            ClientRegistrationUtil.registerScreen(event, EMContainerTypes.SORTIFICHANTOR, GuiEnchantmentSorter::new);

    }




    @SubscribeEvent
    public static void registerBlockColorHandlers(RegisterColorHandlersEvent.BlockTintSources event) {
        ClientRegistrationUtil.registerBlockColorHandler(event, state -> {
                  mekanism.api.tier.BaseTier tier = Attribute.getBaseTier(state.getBlock());
                  return tier == null ? CommonColors.WHITE : tier.getPackedColor();
              }, EMBlocks.OVERCLOCKED_FLUID_TANK, EMBlocks.QUANTUM_FLUID_TANK, EMBlocks.DENSE_FLUID_TANK, EMBlocks.MULTIVERSAL_FLUID_TANK);
        ClientRegistrationUtil.registerBlockColorHandler(event, LogisticalTransporterBlockTintSource.INSTANCE, EMBlocks.OVERCLOCKED_LOGISTICAL_TRANSPORTER,
              EMBlocks.QUANTUM_LOGISTICAL_TRANSPORTER, EMBlocks.DENSE_LOGISTICAL_TRANSPORTER, EMBlocks.MULTIVERSAL_LOGISTICAL_TRANSPORTER,
              EMBlocks.CREATIVE_LOGISTICAL_TRANSPORTER);
    }

    //TODO - 26.3: bucket and fluid tank item tinting is now data driven (assets/<ns>/items/*.json with tint sources), no RegisterColorHandlersEvent.Item anymore

    @SubscribeEvent
    public static void registerItemDecorations(RegisterItemDecorationsEvent event) {
        for (BlockRegistryObject<?, ?> block : new BlockRegistryObject<?, ?>[]{
              EMBlocks.OVERCLOCKED_PRESSURIZED_TUBE, EMBlocks.QUANTUM_PRESSURIZED_TUBE, EMBlocks.DENSE_PRESSURIZED_TUBE, EMBlocks.MULTIVERSAL_PRESSURIZED_TUBE,
              EMBlocks.CREATIVE_PRESSURIZED_TUBE, EMBlocks.OVERCLOCKED_THERMODYNAMIC_CONDUCTOR, EMBlocks.QUANTUM_THERMODYNAMIC_CONDUCTOR,
              EMBlocks.DENSE_THERMODYNAMIC_CONDUCTOR, EMBlocks.MULTIVERSAL_THERMODYNAMIC_CONDUCTOR, EMBlocks.CREATIVE_THERMODYNAMIC_CONDUCTOR,
              EMBlocks.OVERCLOCKED_UNIVERSAL_CABLE, EMBlocks.QUANTUM_UNIVERSAL_CABLE, EMBlocks.DENSE_UNIVERSAL_CABLE, EMBlocks.MULTIVERSAL_UNIVERSAL_CABLE,
              EMBlocks.CREATIVE_UNIVERSAL_CABLE}) {
            event.register(block, new TransmitterIconDecorator(block.getId()));
        }
    }

    /**
     * Mirrors Mekanism's TransmitterTypeDecorator (private constructor) using this mod's namespace for the icon sprite.
     */
    private static final class TransmitterIconDecorator implements IItemDecorator {

        private final Identifier texture;

        private TransmitterIconDecorator(Identifier blockId) {
            this.texture = Identifier.fromNamespaceAndPath(EvolvedMekanism.MODID, "transmitter_icon/" + blockId.getPath());
        }

        @Override
        public boolean render(GuiGraphicsExtractor guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, xOffset, yOffset, 16, 16);
            return true;
        }
    }

    @SubscribeEvent
    public static void onStitch(TextureAtlasStitchedEvent event) {
        if (!event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
            return;
        }
        CustomModelRenderer.resetCachedModels();
        MultipleCustomRenderData.clearCaches();
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        //Energy cube and fluid tank items are rendered through special models declared in assets/<ns>/items/*.json
        ClientRegistrationUtil.registerBlockExtensions(event, EMBlocks.BLOCKS);
        EMFluidExtensions.register(event);
    }

}
