package me.lucko.luckperms.fabric;

import java.nio.file.Path;
import me.lucko.luckperms.common.config.adapter.ConfigurateConfigAdapter;
import me.lucko.luckperms.common.config.adapter.ConfigurationAdapter;
import me.lucko.luckperms.common.plugin.LuckPermsPlugin;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

/**
 * Fabric doesn't have any real config API for say, so we will use Configurate.
 */
public class FabricConfigAdapter extends ConfigurateConfigAdapter implements ConfigurationAdapter {
	public FabricConfigAdapter(LuckPermsPlugin plugin, Path path) {
		super(plugin, path);
	}

	@Override
	protected ConfigurationLoader<? extends ConfigurationNode> createLoader(Path path) {
		return HoconConfigurationLoader.builder().setPath(path).build();
	}
}
