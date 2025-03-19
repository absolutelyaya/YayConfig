package absolutelyaya.yayconfig.config;

import absolutelyaya.yayconfig.networking.FinishedSyncConfigS2CPayload;
import absolutelyaya.yayconfig.networking.SyncAllConfigS2CPayload;
import absolutelyaya.yayconfig.networking.SyncConfigS2CPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public abstract class Config extends AbstractConfig
{
	static final Map<Identifier, Config> configMap = new LinkedHashMap<>();
	
	public Config(Identifier id)
	{
		super(id);
		configMap.put(id, this);
	}
	
	public EnumEntry<?> set(EnumEntry<?> entry, int ordinal, @Nullable MinecraftServer server)
	{
		EnumEntry<?> v = super.set(entry, ordinal);
		if(server != null)
			save(server);
		return v;
	}
	
	public <V> ConfigEntry<V> set(String id, V value, @Nullable MinecraftServer server)
	{
		ConfigEntry<V> v = super.set(id, value);
		if(server != null)
			save(server);
		return v;
	}
	
	public static Config getFromID(Identifier configID)
	{
		return configMap.get(configID);
	}
	
	public void save(MinecraftServer server)
	{
		if(server == null)
			return;
		Path gameDir = server.getSavePath(WorldSavePath.ROOT);
		try
		{
			File dir = Path.of(gameDir.toString(), "config", id.getNamespace()).toFile();
			dir.mkdirs();
			File file = Path.of(gameDir.toString(), "config", id.getNamespace(), getFileName()).toFile();
			file.createNewFile();
			try (FileWriter writer = new FileWriter(file))
			{
				for (String entry : idList)
				{
					if(entries.get(entry) == null)
						continue;
					writer.write(entries.get(entry).serialize() + "\n");
				}
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void load(MinecraftServer server)
	{
		if(server == null)
			return;
		Path gameDir = server.getSavePath(WorldSavePath.ROOT);
		Path path = Path.of(gameDir.toString(), "config", id.getNamespace(), getFileName());
		File file = new File(path.toUri());
		if(!file.exists())
			save(server);
		try (Scanner reader = new Scanner(file))
		{
			while(reader.hasNextLine())
			{
				String line = reader.nextLine();
				if(line.startsWith("#") || line.isEmpty())
					continue;
				String[] segments = line.split(":");
				if(segments.length != 2 || !entries.containsKey(segments[0]))
					continue;
				entries.get(segments[0]).deserialize(segments[1]);
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static <V> void onChanged(MinecraftServer server, Identifier configID, ConfigEntry<V> entry)
	{
		server.getPlayerManager().getPlayerList().forEach(p -> onChanged(p, configID, entry));
	}
	
	public static <V> void onChanged(ServerPlayerEntity player, Identifier configId, ConfigEntry<V> rule)
	{
		ServerPlayNetworking.send(player, new SyncConfigS2CPayload(configId, rule.getAsNBT()));
	}
	
	public void syncAll(ServerPlayerEntity player)
	{
		entries.values().forEach(i -> ServerPlayNetworking.send(player, new SyncAllConfigS2CPayload(id, getAsNBT())));
		ServerPlayNetworking.send(player, new FinishedSyncConfigS2CPayload(id));
	}
	
	public void syncAll(MinecraftServer server)
	{
		PlayerManager manager = server.getPlayerManager();
		if(manager != null)
			manager.getPlayerList().forEach(this::syncAll);
	}
	
	public static void SyncAll(ServerPlayerEntity player)
	{
		configMap.forEach((id, config) -> config.syncAll(player));
	}
	
	public static Map<Identifier, Config> getAll()
	{
		return configMap;
	}
	
	public static Collection<Identifier> getAllIds()
	{
		return configMap.keySet();
	}
	
	public static void saveAll(MinecraftServer server)
	{
		configMap.values().forEach(i -> i.save(server));
	}
}
