package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SinkingEffect extends MobEffect {

    public SinkingEffect() {
        super(MobEffectCategory.HARMFUL, 0x2B3B4C);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}