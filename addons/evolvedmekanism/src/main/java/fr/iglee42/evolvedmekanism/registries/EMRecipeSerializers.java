package fr.iglee42.evolvedmekanism.registries;

import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function6;
import com.mojang.datafixers.util.Function9;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import fr.iglee42.evolvedmekanism.impl.BasicAPTRecipe;
import fr.iglee42.evolvedmekanism.impl.BasicAlloyerRecipe;
import fr.iglee42.evolvedmekanism.impl.BasicChemixerRecipe;
import fr.iglee42.evolvedmekanism.impl.BasicFluidAlloyingRecipe;
import fr.iglee42.evolvedmekanism.impl.BasicFluidCombiningRecipe;
import fr.iglee42.evolvedmekanism.impl.BasicMelterRecipe;
import fr.iglee42.evolvedmekanism.impl.BasicSolidificationRecipe;
import fr.iglee42.evolvedmekanism.recipes.AlloyerRecipe;
import fr.iglee42.evolvedmekanism.recipes.ChemixerRecipe;
import fr.iglee42.evolvedmekanism.recipes.FluidAlloyingRecipe;
import fr.iglee42.evolvedmekanism.recipes.FluidCombiningRecipe;
import fr.iglee42.evolvedmekanism.recipes.MeltingRecipe;
import fr.iglee42.evolvedmekanism.recipes.SolidificationRecipe;
import fr.iglee42.evolvedmekanism.utils.EMSerializationConstants;
import mekanism.api.SerializationConstants;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.book.CombiningBookCategory.CombiningBookInfo;
import mekanism.api.recipes.book.CompressingBookCategory.CompressingBookInfo;
import mekanism.api.recipes.book.InjectingBookCategory.InjectingBookInfo;
import mekanism.api.recipes.book.LiquificationBookCategory.LiquificationBookInfo;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.common.recipe.serializer.MekanismRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EMRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, EvolvedMekanism.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BasicAlloyerRecipe>> ALLOYER = RECIPE_SERIALIZERS.register("alloying", () -> alloyer(BasicAlloyerRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BasicChemixerRecipe>> CHEMIXER = RECIPE_SERIALIZERS.register("chemixing", () -> chemixer(BasicChemixerRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BasicAPTRecipe>> APT = RECIPE_SERIALIZERS.register("apt",
          () -> MekanismRecipeSerializer.itemChemicalToItem(BasicAPTRecipe::new, CompressingBookInfo.MAP_CODEC, CompressingBookInfo.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BasicMelterRecipe>> MELTER = RECIPE_SERIALIZERS.register("melting", () -> itemToFluid(BasicMelterRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BasicSolidificationRecipe>> SOLIDIFICATION = RECIPE_SERIALIZERS.register("solidifying", () -> solidifier(BasicSolidificationRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BasicFluidCombiningRecipe>> FLUID_COMBINING = RECIPE_SERIALIZERS.register("fluid_combining", () -> fluidCombining(BasicFluidCombiningRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BasicFluidAlloyingRecipe>> FLUID_ALLOYING = RECIPE_SERIALIZERS.register("fluid_alloying", () -> fluidAlloying(BasicFluidAlloyingRecipe::new));

    public static <RECIPE extends BasicAlloyerRecipe> RecipeSerializer<RECIPE> alloyer(
          Function6<Recipe.CommonInfo, CombiningBookInfo, ItemStackIngredient, ItemStackIngredient, ItemStackIngredient, ItemStackTemplate, RECIPE> factory) {
        return new RecipeSerializer<>(RecordCodecBuilder.mapCodec(instance -> instance.group(
              Recipe.CommonInfo.MAP_CODEC.forGetter(MekanismRecipe::commonInfo),
              CombiningBookInfo.MAP_CODEC.forGetter(MekanismRecipe::bookInfo),
              ItemStackIngredient.CODEC.fieldOf(SerializationConstants.MAIN_INPUT).forGetter(AlloyerRecipe::getMainInput),
              ItemStackIngredient.CODEC.fieldOf(SerializationConstants.EXTRA_INPUT).forGetter(AlloyerRecipe::getExtraInput),
              ItemStackIngredient.CODEC.fieldOf(EMSerializationConstants.SECOND_EXTRA_INPUT).forGetter(AlloyerRecipe::getTertiaryExtraInput),
              ItemStackTemplate.CODEC.fieldOf(SerializationConstants.OUTPUT).forGetter(BasicAlloyerRecipe::getOutputRaw)
        ).apply(instance, factory)), StreamCodec.composite(
              Recipe.CommonInfo.STREAM_CODEC, MekanismRecipe::commonInfo,
              CombiningBookInfo.STREAM_CODEC, MekanismRecipe::bookInfo,
              ItemStackIngredient.STREAM_CODEC, AlloyerRecipe::getMainInput,
              ItemStackIngredient.STREAM_CODEC, AlloyerRecipe::getExtraInput,
              ItemStackIngredient.STREAM_CODEC, AlloyerRecipe::getTertiaryExtraInput,
              ItemStackTemplate.STREAM_CODEC, BasicAlloyerRecipe::getOutputRaw,
              factory::apply
        ));
    }

    public static <RECIPE extends BasicFluidCombiningRecipe> RecipeSerializer<RECIPE> fluidCombining(
          Function5<Recipe.CommonInfo, CombiningBookInfo, FluidStackIngredient, FluidStackIngredient, FluidStackTemplate, RECIPE> factory) {
        return new RecipeSerializer<>(RecordCodecBuilder.mapCodec(instance -> instance.group(
              Recipe.CommonInfo.MAP_CODEC.forGetter(MekanismRecipe::commonInfo),
              CombiningBookInfo.MAP_CODEC.forGetter(MekanismRecipe::bookInfo),
              FluidStackIngredient.CODEC.fieldOf(SerializationConstants.MAIN_INPUT).forGetter(FluidCombiningRecipe::getMainInput),
              FluidStackIngredient.CODEC.fieldOf(SerializationConstants.EXTRA_INPUT).forGetter(FluidCombiningRecipe::getExtraInput),
              FluidStackTemplate.MAP_CODEC.fieldOf(SerializationConstants.OUTPUT).forGetter(FluidCombiningRecipe::getOutputRaw)
        ).apply(instance, factory)), StreamCodec.composite(
              Recipe.CommonInfo.STREAM_CODEC, MekanismRecipe::commonInfo,
              CombiningBookInfo.STREAM_CODEC, MekanismRecipe::bookInfo,
              FluidStackIngredient.STREAM_CODEC, FluidCombiningRecipe::getMainInput,
              FluidStackIngredient.STREAM_CODEC, FluidCombiningRecipe::getExtraInput,
              FluidStackTemplate.STREAM_CODEC, FluidCombiningRecipe::getOutputRaw,
              factory::apply
        ));
    }

    public static <RECIPE extends BasicFluidAlloyingRecipe> RecipeSerializer<RECIPE> fluidAlloying(
          Function6<Recipe.CommonInfo, CombiningBookInfo, FluidStackIngredient, FluidStackIngredient, FluidStackIngredient, FluidStackTemplate, RECIPE> factory) {
        return new RecipeSerializer<>(RecordCodecBuilder.mapCodec(instance -> instance.group(
              Recipe.CommonInfo.MAP_CODEC.forGetter(MekanismRecipe::commonInfo),
              CombiningBookInfo.MAP_CODEC.forGetter(MekanismRecipe::bookInfo),
              FluidStackIngredient.CODEC.fieldOf(SerializationConstants.MAIN_INPUT).forGetter(FluidAlloyingRecipe::getMainInput),
              FluidStackIngredient.CODEC.fieldOf(SerializationConstants.EXTRA_INPUT).forGetter(FluidAlloyingRecipe::getExtraInput),
              FluidStackIngredient.CODEC.fieldOf(EMSerializationConstants.SECOND_EXTRA_INPUT).forGetter(FluidAlloyingRecipe::getTertiaryExtraInput),
              FluidStackTemplate.MAP_CODEC.fieldOf(SerializationConstants.OUTPUT).forGetter(FluidAlloyingRecipe::getOutputRaw)
        ).apply(instance, factory)), StreamCodec.composite(
              Recipe.CommonInfo.STREAM_CODEC, MekanismRecipe::commonInfo,
              CombiningBookInfo.STREAM_CODEC, MekanismRecipe::bookInfo,
              FluidStackIngredient.STREAM_CODEC, FluidAlloyingRecipe::getMainInput,
              FluidStackIngredient.STREAM_CODEC, FluidAlloyingRecipe::getExtraInput,
              FluidStackIngredient.STREAM_CODEC, FluidAlloyingRecipe::getTertiaryExtraInput,
              FluidStackTemplate.STREAM_CODEC, FluidAlloyingRecipe::getOutputRaw,
              factory::apply
        ));
    }

    public static <RECIPE extends BasicMelterRecipe> RecipeSerializer<RECIPE> itemToFluid(
          Function4<Recipe.CommonInfo, LiquificationBookInfo, ItemStackIngredient, FluidStackTemplate, RECIPE> factory) {
        return new RecipeSerializer<>(RecordCodecBuilder.mapCodec(instance -> instance.group(
              Recipe.CommonInfo.MAP_CODEC.forGetter(MekanismRecipe::commonInfo),
              LiquificationBookInfo.MAP_CODEC.forGetter(MekanismRecipe::bookInfo),
              ItemStackIngredient.CODEC.fieldOf(SerializationConstants.INPUT).forGetter(MeltingRecipe::getInput),
              FluidStackTemplate.MAP_CODEC.fieldOf(SerializationConstants.OUTPUT).forGetter(MeltingRecipe::getOutputRaw)
        ).apply(instance, factory)), StreamCodec.composite(
              Recipe.CommonInfo.STREAM_CODEC, MekanismRecipe::commonInfo,
              LiquificationBookInfo.STREAM_CODEC, MekanismRecipe::bookInfo,
              ItemStackIngredient.STREAM_CODEC, MeltingRecipe::getInput,
              FluidStackTemplate.STREAM_CODEC, MeltingRecipe::getOutputRaw,
              factory::apply
        ));
    }

    public static <RECIPE extends BasicChemixerRecipe> RecipeSerializer<RECIPE> chemixer(
          Function6<Recipe.CommonInfo, InjectingBookInfo, ItemStackIngredient, ItemStackIngredient, ChemicalStackIngredient, ItemStackTemplate, RECIPE> factory) {
        return new RecipeSerializer<>(RecordCodecBuilder.mapCodec(instance -> instance.group(
              Recipe.CommonInfo.MAP_CODEC.forGetter(MekanismRecipe::commonInfo),
              InjectingBookInfo.MAP_CODEC.forGetter(MekanismRecipe::bookInfo),
              ItemStackIngredient.CODEC.fieldOf(SerializationConstants.MAIN_INPUT).forGetter(ChemixerRecipe::getInputMain),
              ItemStackIngredient.CODEC.fieldOf(SerializationConstants.EXTRA_INPUT).forGetter(ChemixerRecipe::getInputExtra),
              ChemicalStackIngredient.CODEC.fieldOf(SerializationConstants.CHEMICAL_INPUT).forGetter(ChemixerRecipe::getInputGas),
              ItemStackTemplate.CODEC.fieldOf(SerializationConstants.OUTPUT).forGetter(BasicChemixerRecipe::getOutputRaw)
        ).apply(instance, factory)), StreamCodec.composite(
              Recipe.CommonInfo.STREAM_CODEC, MekanismRecipe::commonInfo,
              InjectingBookInfo.STREAM_CODEC, MekanismRecipe::bookInfo,
              ItemStackIngredient.STREAM_CODEC, ChemixerRecipe::getInputMain,
              ItemStackIngredient.STREAM_CODEC, ChemixerRecipe::getInputExtra,
              ChemicalStackIngredient.STREAM_CODEC, ChemixerRecipe::getInputGas,
              ItemStackTemplate.STREAM_CODEC, BasicChemixerRecipe::getOutputRaw,
              factory::apply
        ));
    }

    public static <RECIPE extends BasicSolidificationRecipe> RecipeSerializer<RECIPE> solidifier(
          Function9<Recipe.CommonInfo, CombiningBookInfo, ItemStackIngredient, FluidStackIngredient, FluidStackIngredient, Long, Integer, ItemStackTemplate, Boolean, RECIPE> factory) {
        return new RecipeSerializer<>(RecordCodecBuilder.mapCodec(instance -> instance.group(
              Recipe.CommonInfo.MAP_CODEC.forGetter(MekanismRecipe::commonInfo),
              CombiningBookInfo.MAP_CODEC.forGetter(MekanismRecipe::bookInfo),
              ItemStackIngredient.CODEC.fieldOf(SerializationConstants.ITEM_INPUT).forGetter(SolidificationRecipe::getInputSolid),
              FluidStackIngredient.CODEC.fieldOf(SerializationConstants.FLUID_INPUT).forGetter(SolidificationRecipe::getInputFluid),
              FluidStackIngredient.CODEC.fieldOf(SerializationConstants.EXTRA_INPUT).forGetter(SolidificationRecipe::getFluidInputExtra),
              Codec.LONG.optionalFieldOf(SerializationConstants.ENERGY_REQUIRED, 0L).forGetter(SolidificationRecipe::getEnergyRequired),
              Codec.INT.optionalFieldOf(SerializationConstants.DURATION, 100).forGetter(SolidificationRecipe::getDuration),
              ItemStackTemplate.CODEC.fieldOf(SerializationConstants.OUTPUT).forGetter(SolidificationRecipe::getOutputRaw),
              Codec.BOOL.optionalFieldOf(EMSerializationConstants.KEEP_ITEM, true).forGetter(SolidificationRecipe::shouldKeepItem)
        ).apply(instance, factory::apply)), composite9(
              Recipe.CommonInfo.STREAM_CODEC, MekanismRecipe::commonInfo,
              CombiningBookInfo.STREAM_CODEC, MekanismRecipe::bookInfo,
              ItemStackIngredient.STREAM_CODEC, SolidificationRecipe::getInputSolid,
              FluidStackIngredient.STREAM_CODEC, SolidificationRecipe::getInputFluid,
              FluidStackIngredient.STREAM_CODEC, SolidificationRecipe::getFluidInputExtra,
              ByteBufCodecs.VAR_LONG, SolidificationRecipe::getEnergyRequired,
              ByteBufCodecs.VAR_INT, SolidificationRecipe::getDuration,
              ItemStackTemplate.STREAM_CODEC, SolidificationRecipe::getOutputRaw,
              ByteBufCodecs.BOOL, SolidificationRecipe::shouldKeepItem,
              factory
        ));
    }

    private static <C, T1, T2, T3, T4, T5, T6, T7, T8, T9> StreamCodec<RegistryFriendlyByteBuf, C> composite9(
          StreamCodec<? super RegistryFriendlyByteBuf, T1> c1, java.util.function.Function<C, T1> g1,
          StreamCodec<? super RegistryFriendlyByteBuf, T2> c2, java.util.function.Function<C, T2> g2,
          StreamCodec<? super RegistryFriendlyByteBuf, T3> c3, java.util.function.Function<C, T3> g3,
          StreamCodec<? super RegistryFriendlyByteBuf, T4> c4, java.util.function.Function<C, T4> g4,
          StreamCodec<? super RegistryFriendlyByteBuf, T5> c5, java.util.function.Function<C, T5> g5,
          StreamCodec<? super RegistryFriendlyByteBuf, T6> c6, java.util.function.Function<C, T6> g6,
          StreamCodec<? super RegistryFriendlyByteBuf, T7> c7, java.util.function.Function<C, T7> g7,
          StreamCodec<? super RegistryFriendlyByteBuf, T8> c8, java.util.function.Function<C, T8> g8,
          StreamCodec<? super RegistryFriendlyByteBuf, T9> c9, java.util.function.Function<C, T9> g9,
          Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, C> factory) {
        return new StreamCodec<>() {
            @Override
            public C decode(RegistryFriendlyByteBuf buf) {
                T1 t1 = c1.decode(buf);
                T2 t2 = c2.decode(buf);
                T3 t3 = c3.decode(buf);
                T4 t4 = c4.decode(buf);
                T5 t5 = c5.decode(buf);
                T6 t6 = c6.decode(buf);
                T7 t7 = c7.decode(buf);
                T8 t8 = c8.decode(buf);
                T9 t9 = c9.decode(buf);
                return factory.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, C value) {
                c1.encode(buf, g1.apply(value));
                c2.encode(buf, g2.apply(value));
                c3.encode(buf, g3.apply(value));
                c4.encode(buf, g4.apply(value));
                c5.encode(buf, g5.apply(value));
                c6.encode(buf, g6.apply(value));
                c7.encode(buf, g7.apply(value));
                c8.encode(buf, g8.apply(value));
                c9.encode(buf, g9.apply(value));
            }
        };
    }
}
