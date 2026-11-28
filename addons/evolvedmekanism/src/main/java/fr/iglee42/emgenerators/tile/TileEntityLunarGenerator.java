package fr.iglee42.emgenerators.tile;

import fr.iglee42.emgenerators.registries.EMGenBlocks;
import fr.iglee42.evolvedmekanism.utils.EMNight;
import fr.iglee42.evolvedmekanism.interfaces.EMUpgradeIds;
import mekanism.api.math.MathUtils;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableBoolean;
import mekanism.common.inventory.container.sync.SyncableInt;
import mekanism.common.util.LightLevelUtils;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.tile.TileEntitySolarGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileEntityLunarGenerator extends TileEntitySolarGenerator {

    private boolean seesMoon;
    private int lunarProduction;

    @Nullable
    protected LunarCheck lunarCheck;

    public TileEntityLunarGenerator(BlockPos pos, BlockState state) {
        this(EMGenBlocks.LUNAR_GENERATOR, pos, state);
    }

    protected TileEntityLunarGenerator(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @ComputerMethod(nameOverride = "canSeeMoon")
    @Override
    public boolean canSeeSun() {
        return seesMoon;
    }

    @Override
    protected boolean onUpdateServer(ServerLevel level) {
        //The base class drives the tick, recheckSettings/checkCanSeeSun/getProduction are all overridden to use the moon instead of the sun
        boolean sendUpdatePacket = super.onUpdateServer(level);
        lunarProduction = seesMoon && canFunction() ? getProduction() : 0;
        return sendUpdatePacket;
    }

    @Override
    protected void recheckSettings() {
        if (level == null || lunarCheck != null) {
            return;
        }
        lunarCheck = new LunarCheck(level, worldPosition);
    }

    @Override
    protected boolean checkCanSeeSun() {
        return checkCanSeeMoon();
    }

    protected boolean checkCanSeeMoon() {
        if (lunarCheck == null) {
            seesMoon = false;
            return false;
        }
        lunarCheck.recheckCanSeeMoon();
        seesMoon = lunarCheck.canSeeMoon();
        return seesMoon;
    }

    @Override
    public int getProduction() {
        if (level == null || lunarCheck == null) {
            return 0;
        }
        float brightness = getBrightnessMultiplier(level);
        return MathUtils.clampToInt(getConfiguredMax() * (brightness * lunarCheck.getGenerationMultiplier()));
    }

    @Override
    protected float getBrightnessMultiplier(@NotNull Level world) {
        return 1.0F - LightLevelUtils.getSunBrightness(world, worldPosition);
    }

    @Override
    public int getProductionRate() {
        return lunarProduction;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableBoolean.create(this::canSeeSun, value -> seesMoon = value));
        container.track(SyncableInt.create(this::getProductionRate, value -> lunarProduction = value));
    }

    @Override
    protected int getConfiguredMax() {
        int modifier = 1 + EMUpgradeIds.getUpgrades(this, EMUpgradeIds.LUNAR);
        return super.getConfiguredMax() * modifier;
    }

    protected static class LunarCheck {

        private final boolean needsRainCheck;
        private final float peakMultiplier;
        protected final BlockPos pos;
        protected final Level world;
        protected boolean canSeeMoon;

        public LunarCheck(Level world, BlockPos pos) {
            this.world = world;
            this.pos = pos;
            int seaLevel = this.world.getSeaLevel();
            Biome b = this.world.getBiomeManager().getBiome(this.pos).value();
            needsRainCheck = b.getPrecipitationAt(this.pos, seaLevel) != Biome.Precipitation.NONE;
            // Consider the best temperature to be 0.8; biomes that are higher than that
            // will suffer an efficiency loss (semiconductors don't like heat); biomes that are cooler
            // get a boost. We scale the efficiency to around 30% so that it doesn't totally dominate
            float tempEff = 0.3F * (0.8F - b.getTemperature(this.pos, seaLevel));

            // Treat rainfall as a proxy for humidity; any humidity works as a drag on overall efficiency.
            // As with temperature, we scale it so that it doesn't overwhelm production. Note the signedness
            // on the scaling factor. Also note that we only use rainfall as a proxy if it CAN rain; some dimensions
            // (like the End) have rainfall set, but can't actually support rain.
            float humidityEff = needsRainCheck ? -0.3F * b.getModifiedClimateSettings().downfall() : 0;
            peakMultiplier = 1.0F + tempEff + humidityEff;
        }

        public void recheckCanSeeMoon() {
            canSeeMoon = world != null && world.dimensionType().hasSkyLight() && world.canSeeSky(pos.above()) && EMNight.isNight(world);
        }

        public boolean canSeeMoon() {
            return canSeeMoon;
        }

        public float getPeakMultiplier() {
            return peakMultiplier;
        }

        public float getGenerationMultiplier() {
            if (!canSeeMoon) {
                return 0;
            }
            if (needsRainCheck && (this.world.isRaining() || this.world.isThundering())) {
                //If the generator is in a biome where it can rain, and it's raining penalize production by 80%
                return peakMultiplier * 0.2F;
            }
            return peakMultiplier;
        }
    }
}
