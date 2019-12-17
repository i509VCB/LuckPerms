package me.lucko.luckperms.fabric.listeners;

import me.lucko.luckperms.fabric.LPFabricPlugin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

/**
 * As much I would hate to admit it, Fabric doesn't have as many events as Bukkit or Sponge. So we implemented our own events in a few spots.
 */
public class FabricEventListeners {
	private final LPFabricPlugin plugin;

	public FabricEventListeners(LPFabricPlugin plugin) {
		this.plugin = plugin;
	}

	public void onWorldChange(World world, ServerPlayerEntity playerEntity) {
		this.plugin.getContextManager().invalidateCache(playerEntity);
		this.plugin.refreshAutoOp(playerEntity, true);
	}
}
