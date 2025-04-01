package absolutelyaya.yayconfig.networking;

import absolutelyaya.yayconfig.YayConfig;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncAllConfigS2CPayload(Identifier configId, NbtCompound data) implements CustomPayload
{
	public static Id<SyncAllConfigS2CPayload> ID = new Id<>(YayConfig.id("sync_all_config_c2s"));
	public static PacketCodec<RegistryByteBuf, SyncAllConfigS2CPayload> CODEC =
			PacketCodec.tuple(Identifier.PACKET_CODEC, SyncAllConfigS2CPayload::configId,
					PacketCodecs.NBT_COMPOUND, SyncAllConfigS2CPayload::data,
					SyncAllConfigS2CPayload::new);
	
	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
