package org.astral.velocitybridge.signed;

import org.astral.velocitybridge.VelocityBridge;
import org.astral.velocitybridge.signed.queue.SignedQueue;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerQuitListener implements Listener {
    private final SignedQueue chatQueue;
    private final SignedQueue commandQueue;

    public PlayerQuitListener(@NotNull VelocityBridge plugin) {
        this.chatQueue = plugin.getChatQueue();
        this.commandQueue = plugin.getCommandQueue();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onQuit(@NotNull PlayerQuitEvent event) {
        chatQueue.removeData(event.getPlayer().getUniqueId());
        commandQueue.removeData(event.getPlayer().getUniqueId());
    }
}