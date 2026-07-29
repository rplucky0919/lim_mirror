package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registration {

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, lim_mirror.MODID);

    public static final RegistryObject<MobEffect> POISE = EFFECTS.register("poise", Poise::new);
    public static final RegistryObject<MobEffect> BLEED = EFFECTS.register("bleed", BleedEffect::new);
    public static final RegistryObject<MobEffect> SINKING = EFFECTS.register("sinking", SinkingEffect::new);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, lim_mirror.MODID);

    public static final RegistryObject<Item> PIPE = ITEMS.register("pipe",
            () -> new pipe(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> STONE_MOUND = ITEMS.register("stone_mound",
            () -> new StoneMound(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> CLOVER = ITEMS.register("clover",
            () -> new Clover(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> MEMORY_PENDANT = ITEMS.register("memory_pendant",
            () -> new MemoryPendant(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> NEBULIZER = ITEMS.register("nebulizer",
            () -> new Nebulizer(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> STILL_WATER = ITEMS.register("still_water",
            () -> new StillWater(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ENDORPHIN_KIT = ITEMS.register("endorphin_kit",
            () -> new EndorphinKit(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> HORSESHOE = ITEMS.register("horseshoe",
            () -> new Horseshoe(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> LUCKY_BAG = ITEMS.register("lucky_bag",
            () -> new LuckyBag(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> DEVILS_DELIGHT = ITEMS.register("devils_delight",
            () -> new DevilsDelight(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> GREEN_ELYTRA = ITEMS.register("green_elytra",
            () -> new GreenElytra(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> OLD_WOODEN_FIGURINE = ITEMS.register("old_wooden_figurine",
            () -> new OldWoodenFigurine(new Item.Properties().stacksTo(1)));

    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, lim_mirror.MODID);

    public static final RegistryObject<Enchantment> BREATH_SMOOTH =
            ENCHANTMENTS.register("breath_smooth", BreathSmoothEnchantment::new);

    public static final RegistryObject<Enchantment> SANCHO_BLOOD =
            ENCHANTMENTS.register("sancho_blood", SanchoBloodEnchantment::new);

    public static final RegistryObject<Item> NOSTALGIA = ITEMS.register("nostalgia",
            () -> new Nostalgia(new Item.Properties().stacksTo(1)));

    public static void init(IEventBus modEventBus) {
        EFFECTS.register(modEventBus);
        ITEMS.register(modEventBus);
        ENCHANTMENTS.register(modEventBus);
    }
}