package absolutelyaya.yayconfig.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public abstract class ClientConfig extends AbstractConfig
{
	static final Map<Identifier, ClientConfig> configMap = new LinkedHashMap<>();
	
	public ClientConfig(Identifier id)
	{
		super(id);
		configMap.put(id, this);
	}
	
	public static ClientConfig getFromID(Identifier configID)
	{
		return configMap.get(configID);
	}
	
	public void save(MinecraftClient client)
	{
		if(client == null)
			return;
		Path path = Path.of(client.runDirectory.getPath(), "config", id.getNamespace());
		try
		{
			File dir = path.toFile();
			dir.mkdirs();
			File file = Path.of(dir.getPath(), getFileName()).toFile();
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
	
	public void load(MinecraftClient client)
	{
		if(client == null)
			return;
		Path path = Path.of(client.runDirectory.getPath(), "config", id.getNamespace(), getFileName());
		File file = new File(path.toUri());
		if(!file.exists())
			save(client);
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
	
	public static Map<Identifier, ClientConfig> getAll()
	{
		return configMap;
	}
	
	public static Collection<Identifier> getAllIds()
	{
		return configMap.keySet();
	}
	
	public static void saveAll(MinecraftClient client)
	{
		configMap.values().forEach(i -> i.save(client));
	}
}
