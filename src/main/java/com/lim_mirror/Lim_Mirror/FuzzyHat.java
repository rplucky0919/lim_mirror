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

public class FuzzyHat extends Item implements ICurioItem {

    public FuzzyHat(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasFuzzyHat", true);

            long lastTrigger = player.getPersistentData().getLong("fuzzyHatLastTick");
            long currentTick = player.level().getGameTime();

            if (lastTrigger == 0) {
                player.getPersistentData().putLong("fuzzyHatLastTick", currentTick);
                return;
            }

            if (currentTick - lastTrigger >= 20 * 2) {
                MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                if (currentPoise != null) {
                    int currentAmplifier = currentPoise.getAmplifier();
                    int currentDuration = currentPoise.getDuration();

                    player.removeEffect(Registration.POISE.get());
                    player.addEffect(new MobEffectInstance(
                            Registration.POISE.get(),
                            currentDuration,
                            currentAmplifier + 1,
                            false,
                            false
                    ));
                }
                player.getPersistentData().putLong("fuzzyHatLastTick", currentTick);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasFuzzyHat", false);
            player.getPersistentData().putLong("fuzzyHatLastTick", 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7每2秒增加1层呼吸法强度"));
    }
}