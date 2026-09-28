package com.lim_mirror.Lim_Mirror;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = lim_mirror.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StrikeCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("lim_mirror")
                        .then(Commands.literal("strikeweapon")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    Player player = source.getPlayerOrException();
                                    ItemStack mainHand = player.getMainHandItem();

                                    if (mainHand.isEmpty()) {
                                        source.sendFailure(Component.literal("§c主手没有物品！"));
                                        return 0;
                                    }

                                    CompoundTag tag = mainHand.getOrCreateTag();
                                    tag.putBoolean("lim_mirror_strike", true);

                                    source.sendSuccess(
                                            () -> Component.literal("§a已将主手武器标记为打击武器！"),
                                            true
                                    );
                                    return 1;
                                })
                        )
        );

        // 移除打击标记
        dispatcher.register(
                Commands.literal("lim_mirror")
                        .then(Commands.literal("unstrikeweapon")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    Player player = source.getPlayerOrException();
                                    ItemStack mainHand = player.getMainHandItem();

                                    if (mainHand.isEmpty()) {
                                        source.sendFailure(Component.literal("§c主手没有物品！"));
                                        return 0;
                                    }

                                    CompoundTag tag = mainHand.getTag();
                                    if (tag != null) {
                                        tag.remove("lim_mirror_strike");
                                    }

                                    source.sendSuccess(
                                            () -> Component.literal("§a已移除主手武器的打击标记！"),
                                            true
                                    );
                                    return 1;
                                })
                        )
        );
    }
}