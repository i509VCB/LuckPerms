package me.lucko.luckperms.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public interface PlayerWorldChangeCallback {
	Event<PlayerWorldChangeCallback> EVENT = EventFactory.createArrayBacked(PlayerWorldChangeCallback.class, (callbacks) -> (originalWorld, player) -> {
		for (PlayerWorldChangeCallback callback : callbacks) {
			callback.onChangeWorld(originalWorld, player);
		}
	});

	void onChangeWorld(World originalWorld, ServerPlayerEntity player);
}
