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

public class PocketWatchTypeL extends Item implements ICurioItem {

    public PocketWatchTypeL(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasPocketWatchTypeL", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasPocketWatchTypeL", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.pocket_watch_type_l.1", "§7武器带震颤爆发时：伤害+8，最终伤害×1.25"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.pocket_watch_type_l.2", "§7震颤转化为特殊震颤时：时间与层数×1.5"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.pocket_watch_type_l.3", "§7敌方受到伤害+50%"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.pocket_watch_type_l.4", "§7每30秒对攻击目标+6级震颤强度"));
    }
}