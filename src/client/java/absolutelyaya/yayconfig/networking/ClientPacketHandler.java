package absolutelyaya.yayconfig.networking;

import absolutelyaya.yayconfig.YayConfig;
import absolutelyaya.yayconfig.config.Config;
import absolutelyaya.yayconfig.config.Constants;
import absolutelyaya.yayconfig.config.EnumEntry;
import absolutelyaya.yayconfig.gui.screen.ConfigScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

public class ClientPacketHandler implements Constants
{
	public static void register()
	{
		ClientPlayNetworking.registerGlobalReceiver(SyncConfigS2CPayload.ID, (payload, context) -> {
			applyData(payload.configId(), payload.data());
		});
		ClientPlayNetworking.registerGlobalReceiver(FinishedSyncConfigS2CPayload.ID,
				(payload, context) -> YayConfig.onFinishSync(payload.configId()));
		ClientPlayNetworking.registerGlobalReceiver(SyncAllConfigS2CPayload.ID, (payload, context) -> {
			NbtCompound data = payload.data();
			for (String i : data.getKeys())
			{
				if(!data.contains(i, NbtElement.COMPOUND_TYPE))
					continue;
				NbtCompound nbt = data.getCompound(i);
				applyData(payload.configId(), nbt);
			}
		});
		ClientPlayNetworking.registerGlobalReceiver(OpenConfigScreenPayload.ID, (payload, context) -> {
			Identifier configId = payload.configId();
			Config config = Config.getFromID(configId);
			if(config == null)
				return;
			context.client().setScreen(new ConfigScreen(config));
		});
	}
	
	static void applyData(Identifier configId, NbtCompound data)
	{
		Config config = Config.getFromID(configId);
		if(config == null)
		{
			YayConfig.LOGGER.error("S2C Sync -> No config with configId '{}' found", configId);
			return;
		}
		if(!(data.contains(RULE_KEY, NbtElement.STRING_TYPE) &&
					 data.contains(TYPE_KEY, NbtElement.BYTE_TYPE) &&
					 data.contains(VALUE_KEY)))
		{
			YayConfig.LOGGER.warn("Invalid S2C Sync Packet Received: {}", data.asString());
			return;
		}
		String rule = data.getString(RULE_KEY);
		byte type = data.getByte(TYPE_KEY);
		switch(type)
		{
			case BOOLEAN_TYPE -> config.set(rule, data.getBoolean(VALUE_KEY), null);
			case INT_TYPE -> config.set(rule, data.getInt(VALUE_KEY), null);
			case FLOAT_TYPE -> config.set(rule, data.getFloat(VALUE_KEY), null);
			case ENUM_TYPE -> config.set((EnumEntry<?>)config.getEntry(rule), data.getInt(VALUE_KEY), null);
		}
	}
}
