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

public class PocketWatchTypeP extends Item implements ICurioItem {

    public PocketWatchTypeP(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasPocketWatchTypeP", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasPocketWatchTypeP", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.pocket_watch_type_p.1", "§7武器带震颤爆发时：伤害+6，最终伤害×1.25"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.pocket_watch_type_p.2", "§7自身有缓慢II+时：伤害+6，最终伤害×1.5"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.pocket_watch_type_p.3", "§7自身造成伤害+5"));
    }
}