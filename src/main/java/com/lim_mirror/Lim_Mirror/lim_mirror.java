package com.lim_mirror.Lim_Mirror;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(lim_mirror.MODID)
public class lim_mirror {
    public static final String MODID = "lim_mirror";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<net.minecraft.world.item.Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // 呼吸法饰品创造栏
    public static final RegistryObject<CreativeModeTab> POISE_TAB = CREATIVE_MODE_TABS.register("poise_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> new ItemStack(Registration.PIPE.get()))
            .title(net.minecraft.network.chat.Component.translatable("creativeTab.lim_mirror.poise_tab"))
            .displayItems((parameters, output) -> {
                output.accept(Registration.PIPE.get());
                output.accept(Registration.STONE_MOUND.get());
                output.accept(Registration.CLOVER.get());
                output.accept(Registration.MEMORY_PENDANT.get());
                output.accept(Registration.NEBULIZER.get());
                output.accept(Registration.STILL_WATER.get());
                output.accept(Registration.ENDORPHIN_KIT.get());
                output.accept(Registration.HORSESHOE.get());
                output.accept(Registration.LUCKY_BAG.get());
                output.accept(Registration.DEVILS_DELIGHT.get());
                output.accept(Registration.GREEN_ELYTRA.get());
                output.accept(Registration.OLD_WOODEN_FIGURINE.get());
                output.accept(Registration.NOSTALGIA.get());
                output.accept(Registration.MEMORY_OF_A_DAY.get());
                output.accept(Registration.ANGELS_PRIVILEGE.get());
                output.accept(Registration.REMINISCENCE.get());
                output.accept(Registration.BARREL_LIQUOR.get());
                output.accept(Registration.END_OF_EVIL.get());
                output.accept(Registration.COMMAND_SANCTUARY.get());
                output.accept(Registration.SOMEONES_GREEN_BLADE.get());
                output.accept(Registration.ROPE_CATCHER.get());
                output.accept(Registration.WHALE_HEART.get());
                output.accept(Registration.HARPOON_GUN_LEG.get());
                output.accept(Registration.GAS_LAMP.get());
                output.accept(Registration.FUZZY_HAT.get());
                output.accept(Registration.GIANT_GIFT_BAG.get());
                output.accept(Registration.BROKEN_BLADE.get());
                output.accept(Registration.BROKEN_BAMBOO_HAT.get());
                output.accept(Registration.HAPPY_PLUSHIE.get());
                output.accept(Registration.GEAR_SHARD.get());
                output.accept(Registration.MELEE_COMBAT_MANUAL.get());
                output.accept(Registration.STIFLED_BREATH.get());
                output.accept(Registration.MOON_IN_WATER.get());
            }).build());

    // 烧伤饰品创造栏
    public static final RegistryObject<CreativeModeTab> BURN_TAB = CREATIVE_MODE_TABS.register("burn_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> new ItemStack(Registration.HELL_BUTTERFLY_DREAM.get()))
            .title(net.minecraft.network.chat.Component.translatable("creativeTab.lim_mirror.burn_tab"))
            .displayItems((parameters, output) -> {
                output.accept(Registration.HELL_BUTTERFLY_DREAM.get());
                output.accept(Registration.DUST_TO_DUST.get());
                output.accept(Registration.BLAZING_FEATHER.get());
                output.accept(Registration.SINGLE_POINT_LOGIC_CIRCUIT.get());
                output.accept(Registration.YANLING.get());
                output.accept(Registration.EARTH_TO_EARTH.get());
                output.accept(Registration.SCORCHED_DISC.get());
                output.accept(Registration.BLAZING_WISDOM.get());
                output.accept(Registration.REQUIEM.get());
                output.accept(Registration.MELTED_WAX.get());
                output.accept(Registration.POLARIZED_LIGHT.get());
                output.accept(Registration.SUPPRESSED_FIRE.get());
                output.accept(Registration.FIRELIGHT_FLOWER.get());
                output.accept(Registration.ETERNAL_STEWPOT.get());
                output.accept(Registration.ETERNAL_HEARTHFIRE.get());
                output.accept(Registration.COOKING_SECRETS_BOOK.get());
                output.accept(Registration.STOLEN_FLAME.get());
                output.accept(Registration.RED_TIE.get());
                output.accept(Registration.UNIFORM_SIX_ASSOCIATION.get());
                output.accept(Registration.ROYAL_JELLY_PERFUME.get());
                output.accept(Registration.RED_DISASTER_EXTRACT.get());
                output.accept(Registration.BURNING_FATE.get());
                output.accept(Registration.SCORCHING_COPPER_PIPE.get());
                output.accept(Registration.WING_CANDLE.get());
                output.accept(Registration.SPICY_DRUMSTICK.get());
                output.accept(Registration.UNHATCHED_SPARKS.get());
                output.accept(Registration.IGNITION_GLOVES.get());
                output.accept(Registration.REIGNITION_SPARK_PLUG.get());
                output.accept(Registration.EMBERS.get());
                output.accept(Registration.BLOOD_FLAME_BLADE.get());
            }).build());

