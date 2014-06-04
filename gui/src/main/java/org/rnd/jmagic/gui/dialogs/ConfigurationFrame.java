package org.rnd.jmagic.gui.dialogs;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class ConfigurationFrame extends JFrame
{
	public abstract static class OptionPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;

		private String name;

		public OptionPanel(String name)
		{
			super(true);

			this.name = name;
		}

		@Override
		public String getName()
		{
			return this.name;
		}

		/**
		 * This is called before the panel is displayed to load the "current"
		 * properties.
		 */
		public abstract void loadSettings(Properties properties);

		/**
		 * This is called when the panel is saved, to write the updated
		 * properties to file.
		 */
		public abstract void saveChanges(Properties properties);
	}

	private static final long serialVersionUID = 1L;

	private Set<OptionPanel> optionPanels;
	private final DefaultListModel listModel;
	private final JPanel content;
	private final Properties properties;

	public ConfigurationFrame(Properties properties)
	{
		super("jMagic Settings");

		this.properties = properties;

		this.optionPanels = new HashSet<OptionPanel>();

		this.listModel = new DefaultListModel();
		final CardLayout contentLayout = new CardLayout();
		this.content = new JPanel(contentLayout);

		JPanel buttonPanel = new JPanel(new FlowLayout());
		{
			JButton applyButton = new JButton(new AbstractAction("Apply")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e)
				{
					ConfigurationFrame.this.save();
				}
			});
			buttonPanel.add(applyButton);

			JButton okButton = new JButton(new AbstractAction("OK")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e)
				{
					ConfigurationFrame.this.save();
					ConfigurationFrame.this.dispose();
				}
			});
			buttonPanel.add(okButton);

			AbstractAction cancelAction = new AbstractAction("Cancel")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e)
				{
					ConfigurationFrame.this.dispose();
				}
			};
			JButton cancelButton = new JButton(cancelAction);
			buttonPanel.add(cancelButton);

			KeyStroke cancelKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
			cancelButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cancelKeyStroke, "Cancel");
			cancelButton.getActionMap().put("Cancel", cancelAction);
		}

		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		right.setPreferredSize(new Dimension(600, 600));
		right.add(this.content);
		right.add(buttonPanel);

		final JList tree = new JList(this.listModel);
		tree.setPreferredSize(new Dimension(150, 600));
		tree.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				String selected = (String)tree.getSelectedValue();

				contentLayout.show(ConfigurationFrame.this.content, selected);
			}
		});
		final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tree, right);

		this.add(split);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	public void addOptionPanel(OptionPanel panel)
	{
		this.optionPanels.add(panel);
		int i;
		for(i = 0; i < this.listModel.size(); ++i)
			if(this.listModel.get(i).toString().compareTo(panel.getName()) > 0)
				break;
		this.listModel.add(i, panel.getName());
		this.content.add(panel, panel.getName());
	}

	public void load()
	{
		for(OptionPanel option: this.optionPanels)
			option.loadSettings(this.properties);
	}

	public void save()
	{
		for(OptionPanel option: this.optionPanels)
			option.saveChanges(this.properties);
	}
}
