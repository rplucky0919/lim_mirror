package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
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

public class ImperfectForesightEye extends Item implements ICurioItem {

    public ImperfectForesightEye(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasImperfectForesightEye", true);

            // 佩戴时自动给予瞬间的预知buff，保持5秒
            MobEffectInstance buff = player.getEffect(Registration.INSTANT_FORESIGHT.get());
            if (buff == null || buff.getDuration() < 20 * 3) {
                player.addEffect(new MobEffectInstance(
                        Registration.INSTANT_FORESIGHT.get(),
                        20 * 5,
                        0,
                        false,
                        false
                ));
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasImperfectForesightEye", false);
            // 卸下时移除buff
            player.removeEffect(Registration.INSTANT_FORESIGHT.get());
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7玩家造成伤害+80%（独立乘区）"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7获得§b瞬间的预知§7buff："));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7  伤害+30，最终伤害×1.2"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7  攻击时对目标+2级烧伤+2级震颤"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§710秒冷却：将所有震颤转化为震颤-灼热并引爆"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7目标有虚弱时伤害+30%（独立乘区）"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7暴击时触发震颤引爆"));
    }
}