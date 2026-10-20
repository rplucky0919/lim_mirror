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

public class TinyDoll extends Item implements ICurioItem {

    public TinyDoll(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasTinyDoll", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasTinyDoll", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.tiny_doll.1", "§7若敌方带有流血："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.tiny_doll.2", "§7  造成伤害 §c+10%§7，自身受到伤害 §a−20%"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.tiny_doll.3", "§7主手武器带 §c亡灵杀手§7 附魔时改为："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.tiny_doll.4", "§7  造成伤害 §c+20%§7，自身受到伤害 §a−30%"));
    }
}