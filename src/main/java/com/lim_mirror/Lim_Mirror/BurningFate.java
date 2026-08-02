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

public class BurningFate extends Item implements ICurioItem {

    public BurningFate(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            boolean wasEquipped = player.getPersistentData().getBoolean("hasBurningFate");
            if (!wasEquipped) {
                player.getPersistentData().putBoolean("hasBurningFate", true);
                BurnEvents.updateMaxLevels(player);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasBurningFate", false);
            BurnEvents.updateMaxLevels(player);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7燃烧强度上限+30（可叠加）"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7造成伤害+8"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7火柴之焰攻击时触发一次烧伤伤害"));
    }
}