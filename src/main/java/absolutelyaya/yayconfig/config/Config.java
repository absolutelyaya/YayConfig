package absolutelyaya.yayconfig.config;

import absolutelyaya.yayconfig.YayConfig;
import absolutelyaya.yayconfig.networking.FinishedSyncConfigS2CPayload;
import absolutelyaya.yayconfig.networking.SyncAllConfigS2CPayload;
import absolutelyaya.yayconfig.networking.SyncConfigS2CPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public abstract class Config
{
	static final Identifier DEFAULT_BG_TEX = Identifier.of("textures/block/dirt.png");
	static final Map<Identifier, Config> configMap = new HashMap<>();
	
	public final Map<String, ConfigEntry<?>> entries = new HashMap<>();
	public final List<String> idList = new ArrayList<>();
	final Identifier id;
	
	public Config(Identifier id)
	{
		this.id = id;
		configMap.put(id, this);
	}
	
	public static Config getFromID(Identifier configID)
	{
		return configMap.get(configID);
	}
	
	protected abstract String getFileName();
	
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
	
	public EnumEntry<?> set(EnumEntry<?> entry, int ordinal, @Nullable MinecraftServer server)
	{
		if(!entries.containsKey(entry.getId()))
			return entry;
		if(entries.get(entry.getId()) instanceof EnumEntry<?> enumEntry)
			enumEntry.setValue(ordinal);
		if(server != null)
			save(server);
		return entry;
	}
	
	public <V> ConfigEntry<V> set(String id, V value, @Nullable MinecraftServer server)
	{
		if(!entries.containsKey(id))
			return null;
		try
		{
			ConfigEntry<V> entry = ((ConfigEntry<V>)entries.get(id));
			entry.setValue(value);
			if(server != null)
				save(server);
			return entry;
		}
		catch (Exception e)
		{
			YayConfig.LOGGER.error("Exception encountered when trying to set Config Value '{}'", id);
			e.printStackTrace();
		}
		return null;
	}
	
	public NbtCompound getAsNBT()
	{
		NbtCompound nbt = new NbtCompound();
		for (ConfigEntry<?> entry : entries.values())
		{
			if(entry instanceof Comment)
				continue;
			nbt.put(entry.getId(), entry.getAsNBT());
		}
		return nbt;
	}
	
	public ConfigEntry<?> getEntry(String id)
	{
		return entries.get(id);
	}
	
	public void addEntry(ConfigEntry<?> entry)
	{
		if(entry instanceof Comment)
		{
			String id = entry.getId() + entries.values();
			entries.put(id, entry);
			idList.add(id);
		}
		else
		{
			entries.put(entry.getId(), entry);
			idList.add(entry.id);
		}
	}
	
	public Identifier getId()
	{
		return id;
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
	
	public Text getTitle()
	{
		return Text.translatable("screen.yayconfig.config-screen.title");
	}
	
	public Identifier getBackgroundTexture()
	{
		return DEFAULT_BG_TEX;
	}
}
