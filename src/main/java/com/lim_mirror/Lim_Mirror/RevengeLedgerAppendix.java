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

public class RevengeLedgerAppendix extends Item implements ICurioItem {

    public RevengeLedgerAppendix(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasRevengeLedgerAppendix", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasRevengeLedgerAppendix", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.revenge_ledger_appendix.1", "§7空手或携带打击武器时："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.revenge_ledger_appendix.2", "§7造成伤害 §c+36"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.revenge_ledger_appendix.3", "§7最终打击伤害 §c+90%"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.revenge_ledger_appendix.4", "§7恢复造成打击伤害 §a10% §7的体力"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.revenge_ledger_appendix.5", "§7每 30 秒获得 §e+90 §7黄心"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.revenge_ledger_appendix.6", "§7被攻击后 30 秒：恢复量变为 §a20%§7，溢出体力转为黄心"));
    }
}
