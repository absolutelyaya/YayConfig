package absolutelyaya.yayconfig.networking;

import absolutelyaya.yayconfig.YayConfig;
import absolutelyaya.yayconfig.config.Config;
import absolutelyaya.yayconfig.config.Constants;
import absolutelyaya.yayconfig.config.EnumEntry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class PacketRegistry implements Constants
{
	public static void registerC2S()
	{
		PayloadTypeRegistry.playS2C().register(SyncConfigS2CPayload.ID, SyncConfigS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(SyncAllConfigS2CPayload.ID, SyncAllConfigS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(FinishedSyncConfigS2CPayload.ID, FinishedSyncConfigS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(OpenConfigScreenPayload.ID, OpenConfigScreenPayload.CODEC);
		
		PayloadTypeRegistry.playC2S().register(SyncConfigC2SPayload.ID, SyncConfigC2SPayload.CODEC);
		
		ServerPlayNetworking.registerGlobalReceiver(SyncConfigC2SPayload.ID, (payload, context) -> {
			NbtCompound data = payload.data();
			Identifier configId = payload.configId();
			Config config = Config.getFromID(configId);
			if(config == null)
			{
				YayConfig.LOGGER.error("C2S Sync -> No config with configId '{}' found", configId);
				return;
			}
			if(!data.getKeys().containsAll(List.of(RULE_KEY, TYPE_KEY, VALUE_KEY)))
			{
				YayConfig.LOGGER.warn("Invalid C2S Sync Packet Received: {}", data.asString());
				return;
			}
			Optional<String> rule = data.getString(RULE_KEY);
			Optional<Byte> type = data.getByte(TYPE_KEY);
			MinecraftServer server = context.server();
			if(rule.isEmpty() || type.isEmpty())
				return;
			switch(type.get())
			{
				case 0 -> Config.onChanged(server, configId, config.set(rule.get(), data.getBoolean(VALUE_KEY).orElse(false), server));
				case 1 -> Config.onChanged(server, configId, config.set(rule.get(), data.getInt(VALUE_KEY).orElse(0), server));
				case 2 -> Config.onChanged(server, configId, config.set(rule.get(), data.getFloat(VALUE_KEY).orElse(0f), server));
				case 3 -> Config.onChanged(server, configId, config.set((EnumEntry<?>)config.getEntry(rule.get()), data.getInt(VALUE_KEY).orElse(0), server));
			}
		});
	}
}
