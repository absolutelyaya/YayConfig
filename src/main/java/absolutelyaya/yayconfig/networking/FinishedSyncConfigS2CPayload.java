package absolutelyaya.yayconfig.networking;

import absolutelyaya.yayconfig.YayConfig;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record FinishedSyncConfigS2CPayload(Identifier configId) implements CustomPayload
{
	public static Id<FinishedSyncConfigS2CPayload> ID = new Id<>(YayConfig.id("finish_sync_config_c2s"));
	public static PacketCodec<RegistryByteBuf, FinishedSyncConfigS2CPayload> CODEC =
			PacketCodec.tuple(Identifier.PACKET_CODEC, FinishedSyncConfigS2CPayload::configId, FinishedSyncConfigS2CPayload::new);
	
	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
