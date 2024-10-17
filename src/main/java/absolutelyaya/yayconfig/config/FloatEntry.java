package absolutelyaya.yayconfig.config;

import net.minecraft.util.Identifier;

public class FloatEntry extends NumberEntry<Float>
{
	public FloatEntry(String id, Float defaultValue)
	{
		super(id, defaultValue);
	}
	
	@Override
	public void deserialize(String value)
	{
		this.value = Float.valueOf(value);
	}
	
	@Override
	public byte getType()
	{
		return FLOAT_TYPE;
	}
	
	public FloatEntry setIcon(Identifier icon)
	{
		this.icon = icon;
		return this;
	}
}
