package com.lim_mirror.Lim_Mirror;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registration {

    // ==================== 打击武器标签 ====================
    public static final TagKey<Item> STRIKE_WEAPONS_TAG =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath(lim_mirror.MODID, "strike_weapons"));

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, lim_mirror.MODID);

    public static final RegistryObject<MobEffect> POISE = EFFECTS.register("poise", Poise::new);
    public static final RegistryObject<MobEffect> BLEED = EFFECTS.register("bleed", BleedEffect::new);
    public static final RegistryObject<MobEffect> SINKING = EFFECTS.register("sinking", SinkingEffect::new);
    public static final RegistryObject<MobEffect> RUPTURE = EFFECTS.register("rupture", RuptureEffect::new);
    public static final RegistryObject<MobEffect> BURN = EFFECTS.register("burn", BurnEffect::new);

    // ==================== 震颤 ====================
    public static final RegistryObject<MobEffect> TREMOR = EFFECTS.register("tremor", TremorEffect::new);

    // ==================== 充能 ====================
    public static final RegistryObject<MobEffect> CHARGE = EFFECTS.register("charge", ChargeEffect::new);

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

    public static final RegistryObject<Enchantment> SHADOW_BEAST =
            ENCHANTMENTS.register("shadow_beast", ShadowBeastEnchantment::new);

    public static final RegistryObject<Enchantment> MATCH_FLAME =
            ENCHANTMENTS.register("match_flame", MatchFlameEnchantment::new);

    // ==================== 沸腾腐蚀 ====================
    public static final RegistryObject<Enchantment> BOILING_CORROSION =
            ENCHANTMENTS.register("boiling_corrosion", BoilingCorrosionEnchantment::new);

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

    public static final RegistryObject<Item> MOON_IN_WATER = ITEMS.register("moon_in_water",
            () -> new MoonInWater(new Item.Properties().stacksTo(1)));

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

    public static final RegistryObject<Item> POLARIZED_LIGHT = ITEMS.register("polarized_light",
            () -> new PolarizedLight(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SUPPRESSED_FIRE = ITEMS.register("suppressed_fire",
            () -> new SuppressedFire(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> FIRELIGHT_FLOWER = ITEMS.register("firelight_flower",
            () -> new FirelightFlower(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ETERNAL_STEWPOT = ITEMS.register("eternal_stewpot",
            () -> new EternalStewpot(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ETERNAL_HEARTHFIRE = ITEMS.register("eternal_hearthfire",
            () -> new EternalHearthfire(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> COOKING_SECRETS_BOOK = ITEMS.register("cooking_secrets_book",
            () -> new CookingSecretsBook(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> STOLEN_FLAME = ITEMS.register("stolen_flame",
            () -> new StolenFlame(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RED_TIE = ITEMS.register("red_tie",
            () -> new RedTie(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> UNIFORM_SIX_ASSOCIATION = ITEMS.register("uniform_six_association",
            () -> new UniformSixAssociation(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ROYAL_JELLY_PERFUME = ITEMS.register("royal_jelly_perfume",
            () -> new RoyalJellyPerfume(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RED_DISASTER_EXTRACT = ITEMS.register("red_disaster_extract",
            () -> new RedDisasterExtract(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BURNING_FATE = ITEMS.register("burning_fate",
            () -> new BurningFate(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SCORCHING_COPPER_PIPE = ITEMS.register("scorching_copper_pipe",
            () -> new ScorchingCopperPipe(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> WING_CANDLE = ITEMS.register("wing_candle",
            () -> new WingCandle(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SPICY_DRUMSTICK = ITEMS.register("spicy_drumstick",
            () -> new SpicyDrumstick(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> UNHATCHED_SPARKS = ITEMS.register("unhatched_sparks",
            () -> new UnhatchedSparks(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> IGNITION_GLOVES = ITEMS.register("ignition_gloves",
            () -> new IgnitionGloves(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> REIGNITION_SPARK_PLUG = ITEMS.register("reignition_spark_plug",
            () -> new ReignitionSparkPlug(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> EMBERS = ITEMS.register("embers",
            () -> new Embers(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BLOOD_FLAME_BLADE = ITEMS.register("blood_flame_blade",
            () -> new BloodFlameBlade(new Item.Properties().stacksTo(1)));

    // ==================== 膏血 ====================
    public static final RegistryObject<Item> GAO_XUE = ITEMS.register("gao_xue",
            () -> new GaoXue(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> GREEN_LIGHT_FRUIT = ITEMS.register("green_light_fruit",
            () -> new GreenLightFruit(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> MELTED_EYEBALL = ITEMS.register("melted_eyeball",
            () -> new MeltedEyeball(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> GLOW_VARIATOR = ITEMS.register("glow_variator",
            () -> new GlowVariator(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SHOCK_BRACELET = ITEMS.register("shock_bracelet",
            () -> new ShockBracelet(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RAINSTORM = ITEMS.register("rainstorm",
            () -> new Rainstorm(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ECHO = ITEMS.register("echo",
            () -> new Echo(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TRUTH_CLOCK = ITEMS.register("truth_clock",
            () -> new TruthClock(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TREMOR_COUPLING = ITEMS.register("tremor_coupling",
            () -> new TremorCoupling(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BIO_TOXIN_VIAL = ITEMS.register("bio_toxin_vial",
            () -> new BioToxinVial(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TOXIC_SKIN = ITEMS.register("toxic_skin",
            () -> new ToxicSkin(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SOUR_WINE_AROMA = ITEMS.register("sour_wine_aroma",
            () -> new SourWineAroma(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> MIRROR_TACTILE_SYNESTHESIA = ITEMS.register("mirror_tactile_synesthesia",
            () -> new MirrorTactileSynesthesia(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> GEAR_SPRING = ITEMS.register("gear_spring",
            () -> new GearSpring(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> GEM_PENDULUM = ITEMS.register("gem_pendulum",
            () -> new GemPendulum(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SHAKING_BARREL = ITEMS.register("shaking_barrel",
            () -> new ShakingBarrel(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> MESHED_GEAR = ITEMS.register("meshed_gear",
            () -> new MeshedGear(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> EPICENTER = ITEMS.register("epicenter",
            () -> new Epicenter(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> EIGHT_DIRECTION_BELL = ITEMS.register("eight_direction_bell",
            () -> new EightDirectionBell(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<MobEffect> TREMOR_BURN = EFFECTS.register("tremor_burn", TremorBurnEffect::new);

    public static final RegistryObject<MobEffect> TREMOR_BLEED = EFFECTS.register("tremor_bleed", TremorBleedEffect::new);

    public static final RegistryObject<MobEffect> TREMOR_ECHO = EFFECTS.register("tremor_echo", TremorEchoEffect::new);

    public static final RegistryObject<MobEffect> TREMOR_COLLAPSE = EFFECTS.register("tremor_collapse", TremorCollapseEffect::new);

    public static final RegistryObject<MobEffect> TREMOR_CHAIN = EFFECTS.register("tremor_chain", TremorChainEffect::new);

    public static final RegistryObject<Item> CLAP_CLAP = ITEMS.register("clap_clap",
            () -> new ClapClap(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> HEATING_HAMMER = ITEMS.register("heating_hammer",
            () -> new HeatingHammer(new Item.Properties().stacksTo(1)));

    // ==================== 空间撕裂附魔 ====================
    public static final RegistryObject<Enchantment> SPACE_TEAR = ENCHANTMENTS.register("space_tear",
            SpaceTearEnchantment::new);

    public static final RegistryObject<Item> ONE_MORE_DRINK = ITEMS.register("one_more_drink",
            () -> new OneMoreDrink(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<MobEffect> INSTANT_FORESIGHT = EFFECTS.register("instant_foresight", InstantForesightEffect::new);

    public static final RegistryObject<Item> IMPERFECT_FORESIGHT_EYE = ITEMS.register("imperfect_foresight_eye",
            () -> new ImperfectForesightEye(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> OFFERING_CIGAR = ITEMS.register("offering_cigar",
            () -> new OfferingCigar(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> CUSTOM_MADE = ITEMS.register("custom_made",
            () -> new CustomMade(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> HAND_MIRROR = ITEMS.register("hand_mirror",
            () -> new HandMirror(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> OILY_WRENCH = ITEMS.register("oily_wrench",
            () -> new OilyWrench(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SILVER_WATCH_CASE = ITEMS.register("silver_watch_case",
            () -> new SilverWatchCase(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> FADED_WATCH_CASE = ITEMS.register("faded_watch_case",
            () -> new FadedWatchCase(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ETCHED_HAND = ITEMS.register("etched_hand",
            () -> new EtchedHand(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RUSTED_HAND = ITEMS.register("rusted_hand",
            () -> new RustedHand(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TRICKLE_CUP = ITEMS.register("trickle_cup",
            () -> new TrickleCup(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> POCKET_WATCH_TYPE_L = ITEMS.register("pocket_watch_type_l",
            () -> new PocketWatchTypeL(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> POCKET_WATCH_TYPE_E = ITEMS.register("pocket_watch_type_e",
            () -> new PocketWatchTypeE(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> POCKET_WATCH_TYPE_Y = ITEMS.register("pocket_watch_type_y",
            () -> new PocketWatchTypeY(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> POCKET_WATCH_TYPE_P = ITEMS.register("pocket_watch_type_p",
            () -> new PocketWatchTypeP(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> EXTINGUISHED_LANTERN = ITEMS.register("extinguished_lantern",
            () -> new ExtinguishedLantern(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SHADOW_MONSTER = ITEMS.register("shadow_monster",
            () -> new ShadowMonster(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> INTERTWINED_TIME = ITEMS.register("intertwined_time",
            () -> new IntertwinedTime(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> FORCED_ENTANGLEMENT_SEQUENCER = ITEMS.register("forced_entanglement_sequencer",
            () -> new ForcedEntanglementSequencer(new Item.Properties().stacksTo(1)));
    // ==================== 精密时间加速效果 ====================
    public static final RegistryObject<MobEffect> PRECISION_TIME_ACCELERATION = EFFECTS.register("precision_time_acceleration", PrecisionTimeAccelerationEffect::new);

    // ==================== 超精密时间加速装置 ====================
    public static final RegistryObject<Item> ULTRA_PRECISION_TIME_ACCELERATOR = ITEMS.register("ultra_precision_time_accelerator",
            () -> new UltraPrecisionTimeAccelerator(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BLACK_IRON_BARDING = ITEMS.register("black_iron_barding",
            () -> new BlackIronBarding(new Item.Properties().stacksTo(1)));

    // ===== 新增：为了指挥官 =====
    public static final RegistryObject<Item> FOR_THE_COMMANDER = ITEMS.register("for_the_commander",
            () -> new ForTheCommander(new Item.Properties().stacksTo(1)));

    // ==================== 斩击威力提升 ====================
    public static final RegistryObject<MobEffect> SLASHING_POWER = EFFECTS.register("slashing_power", SlashingPowerEffect::new);

    public static final RegistryObject<Item> DREAMING_ELECTRIC_SHEEP = ITEMS.register("dreaming_electric_sheep",
            () -> new DreamingElectricSheep(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SURGICAL_KNIFE = ITEMS.register("surgical_knife",
            () -> new SurgicalKnife(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> VAIN_PEACE = ITEMS.register("vain_peace",
            () -> new VainPeace(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TAILORS_SCISSORS = ITEMS.register("tailors_scissors",
            () -> new TailorsScissors(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RESOLVE = ITEMS.register("resolve",
            () -> new Resolve(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> MOMENT_OF_JUDGMENT = ITEMS.register("moment_of_judgment",
            () -> new MomentOfJudgment(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SEVERED_MEMORY = ITEMS.register("severed_memory",
            () -> new SeveredMemory(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> WORN_WHETSTONE = ITEMS.register("worn_whetstone",
            () -> new WornWhetstone(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> CANE_SWORD = ITEMS.register("cane_sword",
            () -> new CaneSword(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> CLOUD_PATTERNED_CRANE_NECK_VASE = ITEMS.register("cloud_patterned_crane_neck_vase",
            () -> new CloudPatternedCraneNeckVase(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SHATTERED_GREATSWORD = ITEMS.register("shattered_greatsword",
            () -> new ShatteredGreatsword(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SWORD_OF_CALAMUS = ITEMS.register("sword_of_calamus",
            () -> new SwordOfCalamus(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SHATTERED_WOODEN_TORTURE_DEVICE = ITEMS.register("shattered_wooden_torture_device",
            () -> new ShatteredWoodenTortureDevice(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> UNADORNED_TSUBA = ITEMS.register("unadorned_tsuba",
            () -> new UnadornedTsuba(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> NATIVE_SWORD_MANUAL = ITEMS.register("native_sword_manual",
            () -> new NativeSwordManual(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RUSTY_HANDLE = ITEMS.register("rusty_handle",
            () -> new RustyHandle(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RED_TASSEL = ITEMS.register("red_tassel",
            () -> new RedTassel(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> MAGNIFICENT = ITEMS.register("magnificent",
            () -> new Magnificent(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> IMMOVABLE = ITEMS.register("immovable",
            () -> new Immovable(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> OLD_ROBE = ITEMS.register("old_robe",
            () -> new OldRobe(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> OLD_HANDLE = ITEMS.register("old_handle",
            () -> new OldHandle(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> POLISHING = ITEMS.register("polishing",
            () -> new Polishing(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> MURAKUMO = ITEMS.register("murakumo",
            () -> new Murakumo(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SHADOW_WOVEN_HAT = ITEMS.register("shadow_woven_hat",
            () -> new ShadowWovenHat(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BLINDING_RED_CLOTH = ITEMS.register("blinding_red_cloth",
            () -> new BlindingRedCloth(new Item.Properties().stacksTo(1)));

    // ==================== 打击威力提升 ====================
    public static final RegistryObject<MobEffect> STRIKE_POWER = EFFECTS.register("strike_power", StrikePowerEffect::new);

    // ==================== 打击伤害强化 ====================
    public static final RegistryObject<MobEffect> STRIKE_ENHANCE = EFFECTS.register("strike_enhance", StrikeEnhanceEffect::new);

    public static final RegistryObject<Item> EXPRESSION_OF_THIS_MOMENT = ITEMS.register("expression_of_this_moment",
            () -> new ExpressionOfThisMoment(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> STAR_OF_THE_VALLEY = ITEMS.register("star_of_the_valley",
            () -> new StarOfTheValley(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> HAUNTING = ITEMS.register("haunting",
            () -> new Haunting(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> COMPRESSION_BANDAGE = ITEMS.register("compression_bandage",
            () -> new CompressionBandage(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> CHAINS_OF_TIME = ITEMS.register("chains_of_time",
            () -> new ChainsOfTime(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> CLENCHED_STATUE = ITEMS.register("clenched_statue",
            () -> new ClenchedStatue(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SHATTERED_MEMORY_STRIKE = ITEMS.register("shattered_memory_strike",
            () -> new ShatteredMemoryStrike(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> DIMENSIONAL_SORTING_RECYCLING_BIN = ITEMS.register("dimensional_sorting_recycling_bin",
            () -> new DimensionalSortingRecyclingBin(new Item.Properties().stacksTo(1)));

    // ==================== 口袋助记卡 ====================
    public static final RegistryObject<Item> POCKET_MNEMONIC_CARD = ITEMS.register("pocket_mnemonic_card",
            () -> new PocketMnemonicCard(new Item.Properties().stacksTo(1)));

    // ==================== 次元知觉变体 ====================
    public static final RegistryObject<Item> DIMENSIONAL_PERCEPTION_VARIANT = ITEMS.register("dimensional_perception_variant",
            () -> new DimensionalPerceptionVariant(new Item.Properties().stacksTo(1)));

    // ==================== 复仇账簿 ====================
    public static final RegistryObject<Item> REVENGE_LEDGER = ITEMS.register("revenge_ledger",
            () -> new RevengeLedger(new Item.Properties().stacksTo(1)));

    // ==================== 嫩肉锤 ====================
    public static final RegistryObject<Item> MEAT_TENDERIZER = ITEMS.register("meat_tenderizer",
            () -> new MeatTenderizer(new Item.Properties().stacksTo(1)));

    // ==================== 被盗的收尾人杂志 ====================
    public static final RegistryObject<Item> STOLEN_FIXER_MAGAZINE = ITEMS.register("stolen_fixer_magazine",
            () -> new StolenFixerMagazine(new Item.Properties().stacksTo(1)));

    // ==================== 复仇账簿：附录 ====================
    public static final RegistryObject<Item> REVENGE_LEDGER_APPENDIX = ITEMS.register("revenge_ledger_appendix",
            () -> new RevengeLedgerAppendix(new Item.Properties().stacksTo(1)));

    // ==================== 中指式指虎 ====================
    public static final RegistryObject<Item> MIDDLE_FINGER_KNUCKLE = ITEMS.register("middle_finger_knuckle",
            () -> new MiddleFingerKnuckle(new Item.Properties().stacksTo(1)));

    // ==================== 登场专用音响 ====================
    public static final RegistryObject<Item> DEBUT_EXCLUSIVE_SPEAKER = ITEMS.register("debut_exclusive_speaker",
            () -> new DebutExclusiveSpeaker(new Item.Properties().stacksTo(1)));

    // ==================== 幼·兄登场 ====================
    public static final RegistryObject<Item> YOUNG_BROTHER_DEBUT = ITEMS.register("young_brother_debut",
            () -> new YoungBrotherDebut(new Item.Properties().stacksTo(1)));

    // ==================== 强化纹身-中指 ====================
    public static final RegistryObject<Item> ENHANCED_TATTOO_MIDDLE_FINGER = ITEMS.register("enhanced_tattoo_middle_finger",
            () -> new EnhancedTattooMiddleFinger(new Item.Properties().stacksTo(1)));

    // ==================== 忠义锁链 ====================
    public static final RegistryObject<Item> LOYALTY_CHAINS = ITEMS.register("loyalty_chains",
            () -> new LoyaltyChains(new Item.Properties().stacksTo(1)));

    // ==================== 永恒的缘分锁链 ====================
    public static final RegistryObject<Item> ETERNAL_CHAINS = ITEMS.register("eternal_chains",
            () -> new EternalChains(new Item.Properties().stacksTo(1)));

    // ==================== 中指规矩 ====================
    public static final RegistryObject<Item> MIDDLE_FINGER_RULES = ITEMS.register("middle_finger_rules",
            () -> new MiddleFingerRules(new Item.Properties().stacksTo(1)));

    // ==================== 拟伤虫 ====================
    public static final RegistryObject<Item> MIMIC_WOUND_BUG = ITEMS.register("mimic_wound_bug",
            () -> new MimicWoundBug(new Item.Properties().stacksTo(1)));

    // ==================== 染血铁钉 ====================
    public static final RegistryObject<Item> BLOODY_IRON_SPIKE = ITEMS.register("bloody_iron_spike",
            () -> new BloodyIronSpike(new Item.Properties().stacksTo(1)));

    // ==================== 白棉花 ====================
    public static final RegistryObject<Item> WHITE_COTTON = ITEMS.register("white_cotton",
            () -> new WhiteCotton(new Item.Properties().stacksTo(1)));

    // ==================== 又小又可能很坏的玩偶 ====================
    public static final RegistryObject<Item> TINY_DOLL = ITEMS.register("tiny_doll",
            () -> new TinyDoll(new Item.Properties().stacksTo(1)));


    public static void init(IEventBus modEventBus) {
        EFFECTS.register(modEventBus);
        ITEMS.register(modEventBus);
        ENCHANTMENTS.register(modEventBus);
    }
}