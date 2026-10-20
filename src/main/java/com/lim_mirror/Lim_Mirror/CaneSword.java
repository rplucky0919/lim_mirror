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

public class CaneSword extends Item implements ICurioItem {

    public CaneSword(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasCaneSword", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasCaneSword", false);
            player.getPersistentData().putBoolean("caneSwordActive", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.cane_sword.1", ""));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.cane_sword.2", "§6=== 常驻效果 ==="));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.cane_sword.3", "§7手持剑类武器时："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.cane_sword.4", "§7  对敌方造成伤害 §c+20%"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.cane_sword.5", ""));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.cane_sword.6", "§6=== 右键开关 ==="));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.cane_sword.7", "§e右键切换开启/关闭"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.cane_sword.8", ""));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.cane_sword.9", "§6=== 开启后额外效果 ==="));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.cane_sword.10", "§7对所有伤害："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.cane_sword.11", "§7  自身造成伤害 §c+20%"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.cane_sword.12", "§7  造成伤害 §c+3"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.cane_sword.13", "§7  对自身受到伤害 §c+20%"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.cane_sword.14", "§8- 不限于剑类，同样适用于打击与突刺攻击"));
    }
}