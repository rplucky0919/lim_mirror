package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingUseTotemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = lim_mirror.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TotemEvents {

    @SubscribeEvent
    public static void onUseTotem(LivingUseTotemEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (!player.getPersistentData().getBoolean("hasBlindingRedCloth")) return;

        long currentTick = player.level().getGameTime();
        long lastTotemTrigger = player.getPersistentData().getLong("blindingRedClothTotemLastTrigger");

        // 冷却检查：30 秒
        if (currentTick - lastTotemTrigger < 20 * 30) return;

        // 增加 30 秒斩击伤害提升 II
        MobEffectInstance existing = player.getEffect(Registration.SLASHING_POWER.get());
        int newDuration = 20 * 30;
        int newAmplifier = 1;

        if (existing != null) {
            newDuration = existing.getDuration() + 20 * 30;
            newAmplifier = existing.getAmplifier() + 1;
        }

        player.removeEffect(Registration.SLASHING_POWER.get());
        player.addEffect(new MobEffectInstance(
                Registration.SLASHING_POWER.get(),
                newDuration,
                newAmplifier,
                false,
                false
        ));

        // 造成伤害 +20%（持续 30 秒）
        player.getPersistentData().putLong("blindingRedClothTotemEnd", currentTick + 20 * 30);

        // 记录冷却
        player.getPersistentData().putLong("blindingRedClothTotemLastTrigger", currentTick);

        player.displayClientMessage(
                Component.literal("§d[蒙蔽心扉的红布] §c不死图腾触发！斩击伤害提升 II，造成伤害 +20%"),
                true
        );
    }
}