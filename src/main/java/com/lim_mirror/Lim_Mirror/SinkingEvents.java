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

    private static final int WEAKNESS_TRIGGER_LEVEL = 14;

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

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null) return;
        if (entity.level().isClientSide()) return;

        MobEffectInstance sinking = entity.getEffect(Registration.SINKING.get());
        if (sinking == null) {
            entity.removeEffect(MobEffects.WEAKNESS);
            return;
        }

        int level = sinking.getAmplifier() + 1;

        if (level >= WEAKNESS_TRIGGER_LEVEL) {
            entity.addEffect(new MobEffectInstance(
                    MobEffects.WEAKNESS,
                    20 * 2,
                    0,
                    false,
                    false
            ));
        } else {
            entity.removeEffect(MobEffects.WEAKNESS);
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target.level().isClientSide()) return;

        if (event.getSource() != null && event.getSource().getMsgId() != null &&
                event.getSource().getMsgId().equals("sinking")) {
            return;
        }

        MobEffectInstance sinking = target.getEffect(Registration.SINKING.get());
        if (sinking == null) return;

        int level = sinking.getAmplifier() + 1;
        float extraDamage = level;

        event.setAmount(event.getAmount() + extraDamage);
    }
}