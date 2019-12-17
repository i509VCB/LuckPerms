package me.lucko.luckperms.fabric;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import com.mojang.authlib.GameProfile;
import me.lucko.luckperms.common.dependencies.classloader.PluginClassLoader;
import me.lucko.luckperms.common.plugin.bootstrap.LuckPermsBootstrap;
import me.lucko.luckperms.common.plugin.logging.PluginLogger;
import me.lucko.luckperms.common.plugin.scheduler.SchedulerAdapter;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.launch.common.FabricLauncherBase;
import net.luckperms.api.platform.Platform;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract Bootstrap Mod for LuckPerms running on fabric. Note that the context in which fabric can ran be either the client (on the IntegratedServer) or a server.
 */
public class LPFabricBootstrap implements LuckPermsBootstrap, DedicatedServerModInitializer {
    private static final Logger LOGGER = LogManager.getLogger(LPFabricBootstrap.class);
	private static final String MODID = "luckperms";
	private boolean opsBypassPlayerLimit = true; // TODO

    public LPFabricBootstrap() {
        this.classLoader = new FabricClassLoader();
        this.schedulerAdapter = new FabricSchedulerAdapter(this);
        this.plugin = new LPFabricPlugin(this);
    }

    /**
     * The plugin logger
     */
    private PluginLogger logger;

    /**
     * A scheduler adapter for the platform
     */
    private final SchedulerAdapter schedulerAdapter;

    /**
     * The plugin class loader.
     */
    private PluginClassLoader classLoader;

    /**
     * The plugin instance
     */
    private LPFabricPlugin plugin;

    /**
     * The time when the plugin was enabled
     */
    private long startTime;

    // load/enable latches
    private final CountDownLatch loadLatch = new CountDownLatch(1);
    private final CountDownLatch enableLatch = new CountDownLatch(1);

    @Override
    public PluginLogger getPluginLogger() {
        return logger;
    }

    @Override
    public SchedulerAdapter getScheduler() {
        return schedulerAdapter;
    }

    @Override
    public PluginClassLoader getPluginClassLoader() {
        return this.classLoader;
    }

    @Override
    public CountDownLatch getLoadLatch() {
        return this.loadLatch;
    }

    @Override
    public CountDownLatch getEnableLatch() {
        return this.enableLatch;
    }

    @Override
    public String getVersion() {
        return FabricLoader.getInstance().getModContainer(MODID).orElseThrow(this::modContainerNotFound).getMetadata().getVersion().getFriendlyString(); // Optionals, this should ALWAYS work. If the optional is empty we do this just in case.
    }

	private RuntimeException modContainerNotFound() {
    	throw new RuntimeException("Could not find the LuckPerms mod container, something has gone wrong with FabricLoader and this should be brought up with the fabric development team");
	}

	@Override
    public long getStartupTime() {
        return startTime;
    }

    @Override
    public Platform.Type getType() {
        return Platform.Type.FABRIC;
    }

    @Override
    public String getServerBrand() {
        return getServer().getServerModName();
    }

    @Override
    public String getServerVersion() {
        return getServer().getVersion();
    }

    @Override
    public Path getDataDirectory() {
        return FabricLoader.getInstance().getConfigDirectory().toPath().resolve("LuckPerms");
    }

    @Override
    public InputStream getResourceStream(String path) {
        return FabricLauncherBase.getLauncher().getResourceAsStream(path);
    }

    @Override
    public Optional<?> getPlayer(UUID uniqueId) {
        return Optional.ofNullable(getServer().getPlayerManager().getPlayer(uniqueId));
    }

    @Override
    public Optional<UUID> lookupUniqueId(String username) {
        GameProfile profile = getServer().getUserCache().findByName(username);

        if (profile != null && profile.getId() != null) {
            return Optional.of(profile.getId());
        }

        return Optional.empty();
    }

    @Override
    public Optional<String> lookupUsername(UUID uniqueId) {
        GameProfile profile = getServer().getUserCache().getByUuid(uniqueId);

        if (profile != null && profile.getId() != null) {
            return Optional.of(profile.getName());
        }

        return Optional.empty();
    }

    @Override
    public int getPlayerCount() {
		return getServer().getCurrentPlayerCount();

	}

    @Override
    public Stream<String> getPlayerList() {
        return getServer().getPlayerManager().getPlayerList().stream().map(PlayerEntity::getEntityName);
    }

    @Override
    public Stream<UUID> getOnlinePlayers() {
        return getServer().getPlayerManager().getPlayerList().stream().map(PlayerEntity::getUuid);
    }

    @Override
    public boolean isPlayerOnline(UUID uniqueId) {
        return getServer().getPlayerManager().getPlayer(uniqueId) != null;
    }

    public MinecraftServer getServer() {
        return (MinecraftServer) FabricLoader.getInstance().getGameInstance();
    }

    public int getDefaultOpLevel() {
        return 4;
    }

	public boolean shouldOpsBypassPlayerLimit() {
    	return opsBypassPlayerLimit;
	}

    @Override
    public void onInitializeServer() {
        System.out.println("TEST MESSAGE -- LP STARTED");
        this.logger = new FabricPluginLogger(LOGGER);
        this.plugin.load();
        ServerStartCallback.EVENT.register(server -> this.plugin.enable());
        //ServerStopCallback.EVENT.register(server -> this.plugin.);
    }
}
