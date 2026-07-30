package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = lim_mirror.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RuptureEvents {

    @SubscribeEvent
    public static void onAttack(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            ItemStack mainHand = player.getMainHandItem();
            int shadowLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.SHADOW_BEAST.get(), mainHand);

            if (shadowLevel > 0) {
                MobEffectInstance currentRupture = target.getEffect(Registration.RUPTURE.get());
                int newAmplifier = 0;
                int currentDuration = 0;
                if (currentRupture != null) {
                    newAmplifier = currentRupture.getAmplifier() + 1;
                    currentDuration = currentRupture.getDuration();
                }
                int totalDuration = currentDuration + 20;

                target.removeEffect(Registration.RUPTURE.get());
                target.addEffect(new MobEffectInstance(
                        Registration.RUPTURE.get(),
                        totalDuration,
                        newAmplifier,
                        false,
                        false
                ));
            }
        }
    }
}