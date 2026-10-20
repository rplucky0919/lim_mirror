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

public class CloudPatternedCraneNeckVase extends Item implements ICurioItem {

    public CloudPatternedCraneNeckVase(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasCloudPatternedCraneNeckVase", true);

            long lastReset = player.getPersistentData().getLong("craneNeckVaseLastReset");
            long currentTick = player.level().getGameTime();
            if (currentTick - lastReset >= 20 * 30) {
                player.getPersistentData().putInt("craneNeckVaseTriggerCount", 0);
                player.getPersistentData().putLong("craneNeckVaseLastReset", currentTick);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasCloudPatternedCraneNeckVase", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.cloud_patterned_crane_neck_vase.1", "§7手持剑类武器时："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.cloud_patterned_crane_neck_vase.2", "§7  造成伤害 §c+3"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.cloud_patterned_crane_neck_vase.3", "§7  最终造成伤害 §c+10%"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.cloud_patterned_crane_neck_vase.4", "§7  使目标造成伤害 §c-20%（持续 5 秒）"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.cloud_patterned_crane_neck_vase.5", "§7武器带有 §d桑丘之血 §7附魔时："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.cloud_patterned_crane_neck_vase.6", "§7  攻击获得 §b1~2 级 §7呼吸法强度（30 秒内最多 8 次）"));
    }
}