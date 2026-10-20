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

public class Haunting extends Item implements ICurioItem {

    public Haunting(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasHaunting", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasHaunting", false);
            player.getPersistentData().putBoolean("hauntingPending", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.haunting.1", "§7造成打击伤害后："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.haunting.2", "§730 秒后获得 §b4 秒迅捷 II §7+ §c4 秒打击威力提升 II"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.haunting.3", "§7- 等级和时间可叠加"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.haunting.4", ""));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.haunting.5", "§730 秒内没有攻击敌人："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.haunting.6", "§7获得 §b6 秒迅捷 §7+ §c4 秒打击威力提升 II"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.haunting.7", "§7- 随后进入 §c30 秒 §7冷却"));
    }
}