package org.rnd.jmagic.gui.dialogs;

import org.rnd.jmagic.gui.*;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class TextChoicePanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private Play gui;
	private List<Object> choices;
	private List<Object> chosen;
	private boolean chooseExactlyOne;
	private JButton doneButton;

	public <T> TextChoicePanel(Play gui, List<T> choices, Comparator<T> comparator, boolean chooseExactlyOne)
	{
		super(new BorderLayout());

		this.gui = gui;
		this.choices = new LinkedList<Object>(choices);
		if(chooseExactlyOne)
			this.chosen = null;
		else
			this.chosen = new LinkedList<Object>();
		this.chooseExactlyOne = chooseExactlyOne;

		Box choiceBox = Box.createVerticalBox();
		ButtonGroup group = (chooseExactlyOne ? new ButtonGroup() : null);

		List<T> sorted = new LinkedList<T>(choices);
		Collections.sort(sorted, comparator);
		int scrollHeight = 1;
		for(final T choice: sorted)
		{
			final JToggleButton button;
			if(chooseExactlyOne)
			{
				button = new JRadioButton();
				group.add(button);
			}
			else
				button = new JCheckBox();

			button.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					TextChoicePanel.this.toggleChoice(choice);
				}
			});

			JTextPane text = new JMagicTextPane(true);
			text.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{
					button.doClick();
					super.mouseClicked(e);
				}
			});
			text.setText(choice.toString());

			button.setAlignmentY(0.2f);
			text.setAlignmentY(0.0f);

			Box box = Box.createHorizontalBox();
			box.add(button);
			box.add(text);
			box.add(Box.createHorizontalGlue());
			choiceBox.add(box);
			scrollHeight = box.getPreferredSize().height;
		}

		final JScrollPane scrollPane = new JScrollPane(choiceBox, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		Dimension choiceBoxSize = choiceBox.getPreferredSize();
		int maximumHeight = gui.mainWindow.getSize().height - 200;
		if(maximumHeight < choiceBoxSize.height)
			scrollPane.getViewport().setPreferredSize(new Dimension(choiceBoxSize.width, maximumHeight));
		scrollPane.getVerticalScrollBar().setUnitIncrement(scrollHeight);
		this.add(scrollPane, BorderLayout.CENTER);

		this.doneButton = new JButton("Done");
		this.doneButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				TextChoicePanel.this.gui.choose = new LinkedList<Integer>();
				for(int i = 0; i < TextChoicePanel.this.choices.size(); ++i)
					if(TextChoicePanel.this.chosen.contains(TextChoicePanel.this.choices.get(i)))
						TextChoicePanel.this.gui.choose.add(i);
				TextChoicePanel.this.gui.choiceReady();
			}
		});
		this.doneButton.setEnabled(!chooseExactlyOne);
		Box doneBox = Box.createHorizontalBox();
		doneBox.add(Box.createHorizontalGlue());
		doneBox.add(this.doneButton);
		this.add(doneBox, BorderLayout.PAGE_END);

		// Scroll back up to the top after all the text areas are done updating
		// the viewport
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				scrollPane.getViewport().setViewPosition(new Point(0, 0));
			}
		});
	}

	private void toggleChoice(Object choice)
	{
		if(this.chooseExactlyOne)
		{
			this.chosen = Collections.singletonList(choice);
		}
		else
		{
			if(this.chosen.contains(choice))
				this.chosen.remove(choice);
			else
				this.chosen.add(choice);
		}

		this.doneButton.setEnabled(true);
	}
}
