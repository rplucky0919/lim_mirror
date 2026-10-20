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

public class MiddleFingerRules extends Item implements ICurioItem {

    public MiddleFingerRules(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasMiddleFingerRules", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasMiddleFingerRules", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.middle_finger_rules.1", "§7打击伤害 §c+20%"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.middle_finger_rules.2", "§7受到攻击时：下一次造成伤害 §c+2§7（可叠加）"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.middle_finger_rules.3", "§7每 30 秒：获得 30 秒 §b抗性提升 III§7，期间造成伤害 §c+30%"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.middle_finger_rules.4", "§7每 15 秒：获得 4 秒 §c打击伤害提升 II"));
    }
}