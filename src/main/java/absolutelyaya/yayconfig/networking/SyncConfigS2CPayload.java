package absolutelyaya.yayconfig.networking;

import absolutelyaya.yayconfig.YayConfig;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncConfigS2CPayload(Identifier configId, NbtCompound data) implements CustomPayload
{
	public static Id<SyncConfigS2CPayload> ID = new Id<>(YayConfig.id("sync_config_s2c"));
	public static PacketCodec<RegistryByteBuf, SyncConfigS2CPayload> CODEC =
			PacketCodec.tuple(Identifier.PACKET_CODEC, SyncConfigS2CPayload::configId,
					PacketCodecs.NBT_COMPOUND, SyncConfigS2CPayload::data,
					SyncConfigS2CPayload::new);
	
	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
