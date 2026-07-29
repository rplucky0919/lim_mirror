package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = lim_mirror.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SinkingEvents {

    private static final int WEAKNESS_TRIGGER_LEVEL = 14;

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