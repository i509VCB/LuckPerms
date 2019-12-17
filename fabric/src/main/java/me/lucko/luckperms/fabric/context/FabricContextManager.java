package me.lucko.luckperms.fabric.context;

import me.lucko.luckperms.common.context.ContextManager;
import me.lucko.luckperms.common.context.QueryOptionsSupplier;
import me.lucko.luckperms.common.plugin.LuckPermsPlugin;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.server.network.ServerPlayerEntity;

public class FabricContextManager extends ContextManager<ServerPlayerEntity> {
	public FabricContextManager(LuckPermsPlugin plugin) {
		super(plugin, ServerPlayerEntity.class);
	}

	@Override
	public QueryOptionsSupplier getCacheFor(ServerPlayerEntity subject) {
		return null;
	}

	@Override
	public QueryOptions formQueryOptions(ServerPlayerEntity subject, ImmutableContextSet contextSet) {
		return null;
	}

	@Override
	public void invalidateCache(ServerPlayerEntity subject) {

	}
}
