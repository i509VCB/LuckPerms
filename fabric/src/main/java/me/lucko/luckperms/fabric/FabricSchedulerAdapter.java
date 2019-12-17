package me.lucko.luckperms.fabric;

import java.util.concurrent.Executor;

import me.lucko.luckperms.common.plugin.scheduler.AbstractJavaScheduler;

/**
 * Fabric doesn't really have a scheduler api, so we use the NonBlockingThreadExecutor from the MinecraftClient/Server for executing sync.
 */
public class FabricSchedulerAdapter extends AbstractJavaScheduler {
	private final LPFabricBootstrap bootstrap;

	public FabricSchedulerAdapter(LPFabricBootstrap bootstrap) {
		this.bootstrap = bootstrap;
	}

	@Override
	public Executor sync() {
		return this.bootstrap.getServer(); // The Server is a NonBlockingThreadExecutor so this is legit.
	}
}
