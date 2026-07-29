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

public class HappyPlushie extends Item implements ICurioItem {

    public HappyPlushie(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasHappyPlushie", true);

            MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());

            if (currentPoise != null) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.DAMAGE_RESISTANCE,
                        20 * 2,
                        1,
                        false,
                        false
                ));

                long lastTrigger = player.getPersistentData().getLong("happyPlushieLastTick");
                long currentTick = player.level().getGameTime();

                if (lastTrigger == 0) {
                    player.getPersistentData().putLong("happyPlushieLastTick", currentTick);
                    return;
                }

                if (currentTick - lastTrigger >= 20 * 2) {
                    int newAmplifier = currentPoise.getAmplifier() + 2;
                    int currentDuration = currentPoise.getDuration();

                    player.removeEffect(Registration.POISE.get());
                    player.addEffect(new MobEffectInstance(
                            Registration.POISE.get(),
                            currentDuration,
                            newAmplifier,
                            false,
                            false
                    ));

                    player.getPersistentData().putLong("happyPlushieLastTick", currentTick);
                }
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasHappyPlushie", false);
            player.getPersistentData().putLong("happyPlushieLastTick", 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7每2秒增加2层呼吸法强度（时间不变）"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7有呼吸法时获得抗性提升II"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7造成伤害×1.45"));
    }
}