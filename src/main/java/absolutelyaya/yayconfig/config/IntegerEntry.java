package absolutelyaya.yayconfig.config;

import net.minecraft.util.Identifier;

public class IntegerEntry extends NumberEntry<Integer>
{
	public IntegerEntry(String id, Integer defaultValue)
	{
		super(id, defaultValue);
	}
	
	@Override
	public void deserialize(String value)
	{
		this.value = Integer.parseInt(value);
	}
	
	@Override
	public byte getType()
	{
		return INT_TYPE;
	}
	
	public IntegerEntry setIcon(Identifier icon)
	{
		this.icon = icon;
		return this;
	}
}
