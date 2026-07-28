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

public class LuckyBag extends Item implements ICurioItem {

    public LuckyBag(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasLuckyBag", true);

            long lastTick = player.getPersistentData().getLong("luckyBagLastTick");
            long currentTick = player.level().getGameTime();

            if (currentTick - lastTick >= 20 * 3) {
                MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());

                int currentAmplifier = 0;
                int currentDuration = 0;
                if (currentPoise != null) {
                    currentAmplifier = currentPoise.getAmplifier();
                    currentDuration = currentPoise.getDuration();
                }
                int totalDuration = currentDuration + 20 * 2;

                player.removeEffect(Registration.POISE.get());
                player.addEffect(new MobEffectInstance(
                        Registration.POISE.get(),
                        totalDuration,
                        currentAmplifier,
                        false,
                        false
                ));

                player.getPersistentData().putLong("luckyBagLastTick", currentTick);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasLuckyBag", false);
            player.getPersistentData().putBoolean("luckyBagNextAttackBoost", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7暴击时增加7秒呼吸法和7级强度"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7有呼吸法时基础伤害+9，获得力量12级"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7每3秒增加2秒呼吸法时间"));
    }
}