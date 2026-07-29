package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class GasLamp extends Item implements ICurioItem {

    public GasLamp(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasGasLamp", true);

            long lastTrigger = player.getPersistentData().getLong("gasLampLastTick");
            long currentTick = player.level().getGameTime();

            if (lastTrigger == 0) {
                player.getPersistentData().putLong("gasLampLastTick", currentTick);
                return;
            }

            if (currentTick - lastTrigger >= 20 * 30) {
                MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());

                int newAmplifier;
                if (currentPoise != null) {
                    newAmplifier = currentPoise.getAmplifier() + 5;
                } else {
                    newAmplifier = 0;
                }

                int currentDuration = 0;
                if (currentPoise != null) {
                    currentDuration = currentPoise.getDuration();
                }
                int totalDuration = currentDuration + 20 * 5;

                player.removeEffect(Registration.POISE.get());
                player.addEffect(new MobEffectInstance(
                        Registration.POISE.get(),
                        totalDuration,
                        newAmplifier,
                        false,
                        false
                ));

                player.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SPEED,
                        20 * 30,
                        1,
                        false,
                        false
                ));

                player.addEffect(new MobEffectInstance(
                        MobEffects.DIG_SPEED,
                        20 * 30,
                        1,
                        false,
                        false
                ));

                player.getPersistentData().putLong("gasLampLastTick", currentTick);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasGasLamp", false);
            player.getPersistentData().putLong("gasLampLastTick", 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7每30秒获得5秒呼吸法+5级强度"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7迅捷II+急迫II（持续30秒）"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7造成伤害×1.39"));
    }
}