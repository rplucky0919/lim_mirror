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

public class WhiteCotton extends Item implements ICurioItem {

    public WhiteCotton(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasWhiteCotton", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasWhiteCotton", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.white_cotton.1", "§7解除敌方的 §7虚弱 §7效果"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.white_cotton.2", "§7每 30 秒：对自身 5 格范围内的生物"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.white_cotton.3", "§7  施加 §c1 秒 12 级流血"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.white_cotton.4", "§7  这些生物 30 秒后："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.white_cotton.5", "§7    对自身造成伤害 §a−6§7，受到伤害 §c+6"));
    }
}