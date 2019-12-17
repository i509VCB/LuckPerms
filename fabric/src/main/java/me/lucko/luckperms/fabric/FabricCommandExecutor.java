package me.lucko.luckperms.fabric;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.lucko.luckperms.common.command.CommandManager;
import me.lucko.luckperms.common.plugin.LuckPermsPlugin;
import me.lucko.luckperms.common.sender.Sender;
import net.minecraft.server.command.ServerCommandSource;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Fabric uses brigadier.
 */
public class FabricCommandExecutor extends CommandManager implements Command<ServerCommandSource>, SuggestionProvider<ServerCommandSource> {
    private static final Splitter ARGUMENT_SPLITTER = Splitter.on(COMMAND_SEPARATOR_PATTERN).omitEmptyStrings();
    private static final Joiner ARGUMENT_JOINER = Joiner.on(' ');
	private static final Splitter TAB_COMPLETE_ARGUMENT_SPLITTER = Splitter.on(COMMAND_SEPARATOR_PATTERN);

    private LPFabricPlugin plugin;

    public FabricCommandExecutor(LPFabricPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    public int run(CommandContext<ServerCommandSource> ctx) {
        Sender lpSender = this.plugin.getSenderFactory().wrap(ctx.getSource());

        String[] input = ctx.getInput().split(" "); // This includes the beginning of the command also, such as /lp group

        List<String> splitArguments = new LinkedList<>(Arrays.asList(input)); // asList gives us an immutable list, so we have to create another one.
        splitArguments.remove(0); // Get rid of the front of the command

        List<String> arguments = stripQuotes(ARGUMENT_SPLITTER.splitToList(ARGUMENT_JOINER.join(splitArguments)));

        onCommand(lpSender, input[0], arguments);

        return 1;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) throws CommandSyntaxException {
        Sender lpSender = this.plugin.getSenderFactory().wrap(ctx.getSource());

		String[] input = ctx.getInput().split(" "); // This includes the beginning of the command also, such as /lp group

		List<String> splitArguments = new LinkedList<>(Arrays.asList(input)); // asList gives us an immutable list, so we have to create another one.
		splitArguments.remove(0); // Get rid of the front of the command

		List<String> arguments = stripQuotes(TAB_COMPLETE_ARGUMENT_SPLITTER.splitToList(ARGUMENT_JOINER.join(splitArguments)));
        List<String> completions = onTabComplete(lpSender, arguments);

		for (String str : completions) {
			builder.suggest(str);
		}

		return builder.buildFuture();
    }
}
