package absolutelyaya.yayconfig.config;

import absolutelyaya.yayconfig.YayConfig;
import net.minecraft.util.Identifier;

public class EnumEntry<E extends Enum<E>> extends ConfigEntry<Enum<E>>
{
	protected final Class<E> valueClass;
	protected E[] options;
	
	public EnumEntry(String id, E defaultValue)
	{
		super(id, defaultValue);
		valueClass = defaultValue.getDeclaringClass();
		options = valueClass.getEnumConstants();
	}
	
	@Override
	public void deserialize(String value)
	{
		E v = E.valueOf(valueClass, value);
		this.value = isValid(v) ? v : defaultValue;
	}
	
	public EnumEntry<E> setValidOptions(E[] options)
	{
		this.options = options;
		return this;
	}
	
	public E[] getValidOptions()
	{
		return options;
	}
	
	@Override
	public boolean isValid(Enum<E> v)
	{
		if(options == null)
			return true;
		for (E option : options)
			if(v.equals(option))
				return true;
		return false;
	}
	
	@Override
	public E getValue()
	{
		return valueClass.cast(super.getValue());
	}
	
	@Override
	public void setValue(Enum<E> value)
	{
		for (E e : valueClass.getEnumConstants())
		{
			if (e.equals(value))
			{
				super.setValue(e);
				return;
			}
		}
		YayConfig.LOGGER.warn("Enum Rule '{}' couldn't resolve new value '{}'", id, value);
	}
	
	public EnumEntry<?> setValue(int i)
	{
		super.setValue(getValidOptions()[i]);
		return this;
	}
	
	@Override
	public byte getType()
	{
		return ENUM_TYPE;
	}
	
	public EnumEntry<E> setIcon(Identifier icon)
	{
		this.icon = icon;
		return this;
	}
}
