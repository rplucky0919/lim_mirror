package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = lim_mirror.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BleedEvents {

    private static final int MAX_LEVELS = 99;
    private static final int INTERVAL_TICKS = 20;
    private static final float DAMAGE_PER_LEVEL = 1.0f;

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;

        ItemStack weapon = attacker.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.SANCHO_BLOOD.get(), weapon);
        if (enchantLevel <= 0) return;

        MobEffectInstance currentBleed = target.getEffect(Registration.BLEED.get());
        int currentAmplifier = 0;
        int currentDuration = 0;
        if (currentBleed != null) {
            currentAmplifier = currentBleed.getAmplifier() + 1;
            currentDuration = currentBleed.getDuration();
        }
        int newAmplifier = Math.min(currentAmplifier, MAX_LEVELS - 1);
        int totalDuration = currentDuration + 20;

        target.removeEffect(Registration.BLEED.get());
        target.addEffect(new MobEffectInstance(
                Registration.BLEED.get(),
                totalDuration,
                newAmplifier,
                false,
                false
        ));
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target.level().isClientSide()) return;

        MobEffectInstance bleed = target.getEffect(Registration.BLEED.get());
        if (bleed == null) return;

        long currentTick = target.level().getGameTime();
        if (currentTick % INTERVAL_TICKS != 0) return;

        int level = Math.min(bleed.getAmplifier() + 1, MAX_LEVELS);
        float damage = level * DAMAGE_PER_LEVEL;

        if (target.getAbsorptionAmount() > 0) {
            float absorption = target.getAbsorptionAmount();
            if (absorption >= damage) {
                target.setAbsorptionAmount(absorption - damage);
                return;
            } else {
                target.setAbsorptionAmount(0);
                float remaining = damage - absorption;
                target.hurt(target.damageSources().genericKill(), remaining);
                return;
            }
        }

        target.hurt(target.damageSources().genericKill(), damage);
    }

    @SubscribeEvent
    public static void onEntityUseItem(LivingEntityUseItemEvent event) {
        LivingEntity living = event.getEntity();
        if (living == null) return;
        if (event.getItem().getItem() == Items.MILK_BUCKET) {
            living.removeEffect(Registration.BLEED.get());
        }
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        MobEffectInstance effect = event.getEffectInstance();
        if (effect == null) return;
        if (!effect.getEffect().equals(Registration.BLEED.get())) return;
    }
}