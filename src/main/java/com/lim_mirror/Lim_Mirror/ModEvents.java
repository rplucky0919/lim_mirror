package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = lim_mirror.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {

    @SubscribeEvent
    public static void onAttack(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            if (player.hasEffect(Registration.POISE.get())) {
                var effectInstance = player.getEffect(Registration.POISE.get());
                if (effectInstance != null) {
                    int level = effectInstance.getAmplifier() + 1;
                    float critChance = Math.min(level * 0.05f, 1.0f);
                    if (player.getRandom().nextFloat() < critChance) {
                        // 这里只修改伤害数值，不尝试调用 setCritical
                        event.setAmount(event.getAmount() * 1.5f);
                    }
                }
            }
        }
    }
}