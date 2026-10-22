package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
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

public class RedDyedCotton extends Item implements ICurioItem {

    public RedDyedCotton(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasRedDyedCotton", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasRedDyedCotton", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.red_dyed_cotton.1", "§7每 30 秒：对自身 10 格内的怪物"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.red_dyed_cotton.2", "§7  施加 §c40 秒 4 级流血§7（可叠加，最高 §c30 级§7）"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.red_dyed_cotton.3", "§7敌方造成伤害减少 §c(流血强度/2)%"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.red_dyed_cotton.4", "§7敌方受到伤害增加 §c(流血强度/2)%"));
    }
}