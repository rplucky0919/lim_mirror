package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = lim_mirror.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TremorEvents2 {

    private static final int MAX_TREMOR_LEVEL = 99;

    // ==================== 涓滴之杯 ====================
    private static final int TRICKLE_CUP_ADD_TREMOR_LEVEL = 3;
    private static final int TRICKLE_CUP_ADD_TREMOR_DURATION = 20 * 6;
    private static final int TRICKLE_CUP_MAX_COUNT = 3;
    private static final int TRICKLE_CUP_COOLDOWN = 20 * 10;
    private static final float TRICKLE_CUP_ADD_DAMAGE = 6.0f;
    private static final float TRICKLE_CUP_FINAL_MULTIPLIER = 1.2f;

    // ==================== 怀表：Type L ====================
    private static final float POCKET_WATCH_L_ADD_DAMAGE = 8.0f;
    private static final float POCKET_WATCH_L_FINAL_MULTIPLIER = 1.25f;
    private static final float POCKET_WATCH_L_ENEMY_DAMAGE_MULTIPLIER = 1.5f;
    private static final int POCKET_WATCH_L_TREMOR_INTERVAL = 20 * 30;
    private static final int POCKET_WATCH_L_ADD_TREMOR_LEVEL = 6;

    // ==================== 怀表：Type E ====================
    private static final float POCKET_WATCH_E_ADD_DAMAGE = 6.0f;
    private static final float POCKET_WATCH_E_FINAL_MULTIPLIER = 1.25f;
    private static final float POCKET_WATCH_E_SLOW_DAMAGE_MULTIPLIER = 1.25f;
    private static final float POCKET_WATCH_E_ENEMY_DAMAGE_MULTIPLIER = 1.6f;
    private static final int POCKET_WATCH_E_TREMOR_INTERVAL = 20 * 30;
    private static final int POCKET_WATCH_E_ADD_TREMOR_LEVEL = 10;
    private static final int POCKET_WATCH_E_ADD_TREMOR_DURATION = 20 * 10;

    // ==================== 怀表：Type Y ====================
    private static final float POCKET_WATCH_Y_ADD_DAMAGE = 6.0f;
    private static final float POCKET_WATCH_Y_FINAL_MULTIPLIER = 1.25f;
    private static final float POCKET_WATCH_Y_EXTRA_ADD_DAMAGE = 6.0f;
    private static final float POCKET_WATCH_Y_EXTRA_FINAL_MULTIPLIER = 2.0f;
    private static final int POCKET_WATCH_Y_TREMOR_INTERVAL = 20 * 30;
    private static final int POCKET_WATCH_Y_SELF_TREMOR_DURATION = 20 * 6;

    // ==================== 怀表：Type P ====================
    private static final float POCKET_WATCH_P_ADD_DAMAGE = 6.0f;
    private static final float POCKET_WATCH_P_FINAL_MULTIPLIER = 1.25f;
    private static final float POCKET_WATCH_P_SLOW_ADD_DAMAGE = 6.0f;
    private static final float POCKET_WATCH_P_SLOW_FINAL_MULTIPLIER = 1.5f;
    private static final float POCKET_WATCH_P_EXTRA_ADD_DAMAGE = 5.0f;

    // ==================== 影子怪物 ====================
    private static final int SHADOW_MONSTER_SELF_TREMOR_DURATION = 20 * 2;
    private static final int SHADOW_MONSTER_SELF_TREMOR_WEAK_DURATION = 20 * 4;
    private static final int SHADOW_MONSTER_WEAKNESS_DURATION = 20 * 5;
    private static final int SHADOW_MONSTER_WEAKNESS_COOLDOWN = 20 * 30;
    private static final int SHADOW_MONSTER_WEAKNESS_PER_WEAPON = 3;

    /**
     * 检查物品是否为武器
     */
    private static boolean isWeapon(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof TieredItem
                || stack.getItem() instanceof TridentItem;
    }

    /**
     * 统计玩家快捷栏中武器数量
     */
    private static int countHotbarWeapons(Player player) {
        int count = 0;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (isWeapon(stack)) {
                count++;
            }
        }
        return count;
    }

    // ==================== 涓滴之杯：3次攻击触发震颤，之后10秒冷却 ====================

    @SubscribeEvent
    public static void onLivingHurtTrickleCupApply(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasTrickleCup")) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);
        if (enchantLevel <= 0) return;

        long currentTick = player.level().getGameTime();
        long cooldownEnd = player.getPersistentData().getLong("trickleCupCooldownEnd");

        if (currentTick < cooldownEnd) return;

        int count = player.getPersistentData().getInt("trickleCupCount") + 1;

        MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
        int newAmplifier = (tremor != null)
                ? Math.min(Math.max(0, tremor.getAmplifier() + TRICKLE_CUP_ADD_TREMOR_LEVEL), MAX_TREMOR_LEVEL - 1)
                : TRICKLE_CUP_ADD_TREMOR_LEVEL - 1;
        int newDuration = (tremor != null)
                ? tremor.getDuration() + TRICKLE_CUP_ADD_TREMOR_DURATION
                : TRICKLE_CUP_ADD_TREMOR_DURATION;

        target.removeEffect(Registration.TREMOR.get());
        target.addEffect(new MobEffectInstance(
                Registration.TREMOR.get(),
                newDuration,
                newAmplifier,
                false,
                false
        ));

        if (count >= TRICKLE_CUP_MAX_COUNT) {
            player.getPersistentData().putInt("trickleCupCount", 0);
            player.getPersistentData().putLong("trickleCupCooldownEnd", currentTick + TRICKLE_CUP_COOLDOWN);
        } else {
            player.getPersistentData().putInt("trickleCupCount", count);
        }
    }

    // ==================== 涓滴之杯：伤害+6，最终伤害×1.2 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtTrickleCupDamage(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasTrickleCup")) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);
        if (enchantLevel <= 0) return;

        event.setAmount(event.getAmount() + TRICKLE_CUP_ADD_DAMAGE);
        event.setAmount(event.getAmount() * TRICKLE_CUP_FINAL_MULTIPLIER);
    }

    // ==================== 怀表Type L：伤害+8，最终伤害×1.25 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtPocketWatchLDamage(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasPocketWatchTypeL")) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);
        if (enchantLevel <= 0) return;

        event.setAmount(event.getAmount() + POCKET_WATCH_L_ADD_DAMAGE);
        event.setAmount(event.getAmount() * POCKET_WATCH_L_FINAL_MULTIPLIER);
    }

    // ==================== 怀表Type L：敌方受到伤害+50% ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtPocketWatchLEnemyDamage(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;

        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasPocketWatchTypeL")) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);
        if (enchantLevel <= 0) return;

        event.setAmount(event.getAmount() * POCKET_WATCH_L_ENEMY_DAMAGE_MULTIPLIER);
    }

    // ==================== 怀表Type L：每30秒对攻击目标+6级震颤 ====================

    @SubscribeEvent
    public static void onLivingHurtPocketWatchLApplyTremor(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasPocketWatchTypeL")) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);
        if (enchantLevel <= 0) return;

        long currentTick = player.level().getGameTime();
        long lastTrigger = player.getPersistentData().getLong("pocketWatchLLastTrigger");

        if (currentTick - lastTrigger < POCKET_WATCH_L_TREMOR_INTERVAL) return;

        MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
        int newAmplifier = (tremor != null)
                ? Math.min(Math.max(0, tremor.getAmplifier() + POCKET_WATCH_L_ADD_TREMOR_LEVEL), MAX_TREMOR_LEVEL - 1)
                : POCKET_WATCH_L_ADD_TREMOR_LEVEL - 1;
        int newDuration = (tremor != null) ? tremor.getDuration() : 20;

        target.removeEffect(Registration.TREMOR.get());
        target.addEffect(new MobEffectInstance(
                Registration.TREMOR.get(),
                newDuration,
                newAmplifier,
                false,
                false
        ));

        player.getPersistentData().putLong("pocketWatchLLastTrigger", currentTick);
    }

    // ==================== 怀表Type E：伤害+6，最终伤害×1.25 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtPocketWatchEDamage(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasPocketWatchTypeE")) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);
        if (enchantLevel <= 0) return;

        event.setAmount(event.getAmount() + POCKET_WATCH_E_ADD_DAMAGE);
        event.setAmount(event.getAmount() * POCKET_WATCH_E_FINAL_MULTIPLIER);
    }

    // ==================== 怀表Type E：目标有缓慢时伤害+25% ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtPocketWatchESlowBonus(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasPocketWatchTypeE")) return;

        if (!target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) return;

        event.setAmount(event.getAmount() * POCKET_WATCH_E_SLOW_DAMAGE_MULTIPLIER);
    }

    // ==================== 怀表Type E：敌方受到伤害+60% ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtPocketWatchEEnemyDamage(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasPocketWatchTypeE")) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);
        if (enchantLevel <= 0) return;

        event.setAmount(event.getAmount() * POCKET_WATCH_E_ENEMY_DAMAGE_MULTIPLIER);
    }

    // ==================== 怀表Type E：每30秒对攻击目标+10秒10级震颤 ====================

    @SubscribeEvent
    public static void onLivingHurtPocketWatchEApplyTremor(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasPocketWatchTypeE")) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);
        if (enchantLevel <= 0) return;

        long currentTick = player.level().getGameTime();
        long lastTrigger = player.getPersistentData().getLong("pocketWatchELastTrigger");

        if (currentTick - lastTrigger < POCKET_WATCH_E_TREMOR_INTERVAL) return;

        MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
        int newAmplifier = (tremor != null)
                ? Math.min(Math.max(0, tremor.getAmplifier() + POCKET_WATCH_E_ADD_TREMOR_LEVEL), MAX_TREMOR_LEVEL - 1)
                : POCKET_WATCH_E_ADD_TREMOR_LEVEL - 1;
        int newDuration = (tremor != null)
                ? tremor.getDuration() + POCKET_WATCH_E_ADD_TREMOR_DURATION
                : POCKET_WATCH_E_ADD_TREMOR_DURATION;

        target.removeEffect(Registration.TREMOR.get());
        target.addEffect(new MobEffectInstance(
                Registration.TREMOR.get(),
                newDuration,
                newAmplifier,
                false,
                false
        ));

        player.getPersistentData().putLong("pocketWatchELastTrigger", currentTick);
    }

    // ==================== 怀表Type Y：伤害+6，最终伤害×1.25，额外+6，最终×2.0 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtPocketWatchYDamage(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasPocketWatchTypeY")) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);
        if (enchantLevel <= 0) return;

        event.setAmount(event.getAmount() + POCKET_WATCH_Y_ADD_DAMAGE);
        event.setAmount(event.getAmount() * POCKET_WATCH_Y_FINAL_MULTIPLIER);

        event.setAmount(event.getAmount() + POCKET_WATCH_Y_EXTRA_ADD_DAMAGE);
        event.setAmount(event.getAmount() * POCKET_WATCH_Y_EXTRA_FINAL_MULTIPLIER);
    }

    // ==================== 怀表Type Y：每30秒对自身+6秒震颤 ====================

    @SubscribeEvent
    public static void onLivingTickPocketWatchY(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (!player.getPersistentData().getBoolean("hasPocketWatchTypeY")) return;

        long currentTick = player.level().getGameTime();
        long lastTrigger = player.getPersistentData().getLong("pocketWatchYLastTrigger");

        if (currentTick - lastTrigger < POCKET_WATCH_Y_TREMOR_INTERVAL) return;

        MobEffectInstance tremor = player.getEffect(Registration.TREMOR.get());
        int duration = POCKET_WATCH_Y_SELF_TREMOR_DURATION;
        if (TremorEvents.shouldExtendSelfTremor(player)) {
            duration += 20;
        }
        int newDuration = (tremor != null) ? tremor.getDuration() + duration : duration;
        int amplifier = (tremor != null) ? Math.max(0, tremor.getAmplifier()) : 0;

        player.removeEffect(Registration.TREMOR.get());
        player.addEffect(new MobEffectInstance(
                Registration.TREMOR.get(),
                newDuration,
                amplifier,
                false,
                false
        ));

        player.getPersistentData().putLong("pocketWatchYLastTrigger", currentTick);
    }

    // ==================== 怀表Type P：伤害+6，最终伤害×1.25，缓慢II+时额外+6×1.5，额外+5 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtPocketWatchPDamage(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasPocketWatchTypeP")) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);
        if (enchantLevel <= 0) return;

        // 第一组：伤害+6，最终伤害×1.25
        event.setAmount(event.getAmount() + POCKET_WATCH_P_ADD_DAMAGE);
        event.setAmount(event.getAmount() * POCKET_WATCH_P_FINAL_MULTIPLIER);

        // 缓慢II+：伤害+6，最终伤害×1.5
        MobEffectInstance slow = player.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
        if (slow != null && slow.getAmplifier() >= 1) {
            event.setAmount(event.getAmount() + POCKET_WATCH_P_SLOW_ADD_DAMAGE);
            event.setAmount(event.getAmount() * POCKET_WATCH_P_SLOW_FINAL_MULTIPLIER);
        }

        // 额外伤害+5
        event.setAmount(event.getAmount() + POCKET_WATCH_P_EXTRA_ADD_DAMAGE);
    }

    // ==================== 影子怪物：攻击时自身+震颤，目标有虚弱时施加虚弱，每把武器+10%伤害 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtShadowMonster(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasShadowMonster")) return;

        boolean targetHasWeakness = target.hasEffect(MobEffects.WEAKNESS);

        // ===== 1. 对自身增加震颤 =====
        int selfDuration = targetHasWeakness
                ? SHADOW_MONSTER_SELF_TREMOR_WEAK_DURATION
                : SHADOW_MONSTER_SELF_TREMOR_DURATION;

        MobEffectInstance selfTremor = player.getEffect(Registration.TREMOR.get());
        int finalDuration = selfDuration;
        if (TremorEvents.shouldExtendSelfTremor(player)) {
            finalDuration += 20;
        }
        int newSelfDuration = (selfTremor != null) ? selfTremor.getDuration() + finalDuration : finalDuration;
        int selfAmplifier = (selfTremor != null) ? Math.max(0, selfTremor.getAmplifier()) : 0;

        player.removeEffect(Registration.TREMOR.get());
        player.addEffect(new MobEffectInstance(
                Registration.TREMOR.get(),
                newSelfDuration,
                selfAmplifier,
                false,
                false
        ));

        // ===== 2. 目标有虚弱时：施加(快捷栏武器数×3)级虚弱5秒，冷却30秒 =====
        if (targetHasWeakness) {
            long currentTick = player.level().getGameTime();
            long lastTrigger = player.getPersistentData().getLong("shadowMonsterLastTrigger");

            if (currentTick - lastTrigger >= SHADOW_MONSTER_WEAKNESS_COOLDOWN) {
                int weaponCount = countHotbarWeapons(player);
                int weaknessLevel = weaponCount * SHADOW_MONSTER_WEAKNESS_PER_WEAPON;

                if (weaknessLevel > 0) {
                    target.addEffect(new MobEffectInstance(
                            MobEffects.WEAKNESS,
                            SHADOW_MONSTER_WEAKNESS_DURATION,
                            weaknessLevel - 1,
                            false,
                            false
                    ));
                    player.getPersistentData().putLong("shadowMonsterLastTrigger", currentTick);
                }
            }
        }

        // ===== 3. 每把武器 +10% 伤害 =====
        int weaponCount = countHotbarWeapons(player);
        if (weaponCount > 0) {
            float damageMultiplier = 1.0f + weaponCount * 0.1f;
            event.setAmount(event.getAmount() * damageMultiplier);
        }
    }

    // ==================== 强制纠缠定序器：伤害+5 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtForcedEntanglementSequencer(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasForcedEntanglementSequencer")) return;

        event.setAmount(event.getAmount() + 5.0f);
    }

    // ==================== 超精密时间加速装置：伤害加成 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtUltraPrecisionTimeAccelerator(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasUltraPrecisionTimeAccelerator")) return;

        if (!player.hasEffect(Registration.PRECISION_TIME_ACCELERATION.get())) return;

        event.setAmount(event.getAmount() + 5.0f);
        event.setAmount(event.getAmount() * 1.5f);
    }

    // ==================== 黑铁马胄：命中+2级震颤 ====================

    @SubscribeEvent
    public static void onLivingHurtBlackIronBarding(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasBlackIronBarding")) return;

        if (!player.hasEffect(MobEffects.ABSORPTION)) return;

        MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
        int newAmplifier = (tremor != null)
                ? Math.min(Math.max(0, tremor.getAmplifier() + 2), MAX_TREMOR_LEVEL - 1)
                : 1;
        int newDuration = (tremor != null)
                ? tremor.getDuration() + 20
                : 20;

        target.removeEffect(Registration.TREMOR.get());
        target.addEffect(new MobEffectInstance(
                Registration.TREMOR.get(),
                newDuration,
                newAmplifier,
                false,
                false
        ));
    }

    // ==================== 为了指挥官：常驻伤害加成 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtForTheCommander(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasForTheCommander")) return;

        event.setAmount(event.getAmount() + 6.0f);
        event.setAmount(event.getAmount() * 1.3f);
    }

    // ==================== 为了指挥官：消耗弹药后命中完整引爆 + 追加火焰伤害 ====================

    @SubscribeEvent
    public static void onLivingHurtForTheCommanderEffect(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasForTheCommander")) return;

        long lastConsume = player.getPersistentData().getLong("forTheCommanderLastAmmoConsume");
        long currentTick = player.level().getGameTime();

        if (currentTick - lastConsume > 20 * 60) {
            player.getPersistentData().putBoolean("forTheCommanderActive", false);
            return;
        }

        if (!player.getPersistentData().getBoolean("forTheCommanderActive")) return;

        // ===== 1. 完整震颤引爆（与右键引爆一致） =====
        if (TremorEvents.hasAnyTremorEffect(target)) {
            TremorEvents.explodeSingleTarget(player, target, currentTick);
        }

        // ===== 2. 若目标有烧伤+震颤：追加（烧伤+震颤）火焰伤害 =====
        MobEffectInstance burn = target.getEffect(Registration.BURN.get());
        MobEffectInstance targetTremor = target.getEffect(Registration.TREMOR.get());

        if (burn != null && targetTremor != null) {
            int burnLevel = Math.max(0, burn.getAmplifier() + 1);
            int tremorLevel = Math.max(0, targetTremor.getAmplifier() + 1);

            if (burnLevel > 0 && tremorLevel > 0) {
                float extraDamage = burnLevel + tremorLevel;
                target.hurt(target.damageSources().onFire(), extraDamage);
            }
        }
    }

    // ==================== 弹药消耗统一触发（震颤饰品） ====================

    @SubscribeEvent
    public static void onArrowNockForTremor(ArrowNockEvent event) {
        Player player = event.getEntity();
        if (player == null) return;
        if (player.level().isClientSide()) return;

        ItemStack bow = event.getBow();
        if (bow.isEmpty()) return;

        boolean hasArrow = !player.getProjectile(bow).isEmpty() || player.getAbilities().instabuild;
        if (!hasArrow) return;

        if (player.getPersistentData().getBoolean("hasForTheCommander")) {
            ForTheCommander.triggerSpeedBoost(player);
        }
    }
}