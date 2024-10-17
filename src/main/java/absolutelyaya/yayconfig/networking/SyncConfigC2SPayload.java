package absolutelyaya.yayconfig.networking;

import absolutelyaya.yayconfig.YayConfig;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncConfigC2SPayload(Identifier configId, NbtCompound data) implements CustomPayload
{
	public static Id<SyncConfigC2SPayload> ID = new Id<>(YayConfig.indentifier("sync_config_c2s"));
	public static PacketCodec<RegistryByteBuf, SyncConfigC2SPayload> CODEC =
			PacketCodec.tuple(Identifier.PACKET_CODEC, SyncConfigC2SPayload::configId,
					PacketCodecs.NBT_COMPOUND, SyncConfigC2SPayload::data,
					SyncConfigC2SPayload::new);
	
	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
