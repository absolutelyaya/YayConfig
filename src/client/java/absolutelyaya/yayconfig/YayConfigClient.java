package absolutelyaya.yayconfig;

import absolutelyaya.yayconfig.config.ClientConfig;
import absolutelyaya.yayconfig.networking.ClientPacketHandler;
import absolutelyaya.yayconfig.test.TestClientConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

public class YayConfigClient implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		ClientPacketHandler.register();
		
		ClientLifecycleEvents.CLIENT_STARTED.register(this::loadClientConfig);
		
		if(FabricLoader.getInstance().isDevelopmentEnvironment())
			new TestClientConfig();
	}
	
	void loadClientConfig(MinecraftClient client)
	{
		ClientConfig.getAll().forEach((id, config) -> ClientConfig.getFromID(id).load(client));
	}
}
