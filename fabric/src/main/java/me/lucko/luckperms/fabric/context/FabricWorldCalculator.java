package me.lucko.luckperms.fabric.context;

import me.lucko.luckperms.common.config.ConfigKeys;
import me.lucko.luckperms.fabric.LPFabricPlugin;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.DefaultContextKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;
import java.util.Set;

public class FabricWorldCalculator implements ContextCalculator<ServerPlayerEntity> {
    private final LPFabricPlugin plugin;

    public FabricWorldCalculator(LPFabricPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
	public void calculate(@NonNull ServerPlayerEntity target, @NonNull ContextConsumer consumer) {
        Set<String> seen = new HashSet<>();
        String world = target.getServerWorld().getLevelProperties().getLevelName().toLowerCase();
        while (seen.add(world)) {
            consumer.accept(DefaultContextKeys.WORLD_KEY, world);
            world = this.plugin.getConfiguration().get(ConfigKeys.WORLD_REWRITES).getOrDefault(world, world).toLowerCase();
        }
	}


}
