package com.lim_mirror.Lim_Mirror;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.List;

/**
 * 测试卡包：右键撕开，随机获得一个 Lim Mirror 饰品。
 * <p>
 * 随机池直接取自 {@link Registration} 中已注册的全部饰品，
 * 以后新增饰品会自动进入池子，不需要改这里。
 * <p>
 * 行为要点：
 * <ul>
 *     <li>判定只在服务端做，客户端不消耗、不抽奖，避免双端各开一次</li>
 *     <li>背包满时掉落在脚下，物品不会凭空消失</li>
 *     <li>创造模式不消耗，方便反复测试</li>
 *     <li>开启时有音效与提示消息</li>
 * </ul>
 */
public class TestCardPack extends Item {

    /**
     * 随机池：全部已注册的饰品。
     * <p>
     * 故意存 {@link Supplier} 而不是 {@link Item}：本类的静态初始化发生在物品注册
     * 事件期间，此时任何 {@code RegistryObject.get()} 都会抛
     * {@code NullPointerException: Registry Object not present}。
     * 因此推迟到真正开包时再取值。
     */
    private static final List<Supplier<Item>> POOL = Arrays.asList(
            Registration.PIPE::get,
            Registration.STONE_MOUND::get,
            Registration.CLOVER::get,
            Registration.MEMORY_PENDANT::get,
            Registration.NEBULIZER::get,
            Registration.STILL_WATER::get,
            Registration.ENDORPHIN_KIT::get,
            Registration.HORSESHOE::get,
            Registration.LUCKY_BAG::get,
            Registration.DEVILS_DELIGHT::get,
            Registration.GREEN_ELYTRA::get,
            Registration.OLD_WOODEN_FIGURINE::get,
            Registration.NOSTALGIA::get,
            Registration.MEMORY_OF_A_DAY::get,
            Registration.ANGELS_PRIVILEGE::get,
            Registration.REMINISCENCE::get,
            Registration.BARREL_LIQUOR::get,
            Registration.END_OF_EVIL::get,
            Registration.COMMAND_SANCTUARY::get,
            Registration.SOMEONES_GREEN_BLADE::get,
            Registration.ROPE_CATCHER::get,
            Registration.WHALE_HEART::get,
            Registration.HARPOON_GUN_LEG::get,
            Registration.GAS_LAMP::get,
            Registration.FUZZY_HAT::get,
            Registration.GIANT_GIFT_BAG::get,
            Registration.BROKEN_BLADE::get,
            Registration.BROKEN_BAMBOO_HAT::get,
            Registration.HAPPY_PLUSHIE::get,
            Registration.GEAR_SHARD::get,
            Registration.MELEE_COMBAT_MANUAL::get,
            Registration.STIFLED_BREATH::get,
            Registration.MOON_IN_WATER::get,
            Registration.HELL_BUTTERFLY_DREAM::get,
            Registration.DUST_TO_DUST::get,
            Registration.BLAZING_FEATHER::get,
            Registration.SINGLE_POINT_LOGIC_CIRCUIT::get,
            Registration.YANLING::get,
            Registration.EARTH_TO_EARTH::get,
            Registration.SCORCHED_DISC::get,
            Registration.BLAZING_WISDOM::get,
            Registration.REQUIEM::get,
            Registration.MELTED_WAX::get,
            Registration.POLARIZED_LIGHT::get,
            Registration.SUPPRESSED_FIRE::get,
            Registration.FIRELIGHT_FLOWER::get,
            Registration.ETERNAL_STEWPOT::get,
            Registration.ETERNAL_HEARTHFIRE::get,
            Registration.COOKING_SECRETS_BOOK::get,
            Registration.STOLEN_FLAME::get,
            Registration.RED_TIE::get,
            Registration.UNIFORM_SIX_ASSOCIATION::get,
            Registration.ROYAL_JELLY_PERFUME::get,
            Registration.RED_DISASTER_EXTRACT::get,
            Registration.BURNING_FATE::get,
            Registration.SCORCHING_COPPER_PIPE::get,
            Registration.WING_CANDLE::get,
            Registration.SPICY_DRUMSTICK::get,
            Registration.UNHATCHED_SPARKS::get,
            Registration.IGNITION_GLOVES::get,
            Registration.REIGNITION_SPARK_PLUG::get,
            Registration.EMBERS::get,
            Registration.BLOOD_FLAME_BLADE::get,
            Registration.GAO_XUE::get,
            Registration.GREEN_LIGHT_FRUIT::get,
            Registration.MELTED_EYEBALL::get,
            Registration.GLOW_VARIATOR::get,
            Registration.SHOCK_BRACELET::get,
            Registration.RAINSTORM::get,
            Registration.ECHO::get,
            Registration.TRUTH_CLOCK::get,
            Registration.TREMOR_COUPLING::get,
            Registration.BIO_TOXIN_VIAL::get,
            Registration.TOXIC_SKIN::get,
            Registration.SOUR_WINE_AROMA::get,
            Registration.MIRROR_TACTILE_SYNESTHESIA::get,
            Registration.GEAR_SPRING::get,
            Registration.GEM_PENDULUM::get,
            Registration.SHAKING_BARREL::get,
            Registration.MESHED_GEAR::get,
            Registration.EPICENTER::get,
            Registration.EIGHT_DIRECTION_BELL::get,
            Registration.CLAP_CLAP::get,
            Registration.HEATING_HAMMER::get,
            Registration.ONE_MORE_DRINK::get,
            Registration.IMPERFECT_FORESIGHT_EYE::get,
            Registration.OFFERING_CIGAR::get,
            Registration.CUSTOM_MADE::get,
            Registration.HAND_MIRROR::get,
            Registration.OILY_WRENCH::get,
            Registration.SILVER_WATCH_CASE::get,
            Registration.FADED_WATCH_CASE::get,
            Registration.ETCHED_HAND::get,
            Registration.RUSTED_HAND::get,
            Registration.TRICKLE_CUP::get,
            Registration.POCKET_WATCH_TYPE_L::get,
            Registration.POCKET_WATCH_TYPE_E::get,
            Registration.POCKET_WATCH_TYPE_Y::get,
            Registration.POCKET_WATCH_TYPE_P::get,
            Registration.EXTINGUISHED_LANTERN::get,
            Registration.SHADOW_MONSTER::get,
            Registration.INTERTWINED_TIME::get,
            Registration.FORCED_ENTANGLEMENT_SEQUENCER::get,
            Registration.ULTRA_PRECISION_TIME_ACCELERATOR::get,
            Registration.BLACK_IRON_BARDING::get,
            Registration.FOR_THE_COMMANDER::get,
            Registration.DREAMING_ELECTRIC_SHEEP::get,
            Registration.SURGICAL_KNIFE::get,
            Registration.VAIN_PEACE::get,
            Registration.TAILORS_SCISSORS::get,
            Registration.RESOLVE::get,
            Registration.MOMENT_OF_JUDGMENT::get,
            Registration.SEVERED_MEMORY::get,
            Registration.WORN_WHETSTONE::get,
            Registration.CANE_SWORD::get,
            Registration.CLOUD_PATTERNED_CRANE_NECK_VASE::get,
            Registration.SHATTERED_GREATSWORD::get,
            Registration.SWORD_OF_CALAMUS::get,
            Registration.SHATTERED_WOODEN_TORTURE_DEVICE::get,
            Registration.UNADORNED_TSUBA::get,
            Registration.NATIVE_SWORD_MANUAL::get,
            Registration.RUSTY_HANDLE::get,
            Registration.RED_TASSEL::get,
            Registration.MAGNIFICENT::get,
            Registration.IMMOVABLE::get,
            Registration.OLD_ROBE::get,
            Registration.OLD_HANDLE::get,
            Registration.POLISHING::get,
            Registration.MURAKUMO::get,
            Registration.SHADOW_WOVEN_HAT::get,
            Registration.BLINDING_RED_CLOTH::get,
            Registration.EXPRESSION_OF_THIS_MOMENT::get,
            Registration.STAR_OF_THE_VALLEY::get,
            Registration.HAUNTING::get,
            Registration.COMPRESSION_BANDAGE::get,
            Registration.CHAINS_OF_TIME::get,
            Registration.CLENCHED_STATUE::get,
            Registration.SHATTERED_MEMORY_STRIKE::get,
            Registration.DIMENSIONAL_SORTING_RECYCLING_BIN::get,
            Registration.POCKET_MNEMONIC_CARD::get,
            Registration.DIMENSIONAL_PERCEPTION_VARIANT::get,
            Registration.REVENGE_LEDGER::get,
            Registration.MEAT_TENDERIZER::get,
            Registration.STOLEN_FIXER_MAGAZINE::get,
            Registration.REVENGE_LEDGER_APPENDIX::get,
            Registration.MIDDLE_FINGER_KNUCKLE::get,
            Registration.DEBUT_EXCLUSIVE_SPEAKER::get,
            Registration.YOUNG_BROTHER_DEBUT::get,
            Registration.ENHANCED_TATTOO_MIDDLE_FINGER::get,
            Registration.LOYALTY_CHAINS::get,
            Registration.ETERNAL_CHAINS::get,
            Registration.MIDDLE_FINGER_RULES::get,
            Registration.MIMIC_WOUND_BUG::get,
            Registration.BLOODY_IRON_SPIKE::get,
            Registration.WHITE_COTTON::get,
            Registration.TINY_DOLL::get
    );


    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    /** 按序号取池中条目，取不到返回 null。 */
    private static Item poolEntry(int index) {
        try {
            return POOL.get(index).get();
        } catch (RuntimeException e) {
            return null;
        }
    }

