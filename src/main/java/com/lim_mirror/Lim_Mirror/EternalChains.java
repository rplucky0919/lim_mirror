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

public class EternalChains extends Item implements ICurioItem {

    public EternalChains(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasEternalChains", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasEternalChains", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.eternal_chains.1", "§7常驻：打击伤害增加 §c30%"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.eternal_chains.2", "§7每受一次伤害：额外增加 §c+10%§7 打击伤害，持续 §c30s§7（可叠加至 §c3层§7，最多共享同一计数器）"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.eternal_chains.3", "§7每30秒：获得 §c30s 迅捷 I§7（可叠加）"));
    }
}