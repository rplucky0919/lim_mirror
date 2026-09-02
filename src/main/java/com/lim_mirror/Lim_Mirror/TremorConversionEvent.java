package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class TremorConversionEvent extends LivingEvent {

    private final Player player;
    private final ConversionSource source;
    private final MobEffect sourceEffect;
    private final MobEffectInstance sourceInstance;
    private MobEffect targetEffect;
    private int targetAmplifier;
    private int targetDuration;
    private float conversionMultiplier;

    public enum ConversionSource {
        CLAP_CLAP,
        IMPERFECT_EYE,
        UNKNOWN
    }

    public TremorConversionEvent(LivingEntity target, Player player, ConversionSource source,
                                 MobEffect sourceEffect, MobEffectInstance sourceInstance,
                                 MobEffect targetEffect, int targetAmplifier, int targetDuration,
                                 float conversionMultiplier) {
        super(target);
        this.player = player;
        this.source = source;
        this.sourceEffect = sourceEffect;
        this.sourceInstance = sourceInstance;
        this.targetEffect = targetEffect;
        this.targetAmplifier = Math.max(0, targetAmplifier);
        this.targetDuration = Math.max(0, targetDuration);
        this.conversionMultiplier = Math.max(0, conversionMultiplier);
    }

    public Player getPlayer() {
        return player;
    }

    public ConversionSource getSource() {
        return source;
    }

    public MobEffect getSourceEffect() {
        return sourceEffect;
    }

    public MobEffectInstance getSourceInstance() {
        return sourceInstance;
    }

    public int getSourceAmplifier() {
        return sourceInstance != null ? Math.max(0, sourceInstance.getAmplifier()) : 0;
    }

    public int getSourceDuration() {
        return sourceInstance != null ? sourceInstance.getDuration() : 0;
    }

    public MobEffect getTargetEffect() {
        return targetEffect;
    }

    public void setTargetEffect(MobEffect targetEffect) {
        this.targetEffect = targetEffect;
    }

    public int getTargetAmplifier() {
        return targetAmplifier;
    }

    public void setTargetAmplifier(int targetAmplifier) {
        this.targetAmplifier = Math.max(0, targetAmplifier);
    }

    public int getTargetDuration() {
        return targetDuration;
    }

    public void setTargetDuration(int targetDuration) {
        this.targetDuration = Math.max(0, targetDuration);
    }

    public float getConversionMultiplier() {
        return conversionMultiplier;
    }

    public void setConversionMultiplier(float conversionMultiplier) {
        this.conversionMultiplier = Math.max(0, conversionMultiplier);
    }

    /**
     * 应用转化结果到目标实体
     */
    public void applyConversion() {
        if (getEntity() == null || targetEffect == null) return;
        getEntity().removeEffect(targetEffect);
        getEntity().addEffect(new MobEffectInstance(
                targetEffect,
                targetDuration,
                Math.min(targetAmplifier, 98),
                false,
                false
        ));
    }
}