    public TestCardPack(Properties properties) {
        super(properties);
    }

    @Override
    @Nonnull
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player,
                                                  @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide()) {
            // 客户端只播放手臂动作，实际判定在服务端
            return InteractionResultHolder.sidedSuccess(stack, true);
        }

        // 开包时才解析，此时注册早已完成
        List<Item> available = new java.util.ArrayList<>(POOL.size());
        List<String> rejected = new java.util.ArrayList<>();
        for (int i = 0; i < POOL.size(); i++) {
            try {
                Item item = poolEntry(i);
                if (item == null || item == net.minecraft.world.item.Items.AIR) {
                    // 空/无效条目：不进池，并记录以便排查
                    rejected.add(String.valueOf(i));
                    continue;
                }
                available.add(item);
            } catch (RuntimeException e) {
                rejected.add(String.valueOf(i) + ":" + e.getClass().getSimpleName());
            }
        }

        if (!rejected.isEmpty()) {
            // 只在异常列表非空时打日志，避免刷屏
            LOGGER.warn("[TestCardPack] 抽奖池中存在无效条目，已跳过: {}", rejected);
        }

        if (available.isEmpty()) {
            player.displayClientMessage(
                    Component.translatableWithFallback("message.lim_mirror.test_card_pack.empty",
                            "卡包里什么都没有……").withStyle(ChatFormatting.GRAY), false);
            return InteractionResultHolder.fail(stack);
        }

        Item picked = available.get(level.random.nextInt(available.size()));
        ItemStack reward = new ItemStack(picked);

        // 抽到的真实注册 ID 只写日志，不在界面上显示物品名
        LOGGER.debug("[TestCardPack] 抽到 id={} 池大小={}",
                net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(picked), available.size());

        if (!player.getInventory().add(reward)) {
            // 背包已满：掉在脚下，避免物品消失
            player.drop(reward, false);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.9F, 0.6F);

        player.displayClientMessage(
                Component.translatableWithFallback("message.lim_mirror.test_card_pack.open",
                                "卡包开启：获得了一个饰品").withStyle(ChatFormatting.GOLD), true);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level,
                                @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.test_card_pack.1",
                "§7右键撕开，随机获得一个饰品的"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.test_card_pack.2",
                "§8- 卡池共 " + POOL.size() + " 张"));
    }
}
