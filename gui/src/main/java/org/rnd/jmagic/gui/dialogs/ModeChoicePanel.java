package org.rnd.jmagic.gui.dialogs;

import java.awt.event.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.*;

import org.rnd.jmagic.engine.MagicSet;
import org.rnd.jmagic.engine.generators.Intersect;
import org.rnd.jmagic.engine.generators.Maximum;
import org.rnd.jmagic.engine.generators.Minimum;
import org.rnd.jmagic.gui.*;
import org.rnd.jmagic.sanitized.*;
import org.rnd.util.NumberRange;

public class ModeChoicePanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private Play gui;
	private List<SanitizedMode> choices;
	private Set<SanitizedMode> choice;
	private int minimum;
	private int maximum;
	private JButton doneButton;

	public ModeChoicePanel(Play gui, final List<SanitizedMode> modes, MagicSet number)
	{
		super();

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.gui = gui;
		this.choices = modes;
		this.choice = new HashSet<SanitizedMode>();
		number = Intersect.get(number, new MagicSet(new NumberRange(0, modes.size())));
		this.minimum = Minimum.get(number);
		this.maximum = Maximum.get(number);

		for(final SanitizedMode mode: modes)
		{
			String modeName = mode.toString();
			modeName = Character.toUpperCase(modeName.charAt(0)) + modeName.substring(1);
			JCheckBox check = new JCheckBox(new AbstractAction(modeName)
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e)
				{
					ModeChoicePanel.this.toggleChoice(mode);
				}
			});
			Box checkBox = Box.createHorizontalBox();
			checkBox.add(check);
			checkBox.add(Box.createHorizontalGlue());
			this.add(checkBox);
		}

		this.doneButton = new JButton(new AbstractAction("Done")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				ModeChoicePanel.this.gui.choose = new LinkedList<Integer>();
				for(int i = 0; i < ModeChoicePanel.this.choices.size(); ++i)
					if(ModeChoicePanel.this.choice.contains(ModeChoicePanel.this.choices.get(i)))
						ModeChoicePanel.this.gui.choose.add(i);
				ModeChoicePanel.this.gui.choiceReady();
			}
		});
		this.doneButton.setEnabled(false);
		Box doneBox = Box.createHorizontalBox();
		doneBox.add(Box.createHorizontalGlue());
		doneBox.add(this.doneButton);
		this.add(doneBox);
	}

	private void toggleChoice(SanitizedMode mode)
	{
		if(this.choice.contains(mode))
			this.choice.remove(mode);
		else
			this.choice.add(mode);

		// Enable the done button only if the correct number of choices have
		// been made
		this.doneButton.setEnabled(this.choice.size() >= this.minimum && this.choice.size() <= this.maximum);
	}
}
