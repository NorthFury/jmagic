package org.rnd.jmagic.interfaceAdapters;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.gui.dialogs.ConfigurationFrame;

import java.util.Properties;
import java.util.SortedSet;

public interface ConfigurableInterface extends PlayerInterface
{
	public ConfigurationFrame.OptionPanel getOptionPanel();

	public SortedSet<ConfigurationFrame.OptionPanel> getOptions();

	public void setProperties(Properties properties);
}