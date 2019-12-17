package me.lucko.luckperms.fabric;

import java.util.UUID;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.luckperms.common.plugin.LuckPermsPlugin;
import me.lucko.luckperms.common.sender.SenderFactory;
import me.lucko.luckperms.fabric.adapter.FabricTextAdapter;
import net.kyori.text.Component;
import net.luckperms.api.util.Tristate;
import net.minecraft.server.command.ServerCommandSource;

/**
 * Minecraft's ServerCommandSource may not always be an entity. You should watch out for that.
 */
public class FabricSenderFactory extends SenderFactory<ServerCommandSource> {
	public FabricSenderFactory(LuckPermsPlugin plugin) {
		super(plugin);
	}

	@Override
	protected UUID getUniqueId(ServerCommandSource commandSource) {
		try {
			return commandSource.getEntityOrThrow().getUuid();
		} catch (CommandSyntaxException e) {
			return null; // TODO: Exception or null?
		}
	}

	@Override
	protected String getName(ServerCommandSource commandSource) {
		return commandSource.getName();
	}

	@Override
	protected void sendMessage(ServerCommandSource commandSource, String s) {
		commandSource.sendFeedback(FabricTextAdapter.adaptString(s), false);
	}

	@Override
	protected void sendMessage(ServerCommandSource commandSource, Component message) {
		commandSource.sendFeedback(FabricTextAdapter.adapt(message), false);
	}

	@Override
	protected Tristate getPermissionValue(ServerCommandSource commandSource, String node) {
		return null;
	}

	@Override
	protected boolean hasPermission(ServerCommandSource commandSource, String node) {
		return false;
	}
}
