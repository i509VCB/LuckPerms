package me.lucko.luckperms.fabric;

import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.luckperms.common.api.LuckPermsApiProvider;
import me.lucko.luckperms.common.calculator.CalculatorFactory;
import me.lucko.luckperms.common.config.ConfigKeys;
import me.lucko.luckperms.common.config.adapter.ConfigurationAdapter;
import me.lucko.luckperms.common.context.ContextManager;
import me.lucko.luckperms.common.event.AbstractEventBus;
import me.lucko.luckperms.common.messaging.MessagingFactory;
import me.lucko.luckperms.common.model.User;
import me.lucko.luckperms.common.model.manager.group.StandardGroupManager;
import me.lucko.luckperms.common.model.manager.track.StandardTrackManager;
import me.lucko.luckperms.common.model.manager.user.StandardUserManager;
import me.lucko.luckperms.common.plugin.AbstractLuckPermsPlugin;
import me.lucko.luckperms.common.plugin.util.AbstractConnectionListener;
import me.lucko.luckperms.common.sender.Sender;
import me.lucko.luckperms.common.sender.SenderFactory;
import me.lucko.luckperms.common.util.MoreFiles;
import me.lucko.luckperms.fabric.context.FabricContextManager;
import me.lucko.luckperms.fabric.context.FabricWorldCalculator;
import me.lucko.luckperms.fabric.event.PlayerWorldChangeCallback;
import me.lucko.luckperms.fabric.listeners.FabricEventListeners;
import me.lucko.luckperms.fabric.messaging.FabricMessagingFactory;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class LPFabricPlugin extends AbstractLuckPermsPlugin {
	private LPFabricBootstrap bootstrap;
	private ContextManager<ServerPlayerEntity> contextManager;
	private SenderFactory<ServerCommandSource> senderFactory;

	private StandardUserManager userManager;
	private StandardGroupManager groupManager;
	private StandardTrackManager trackManager;

	private FabricCommandExecutor commandManager = new FabricCommandExecutor(this);

	public LPFabricPlugin(LPFabricBootstrap bootstrap) {
		this.bootstrap = bootstrap;
		// Fabric's Commands are handled through it's own API, however we need to let fabric know about the commands now or it will be too late to register commands.

		CommandRegistry.INSTANCE.register(false, dispatcher -> {
			LiteralCommandNode<ServerCommandSource> lp = literal("luckperms")
					.then(argument("args", greedyString())
					.executes(this.getCommandManager()))
					.build();
		});
	}

	@Override
	protected void setupSenderFactory() {
		this.senderFactory = new FabricSenderFactory(this);
	}

	@Override
	protected ConfigurationAdapter provideConfigurationAdapter() {
		return new FabricConfigAdapter(this, resolveConfig());
	}

	private Path resolveConfig() {
		Path path = FabricLoader.getInstance().getConfigDirectory().toPath().resolve("luckperms").resolve("luckperms.conf");
		if (!Files.exists(path)) {
			try {
				MoreFiles.createDirectoriesIfNotExists(this.bootstrap.getConfigDirectory());
				try (InputStream is = getClass().getClassLoader().getResourceAsStream("luckperms.conf")) {
					Files.copy(is, path);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return path;
	}

	@Override
	protected void registerPlatformListeners() {
		FabricEventListeners listeners = new FabricEventListeners(this);
		PlayerWorldChangeCallback.EVENT.register(listeners::onWorldChange);
	}

	@Override
	protected MessagingFactory<LPFabricPlugin> provideMessagingFactory() {
		return new FabricMessagingFactory(this);
	}

	@Override
	protected void registerCommands() {
		// For the Fabric Context, this happens too late, so we have to register commands in Constructor so Fabric's API can register them.
	}

	@Override
	protected void setupManagers() {
		this.userManager = new StandardUserManager(this);
		this.groupManager = new StandardGroupManager(this);
		this.trackManager = new StandardTrackManager(this);
	}

	@Override
	protected CalculatorFactory provideCalculatorFactory() {
		return null;
	}

	@Override
	protected void setupContextManager() {
        this.contextManager = new FabricContextManager(this);
        this.contextManager.registerCalculator(new FabricWorldCalculator(this));
	}

	@Override
	protected void setupPlatformHooks() {

	}

	@Override
	protected AbstractEventBus provideEventBus(LuckPermsApiProvider provider) {
		return new FabricEventBus(this, provider);
	}

	@Override
	protected void registerApiOnPlatform(LuckPerms api) {

	}

	@Override
	protected void registerHousekeepingTasks() {

	}

	@Override
	protected void performFinalSetup() {

	}

	@Override
	public LPFabricBootstrap getBootstrap() {
		return this.bootstrap;
	}

	@Override
	public StandardUserManager getUserManager() {
		return this.userManager;
	}

	@Override
	public StandardGroupManager getGroupManager() {
		return this.groupManager;
	}

	@Override
	public StandardTrackManager getTrackManager() {
		return this.trackManager;
	}

	@Override
	public FabricCommandExecutor getCommandManager() {
		return this.commandManager;
	}

	@Override
	public AbstractConnectionListener getConnectionListener() {
		return null;
	}

	@Override
	public ContextManager<ServerPlayerEntity> getContextManager() {
		return this.contextManager;
	}

	@Override
	public Optional<QueryOptions> getQueryOptionsForUser(User user) {
		return Optional.empty();
	}

	@Override
	public Stream<Sender> getOnlineSenders() {
		return Stream.concat(
				Stream.of(getConsoleSender()),
				bootstrap.getServer().getPlayerManager().getPlayerList().stream().map(serverPlayerEntity -> getSenderFactory().wrap(serverPlayerEntity.getCommandSource()))
		);
	}

	@Override
	public Sender getConsoleSender() {
		return getSenderFactory().wrap(bootstrap.getServer().getCommandSource());
	}

	public SenderFactory<ServerCommandSource> getSenderFactory() {
		return this.senderFactory;
	}

	public void refreshAutoOp(ServerPlayerEntity player, boolean callerIsSync) {
		if (!getConfiguration().get(ConfigKeys.AUTO_OP)) {
			return;
		}

		User user = getUserManager().getIfLoaded(player.getUuid());
		boolean value;

		if (user != null) {
			Map<String, Boolean> permData = user.getCachedData().getPermissionData(this.contextManager.getQueryOptions(player)).getPermissionMap();
			value = permData.getOrDefault("luckperms.autoop", false);
		} else {
			value = false;
		}

		if (callerIsSync) {
			setOp(player, value);
		} else {
			this.bootstrap.getScheduler().executeSync(() -> setOp(player, value));
		}
	}

	private void setOp(PlayerEntity player, boolean value) {
		if (value) {
			this.bootstrap.getServer().getPlayerManager().getOpList().add(new OperatorEntry(player.getGameProfile(), bootstrap.getDefaultOpLevel(), bootstrap.shouldOpsBypassPlayerLimit()));
			return;
		}

		this.bootstrap.getServer().getPlayerManager().getOpList().remove(player.getGameProfile());
	}
}
