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

public class ShadowMonster extends Item implements ICurioItem {

    public ShadowMonster(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasShadowMonster", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasShadowMonster", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7攻击时自身获得2秒震颤（目标有虚弱时改为4秒）"));
        tooltip.add(Component.literal("§7目标有虚弱时：施加（快捷栏武器数×3）级虚弱5秒（冷却30秒）"));
        tooltip.add(Component.literal("§7每有1把武器，对目标伤害+10%（快捷栏）"));
        tooltip.add(Component.literal("§8- 受熄灭的提灯加成（+1秒）"));
    }
}