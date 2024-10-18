package absolutelyaya.yayconfig.commands;

import absolutelyaya.yayconfig.config.Config;
import absolutelyaya.yayconfig.networking.OpenConfigScreenPayload;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.command.argument.IdentifierArgumentType.identifier;

public class Commands
{
	public static void register()
	{
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, enviroment) -> {
			dispatcher.register(CommandManager.literal("yayConfig")
										.requires(ServerCommandSource::isExecutedByPlayer).requires(source -> source.hasPermissionLevel(2))
										.then(CommandManager.argument("configId", identifier())
													  .suggests(Commands::suggestConfigIds).executes(Commands::executeOpenConfigMenu)));
		});
	}
	
	private static CompletableFuture<Suggestions> suggestConfigIds(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder)
	{
		Config.getAllIds().forEach(i -> builder.suggest(i.toString()));
		return builder.buildFuture();
	}
	
	private static byte executeOpenConfigMenu(CommandContext<ServerCommandSource> context)
	{
		if(context.getSource().getPlayer() == null)
			return 0;
		Identifier configId = context.getArgument("configId", Identifier.class);
		if(Config.getFromID(configId) == null)
		{
			context.getSource().sendFeedback(() -> Text.translatable("command.yayconfig.open-config.invalid", configId), false);
			return Command.SINGLE_SUCCESS;
		}
		ServerPlayNetworking.send(context.getSource().getPlayer(), new OpenConfigScreenPayload(configId));
		return Command.SINGLE_SUCCESS;
	}
}
