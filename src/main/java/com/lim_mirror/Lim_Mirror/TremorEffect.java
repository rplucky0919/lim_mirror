package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class TremorEffect extends MobEffect {

    public TremorEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B8B8B); // 灰色
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // 无特殊效果，仅作为状态标记
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}