package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = lim_mirror.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StrikeEvents {

    private static final Random RANDOM = new Random();

    // ==================== 打击武器判定 ====================

    public static boolean isStrikeWeapon(Player player) {
        ItemStack mainHand = player.getMainHandItem();

        if (mainHand.isEmpty()) return true;

        if (mainHand.is(Registration.STRIKE_WEAPONS_TAG)) return true;

        if (mainHand.getTag() != null && mainHand.getTag().getBoolean("lim_mirror_strike")) return true;

        return false;
    }

    // ==================== 打击威力提升检测 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtStrikePower(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!isStrikeWeapon(player)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.isEmpty()) {
            event.setAmount(event.getAmount() + 5.0f);
        }

        MobEffectInstance effect = player.getEffect(Registration.STRIKE_POWER.get());
        if (effect != null) {
            int level = effect.getAmplifier() + 1;
            float multiplier = 1.0f + level * 0.1f;
            event.setAmount(event.getAmount() * multiplier);
        }

        player.getPersistentData().putBoolean("isStrikeAttack", true);
    }

    // ==================== 此刻的神色 ====================

    @SubscribeEvent
    public static void onLivingDeathExpressionOfThisMoment(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;

        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!player.getPersistentData().getBoolean("hasExpressionOfThisMoment")) return;

        if (!isStrikeWeapon(player)) return;

        long currentTick = player.level().getGameTime();

        player.getPersistentData().putLong("expressionOfThisMomentKillTime", currentTick);
        player.getPersistentData().putBoolean("expressionOfThisMomentPending", true);
    }

    @SubscribeEvent
    public static void onLivingTickExpressionOfThisMoment(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (!player.getPersistentData().getBoolean("hasExpressionOfThisMoment")) return;
        if (!player.getPersistentData().getBoolean("expressionOfThisMomentPending")) return;

        long killTime = player.getPersistentData().getLong("expressionOfThisMomentKillTime");
        long currentTick = player.level().getGameTime();

        if (currentTick - killTime >= 20 * 30) {
            player.getPersistentData().putBoolean("expressionOfThisMomentPending", false);

            MobEffectInstance existing = player.getEffect(Registration.STRIKE_POWER.get());
            int newDuration = 20 * 60;
            int newAmplifier = 1;

            if (existing != null) {
                newDuration = existing.getDuration() + 20 * 60;
                newAmplifier = existing.getAmplifier() + 1;
            }

            player.removeEffect(Registration.STRIKE_POWER.get());
            player.addEffect(new MobEffectInstance(
                    Registration.STRIKE_POWER.get(),
                    newDuration,
                    newAmplifier,
                    false,
                    false
            ));

            player.displayClientMessage(
                    Component.literal("§d[此刻的神色] §c打击威力提升 II §a已激活，持续 60 秒！"),
                    true
            );
        }
    }

    // ==================== 谷底之星 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtStarOfTheValley(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!isStrikeWeapon(player)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasStarOfTheValley")) return;

        event.setAmount(event.getAmount() + 5.0f);

        if (player.hasEffect(MobEffects.WEAKNESS)) {
            event.setAmount(event.getAmount() * 1.25f);
        }
    }

    // ==================== 作祟 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtHaunting(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!isStrikeWeapon(player)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasHaunting")) return;

        long currentTick = player.level().getGameTime();

        player.getPersistentData().putLong("hauntingLastAttackTime", currentTick);
        player.getPersistentData().putBoolean("hauntingPending", true);
        player.getPersistentData().putLong("hauntingPendingTime", currentTick + 20 * 30);
    }

    @SubscribeEvent
    public static void onLivingTickHaunting(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (!player.getPersistentData().getBoolean("hasHaunting")) return;

        long currentTick = player.level().getGameTime();

        if (player.getPersistentData().getBoolean("hauntingPending")) {
            long pendingTime = player.getPersistentData().getLong("hauntingPendingTime");
            if (currentTick >= pendingTime) {
                player.getPersistentData().putBoolean("hauntingPending", false);

                MobEffectInstance existingSpeed = player.getEffect(MobEffects.MOVEMENT_SPEED);
                int newSpeedDuration = 20 * 4;
                int newSpeedAmplifier = 1;

                if (existingSpeed != null) {
                    newSpeedDuration = existingSpeed.getDuration() + 20 * 4;
                    newSpeedAmplifier = existingSpeed.getAmplifier() + 2;
                }

                player.removeEffect(MobEffects.MOVEMENT_SPEED);
                player.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SPEED,
                        newSpeedDuration,
                        newSpeedAmplifier,
                        false,
                        false
                ));

                MobEffectInstance existingStrike = player.getEffect(Registration.STRIKE_POWER.get());
                int newStrikeDuration = 20 * 4;
                int newStrikeAmplifier = 1;

                if (existingStrike != null) {
                    newStrikeDuration = existingStrike.getDuration() + 20 * 4;
                    newStrikeAmplifier = existingStrike.getAmplifier() + 1;
                }

                player.removeEffect(Registration.STRIKE_POWER.get());
                player.addEffect(new MobEffectInstance(
                        Registration.STRIKE_POWER.get(),
                        newStrikeDuration,
                        newStrikeAmplifier,
                        false,
                        false
                ));

                player.displayClientMessage(
                        Component.literal("§d[作祟] §b迅捷 II §7+ §c打击威力提升 II §a已激活！"),
                        true
                );
            }
        }

        long lastAttackTime = player.getPersistentData().getLong("hauntingLastAttackTime");

        if (lastAttackTime == 0) {
            player.getPersistentData().putLong("hauntingLastAttackTime", currentTick);
            return;
        }

        long cooldownEnd = player.getPersistentData().getLong("hauntingNoAttackCooldownEnd");
        if (currentTick < cooldownEnd) return;

        if (currentTick - lastAttackTime >= 20 * 30) {
            MobEffectInstance existingSpeed = player.getEffect(MobEffects.MOVEMENT_SPEED);
            int newSpeedDuration = 20 * 6;
            int newSpeedAmplifier = 0;

            if (existingSpeed != null) {
                newSpeedDuration = existingSpeed.getDuration() + 20 * 6;
                newSpeedAmplifier = existingSpeed.getAmplifier() + 1;
            }

            player.removeEffect(MobEffects.MOVEMENT_SPEED);
            player.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SPEED,
                    newSpeedDuration,
                    newSpeedAmplifier,
                    false,
                    false
            ));

            MobEffectInstance existingStrike = player.getEffect(Registration.STRIKE_POWER.get());
            int newStrikeDuration = 20 * 4;
            int newStrikeAmplifier = 1;

            if (existingStrike != null) {
                newStrikeDuration = existingStrike.getDuration() + 20 * 4;
                newStrikeAmplifier = existingStrike.getAmplifier() + 1;
            }

            player.removeEffect(Registration.STRIKE_POWER.get());
            player.addEffect(new MobEffectInstance(
                    Registration.STRIKE_POWER.get(),
                    newStrikeDuration,
                    newStrikeAmplifier,
                    false,
                    false
            ));

            player.getPersistentData().putLong("hauntingNoAttackCooldownEnd", currentTick + 20 * 30);
            player.getPersistentData().putLong("hauntingLastAttackTime", currentTick);

            player.displayClientMessage(
                    Component.literal("§d[作祟] 30 秒未攻击！§b迅捷 I §7+ §c打击威力提升 II §a已激活！"),
                    true
            );
        }
    }

    // ==================== 加压绷带 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtCompressionBandage(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!isStrikeWeapon(player)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasCompressionBandage")) return;

        int bonusDamage = 7 + RANDOM.nextInt(3);
        event.setAmount(event.getAmount() + bonusDamage);

        player.heal(3.0f);
    }

    // ==================== 时间的枷锁 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtChainsOfTime(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!isStrikeWeapon(player)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasChainsOfTime")) return;

        event.setAmount(event.getAmount() + 3.0f);

        long currentTick = player.level().getGameTime();
        long lastTrigger = player.getPersistentData().getLong("chainsOfTimeLastTrigger");
        if (currentTick - lastTrigger < 20 * 30) return;

        MobEffectInstance existing = player.getEffect(Registration.STRIKE_POWER.get());
        int newDuration = 20 * 6;
        int newAmplifier = 2;

        if (existing != null) {
            newDuration = existing.getDuration() + 20 * 6;
            newAmplifier = existing.getAmplifier() + 3;
        }

        player.removeEffect(Registration.STRIKE_POWER.get());
        player.addEffect(new MobEffectInstance(
                Registration.STRIKE_POWER.get(),
                newDuration,
                newAmplifier,
                false,
                false
        ));

        player.getPersistentData().putLong("chainsOfTimeLastTrigger", currentTick);
    }

    // ==================== 紧握的雕像 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtClenchedStatue(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!isStrikeWeapon(player)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasClenchedStatue")) return;

        if (player.getPersistentData().getBoolean("clenchedStatueNextAttack")) {
            player.getPersistentData().putBoolean("clenchedStatueNextAttack", false);

            float bonus = player.getPersistentData().getFloat("clenchedStatueBonus");
            event.setAmount(event.getAmount() * (1.0f + bonus));

            player.displayClientMessage(
                    Component.literal("§d[紧握的雕像] §c伤害 +" + (int)(bonus * 100) + "%"),
                    true
            );
        }

        float[] bonuses = {0.125f, 0.25f, 0.5f};
        float selectedBonus = bonuses[RANDOM.nextInt(3)];

        player.getPersistentData().putBoolean("clenchedStatueNextAttack", true);
        player.getPersistentData().putFloat("clenchedStatueBonus", selectedBonus);
    }

    // ==================== 粉碎的记忆 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtShatteredMemoryStrike(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasShatteredMemoryStrike")) return;

        event.setAmount(event.getAmount() + 6.0f);

        event.setAmount(event.getAmount() * 1.1f);

        if (isStrikeWeapon(player)) {
            event.setAmount(event.getAmount() * 1.2f);
        }

        if (player.getPersistentData().getBoolean("shatteredMemoryStrikeNextAttack")) {
            player.getPersistentData().putBoolean("shatteredMemoryStrikeNextAttack", false);

            float bonus = player.getPersistentData().getFloat("shatteredMemoryStrikeBonus");
            event.setAmount(event.getAmount() * (1.0f + bonus));

            player.displayClientMessage(
                    Component.literal("§d[粉碎的记忆] §c伤害 +" + (int)(bonus * 100) + "%"),
                    true
            );
        }

        if (isStrikeWeapon(player)) {
            float bonus = 0.1f + RANDOM.nextFloat() * 0.2f;
            player.getPersistentData().putBoolean("shatteredMemoryStrikeNextAttack", true);
            player.getPersistentData().putFloat("shatteredMemoryStrikeBonus", bonus);
        }
    }

    // ==================== 次元分类回收箱 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtDimensionalSortingRecyclingBin(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!isStrikeWeapon(player)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasDimensionalSortingRecyclingBin")) return;

        event.setAmount(event.getAmount() + 3.0f);

        event.setAmount(event.getAmount() * 1.25f);
    }

    // ==================== 口袋助记卡 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtPocketMnemonicCard(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!isStrikeWeapon(player)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasPocketMnemonicCard")) return;

        event.setAmount(event.getAmount() * 1.2f);
    }

    // ==================== 次元知觉变体 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtDimensionalPerceptionVariant(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!isStrikeWeapon(player)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasDimensionalPerceptionVariant")) return;

        // 原本两次 setAmount 会导致第一次的 +4.0f 被第二次的 *1.3f 覆盖，
        // 现改为先乘后加，确保伤害乘倍与基础加成同步生效：(amount * 1.3f) + 4.0f
        event.setAmount(event.getAmount() * 1.3f + 4.0f);
    }

    // ==================== 复仇账簿 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtRevengeLedger(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!isStrikeWeapon(player)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasRevengeLedger")) return;

        event.setAmount(event.getAmount() + 9.0f);

        if (player.getPersistentData().getBoolean("revengeLedgerNextAttack")) {
            player.getPersistentData().putBoolean("revengeLedgerNextAttack", false);
            event.setAmount(event.getAmount() * 1.2f);
        }

        long currentTick = player.level().getGameTime();
        long lastStack = player.getPersistentData().getLong("revengeLedgerLastStack");
        if (currentTick - lastStack < 20 * 30) return;

        MobEffectInstance existing = player.getEffect(Registration.STRIKE_ENHANCE.get());
        int newDuration = 20 * 60;
        int newAmplifier = 0;

        if (existing != null) {
            newDuration = existing.getDuration() + 20 * 60;
            newAmplifier = existing.getAmplifier() + 1;
        }

        player.removeEffect(Registration.STRIKE_ENHANCE.get());
        player.addEffect(new MobEffectInstance(
                Registration.STRIKE_ENHANCE.get(),
                newDuration,
                newAmplifier,
                false,
                false
        ));

        player.getPersistentData().putLong("revengeLedgerLastStack", currentTick);
    }

    // ==================== 打击伤害强化（buff 伤害） ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtStrikeEnhance(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!isStrikeWeapon(player)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        MobEffectInstance effect = player.getEffect(Registration.STRIKE_ENHANCE.get());
        if (effect == null) return;

        int level = effect.getAmplifier() + 1;
        event.setAmount(event.getAmount() * (1.0f + level * 0.5f));
    }

    // ==================== 复仇账簿：被攻击后 ====================

    @SubscribeEvent
    public static void onLivingDamageRevengeLedger(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (event.getAmount() <= 0) return;
        if (event.getSource().getEntity() == null) return;
        if (event.getSource().getEntity() == player) return;

        if (!player.getPersistentData().getBoolean("hasRevengeLedger")) return;

        player.getPersistentData().putBoolean("revengeLedgerNextAttack", true);
        player.setAbsorptionAmount(player.getAbsorptionAmount() + player.getMaxHealth() * 0.075f);
    }

    // ==================== 嫩肉锤 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtMeatTenderizer(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!isStrikeWeapon(player)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasMeatTenderizer")) return;

        event.setAmount(event.getAmount() + 6.0f);
    }

    // ==================== 被盗的收尾人杂志 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtStolenFixerMagazine(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!isStrikeWeapon(player)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasStolenFixerMagazine")) return;

        // 基础伤害乘倍，每次命中独立生效
        event.setAmount(event.getAmount() * 1.1f);

        long currentTick = player.level().getGameTime();
        long lastEffect = player.getPersistentData().getLong("stolenFixerMagazineLastEffectTime");
        // only refresh strike power effect every 30 seconds (real time, not hit-count)
        if (currentTick - lastEffect < 20 * 30) return;

        MobEffectInstance existing = player.getEffect(Registration.STRIKE_POWER.get());
        int newDuration = 20 * 30;
        int newAmplifier = 0;

        if (existing != null) {
            newDuration = existing.getDuration() + 20 * 30;
            newAmplifier = existing.getAmplifier();
        }

        player.removeEffect(Registration.STRIKE_POWER.get());
        player.addEffect(new MobEffectInstance(
                Registration.STRIKE_POWER.get(),
                newDuration,
                newAmplifier,
                false,
                false
        ));

        // 记录效果生效时间，而不是每次击中的时间，确保30秒冷却为真实时间而非击中计数
        player.getPersistentData().putLong("stolenFixerMagazineLastEffectTime", currentTick);
    }

    // ==================== 复仇账簿：附录 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtRevengeLedgerAppendix(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!isStrikeWeapon(player)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasRevengeLedgerAppendix")) return;

        // 原本两次 setAmount 会导致第一次的 +36.0f 被第二次的 *1.9f 覆盖，
        // 现改为先加后乘，确保基础加成与乘倍同步生效：(amount + 36.0f) * 1.9f
        event.setAmount(event.getAmount() * 1.9f + 36.0f);

        long currentTick = player.level().getGameTime();
        long hurtTime = player.getPersistentData().getLong("revengeLedgerAppendixHurtTime");
        boolean enraged = currentTick - hurtTime < 20 * 30;

        float healRate = enraged ? 0.20f : 0.10f;
        float healAmount = event.getAmount() * healRate;

        if (enraged) {
            float missing = player.getMaxHealth() - player.getHealth();
            if (healAmount <= missing) {
                player.heal(healAmount);
            } else {
                player.heal(missing);
                player.setAbsorptionAmount(player.getAbsorptionAmount() + (healAmount - missing));
            }
        } else {
            player.heal(healAmount);
        }
    }

    @SubscribeEvent
    public static void onLivingTickRevengeLedgerAppendix(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (!player.getPersistentData().getBoolean("hasRevengeLedgerAppendix")) return;

        long currentTick = player.level().getGameTime();
        long lastHeart = player.getPersistentData().getLong("revengeLedgerAppendixLastHeart");

        if (lastHeart == 0) {
            player.getPersistentData().putLong("revengeLedgerAppendixLastHeart", currentTick);
            return;
        }

        if (currentTick - lastHeart >= 20 * 30) {
            player.setAbsorptionAmount(player.getAbsorptionAmount() + 90.0f);
            player.getPersistentData().putLong("revengeLedgerAppendixLastHeart", currentTick);
        }
    }

    @SubscribeEvent
    public static void onLivingDamageRevengeLedgerAppendix(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (event.getAmount() <= 0) return;
        if (event.getSource().getEntity() == null) return;
        if (event.getSource().getEntity() == player) return;

        if (!player.getPersistentData().getBoolean("hasRevengeLedgerAppendix")) return;

        player.getPersistentData().putLong("revengeLedgerAppendixHurtTime", player.level().getGameTime());
    }

    // ==================== 中指式指虎 ====================

    private static final float[] MIDDLE_FINGER_KNUCKLE_BONUSES = {0.2f, 0.3f, 0.6f};

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtMiddleFingerKnuckle(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!isStrikeWeapon(player)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasMiddleFingerKnuckle")) return;

        float bonus = MIDDLE_FINGER_KNUCKLE_BONUSES[RANDOM.nextInt(MIDDLE_FINGER_KNUCKLE_BONUSES.length)];
        event.setAmount(event.getAmount() * (1.0f + bonus));
    }

    // ==================== 登场专用音响 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtDebutExclusiveSpeaker(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!isStrikeWeapon(player)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasDebutExclusiveSpeaker")) return;

        // 恢复造成伤害量的 5% 体力
        float healAmount = event.getAmount() * 0.05f;
        player.heal(healAmount);
    }

    // ==================== 幼·兄登场 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtYoungBrotherDebut(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasYoungBrotherDebut")) return;

        // 常驻：造成伤害 +60%，+3
        event.setAmount(event.getAmount() * 1.6f + 3.0f);

        long currentTick = player.level().getGameTime();
        long hurtTime = player.getPersistentData().getLong("youngBrotherDebutHurtTime");

        // 受到伤害后 30 秒内：造成伤害 +3，攻击对象增加 8 秒流血 I（可叠加）
        if (hurtTime != 0 && currentTick - hurtTime < 20 * 30) {
            event.setAmount(event.getAmount() + 3.0f);

            MobEffectInstance currentBleed = target.getEffect(Registration.BLEED.get());
            int newAmplifier = 0;
            int newDuration = 20 * 8;
            if (currentBleed != null) {
                newAmplifier = Math.min(currentBleed.getAmplifier() + 1, 98);
                newDuration = currentBleed.getDuration() + 20 * 8;
            }
            target.removeEffect(Registration.BLEED.get());
            target.addEffect(new MobEffectInstance(
                    Registration.BLEED.get(),
                    newDuration,
                    newAmplifier,
                    false,
                    false
            ));
        }

        // 打击命中时：使自身恢复造成伤害量 10% 的体力
        if (isStrikeWeapon(player)) {
            player.heal(event.getAmount() * 0.10f);
        }
    }

    @SubscribeEvent
    public static void onLivingDamageYoungBrotherDebut(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (event.getAmount() <= 0) return;
        if (event.getSource().getEntity() == null) return;
        if (event.getSource().getEntity() == player) return;

        if (!player.getPersistentData().getBoolean("hasYoungBrotherDebut")) return;

        // 记录受伤时间，用于 30 秒复仇窗口
        player.getPersistentData().putLong("youngBrotherDebutHurtTime", player.level().getGameTime());
    }

    // ==================== 强化纹身-中指 ====================

    private static final float[] ENHANCED_TATTOO_MIDDLE_FINGER_BONUSES = {0.10f, 0.20f, 0.30f};

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtEnhancedTattooMiddleFinger(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!isStrikeWeapon(player)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasEnhancedTattooMiddleFinger")) return;

        float bonus = ENHANCED_TATTOO_MIDDLE_FINGER_BONUSES[RANDOM.nextInt(ENHANCED_TATTOO_MIDDLE_FINGER_BONUSES.length)];
        event.setAmount(event.getAmount() * (1.0f + bonus));
    }

    // ==================== 忠义锁链 ====================

    private static final int LOYALTY_CHAINS_MAX_STACKS = 3;
    private static final float LOYALTY_CHAINS_STACK_BONUS = 0.10f;
    private static final float LOYALTY_CHAINS_PASSIVE_BONUS = 0.10f;
    private static final int LOYALTY_CHAINS_DURATION_TICKS = 20 * 30; // 30 seconds

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtLoyaltyChains(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!isStrikeWeapon(player)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasLoyaltyChains")) return;

        // 常驻：打击伤害 +10%
        event.setAmount(event.getAmount() * (1.0f + LOYALTY_CHAINS_PASSIVE_BONUS));

        // 受到伤害后 30 秒内：每层 +10%，最多 3 层（共 30%）
        long currentTick = player.level().getGameTime();
        long hurtTime = player.getPersistentData().getLong("loyaltyChainsHurtTime");
        int stacks = player.getPersistentData().getInt("loyaltyChainsStacks");

        if (hurtTime != 0 && currentTick - hurtTime < LOYALTY_CHAINS_DURATION_TICKS && stacks > 0) {
            float stackBonus = stacks * LOYALTY_CHAINS_STACK_BONUS;
            event.setAmount(event.getAmount() * (1.0f + stackBonus));
        }
    }

    @SubscribeEvent
    public static void onLivingDamageLoyaltyChains(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (event.getAmount() <= 0) return;
        if (event.getSource().getEntity() == null) return;
        if (event.getSource().getEntity() == player) return;

        if (!player.getPersistentData().getBoolean("hasLoyaltyChains")) return;

        // 记录受伤时间，增加叠层（最多 3 层）
        long currentTick = player.level().getGameTime();
        long lastHurtTime = player.getPersistentData().getLong("loyaltyChainsHurtTime");
        int currentStacks = player.getPersistentData().getInt("loyaltyChainsStacks");

        // 如果上次受伤在 30 秒内，增加叠层；否则重置为 1
        int newStacks;
        if (lastHurtTime != 0 && currentTick - lastHurtTime < LOYALTY_CHAINS_DURATION_TICKS) {
            newStacks = Math.min(currentStacks + 1, LOYALTY_CHAINS_MAX_STACKS);
        } else {
            newStacks = 1;
        }

        player.getPersistentData().putLong("loyaltyChainsHurtTime", currentTick);
        player.getPersistentData().putInt("loyaltyChainsStacks", newStacks);
    }

