package org.rnd.jmagic.gui.dialogs;

import org.rnd.jmagic.comms.GameFinder;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameFinderDialog extends JDialog
{
	public interface ExceptionListener<E extends Exception>
	{
		public void handleException(E e);
	}

	public interface JoinGameListener
	{
		public void joinGame(GameFinder.Game game);
	}

	private static final String[] HEADERS = new String[] {"Hosting Player", "Format", "Description"};

	private static final long serialVersionUID = 1L;

	private class UpdateGamesThread extends Thread
	{
		@Override
		public void run()
		{
			updateGames();
		}
	}

	private GameFinder finder;

	private List<GameFinder.Game> games = Collections.emptyList();

	private Object gamesLock = new Object();

	private ExceptionListener<GameFinder.GameFinderException> gameFinderExceptionListener = null;

	private ExceptionListener<IOException> ioExceptionListener = null;

	private JoinGameListener joinGameListener = null;

	private JTable table;

	private AbstractTableModel tableModel;

	public GameFinderDialog(String location, JFrame parent) throws URISyntaxException, IllegalArgumentException, MalformedURLException, IOException, GameFinder.GameFinderException
	{
		super(parent, "Game Finder (double-click to join)", true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		this.finder = new GameFinder(location);

		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowOpened(WindowEvent e)
			{
				new UpdateGamesThread().start();
			}
		});

		JButton refreshButton = new JButton("Refresh");
		refreshButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				new UpdateGamesThread().start();
			}
		});
		this.add(refreshButton, BorderLayout.PAGE_START);

		this.tableModel = new AbstractTableModel()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public int getColumnCount()
			{
				return HEADERS.length;
			}

			@Override
			public String getColumnName(int column)
			{
				return HEADERS[column];
			}

			@Override
			public int getRowCount()
			{
				synchronized(GameFinderDialog.this.gamesLock)
				{
					return GameFinderDialog.this.games.size();
				}
			}

			@Override
			public Object getValueAt(int arg0, int arg1)
			{
				GameFinder.Game game;

				synchronized(GameFinderDialog.this.gamesLock)
				{
					game = GameFinderDialog.this.games.get(arg0);
				}

				switch(arg1)
				{
				case 0:
					return game.hostPlayerName;
				case 1:
					return game.format;
				case 2:
					return game.description;
				}

				return null;
			}
		};

		this.table = new JTable(this.tableModel);
		this.table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(2 == e.getClickCount())
					joinSelectedGame();
			}
		});
		this.table.setAutoCreateRowSorter(true);
		this.table.setFillsViewportHeight(true);

		JButton joinButton = new JButton("Join Game");
		joinButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				joinSelectedGame();
			}
		});
		this.add(joinButton, BorderLayout.PAGE_END);

		JScrollPane scroll = new JScrollPane(this.table);
		this.add(scroll, BorderLayout.CENTER);

		AbstractAction cancel = new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				GameFinderDialog.this.dispose();
			}
		};
		KeyStroke cancelKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cancelKeyStroke, "Cancel");
		this.getRootPane().getActionMap().put("Cancel", cancel);

		this.pack();
	}

	public void setGameFinderExceptionListener(ExceptionListener<GameFinder.GameFinderException> listener)
	{
		this.gameFinderExceptionListener = listener;
	}

	public void setJoinGameListener(JoinGameListener listener)
	{
		this.joinGameListener = listener;
	}

	public void setIOExceptionListener(ExceptionListener<IOException> listener)
	{
		this.ioExceptionListener = listener;
	}

	private void joinSelectedGame()
	{
		int row = this.table.getSelectedRow();
		if((-1 != row) && (null != this.joinGameListener))
			this.joinGameListener.joinGame(this.games.get(0));
	}

	private void updateGames()
	{
		try
		{
			List<GameFinder.Game> newGames = new ArrayList<GameFinder.Game>(this.finder.list());
			synchronized(this.gamesLock)
			{
				this.games = newGames;
			}
			this.tableModel.fireTableDataChanged();
		}
		catch(IOException e)
		{
			if(null != this.ioExceptionListener)
				this.ioExceptionListener.handleException(e);
		}
		catch(GameFinder.GameFinderException e)
		{
			if(null != this.gameFinderExceptionListener)
				this.gameFinderExceptionListener.handleException(e);
		}
	}
}
