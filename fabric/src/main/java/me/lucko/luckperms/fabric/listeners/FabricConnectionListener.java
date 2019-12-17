package me.lucko.luckperms.fabric.listeners;

import me.lucko.luckperms.common.config.ConfigKeys;
import me.lucko.luckperms.common.plugin.LuckPermsPlugin;
import me.lucko.luckperms.common.plugin.util.AbstractConnectionListener;
import me.lucko.luckperms.fabric.event.PlayerQuitCallback;
import net.minecraft.server.network.ServerPlayerEntity;

public class FabricConnectionListener extends AbstractConnectionListener {
    private final LuckPermsPlugin plugin;

    public FabricConnectionListener(LuckPermsPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        PlayerQuitCallback.EVENT.register(this::onDisconnect);
    }

    private void onDisconnect(ServerPlayerEntity playerEntity) {
        handleDisconnect(playerEntity.getUuid());
    }
}