// ==================== 永恒的缘分锁链 ====================

    private static final float ETERNAL_CHAINS_PASSIVE_BONUS = 0.30f;
    private static final int ETERNAL_CHAINS_MAX_STACKS = 3;
    private static final float ETERNAL_CHAINS_STACK_BONUS = 0.10f;
    private static final int ETERNAL_CHAINS_DURATION_TICKS = 20 * 30; // 30 seconds
    private static final int ETERNAL_CHAINS_HASTE_INTERVAL_TICKS = 20 * 30; // 30 seconds

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtEternalChains(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!isStrikeWeapon(player)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasEternalChains")) return;

        // 永恒的缘分锁链：常驻打击伤害 +30%
        event.setAmount(event.getAmount() * ETERNAL_CHAINS_PASSIVE_BONUS);

        // 每受一次伤害：增加1层 buff，每层 +10% 打击伤害，最多 3 层（共 +30% 额外加成）
        long currentTick = player.level().getGameTime();
        int currentStacks = player.getPersistentData().getInt("eternalChainsStacks");
        int expiryTime = player.getPersistentData().getInt("eternalChainsStackExpiry");

        // 如果当前时间超过 buff 失效时间，重置计数；否则累加
        if (currentTick > expiryTime) {
            currentStacks = 0;
        }
        if (currentStacks < ETERNAL_CHAINS_MAX_STACKS) {
            currentStacks++;
        }
        // 设定新的失效时间：受伤后 30 秒内计数保持不变，30 秒后开始递减
        long newExpiry = currentTick + ETERNAL_CHAINS_DURATION_TICKS;
        player.getPersistentData().putInt("eternalChainsStacks", currentStacks);
        player.getPersistentData().putLong("eternalChainsStackExpiry", newExpiry);

        // 应用额外加成：每层 +10%
        float extraBonus = currentStacks * ETERNAL_CHAINS_STACK_BONUS;
        if (extraBonus > 0.0f) {
            event.setAmount(event.getAmount() * (1.0f + extraBonus));
        }
    }

    @SubscribeEvent
    public static void onLivingDamageEternalChains(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (event.getAmount() <= 0) return;
        if (event.getSource().getEntity() == null) return;
        if (event.getSource().getEntity() == player) return;

        if (!player.getPersistentData().getBoolean("hasEternalChains")) return;

        // 每30s增加30s迅捷1（可叠加）
        long currentTick = player.level().getGameTime();
        long lastHaste = player.getPersistentData().getLong("eternalChainsLastHaste");

        if (currentTick - lastHaste >= ETERNAL_CHAINS_HASTE_INTERVAL_TICKS) {
            // 获得 30 秒 迅捷 I
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, ETERNAL_CHAINS_DURATION_TICKS, 0, false, false));
            player.getPersistentData().putLong("eternalChainsLastHaste", currentTick);
        }
    }

    // ==================== 中指规矩 ====================

    private static final float MIDDLE_FINGER_RULES_PASSIVE_BONUS = 0.20f;
    private static final float MIDDLE_FINGER_RULES_WINDOW_BONUS = 0.30f;
    private static final float MIDDLE_FINGER_RULES_FLAT_PER_STACK = 2.0f;
    private static final int MIDDLE_FINGER_RULES_RESIST_INTERVAL = 20 * 30; // 30s
    private static final int MIDDLE_FINGER_RULES_RESIST_DURATION = 20 * 30; // 30s
    private static final int MIDDLE_FINGER_RULES_STRIKE_INTERVAL = 20 * 15; // 15s
    private static final int MIDDLE_FINGER_RULES_STRIKE_DURATION = 20 * 4;   // 4s

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtMiddleFingerRules(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!isStrikeWeapon(player)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasMiddleFingerRules")) return;

        // 常驻：打击伤害 +20%
        event.setAmount(event.getAmount() * (1.0f + MIDDLE_FINGER_RULES_PASSIVE_BONUS));

        long currentTick = player.level().getGameTime();

        // 抗性提升期间：造成伤害 +30%
        long resistEnd = player.getPersistentData().getLong("middleFingerRulesResistEnd");
        if (currentTick < resistEnd) {
            event.setAmount(event.getAmount() * (1.0f + MIDDLE_FINGER_RULES_WINDOW_BONUS));
        }

        // 受到攻击时累计的"下一次造成伤害 +2（可叠加）"
        int pendingStacks = player.getPersistentData().getInt("middleFingerRulesPendingStacks");
        if (pendingStacks > 0) {
            event.setAmount(event.getAmount() + pendingStacks * MIDDLE_FINGER_RULES_FLAT_PER_STACK);
            player.getPersistentData().putInt("middleFingerRulesPendingStacks", 0);
        }
    }

    @SubscribeEvent
    public static void onLivingDamageMiddleFingerRules(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (event.getAmount() <= 0) return;
        if (event.getSource().getEntity() == null) return;
        if (event.getSource().getEntity() == player) return;

        if (!player.getPersistentData().getBoolean("hasMiddleFingerRules")) return;

        // 受到攻击时：为下一次造成伤害累积 +2（可叠加）
        int pending = player.getPersistentData().getInt("middleFingerRulesPendingStacks");
        player.getPersistentData().putInt("middleFingerRulesPendingStacks", pending + 1);
    }

    @SubscribeEvent
    public static void onLivingTickMiddleFingerRules(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (!player.getPersistentData().getBoolean("hasMiddleFingerRules")) return;

        long currentTick = player.level().getGameTime();

        // 每 30 秒：获得 30 秒抗性提升 III，期间造成伤害 +30%
        long lastResist = player.getPersistentData().getLong("middleFingerRulesLastResist");
        if (currentTick - lastResist >= MIDDLE_FINGER_RULES_RESIST_INTERVAL) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.DAMAGE_RESISTANCE,
                    MIDDLE_FINGER_RULES_RESIST_DURATION,
                    2,
                    false,
                    false
            ));
            player.getPersistentData().putLong("middleFingerRulesLastResist", currentTick);
            player.getPersistentData().putLong("middleFingerRulesResistEnd", currentTick + MIDDLE_FINGER_RULES_RESIST_DURATION);
        }

        // 每 15 秒：获得 4 秒打击伤害提升 II
        long lastStrike = player.getPersistentData().getLong("middleFingerRulesLastStrike");
        if (currentTick - lastStrike >= MIDDLE_FINGER_RULES_STRIKE_INTERVAL) {
            MobEffectInstance existing = player.getEffect(Registration.STRIKE_POWER.get());
            int amplifier = 1;
            if (existing != null && existing.getAmplifier() > amplifier) {
                amplifier = existing.getAmplifier();
            }
            player.removeEffect(Registration.STRIKE_POWER.get());
            player.addEffect(new MobEffectInstance(
                    Registration.STRIKE_POWER.get(),
                    MIDDLE_FINGER_RULES_STRIKE_DURATION,
                    amplifier,
                    false,
                    false
            ));
            player.getPersistentData().putLong("middleFingerRulesLastStrike", currentTick);
        }
    }
}