    // ==================== 震颤饰品创造栏 ====================
    public static final RegistryObject<CreativeModeTab> TREMOR_TAB = CREATIVE_MODE_TABS.register("tremor_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> new ItemStack(Registration.GAO_XUE.get()))
            .title(net.minecraft.network.chat.Component.translatable("creativeTab.lim_mirror.tremor_tab"))
            .displayItems((parameters, output) -> {
                output.accept(Registration.GAO_XUE.get());
                output.accept(Registration.GREEN_LIGHT_FRUIT.get());
                output.accept(Registration.MELTED_EYEBALL.get());
                output.accept(Registration.GLOW_VARIATOR.get());
                output.accept(Registration.SHOCK_BRACELET.get());
                output.accept(Registration.RAINSTORM.get());
                output.accept(Registration.ECHO.get());
                output.accept(Registration.TRUTH_CLOCK.get());
                output.accept(Registration.TREMOR_COUPLING.get());
                output.accept(Registration.BIO_TOXIN_VIAL.get());
                output.accept(Registration.TOXIC_SKIN.get());
                output.accept(Registration.SOUR_WINE_AROMA.get());
                output.accept(Registration.MIRROR_TACTILE_SYNESTHESIA.get());
                output.accept(Registration.GEAR_SPRING.get());
                output.accept(Registration.GEM_PENDULUM.get());
                output.accept(Registration.SHAKING_BARREL.get());
                output.accept(Registration.MESHED_GEAR.get());
                output.accept(Registration.EPICENTER.get());
                output.accept(Registration.EIGHT_DIRECTION_BELL.get());
                output.accept(Registration.CLAP_CLAP.get());
                output.accept(Registration.HEATING_HAMMER.get());
                output.accept(Registration.ONE_MORE_DRINK.get());
                output.accept(Registration.IMPERFECT_FORESIGHT_EYE.get());
                output.accept(Registration.OFFERING_CIGAR.get());
                output.accept(Registration.OFFERING_CIGAR.get());
                output.accept(Registration.CUSTOM_MADE.get());
                output.accept(Registration.HAND_MIRROR.get());
                output.accept(Registration.OILY_WRENCH.get());
                output.accept(Registration.SILVER_WATCH_CASE.get());
                output.accept(Registration.FADED_WATCH_CASE.get());
                output.accept(Registration.ETCHED_HAND.get());
                output.accept(Registration.RUSTED_HAND.get());
                output.accept(Registration.TRICKLE_CUP.get());
                output.accept(Registration.POCKET_WATCH_TYPE_L.get());
                output.accept(Registration.POCKET_WATCH_TYPE_E.get());
                output.accept(Registration.POCKET_WATCH_TYPE_Y.get());
                output.accept(Registration.POCKET_WATCH_TYPE_P.get());
                output.accept(Registration.EXTINGUISHED_LANTERN.get());
                output.accept(Registration.SHADOW_MONSTER.get());
                output.accept(Registration.INTERTWINED_TIME.get());
                output.accept(Registration.FORCED_ENTANGLEMENT_SEQUENCER.get());
                output.accept(Registration.ULTRA_PRECISION_TIME_ACCELERATOR.get());
                output.accept(Registration.BLACK_IRON_BARDING.get());
                output.accept(Registration.FOR_THE_COMMANDER.get());
            }).build());

