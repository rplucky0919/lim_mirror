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

public class SeveredMemory extends Item implements ICurioItem {

    public SeveredMemory(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasSeveredMemory", true);

            float maxHealth = player.getMaxHealth();
            float currentHealth = player.getHealth();
            float lostPercent = ((maxHealth - currentHealth) / maxHealth) * 100;

            int bonus = Math.min((int)(lostPercent / 5) * 5, 20);
            player.getPersistentData().putInt("severedMemoryBonus", bonus);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasSeveredMemory", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7手持剑类武器时"));
        tooltip.add(Component.literal("§7造成伤害 §c+9"));
        tooltip.add(Component.literal("§7造成伤害 §c+30%"));
        tooltip.add(Component.literal("§7每失去 §c5% §7体力，造成伤害 §c+5% §7（最多 +20%）"));
    }
}