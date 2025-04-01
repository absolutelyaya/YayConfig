package absolutelyaya.yayconfig.config;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public abstract class ConfigEntry<T> implements Constants
{
	protected final String id;
	protected T value = null, defaultValue;
	protected String translationKey;
	protected Identifier icon;
	
	public ConfigEntry(String id, T defaultValue)
	{
		this.id = id;
		this.defaultValue = defaultValue;
	}
	
	public String getId()
	{
		return id;
	}
	
	public T getValue()
	{
		if(value == null)
			return defaultValue;
		return value;
	}
	
	public void setValue(T value)
	{
		this.value = value;
	}
	
	public String serialize()
	{
		return String.format("%s:%s", id, getValue());
	}
	
	public abstract void deserialize(String value);
	
	public boolean isValid(T v)
	{
		return true;
	}
	
	public String getTranslationKey(String namespace)
	{
		if(translationKey != null && !translationKey.isEmpty())
			return translationKey;
		return String.format("config.%s.%s", namespace, id);
	}
	
	public ConfigEntry<T> setTranslationKey(String key)
	{
		this.translationKey = key;
		return this;
	}
	
	public boolean isDefault()
	{
		return value == null || value.equals(defaultValue);
	}
	
	public abstract byte getType();
	
	public Identifier getIcon()
	{
		return icon;
	}
	
	public NbtCompound getAsNBT()
	{
		NbtCompound nbt = new NbtCompound();
		nbt.putString(RULE_KEY, getId());
		byte type = getType();
		nbt.putByte(TYPE_KEY, type);
		switch(type)
		{
			case BOOLEAN_TYPE -> nbt.putBoolean(VALUE_KEY, (boolean)getValue());
			case INT_TYPE -> nbt.putInt(VALUE_KEY, (int)getValue());
			case FLOAT_TYPE -> nbt.putFloat(VALUE_KEY, (float)getValue());
			case ENUM_TYPE -> nbt.putInt(VALUE_KEY, ((Enum<?>)getValue()).ordinal());
		}
		return nbt;
	}
}