    public static final RegistryObject<CreativeModeTab> SLASHING_TAB = CREATIVE_MODE_TABS.register("slashing_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> new ItemStack(Registration.DREAMING_ELECTRIC_SHEEP.get()))
            .title(net.minecraft.network.chat.Component.translatable("creativeTab.lim_mirror.slashing_tab"))
            .displayItems((parameters, output) -> {
                output.accept(Registration.DREAMING_ELECTRIC_SHEEP.get());
                output.accept(Registration.SURGICAL_KNIFE.get());
                output.accept(Registration.VAIN_PEACE.get());
                output.accept(Registration.TAILORS_SCISSORS.get());
                output.accept(Registration.RESOLVE.get());
                output.accept(Registration.MOMENT_OF_JUDGMENT.get());
                output.accept(Registration.SEVERED_MEMORY.get());
                output.accept(Registration.WORN_WHETSTONE.get());
                output.accept(Registration.CANE_SWORD.get());
                output.accept(Registration.CLOUD_PATTERNED_CRANE_NECK_VASE.get());
                output.accept(Registration.SHATTERED_GREATSWORD.get());
                output.accept(Registration.SWORD_OF_CALAMUS.get());
                output.accept(Registration.SHATTERED_WOODEN_TORTURE_DEVICE.get());
                output.accept(Registration.UNADORNED_TSUBA.get());
                output.accept(Registration.NATIVE_SWORD_MANUAL.get());
                output.accept(Registration.RUSTY_HANDLE.get());
                output.accept(Registration.RED_TASSEL.get());
                output.accept(Registration.RED_TASSEL.get());
                output.accept(Registration.MAGNIFICENT.get());
                output.accept(Registration.IMMOVABLE.get());
                output.accept(Registration.OLD_ROBE.get());
                output.accept(Registration.OLD_HANDLE.get());
                output.accept(Registration.POLISHING.get());
                output.accept(Registration.MURAKUMO.get());
                output.accept(Registration.SHADOW_WOVEN_HAT.get());
                output.accept(Registration.BLINDING_RED_CLOTH.get());
            }).build());

    // ==================== 打击饰品创造栏 ====================
    public static final RegistryObject<CreativeModeTab> STRIKE_TAB = CREATIVE_MODE_TABS.register("strike_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> new ItemStack(Registration.EXPRESSION_OF_THIS_MOMENT.get()))
            .title(net.minecraft.network.chat.Component.translatable("creativeTab.lim_mirror.strike_tab"))
            .displayItems((parameters, output) -> {
                output.accept(Registration.EXPRESSION_OF_THIS_MOMENT.get());
                output.accept(Registration.STAR_OF_THE_VALLEY.get());
                output.accept(Registration.HAUNTING.get());
                output.accept(Registration.COMPRESSION_BANDAGE.get());
                output.accept(Registration.CHAINS_OF_TIME.get());
                output.accept(Registration.CLENCHED_STATUE.get());
                output.accept(Registration.SHATTERED_MEMORY_STRIKE.get());
                output.accept(Registration.DIMENSIONAL_SORTING_RECYCLING_BIN.get());
                output.accept(Registration.POCKET_MNEMONIC_CARD.get());
                output.accept(Registration.DIMENSIONAL_PERCEPTION_VARIANT.get());
                output.accept(Registration.REVENGE_LEDGER.get());
                output.accept(Registration.MEAT_TENDERIZER.get());
                output.accept(Registration.STOLEN_FIXER_MAGAZINE.get());
                output.accept(Registration.REVENGE_LEDGER_APPENDIX.get());
                output.accept(Registration.MIDDLE_FINGER_KNUCKLE.get());
                output.accept(Registration.DEBUT_EXCLUSIVE_SPEAKER.get());
                output.accept(Registration.YOUNG_BROTHER_DEBUT.get());
                output.accept(Registration.ENHANCED_TATTOO_MIDDLE_FINGER.get());
                output.accept(Registration.LOYALTY_CHAINS.get());
                output.accept(Registration.ETERNAL_CHAINS.get());
                output.accept(Registration.MIDDLE_FINGER_RULES.get());
            }).build());

    // ==================== 流血饰品创造栏 ====================
    public static final RegistryObject<CreativeModeTab> BLEED_TAB = CREATIVE_MODE_TABS.register("bleed_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> new ItemStack(Registration.MIMIC_WOUND_BUG.get()))
            .title(net.minecraft.network.chat.Component.translatable("creativeTab.lim_mirror.bleed_tab"))
            .displayItems((parameters, output) -> {
                output.accept(Registration.MIMIC_WOUND_BUG.get());
                output.accept(Registration.BLOODY_IRON_SPIKE.get());
                output.accept(Registration.WHITE_COTTON.get());
                output.accept(Registration.TINY_DOLL.get());
            }).build());

    public lim_mirror(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        Registration.init(modEventBus);
        Sounds.init(modEventBus);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        MinecraftForge.EVENT_BUS.register(this);
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);
        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // ==================== 附魔书放入原材料创造栏 ====================
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            ItemStack boilingCorrosionBook = EnchantedBookItem.createForEnchantment(
                    new EnchantmentInstance(Registration.BOILING_CORROSION.get(), 1)
            );
            event.accept(boilingCorrosionBook);

            // ==================== 空间撕裂附魔书 ====================
            ItemStack spaceTearBook = EnchantedBookItem.createForEnchantment(
                    new EnchantmentInstance(Registration.SPACE_TEAR.get(), 1)
            );
            event.accept(spaceTearBook);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}