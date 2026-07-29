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

public class CommandSanctuary extends Item implements ICurioItem {

    public CommandSanctuary(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasCommandSanctuary", true);

            long lastTrigger = player.getPersistentData().getLong("commandSanctuaryLastTick");
            long currentTick = player.level().getGameTime();

            if (lastTrigger == 0) {
                player.getPersistentData().putLong("commandSanctuaryLastTick", currentTick);
                return;
            }

            if (currentTick - lastTrigger >= 20 * 30) {
                ItemStack mainHand = player.getMainHandItem();
                int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.SINKING_TOUCH.get(), mainHand);

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

                    player.getPersistentData().putLong("commandSanctuaryDamageBoost", 20 * 10);
                }

                player.getPersistentData().putLong("commandSanctuaryLastTick", currentTick);
            }

            long remainingBoost = player.getPersistentData().getLong("commandSanctuaryDamageBoost");
            if (remainingBoost > 0) {
                player.getPersistentData().putLong("commandSanctuaryDamageBoost", remainingBoost - 1);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasCommandSanctuary", false);
            player.getPersistentData().putLong("commandSanctuaryLastTick", 0);
            player.getPersistentData().putLong("commandSanctuaryDamageBoost", 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7每30秒检测一次"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7武器有沉沦之触附魔时：+2秒呼吸法，+3级强度"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7触发后10秒内伤害×1.3"));
    }
}