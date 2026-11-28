package fr.iglee42.emgenerators.client;

import fr.iglee42.emgenerators.registries.EMGenBlocks;
import fr.iglee42.emgenerators.registries.EMGenContainerTypes;
import fr.iglee42.emgenerators.registries.EMGenTileEntityTypes;
import fr.iglee42.emgenerators.tile.TileEntityLunarGenerator;
import fr.iglee42.emgenerators.tile.TileEntityTieredAdvancedLunarGenerator;
import fr.iglee42.emgenerators.tile.TileEntityTieredAdvancedSolarGenerator;
import mekanism.client.ClientRegistrationUtil;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.generators.client.gui.GuiSolarGenerator;
import mekanism.generators.client.render.RenderWindGenerator;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public class EMGenClientRegistration {

    @SubscribeEvent
    public void init(FMLClientSetupEvent event) {
        //TODO - 26.3: ClientRegistration.addCustomModel / ExtensionBakedModel.TransformedBakedModel are gone. The +1 Y lift that was applied to the
        // solar/lunar generator models must now be baked into the model json (or a custom block state model) of the tall solar/lunar generators.
    }

    @SubscribeEvent
    public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        //The tiered wind generators reuse Mekanism's blade renderer (TileEntityTieredWindGenerator extends TileEntityWindGenerator)
        ClientRegistrationUtil.bindTileEntityRenderer(event, _ -> new RenderWindGenerator(),
                EMGenTileEntityTypes.ADVANCED_WIND_GENERATOR,
                EMGenTileEntityTypes.ELITE_WIND_GENERATOR,
                EMGenTileEntityTypes.ULTIMATE_WIND_GENERATOR,
                EMGenTileEntityTypes.OVERCLOCKED_WIND_GENERATOR,
                EMGenTileEntityTypes.QUANTUM_WIND_GENERATOR,
                EMGenTileEntityTypes.DENSE_WIND_GENERATOR,
                EMGenTileEntityTypes.MULTIVERSAL_WIND_GENERATOR,
                EMGenTileEntityTypes.CREATIVE_WIND_GENERATOR);
    }

    @SuppressWarnings("Convert2MethodRef")
    @SubscribeEvent(priority = EventPriority.LOW)
    public void registerContainers(RegisterMenuScreensEvent event) {
        ClientRegistrationUtil.registerScreen(event, EMGenContainerTypes.TIERED_ADVANCED_SOLAR_GENERATOR, (MekanismTileContainer<TileEntityTieredAdvancedSolarGenerator> container, Inventory inv, Component title) -> new GuiSolarGenerator<>(container, inv, title));
        ClientRegistrationUtil.registerScreen(event, EMGenContainerTypes.LUNAR_GENERATOR, (MekanismTileContainer<TileEntityLunarGenerator> container, Inventory inv, Component title) -> new GuiLunarGenerator<>(container, inv, title));
        ClientRegistrationUtil.registerScreen(event, EMGenContainerTypes.TIERED_ADVANCED_LUNAR_GENERATOR, (MekanismTileContainer<TileEntityTieredAdvancedLunarGenerator> container, Inventory inv, Component title) -> new GuiLunarGenerator<>(container, inv, title));
        ClientRegistrationUtil.registerScreen(event, EMGenContainerTypes.TIERED_WIND_GENERATOR, GuiTieredWindGenerator::new);
    }

    @SubscribeEvent
    public void registerClientExtensions(RegisterClientExtensionsEvent event) {
        //Wind generator items use Mekanism's special model renderer via assets/<ns>/items/*.json (type mekanismgenerators:wind_generator_model)
        ClientRegistrationUtil.registerBlockExtensions(event, EMGenBlocks.BLOCKS);
    }

    @SubscribeEvent
    public void onStitch(TextureAtlasStitchedEvent event) {
        if (!event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
            return;
        }
    }
}
