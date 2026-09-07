package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SlashingPowerEffect extends MobEffect {

    public SlashingPowerEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF4444);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}