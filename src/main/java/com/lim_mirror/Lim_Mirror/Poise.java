package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class Poise extends MobEffect {
    public Poise() {
        // BENEFICIAL 表示增益效果（蓝色图标）
        // 0x36ebab 是淡青色
        super(MobEffectCategory.BENEFICIAL, 0x36ebab);
    }

    // 返回 false，表示不需要每秒钟自动触发效果
    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return false;
    }
}