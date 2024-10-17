package absolutelyaya.yayconfig;

import absolutelyaya.yayconfig.networking.ClientPacketHandler;
import net.fabricmc.api.ClientModInitializer;

public class YayConfigClient implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		ClientPacketHandler.register();
	}
}
