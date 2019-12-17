package me.lucko.luckperms.fabric;

import me.lucko.luckperms.common.api.LuckPermsApiProvider;
import me.lucko.luckperms.common.event.AbstractEventBus;
import me.lucko.luckperms.common.plugin.LuckPermsPlugin;
import net.fabricmc.loader.api.ModContainer;

public class FabricEventBus extends AbstractEventBus<ModContainer> {
	protected FabricEventBus(LuckPermsPlugin plugin, LuckPermsApiProvider apiProvider) {
		super(plugin, apiProvider);
	}

	@Override
	protected ModContainer checkPlugin(Object mod) throws IllegalArgumentException {
		if (mod instanceof ModContainer) {
			return (ModContainer) mod;
		}

		throw new IllegalArgumentException("Object " + mod + " (" + mod.getClass().getName() + ") is not a ModContainer.");
	}
}
