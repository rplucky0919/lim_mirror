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

public class RevengeLedger extends Item implements ICurioItem {

    public RevengeLedger(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasRevengeLedger", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasRevengeLedger", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.revenge_ledger.1", "§7空手或携带打击武器时："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.revenge_ledger.2", "§7造成伤害 §c+9"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.revenge_ledger.3", "§7获得 60 秒 §c打击伤害强化§7（可叠加，30 秒冷却）"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.revenge_ledger.4", "§7打击伤害强化：每层最终伤害 §c+50%"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.revenge_ledger.5", "§7被攻击后：下一击伤害 §c+20%§7，获得 §e生命上限 7.5% §7护盾（黄心）"));
    }
}
