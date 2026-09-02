package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class PrecisionTimeAccelerationEffect extends MobEffect {

    public PrecisionTimeAccelerationEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x00FFFF); // 青色
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // 仅作为标记效果，无 tick 逻辑
        // 伤害加成由事件处理
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }

    /**
     * 重写此方法使其不能被牛奶清除
     * 返回 false 表示该效果不可被治愈（牛奶）
     */
    @Override
    public boolean isBeneficial() {
        return true;
    }
}