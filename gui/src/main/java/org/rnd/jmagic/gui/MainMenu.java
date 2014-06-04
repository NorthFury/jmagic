package org.rnd.jmagic.gui;

import org.rnd.jmagic.Version;
import org.rnd.jmagic.gui.dialogs.*;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class MainMenu extends JMenuBar
{
	private static final long serialVersionUID = 1L;

	public MainMenu(final Play gui)
	{
		JMenu game = new JMenu("Game");

		JMenuItem settings = new JMenuItem("Settings");
		settings.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ConfigurationFrame options = gui.configuration;
				options.load();
				options.pack();
				options.setVisible(true);
			}
		});

		JMenuItem quit = new JMenuItem("Quit");
		quit.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				gui.mainWindow.dispose();
			}
		});
		quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		game.add(settings);
		game.add(quit);
		this.add(game);

		JMenuItem about = new JMenuItem("About jMagic");
		about.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(gui.mainWindow, "jMagic version " + new Version() + ".\n\nThe developers of jMagic have nothing (official) to do with Wizards of the Coast or Magic: the Gathering.\n\nYou may distribute jMagic freely as long as you don't claim credit for it.", "About jMagic", JOptionPane.PLAIN_MESSAGE);
			}
		});

		JMenu help = new JMenu("Help");
		help.add(about);
		this.add(help);
	}
}
