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

public class MomentOfJudgment extends Item implements ICurioItem {

    public MomentOfJudgment(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasMomentOfJudgment", true);

            ItemStack mainHand = player.getMainHandItem();
            boolean isHoldingSword = mainHand.getItem() instanceof SwordItem;

            if (isHoldingSword) {
                boolean isFromValidSlot = false;
                for (int i = 2; i <= 5; i++) {
                    ItemStack slot = player.getInventory().getItem(i);
                    if (!slot.isEmpty() && slot == mainHand) {
                        isFromValidSlot = true;
                        break;
                    }
                }

                int swordCount = 0;
                for (int i = 0; i < 9; i++) {
                    ItemStack item = player.getInventory().getItem(i);
                    if (item.getItem() instanceof SwordItem) {
                        swordCount++;
                    }
                }

                player.getPersistentData().putBoolean("momentOfJudgmentActive", isFromValidSlot);
                player.getPersistentData().putInt("momentOfJudgmentSwordCount", swordCount);
            } else {
                player.getPersistentData().putBoolean("momentOfJudgmentActive", false);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasMomentOfJudgment", false);
            player.getPersistentData().putBoolean("momentOfJudgmentActive", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7手持第3/4/5/6格快捷栏中的剑类武器时"));
        tooltip.add(Component.literal("§7造成伤害 §c+12.5%"));
        tooltip.add(Component.literal("§7斩击伤害提升 §c(50 / 快捷栏剑数)%"));
        tooltip.add(Component.literal("§8- 剑数越多，单把加成越低"));
    }
}