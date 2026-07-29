package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class GreenElytra extends Item implements ICurioItem {

    public GreenElytra(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasGreenElytra", true);

            long lastTrigger = player.getPersistentData().getLong("greenElytraLastTrigger");
            long currentTick = player.level().getGameTime();

            if (lastTrigger == 0) {
                player.getPersistentData().putLong("greenElytraLastTrigger", currentTick);
                return;
            }

            if (currentTick - lastTrigger >= 20 * 20) {
                ItemStack mainHand = player.getMainHandItem();
                int baneLevel = mainHand.getEnchantmentLevel(Enchantments.BANE_OF_ARTHROPODS);

                if (baneLevel > 0) {
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

                    player.getPersistentData().putLong("greenElytraLastTrigger", currentTick);
                }
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasGreenElytra", false);
            player.getPersistentData().putLong("greenElytraLastTrigger", 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7若武器有节肢杀手附魔"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7每20秒获得5秒呼吸法和5层呼吸法强度"));
    }
}