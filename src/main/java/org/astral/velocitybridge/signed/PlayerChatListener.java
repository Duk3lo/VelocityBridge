package org.astral.velocitybridge.signed;

import org.astral.velocitybridge.VelocityBridge;
import org.astral.velocitybridge.signed.queue.SignedQueue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerChatListener implements Listener {
    private final SignedQueue chatQueue;

    public PlayerChatListener(@NotNull VelocityBridge plugin) {
        this.chatQueue = plugin.getChatQueue();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(@SuppressWarnings("deprecation") AsyncPlayerChatEvent event) {
        if (isLocal()) {
            return;
        }
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        chatQueue.dataFrom(player.getUniqueId())
                .nextResult()
                .thenAccept(result -> {
                    if (result.cancelled()) {
                        event.setCancelled(true);
                    } else {
                        String modified = result.toModify();
                        if (modified != null) {
                            event.setMessage(modified);
                        }
                    }
                }).join();
    }

    private boolean isLocal() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            String methodName = element.getMethodName();
            String className = element.getClassName();
            if (methodName.contains("handleChat") ||
                    className.contains("PlayerConnection") ||
                    className.contains("ServerGamePacketListenerImpl")) {
                return false;
            }
        }
        return true;
    }
}