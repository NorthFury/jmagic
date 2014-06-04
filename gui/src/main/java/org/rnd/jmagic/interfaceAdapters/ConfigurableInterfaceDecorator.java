package org.rnd.jmagic.interfaceAdapters;

import org.rnd.jmagic.engine.PlayerInterface;
import org.rnd.jmagic.gui.dialogs.ConfigurationFrame;

import java.util.Properties;
import java.util.SortedSet;

public class ConfigurableInterfaceDecorator extends SimplePlayerInterface implements ConfigurableInterface
{
	private final ConfigurableInterface adapt;

	public ConfigurableInterfaceDecorator(ConfigurableInterface adapt, PlayerInterface decorate)
	{
		super(decorate);
		this.adapt = adapt;
	}

	@Override
	public ConfigurationFrame.OptionPanel getOptionPanel()
	{
		return null;
	}

	@Override
	public SortedSet<ConfigurationFrame.OptionPanel> getOptions()
	{
		return this.adapt.getOptions();
	}

	@Override
	public void setProperties(Properties properties)
	{
		this.adapt.setProperties(properties);
	}
}
