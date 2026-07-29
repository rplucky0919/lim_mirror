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

public class AngelsPrivilege extends Item implements ICurioItem {

    public AngelsPrivilege(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasAngelsPrivilege", true);

            long lastTrigger = player.getPersistentData().getLong("angelsPrivilegeLastTick");
            long currentTick = player.level().getGameTime();

            if (lastTrigger == 0) {
                player.getPersistentData().putLong("angelsPrivilegeLastTick", currentTick);
                return;
            }

            if (currentTick - lastTrigger >= 20 * 5) {
                MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                if (currentPoise != null) {
                    int amplifier = currentPoise.getAmplifier();
                    if (amplifier >= 20) {
                        int newAmplifier = amplifier - 1;
                        int currentDuration = currentPoise.getDuration();
                        int totalDuration = currentDuration + 20;

                        player.removeEffect(Registration.POISE.get());
                        player.addEffect(new MobEffectInstance(
                                Registration.POISE.get(),
                                totalDuration,
                                newAmplifier,
                                false,
                                false
                        ));
                    }
                }
                player.getPersistentData().putLong("angelsPrivilegeLastTick", currentTick);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasAngelsPrivilege", false);
            player.getPersistentData().putLong("angelsPrivilegeLastTick", 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7呼吸法等级大于20时"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7每5秒减少1级呼吸法等级"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7增加1秒呼吸法时间"));
    }
}