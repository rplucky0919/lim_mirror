package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = lim_mirror.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {

    @SubscribeEvent
    public static void onAttack(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            MobEffectInstance currentEffect = player.getEffect(Registration.POISE.get());

            if (currentEffect != null) {
                int currentDuration = currentEffect.getDuration();
                int amplifier = currentEffect.getAmplifier();

                if (currentDuration > 20) {
                    player.removeEffect(Registration.POISE.get());
                    player.addEffect(new MobEffectInstance(Registration.POISE.get(), currentDuration - 20, amplifier, false, false));
                } else {
                    player.removeEffect(Registration.POISE.get());
                }

                int level = amplifier + 1;
                float critChance = Math.min(level * 0.05f, 1.0f);
                if (player.getRandom().nextFloat() < critChance) {
                    event.setAmount(event.getAmount() * 1.5f);
                }
            }

            if (player.getPersistentData().getBoolean("hasPipeBonus")) {
                event.setAmount(event.getAmount() + 2.0f);
            }

            // ===== 呼吸顺畅附魔效果 =====
            ItemStack mainHand = player.getMainHandItem();
            int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BREATH_SMOOTH.get(), mainHand);

            if (enchantLevel > 0) {
                MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());

                int newAmplifier;
                if (currentPoise != null) {
                    newAmplifier = currentPoise.getAmplifier() + 1;
                } else {
                    newAmplifier = 0;
                }

                player.removeEffect(Registration.POISE.get());
                player.addEffect(new MobEffectInstance(
                        Registration.POISE.get(),
                        20 * 5,
                        Math.min(newAmplifier, 4),
                        false,
                        false
                ));
            }
        }
    }
}