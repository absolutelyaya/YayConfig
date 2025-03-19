package absolutelyaya.yayconfig;

import absolutelyaya.yayconfig.commands.Commands;
import absolutelyaya.yayconfig.config.Config;
import absolutelyaya.yayconfig.networking.PacketRegistry;
import absolutelyaya.yayconfig.test.TestConfig;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class YayConfig implements ModInitializer
{
	public static final String MOD_ID = "yayconfig";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize()
	{
		PacketRegistry.registerC2S();
		Commands.register();
		
		ServerLifecycleEvents.SERVER_STARTING.register(this::onLoadServerConfig);
		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, handler) -> onLoadServerConfig(server));
		
		ServerPlayConnectionEvents.JOIN.register((networkHandler, sender, server) -> Config.SyncAll(networkHandler.player));
		
		if(FabricLoader.getInstance().isDevelopmentEnvironment())
			new TestConfig();
	}
	
	public static Identifier indentifier(String path)
	{
		return Identifier.of(MOD_ID, path);
	}
	
	void onLoadServerConfig(MinecraftServer server)
	{
		Config.getAll().forEach((id, config) -> onLoadServerConfig(server, id, config));
	}
	
	void onLoadServerConfig(MinecraftServer server, Identifier id, Config config)
	{
		config.load(server);
		config.syncAll(server);
		if(!Events.onLoadConfigListeners.containsKey(id))
			return;
		Events.onLoadConfigListeners.get(id).forEach(i -> i.accept(server));
	}
	
	public static void onFinishSync(Identifier id)
	{
		if(!Events.onFinishSyncListeners.containsKey(id))
			return;
		Events.onFinishSyncListeners.get(id).forEach(Runnable::run);
	}
	
	public static class Events
	{
		static final Map<Identifier, List<Runnable>> onFinishSyncListeners = new HashMap<>();
		static final Map<Identifier, List<Consumer<MinecraftServer>>> onLoadConfigListeners = new HashMap<>();
		
		public static void registerOnFinishSyncListener(Identifier configId, Runnable listener)
		{
			if(!onFinishSyncListeners.containsKey(configId))
				onFinishSyncListeners.put(configId, new ArrayList<>());
			onFinishSyncListeners.get(configId).add(listener);
		}
		
		public static void registerOnLoadConfigListener(Identifier configId, Consumer<MinecraftServer> listener)
		{
			if(!onLoadConfigListeners.containsKey(configId))
				onLoadConfigListeners.put(configId, new ArrayList<>());
			onLoadConfigListeners.get(configId).add(listener);
		}
	}
}
