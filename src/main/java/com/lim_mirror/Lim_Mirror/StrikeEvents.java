package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
}