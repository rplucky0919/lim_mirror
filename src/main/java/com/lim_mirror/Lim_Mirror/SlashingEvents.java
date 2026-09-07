package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = lim_mirror.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SlashingEvents {

    private static final Random RANDOM = new Random();

    // ==================== 斩击威力提升效果检测 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtSlashingPower(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof SwordItem)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        MobEffectInstance effect = player.getEffect(Registration.SLASHING_POWER.get());
        if (effect != null) {
            int level = effect.getAmplifier() + 1;
            float multiplier = 1.0f + level * 0.1f;
            event.setAmount(event.getAmount() * multiplier);
        }

        player.getPersistentData().putBoolean("isSlashingAttack", true);
    }

    // ==================== 梦中的电子羊：击杀触发（叠加） ====================

    @SubscribeEvent
    public static void onLivingDeathDreamingElectricSheep(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;

        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!player.getPersistentData().getBoolean("hasDreamingElectricSheep")) return;

        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof SwordItem)) return;

        long currentTick = player.level().getGameTime();
        long lastTrigger = player.getPersistentData().getLong("dreamingElectricSheepLastTrigger");
        if (currentTick - lastTrigger < 20 * 30) return;

        MobEffectInstance existing = player.getEffect(Registration.SLASHING_POWER.get());
        int addDuration = 20 * 60;
        int addAmplifier = 1;

        int newDuration = addDuration;
        int newAmplifier = addAmplifier;

        if (existing != null) {
            newDuration = existing.getDuration() + addDuration;
            newAmplifier = existing.getAmplifier() + 1;
        }

        player.removeEffect(Registration.SLASHING_POWER.get());
        player.addEffect(new MobEffectInstance(
                Registration.SLASHING_POWER.get(),
                newDuration,
                newAmplifier,
                false,
                false
        ));

        player.getPersistentData().putLong("dreamingElectricSheepLastTrigger", currentTick);

        player.displayClientMessage(
                Component.literal("§d[梦中的电子羊] §c斩击威力提升 +" + (newAmplifier + 1) + " 级 §a已增加，持续 60 秒！"),
                true
        );
    }

    // ==================== 梦中的电子羊：伤害 +20% ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtDreamingElectricSheep(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasDreamingElectricSheep")) return;

        event.setAmount(event.getAmount() * 1.2f);
    }

    // ==================== 虚饰的和平：迅捷时伤害 +20% ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtVainPeace(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasVainPeace")) return;

        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof SwordItem)) return;

        ItemStack slot5 = player.getInventory().getItem(4);
        ItemStack slot6 = player.getInventory().getItem(5);
        boolean hasSwordInSlot5 = slot5.getItem() instanceof SwordItem;
        boolean hasSwordInSlot6 = slot6.getItem() instanceof SwordItem;

        if (!hasSwordInSlot5 && !hasSwordInSlot6) return;

        if (!player.hasEffect(MobEffects.MOVEMENT_SPEED)) return;

        event.setAmount(event.getAmount() * 1.2f);
    }

    // ==================== 裁缝用剪刀：伤害 +3，最终伤害 +30% ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtTailorsScissors(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasTailorsScissors")) return;
        if (!player.getPersistentData().getBoolean("tailorsScissorsActive")) return;

        event.setAmount(event.getAmount() + 3.0f);
        event.setAmount(event.getAmount() * 1.3f);
    }

    // ==================== 决意：常驻伤害+6 + 开启后斩击伤害强化（等级累加） ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtResolve(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasResolve")) return;

        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof SwordItem)) return;

        event.setAmount(event.getAmount() + 6.0f);

        if (player.getPersistentData().getBoolean("resolveActive")) {
            MobEffectInstance existing = player.getEffect(Registration.SLASHING_POWER.get());
            int addDuration = 20 * 60;
            int addAmplifier = 5;

            int newDuration = addDuration;
            int newAmplifier = addAmplifier;

            if (existing != null) {
                newDuration = existing.getDuration() + addDuration;
                newAmplifier = existing.getAmplifier() + addAmplifier + 1;
            }

            player.removeEffect(Registration.SLASHING_POWER.get());
            player.addEffect(new MobEffectInstance(
                    Registration.SLASHING_POWER.get(),
                    newDuration,
                    newAmplifier,
                    false,
                    false
            ));
        }
    }

    // ==================== 决意：受到伤害 -75% ====================

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurtResolveDamageReduction(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;

        if (!player.getPersistentData().getBoolean("hasResolve")) return;
        if (!player.getPersistentData().getBoolean("resolveActive")) return;

        event.setAmount(event.getAmount() * 0.25f);
    }

    // ==================== 决意：受到治疗效果 -75% ====================

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHealResolveHealReduction(LivingHealEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;

        if (!player.getPersistentData().getBoolean("hasResolve")) return;
        if (!player.getPersistentData().getBoolean("resolveActive")) return;

        event.setAmount(event.getAmount() * 0.25f);
    }

    // ==================== 宣判之刻：伤害 +12.5% + 斩击伤害提升 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtMomentOfJudgment(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasMomentOfJudgment")) return;
        if (!player.getPersistentData().getBoolean("momentOfJudgmentActive")) return;

        int swordCount = player.getPersistentData().getInt("momentOfJudgmentSwordCount");
        if (swordCount <= 0) return;

        event.setAmount(event.getAmount() * 1.125f);

        float slashingBonus = 50.0f / swordCount / 100.0f;
        event.setAmount(event.getAmount() * (1.0f + slashingBonus));
    }

    // ==================== 切开的记忆 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtSeveredMemory(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasSeveredMemory")) return;

        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof SwordItem)) return;

        event.setAmount(event.getAmount() + 9.0f);
        event.setAmount(event.getAmount() * 1.3f);

        int bonus = player.getPersistentData().getInt("severedMemoryBonus");
        if (bonus > 0) {
            event.setAmount(event.getAmount() * (1.0f + bonus / 100.0f));
        }
    }

    // ==================== 磨损的砥石 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtWornWhetstone(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasWornWhetstone")) return;
        if (!player.getPersistentData().getBoolean("wornWhetstoneActive")) return;

        event.setAmount(event.getAmount() + 3.0f);
        event.setAmount(event.getAmount() * 1.25f);
    }

    // ==================== 拐杖短剑 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtCaneSword(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasCaneSword")) return;

        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof SwordItem)) return;

        event.setAmount(event.getAmount() * 1.2f);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtCaneSwordActive(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasCaneSword")) return;
        if (!player.getPersistentData().getBoolean("caneSwordActive")) return;

        event.setAmount(event.getAmount() * 1.2f);
        event.setAmount(event.getAmount() + 3.0f);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurtCaneSwordSelfDamage(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;

        if (!player.getPersistentData().getBoolean("hasCaneSword")) return;
        if (!player.getPersistentData().getBoolean("caneSwordActive")) return;

        if (event.getSource().getEntity() == player) return;

        event.setAmount(event.getAmount() * 1.2f);
    }

    // ==================== 云纹鹤颈瓶 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtCloudPatternedCraneNeckVase(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasCloudPatternedCraneNeckVase")) return;

        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof SwordItem)) return;

        // 伤害 +3
        event.setAmount(event.getAmount() + 3.0f);

        // 最终伤害 +10%
        event.setAmount(event.getAmount() * 1.1f);

        // 使目标造成伤害 -20%（标记目标，持续5秒）
        target.getPersistentData().putBoolean("craneNeckVaseDebuff", true);
        target.getPersistentData().putLong("craneNeckVaseDebuffEnd", target.level().getGameTime() + 20 * 5);

        // 桑丘之血附魔检测
        int sanchoLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.SANCHO_BLOOD.get(), weapon);
        if (sanchoLevel > 0) {
            int count = player.getPersistentData().getInt("craneNeckVaseTriggerCount");
            if (count < 8) {
                int addAmplifier = RANDOM.nextInt(2) + 1;
                MobEffectInstance existing = player.getEffect(Registration.POISE.get());
                int newAmplifier = addAmplifier - 1;
                int newDuration = 20 * 2;

                if (existing != null) {
                    newAmplifier = existing.getAmplifier() + addAmplifier;
                    newDuration = existing.getDuration() + 20 * 2;
                }

                player.removeEffect(Registration.POISE.get());
                player.addEffect(new MobEffectInstance(
                        Registration.POISE.get(),
                        newDuration,
                        newAmplifier,
                        false,
                        false
                ));

                player.getPersistentData().putInt("craneNeckVaseTriggerCount", count + 1);
            }
        }
    }

    // 目标造成伤害时减少 20%（目标作为攻击者）
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurtCloudPatternedCraneNeckVaseDebuff(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;

        long endTime = attacker.getPersistentData().getLong("craneNeckVaseDebuffEnd");
        long currentTick = attacker.level().getGameTime();

        if (currentTick <= endTime && attacker.getPersistentData().getBoolean("craneNeckVaseDebuff")) {
            event.setAmount(event.getAmount() * 0.8f);
        }
    }

    // ==================== 破碎的大剑 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtShatteredGreatsword(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasShatteredGreatsword")) return;

        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof SwordItem)) return;

        // 伤害 +6
        event.setAmount(event.getAmount() + 6.0f);

        // 伤害 +30%
        event.setAmount(event.getAmount() * 1.3f);
    }

    // 盾牌防御时：受到伤害 -3 点（持续3秒）
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurtShatteredGreatswordShieldBlock(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;

        if (!player.getPersistentData().getBoolean("hasShatteredGreatsword")) return;
        if (!player.getPersistentData().getBoolean("shatteredGreatswordShieldBlocking")) return;

        // 减少 3 点伤害
        event.setAmount(Math.max(0, event.getAmount() - 3.0f));

        // 标记受到伤害（用于触发第二次减伤）
        player.getPersistentData().putBoolean("shatteredGreatswordShieldHit", true);
        player.getPersistentData().putLong("shatteredGreatswordShieldHitTime", player.level().getGameTime());
    }

    // 若防御后依旧受到伤害，30秒后受到伤害再减少3点（持续5秒）
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurtShatteredGreatswordSecondReduction(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;

        if (!player.getPersistentData().getBoolean("hasShatteredGreatsword")) return;

        long hitTime = player.getPersistentData().getLong("shatteredGreatswordShieldHitTime");
        long currentTick = player.level().getGameTime();

        // 距离上次被击中 30 秒后
        if (currentTick - hitTime >= 20 * 30 && currentTick - hitTime < 20 * 35) {
            if (player.getPersistentData().getBoolean("shatteredGreatswordShieldHit")) {
                event.setAmount(Math.max(0, event.getAmount() - 3.0f));
                player.getPersistentData().putBoolean("shatteredGreatswordSecondReductionActive", true);
                player.getPersistentData().putLong("shatteredGreatswordSecondReductionEnd", currentTick + 20 * 5);
                player.getPersistentData().putBoolean("shatteredGreatswordShieldHit", false);
            }
        }

        // 检查二次减伤是否仍有效
        long reductionEnd = player.getPersistentData().getLong("shatteredGreatswordSecondReductionEnd");
        if (currentTick <= reductionEnd && player.getPersistentData().getBoolean("shatteredGreatswordSecondReductionActive")) {
            event.setAmount(Math.max(0, event.getAmount() - 3.0f));
        }
    }
}