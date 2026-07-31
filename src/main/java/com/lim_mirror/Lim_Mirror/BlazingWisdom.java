package com.lim_mirror.Lim_Mirror;

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

public class BlazingWisdom extends Item implements ICurioItem {

    public BlazingWisdom(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasBlazingWisdom", true);

            long remainingBoost = player.getPersistentData().getLong("blazingWisdomBoost");
            if (remainingBoost > 0) {
                player.getPersistentData().putLong("blazingWisdomBoost", remainingBoost - 1);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasBlazingWisdom", false);
            player.getPersistentData().putLong("blazingWisdomBoost", 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7通过烧伤击杀生物时"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7玩家在30秒内造成伤害提升20%"));
    }
}