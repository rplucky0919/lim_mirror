package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class InstantForesightEffect extends MobEffect {

    public InstantForesightEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xE0FFFF);
    }

    @Override
    public void applyEffectTick(net.minecraft.world.entity.LivingEntity entity, int amplifier) {
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }

    @Override
    public boolean isInstantenous() {
        return false;
    }
}