package org.rnd.jmagic.gui;

import org.rnd.util.Logging;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Splash extends JDialog
{
	private static final Logger LOG = Logger.getLogger("org.rnd.jmagic.gui.Splash");

	private static final long serialVersionUID = 1L;

	private JScrollPane scrollPane;
	private JTextArea textBox;

	public Splash(final JFrame parent)
	{
		super(parent, "jMagic Starting", true);

		this.textBox = new JTextArea(10, 60);
		this.textBox.setEditable(false);
		this.textBox.setLineWrap(true);
		this.textBox.setWrapStyleWord(true);

		this.scrollPane = new JScrollPane(Splash.this.textBox);
		this.scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		this.add(Splash.this.scrollPane);
		this.pack();
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(null);

		Point location = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		location.translate((int)(this.getWidth() * -.5), (int)(this.getHeight() * -.5));
		this.setLocation(location);

		final Handler handler = new Handler()
		{
			@Override
			public void close() throws SecurityException
			{
				// Nothing to do here
			}

			@Override
			public void flush()
			{
				// Nothing to do here
			}

			@Override
			public void publish(LogRecord record)
			{
				if(Level.INFO == record.getLevel())
					addLine(record.getMessage());
			}
		};
		Logging.getRootLogger(LOG).addHandler(handler);

		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				Logging.getRootLogger(LOG).removeHandler(handler);
			}
		});
	}

	/**
	 * Add a line after any lines already added, or at the top if no lines have
	 * been added yet. This is executed in the event processing thread, so it is
	 * safe to call this from any thread.
	 * 
	 * @param newLine The line to add
	 */
	public void addLine(final String newLine)
	{
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				if(0 == Splash.this.textBox.getText().length())
					Splash.this.textBox.setText("> " + newLine);
				else
					Splash.this.textBox.append("\n> " + newLine);
			}
		});
	}

	/**
	 * Remove all lines added by {@link #addLine(String)}. This is executed in
	 * the event processing thread, so it is safe to call this from any thread.
	 */
	public void clear()
	{
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				Splash.this.textBox.setText("");
			}
		});
	}
}
