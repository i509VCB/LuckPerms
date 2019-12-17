package me.lucko.luckperms.fabric;

import java.net.MalformedURLException;
import java.nio.file.Path;
import me.lucko.luckperms.common.dependencies.classloader.PluginClassLoader;
import net.fabricmc.loader.launch.common.FabricLauncherBase;

public class FabricClassLoader implements PluginClassLoader {
	@Override
	public void loadJar(Path file) {
		try {
			// Fabric abstracts class loading away to the FabricLauncher.
			FabricLauncherBase.getLauncher().propose(file.toUri().toURL());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}
