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

public class BloodFlameBlade extends Item implements ICurioItem {

    // 充能间隔：30秒 = 600 ticks
    private static final int CHARGE_INTERVAL = 20 * 30;

    public BloodFlameBlade(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasBloodFlameBlade", true);

            // 充能逻辑
            long lastCharge = player.getPersistentData().getLong("bloodFlameBladeLastCharge");
            long currentTick = player.level().getGameTime();

            // 首次佩戴：初始化充能时间
            if (lastCharge == 0) {
                player.getPersistentData().putLong("bloodFlameBladeLastCharge", currentTick);
                return;
            }

            // 每30秒充能一次
            if (currentTick - lastCharge >= CHARGE_INTERVAL) {
                // 如果还没有充能，则充能
                if (!player.getPersistentData().getBoolean("bloodFlameBladeCharged")) {
                    player.getPersistentData().putBoolean("bloodFlameBladeCharged", true);
                    player.getPersistentData().putLong("bloodFlameBladeLastCharge", currentTick);
                    // 提示玩家
                    player.displayClientMessage(Component.literal("§6[血炎刀] §a充能完成！下一次火柴之焰攻击额外+5层烧伤"), true);
                }
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasBloodFlameBlade", false);
            player.getPersistentData().putBoolean("bloodFlameBladeCharged", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7若武器带有火柴之焰附魔"));
        tooltip.add(Component.literal("§7每30s充能一次，下一次攻击烧伤等级+5"));
        tooltip.add(Component.literal("§8（你似乎无法发挥他的全部力量）"));
    }
}