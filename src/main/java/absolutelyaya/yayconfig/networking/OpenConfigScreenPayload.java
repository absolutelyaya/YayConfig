package absolutelyaya.yayconfig.networking;

import absolutelyaya.yayconfig.YayConfig;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record OpenConfigScreenPayload(Identifier configId) implements CustomPayload
{
	public static CustomPayload.Id<OpenConfigScreenPayload> ID = new CustomPayload.Id<>(YayConfig.id("open_config_c2s"));
	public static PacketCodec<RegistryByteBuf, OpenConfigScreenPayload> CODEC =
			PacketCodec.tuple(Identifier.PACKET_CODEC, OpenConfigScreenPayload::configId, OpenConfigScreenPayload::new);
	
	@Override
	public CustomPayload.Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
