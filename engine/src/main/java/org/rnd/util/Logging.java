package org.rnd.util;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Logging
{
	/**
	 * Add a log-record handler that pops up a dialog with the record message. A
	 * record with level WARNING pops-up a warning dialog box. A record with
	 * level SEVERE pops-up an error dialog box.
	 * 
	 * @param parent The JFrame to lock-out while this dialog is popped-up.
	 */
	public static void addDialogHandler(final String application, final Logger logger, final JFrame parent)
	{
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
				Level level = record.getLevel();
				final String message = record.getMessage();

				if(Level.WARNING == level)
				{
					EventQueue.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							JOptionPane.showMessageDialog(parent, message, application + " Warning", JOptionPane.WARNING_MESSAGE);
						}
					});
				}
				else if(Level.SEVERE == level)
				{
					EventQueue.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							JOptionPane.showMessageDialog(parent, message, application + " Error", JOptionPane.ERROR_MESSAGE);
						}
					});
				}
			}
		};
		logger.addHandler(handler);

		parent.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				logger.removeHandler(handler);
			}
		});
	}

	public static Logger getRootLogger(Logger reference)
	{
		Logger parentLogger = reference.getParent();
		Logger rootLogger = reference;
		while(null != parentLogger)
		{
			rootLogger = parentLogger;
			parentLogger = parentLogger.getParent();
		}
		return rootLogger;
	}
}
