package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = lim_mirror.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BleedEvents {

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

        MobEffectInstance currentBleed = target.getEffect(Registration.BLEED.get());
        int newAmplifier = 0;
        int newDuration = 20;

        if (currentBleed != null) {
            newAmplifier = Math.min(currentBleed.getAmplifier() + 1, 98);
            newDuration = currentBleed.getDuration() + 20;
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

        // 4 秒 5 级流血（amplifier = 4）
        target.removeEffect(Registration.BLEED.get());
        target.addEffect(new MobEffectInstance(
                Registration.BLEED.get(),
                20 * 4,
                4,
                false,
                false
        ));
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

        // 施加强度：目标无流血 → 1 级（amplifier 0）；已有流血 → 3 级（amplifier 2）
        int amplifier = (currentBleed == null) ? 0 : 2;

        // 0 秒 = 不额外续时长：已有流血则沿用剩余时间（不足 1 秒补到 1 秒），无流血则仅瞬时 1 秒
        int duration;
        if (currentBleed == null) {
            duration = 20;
        } else {
            duration = Math.max(currentBleed.getDuration(), 20);
        }

        target.removeEffect(Registration.BLEED.get());
        target.addEffect(new MobEffectInstance(
                Registration.BLEED.get(),
                duration,
                amplifier,
                false,
                false
        ));

        // 目标原本无流血时，额外降低护甲 2 点（固定 UUID，不叠加）
        if (currentBleed == null) {
            target.getAttribute(Attributes.ARMOR).removeModifier(BloodyIronSpike.ARMOR_REDUCE);
            target.getAttribute(Attributes.ARMOR).addTransientModifier(BloodyIronSpike.ARMOR_REDUCE);
        }
    }
}