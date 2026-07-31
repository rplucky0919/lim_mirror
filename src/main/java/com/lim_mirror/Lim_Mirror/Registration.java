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

    public static final RegistryObject<Item> NOSTALGIA = ITEMS.register("nostalgia",
            () -> new Nostalgia(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> MEMORY_OF_A_DAY = ITEMS.register("memory_of_a_day",
            () -> new MemoryOfADay(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ANGELS_PRIVILEGE = ITEMS.register("angels_privilege",
            () -> new AngelsPrivilege(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> REMINISCENCE = ITEMS.register("reminiscence",
            () -> new Reminiscence(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BARREL_LIQUOR = ITEMS.register("barrel_liquor",
            () -> new BarrelLiquor(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> END_OF_EVIL = ITEMS.register("end_of_evil",
            () -> new EndOfEvil(new Item.Properties().stacksTo(1)));

    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, lim_mirror.MODID);

    public static final RegistryObject<Enchantment> BREATH_SMOOTH =
            ENCHANTMENTS.register("breath_smooth", BreathSmoothEnchantment::new);

    public static final RegistryObject<Enchantment> SANCHO_BLOOD =
            ENCHANTMENTS.register("sancho_blood", SanchoBloodEnchantment::new);

    public static final RegistryObject<Enchantment> SINKING_TOUCH =
            ENCHANTMENTS.register("sinking_touch", SinkingTouchEnchantment::new);

    public static final RegistryObject<Item> COMMAND_SANCTUARY = ITEMS.register("command_sanctuary",
            () -> new CommandSanctuary(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SOMEONES_GREEN_BLADE = ITEMS.register("someones_green_blade",
            () -> new SomeonesGreenBlade(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ROPE_CATCHER = ITEMS.register("rope_catcher",
            () -> new RopeCatcher(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> WHALE_HEART = ITEMS.register("whale_heart",
            () -> new WhaleHeart(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> HARPOON_GUN_LEG = ITEMS.register("harpoon_gun_leg",
            () -> new HarpoonGunLeg(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> GAS_LAMP = ITEMS.register("gas_lamp",
            () -> new GasLamp(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> FUZZY_HAT = ITEMS.register("fuzzy_hat",
            () -> new FuzzyHat(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> GIANT_GIFT_BAG = ITEMS.register("giant_gift_bag",
            () -> new GiantGiftBag(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BROKEN_BLADE = ITEMS.register("broken_blade",
            () -> new BrokenBlade(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BROKEN_BAMBOO_HAT = ITEMS.register("broken_bamboo_hat",
            () -> new BrokenBambooHat(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> HAPPY_PLUSHIE = ITEMS.register("happy_plushie",
            () -> new HappyPlushie(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> GEAR_SHARD = ITEMS.register("gear_shard",
            () -> new GearShard(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> MELEE_COMBAT_MANUAL = ITEMS.register("melee_combat_manual",
            () -> new MeleeCombatManual(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> STIFLED_BREATH = ITEMS.register("stifled_breath",
            () -> new StifledBreath(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<MobEffect> RUPTURE = EFFECTS.register("rupture", RuptureEffect::new);

    public static final RegistryObject<Enchantment> SHADOW_BEAST =
            ENCHANTMENTS.register("shadow_beast", ShadowBeastEnchantment::new);

    public static final RegistryObject<Item> MOON_IN_WATER = ITEMS.register("moon_in_water",
            () -> new MoonInWater(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<MobEffect> BURN = EFFECTS.register("burn", BurnEffect::new);

    public static final RegistryObject<Enchantment> MATCH_FLAME =
            ENCHANTMENTS.register("match_flame", MatchFlameEnchantment::new);

    public static final RegistryObject<Item> HELL_BUTTERFLY_DREAM = ITEMS.register("hell_butterfly_dream",
            () -> new HellButterflyDream(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> DUST_TO_DUST = ITEMS.register("dust_to_dust",
            () -> new DustToDust(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BLAZING_FEATHER = ITEMS.register("blazing_feather",
            () -> new BlazingFeather(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SINGLE_POINT_LOGIC_CIRCUIT = ITEMS.register("single_point_logic_circuit",
            () -> new SinglePointLogicCircuit(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> YANLING = ITEMS.register("yanling",
            () -> new Yanling(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> EARTH_TO_EARTH = ITEMS.register("earth_to_earth",
            () -> new EarthToEarth(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SCORCHED_DISC = ITEMS.register("scorched_disc",
            () -> new ScorchedDisc(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BLAZING_WISDOM = ITEMS.register("blazing_wisdom",
            () -> new BlazingWisdom(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> REQUIEM = ITEMS.register("requiem",
            () -> new Requiem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> MELTED_WAX = ITEMS.register("melted_wax",
            () -> new MeltedWax(new Item.Properties().stacksTo(1)));

    public static void init(IEventBus modEventBus) {
        EFFECTS.register(modEventBus);
        ITEMS.register(modEventBus);
        ENCHANTMENTS.register(modEventBus);
    }
}