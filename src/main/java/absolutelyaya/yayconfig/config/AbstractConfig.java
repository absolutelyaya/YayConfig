package absolutelyaya.yayconfig.config;

import absolutelyaya.yayconfig.YayConfig;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;

public abstract class AbstractConfig
{
	static final Identifier DEFAULT_BG_TEX = Identifier.of("textures/block/dirt.png");
	
	public final Map<String, ConfigEntry<?>> entries = new LinkedHashMap<>();
	public final List<String> idList = new ArrayList<>();
	final Identifier id;
	
	public AbstractConfig(Identifier id)
	{
		this.id = id;
	}
	
	protected abstract String getFileName();
	
	public EnumEntry<?> set(EnumEntry<?> entry, int ordinal)
	{
		if(!entries.containsKey(entry.getId()))
			return entry;
		if(entries.get(entry.getId()) instanceof EnumEntry<?> enumEntry)
			enumEntry.setValue(ordinal);
		return entry;
	}
	
	@SuppressWarnings("unchecked")
	public <V> ConfigEntry<V> set(String id, V value)
	{
		if(!entries.containsKey(id))
			return null;
		try
		{
			ConfigEntry<V> entry = ((ConfigEntry<V>)entries.get(id));
			entry.setValue(value);
			return entry;
		}
		catch (Exception e)
		{
			YayConfig.LOGGER.error("Exception encountered when trying to set Config Value '{}'", id, e);
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
	
	public Text getTitle()
	{
		return Text.translatable("screen.yayconfig.config-screen.title");
	}
	
	public Identifier getBackgroundTexture()
	{
		return DEFAULT_BG_TEX;
	}
}
