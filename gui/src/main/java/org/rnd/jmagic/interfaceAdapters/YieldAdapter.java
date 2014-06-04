package org.rnd.jmagic.interfaceAdapters;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.Maximum;
import org.rnd.jmagic.gui.*;
import org.rnd.jmagic.gui.dialogs.ConfigurationFrame;
import org.rnd.jmagic.sanitized.*;
import org.rnd.util.NumberRange;
import org.rnd.util.StringBooleanTableModel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class YieldAdapter extends SimpleConfigurableInterface
{
	private final class YieldOptionPanel extends ConfigurationFrame.OptionPanel
	{
		private static final long serialVersionUID = 1L;

		private StringBooleanTableModel tableModel;

		public YieldOptionPanel()
		{
			super("Yields");

			this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

			this.tableModel = new StringBooleanTableModel()
			{
				private static final long serialVersionUID = 1L;

				@Override
				public String getCheckboxColumnTitle()
				{
					return "Enabled";
				}

				@Override
				public String getStringColumnTitle()
				{
					return "Yield Pattern";
				}

				@Override
				public boolean validateNewEntry(String value)
				{
					try
					{
						Pattern.compile(value);
						return true;
					}
					catch(PatternSyntaxException e)
					{
						return false;
					}
				}
			};

			final JTable table = new JTable(this.tableModel);
			table.setPreferredScrollableViewportSize(new Dimension(500, 70));
			// Make the second column take up most of the space.
			table.getColumnModel().getColumn(1).setPreferredWidth(450);

			JButton interfaceAdapterRemove = new JButton(new ImageIcon(Start.class.getResource("delete.png")));
			interfaceAdapterRemove.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					YieldOptionPanel.this.tableModel.removeSelected(table);
				}
			});

			Box interfaceAdaptersBox = Box.createHorizontalBox();
			interfaceAdaptersBox.setBorder(BorderFactory.createTitledBorder("Yields"));
			interfaceAdaptersBox.add(new JScrollPane(table));
			interfaceAdaptersBox.add(Box.createHorizontalStrut(5));
			interfaceAdaptersBox.add(interfaceAdapterRemove);

			this.add(interfaceAdaptersBox);
		}

		@Override
		public void loadSettings(Properties properties)
		{
			this.tableModel.clear();
			for(Map.Entry<Pattern, Boolean> entry: YieldAdapter.this.patterns.entrySet())
				this.tableModel.addRow(entry.getKey().pattern(), entry.getValue().booleanValue());
		}

		@Override
		public void saveChanges(Properties properties)
		{
			YieldAdapter.this.patterns.clear();
			for(Map.Entry<String, Boolean> entry: this.tableModel.getData().entrySet())
			{
				YieldAdapter.this.patterns.put(Pattern.compile(entry.getKey()), entry.getValue());
			}
		}
	}

	private Map<Pattern, Boolean> patterns;
	private SanitizedGameState state = null;

	public YieldAdapter(ConfigurableInterface adapt)
	{
		super(adapt);

		this.patterns = new HashMap<Pattern, Boolean>();
		this.state = null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Serializable> List<Integer> choose(ChooseParameters<T> parameterObject)
	{
		if(parameterObject.type == PlayerInterface.ChoiceType.NORMAL_ACTIONS && this.state != null)
		{
			SanitizedZone stack = (SanitizedZone)this.state.get(this.state.stack);
			if(stack != null && !stack.objects.isEmpty())
			{
				SanitizedIdentified topOfStack = this.state.get(stack.objects.get(0));
				if(topOfStack != null)
				{
					String name = topOfStack.toString();
					for(Map.Entry<Pattern, Boolean> entry: this.patterns.entrySet())
						if(entry.getValue().booleanValue() && entry.getKey().matcher(name).matches())
							return Collections.emptyList();
					int yieldIndex = parameterObject.choices.size();

					ChooseParameters<T> newParameters = new ChooseParameters<T>(parameterObject);
					if(Maximum.get(newParameters.number) == 0)
						newParameters.number = new MagicSet(new NumberRange(0, 1));
					newParameters.choices.add((T)new SanitizedPlayerAction("Always yield to this.", topOfStack.ID, topOfStack.ID));
					List<Integer> ret = super.choose(newParameters);
					if(ret.contains(yieldIndex))
					{
						String pattern = Pattern.quote(name);
						this.patterns.put(Pattern.compile(pattern), true);
						return Collections.emptyList();
					}
					return ret;
				}
			}
		}

		return super.choose(parameterObject);
	}

	@Override
	public void alertState(SanitizedGameState sanitizedGameState)
	{
		this.state = sanitizedGameState;
		super.alertState(sanitizedGameState);
	}

	@Override
	public ConfigurationFrame.OptionPanel getOptionPanel()
	{
		return new YieldOptionPanel();
	}
}
