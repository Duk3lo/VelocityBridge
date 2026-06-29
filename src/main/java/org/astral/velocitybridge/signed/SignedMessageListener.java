package org.astral.velocitybridge.signed;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.astral.velocitybridge.VelocityBridge;
import org.astral.velocitybridge.signed.queue.SignedQueue;
import org.astral.velocitybridge.signed.queue.SignedResult;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SignedMessageListener implements PluginMessageListener {
    private final VelocityBridge plugin;

    public SignedMessageListener(VelocityBridge plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!channel.equals("signedvelocity:main")) {
            return;
        }

        ByteArrayDataInput input = ByteStreams.newDataInput(message);
        UUID playerId = UUID.fromString(input.readUTF());
        String source = input.readUTF();
        String result = input.readUTF();

        SignedQueue queue;
        if ("COMMAND".equalsIgnoreCase(source)) {
            queue = plugin.getCommandQueue();
        } else if ("CHAT".equalsIgnoreCase(source)) {
            queue = plugin.getChatQueue();
        } else {
            return;
        }

        SignedResult signedResult;
        if ("CANCEL".equalsIgnoreCase(result)) {
            signedResult = SignedResult.cancel();
        } else if ("MODIFY".equalsIgnoreCase(result)) {
            signedResult = SignedResult.modify(input.readUTF());
        } else {
            signedResult = SignedResult.allowed();
        }

        queue.dataFrom(playerId).complete(signedResult);
    }
}