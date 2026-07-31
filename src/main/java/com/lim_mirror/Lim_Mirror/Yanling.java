package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class Yanling extends Item implements ICurioItem {

    private static final AttributeModifier ARMOR_REDUCE = new AttributeModifier(
            "yanling_armor_reduce", -16.0, AttributeModifier.Operation.ADDITION
    );

    public Yanling(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasYanling", true);

            long lastTrigger = player.getPersistentData().getLong("yanlingLastTick");
            long currentTick = player.level().getGameTime();

            if (lastTrigger == 0) {
                player.getPersistentData().putLong("yanlingLastTick", currentTick);
                return;
            }

            if (currentTick - lastTrigger >= 20 * 30) {
                AABB aabb = new AABB(player.blockPosition()).inflate(30.0);
                List<LivingEntity> entities = player.level().getEntitiesOfClass(
                        LivingEntity.class, aabb,
                        e -> e != player && e.isAlive()
                );

                for (LivingEntity target : entities) {
                    MobEffectInstance burn = target.getEffect(Registration.BURN.get());
                    if (burn != null) {
                        int amplifier = burn.getAmplifier();
                        int duration = burn.getDuration();

                        int consumedDuration = duration / 2;
                        int remainingDuration = duration - consumedDuration;

                        float damage = (consumedDuration / 20.0f) * (amplifier + 1);

                        target.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_REDUCE);
                        target.getAttribute(Attributes.ARMOR).addTransientModifier(ARMOR_REDUCE);

                        target.removeEffect(Registration.BURN.get());
                        target.addEffect(new MobEffectInstance(
                                Registration.BURN.get(),
                                remainingDuration,
                                amplifier,
                                false,
                                false
                        ));

                        target.hurt(target.damageSources().onFire(), damage);
                    }
                }

                player.getPersistentData().putLong("yanlingLastTick", currentTick);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasYanling", false);
            player.getPersistentData().putLong("yanlingLastTick", 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7每30秒检测30格内烧伤生物"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7烧伤时间减半，造成消耗时间×强度的火焰伤害"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7降低目标16点护甲"));
    }
}