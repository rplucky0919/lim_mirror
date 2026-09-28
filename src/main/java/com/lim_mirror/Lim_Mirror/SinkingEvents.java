package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = lim_mirror.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SinkingEvents {

    private static final int WEAKNESS_TRIGGER_LEVEL = 45;

    // ==================== 沉沦之触附魔：攻击时施加沉沦 ====================

    @SubscribeEvent
    public static void onAttack(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            ItemStack mainHand = player.getMainHandItem();
            int sinkingEnchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.SINKING_TOUCH.get(), mainHand);

            if (sinkingEnchantLevel > 0) {
                MobEffectInstance currentSinking = target.getEffect(Registration.SINKING.get());
                int newAmplifier = 0;
                int currentDuration = 0;
                if (currentSinking != null) {
                    newAmplifier = currentSinking.getAmplifier() + 1;
                    currentDuration = currentSinking.getDuration();
                }
                int totalDuration = currentDuration + 20;

                target.removeEffect(Registration.SINKING.get());
                target.addEffect(new MobEffectInstance(
                        Registration.SINKING.get(),
                        totalDuration,
                        newAmplifier,
                        false,
                        false
                ));
            }
        }
    }

    // ==================== 沉沦：层数 ≥ 45 触发虚弱 I ====================

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null) return;
        if (entity.level().isClientSide()) return;

        MobEffectInstance sinking = entity.getEffect(Registration.SINKING.get());
        if (sinking == null) return;

        int level = sinking.getAmplifier() + 1;

        if (level >= WEAKNESS_TRIGGER_LEVEL) {
            MobEffectInstance existingWeakness = entity.getEffect(MobEffects.WEAKNESS);
            if (existingWeakness == null || existingWeakness.getDuration() < 40) {
                entity.addEffect(new MobEffectInstance(
                        MobEffects.WEAKNESS,
                        20 * 5,
                        0,
                        false,
                        false
                ));
            }
        }
    }

    // ==================== 沉沦：被攻击时额外溺水伤害 ====================

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target.level().isClientSide()) return;

        MobEffectInstance sinking = target.getEffect(Registration.SINKING.get());
        if (sinking == null) return;

        int level = sinking.getAmplifier() + 1;
        if (level > 0) {
            // 额外伤害 = 层数 × 1（溺水伤害）
            target.hurt(target.damageSources().drown(), level);
        }
    }
}