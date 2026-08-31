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
public class ChargeEvents {

    private static final int MAX_CHARGE_LEVEL = 99;
    private static final int MAX_CHARGE_DURATION = 20 * 60; // 60秒

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.SPACE_TEAR.get(), weapon);
        if (enchantLevel <= 0) return;

        MobEffectInstance currentCharge = player.getEffect(Registration.CHARGE.get());

        int newAmplifier;
        int newDuration;

        if (currentCharge != null) {
            newAmplifier = Math.min(currentCharge.getAmplifier() + 1, MAX_CHARGE_LEVEL - 1);
            newDuration = Math.min(currentCharge.getDuration() + 20, MAX_CHARGE_DURATION);
        } else {
            newAmplifier = 0;
            newDuration = 20;
        }

        player.removeEffect(Registration.CHARGE.get());
        player.addEffect(new MobEffectInstance(
                Registration.CHARGE.get(),
                newDuration,
                newAmplifier,
                false,
                false
        ));
    }
}