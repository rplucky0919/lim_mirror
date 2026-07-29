package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class Reminiscence extends Item implements ICurioItem {

    public Reminiscence(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasReminiscence", true);

            long lastTrigger = player.getPersistentData().getLong("reminiscenceLastTick");
            long currentTick = player.level().getGameTime();

            if (lastTrigger == 0) {
                player.getPersistentData().putLong("reminiscenceLastTick", currentTick);
                return;
            }

            if (currentTick - lastTrigger >= 20 * 5) {
                ItemStack mainHand = player.getMainHandItem();
                int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BREATH_SMOOTH.get(), mainHand);

                if (enchantLevel > 0) {
                    MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());

                    int newAmplifier;
                    if (currentPoise != null) {
                        newAmplifier = currentPoise.getAmplifier() + 3;
                    } else {
                        newAmplifier = 0;
                    }

                    int currentDuration = 0;
                    if (currentPoise != null) {
                        currentDuration = currentPoise.getDuration();
                    }
                    int totalDuration = currentDuration + 20 * 2;

                    player.removeEffect(Registration.POISE.get());
                    player.addEffect(new MobEffectInstance(
                            Registration.POISE.get(),
                            totalDuration,
                            newAmplifier,
                            false,
                            false
                    ));
                }
                player.getPersistentData().putLong("reminiscenceLastTick", currentTick);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasReminiscence", false);
            player.getPersistentData().putLong("reminiscenceLastTick", 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7手持呼吸顺畅附魔武器时伤害+3"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7每5秒增加2秒呼吸法和3级呼吸法强度"));
    }
}