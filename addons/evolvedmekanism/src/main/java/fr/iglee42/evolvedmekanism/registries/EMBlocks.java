package fr.iglee42.evolvedmekanism.registries;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import fr.iglee42.evolvedmekanism.blocks.*;
import fr.iglee42.evolvedmekanism.interfaces.EMInputRecipeCache;
import fr.iglee42.evolvedmekanism.interfaces.InitializableEnum;
import fr.iglee42.evolvedmekanism.items.EMItemBlockResource;
import fr.iglee42.evolvedmekanism.items.ItemBlockTieredPersonalStorage;
import fr.iglee42.evolvedmekanism.multiblock.apt.TileEntityAPTCasing;
import fr.iglee42.evolvedmekanism.multiblock.apt.TileEntityAPTPort;
import fr.iglee42.evolvedmekanism.tiers.EMAlloyTier;
import fr.iglee42.evolvedmekanism.tiers.EMFactoryTier;
import fr.iglee42.evolvedmekanism.tiers.PersonalStorageTier;
import fr.iglee42.evolvedmekanism.tiles.TileEntityLunarNeutronActivator;
import fr.iglee42.evolvedmekanism.tiles.TileEntitySuperchargingElement;
import fr.iglee42.evolvedmekanism.tiles.TileEntityTieredPersonalBarrel;
import fr.iglee42.evolvedmekanism.tiles.TileEntityTieredPersonalChest;
import fr.iglee42.evolvedmekanism.tiles.enchantment.TileEntityEnchantingStation;
import fr.iglee42.evolvedmekanism.tiles.enchantment.TileEntityEnchantmentSorter;
import fr.iglee42.evolvedmekanism.tiles.enchantment.TileEntityLaserDisenchanter;
import fr.iglee42.evolvedmekanism.tiles.machine.TileEntityAlloyer;
import fr.iglee42.evolvedmekanism.tiles.machine.TileEntityChemixer;
import fr.iglee42.evolvedmekanism.tiles.machine.TileEntityFluidAlloyer;
import fr.iglee42.evolvedmekanism.tiles.machine.TileEntityFluidCombiner;
import fr.iglee42.evolvedmekanism.tiles.machine.TileEntityMelter;
import fr.iglee42.evolvedmekanism.tiles.machine.TileEntitySolidifier;
import fr.iglee42.evolvedmekanism.utils.EMAttachedSideConfig;
import fr.iglee42.evolvedmekanism.utils.EMOreBlockType;
import fr.iglee42.evolvedmekanism.utils.EMVanillaMekanism;
import mekanism.api.gear.IModuleHelper;
import mekanism.api.tier.AlloyTier;
import mekanism.api.tier.ITier;
import mekanism.common.component.FilterAware;
import mekanism.common.component.component.AttachedEjector;
import mekanism.common.component.component.AttachedSideConfig;
import mekanism.common.component.containers.type.ContainerType;
import mekanism.common.component.containers.chemical.ChemicalTanksBuilder;
import mekanism.common.component.containers.chemical.ComponentBackedChemicalTankTank;
import mekanism.common.component.containers.fluid.ComponentBackedFluidTankFluidTank;
import mekanism.common.component.containers.fluid.FluidTanksBuilder;
import mekanism.common.component.containers.heat.HeatCapacitorBuilder;
import mekanism.common.component.containers.item.ComponentBackedBinInventorySlot;
import mekanism.common.component.containers.item.ItemSlotsBuilder;
import mekanism.common.block.BlockEnergyCube;
import mekanism.common.block.BlockOre;
import mekanism.common.block.attribute.AttributeFactoryType;
import mekanism.common.block.attribute.AttributeTier;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Unit;
import mekanism.common.block.basic.BlockBin;
import mekanism.common.block.basic.BlockFluidTank;
import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.block.prefab.BlockBasicMultiblock;
import mekanism.common.block.prefab.BlockFactoryMachine;
import mekanism.common.block.prefab.BlockFactoryMachine.BlockFactory;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.block.transmitter.*;
import mekanism.common.content.blocktype.*;
import mekanism.common.item.block.*;
import mekanism.common.item.block.machine.ItemBlockFluidTank;
import mekanism.common.item.block.machine.ItemBlockLaserTractorBeam;
import mekanism.common.item.block.transmitter.*;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registries.MekanismBlockTypes;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismDataComponents;
import mekanism.common.resource.BlockResourceInfo;
import mekanism.common.resource.ore.OreType;
import mekanism.common.tier.*;
import mekanism.common.tile.*;
import mekanism.common.tile.factory.TileEntityFactory;
import mekanism.common.tile.laser.TileEntityLaserTractorBeam;
import mekanism.common.tile.machine.TileEntityMetallurgicInfuser;
import mekanism.common.tile.machine.TileEntityOredictionificator;
import mekanism.common.tile.machine.TileEntitySolarNeutronActivator;
import mekanism.common.tile.multiblock.TileEntityInductionCell;
import mekanism.common.tile.multiblock.TileEntityInductionProvider;
import mekanism.common.tile.prefab.TileEntityAdvancedElectricMachine;
import mekanism.common.tile.transmitter.*;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class EMBlocks {
    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(EvolvedMekanism.MODID);
    public static final DeferredRegister.Blocks BLOCKS_NO_ITEMS = DeferredRegister.createBlocks(EvolvedMekanism.MODID);

    private static final Table<FactoryTier, FactoryType, BlockRegistryObject<BlockFactory<?>, ItemBlockTooltip>> FACTORIES = HashBasedTable.create();
    public static final Map<EMOreType, EMOreBlockType> ORES = new LinkedHashMap<>();

    static {
        // factories
        for (FactoryTier tier :  Arrays.asList(EMFactoryTier.OVERCLOCKED,EMFactoryTier.QUANTUM,EMFactoryTier.DENSE,EMFactoryTier.MULTIVERSAL,EMFactoryTier.CREATIVE)) {
            for (FactoryType type : EMVanillaMekanism.FACTORY_TYPES) {
                FACTORIES.put(tier, type, registerFactory(EMBlockTypes.getFactory(tier, type)));
            }
        }
        List<FactoryTier> allTiers = new ArrayList<>();
        allTiers.addAll(Arrays.asList(EMVanillaMekanism.FACTORY_TIERS));
        allTiers.addAll( Arrays.asList(EMFactoryTier.OVERCLOCKED,EMFactoryTier.QUANTUM,EMFactoryTier.DENSE,EMFactoryTier.MULTIVERSAL,EMFactoryTier.CREATIVE));
        for (FactoryTier tier :  allTiers) {
            FACTORIES.put(tier, EMFactoryType.ALLOYING, registerFactory(EMBlockTypes.getFactory(tier, EMFactoryType.ALLOYING)));
        }
        for (OreType ore : EMVanillaMekanism.ORE_TYPES) {
            registerOre(ore);
        }

        EMOreType ore = EMOreType.NOCTIS_ROZULI;
        String name = ore.getResource().getRegistrySuffix() + "_ore";
        BlockRegistryObject<EMBlockOre, ItemBlockTooltip> stoneOre = registerBlock(name, properties -> new EMBlockOre(ore, EMBlockOre.defaultProperties(properties)));
        BlockRegistryObject<EMBlockOre, ItemBlockTooltip> deepslateOre = BLOCKS.register("deepslate_" + name, () -> BlockBehaviour.Properties.ofLegacyCopy(stoneOre.value()),
                properties -> new EMBlockOre(ore, properties.mapColor(MapColor.DEEPSLATE)
                        .strength(4.5F, 3).sound(SoundType.DEEPSLATE)), ItemBlockTooltip::new);
        BLOCKS_NO_ITEMS.registerBlock(name+"_natural", props -> new BlockNoctisRozuliOre(ore, EMBlockOre.defaultProperties(props)));
        BLOCKS_NO_ITEMS.register("deepslate_" + name + "_natural",
                id -> new BlockNoctisRozuliOre(ore, BlockBehaviour.Properties.ofLegacyCopy(stoneOre.value()).setId(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK, id)).mapColor(MapColor.DEEPSLATE)
                        .strength(4.5F, 3).sound(SoundType.DEEPSLATE)));


        ORES.put(ore,new EMOreBlockType(stoneOre, deepslateOre));
    }
    public static final BlockRegistryObject<BlockBasicMultiblock<TileEntityAPTCasing>, ItemBlockTooltip> APT_CASING = registerBlock("apt_casing", properties -> new BlockBasicMultiblock<>(EMBlockTypes.APT_CASING, BlockBasicMultiblock.defaultProperties(properties).mapColor(MapColor.COLOR_MAGENTA)), Rarity.EPIC);
    public static final BlockRegistryObject<BlockBasicMultiblock<TileEntityAPTPort>, ItemBlockTooltip> APT_PORT = registerBlock("apt_port", properties -> new BlockBasicMultiblock<>(EMBlockTypes.APT_PORT, BlockBasicMultiblock.defaultProperties(properties).mapColor(MapColor.COLOR_MAGENTA)), Rarity.EPIC);
    public static final BlockRegistryObject<BlockTile<TileEntitySuperchargingElement, BlockTypeTile<TileEntitySuperchargingElement>>, ItemBlockTooltip> SUPERCHARGING_ELEMENT = registerBlock("supercharging_element", properties -> new BlockTile<>(EMBlockTypes.SUPERCHARGING_ELEMENT, BlockTile.defaultProperties(properties).mapColor(MapColor.COLOR_MAGENTA)));


    public static final BlockRegistryObject<BlockFactoryMachine<TileEntityAlloyer, Machine.FactoryMachine<TileEntityAlloyer>>, ItemBlockTooltip> ALLOYER = BLOCKS.register("alloyer", properties -> new BlockFactoryMachine<>(EMBlockTypes.ALLOYER, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor())),
            (block, properties) -> new ItemBlockTooltip(block, properties
                    .component(MekanismDataComponents.DETAILS, Unit.INSTANCE)
                    .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                    .component(MekanismDataComponents.SIDE_CONFIG, AttachedSideConfig.EXTRA_MACHINE)
            )
    ).forItemHolder(holder -> holder.addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
            .addInput(EMRecipeType.ALLOYING, EMInputRecipeCache.TripleItem::containsInputA)
            .addInput(EMRecipeType.ALLOYING, EMInputRecipeCache.TripleItem::containsInputB)
            .addInput(EMRecipeType.ALLOYING, EMInputRecipeCache.TripleItem::containsInputC)
            .addOutput()
            .addEnergy()
            .build()
    ));

    public static final BlockRegistryObject<BlockFactoryMachine<TileEntityFluidCombiner, Machine.FactoryMachine<TileEntityFluidCombiner>>, ItemBlockTooltip> FLUID_COMBINER = BLOCKS.register("fluid_combiner", properties -> new BlockFactoryMachine<>(EMBlockTypes.FLUID_COMBINER, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor())),
            (block, properties) -> new ItemBlockTooltip(block, properties
                    .component(MekanismDataComponents.DETAILS, Unit.INSTANCE)
                    .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                    .component(MekanismDataComponents.SIDE_CONFIG, EMAttachedSideConfig.FLUID_COMBINER_MACHINE)
            )
    ).forItemHolder(holder -> holder
            .addAttachmentOnlyContainers(ContainerType.FLUID, () -> FluidTanksBuilder.builder()
                    .addBasic(TileEntityFluidCombiner.MAX_FLUID, EMRecipeType.FLUID_COMBINING, EMInputRecipeCache.DoubleFluid::containsInputA)
                    .addBasic(TileEntityFluidCombiner.MAX_FLUID, EMRecipeType.FLUID_COMBINING, EMInputRecipeCache.DoubleFluid::containsInputB)
                    .addBasic(TileEntityFluidCombiner.MAX_FLUID)
                    .build())
            .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                    .addFluidFillSlot(0)
                    .addFluidFillSlot(1)
                    .addFluidDrainSlot(2)
                    .addOutput()
                    .addEnergy()
                    .build()));

    public static final BlockRegistryObject<BlockFactoryMachine<TileEntityFluidAlloyer, Machine.FactoryMachine<TileEntityFluidAlloyer>>, ItemBlockTooltip> FLUID_ALLOYER = BLOCKS.register("fluid_alloyer", properties -> new BlockFactoryMachine<>(EMBlockTypes.FLUID_ALLOYER, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor())),
            (block, properties) -> new ItemBlockTooltip(block, properties
                    .component(MekanismDataComponents.DETAILS, Unit.INSTANCE)
                    .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                    .component(MekanismDataComponents.SIDE_CONFIG, EMAttachedSideConfig.FLUID_ALLOYER_MACHINE)
            )
    ).forItemHolder(holder -> holder
            .addAttachmentOnlyContainers(ContainerType.FLUID, () -> FluidTanksBuilder.builder()
                    .addBasic(TileEntityFluidAlloyer.MAX_FLUID, EMRecipeType.FLUID_ALLOYING, EMInputRecipeCache.TripleFluid::containsInputA)
                    .addBasic(TileEntityFluidAlloyer.MAX_FLUID, EMRecipeType.FLUID_ALLOYING, EMInputRecipeCache.TripleFluid::containsInputB)
                    .addBasic(TileEntityFluidAlloyer.MAX_FLUID, EMRecipeType.FLUID_ALLOYING, EMInputRecipeCache.TripleFluid::containsInputC)
                    .addBasic(TileEntityFluidAlloyer.MAX_FLUID)
                    .build())
            .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                    .addFluidFillSlot(0)
                    .addFluidFillSlot(1)
                    .addFluidFillSlot(2)
                    .addFluidDrainSlot(3)
                    .addOutput()
                    .addEnergy()
                    .build()));

    public static final BlockRegistryObject<BlockTileModel<TileEntityChemixer, Machine<TileEntityChemixer>>, ItemBlockTooltip> CHEMIXER =
            BLOCKS.register("chemixer", properties -> new BlockTileModel<>(EMBlockTypes.CHEMIXER, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor())),
                    (block, properties) -> new ItemBlockTooltip(block, properties
                    .component(MekanismDataComponents.DETAILS, Unit.INSTANCE)
                            .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                            .component(MekanismDataComponents.SIDE_CONFIG, EMAttachedSideConfig.CHEMIXER_MACHINE)
                    )).forItemHolder(holder -> holder
                    .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                            .addInput(EMRecipeType.CHEMIXING, EMInputRecipeCache.ItemItemChemical::containsInputA)
                            .addInput(EMRecipeType.CHEMIXING, EMInputRecipeCache.ItemItemChemical::containsInputB)
                            .addOutput()
                            .addEnergy()
                            .build()
                    ).addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> ChemicalTanksBuilder.builder()
                            .addBasic(TileEntityChemixer.MAX_GAS, EMRecipeType.CHEMIXING, EMInputRecipeCache.ItemItemChemical::containsInputC)
                            .build()
                    )
            );
    public static final BlockRegistryObject<BlockTileModel<TileEntityMelter, Machine<TileEntityMelter>>,  ItemBlockTooltip> MELTER =
            BLOCKS.register("thermalizer", properties -> new BlockTileModel<>(EMBlockTypes.MELTER, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor())),
                    (block, properties) -> new ItemBlockTooltip(block, properties
                    .component(MekanismDataComponents.DETAILS, Unit.INSTANCE)
                            .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                            .component(MekanismDataComponents.SIDE_CONFIG, EMAttachedSideConfig.MELTER_MACHINE)
                    )).forItemHolder(holder -> holder
                    .addAttachmentOnlyContainers(ContainerType.FLUID, () -> FluidTanksBuilder.builder()
                            .addBasic(TileEntityMelter.MAX_FLUID)
                            .build()
                    ).addAttachmentOnlyContainers(ContainerType.HEAT, () -> HeatCapacitorBuilder.builder()
                            .addBasic(10,5,100)
                            .build()
                    ).addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                            .addInput(EMRecipeType.MELTING, InputRecipeCache.SingleItem::containsInput)
                            .addFluidDrainSlot(0)
                            .addOutput()
                            .build()
                    )
            );
    public static final BlockRegistryObject<BlockTileModel<TileEntitySolidifier, Machine<TileEntitySolidifier>>,   ItemBlockTooltip> SOLIDIFIER =
            BLOCKS.register("solidification_chamber", properties -> new BlockTileModel<>(EMBlockTypes.SOLIDIFIER, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor())),
                    (block, properties) -> new ItemBlockTooltip(block, properties
                    .component(MekanismDataComponents.DETAILS, Unit.INSTANCE)
                            .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                            .component(MekanismDataComponents.SIDE_CONFIG, EMAttachedSideConfig.SOLIDIFICATION_MACHINE)
                    )).forItemHolder(holder -> holder
                    .addAttachmentOnlyContainers(ContainerType.FLUID, () -> FluidTanksBuilder.builder()
                            .addBasic(10_000,EMRecipeType.SOLIDIFICATION, EMInputRecipeCache.ItemFluidFluid::containsInputC)
                            .addBasic(10_000,EMRecipeType.SOLIDIFICATION, EMInputRecipeCache.ItemFluidFluid::containsInputB)
                            .build()
                    ).addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                            .addInput(EMRecipeType.SOLIDIFICATION, EMInputRecipeCache.ItemFluidFluid::containsInputA)
                            .addOutput()
                            .addEnergy()
                            .build()
                    ));

    public static final BlockRegistryObject<BlockTileModel<TileEntityLunarNeutronActivator, Machine<TileEntityLunarNeutronActivator>>, ItemBlockTooltip> LUNAR_NEUTRON_ACTIVATOR =
            BLOCKS.register("lunar_neutron_activator", properties -> new BlockTileModel<>(EMBlockTypes.LUNAR_NEUTRON_ACTIVATOR, BlockTile.defaultProperties(properties).mapColor(MapColor.COLOR_PINK)),
                    (block, properties) -> new ItemBlockTooltip(block, properties
                    .component(MekanismDataComponents.DETAILS, Unit.INSTANCE)
                            .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                            .component(MekanismDataComponents.SIDE_CONFIG, AttachedSideConfig.SNA)
                    )
            ).forItemHolder(holder -> holder
                    .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> ChemicalTanksBuilder.builder()
                            .addBasic(TileEntitySolarNeutronActivator.MAX_GAS, MekanismRecipeType.ACTIVATING, InputRecipeCache.SingleChemical::containsInput)
                            .addBasic(TileEntitySolarNeutronActivator.MAX_GAS)
                            .build()
                    ).addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                            .addChemicalFillSlot(0)
                            .addChemicalDrainSlot(1)
                            .build()
                    )
            );


    public static final BlockRegistryObject<BlockTileModel<TileEntityLaserDisenchanter, BlockTypeTile<TileEntityLaserDisenchanter>>, ItemBlockLaserTractorBeam> LASER_DISENCHANTER =
            BLOCKS.register("laser_disenchanter", properties -> new BlockTileModel<>(EMBlockTypes.LASER_DISENCHANTER, BlockTile.defaultProperties(properties).mapColor(MapColor.COLOR_GRAY)), ItemBlockLaserTractorBeam::new
            ).forItemHolder(holder -> holder.addAttachmentOnlyContainers(ContainerType.ITEM,() ->
                    ItemSlotsBuilder.builder()
                            .addOutput(2 * 6)
                            .addChemicalDrainSlot(0)
                            .addChemicalFillSlot(0)
                            .build()).addAttachmentOnlyContainers(ContainerType.CHEMICAL, () ->
                    ChemicalTanksBuilder.builder()
                            .addBasic(TileEntityLaserDisenchanter.MAX_GAS,stack->stack.is(EMChemicals.CRYONOCTIS))
                            .build()));

    public static final BlockRegistryObject<BlockTileModel<TileEntityEnchantingStation, BlockTypeTile<TileEntityEnchantingStation>>, ItemBlockTooltip> ENCHANTING_STATION =
            BLOCKS.registerDetails("enchanting_station", properties -> new BlockTileModel<>(EMBlockTypes.ENCHANTING_STATION, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor())))
                    .forItemHolder(holder ->
                            holder.addAttachmentOnlyContainers(ContainerType.ITEM,
                                    () -> ItemSlotsBuilder.builder()
                            .addInput(stack -> stack.getItem().isEnchantable(stack))
                                            .addChemicalFillSlot(0)
                                            .addEnergy()
                            .build()
                    ).addAttachmentOnlyContainers(ContainerType.CHEMICAL,
                                    ()-> ChemicalTanksBuilder.builder()
                                            .addBasic(TileEntityEnchantingStation.MAX_GAS, stack -> stack.is(EMChemicals.CRYONOCTIS))
                                            .build()));

    public static final BlockRegistryObject<BlockTile<TileEntityEnchantmentSorter, BlockTypeTile<TileEntityEnchantmentSorter>>, ItemBlockTooltip> SORTIFICHANTOR =
            BLOCKS.register("sortifichantor", properties -> new BlockTile<>(EMBlockTypes.SORTIFICHANTOR, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor())),
                    (block, properties) -> new ItemBlockTooltip(block, properties
                    .component(MekanismDataComponents.DETAILS, Unit.INSTANCE)
                            .component(MekanismDataComponents.FILTER_AWARE, FilterAware.EMPTY)
                            .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                            .component(MekanismDataComponents.SIDE_CONFIG, new AttachedSideConfig(Map.of(TransmissionType.ITEM, EMAttachedSideConfig.SORTER_MACHINE)))
                    )
            ).forItemHolder(holder -> holder.addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                    .addInput(1)
                    .addOutput(2)
                    .build()
            ));

    public static final BlockRegistryObject<EMBlockResource, EMItemBlockResource> BETTER_GOLD_BLOCK = registerResourceBlock(EMBlockResourceInfo.BETTER_GOLD);
    public static final BlockRegistryObject<EMBlockResource, EMItemBlockResource> PLASLITHERITE_BLOCK = registerResourceBlock(EMBlockResourceInfo.PLASLITHERITE);
    public static final BlockRegistryObject<EMBlockResource, EMItemBlockResource> REFINED_REDSTONE_BLOCK = registerResourceBlock(EMBlockResourceInfo.REFINED_REDSTONE);
    public static final BlockRegistryObject<EMBlockResource, EMItemBlockResource> NOCTIS_ROZULI_BLOCK = registerResourceBlock(EMBlockResourceInfo.NOCTIS_ROZULI);

    public static final BlockRegistryObject<Block,BlockItem> INFUSED_ALLOY_BLOCK = registerAlloyBlock(AlloyTier.INFUSED);
    public static final BlockRegistryObject<Block,BlockItem> REINFORCED_ALLOY_BLOCK = registerAlloyBlock(AlloyTier.REINFORCED);
    public static final BlockRegistryObject<Block,BlockItem> ATOMIC_ALLOY_BLOCK = registerAlloyBlock(AlloyTier.ATOMIC);
    public static final BlockRegistryObject<Block,BlockItem> HYPERCHARGED_ALLOY_BLOCK = registerAlloyBlock(EMAlloyTier.HYPERCHARGED);
    public static final BlockRegistryObject<Block,BlockItem> SUBATOMIC_ALLOY_BLOCK = registerAlloyBlock(EMAlloyTier.SUBATOMIC);
    public static final BlockRegistryObject<Block,BlockItem> SINGULAR_ALLOY_BLOCK = registerAlloyBlock(EMAlloyTier.SINGULAR);
    public static final BlockRegistryObject<Block,BlockItem> EXOVERSAL_ALLOY_BLOCK = registerAlloyBlock(EMAlloyTier.EXOVERSAL);
    public static final BlockRegistryObject<Block,BlockItem> CREATIVE_ALLOY_BLOCK = registerAlloyBlock(EMAlloyTier.CREATIVE);

    public static final BlockRegistryObject<BlockBin, ItemBlockBin> OVERCLOCKED_BIN = registerBin(EMBlockTypes.OVERCLOCKED_BIN);
    public static final BlockRegistryObject<BlockBin, ItemBlockBin> QUANTUM_BIN = registerBin(EMBlockTypes.QUANTUM_BIN);
    public static final BlockRegistryObject<BlockBin, ItemBlockBin> DENSE_BIN = registerBin(EMBlockTypes.DENSE_BIN);
    public static final BlockRegistryObject<BlockBin, ItemBlockBin> MULTIVERSAL_BIN = registerBin(EMBlockTypes.MULTIVERSAL_BIN);

    public static final BlockRegistryObject<BlockTile<TileEntityInductionCell, BlockTypeTile<TileEntityInductionCell>>, ItemBlockInductionCell> OVERCLOCKED_INDUCTION_CELL = registerInductionCell(EMBlockTypes.OVERCLOCKED_INDUCTION_CELL);
    public static final BlockRegistryObject<BlockTile<TileEntityInductionCell, BlockTypeTile<TileEntityInductionCell>>, ItemBlockInductionCell> QUANTUM_INDUCTION_CELL = registerInductionCell(EMBlockTypes.QUANTUM_INDUCTION_CELL);
    public static final BlockRegistryObject<BlockTile<TileEntityInductionCell, BlockTypeTile<TileEntityInductionCell>>, ItemBlockInductionCell> DENSE_INDUCTION_CELL = registerInductionCell(EMBlockTypes.DENSE_INDUCTION_CELL);
    public static final BlockRegistryObject<BlockTile<TileEntityInductionCell, BlockTypeTile<TileEntityInductionCell>>, ItemBlockInductionCell> MULTIVERSAL_INDUCTION_CELL = registerInductionCell(EMBlockTypes.MULTIVERSAL_INDUCTION_CELL);
    public static final BlockRegistryObject<BlockTile<TileEntityInductionCell, BlockTypeTile<TileEntityInductionCell>>, ItemBlockInductionCell> CREATIVE_INDUCTION_CELL = registerInductionCell(EMBlockTypes.CREATIVE_INDUCTION_CELL);

    public static final BlockRegistryObject<BlockTile<TileEntityInductionProvider, BlockTypeTile<TileEntityInductionProvider>>, BlockItem> OVERCLOCKED_INDUCTION_PROVIDER = registerInductionProvider(EMBlockTypes.OVERCLOCKED_INDUCTION_PROVIDER);
    public static final BlockRegistryObject<BlockTile<TileEntityInductionProvider, BlockTypeTile<TileEntityInductionProvider>>, BlockItem> QUANTUM_INDUCTION_PROVIDER = registerInductionProvider(EMBlockTypes.QUANTUM_INDUCTION_PROVIDER);
    public static final BlockRegistryObject<BlockTile<TileEntityInductionProvider, BlockTypeTile<TileEntityInductionProvider>>, BlockItem> DENSE_INDUCTION_PROVIDER = registerInductionProvider(EMBlockTypes.DENSE_INDUCTION_PROVIDER);
    public static final BlockRegistryObject<BlockTile<TileEntityInductionProvider, BlockTypeTile<TileEntityInductionProvider>>, BlockItem> MULTIVERSAL_INDUCTION_PROVIDER = registerInductionProvider(EMBlockTypes.MULTIVERSAL_INDUCTION_PROVIDER);
    public static final BlockRegistryObject<BlockTile<TileEntityInductionProvider, BlockTypeTile<TileEntityInductionProvider>>, BlockItem> CREATIVE_INDUCTION_PROVIDER = registerInductionProvider(EMBlockTypes.CREATIVE_INDUCTION_PROVIDER);

    public static final BlockRegistryObject<BlockFluidTank, ItemBlockFluidTank> OVERCLOCKED_FLUID_TANK = registerFluidTank(EMBlockTypes.OVERCLOCKED_FLUID_TANK);
    public static final BlockRegistryObject<BlockFluidTank, ItemBlockFluidTank> QUANTUM_FLUID_TANK = registerFluidTank(EMBlockTypes.QUANTUM_FLUID_TANK);
    public static final BlockRegistryObject<BlockFluidTank, ItemBlockFluidTank> DENSE_FLUID_TANK = registerFluidTank(EMBlockTypes.DENSE_FLUID_TANK);
    public static final BlockRegistryObject<BlockFluidTank, ItemBlockFluidTank> MULTIVERSAL_FLUID_TANK = registerFluidTank(EMBlockTypes.MULTIVERSAL_FLUID_TANK);

    public static final BlockRegistryObject<BlockEnergyCube, ItemBlockEnergyCube> OVERCLOCKED_ENERGY_CUBE = registerEnergyCube(EMBlockTypes.OVERCLOCKED_ENERGY_CUBE);
    public static final BlockRegistryObject<BlockEnergyCube, ItemBlockEnergyCube> QUANTUM_ENERGY_CUBE = registerEnergyCube(EMBlockTypes.QUANTUM_ENERGY_CUBE);
    public static final BlockRegistryObject<BlockEnergyCube, ItemBlockEnergyCube> DENSE_ENERGY_CUBE = registerEnergyCube(EMBlockTypes.DENSE_ENERGY_CUBE);
    public static final BlockRegistryObject<BlockEnergyCube, ItemBlockEnergyCube> MULTIVERSAL_ENERGY_CUBE = registerEnergyCube(EMBlockTypes.MULTIVERSAL_ENERGY_CUBE);

    public static final BlockRegistryObject<BlockSmallTransmitter<TileEntityUniversalCable>, BlockItem> OVERCLOCKED_UNIVERSAL_CABLE = registerUniversalCable(EMBlockTypes.OVERCLOCKED_UNIVERSAL_CABLE);
    public static final BlockRegistryObject<BlockSmallTransmitter<TileEntityUniversalCable>, BlockItem> QUANTUM_UNIVERSAL_CABLE = registerUniversalCable(EMBlockTypes.QUANTUM_UNIVERSAL_CABLE);
    public static final BlockRegistryObject<BlockSmallTransmitter<TileEntityUniversalCable>, BlockItem> DENSE_UNIVERSAL_CABLE = registerUniversalCable(EMBlockTypes.DENSE_UNIVERSAL_CABLE);
    public static final BlockRegistryObject<BlockSmallTransmitter<TileEntityUniversalCable>, BlockItem> MULTIVERSAL_UNIVERSAL_CABLE = registerUniversalCable(EMBlockTypes.MULTIVERSAL_UNIVERSAL_CABLE);
    public static final BlockRegistryObject<BlockSmallTransmitter<TileEntityUniversalCable>, BlockItem> CREATIVE_UNIVERSAL_CABLE = registerUniversalCable(EMBlockTypes.CREATIVE_UNIVERSAL_CABLE);

    public static final BlockRegistryObject<BlockLargeTransmitter<TileEntityMechanicalPipe>, BlockItem> OVERCLOCKED_MECHANICAL_PIPE = registerMechanicalPipe(EMBlockTypes.OVERCLOCKED_MECHANICAL_PIPE);
    public static final BlockRegistryObject<BlockLargeTransmitter<TileEntityMechanicalPipe>, BlockItem> QUANTUM_MECHANICAL_PIPE = registerMechanicalPipe(EMBlockTypes.QUANTUM_MECHANICAL_PIPE);
    public static final BlockRegistryObject<BlockLargeTransmitter<TileEntityMechanicalPipe>, BlockItem> DENSE_MECHANICAL_PIPE = registerMechanicalPipe(EMBlockTypes.DENSE_MECHANICAL_PIPE);
    public static final BlockRegistryObject<BlockLargeTransmitter<TileEntityMechanicalPipe>, BlockItem> MULTIVERSAL_MECHANICAL_PIPE = registerMechanicalPipe(EMBlockTypes.MULTIVERSAL_MECHANICAL_PIPE);
    public static final BlockRegistryObject<BlockLargeTransmitter<TileEntityMechanicalPipe>, BlockItem> CREATIVE_MECHANICAL_PIPE = registerMechanicalPipe(EMBlockTypes.CREATIVE_MECHANICAL_PIPE);

    public static final BlockRegistryObject<BlockSmallTransmitter<TileEntityPressurizedTube>, BlockItem> OVERCLOCKED_PRESSURIZED_TUBE = registerPressurizedTube(EMBlockTypes.OVERCLOCKED_PRESSURIZED_TUBE);
    public static final BlockRegistryObject<BlockSmallTransmitter<TileEntityPressurizedTube>, BlockItem> QUANTUM_PRESSURIZED_TUBE = registerPressurizedTube(EMBlockTypes.QUANTUM_PRESSURIZED_TUBE);
    public static final BlockRegistryObject<BlockSmallTransmitter<TileEntityPressurizedTube>, BlockItem> DENSE_PRESSURIZED_TUBE = registerPressurizedTube(EMBlockTypes.DENSE_PRESSURIZED_TUBE);
    public static final BlockRegistryObject<BlockSmallTransmitter<TileEntityPressurizedTube>, BlockItem> MULTIVERSAL_PRESSURIZED_TUBE = registerPressurizedTube(EMBlockTypes.MULTIVERSAL_PRESSURIZED_TUBE);
    public static final BlockRegistryObject<BlockSmallTransmitter<TileEntityPressurizedTube>, BlockItem> CREATIVE_PRESSURIZED_TUBE = registerPressurizedTube(EMBlockTypes.CREATIVE_PRESSURIZED_TUBE);

    public static final BlockRegistryObject<BlockLargeTransmitter<TileEntityLogisticalTransporter>, BlockItem> OVERCLOCKED_LOGISTICAL_TRANSPORTER = registerLogisticalTransporter(EMBlockTypes.OVERCLOCKED_LOGISTICAL_TRANSPORTER);
    public static final BlockRegistryObject<BlockLargeTransmitter<TileEntityLogisticalTransporter>, BlockItem> QUANTUM_LOGISTICAL_TRANSPORTER = registerLogisticalTransporter(EMBlockTypes.QUANTUM_LOGISTICAL_TRANSPORTER);
    public static final BlockRegistryObject<BlockLargeTransmitter<TileEntityLogisticalTransporter>, BlockItem> DENSE_LOGISTICAL_TRANSPORTER = registerLogisticalTransporter(EMBlockTypes.DENSE_LOGISTICAL_TRANSPORTER);
    public static final BlockRegistryObject<BlockLargeTransmitter<TileEntityLogisticalTransporter>, BlockItem> MULTIVERSAL_LOGISTICAL_TRANSPORTER = registerLogisticalTransporter(EMBlockTypes.MULTIVERSAL_LOGISTICAL_TRANSPORTER);
    public static final BlockRegistryObject<BlockLargeTransmitter<TileEntityLogisticalTransporter>, BlockItem> CREATIVE_LOGISTICAL_TRANSPORTER = registerLogisticalTransporter(EMBlockTypes.CREATIVE_LOGISTICAL_TRANSPORTER);

    public static final BlockRegistryObject<BlockSmallTransmitter<TileEntityThermodynamicConductor>, BlockItem> OVERCLOCKED_THERMODYNAMIC_CONDUCTOR = registerThermodynamicConductor(EMBlockTypes.OVERCLOCKED_THERMODYNAMIC_CONDUCTOR);
    public static final BlockRegistryObject<BlockSmallTransmitter<TileEntityThermodynamicConductor>, BlockItem> QUANTUM_THERMODYNAMIC_CONDUCTOR = registerThermodynamicConductor(EMBlockTypes.QUANTUM_THERMODYNAMIC_CONDUCTOR);
    public static final BlockRegistryObject<BlockSmallTransmitter<TileEntityThermodynamicConductor>, BlockItem> DENSE_THERMODYNAMIC_CONDUCTOR = registerThermodynamicConductor(EMBlockTypes.DENSE_THERMODYNAMIC_CONDUCTOR);
    public static final BlockRegistryObject<BlockSmallTransmitter<TileEntityThermodynamicConductor>, BlockItem> MULTIVERSAL_THERMODYNAMIC_CONDUCTOR = registerThermodynamicConductor(EMBlockTypes.MULTIVERSAL_THERMODYNAMIC_CONDUCTOR);
    public static final BlockRegistryObject<BlockSmallTransmitter<TileEntityThermodynamicConductor>, BlockItem> CREATIVE_THERMODYNAMIC_CONDUCTOR = registerThermodynamicConductor(EMBlockTypes.CREATIVE_THERMODYNAMIC_CONDUCTOR);

    public static final BlockRegistryObject<BlockTileModel<TileEntityChemicalTank, Machine<TileEntityChemicalTank>>, ItemBlockChemicalTank> OVERCLOCKED_CHEMICAL_TANK = registerChemicalTank(EMBlockTypes.OVERCLOCKED_CHEMICAL_TANK);
    public static final BlockRegistryObject<BlockTileModel<TileEntityChemicalTank, Machine<TileEntityChemicalTank>>, ItemBlockChemicalTank> QUANTUM_CHEMICAL_TANK = registerChemicalTank(EMBlockTypes.QUANTUM_CHEMICAL_TANK);
    public static final BlockRegistryObject<BlockTileModel<TileEntityChemicalTank, Machine<TileEntityChemicalTank>>, ItemBlockChemicalTank> DENSE_CHEMICAL_TANK = registerChemicalTank(EMBlockTypes.DENSE_CHEMICAL_TANK);
    public static final BlockRegistryObject<BlockTileModel<TileEntityChemicalTank, Machine<TileEntityChemicalTank>>, ItemBlockChemicalTank> MULTIVERSAL_CHEMICAL_TANK = registerChemicalTank(EMBlockTypes.MULTIVERSAL_CHEMICAL_TANK);

    public static final BlockRegistryObject<BlockTieredPersonnalBarrel, ItemBlockTieredPersonalStorage> ADVANCED_PERSONAL_BARREL =registerPersonalBarrel(EMBlockTypes.ADVANCED_PERSONAL_BARREL, PersonalStorageTier.ADVANCED);
    public static final BlockRegistryObject<BlockTieredPersonnalBarrel, ItemBlockTieredPersonalStorage> ELITE_PERSONAL_BARREL =registerPersonalBarrel(EMBlockTypes.ELITE_PERSONAL_BARREL, PersonalStorageTier.ELITE);
    public static final BlockRegistryObject<BlockTieredPersonnalBarrel, ItemBlockTieredPersonalStorage> ULTIMATE_PERSONAL_BARREL =registerPersonalBarrel(EMBlockTypes.ULTIMATE_PERSONAL_BARREL, PersonalStorageTier.ULTIMATE);
    public static final BlockRegistryObject<BlockTieredPersonnalBarrel, ItemBlockTieredPersonalStorage> OVERCLOCKED_PERSONAL_BARREL =registerPersonalBarrel(EMBlockTypes.OVERCLOCKED_PERSONAL_BARREL, PersonalStorageTier.OVERCLOCKED);
    public static final BlockRegistryObject<BlockTieredPersonnalBarrel, ItemBlockTieredPersonalStorage> QUANTUM_PERSONAL_BARREL =registerPersonalBarrel(EMBlockTypes.QUANTUM_PERSONAL_BARREL, PersonalStorageTier.QUANTUM);
    public static final BlockRegistryObject<BlockTieredPersonnalBarrel, ItemBlockTieredPersonalStorage> DENSE_PERSONAL_BARREL =registerPersonalBarrel(EMBlockTypes.DENSE_PERSONAL_BARREL, PersonalStorageTier.DENSE);
    public static final BlockRegistryObject<BlockTieredPersonnalBarrel, ItemBlockTieredPersonalStorage> MULTIVERSAL_PERSONAL_BARREL =registerPersonalBarrel(EMBlockTypes.MULTIVERSAL_PERSONAL_BARREL, PersonalStorageTier.MULTIVERSAL);
    public static final BlockRegistryObject<BlockTieredPersonnalBarrel, ItemBlockTieredPersonalStorage> CREATIVE_PERSONAL_BARREL =registerPersonalBarrel(EMBlockTypes.CREATIVE_PERSONAL_BARREL, PersonalStorageTier.CREATIVE);

    public static final BlockRegistryObject<BlockTieredPersonnalChest, ItemBlockTieredPersonalStorage> ADVANCED_PERSONAL_CHEST = registerPersonalChest(EMBlockTypes.ADVANCED_PERSONAL_CHEST,PersonalStorageTier.ADVANCED);
    public static final BlockRegistryObject<BlockTieredPersonnalChest, ItemBlockTieredPersonalStorage> ELITE_PERSONAL_CHEST = registerPersonalChest(EMBlockTypes.ELITE_PERSONAL_CHEST,PersonalStorageTier.ELITE);
    public static final BlockRegistryObject<BlockTieredPersonnalChest, ItemBlockTieredPersonalStorage> ULTIMATE_PERSONAL_CHEST = registerPersonalChest(EMBlockTypes.ULTIMATE_PERSONAL_CHEST,PersonalStorageTier.ULTIMATE);
    public static final BlockRegistryObject<BlockTieredPersonnalChest, ItemBlockTieredPersonalStorage> OVERCLOCKED_PERSONAL_CHEST = registerPersonalChest(EMBlockTypes.OVERCLOCKED_PERSONAL_CHEST,PersonalStorageTier.OVERCLOCKED);
    public static final BlockRegistryObject<BlockTieredPersonnalChest, ItemBlockTieredPersonalStorage> QUANTUM_PERSONAL_CHEST = registerPersonalChest(EMBlockTypes.QUANTUM_PERSONAL_CHEST,PersonalStorageTier.QUANTUM);
    public static final BlockRegistryObject<BlockTieredPersonnalChest, ItemBlockTieredPersonalStorage> DENSE_PERSONAL_CHEST = registerPersonalChest(EMBlockTypes.DENSE_PERSONAL_CHEST,PersonalStorageTier.DENSE);
    public static final BlockRegistryObject<BlockTieredPersonnalChest, ItemBlockTieredPersonalStorage> MULTIVERSAL_PERSONAL_CHEST = registerPersonalChest(EMBlockTypes.MULTIVERSAL_PERSONAL_CHEST,PersonalStorageTier.MULTIVERSAL);
    public static final BlockRegistryObject<BlockTieredPersonnalChest, ItemBlockTieredPersonalStorage> CREATIVE_PERSONAL_CHEST = registerPersonalChest(EMBlockTypes.CREATIVE_PERSONAL_CHEST,PersonalStorageTier.CREATIVE);


    private static void registerOre(OreType ore) {
        String name = ore.getResource().getRegistrySuffix() + "_ore";
        BlockRegistryObject<BlockOre, ItemBlockTooltip> nether = BLOCKS.register("netherrack_"+name, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.NETHERRACK), properties -> new BlockOre(ore, properties), ItemBlockTooltip::new);
        BlockRegistryObject<BlockOre, ItemBlockTooltip> endStone = BLOCKS.register("end_stone_" + name, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE), properties -> new BlockOre(ore, properties), ItemBlockTooltip::new);
        BlockRegistryObject<BlockOre, ItemBlockTooltip> aether = BLOCKS.register("holystone_" + name, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE), properties -> new BlockOre(ore, properties.mapColor(MapColor.WOOL).instrument(NoteBlockInstrument.BASEDRUM).strength(3.0F).requiresCorrectToolForDrops()), ItemBlockTooltip::new);
        BlockRegistryObject<BlockOre, ItemBlockTooltip> undergarden = BLOCKS.register("depthrock_" + name, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE), properties -> new BlockOre(ore, properties.instrument(NoteBlockInstrument.BASEDRUM).mapColor(MapColor.TERRACOTTA_LIGHT_GREEN).strength(1.5F, 6.0F).sound(SoundType.BASALT).requiresCorrectToolForDrops()), ItemBlockTooltip::new);
        BlockRegistryObject<BlockOre, ItemBlockTooltip> undergarden2 = BLOCKS.register("shiverstone_" + name, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE), properties -> new BlockOre(ore, properties.instrument(NoteBlockInstrument.BASEDRUM).mapColor(MapColor.GLOW_LICHEN).strength(3.5F, 12F).sound(SoundType.NETHER_BRICKS).requiresCorrectToolForDrops().friction(0.98F)), ItemBlockTooltip::new);
    }

    private static EMOreBlockType registerOre(EMOreType ore) {
        String name = ore.getResource().getRegistrySuffix() + "_ore";
        BlockRegistryObject<EMBlockOre, ItemBlockTooltip> stoneOre = registerBlock(name, properties -> new EMBlockOre(ore, EMBlockOre.defaultProperties(properties)));
        BlockRegistryObject<EMBlockOre, ItemBlockTooltip> deepslateOre = BLOCKS.register("deepslate_" + name, () -> BlockBehaviour.Properties.ofLegacyCopy(stoneOre.value()),
                properties -> new EMBlockOre(ore, properties.mapColor(MapColor.DEEPSLATE)
                        .strength(4.5F, 3).sound(SoundType.DEEPSLATE)), ItemBlockTooltip::new);
        return new EMOreBlockType(stoneOre, deepslateOre);
    }

    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip> registerBlock(String name,
                                                                                                                             Function<BlockBehaviour.Properties, ? extends BLOCK> blockSupplier) {
        return BLOCKS.register(name, blockSupplier, ItemBlockTooltip::new);
    }

    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip> registerBlock(String name,
                                                                                                                             Function<BlockBehaviour.Properties, ? extends BLOCK> blockSupplier, Rarity rarity) {
        return BLOCKS.register(name, blockSupplier, (block, props) -> new ItemBlockTooltip(block, props.rarity(rarity)));
    }


    private static BlockRegistryObject<BlockTieredPersonnalChest, ItemBlockTieredPersonalStorage> registerPersonalChest(BlockTypeTile<TileEntityTieredPersonalChest> type,PersonalStorageTier tier) {
        return registerTieredBlock(tier, "_personal_chest", () -> new BlockTieredPersonnalChest(type,tier), (block,props) -> new ItemBlockTieredPersonalStorage(block, props, Stats.OPEN_CHEST, block.getTier()));
    }
    private static BlockRegistryObject<BlockTieredPersonnalBarrel, ItemBlockTieredPersonalStorage> registerPersonalBarrel(BlockTypeTile<TileEntityTieredPersonalBarrel> type, PersonalStorageTier tier) {
        return registerTieredBlock(tier, "_personal_barrel", () -> new BlockTieredPersonnalBarrel(type,tier), (block,props) -> new ItemBlockTieredPersonalStorage(block, props, Stats.OPEN_CHEST, block.getTier()));
    }

    private static BlockRegistryObject<BlockBin, ItemBlockBin> registerBin(BlockTypeTile<TileEntityBin> type) {
        return registerTieredBlock(type, "_bin", (properties, color) -> new BlockBin(type, BlockTile.defaultProperties(properties).mapColor(color)), ItemBlockBin::new)
                .forItemHolder(holder -> holder.addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                        .addSlot(ComponentBackedBinInventorySlot::create)
                        .build()
                ));
    }

    private static BlockRegistryObject<BlockTile<TileEntityInductionCell, BlockTypeTile<TileEntityInductionCell>>, ItemBlockInductionCell> registerInductionCell(BlockTypeTile<TileEntityInductionCell> type) {
        return registerTieredBlock(type, "_induction_cell", (properties, color) -> new BlockTile<>(type, BlockTile.defaultProperties(properties).mapColor(color)), ItemBlockInductionCell::new);
    }

    private static BlockRegistryObject<BlockTile<TileEntityInductionProvider, BlockTypeTile<TileEntityInductionProvider>>, BlockItem> registerInductionProvider(BlockTypeTile<TileEntityInductionProvider> type) {
        return registerTieredBlock(type, "_induction_provider", (properties, color) -> new BlockTile<>(type, BlockTile.defaultProperties(properties).mapColor(color)), BlockItem::new);
    }

    private static BlockRegistryObject<BlockFluidTank, ItemBlockFluidTank> registerFluidTank(Machine<TileEntityFluidTank> type) {
        return registerTieredBlock(type, "_fluid_tank", properties -> new BlockFluidTank(type, properties), ItemBlockFluidTank::new)
                .forItemHolder(holder -> holder
                        .addAttachedContainerCapabilities(ContainerType.FLUID, () -> FluidTanksBuilder.builder()
                                .addContainer(ComponentBackedFluidTankFluidTank::create)
                                .build()
                        ).addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                                .addFluidInputSlot(0)
                                .addOutput()
                                .build()
                        )
                );
    }

    private static BlockRegistryObject<BlockEnergyCube, ItemBlockEnergyCube> registerEnergyCube(Machine<TileEntityEnergyCube> type) {
        return registerTieredBlock(type, "_energy_cube", properties -> new BlockEnergyCube(type, properties), ItemBlockEnergyCube::new)
                .forItemHolder(holder -> holder.addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                        .addEnergy()
                        .addDrainEnergy()
                        .build()
                ));
    }

    private static BlockRegistryObject<BlockSmallTransmitter<TileEntityUniversalCable>, BlockItem> registerUniversalCable(
            BlockTypeTile<TileEntityUniversalCable> type) {
        return registerTieredBlock(type, "_universal_cable", properties -> new BlockSmallTransmitter<>(type, properties), BlockItem::new);
    }

    private static BlockRegistryObject<BlockLargeTransmitter<TileEntityMechanicalPipe>, BlockItem> registerMechanicalPipe(
            BlockTypeTile<TileEntityMechanicalPipe> type) {
        return registerTieredBlock(type, "_mechanical_pipe", properties -> new BlockLargeTransmitter<>(type, properties), BlockItem::new);
    }

    private static BlockRegistryObject<BlockSmallTransmitter<TileEntityPressurizedTube>, BlockItem> registerPressurizedTube(
            BlockTypeTile<TileEntityPressurizedTube> type) {
        return registerTieredBlock(type, "_pressurized_tube", properties -> new BlockSmallTransmitter<>(type, properties), BlockItem::new);
    }

    private static BlockRegistryObject<BlockLargeTransmitter<TileEntityLogisticalTransporter>, BlockItem> registerLogisticalTransporter(
            BlockTypeTile<TileEntityLogisticalTransporter> type) {
        return registerTieredBlock(type, "_logistical_transporter", properties -> new BlockLargeTransmitter<>(type, properties), BlockItem::new);
    }

    private static BlockRegistryObject<BlockSmallTransmitter<TileEntityThermodynamicConductor>, BlockItem> registerThermodynamicConductor(
            BlockTypeTile<TileEntityThermodynamicConductor> type) {
        return registerTieredBlock(type, "_thermodynamic_conductor", properties -> new BlockSmallTransmitter<>(type, properties), BlockItem::new);
    }

    private static BlockRegistryObject<BlockTileModel<TileEntityChemicalTank, Machine<TileEntityChemicalTank>>, ItemBlockChemicalTank> registerChemicalTank(
            Machine<TileEntityChemicalTank> type) {
        return registerTieredBlock(type, "_chemical_tank", (properties, color) -> new BlockTileModel<>(type, BlockTileModel.defaultProperties(properties).mapColor(color)), ItemBlockChemicalTank::new)
                .forItemHolder(holder -> holder
                        .addAttachedContainerCapabilities(ContainerType.CHEMICAL, () -> ChemicalTanksBuilder.builder()
                                .addContainer(ComponentBackedChemicalTankTank::create).build()
                        ).addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                                .addChemicalDrainSlot(0)
                                .addChemicalFillSlot(0)
                                .build()
                        )
                );
    }

    private static <TILE extends TileEntityFactory<?>> BlockRegistryObject<BlockFactory<?>, ItemBlockTooltip> registerFactory(Factory<TILE> type) {
        FactoryTier tier = (FactoryTier) type.getOrThrow(AttributeTier.class).tier();
        FactoryType factoryType = type.getOrThrow(AttributeFactoryType.class).getFactoryType();
        AttachedSideConfig sideConfig;
        if (factoryType == EMFactoryType.ALLOYING) {
            sideConfig = AttachedSideConfig.EXTRA_MACHINE;
        } else if (factoryType == EMFactoryType.FLUID_ALLOYING) {
            sideConfig = EMAttachedSideConfig.FLUID_ALLOYER_MACHINE;
        } else if (factoryType == EMFactoryType.FLUID_COMBINING) {
            sideConfig = EMAttachedSideConfig.FLUID_COMBINER_MACHINE;
        } else {
            sideConfig = switch (factoryType) {
                case SMELTING, ENRICHING, CRUSHING, SAWING -> AttachedSideConfig.ELECTRIC_MACHINE;
                case COMPRESSING, INFUSING -> AttachedSideConfig.ADVANCED_MACHINE;
                case COMBINING -> AttachedSideConfig.EXTRA_MACHINE;
                case PURIFYING, INJECTING -> AttachedSideConfig.ADVANCED_MACHINE_INPUT_ONLY;
                default -> AttachedSideConfig.ELECTRIC_MACHINE;
            };
        }
        BlockRegistryObject<BlockFactory<?>, ItemBlockTooltip> factory = registerTieredBlock(tier, "_" + factoryType.getSerializedName() + "_factory", properties -> new BlockFactory<>(type, properties),
                (block, properties) -> new ItemBlockTooltip(block, properties
                        .component(MekanismDataComponents.FACTORY_TIER, tier)
                        .component(MekanismDataComponents.FACTORY_TYPE, factoryType)
                        .component(MekanismDataComponents.SORTING, false)
                        .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                        .component(MekanismDataComponents.SIDE_CONFIG, sideConfig)
                        .component(MekanismDataComponents.DETAILS, Unit.INSTANCE)
                ));
        factory.forItemHolder(holder -> {
            int processes = tier.processes;
            Predicate<ItemResource> recipeInputPredicate = getItemPredicate(factoryType);
            if (factoryType == EMFactoryType.ALLOYING) {
                holder.addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                        .addBasicFactorySlots(processes, recipeInputPredicate)
                        .addInput(EMRecipeType.ALLOYING, EMInputRecipeCache.TripleItem::containsInputB)
                        .addInput(EMRecipeType.ALLOYING, EMInputRecipeCache.TripleItem::containsInputC)
                        .addEnergy()
                        .build()
                );
                return;
            }
            switch (factoryType) {
                case SMELTING, ENRICHING, CRUSHING -> holder.addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                        .addBasicFactorySlots(processes, recipeInputPredicate)
                        .addEnergy()
                        .build()
                );
                case COMPRESSING, INJECTING, PURIFYING -> holder
                        .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> ChemicalTanksBuilder.builder()
                                .addBasic(TileEntityAdvancedElectricMachine.MAX_GAS * processes, (IMekanismRecipeTypeProvider<?, ?, ?, ?, ? extends InputRecipeCache.ItemChemical<?>>) switch (factoryType) {
                                    case COMPRESSING -> MekanismRecipeType.COMPRESSING;
                                    case INJECTING -> MekanismRecipeType.INJECTING;
                                    case PURIFYING -> MekanismRecipeType.PURIFYING;
                                    default -> throw new IllegalStateException("Factory type doesn't have a known gas recipe");
                                }, InputRecipeCache.ItemChemical::containsInputB)
                                .build()
                        ).addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                                .addBasicFactorySlots(processes, recipeInputPredicate)
                                .addChemicalFillOrConvertSlot(0)
                                .addEnergy()
                                .build()
                        );
                case COMBINING -> holder.addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                        .addBasicFactorySlots(processes, recipeInputPredicate)
                        .addInput(MekanismRecipeType.COMBINING, InputRecipeCache.DoubleItem::containsInputB)
                        .addEnergy()
                        .build()
                );
                case INFUSING -> holder
                        .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> ChemicalTanksBuilder.builder()
                                .addBasic(TileEntityMetallurgicInfuser.MAX_INFUSE * processes, MekanismRecipeType.METALLURGIC_INFUSING, InputRecipeCache.ItemChemical::containsInputB)
                                .build()
                        ).addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                                .addBasicFactorySlots(processes, recipeInputPredicate)
                                .addChemicalFillOrConvertSlot(0)
                                .addEnergy()
                                .build()
                        );
                case SAWING -> holder.addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                        .addBasicFactorySlots(processes, recipeInputPredicate, true)
                        .addEnergy()
                        .build()
                );
                default -> {
                }
            }
        });
        return factory;
    }

    private static Predicate<ItemResource> getItemPredicate(FactoryType factoryType) {
        if (factoryType == EMFactoryType.ALLOYING) {
            return s -> EMRecipeType.ALLOYING.getInputCache().containsInputA(null, s);
        }
        return switch (factoryType) {
            case SMELTING -> s -> MekanismRecipeType.SMELTING.getInputCache().containsInput(null, s);
            case ENRICHING -> s -> MekanismRecipeType.ENRICHING.getInputCache().containsInput(null, s);
            case CRUSHING -> s -> MekanismRecipeType.CRUSHING.getInputCache().containsInput(null, s);
            case COMPRESSING -> s -> MekanismRecipeType.COMPRESSING.getInputCache().containsInputA(null, s);
            case COMBINING -> s -> MekanismRecipeType.COMBINING.getInputCache().containsInputA(null, s);
            case PURIFYING -> s -> MekanismRecipeType.PURIFYING.getInputCache().containsInputA(null, s);
            case INJECTING -> s -> MekanismRecipeType.INJECTING.getInputCache().containsInputA(null, s);
            case INFUSING -> s -> MekanismRecipeType.METALLURGIC_INFUSING.getInputCache().containsInputA(null, s);
            case SAWING -> s -> MekanismRecipeType.SAWING.getInputCache().containsInput(null, s);
            default -> s -> false;
        };
    }

    private static <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerTieredBlock(BlockType type, String suffix,
          BiFunction<BlockBehaviour.Properties, MapColor, ? extends BLOCK> blockCreator, BiFunction<BLOCK, Item.Properties, ITEM> itemCreator) {
        ITier tier = type.getOrThrow(AttributeTier.class).tier();
        return registerTieredBlock(tier, suffix, properties -> blockCreator.apply(properties, tier.getBaseTier().getMapColor()), itemCreator);
    }

    private static <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerTieredBlock(BlockType type, String suffix,
          Function<BlockBehaviour.Properties, ? extends BLOCK> blockCreator, BiFunction<BLOCK, Item.Properties, ITEM> itemCreator) {
        return registerTieredBlock(type.getOrThrow(AttributeTier.class).tier(), suffix, blockCreator, itemCreator);
    }

    private static <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerTieredBlock(ITier tier, String suffix,
          Function<BlockBehaviour.Properties, ? extends BLOCK> blockSupplier, BiFunction<BLOCK, Item.Properties, ITEM> itemCreator) {
        return BLOCKS.register(tier.getBaseTier().getLowerName() + suffix, blockSupplier, itemCreator);
    }

    private static BlockRegistryObject<Block, BlockItem> registerAlloyBlock(AlloyTier tier) {
        ((InitializableEnum) (Object)tier).evolvedmekanism$initNewValues();
        return BLOCKS.register("block_alloy_"+tier.getName(), properties -> new Block(properties.strength(5,9)), BlockItem::new);
    }


    private static BlockRegistryObject<EMBlockResource, EMItemBlockResource> registerResourceBlock(EMBlockResourceInfo resource) {
        return BLOCKS.register("block_" + resource.getRegistrySuffix(), properties -> new EMBlockResource(resource, properties), (block, properties) -> {
            if (!block.getResourceInfo().burnsInFire()) {
                properties = properties.fireResistant();
            }
            return new EMItemBlockResource(block, properties);
        });
    }
    public static BlockRegistryObject<BlockFactory<?>, ItemBlockTooltip> getFactory(@NotNull FactoryTier tier, @NotNull FactoryType type) {
        BlockRegistryObject<BlockFactory<?>, ItemBlockTooltip> factory = getRegisteredFactory(tier, type);
        if (factory == null) {
            factory = MekanismBlocks.getFactory(tier, type);
        }
        if (factory == null){
            EvolvedMekanism.logger.error("Failed to find factory block for tier {} and type {}, returning null", tier, type);
        }
        return factory;
    }

    public static BlockRegistryObject<BlockFactory<?>, ItemBlockTooltip> getRegisteredFactory(@NotNull FactoryTier tier, @NotNull FactoryType type) {
        return FACTORIES.get(tier, type);
    }

}
