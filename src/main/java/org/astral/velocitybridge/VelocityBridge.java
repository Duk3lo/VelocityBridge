package org.astral.velocitybridge;

import org.astral.velocitybridge.signed.PlayerChatListener;
import org.astral.velocitybridge.signed.PlayerQuitListener;
import org.astral.velocitybridge.signed.SignedMessageListener;
import org.astral.velocitybridge.signed.queue.SignedQueue;
import org.bukkit.plugin.java.JavaPlugin;

public final class VelocityBridge extends JavaPlugin {

    private static final String SIGNED_CHANNEL = "signedvelocity:main";

    private SignedQueue chatQueue;
    private SignedQueue commandQueue;

    @Override
    public void onEnable() {
        this.chatQueue = new SignedQueue();
        this.commandQueue = new SignedQueue();
        getServer().getMessenger().registerIncomingPluginChannel(this, SIGNED_CHANNEL, new SignedMessageListener(this));
        getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);

        getLogger().info("VelocityBridge habilitado (Soporte exclusivo para SignedVelocity).");
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterIncomingPluginChannel(this, SIGNED_CHANNEL);
    }

    public SignedQueue getChatQueue() {
        return chatQueue;
    }

    public SignedQueue getCommandQueue() {
        return commandQueue;
    }
}