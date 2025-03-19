package absolutelyaya.yayconfig;

import absolutelyaya.yayconfig.gui.screen.ConfigScreen;
import absolutelyaya.yayconfig.test.TestClientConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public class ModMenu implements ModMenuApi
{
	@Override
	public ConfigScreenFactory<ConfigScreen> getModConfigScreenFactory()
	{
		if(FabricLoader.getInstance().isDevelopmentEnvironment())
			return i -> new ConfigScreen(TestClientConfig.INSTANCE, i);
		return i -> null;
	}
}
