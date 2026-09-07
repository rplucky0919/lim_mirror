package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class WornWhetstone extends Item implements ICurioItem {

    public WornWhetstone(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasWornWhetstone", true);

            ItemStack mainHand = player.getMainHandItem();
            boolean isHoldingSword = mainHand.getItem() instanceof SwordItem;

            if (isHoldingSword) {
                // 检查主手武器是否来自第3格（索引2）
                ItemStack slot3 = player.getInventory().getItem(2);
                boolean isFromSlot3 = !slot3.isEmpty() && slot3 == mainHand;

                player.getPersistentData().putBoolean("wornWhetstoneActive", isFromSlot3);
            } else {
                player.getPersistentData().putBoolean("wornWhetstoneActive", false);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasWornWhetstone", false);
            player.getPersistentData().putBoolean("wornWhetstoneActive", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7当剑类武器在第3格快捷栏且被手持时"));
        tooltip.add(Component.literal("§7造成伤害 §c+3"));
        tooltip.add(Component.literal("§7造成的伤害 §c+25%"));
    }
}