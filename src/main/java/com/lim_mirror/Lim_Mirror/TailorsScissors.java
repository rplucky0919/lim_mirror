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

public class TailorsScissors extends Item implements ICurioItem {

    public TailorsScissors(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasTailorsScissors", true);

            // 检测条件是否满足，设置标记供事件使用
            ItemStack mainHand = player.getMainHandItem();
            boolean isHoldingSword = mainHand.getItem() instanceof SwordItem;

            if (isHoldingSword) {
                // 检查主手武器是否来自第4格（索引3）或第6格（索引5）
                ItemStack slot4 = player.getInventory().getItem(3);
                ItemStack slot6 = player.getInventory().getItem(5);

                boolean isFromSlot4 = !slot4.isEmpty() && slot4 == mainHand;
                boolean isFromSlot6 = !slot6.isEmpty() && slot6 == mainHand;

                player.getPersistentData().putBoolean("tailorsScissorsActive", isFromSlot4 || isFromSlot6);
            } else {
                player.getPersistentData().putBoolean("tailorsScissorsActive", false);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasTailorsScissors", false);
            player.getPersistentData().putBoolean("tailorsScissorsActive", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.tailors_scissors.1", "§7手持第4格或第6格快捷栏中的剑类武器时"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.tailors_scissors.2", "§7造成伤害 §c+3"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.tailors_scissors.3", "§7最终伤害 §c+30%"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.tailors_scissors.4", "§8- 第4格 = 索引3，第6格 = 索引5"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.tailors_scissors.5", "§8- 武器必须来自对应快捷栏格子"));
    }
}