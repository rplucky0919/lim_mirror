package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;
import java.util.UUID;

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
                Component.literal("§d[梦中的电子羊] §c斩击威力提升 +2 级（当前 " + (newAmplifier + 1) + " 级） §a已增加，持续 60 秒！"),
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

        event.setAmount(event.getAmount() + 3.0f);
        event.setAmount(event.getAmount() * 1.1f);

        target.getPersistentData().putBoolean("craneNeckVaseDebuff", true);
        target.getPersistentData().putLong("craneNeckVaseDebuffEnd", target.level().getGameTime() + 20 * 5);

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

        event.setAmount(event.getAmount() + 6.0f);
        event.setAmount(event.getAmount() * 1.3f);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurtShatteredGreatswordShieldBlock(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;

        if (!player.getPersistentData().getBoolean("hasShatteredGreatsword")) return;
        if (!player.getPersistentData().getBoolean("shatteredGreatswordShieldBlocking")) return;

        event.setAmount(Math.max(0, event.getAmount() - 3.0f));

        player.getPersistentData().putBoolean("shatteredGreatswordShieldHit", true);
        player.getPersistentData().putLong("shatteredGreatswordShieldHitTime", player.level().getGameTime());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurtShatteredGreatswordSecondReduction(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;

        if (!player.getPersistentData().getBoolean("hasShatteredGreatsword")) return;

        long hitTime = player.getPersistentData().getLong("shatteredGreatswordShieldHitTime");
        long currentTick = player.level().getGameTime();

        if (currentTick - hitTime >= 20 * 30 && currentTick - hitTime < 20 * 35) {
            if (player.getPersistentData().getBoolean("shatteredGreatswordShieldHit")) {
                event.setAmount(Math.max(0, event.getAmount() - 3.0f));
                player.getPersistentData().putBoolean("shatteredGreatswordSecondReductionActive", true);
                player.getPersistentData().putLong("shatteredGreatswordSecondReductionEnd", currentTick + 20 * 5);
                player.getPersistentData().putBoolean("shatteredGreatswordShieldHit", false);
            }
        }

        long reductionEnd = player.getPersistentData().getLong("shatteredGreatswordSecondReductionEnd");
        if (currentTick <= reductionEnd && player.getPersistentData().getBoolean("shatteredGreatswordSecondReductionActive")) {
            event.setAmount(Math.max(0, event.getAmount() - 3.0f));
        }
    }

    // ==================== 菖蒲剑 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtSwordOfCalamus(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasSwordOfCalamus")) return;
        if (!player.getPersistentData().getBoolean("swordOfCalamusActive")) return;

        event.setAmount(event.getAmount() * 1.2f);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurtSwordOfCalamusRecordDamage(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;

        if (!player.getPersistentData().getBoolean("hasSwordOfCalamus")) return;

        player.getPersistentData().putLong("swordOfCalamusLastHurtTime", player.level().getGameTime());
        player.getPersistentData().putBoolean("swordOfCalamusActive", false);
    }

    // ==================== 粉碎的木制刑具 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtShatteredWoodenTortureDevice(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasShatteredWoodenTortureDevice")) return;

        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof SwordItem)) return;

        String armorKey = "shatteredWoodenTortureDeviceArmor_" + target.getStringUUID();
        int currentReduction = target.getPersistentData().getInt(armorKey);

        if (currentReduction < 8) {
            int newReduction = Math.min(currentReduction + 2, 8);
            UUID modifierUUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
            target.getAttribute(Attributes.ARMOR).removeModifier(modifierUUID);
            AttributeModifier armorModifier = new AttributeModifier(
                    modifierUUID,
                    "shattered_wooden_torture_device_armor_reduction",
                    -newReduction,
                    AttributeModifier.Operation.ADDITION
            );
            target.getAttribute(Attributes.ARMOR).addTransientModifier(armorModifier);
            target.getPersistentData().putInt(armorKey, newReduction);
            target.getPersistentData().putLong(armorKey + "_end", target.level().getGameTime() + 20 * 5);
        } else {
            target.getPersistentData().putLong(armorKey + "_end", target.level().getGameTime() + 20 * 5);
        }

        int slowLevel = 0;
        if (player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
            slowLevel = player.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() + 1;
        }

        int bonusPercent = Math.min(slowLevel * 6, 24);
        if (bonusPercent > 0) {
            event.setAmount(event.getAmount() * (1.0f + bonusPercent / 100.0f));
        }
    }

    @SubscribeEvent
    public static void onLivingTickShatteredWoodenTortureDeviceCleanup(LivingEvent.LivingTickEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null || target.level().isClientSide()) return;

        String armorKey = "shatteredWoodenTortureDeviceArmor_" + target.getStringUUID();
        long endTime = target.getPersistentData().getLong(armorKey + "_end");
        long currentTick = target.level().getGameTime();

        if (endTime > 0 && currentTick > endTime) {
            int currentReduction = target.getPersistentData().getInt(armorKey);
            if (currentReduction > 0) {
                UUID modifierUUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
                target.getAttribute(Attributes.ARMOR).removeModifier(modifierUUID);
                target.getPersistentData().putInt(armorKey, 0);
                target.getPersistentData().putLong(armorKey + "_end", 0);
            }
        }
    }

    // ==================== 无纹的刀镡 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtUnadornedTsuba(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasUnadornedTsuba")) return;

        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof SwordItem)) return;

        MobEffectInstance poise = player.getEffect(Registration.POISE.get());
        if (poise == null) return;

        int poiseLevel = poise.getAmplifier() + 1;
        int bonusPercent = Math.min(poiseLevel, 15);

        if (bonusPercent > 0) {
            event.setAmount(event.getAmount() * (1.0f + bonusPercent / 100.0f));
        }
    }

    // ==================== 本国剑谱 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtNativeSwordManual(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasNativeSwordManual")) return;

        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof SwordItem)) return;

        event.setAmount(event.getAmount() + 3.0f);

        MobEffectInstance poise = player.getEffect(Registration.POISE.get());
        if (poise != null) {
            int poiseLevel = poise.getAmplifier() + 1;
            int bonusPercent = Math.min((int)(poiseLevel * 1.5f), 30);
            if (bonusPercent > 0) {
                event.setAmount(event.getAmount() * (1.0f + bonusPercent / 100.0f));
            }
        }

        event.setAmount(event.getAmount() * 1.2f);
        event.setAmount(event.getAmount() * 1.25f);

        if (player.getPersistentData().getBoolean("poiseCritThisAttack")) {
            event.setAmount(event.getAmount() * 1.15f);
            player.getPersistentData().putBoolean("nativeSwordManualCritBoost", true);
        }
    }

    // ==================== 锈蚀的刀柄 ====================
    // 效果：每30秒增加4秒2级斩击伤害提升
    // 实现：在 RustyHandle.curioTick 中定期添加 SLASHING_POWER 效果
    // 伤害检测：由 onLivingHurtSlashingPower 统一处理

    // ==================== 红色流苏 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtRedTassel(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasRedTassel")) return;

        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof SwordItem)) return;

        event.setAmount(event.getAmount() * 1.2f);
    }

    // ==================== 壮观 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtMagnificent(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasMagnificent")) return;

        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof SwordItem)) return;

        if (player.getPersistentData().getBoolean("magnificentBleedUsed")) return;

        long lastTrigger = player.getPersistentData().getLong("magnificentLastTrigger");
        long currentTick = player.level().getGameTime();

        if (lastTrigger == 0) return;

        if (currentTick - lastTrigger < 20 * 30) {
            target.removeEffect(Registration.BLEED.get());
            target.addEffect(new MobEffectInstance(
                    Registration.BLEED.get(),
                    20 * 8,
                    3,
                    false,
                    false
            ));

            player.getPersistentData().putBoolean("magnificentBleedUsed", true);
        }
    }

    // ==================== 不动 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtImmovable(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasImmovable")) return;

        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof SwordItem)) return;

        event.setAmount(event.getAmount() * 1.35f);

        MobEffectInstance poise = player.getEffect(Registration.POISE.get());
        if (poise != null) {
            int poiseLevel = poise.getAmplifier() + 1;
            if (poiseLevel > 0) {
                target.hurt(target.damageSources().magic(), poiseLevel);
            }
        }
    }

    // ==================== 陈旧的长袍 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtOldRobe(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasOldRobe")) return;

        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof SwordItem)) return;

        event.setAmount(event.getAmount() * 1.5f);

        ItemStack firstSword = findFirstSwordInHotbar(player);
        if (firstSword != null && firstSword == weapon) {
            event.setAmount(event.getAmount() + 9.0f);
        }
    }

    private static ItemStack findFirstSwordInHotbar(Player player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof SwordItem) {
                return stack;
            }
        }
        return null;
    }

    // ==================== 陈旧的刀柄 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtOldHandle(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasOldHandle")) return;

        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof SwordItem)) return;

        int smiteLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SMITE, weapon);
        if (smiteLevel > 0) {
            event.setAmount(event.getAmount() * 1.2f);
        }
    }

    // ==================== 琢磨 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtPolishing(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasPolishing")) return;

        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof SwordItem)) return;

        event.setAmount(event.getAmount() * 1.35f);

        if (player.getPersistentData().getBoolean("poiseCritThisAttack")) {
            MobEffectInstance poise = player.getEffect(Registration.POISE.get());
            if (poise != null) {
                int poiseLevel = poise.getAmplifier() + 1;
                int extraDamage = poiseLevel / 2;
                if (extraDamage > 0) {
                    target.hurt(target.damageSources().magic(), extraDamage);
                }
            }
        }
    }

    // ==================== 丛云 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtMurakumo(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasMurakumo")) return;

        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof SwordItem)) return;

        long durabilityEnd = player.getPersistentData().getLong("murakumoDurabilityEnd");
        long currentTick = player.level().getGameTime();

        if (currentTick <= durabilityEnd && player.getPersistentData().getBoolean("murakumoDurabilityChecked")) {
            int unbreakingLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, weapon);
            if (unbreakingLevel > 0) {
                float bonusPercent = 80.0f / unbreakingLevel;
                event.setAmount(event.getAmount() * (1.0f + bonusPercent / 100.0f));
            }
        }

        int lostBonus = player.getPersistentData().getInt("murakumoLostBonus");
        if (lostBonus > 0) {
            event.setAmount(event.getAmount() + lostBonus);
        }

        long friendlyKillEnd = player.getPersistentData().getLong("murakumoFriendlyKillEnd");
        if (currentTick <= friendlyKillEnd) {
            event.setAmount(event.getAmount() * 1.2f);
        }
    }

    @SubscribeEvent
    public static void onLivingDeathMurakumo(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;

        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!player.getPersistentData().getBoolean("hasMurakumo")) return;

        if (target instanceof TamableAnimal tameable) {
            if (tameable.isTame() && tameable.getOwnerUUID() != null && tameable.getOwnerUUID().equals(player.getUUID())) {
                player.getPersistentData().putLong("murakumoFriendlyKillEnd", player.level().getGameTime() + 20 * 30);
                player.displayClientMessage(
                        Component.literal("§d[丛云] §c击杀友方生物！30 秒内造成伤害 +20%"),
                        true
                );
            }
        }
    }

    // ==================== 影织斗笠 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtShadowWovenHat(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasShadowWovenHat")) return;

        MobEffectInstance speed = player.getEffect(MobEffects.MOVEMENT_SPEED);
        if (speed == null) return;

        int speedLevel = speed.getAmplifier() + 1;
        float bonusPercent = Math.min(speedLevel / 3.0f, 30.0f);

        if (bonusPercent > 0) {
            event.setAmount(event.getAmount() * (1.0f + bonusPercent / 100.0f));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurtShadowWovenHatDefense(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;

        if (!player.getPersistentData().getBoolean("hasShadowWovenHat")) return;

        MobEffectInstance speed = player.getEffect(MobEffects.MOVEMENT_SPEED);
        if (speed == null) return;

        int speedLevel = speed.getAmplifier() + 1;
        float reductionPercent = Math.min(speedLevel / 3.0f, 30.0f);

        if (reductionPercent > 0) {
            event.setAmount(event.getAmount() * (1.0f - reductionPercent / 100.0f));
        }
    }

    // ==================== 蒙蔽心扉的红布 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtBlindingRedCloth(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasBlindingRedCloth")) return;

        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof SwordItem)) return;

        long currentTick = player.level().getGameTime();
        long lastTrigger = player.getPersistentData().getLong("blindingRedClothLastTrigger");

        if (currentTick - lastTrigger < 20 * 30) return;

        int stacks = player.getPersistentData().getInt("blindingRedClothStacks");
        if (stacks < 6) {
            stacks++;
            player.getPersistentData().putInt("blindingRedClothStacks", stacks);
        }

        event.setAmount(event.getAmount() + stacks);

        player.getPersistentData().putLong("blindingRedClothLastTrigger", currentTick);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtBlindingRedClothTotem(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasBlindingRedCloth")) return;

        long totemEnd = player.getPersistentData().getLong("blindingRedClothTotemEnd");
        long currentTick = player.level().getGameTime();

        if (currentTick <= totemEnd) {
            event.setAmount(event.getAmount() * 1.2f);
        }
    }
}