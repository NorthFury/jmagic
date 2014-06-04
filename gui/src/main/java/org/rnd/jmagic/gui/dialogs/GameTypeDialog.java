package org.rnd.jmagic.gui.dialogs;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.gameTypes.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameTypeDialog extends JDialog
{
	private static class BeanConfiguration<T>
	{
		public final Class<? extends T> beanClass;
		public final String name;
		public final Map<String, PropertyConfiguration> properties;

		/**
		 * Create a new configuration for a Java bean with an optional
		 * {@link Description} annotation.
		 * 
		 * @param beanClass The bean class extending {@link T}
		 */
		public BeanConfiguration(Class<? extends T> beanClass)
		{
			this.beanClass = beanClass;
			this.name = beanClass.getAnnotation(Name.class).value();
			this.properties = new HashMap<String, PropertyConfiguration>();

			try
			{
				// Don't check for properties provided by Object
				for(final PropertyDescriptor propertyDescriptor: Introspector.getBeanInfo(this.beanClass, Object.class).getPropertyDescriptors())
				{
					// Don't bother with properties that can't be changed
					Method writeMethod = propertyDescriptor.getWriteMethod();
					if(null == writeMethod)
						continue;

					String propertyName = propertyDescriptor.getName();
					PropertyConfiguration propertyConfiguration = PropertyConfiguration.create(propertyDescriptor);
					this.properties.put(propertyName, propertyConfiguration);
				}
			}
			catch(IntrospectionException e)
			{
				LOG.log(Level.SEVERE, "Error introspecting bean " + this.beanClass, e);
			}
		}

		public void clear()
		{
			for(PropertyConfiguration component: this.properties.values())
				component.clear();
		}

		public T getValue()
		{
			// Declare this up here so exceptions know which property was being
			// worked on
			String propertyName = null;

			try
			{
				T ret = this.beanClass.newInstance();

				// Don't check for properties provided by Object
				for(PropertyDescriptor propertyDescriptor: Introspector.getBeanInfo(this.beanClass, Object.class).getPropertyDescriptors())
				{
					// Don't bother with properties that can't be changed
					Method writeMethod = propertyDescriptor.getWriteMethod();
					if(null == writeMethod)
						continue;

					propertyName = propertyDescriptor.getName();
					if(!this.properties.containsKey(propertyName))
					{
						LOG.severe("Bean configuration for " + this.beanClass + " does not have configuration for property " + propertyName);
						return null;
					}

					Object value = this.properties.get(propertyName).getValue();
					if(null == value)
						return null;
					writeMethod.invoke(ret, value);
				}

				return ret;
			}
			catch(IllegalAccessException e)
			{
				LOG.log(Level.SEVERE, "Bean " + this.beanClass + " default constructor is not public", e);
			}
			catch(InstantiationException e)
			{
				LOG.log(Level.SEVERE, "Bean " + this.beanClass + " isn't constructable", e);
			}
			catch(IntrospectionException e)
			{
				LOG.log(Level.SEVERE, "Error introspecting bea  " + this.beanClass, e);
			}
			catch(InvocationTargetException e)
			{
				LOG.log(Level.SEVERE, "Bean " + this.beanClass + " property " + propertyName + " write method threw exception", e.getCause());
			}

			return null;
		}

		/**
		 * Set the properties of this bean configuration to the values in the
		 * given bean instance.
		 */
		public void setValue(T beanValue)
		{
			// Declare this up here so exceptions know which property was being
			// worked on
			String propertyName = null;

			try
			{
				// Don't check for properties provided by Object
				for(PropertyDescriptor propertyDescriptor: Introspector.getBeanInfo(this.beanClass, Object.class).getPropertyDescriptors())
				{
					if(null == propertyDescriptor.getWriteMethod())
						continue;

					propertyName = propertyDescriptor.getName();
					if(!this.properties.containsKey(propertyName))
					{
						LOG.severe("Bean configuration for " + this.beanClass + " does not have configuration for property " + propertyName);
						continue;
					}

					Method readMethod = propertyDescriptor.getReadMethod();
					if(null != readMethod)
						this.properties.get(propertyName).setValue(readMethod.invoke(beanValue));
				}
			}
			catch(IntrospectionException ex)
			{
				LOG.log(Level.SEVERE, "Error introspecting bean " + this.beanClass, ex);
			}
			catch(InvocationTargetException ex)
			{
				LOG.log(Level.SEVERE, "Bean " + this.beanClass + " property " + propertyName + " read method threw exception", ex.getCause());
			}
			catch(IllegalAccessException ex)
			{
				LOG.log(Level.SEVERE, "Bean " + this.beanClass + " property " + propertyName + " read method is not public", ex);
			}
		}

		@Override
		public String toString()
		{
			return this.name;
		}
	}

	private static class BooleanPropertyConfiguration extends PropertyConfiguration
	{
		private JCheckBox checkBox;

		public BooleanPropertyConfiguration(String propertyName, Class<?> propertyType)
		{
			super(propertyName, propertyType);

			this.checkBox = new JCheckBox(this.propertyName + " (" + this.propertyType + ")");
			GridBagConstraints textConstraints = new GridBagConstraints();
			textConstraints.fill = GridBagConstraints.HORIZONTAL;
			textConstraints.gridx = 0;
			textConstraints.gridy = 0;
			textConstraints.weightx = 1;
			this.panel.add(this.checkBox, textConstraints);
		}

		@Override
		public void clear()
		{
			this.checkBox.setSelected(false);
		}

		@Override
		public Boolean getValue()
		{
			return this.checkBox.isSelected();
		}

		@Override
		public void setValue(Object value)
		{
			this.checkBox.setSelected((Boolean)value);
		}
	}

	private static class ComplexPropertyConfiguration extends PropertyConfiguration
	{
		private static String COMBO_BOX_NO_VALUE = "Choose an implementation";

		private JComboBox implementationCombo;
		private Map<Class<?>, BeanConfiguration<Object>> implementationConfigurations;

		public ComplexPropertyConfiguration(String propertyName, Class<?> propertyType)
		{
			super(propertyName, propertyType);
			this.implementationConfigurations = new HashMap<Class<?>, BeanConfiguration<Object>>();

			this.panel.setBorder(new TitledBorder(""));

			this.implementationCombo = new JComboBox();
			GridBagConstraints comboConstraints = new GridBagConstraints();
			comboConstraints.fill = GridBagConstraints.HORIZONTAL;
			comboConstraints.gridx = 0;
			comboConstraints.gridy = 0;
			comboConstraints.weightx = 1;
			this.panel.add(this.implementationCombo, comboConstraints);

			final CardLayout implementationLayout = new CardLayout();
			final JPanel implementationCards = new JPanel(implementationLayout);
			GridBagConstraints cardsConstraints = new GridBagConstraints();
			cardsConstraints.fill = GridBagConstraints.BOTH;
			cardsConstraints.gridx = 0;
			cardsConstraints.gridy = 1;
			cardsConstraints.weightx = 1;
			cardsConstraints.weighty = 1;
			this.panel.add(implementationCards, cardsConstraints);

			this.implementationCombo.addItem(COMBO_BOX_NO_VALUE);
			implementationCards.add(new JPanel(), "");

			// TODO: This obviously won't work for anything that isn't in
			// this package, so make this more general
			for(Class<?> c: GameTypeDialog.findImplementations(this.propertyType, "org.rnd.jmagic.engine.gameTypes.packWars"))
			{
				BeanConfiguration<Object> implementationConfiguration = new BeanConfiguration<Object>(c);
				this.implementationConfigurations.put(c, implementationConfiguration);
				this.implementationCombo.addItem(implementationConfiguration);

				JPanel implementationPanel = new JPanel(new GridBagLayout());
				int y = 0;

				for(PropertyConfiguration propertyConfiguration: implementationConfiguration.properties.values())
				{
					JPanel propertyPanel = propertyConfiguration.getPanel();
					GridBagConstraints propertyConstraints = new GridBagConstraints();
					propertyConstraints.fill = GridBagConstraints.HORIZONTAL;
					propertyConstraints.gridx = 0;
					propertyConstraints.gridy = y++;
					propertyConstraints.weightx = 1;
					implementationPanel.add(propertyPanel, propertyConstraints);
				}

				implementationCards.add(implementationPanel, implementationConfiguration.name);
			}

			this.implementationCombo.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					for(BeanConfiguration<Object> c: ComplexPropertyConfiguration.this.implementationConfigurations.values())
						c.clear();
					implementationLayout.show(implementationCards, ComplexPropertyConfiguration.this.implementationCombo.getSelectedItem().toString());
					implementationCards.revalidate();
				}
			});
		}

		@Override
		public void clear()
		{
			this.implementationCombo.setSelectedItem("");
			for(BeanConfiguration<?> c: this.implementationConfigurations.values())
				c.clear();
		}

		@Override
		@SuppressWarnings("unchecked")
		public Object getValue()
		{
			Object selectedItem = this.implementationCombo.getSelectedItem();
			if(selectedItem.equals(COMBO_BOX_NO_VALUE))
			{
				LOG.warning(this.propertyName + " must have an implementation selected to get a value from");
				return null;
			}
			return ((BeanConfiguration<Object>)(this.implementationCombo.getSelectedItem())).getValue();
		}

		@Override
		public void setValue(Object value)
		{
			Class<?> valueClass = value.getClass();
			if(!this.implementationConfigurations.containsKey(valueClass))
			{
				LOG.severe("Complex property " + this.propertyName + " doesn't contain implementation configuration for value of type " + valueClass);
				return;
			}

			for(Map.Entry<Class<?>, BeanConfiguration<Object>> e: this.implementationConfigurations.entrySet())
			{
				if(e.getKey().equals(valueClass))
				{
					BeanConfiguration<Object> implementationConfiguration = e.getValue();
					this.implementationCombo.setSelectedItem(implementationConfiguration);
					implementationConfiguration.setValue(value);
				}
				else
					e.getValue().clear();
			}
		}
	}

	private static class EnumerationPropertyConfiguration extends PropertyConfiguration
	{
		private JComboBox comboBox;

		public EnumerationPropertyConfiguration(String propertyName, Class<?> propertyType)
		{
			super(propertyName, propertyType);
			@SuppressWarnings("unchecked") Class<Enum<?>> enumType = (Class<Enum<?>>)propertyType;

			try
			{
				this.comboBox = new JComboBox((Object[])(enumType.getMethod("values").invoke(null)));
				GridBagConstraints textConstraints = new GridBagConstraints();
				textConstraints.anchor = GridBagConstraints.LINE_START;
				textConstraints.gridx = 0;
				textConstraints.gridy = 0;
				this.panel.add(this.comboBox, textConstraints);

				JLabel label = new JLabel(this.propertyName + " (" + this.propertyType + ")");
				GridBagConstraints labelConstraints = new GridBagConstraints();
				labelConstraints.fill = GridBagConstraints.HORIZONTAL;
				labelConstraints.gridx = 1;
				labelConstraints.gridy = 0;
				labelConstraints.weightx = 1;
				this.panel.add(label, labelConstraints);
			}
			catch(NoSuchMethodException e)
			{
				LOG.log(Level.SEVERE, enumType + " is supposed to be an enumeration type but doesn't have a values() method", e);
			}
			catch(IllegalArgumentException e)
			{
				LOG.log(Level.SEVERE, " null is not a legal argument to " + enumType + " values() method", e);
			}
			catch(SecurityException e)
			{
				LOG.log(Level.SEVERE, enumType + " values() method is not allowed to be called", e);
			}
			catch(IllegalAccessException e)
			{
				LOG.log(Level.SEVERE, enumType + " values() method is not public", e);
			}
			catch(InvocationTargetException e)
			{
				LOG.log(Level.SEVERE, enumType + " values() threw an exception", e.getCause());
			}
		}

		@Override
		public void clear()
		{
			this.comboBox.setSelectedIndex(-1);
		}

		@Override
		public Object getValue()
		{
			return this.comboBox.getSelectedItem();
		}

		@Override
		public void setValue(Object value)
		{
			this.comboBox.setSelectedItem(value);
		}
	}

	private static class IndexedPropertyConfiguration extends PropertyConfiguration
	{
		private final LinkedHashMap<JPanel, PropertyConfiguration> individuals;

		public IndexedPropertyConfiguration(String propertyName, Class<?> indexedPropertyType)
		{
			super(propertyName, indexedPropertyType);
			this.individuals = new LinkedHashMap<JPanel, PropertyConfiguration>();

			rebuildPanel();
		}

		@Override
		public void clear()
		{
			this.individuals.clear();
			rebuildPanel();
		}

		private PropertyConfiguration createNewIndividualConfiguration()
		{
			PropertyConfiguration individual = PropertyConfiguration.create(this.propertyName + "[" + this.individuals.size() + "]", this.propertyType);
			this.individuals.put(individual.getPanel(), individual);
			return individual;
		}

		@Override
		public Object[] getValue()
		{
			Object[] ret = (Object[]) Array.newInstance(this.propertyType, this.individuals.size());
			int i = 0;
			for(PropertyConfiguration p: this.individuals.values())
			{
				Object value = p.getValue();
				if(null == value)
					return null;

				ret[i] = value;
				++i;
			}
			return ret;
		}

		private void rebuildPanel()
		{
			this.panel.removeAll();
			int y = 0;

			JButton addButton = new JButton("Add to " + this.propertyName);
			GridBagConstraints addConstraints = new GridBagConstraints();
			addConstraints.anchor = GridBagConstraints.LINE_START;
			addConstraints.gridx = 0;
			addConstraints.gridy = y++;
			addConstraints.weightx = 1;
			this.panel.add(addButton, addConstraints);

			addButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					IndexedPropertyConfiguration.this.createNewIndividualConfiguration();
					IndexedPropertyConfiguration.this.rebuildPanel();
				}
			});

			for(final JPanel panel: this.individuals.keySet())
			{
				GridBagConstraints panelConstraints = new GridBagConstraints();
				panelConstraints.fill = GridBagConstraints.HORIZONTAL;
				panelConstraints.gridx = 0;
				panelConstraints.gridy = y;
				panelConstraints.weightx = 1;
				this.panel.add(panel, panelConstraints);

				JButton removeButton = new JButton("Remove");
				GridBagConstraints removeConstraints = new GridBagConstraints();
				removeConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
				removeConstraints.gridx = 1;
				removeConstraints.gridy = y;
				this.panel.add(removeButton, removeConstraints);

				y++;

				removeButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						IndexedPropertyConfiguration.this.individuals.remove(panel);
						IndexedPropertyConfiguration.this.rebuildPanel();
					}
				});
			}

			this.panel.revalidate();
		}

		@Override
		public void setValue(Object value)
		{
			if(!(value instanceof Object[]))
				throw new UnsupportedOperationException("Can't set an indexed property with a non-array");

			this.individuals.clear();

			Object[] array = (Object[])value;
			for(int i = 0; i < array.length; ++i)
				this.createNewIndividualConfiguration().setValue(array[i]);
			this.rebuildPanel();
		}
	}

	private static class IndexedStringPropertyConfiguration extends PropertyConfiguration
	{
		private final JTextArea textArea;

		public IndexedStringPropertyConfiguration(String propertyName)
		{
			super(propertyName, String.class);

			this.textArea = new JTextArea(5, 30);
			GridBagConstraints textConstraints = new GridBagConstraints();
			textConstraints.anchor = GridBagConstraints.LINE_START;
			textConstraints.gridx = 0;
			textConstraints.gridy = 0;
			this.panel.add(this.textArea, textConstraints);

			JLabel label = new JLabel(this.propertyName + " (" + this.propertyType + ") delimited by line-breaks");
			GridBagConstraints labelConstraints = new GridBagConstraints();
			labelConstraints.fill = GridBagConstraints.HORIZONTAL;
			labelConstraints.gridx = 1;
			labelConstraints.gridy = 0;
			labelConstraints.weightx = 1;
			this.panel.add(label, labelConstraints);
		}

		@Override
		public void clear()
		{
			this.textArea.setText("");
		}

		@Override
		public String[] getValue()
		{
			return this.textArea.getText().split("\n");
		}

		@Override
		public void setValue(Object value)
		{
			if(!(value instanceof String[]))
				throw new UnsupportedOperationException("Can't set an indexed property with a non-array");

			String[] array = (String[])value;
			StringBuilder text = new StringBuilder();
			for(int i = 0; i < array.length; ++i)
			{
				if(0 != text.length())
					text.append("\n");
				text.append(array[i]);
			}
			this.textArea.setText(text.toString());
		}
	}

	private static class IntegerPropertyConfiguration extends PropertyConfiguration
	{
		private JTextField textBox;

		public IntegerPropertyConfiguration(String propertyName, Class<?> propertyType)
		{
			super(propertyName, propertyType);

			this.textBox = new JTextField(8);
			GridBagConstraints textConstraints = new GridBagConstraints();
			textConstraints.anchor = GridBagConstraints.LINE_START;
			textConstraints.gridx = 0;
			textConstraints.gridy = 0;
			this.panel.add(this.textBox, textConstraints);

			JLabel label = new JLabel(this.propertyName + " (" + this.propertyType + ")");
			GridBagConstraints labelConstraints = new GridBagConstraints();
			labelConstraints.fill = GridBagConstraints.HORIZONTAL;
			labelConstraints.gridx = 1;
			labelConstraints.gridy = 0;
			labelConstraints.weightx = 1;
			this.panel.add(label, labelConstraints);
		}

		@Override
		public void clear()
		{
			this.textBox.setText("");
		}

		@Override
		public Object getValue()
		{
			try
			{
				return Integer.parseInt(this.textBox.getText());
			}
			catch(NumberFormatException e)
			{
				LOG.log(Level.WARNING, this.propertyName + " must have a valid number value", e);
				return null;
			}
		}

		@Override
		public void setValue(Object value)
		{
			this.textBox.setText(value.toString());
		}
	}

	private abstract static class PropertyConfiguration
	{
		public static PropertyConfiguration create(PropertyDescriptor descriptor)
		{
			if(descriptor instanceof IndexedPropertyDescriptor)
			{
				Class<?> type = ((IndexedPropertyDescriptor)descriptor).getIndexedPropertyType();
				if(String.class.equals(type))
					return new IndexedStringPropertyConfiguration(descriptor.getName());
				return new IndexedPropertyConfiguration(descriptor.getName(), type);
			}
			return create(descriptor.getName(), descriptor.getPropertyType());
		}

		public static PropertyConfiguration create(String propertyName, Class<?> propertyType)
		{
			if(Boolean.class.equals(propertyType) || boolean.class.equals(propertyType))
				return new BooleanPropertyConfiguration(propertyName, propertyType);
			if(Enum.class.isAssignableFrom(propertyType))
				return new EnumerationPropertyConfiguration(propertyName, propertyType);
			if(Integer.class.equals(propertyType) || int.class.equals(propertyType))
				return new IntegerPropertyConfiguration(propertyName, propertyType);
			if(String.class.equals(propertyType))
				return new StringPropertyConfiguration(propertyName, propertyType);
			return new ComplexPropertyConfiguration(propertyName, propertyType);
		}

		protected final JPanel panel;
		protected final String propertyName;
		protected final Class<?> propertyType;

		public PropertyConfiguration(String propertyName, Class<?> propertyType)
		{
			this.panel = new JPanel(new GridBagLayout());
			this.propertyName = propertyName;
			this.propertyType = propertyType;
		}

		/**
		 * Clear any controls on the panel returned by {@link #getPanel()}.
		 */
		public abstract void clear();

		/**
		 * Get the panel with controls to change the state of this property.
		 */
		public final JPanel getPanel()
		{
			return this.panel;
		}

		/**
		 * Get the value of this property based on the state of the controls on
		 * the panel returned by {@link #getPanel()}.
		 * 
		 * @return Non-{@code null} for a valid value, {@code null} if an error
		 * occurred and a value can't be returned
		 */
		public abstract Object getValue();

		/**
		 * Set the state of controls on the panel returned by {@link #getPanel}
		 * to correspond to a value.
		 */
		public abstract void setValue(Object value);
	}

	private static class RuleConfiguration extends BeanConfiguration<GameType.GameTypeRule>
	{
		// TODO: Can't this be set by the platform look-and-feel?
		private static final int SCROLL_INCREMENT = 5;
		public final JToggleButton checkBox;
		public final DefaultMutableTreeNode node;
		public final JScrollPane scrollPane;

		public RuleConfiguration(Class<? extends GameType.GameTypeRule> ruleClass)
		{
			super(ruleClass);
			this.node = new DefaultMutableTreeNode(this.name);

			JPanel panel = new JPanel(new GridBagLayout());
			int y = 0;

			this.checkBox = new JCheckBox("Enable");
			GridBagConstraints enableConstraints = new GridBagConstraints();
			enableConstraints.fill = GridBagConstraints.HORIZONTAL;
			enableConstraints.gridx = 0;
			enableConstraints.gridy = y++;
			enableConstraints.weightx = 1;
			panel.add(this.checkBox, enableConstraints);

			for(PropertyConfiguration propertyConfiguration: this.properties.values())
			{
				JPanel propertyPanel = propertyConfiguration.getPanel();
				GridBagConstraints propertyConstraints = new GridBagConstraints();
				propertyConstraints.fill = GridBagConstraints.HORIZONTAL;
				propertyConstraints.gridx = 0;
				propertyConstraints.gridy = y++;
				propertyConstraints.weightx = 1;

				panel.add(propertyPanel, propertyConstraints);
			}

			// Create a description panel
			String description = this.beanClass.getAnnotation(Description.class).value();
			JTextPane descriptionPanel = new JTextPane();
			descriptionPanel.setEditable(false);
			descriptionPanel.setText(description);
			GridBagConstraints descriptionConstraints = new GridBagConstraints();
			descriptionConstraints.fill = GridBagConstraints.BOTH;
			descriptionConstraints.gridx = 0;
			descriptionConstraints.gridy = y++;
			descriptionConstraints.weightx = 1;
			descriptionConstraints.weighty = 1;
			panel.add(descriptionPanel, descriptionConstraints);

			this.scrollPane = new JScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			this.scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_INCREMENT);
		}

		@Override
		public void clear()
		{
			this.checkBox.setSelected(false);
			super.clear();
		}

		@Override
		public void setValue(GameType.GameTypeRule beanValue)
		{
			this.checkBox.setSelected(true);
			super.setValue(beanValue);
		}
	}

	private static class StringPropertyConfiguration extends PropertyConfiguration
	{
		private JTextField textBox;

		public StringPropertyConfiguration(String propertyName, Class<?> propertyType)
		{
			super(propertyName, propertyType);

			this.textBox = new JTextField(8);
			GridBagConstraints textConstraints = new GridBagConstraints();
			textConstraints.anchor = GridBagConstraints.LINE_START;
			textConstraints.gridx = 0;
			textConstraints.gridy = 0;
			this.panel.add(this.textBox, textConstraints);

			JLabel label = new JLabel(this.propertyName + " (" + this.propertyType + ")");
			GridBagConstraints labelConstraints = new GridBagConstraints();
			labelConstraints.fill = GridBagConstraints.HORIZONTAL;
			labelConstraints.gridx = 1;
			labelConstraints.gridy = 0;
			labelConstraints.weightx = 1;
			this.panel.add(label, labelConstraints);
		}

		@Override
		public void clear()
		{
			this.textBox.setText("");
		}

		@Override
		public Object getValue()
		{
			return this.textBox.getText();
		}

		@Override
		public void setValue(Object value)
		{
			this.textBox.setText(value.toString());
		}
	}

	private static final Logger LOG = Logger.getLogger("org.rnd.jmagic.gui.dialogs.GameTypeDialog");

	private static final long serialVersionUID = 1L;

	private static <T> SortedSet<Class<? extends T>> findImplementations(Class<T> rootType, String pkg)
	{
		return findImplementations(rootType, pkg, null);
	}

	private static <T> SortedSet<Class<? extends T>> findImplementations(Class<T> rootType, String pkg, Class<? extends Annotation> ignoreAnnotation)
	{
		SortedSet<Class<? extends T>> ret = new TreeSet<Class<? extends T>>(new Comparator<Class<? extends T>>()
		{
			@Override
			public int compare(Class<? extends T> o1, Class<? extends T> o2)
			{
				return o1.getAnnotation(Name.class).value().compareTo(o2.getAnnotation(Name.class).value());
			}
		});

		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AssignableTypeFilter(rootType));
		if(ignoreAnnotation != null)
			scanner.addExcludeFilter(new AnnotationTypeFilter(ignoreAnnotation));

		for(BeanDefinition bean: scanner.findCandidateComponents(pkg))
		{
			if((bean instanceof ScannedGenericBeanDefinition) //
					&& ((ScannedGenericBeanDefinition)bean).getMetadata().isConcrete())
			{
				try
				{
					@SuppressWarnings("unchecked") Class<? extends T> c = (Class<? extends T>)(Class.forName(bean.getBeanClassName()));
					ret.add(c);
				}
				catch(ClassNotFoundException e)
				{
					LOG.log(Level.SEVERE, "Error loading class from scanned game-type rules", e);
				}
			}
		}

		return ret;
	}

	private GameType gameType;

	private String gameTypeName;

	private Map<Class<? extends GameType.GameTypeRule>, RuleConfiguration> ruleConfiguration;

	private JComboBox presetSelector;

	public GameTypeDialog(JFrame parent, Set<GameType> presets)
	{
		super(parent, "jMagic Game Type", true);
		this.gameType = null;
		this.gameTypeName = "Custom";
		this.ruleConfiguration = new HashMap<Class<? extends GameType.GameTypeRule>, RuleConfiguration>();
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				GameTypeDialog.this.gameType = null;
				GameTypeDialog.this.setVisible(false);
			}
		});

		this.presetSelector = new JComboBox();
		this.presetSelector.addItem("");
		for(GameType preset: presets)
			this.presetSelector.addItem(preset);
		this.presetSelector.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				for(RuleConfiguration rule: GameTypeDialog.this.ruleConfiguration.values())
					rule.clear();

				if((ItemEvent.SELECTED == e.getStateChange()) && !e.getItem().equals(""))
					GameTypeDialog.this.setGameType(e.getItem());
			}
		});

		JButton saveAsNewBaseButton = new JButton("Save as new base");
		saveAsNewBaseButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String customName = JOptionPane.showInputDialog("What name do you want this game type to have (an empty name will cancel)?");
				if(customName == null || (0 == customName.length()))
					return;

				GameTypeDialog.this.gameTypeName = customName;
				GameType ret = GameTypeDialog.this.createGameType();
				GameTypeDialog.this.presetSelector.addItem(ret);
				GameTypeDialog.this.presetSelector.setSelectedItem(ret);
			}
		});

		JPanel topPanel = new JPanel();
		topPanel.add(new JLabel("Base on:"), BorderLayout.LINE_START);
		topPanel.add(this.presetSelector);
		topPanel.add(saveAsNewBaseButton);
		this.add(topPanel, BorderLayout.PAGE_START);

		final CardLayout centerLayout = new CardLayout();
		final JPanel centerPanel = new JPanel(centerLayout);

		String rootName = "Options";
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootName);

		JTextPane rootDescriptionPanel = new JTextPane();
		rootDescriptionPanel.setEditable(false);
		rootDescriptionPanel.setText("These are the possible options to build a game-type for jMagic.");
		centerPanel.add(rootDescriptionPanel, rootName);

		for(Class<? extends GameType.GameTypeRule> rule: findImplementations(GameType.GameTypeRule.class, "org.rnd.jmagic.engine.gameTypes", GameType.Ignore.class))
			createRuleConfiguration(rule, root, centerPanel);

		this.add(centerPanel, BorderLayout.CENTER);

		final JTree ruleTree = new JTree(root);
		ruleTree.addTreeSelectionListener(new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				centerLayout.show(centerPanel, ruleTree.getLastSelectedPathComponent().toString());
			}
		});
		ruleTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.add(new JScrollPane(ruleTree), BorderLayout.LINE_START);

		JButton closeButton = new JButton("OK");
		closeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				GameType ret = GameTypeDialog.this.createGameType();
				if(null != ret)
				{
					GameTypeDialog.this.gameType = ret;
					GameTypeDialog.this.setVisible(false);
				}
			}
		});

		AbstractAction cancelAction = new AbstractAction("Cancel")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				GameTypeDialog.this.dispose();
			}
		};
		JButton cancelButton = new JButton(cancelAction);

		KeyStroke cancelKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		cancelButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cancelKeyStroke, "Cancel");
		cancelButton.getActionMap().put("Cancel", cancelAction);

		JPanel bottomPanel = new JPanel();
		bottomPanel.add(closeButton);
		bottomPanel.add(cancelButton);
		this.add(bottomPanel, BorderLayout.PAGE_END);

		this.pack();
	}

	private void createRuleConfiguration(Class<? extends GameType.GameTypeRule> ruleClass, DefaultMutableTreeNode root, JPanel panelContainer)
	{
		// Don't create configuration for this rule if we've already created it
		if(this.ruleConfiguration.containsKey(ruleClass))
			return;

		RuleConfiguration config = new RuleConfiguration(ruleClass);

		// Add the option to the tree of options
		DependsOn dependsOn = ruleClass.getAnnotation(DependsOn.class);
		if(null != dependsOn)
		{
			Class<? extends GameType.GameTypeRule> parentOption = dependsOn.value();
			if(!this.ruleConfiguration.containsKey(parentOption))
				createRuleConfiguration(parentOption, root, panelContainer);
			this.ruleConfiguration.get(parentOption).node.add(config.node);
		}
		else
			root.add(config.node);

		this.ruleConfiguration.put(ruleClass, config);

		panelContainer.add(config.scrollPane, config.name);
	}

	private GameType createGameType()
	{
		GameType ret = new GameType(GameTypeDialog.this.gameTypeName);
		Set<Class<?>> rulesAlreadyCreated = new HashSet<Class<?>>();
		for(Class<? extends GameType.GameTypeRule> ruleClass: GameTypeDialog.this.ruleConfiguration.keySet())
			if(!createRule(ret, ruleClass, rulesAlreadyCreated))
				return null;

		return ret;
	}

	private boolean createRule(GameType gameType, Class<? extends GameType.GameTypeRule> ruleClass, Set<Class<?>> rulesAlreadyCreated)
	{
		// Don't create rules that are already created
		if(rulesAlreadyCreated.contains(ruleClass))
			return true;

		RuleConfiguration config = this.ruleConfiguration.get(ruleClass);

		// Don't create any rule that isn't selected
		if(!config.checkBox.isSelected())
			return true;

		DependsOn dependsOn = ruleClass.getAnnotation(DependsOn.class);
		if(null != dependsOn)
		{
			Class<? extends GameType.GameTypeRule> parentOption = dependsOn.value();
			if(!rulesAlreadyCreated.contains(parentOption))
				createRule(gameType, parentOption, rulesAlreadyCreated);
		}
		rulesAlreadyCreated.add(ruleClass);

		GameType.GameTypeRule value = config.getValue();
		if(null == value)
			return false;

		gameType.addRule(value);
		return true;
	}

	public GameType getGameType()
	{
		return this.gameType;
	}

	private void setGameType(Object object)
	{
		GameType gameType = (GameType)object;
		for(GameType.GameTypeRule rule: gameType.getRules())
		{
			Class<? extends GameType.GameTypeRule> ruleClass = rule.getClass();
			if(!GameTypeDialog.this.ruleConfiguration.containsKey(ruleClass))
			{
				LOG.severe(ruleClass + " missing from possible rules to configure");
				continue;
			}

			this.ruleConfiguration.get(ruleClass).setValue(rule);
		}
	}
}
