package absolutelyaya.yayconfig.config;

import net.minecraft.util.Identifier;

public class BooleanEntry extends ConfigEntry<Boolean>
{
	public BooleanEntry(String id, boolean defaultValue)
	{
		super(id, defaultValue);
	}
	
	@Override
	public void deserialize(String value)
	{
		this.value = Boolean.parseBoolean(value);
	}
	
	@Override
	public byte getType()
	{
		return Constants.BOOLEAN_TYPE;
	}
	
	public BooleanEntry setIcon(Identifier icon)
	{
		this.icon = icon;
		return this;
	}
}
