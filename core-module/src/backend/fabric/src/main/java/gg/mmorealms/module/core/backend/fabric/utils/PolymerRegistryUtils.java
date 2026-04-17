package gg.mmorealms.module.core.backend.fabric.utils;

import com.mojang.serialization.Codec;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import gg.mmorealms.module.core.backend.common.utils.ResourceUtils;
import gg.mmorealms.module.core.backend.fabric.items.PolymerModelBlockItem;
import gg.mmorealms.module.core.backend.fabric.items.PolymerNamedBlockItem;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Utils used for common registration logic for server side content via Polymer
 */
public class PolymerRegistryUtils {

    private PolymerRegistryUtils() { }

    public static <K, T> Map<K, T> buildLinkedMap(Stream<K> keys, Function<K, T> mapper) {
        return keys.collect(Collectors.toMap(
                k -> k,
                mapper,
                (a, b) -> a,
                LinkedHashMap::new // preserves order of the stream
        ));
    }

    public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String path, BlockEntityType.BlockEntitySupplier<T> supplier, Block... blocks) {
        ResourceKey<BlockEntityType<?>> key = registerResourceKey(Registries.BLOCK_ENTITY_TYPE, path);
        BlockEntityType<T> blockEntityType = BlockEntityType.Builder.of(supplier, blocks).build();

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, key, blockEntityType);
        PolymerBlockUtils.registerBlockEntity(blockEntityType);

        return blockEntityType;
    }

    public static <T extends Block> T registerBlock(String path, Supplier<T> block) {
        ResourceKey<Block> key = registerResourceKey(Registries.BLOCK, path);

        return Registry.register(
                BuiltInRegistries.BLOCK,
                key, block.get());
    }

    public static <T extends Block> T registerBlock(String path, T block) {
        ResourceKey<Block> key = registerResourceKey(Registries.BLOCK, path);

        return Registry.register(
                BuiltInRegistries.BLOCK,
                key, block);
    }

    public static BlockState registerBlockState(String path, BlockModelType blockModelType) {
        ResourceLocation resourceLocation = ResourceUtils.modResource("block/" + path);
        PolymerBlockModel blockModel = PolymerBlockModel.of(resourceLocation);

        BlockState blockState = PolymerBlockResourceUtils.requestBlock(blockModelType, blockModel);

        return blockState == null
                ? Blocks.GOLD_BLOCK.defaultBlockState()
                : blockState;
    }

    public static <T> DataComponentType<T> registerDataComponentType(String name, Codec<T> codec) {
        DataComponentType<T> type = DataComponentType.<T>builder()
                .persistent(codec)
                .build();

        PolymerComponent.registerDataComponent(type);

        return Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                ResourceUtils.modResource(name),
                type
        );
    }

    public static <T extends Item> T registerItem(String path, Function<Item.Properties, T> function) {
        ResourceKey<Item> key = registerItemKey(path);

        return Registry.register(
                BuiltInRegistries.ITEM,
                key, function.apply(new Item.Properties()));
    }

    public static <T extends Item> T registerItem(String path, Supplier<T> supplier) {
        ResourceKey<Item> key = registerItemKey(path);

        return Registry.register(
                BuiltInRegistries.ITEM,
                key, supplier.get());
    }

    public static <T extends Item> T registerItem(String path, T item) {
        ResourceKey<Item> key = registerItemKey(path);

        return Registry.register(
                BuiltInRegistries.ITEM,
                key, item);
    }

    public static BlockItem registerBlockItem(String path, Item virtualItem, Block block) {
        ResourceKey<Item> key = registerItemKey(path);
        BlockItem blockItem = new PolymerModelBlockItem(path, virtualItem, block);

        return Registry.register(
                BuiltInRegistries.ITEM,
                key, blockItem);
    }

    public static BlockItem registerNamedBlockItem(String name, String path, Item virtualItem, Block block) {
        ResourceKey<Item> key = registerItemKey(path);
        BlockItem blockItem = new PolymerNamedBlockItem(name, path, virtualItem, block);

        return Registry.register(
                BuiltInRegistries.ITEM,
                key, blockItem);
    }

    public static <FC extends FeatureConfiguration, F extends Feature<FC>> void registerConfiguredFeature(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key,
                                                                                                          F feature,
                                                                                                          FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }

    public static <FC extends FeatureConfiguration, F extends Feature<FC>> void registerConfiguredFeature(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                                          String path,
                                                                                                          F feature,
                                                                                                          FC configuration) {
        ResourceKey<ConfiguredFeature<?, ?>> key = registerConfigureFeatureKey(path);
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }

    public static void registerPlacedFeature(BootstrapContext<PlacedFeature> context,
                                             String path,
                                             Holder<ConfiguredFeature<?, ?>> configuredFeature,
                                             List<PlacementModifier> placementModifiers) {

        ResourceKey<PlacedFeature> key = registerPlacedFeatureKey(path);
        context.register(key, new PlacedFeature(configuredFeature, placementModifiers));
    }

    public static <T> ResourceKey<T> registerResourceKey(ResourceKey<Registry<T>> key, String path) {
        return ResourceKey.create(key, ResourceUtils.modResource(path));
    }

    public static ResourceKey<Item> registerItemKey(String path) {
        return registerResourceKey(Registries.ITEM, path);
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerConfigureFeatureKey(String path) {
        return registerResourceKey(Registries.CONFIGURED_FEATURE, path);
    }

    public static ResourceKey<PlacedFeature> registerPlacedFeatureKey(String path) {
        return registerResourceKey(Registries.PLACED_FEATURE, path);
    }


    // Copied from minecraft code

    public static List<PlacementModifier> orePlacement(PlacementModifier placementModifier, PlacementModifier placementModifier2) {
        return List.of(placementModifier, InSquarePlacement.spread(), placementModifier2, BiomeFilter.biome());
    }

    public static List<PlacementModifier> commonOrePlacement(int i, PlacementModifier placementModifier) {
        return orePlacement(CountPlacement.of(i), placementModifier);
    }

    public static List<PlacementModifier> rareOrePlacement(int i, PlacementModifier placementModifier) {
        return orePlacement(RarityFilter.onAverageOnceEvery(i), placementModifier);
    }

}