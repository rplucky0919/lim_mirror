package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = lim_mirror.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BleedEvents {

    /**
     * 统一的流血施加入口：时长累加，等级取「现有等级 + 本次增量」，并受 98 级上限约束。
     *
     * @param target         目标
     * @param addDuration    本次要累加的时长（tick）
     * @param addLevels      本次要累加的等级（可小于 0 表示仅覆盖等级）
     * @param forceLevel     是否强制把等级设为 addLevels（true 时忽略累加，直接取该值）
     */
    public static void applyBleed(LivingEntity target, int addDuration, int addLevels, boolean forceLevel) {
        if (target == null || target.level().isClientSide()) return;

        MobEffectInstance current = target.getEffect(Registration.BLEED.get());
        int currentDuration = (current == null) ? 0 : current.getDuration();
        int currentLevel = (current == null) ? 0 : current.getAmplifier() + 1;

        int newDuration = currentDuration + addDuration;
        int newLevel = forceLevel ? addLevels : (currentLevel + addLevels);

        newLevel = Math.max(1, Math.min(newLevel, 99));
        newDuration = Math.max(newDuration, 1);

        target.removeEffect(Registration.BLEED.get());
        target.addEffect(new MobEffectInstance(
                Registration.BLEED.get(),
                newDuration,
                newLevel - 1,
                false,
                false
        ));
    }

    @SubscribeEvent
    public static void onLivingTickBleed(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null) return;
        if (entity.level().isClientSide()) return;

        MobEffectInstance bleed = entity.getEffect(Registration.BLEED.get());
        if (bleed == null) return;

        long currentTick = entity.level().getGameTime();
        long lastDamageTick = entity.getPersistentData().getLong("bleedLastDamageTick");
        if (currentTick - lastDamageTick < 20) return;

        double dx = entity.getX() - entity.getPersistentData().getDouble("bleedLastX");
        double dz = entity.getZ() - entity.getPersistentData().getDouble("bleedLastZ");

        if (dx * dx + dz * dz > 0.0001) {
            int level = bleed.getAmplifier() + 1;
            float damage = level * 1.0f;
            entity.hurt(entity.damageSources().magic(), damage);
            entity.getPersistentData().putLong("bleedLastDamageTick", currentTick);
        }

        entity.getPersistentData().putDouble("bleedLastX", entity.getX());
        entity.getPersistentData().putDouble("bleedLastZ", entity.getZ());
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.SANCHO_BLOOD.get(), weapon);
        if (enchantLevel <= 0) return;

        // 烟霾与铁丝网：每次命中增加的流血强度变为 2.5 倍
        boolean hazeBoost = player.getPersistentData().getBoolean("hasHazeAndBarbedWire");
        // 以 0.1 级为单位累计，支持 2.5 这类小数倍率（+1 级 → +2.5 级）
        int bonusInTenths = hazeBoost ? (int) Math.round(1 * 2.5 * 10) : 10;

        MobEffectInstance currentBleed = target.getEffect(Registration.BLEED.get());
        int newAmplifier;
        int newDuration = 20;

        if (currentBleed != null) {
            // 现有层数 + 本次增量（十进计数再折算回 amplifier）
            int currentTenths = (currentBleed.getAmplifier() + 1) * 10;
            int totalTenths = currentTenths + bonusInTenths;
            newAmplifier = Math.min(totalTenths / 10 - 1, 98);
            // 时长累加
            newDuration = currentBleed.getDuration() + 20;
        } else {
            newAmplifier = Math.min(bonusInTenths / 10 - 1, 98);
        }

        target.removeEffect(Registration.BLEED.get());
        target.addEffect(new MobEffectInstance(
                Registration.BLEED.get(),
                newDuration,
                newAmplifier,
                false,
                false
        ));

        // 烟霾与铁丝网：敌方获得缓慢 III
        if (hazeBoost) {
            target.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            target.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN,
                    20 * 5,
                    2,
                    false,
                    false
            ));
        }
    }

    // ==================== 拟伤虫 ====================
    // 注意：必须晚于上面桑丘之血附魔的流血结算执行，
    // 否则会被附魔的 +1 层逻辑覆盖掉。故用 LOW 优先级。
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingHurtMimicWoundBug(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasMimicWoundBug")) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.SANCHO_BLOOD.get(), weapon);
        if (enchantLevel <= 0) return;

        // 4 秒 5 级流血：时长累加，等级取「现有 +5」
        applyBleed(target, 20 * 4, 5, false);
    }

    // ==================== 锈蚀的美工刀 ====================
    // 与拟伤虫同类：晚于桑丘之血附魔结算，再覆盖为指定等级与时长
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingHurtRustedUtilityKnife(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasRustedUtilityKnife")) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.SANCHO_BLOOD.get(), weapon);
        if (enchantLevel <= 0) return;

        // 4 秒 3 级流血：时长累加，等级取「现有 +3」
        applyBleed(target, 20 * 4, 3, false);
    }

    // ==================== 染血铁钉 ====================
    // 用 LOWEST 保证在附魔/拟伤虫的流血结算之后再判断"目标是否已带有流血"
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtBloodyIronSpike(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasBloodyIronSpike")) return;

        MobEffectInstance currentBleed = target.getEffect(Registration.BLEED.get());

        // 目标无流血 → 1 级；已有流血 → 3 级。时长统一累加 1 秒
        int addLevels = (currentBleed == null) ? 1 : 3;
        applyBleed(target, 20, addLevels, false);

        // 目标原本无流血时，额外降低护甲 2 点（固定 UUID，不叠加）
        if (currentBleed == null) {
            target.getAttribute(Attributes.ARMOR).removeModifier(BloodyIronSpike.ARMOR_REDUCE);
            target.getAttribute(Attributes.ARMOR).addTransientModifier(BloodyIronSpike.ARMOR_REDUCE);
        }
    }

    // ==================== 白棉花 ====================

    private static final int WHITE_COTTON_INTERVAL = 20 * 30;   // 每 30 秒
    private static final double WHITE_COTTON_RADIUS = 5.0;
    private static final int WHITE_COTTON_BLEED_DURATION = 20;  // 1 秒
    private static final int WHITE_COTTON_BLEED_AMPLIFIER = 11;  // 12 级
    private static final long WHITE_COTTON_MARK_DURATION = 20L * 30; // 标记 30 秒
    private static final float WHITE_COTTON_DAMAGE_MOD = 6.0f;

    /** 30 秒周期：解除自身虚弱 + 对 5 格内生物施加 1 秒 12 级流血并打上标记 */
    @SubscribeEvent
    public static void onLivingTickWhiteCotton(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (!player.getPersistentData().getBoolean("hasWhiteCotton")) return;

        long currentTick = player.level().getGameTime();
        long lastTrigger = player.getPersistentData().getLong("whiteCottonLastTrigger");

        // 首次装备只记录时间，不立刻触发
        if (lastTrigger == 0) {
            player.getPersistentData().putLong("whiteCottonLastTrigger", currentTick);
            return;
        }
        if (currentTick - lastTrigger < WHITE_COTTON_INTERVAL) return;
        player.getPersistentData().putLong("whiteCottonLastTrigger", currentTick);

        // 解除自身的虚弱（"解除敌方虚弱"——虚弱本就是压制我方输出的减益）
        if (player.hasEffect(MobEffects.WEAKNESS)) {
            player.removeEffect(MobEffects.WEAKNESS);
        }

        AABB area = new AABB(player.blockPosition()).inflate(WHITE_COTTON_RADIUS);
        for (LivingEntity target : player.level().getEntitiesOfClass(LivingEntity.class, area,
                e -> e != player && e.isAlive() && !(e instanceof Player))) {

            // 对范围内生物施加 1 秒 12 级流血（时长累加）
            applyBleed(target, WHITE_COTTON_BLEED_DURATION, 12, false);

            // 记录标记施加时刻：30 秒后进入"易伤/减伤"状态
            target.getPersistentData().putLong("whiteCottonMarkCreated", currentTick);
        }
    }

    private static boolean isWhiteCottonMarkActive(LivingEntity entity) {
        long createdAt = entity.getPersistentData().getLong("whiteCottonMarkCreated");
        if (createdAt == 0) return false;
        long now = entity.level().getGameTime();
        long elapsed = now - createdAt;
        // 30 秒后开始生效，持续 30 秒
        return elapsed >= WHITE_COTTON_MARK_DURATION && elapsed <= WHITE_COTTON_MARK_DURATION * 2;
    }

    /** 被标记的生物攻击玩家：伤害 −6 */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtWhiteCottonAttacker(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;
        if (!(event.getEntity() instanceof Player)) return;

        if (!isWhiteCottonMarkActive(attacker)) return;

        event.setAmount(Math.max(0.0f, event.getAmount() - WHITE_COTTON_DAMAGE_MOD));
    }

    /** 被标记的生物受到伤害：+6 */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtWhiteCottonVictim(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target instanceof Player) return;

        if (!isWhiteCottonMarkActive(target)) return;

        event.setAmount(event.getAmount() + WHITE_COTTON_DAMAGE_MOD);
    }

    // ==================== 又小又可能很坏的玩偶 ====================

    private static final float TINY_DOLL_BONUS_BASE = 0.10f;
    private static final float TINY_DOLL_BONUS_SMITE = 0.20f;
    private static final float TINY_DOLL_REDUCTION_BASE = 0.20f;
    private static final float TINY_DOLL_REDUCTION_SMITE = 0.30f;

    /** 主手带亡灵杀手附魔？ */
    private static boolean hasSmite(Player player) {
        return EnchantmentHelper.getItemEnchantmentLevel(
                net.minecraft.world.item.enchantment.Enchantments.SMITE, player.getMainHandItem()) > 0;
    }

    /** 敌方带流血时：造成伤害提高（有亡灵杀手则提档） */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtTinyDollDeal(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        LivingEntity target = event.getEntity();
        if (target == null || target == player) return;

        if (!player.getPersistentData().getBoolean("hasTinyDoll")) return;

        // 晚于流血施加方结算，确保"目标是否带流血"判断准确
        if (target.getEffect(Registration.BLEED.get()) == null) return;

        float bonus = hasSmite(player) ? TINY_DOLL_BONUS_SMITE : TINY_DOLL_BONUS_BASE;
        event.setAmount(event.getAmount() * (1.0f + bonus));
    }

    /** 玩家自身受伤时：受到伤害降低（有亡灵杀手则提档） */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtTinyDollTake(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (!player.getPersistentData().getBoolean("hasTinyDoll")) return;

        float reduction = hasSmite(player) ? TINY_DOLL_REDUCTION_SMITE : TINY_DOLL_REDUCTION_BASE;
        event.setAmount(event.getAmount() * (1.0f - reduction));
    }

    // ==================== 红染棉花 ====================

    private static final int RED_DYED_COTTON_INTERVAL = 20 * 30;   // 每 30 秒
    private static final double RED_DYED_COTTON_RADIUS = 10.0;
    private static final int RED_DYED_COTTON_BLEED_DURATION = 20 * 40; // 40 秒
    private static final int RED_DYED_COTTON_BLEED_STACK = 4;          // 每次 +4 级
    private static final int RED_DYED_COTTON_MAX_LEVEL = 30;           // 封顶 30 级

    /** 每 30 秒：对 10 格内怪物施加 40 秒流血，等级叠加（最高 30 级） */
    @SubscribeEvent
    public static void onLivingTickRedDyedCotton(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (!player.getPersistentData().getBoolean("hasRedDyedCotton")) return;

        long currentTick = player.level().getGameTime();
        long lastTrigger = player.getPersistentData().getLong("redDyedCottonLastTrigger");

        // 首次装备只记录时间
        if (lastTrigger == 0) {
            player.getPersistentData().putLong("redDyedCottonLastTrigger", currentTick);
            return;
        }
        if (currentTick - lastTrigger < RED_DYED_COTTON_INTERVAL) return;
        player.getPersistentData().putLong("redDyedCottonLastTrigger", currentTick);

        AABB area = new AABB(player.blockPosition()).inflate(RED_DYED_COTTON_RADIUS);
        for (LivingEntity target : player.level().getEntitiesOfClass(LivingEntity.class, area,
                e -> e != player && e.isAlive() && !(e instanceof Player))) {

            MobEffectInstance current = target.getEffect(Registration.BLEED.get());
            int currentLevel = (current == null) ? 0 : current.getAmplifier() + 1;
            int newLevel = Math.min(currentLevel + RED_DYED_COTTON_BLEED_STACK, RED_DYED_COTTON_MAX_LEVEL);

            // 等级封顶 30，时长累加 40 秒
            target.removeEffect(Registration.BLEED.get());
            target.addEffect(new MobEffectInstance(
                    Registration.BLEED.get(),
                    ((current == null) ? 0 : current.getDuration()) + RED_DYED_COTTON_BLEED_DURATION,
                    newLevel - 1,
                    false,
                    false
            ));
        }
    }

    /** 取目标当前流血强度（等级），无流血返回 0，封顶 30 */
    private static int getBleedLevel(LivingEntity entity) {
        MobEffectInstance bleed = entity.getEffect(Registration.BLEED.get());
        if (bleed == null) return 0;
        return Math.min(bleed.getAmplifier() + 1, RED_DYED_COTTON_MAX_LEVEL);
    }

    /** 敌方攻击玩家时：按 (流血强度/2)% 降低其造成的伤害 */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtRedDyedCottonAttacker(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;
        if (!(event.getEntity() instanceof Player)) return;

        int level = getBleedLevel(attacker);
        if (level <= 0) return;

        float reduction = (level / 2.0f) / 100.0f;  // 流血强度/2 个百分点
        reduction = Math.min(reduction, 0.99f);     // 防止负伤害
        event.setAmount(event.getAmount() * (1.0f - reduction));
    }

    /** 敌方受到伤害时：按 (流血强度/2)% 提高其受到的伤害 */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtRedDyedCottonVictim(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target instanceof Player) return;

        int level = getBleedLevel(target);
        if (level <= 0) return;

        float bonus = (level / 2.0f) / 100.0f;
        event.setAmount(event.getAmount() * (1.0f + bonus));
    }
}