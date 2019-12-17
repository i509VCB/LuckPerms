package me.lucko.luckperms.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerQuitCallback {
    Event<PlayerQuitCallback> EVENT = EventFactory.createArrayBacked(PlayerQuitCallback.class, callbacks -> serverPlayerEntity -> {
        for (PlayerQuitCallback callback : callbacks) {
            callback.onDisconnect(serverPlayerEntity);
        }
    });

    void onDisconnect(ServerPlayerEntity player);
}
