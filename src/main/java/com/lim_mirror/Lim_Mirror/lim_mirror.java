package com.lim_mirror.Lim_Mirror;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
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
            .title(net.minecraft.network.chat.Component.literal("呼吸法饰品"))
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
            .title(net.minecraft.network.chat.Component.literal("烧伤饰品"))
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
            }).build());

    public lim_mirror(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        Registration.init(modEventBus);
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

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
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