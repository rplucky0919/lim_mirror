package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
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

public class Horseshoe extends Item implements ICurioItem {

    public Horseshoe(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            long lastCheck = player.getPersistentData().getLong("horseshoeLastCheck");
            long currentTick = player.level().getGameTime();

            if (lastCheck == 0) {
                player.getPersistentData().putLong("horseshoeLastCheck", currentTick);
                return;
            }

            if (currentTick - lastCheck >= 20 * 20) {
                MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());

                int newAmplifier;
                if (currentPoise != null) {
                    newAmplifier = currentPoise.getAmplifier() + 2;
                } else {
                    newAmplifier = 3;
                }

                int currentDuration = 0;
                if (currentPoise != null) {
                    currentDuration = currentPoise.getDuration();
                }

                int addDuration = 20 * 5;
                int totalDuration = currentDuration + addDuration;

                player.removeEffect(Registration.POISE.get());
                player.addEffect(new MobEffectInstance(
                        Registration.POISE.get(),
                        totalDuration,
                        newAmplifier,
                        false,
                        false
                ));

                player.getPersistentData().putLong("horseshoeLastCheck", currentTick);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putLong("horseshoeLastCheck", 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7每20秒检测一次"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7无呼吸法时获得5秒呼吸法和3层强度"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7有呼吸法时增加2层强度"));
    }
}