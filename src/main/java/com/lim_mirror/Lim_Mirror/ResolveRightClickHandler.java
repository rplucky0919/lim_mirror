package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = lim_mirror.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ResolveRightClickHandler {

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (player == null) return;
        if (player.level().isClientSide()) return;

        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof Resolve)) return;

        // 切换状态
        boolean currentState = player.getPersistentData().getBoolean("resolveActive");
        boolean newState = !currentState;

        player.getPersistentData().putBoolean("resolveActive", newState);

        if (newState) {
            player.displayClientMessage(
                    Component.literal("§d[决意] §a已开启！"),
                    true
            );
        } else {
            player.displayClientMessage(
                    Component.literal("§d[决意] §c已关闭！"),
                    true
            );
        }

        event.setCanceled(true);
    }
}