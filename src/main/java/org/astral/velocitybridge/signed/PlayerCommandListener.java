package org.astral.velocitybridge.signed;

import org.astral.velocitybridge.VelocityBridge;
import org.astral.velocitybridge.signed.queue.SignedQueue;
import org.astral.velocitybridge.signed.queue.SignedResult;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class PlayerCommandListener implements Listener {
    private final VelocityBridge plugin;
    private final SignedQueue commandQueue;

    public PlayerCommandListener(@NotNull VelocityBridge plugin) {
        this.plugin = plugin;
        this.commandQueue = plugin.getCommandQueue();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (isLocal()) {
            return;
        }
        event.setCancelled(true);

        Player player = event.getPlayer();
        String originalMessage = event.getMessage();
        this.commandQueue.dataFrom(player.getUniqueId())
                .nextResult()
                .completeOnTimeout(SignedResult.allowed(), 500, TimeUnit.MILLISECONDS)
                .thenAccept(result -> {
                    if (result.cancelled()) {
                        return;
                    }
                    String modified = result.toModify();
                    String finalCommand = modified != null ? modified : originalMessage;
                    String cmdToRun = finalCommand.startsWith("/") ? finalCommand.substring(1) : finalCommand;
                    plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getServer().dispatchCommand(player, cmdToRun));
                });
    }

    private boolean isLocal() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            String className = element.getClassName();
            if (className.contains("ServerboundChatCommand") ||
                    className.contains("ServerGamePacketListenerImpl") ||
                    className.contains("PlayerConnection")) {
                return false; // Viene desde el cliente/Proxy
            }
        }
        return true;
    }
}