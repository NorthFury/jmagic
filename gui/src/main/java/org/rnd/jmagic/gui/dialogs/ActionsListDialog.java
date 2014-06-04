package org.rnd.jmagic.gui.dialogs;

import org.rnd.jmagic.gui.*;
import org.rnd.jmagic.sanitized.SanitizedPlayerAction;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JTextPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

public class ActionsListDialog extends JInternalFrame
{
	private static final long serialVersionUID = 1L;
	private final Play gui;
	private final List<Box> boxes;

	public ActionsListDialog(Play gui)
	{
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setResizable(false);
		this.setTitle("X = undo");
		this.boxes = new LinkedList<Box>();
		this.gui = gui;
	}

	public void addAction(final SanitizedPlayerAction action)
	{
		final Box box = Box.createHorizontalBox();
		this.boxes.add(box);

		JButton removeButton = new JButton(new ImageIcon(ActionsListDialog.class.getResource("/org/rnd/jmagic/gui/delete.png")));
		removeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ActionsListDialog.this.gui.choose.remove(ActionsListDialog.this.boxes.indexOf(box));
				ActionsListDialog.this.gui.removeIndication(action.sourceID);
				if(ActionsListDialog.this.gui.choose.isEmpty())
					ActionsListDialog.this.setVisible(false);

				ActionsListDialog.this.boxes.remove(box);
				ActionsListDialog.this.remove(box);
				ActionsListDialog.this.pack();
			}
		});

		box.add(removeButton);

		JTextPane text = new JMagicTextPane(true);
		text.setText(action.toString());
		box.add(text);

		box.add(Box.createHorizontalGlue());

		this.add(box);
		this.pack();
		this.setVisible(true);
	}

	public void clearList()
	{
		for(Box box: this.boxes)
			this.remove(box);
		this.boxes.clear();
	}
}