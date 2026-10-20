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

public class EightDirectionBell extends Item implements ICurioItem {

    public EightDirectionBell(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasEightDirectionBell", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasEightDirectionBell", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.eight_direction_bell.1", "§7对敌方造成伤害时，增加6秒震颤，增加3级震颤强度"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.eight_direction_bell.2", "§7自身增加16秒震颤"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.eight_direction_bell.3", "§7自身造成伤害+（1+（震颤时间(秒)×0.4））×3（上限240点）"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.eight_direction_bell.4", "§7若自身带有震颤并击杀生物，自身增加6秒震颤"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.eight_direction_bell.5", "§7自身造成伤害增加（震颤时间(秒)+震颤强度+5）%（上限50%）"));
    }
}