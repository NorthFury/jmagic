package org.rnd.jmagic.gui;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.Stack;

public class DoneButton extends JButton
{
	private static final long serialVersionUID = 1L;

	private Stack<ActionListener> listeners;
	private Stack<String> texts;

	public DoneButton(String defaultText)
	{
		this.setEnabled(false);
		this.setText(defaultText);
		this.listeners = new Stack<ActionListener>();
		this.texts = new Stack<String>();
	}

	public boolean hasListeners()
	{
		return !this.listeners.isEmpty();
	}

	public void popListener()
	{
		this.removeActionListener(this.listeners.pop());
		if(this.listeners.isEmpty())
			this.setEnabled(false);

		this.texts.pop();
		if(!this.texts.isEmpty())
			this.setText(this.texts.peek());
	}

	public void pushListener(ActionListener actionListener, String text)
	{
		if(this.listeners.isEmpty())
			this.setEnabled(true);
		this.addActionListener(actionListener);
		this.listeners.push(actionListener);

		this.texts.push(text);
		this.setText(text);
	}
}