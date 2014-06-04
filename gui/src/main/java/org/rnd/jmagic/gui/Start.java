package org.rnd.jmagic.gui;

import org.rnd.jmagic.CardLoader;
import org.rnd.jmagic.Version;
import org.rnd.jmagic.comms.ChatManager;
import org.rnd.jmagic.comms.Client;
import org.rnd.jmagic.comms.GameFinder;
import org.rnd.jmagic.comms.Server;
import org.rnd.jmagic.engine.Deck;
import org.rnd.jmagic.engine.GameType;
import org.rnd.jmagic.engine.GameTypes;
import org.rnd.jmagic.engine.PlayerInterface;
import org.rnd.jmagic.gui.dialogs.*;
import org.rnd.jmagic.interfaceAdapters.ConfigurableInterface;
import org.rnd.jmagic.interfaceAdapters.ConfigurableInterfaceDecorator;
import org.rnd.jmagic.interfaceAdapters.CountersAcrossCreaturesInterface;
import org.rnd.jmagic.interfaceAdapters.MulticostAdapter;
import org.rnd.jmagic.interfaceAdapters.SimpleConfigurableInterface;
import org.rnd.jmagic.interfaceAdapters.SimplePlayerInterface;
import org.rnd.jmagic.sanitized.SanitizedGameState;
import org.rnd.util.Logging;
import org.rnd.util.SeparatedList;
import org.rnd.util.StringBooleanTableModel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Start
{
	/**
	 * Creating a local interface failed.
	 */
	private static class CreateLocalException extends Exception
	{
		private static final long serialVersionUID = 0L;

		public CreateLocalException()
		{
			super();
		}

		public CreateLocalException(Throwable cause)
		{
			super(cause);
		}
	}

	private class ServerHost implements ActionListener
	{
		private boolean useGameFinder;

		public ServerHost(boolean useGameFinder)
		{
			this.useGameFinder = useGameFinder;
		}

		@Override
		public void actionPerformed(ActionEvent event)
		{
			final String gameFinderLocation;
			if(this.useGameFinder)
				gameFinderLocation = Start.this.properties.getProperty(PROPERTIES_GAME_FINDER_LOCATION);
			else
				gameFinderLocation = null;

			final int numPlayers;
			try
			{
				numPlayers = Integer.parseInt(Start.this.playersField.getText());
			}
			catch(NumberFormatException e)
			{
				LOG.log(Level.SEVERE, "Number of players must be an integer", e);
				return;
			}

			final int port;
			try
			{
				port = Integer.parseInt(Start.this.portField.getText());
			}
			catch(NumberFormatException e)
			{
				LOG.log(Level.SEVERE, "Port must be an integer", e);
				return;
			}

			final String name = Start.this.nameField.getText();
			if(0 == name.length())
			{
				LOG.severe("Name must be specified");
				return;
			}

			LOG.fine("Starting host thread");
			final Thread game = new Thread("Host")
			{
				@Override
				public void run()
				{
					Deck deck = getDeck();
					// The errors are logged elsewhere, so don't say anything
					if(null == deck)
					{
						hideSplash();
						return;
					}

					Server server = new Server(Start.this.gameType, numPlayers, port);

					try
					{
						LOG.info("Loading cards (" + CardLoader.getCardsLoaded() + " loaded so far)");
						while((null != Start.this.cardThread) && Start.this.cardThread.isAlive())
						{
							Start.this.cardThread.join(1000);
							LOG.info("Loading cards (" + CardLoader.getCardsLoaded() + " loaded so far)");
						}
					}
					catch(InterruptedException e)
					{
						LOG.fine("Interrupted while loading cards");
						hideSplash();
						return;
					}

					try
					{
						if(0 == port)
							for(int i = 0; i < numPlayers; ++i)
								addLocal(deck, name + i, server);
						else
							addLocal(deck, name, server);
					}
					catch(CreateLocalException e)
					{
						// The error has already been logged; just quit
						hideSplash();
						return;
					}

					if(null != gameFinderLocation)
					{
						String descriptionMessage;
						if(FORMAT_STRINGS.contains(Start.this.gameType.getName()))
							descriptionMessage = "Describe your game";
						else
							descriptionMessage = "Describe your game; make sure to specify what rules are being used since this is a custom format";

						String description;
						do
						{
							description = JOptionPane.showInputDialog(Start.this.frame, descriptionMessage);
							// The user canceled when giving a description
							if(null == description)
							{
								hideSplash();
								return;
							}
							description = description.trim();
						}
						while(description.isEmpty());

						server.useGameFinder(gameFinderLocation, description);
					}

					server.run();
					hideSplash();
				}

				private void addLocal(Deck deck, String name, Server server) throws CreateLocalException
				{
					SwingAdapter swing = new SwingAdapter(deck, name);
					ChatManager.MessagePoster messagePoster = server.addLocalPlayer(createLocal(swing), swing.getChatCallback());
					if(null == messagePoster)
					{
						// This error is logged to the same location as if
						// createLocal() threw a CreateLocalException, so throw
						// it here too to make error-handling easier upstream
						throw new CreateLocalException();
					}
					swing.setMessagePoster(messagePoster);
				}
			};
			game.start();
			saveProperties();

			Start.this.splash.addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowClosing(WindowEvent e)
				{
					int option = JOptionPane.showConfirmDialog(Start.this.splash, "Are you sure you want to cancel hosting this game?", "jMagic Interrupt", JOptionPane.YES_NO_OPTION);
					if(JOptionPane.YES_OPTION == option)
					{
						LOG.fine("Interrupting the host thread");
						game.interrupt();
					}
				}
			});

			// This will block until the splash dialog is invisible
			Start.this.splash.setVisible(true);
		}
	}

	private static class StartOptions extends ConfigurationFrame.OptionPanel
	{
		private static final long serialVersionUID = 1L;

		private static void loadDataIntoTable(Properties properties, String dataKey, String enabledKey, StringBooleanTableModel model)
		{
			model.clear();

			String dataProperty = properties.getProperty(dataKey);
			if(0 == dataProperty.length())
				return;

			String[] data = dataProperty.split("[|]");
			char[] enabled = properties.getProperty(enabledKey).toCharArray();
			for(int i = 0; i < data.length; ++i)
				// '1' is true, '0' is false. anything else is also false.
				model.addRow(data[i], (enabled[i] == '1'));
		}

		private static void saveDataFromTable(Properties properties, String dataKey, String enabledKey, StringBooleanTableModel model)
		{
			Map<String, Boolean> data = model.getData();
			StringBuilder dataProperty = new StringBuilder();
			StringBuilder enabledProperty = new StringBuilder();

			for(Map.Entry<String, Boolean> entry: data.entrySet())
			{
				if(dataProperty.length() != 0)
					dataProperty.append('|');
				dataProperty.append(entry.getKey());
				enabledProperty.append(entry.getValue() ? '1' : '0');
			}

			properties.setProperty(dataKey, dataProperty.toString());
			properties.setProperty(enabledKey, enabledProperty.toString());
		}

		private StringBooleanTableModel adapterModel;

		private JTextField cardArtLocation;

		private JTextField gameFinderLocation;

		private StringBooleanTableModel jarModel;

		private StringBooleanTableModel packageModel;

		public StartOptions()
		{
			super("jMagic");

			SpringLayout layout = new SpringLayout();
			this.setLayout(layout);

			this.cardArtLocation = new JTextField(35);

			Box artLocationBox = Box.createHorizontalBox();
			artLocationBox.add(new JLabel("Card art location:"));
			artLocationBox.add(Box.createHorizontalStrut(5));
			artLocationBox.add(this.cardArtLocation);

			JButton browseArt = new JButton("Browse");
			browseArt.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					JFileChooser chooser = new JFileChooser();
					if(0 != StartOptions.this.cardArtLocation.getText().length())
						chooser.setCurrentDirectory(new File(StartOptions.this.cardArtLocation.getText()));
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					chooser.setAcceptAllFileFilterUsed(false);

					if(JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(StartOptions.this))
						StartOptions.this.cardArtLocation.setText(chooser.getSelectedFile().getPath());
				}
			});
			artLocationBox.add(browseArt);

			layout.putConstraint(SpringLayout.WEST, artLocationBox, 5, SpringLayout.WEST, this);
			layout.putConstraint(SpringLayout.NORTH, artLocationBox, 5, SpringLayout.NORTH, this);
			this.add(artLocationBox);

			this.gameFinderLocation = new JTextField(35);

			Box gameFinderBox = Box.createHorizontalBox();
			gameFinderBox.add(new JLabel("Game finder:"));
			gameFinderBox.add(Box.createHorizontalStrut(5));
			gameFinderBox.add(this.gameFinderLocation);

			layout.putConstraint(SpringLayout.WEST, gameFinderBox, 5, SpringLayout.WEST, this);
			layout.putConstraint(SpringLayout.NORTH, gameFinderBox, 5, SpringLayout.SOUTH, artLocationBox);
			this.add(gameFinderBox);

			final StringBooleanTableModel adaptersModel = new StringBooleanTableModel()
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
					return "Interface Adapter";
				}

				@Override
				public boolean validateNewEntry(String value)
				{
					// If this adapter is already in the table, don't add
					// it.
					for(TableEntry entry: this.data)
						if(entry.property.equals(value))
							return false;

					// Verify that the new entry is actually a class and an
					// interface adapter.
					try
					{
						Class<?> cls = Class.forName(value);
						if(!SimpleConfigurableInterface.class.isAssignableFrom(cls))
							// The class isn't an interface adapter, return.
							return false;
					}
					catch(ClassNotFoundException e)
					{
						// The class doesn't exist, don't add it.
						return false;
					}

					return true;
				}
			};
			this.adapterModel = adaptersModel;

			final JTable adapterTable = new JTable(adaptersModel);
			adapterTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
			// Make the second column take up most of the space.
			adapterTable.getColumnModel().getColumn(1).setPreferredWidth(450);

			JButton interfaceAdapterMoveUp = new JButton(new ImageIcon(Start.class.getResource("move_up.png")));
			interfaceAdapterMoveUp.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					adaptersModel.moveSelected(true, adapterTable);
				}
			});

			JButton interfaceAdapterRemove = new JButton(new ImageIcon(Start.class.getResource("delete.png")));
			interfaceAdapterRemove.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					adaptersModel.removeSelected(adapterTable);
				}
			});

			JButton interfaceAdapterMoveDown = new JButton(new ImageIcon(Start.class.getResource("move_down.png")));
			interfaceAdapterMoveDown.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					adaptersModel.moveSelected(false, adapterTable);
				}
			});

			Box interfaceAdaptersButtonsBox = Box.createVerticalBox();
			interfaceAdaptersButtonsBox.add(interfaceAdapterMoveUp);
			interfaceAdaptersButtonsBox.add(Box.createVerticalStrut(5));
			interfaceAdaptersButtonsBox.add(interfaceAdapterRemove);
			interfaceAdaptersButtonsBox.add(Box.createVerticalStrut(5));
			interfaceAdaptersButtonsBox.add(interfaceAdapterMoveDown);

			Box interfaceAdaptersBox = Box.createHorizontalBox();
			interfaceAdaptersBox.setBorder(BorderFactory.createTitledBorder("Interface Adapters"));
			interfaceAdaptersBox.add(new JScrollPane(adapterTable));
			interfaceAdaptersBox.add(Box.createHorizontalStrut(5));
			interfaceAdaptersBox.add(interfaceAdaptersButtonsBox);

			layout.putConstraint(SpringLayout.WEST, interfaceAdaptersBox, 5, SpringLayout.WEST, this);
			layout.putConstraint(SpringLayout.NORTH, interfaceAdaptersBox, 5, SpringLayout.SOUTH, gameFinderBox);
			this.add(interfaceAdaptersBox);

			final StringBooleanTableModel jarModel = new StringBooleanTableModel()
			{
				private static final long serialVersionUID = 1L;

				@Override
				public String getCheckboxColumnTitle()
				{
					return "Load";
				}

				@Override
				public String getStringColumnTitle()
				{
					return "Path to jar file";
				}

				@Override
				public boolean validateNewEntry(String value)
				{
					try
					{
						URL rootURL = new URL("file:///" + System.getProperty("user.dir") + File.separator);
						URL jarURL = new URL(rootURL, value);
						return (new File(jarURL.getFile()).exists());
					}
					catch(MalformedURLException e)
					{
						return false;
					}
				}
			};
			this.jarModel = jarModel;

			final JTable jarTable = new JTable(jarModel);
			jarTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
			// Make the second column take up most of the space.
			jarTable.getColumnModel().getColumn(1).setPreferredWidth(450);

			JButton jarMoveUp = new JButton(new ImageIcon(Start.class.getResource("move_up.png")));
			jarMoveUp.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					jarModel.moveSelected(true, jarTable);
				}
			});

			JButton jarRemove = new JButton(new ImageIcon(Start.class.getResource("delete.png")));
			jarRemove.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					jarModel.removeSelected(jarTable);
				}
			});

			JButton jarMoveDown = new JButton(new ImageIcon(Start.class.getResource("move_down.png")));
			jarMoveDown.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					jarModel.moveSelected(false, jarTable);
				}
			});

			Box jarButtonsBox = Box.createVerticalBox();
			jarButtonsBox.add(jarMoveUp);
			jarButtonsBox.add(Box.createVerticalStrut(5));
			jarButtonsBox.add(jarRemove);
			jarButtonsBox.add(Box.createVerticalStrut(5));
			jarButtonsBox.add(jarMoveDown);

			Box jarBox = Box.createHorizontalBox();
			jarBox.setBorder(BorderFactory.createTitledBorder("External Jars (restart required to load changes)"));
			jarBox.add(new JScrollPane(jarTable));
			jarBox.add(Box.createHorizontalStrut(5));
			jarBox.add(jarButtonsBox);

			layout.putConstraint(SpringLayout.WEST, jarBox, 5, SpringLayout.WEST, this);
			layout.putConstraint(SpringLayout.NORTH, jarBox, 5, SpringLayout.SOUTH, interfaceAdaptersBox);
			this.add(jarBox);

			final StringBooleanTableModel packageModel = new StringBooleanTableModel()
			{
				private static final long serialVersionUID = 1L;

				@Override
				public String getCheckboxColumnTitle()
				{
					return "Load";
				}

				@Override
				public String getStringColumnTitle()
				{
					return "Cards Package";
				}

				@Override
				public boolean validateNewEntry(String value)
				{
					return true;
				}
			};
			this.packageModel = packageModel;

			final JTable packageTable = new JTable(packageModel);
			packageTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
			// Make the second column take up most of the space.
			packageTable.getColumnModel().getColumn(1).setPreferredWidth(450);

			JButton packageMoveUp = new JButton(new ImageIcon(Start.class.getResource("move_up.png")));
			packageMoveUp.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					packageModel.moveSelected(true, jarTable);
				}
			});

			JButton packageRemove = new JButton(new ImageIcon(Start.class.getResource("delete.png")));
			packageRemove.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					packageModel.removeSelected(jarTable);
				}
			});

			JButton packageMoveDown = new JButton(new ImageIcon(Start.class.getResource("move_down.png")));
			packageMoveDown.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					packageModel.moveSelected(false, jarTable);
				}
			});

			Box packageButtonsBox = Box.createVerticalBox();
			packageButtonsBox.add(packageMoveUp);
			packageButtonsBox.add(Box.createVerticalStrut(5));
			packageButtonsBox.add(packageRemove);
			packageButtonsBox.add(Box.createVerticalStrut(5));
			packageButtonsBox.add(packageMoveDown);

			Box packageBox = Box.createHorizontalBox();
			packageBox.setBorder(BorderFactory.createTitledBorder("Cards Packages (restart required to load changes)"));
			packageBox.add(new JScrollPane(packageTable));
			packageBox.add(Box.createHorizontalStrut(5));
			packageBox.add(packageButtonsBox);

			layout.putConstraint(SpringLayout.WEST, packageBox, 5, SpringLayout.WEST, this);
			layout.putConstraint(SpringLayout.NORTH, packageBox, 5, SpringLayout.SOUTH, jarBox);
			this.add(packageBox);

			JButton resetButton = new JButton("Reset Settings");
			resetButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					StartOptions.this.reset();
				}
			});
			Box resetBox = Box.createHorizontalBox();
			resetBox.add(resetButton);

			layout.putConstraint(SpringLayout.WEST, resetBox, 5, SpringLayout.WEST, this);
			layout.putConstraint(SpringLayout.NORTH, resetBox, 5, SpringLayout.SOUTH, packageBox);
			this.add(resetBox);
		}

		@Override
		public void loadSettings(Properties properties)
		{
			this.cardArtLocation.setText(properties.getProperty(Start.PROPERTIES_CARD_IMAGE_LOCATION));
			this.gameFinderLocation.setText(properties.getProperty(Start.PROPERTIES_GAME_FINDER_LOCATION));

			loadDataIntoTable(properties, Start.PROPERTIES_ADAPTERS, Start.PROPERTIES_ADAPTER_STATE, this.adapterModel);
			loadDataIntoTable(properties, Start.PROPERTIES_EXTERNAL_JARS, Start.PROPERTIES_EXTERNAL_JARS_LOAD, this.jarModel);
			loadDataIntoTable(properties, Start.PROPERTIES_CARDS_PACKAGES, Start.PROPERTIES_CARDS_PACKAGES_LOAD, this.packageModel);
		}

		private void reset()
		{
			Properties defaults = new Properties();
			for(Map.Entry<String, String> entry: defaultProperties.entrySet())
				defaults.setProperty(entry.getKey(), entry.getValue());
			this.adapterModel.getData();
			this.loadSettings(defaults);
			this.adapterModel.fireTableDataChanged();
			this.jarModel.fireTableDataChanged();
			this.packageModel.fireTableDataChanged();
		}

		@Override
		public void saveChanges(Properties properties)
		{
			String cardArt = this.cardArtLocation.getText();
			properties.setProperty(Start.PROPERTIES_CARD_IMAGE_LOCATION, cardArt);
			if(0 != cardArt.length())
				CardGraphics.setCardImageLocation(cardArt);

			String gameFinder = this.gameFinderLocation.getText();
			properties.setProperty(Start.PROPERTIES_GAME_FINDER_LOCATION, gameFinder);

			saveDataFromTable(properties, Start.PROPERTIES_ADAPTERS, Start.PROPERTIES_ADAPTER_STATE, this.adapterModel);
			saveDataFromTable(properties, Start.PROPERTIES_EXTERNAL_JARS, Start.PROPERTIES_EXTERNAL_JARS_LOAD, this.jarModel);
			saveDataFromTable(properties, Start.PROPERTIES_CARDS_PACKAGES, Start.PROPERTIES_CARDS_PACKAGES_LOAD, this.packageModel);
		}
	}

	private static final String CUSTOM_GAME_TYPE_VALUE = "Custom";

	private static final Map<String, String> defaultProperties = new HashMap<String, String>();

	private static final Set<String> FORMAT_STRINGS = new HashSet<String>();

	static
	{
		FORMAT_STRINGS.add(GameTypes.BLOCK.getName());
		FORMAT_STRINGS.add(GameTypes.STANDARD.getName());
		FORMAT_STRINGS.add(GameTypes.EXTENDED.getName());
		FORMAT_STRINGS.add(GameTypes.MODERN.getName());
		FORMAT_STRINGS.add(GameTypes.LEGACY.getName());
		FORMAT_STRINGS.add(GameTypes.VINTAGE.getName());
	}

	private static final Logger LOG = Logger.getLogger("org.rnd.jmagic.gui.Start");

	/**
	 * What log file to write to under the directory set by
	 * {@link #setDirectory(String)} where "%d" will be replaced with the log
	 * number
	 */
	private static final String LOG_FILE_PATTERN = "log%d.xml";

	/**
	 * How many log files can exist at maximum before the oldest is overwritten
	 */
	private static final int LOG_LIMIT = 10;

	private static final String PROPERTIES_ADAPTER_STATE = "Start.Adapters.State";

	private static final String PROPERTIES_ADAPTERS = "Start.Adapters";

	private static final String PROPERTIES_CARD_IMAGE_LOCATION = "Start.CardImageLocation";

	public static final String PROPERTIES_CARDS_PACKAGES = "Start.CardPackages";

	public static final String PROPERTIES_CARDS_PACKAGES_LOAD = "Start.CardPackages.Load";

	public static final String PROPERTIES_CUSTOM_GAME_TYPES = "Start.CustomGameTypes";

	private static final String PROPERTIES_DECK = "Start.Deck";

	private static final String PROPERTIES_EXTERNAL_JARS = "Start.ExternalJars";

	private static final String PROPERTIES_EXTERNAL_JARS_LOAD = "Start.ExternalJars.Load";

	private static final String PROPERTIES_FILE = "properties.xml";

	private static final String PROPERTIES_GAME_FINDER_LOCATION = "Start.GameFinderLocation";

	private static final String PROPERTIES_GAME_TYPE = "Start.GameType";

	private static final String PROPERTIES_HOST = "Start.Host";

	private static final String PROPERTIES_NAME = "Start.Name";

	private static final String PROPERTIES_PLAYERS = "Start.Players";

	private static final String PROPERTIES_PORT = "Start.Port";

	static
	{
		defaultProperties.put(PROPERTIES_ADAPTERS, "org.rnd.jmagic.interfaceAdapters.AutomaticPassInterface|org.rnd.jmagic.interfaceAdapters.ShortcutInterface|org.rnd.jmagic.interfaceAdapters.ManaAbilitiesAdapter|org.rnd.jmagic.interfaceAdapters.YieldAdapter");
		defaultProperties.put(PROPERTIES_ADAPTER_STATE, "1111");
		defaultProperties.put(PROPERTIES_EXTERNAL_JARS, "");
		defaultProperties.put(PROPERTIES_EXTERNAL_JARS_LOAD, "");
		defaultProperties.put(PROPERTIES_CARD_IMAGE_LOCATION, "");
		defaultProperties.put(PROPERTIES_CARDS_PACKAGES, "org.rnd.jmagic.cards");
		defaultProperties.put(PROPERTIES_CARDS_PACKAGES_LOAD, "1");
		defaultProperties.put(PROPERTIES_DECK, "");
		defaultProperties.put(PROPERTIES_GAME_FINDER_LOCATION, "");
		defaultProperties.put(PROPERTIES_GAME_TYPE, "Standard");
		defaultProperties.put(PROPERTIES_HOST, "localhost");
		defaultProperties.put(PROPERTIES_NAME, "player");
		defaultProperties.put(PROPERTIES_PLAYERS, "2");
		defaultProperties.put(PROPERTIES_PORT, "4099");
	}

	private static String getDirectory()
	{
		String ret = System.getenv("HOME");
		if(null != ret)
			return ret + File.separator + ".jmagic";

		ret = System.getenv("APPDATA");
		if(null != ret)
			return ret + File.separator + "jMagic";

		return null;
	}

	private static void initializeLogging(String directory)
	{
		// This will create the directory if it doesn't exist, and the
		// properties file is stored to the same directory, so don't worry about
		// creating it later
		File file = new File(directory);
		if(!file.exists() && !file.mkdirs())
			LOG.warning("Could not create directory to store log files in");

		File logFile = null;
		for(int i = 0; i < LOG_LIMIT; ++i)
		{
			File current = new File(directory + File.separator + LOG_FILE_PATTERN.replace("%d", Integer.toString(i)));
			if((null == logFile) || (current.lastModified() < logFile.lastModified()))
				logFile = current;
		}

		// Set up the logger to capture all events to a file
		try
		{
			FileHandler handler = new FileHandler(logFile.getAbsolutePath())
			{
				@Override
				public synchronized void publish(LogRecord record)
				{
					super.publish(record);
					Throwable t = record.getThrown();
					if(null != t)
					{
						Throwable cause = t.getCause();
						if(null != cause)
						{
							record.setMessage("Throwable cause of " + t);
							record.setThrown(cause);
							this.publish(record);
						}
					}
				}
			};
			handler.setLevel(Level.ALL);

			Logging.getRootLogger(LOG).addHandler(handler);
		}
		catch(IOException e)
		{
			LOG.log(Level.WARNING, "IO error while opening the log file", e);
		}
	}

	public static void main(String args[])
	{
		String directory = getDirectory();
		if(null == directory)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					JOptionPane.showMessageDialog(null, "Could not determine a directory to store jMagic files to (no HOME or APPDATA environment variables set?)", "jMagic Error", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
		else
			initializeLogging(directory);
		LOG.info("jMagic version " + new Version() + " started");

		try
		{
			// Set the look-and-feel to the system look-and-feel if possible
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(ClassNotFoundException e)
		{
			LOG.log(Level.WARNING, "Could not find system look-and-feel class", e);
		}
		catch(InstantiationException e)
		{
			LOG.log(Level.WARNING, "Could not instantiate system look-and-feel class", e);
		}
		catch(IllegalAccessException e)
		{
			LOG.log(Level.WARNING, "System look-and-feel class does not have a public instantiatior", e);
		}
		catch(UnsupportedLookAndFeelException e)
		{
			LOG.log(Level.WARNING, "System look-and-feel isn't supported", e);
		}

		final SavedProperties properties;
		if(null == directory)
			properties = new SavedProperties();
		else
			properties = SavedProperties.createFromFile(directory + File.separator + PROPERTIES_FILE);

		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				Start start = new Start();
				start.setProperties(properties);
				Logging.addDialogHandler("jMagic", LOG, start.frame);
				start.frame.setVisible(true);
			}
		});
	}

	private JTextArea cardList;

	private Thread cardThread;

	private Set<GameType> customGameTypes;

	private JTextField deckField;

	private File deckFile;

	private JFrame frame;

	private GameType gameType;

	private JComboBox gameTypeComboBox;

	private GameTypeDialog gameTypeDialog;

	private JTextField hostField;

	private JTextField nameField;

	private JTextField playersField;

	private JTextField portField;

	private SavedProperties properties;

	private Splash splash;

	private Start()
	{
		this.deckFile = null;
		this.gameType = null;

		this.frame = new JFrame("jMagic Start");

		this.gameTypeDialog = null;

		this.splash = new Splash(this.frame);

		this.nameField = new JTextField();

		this.portField = new JTextField();
		this.portField.setToolTipText("Default is 4099; set this to 0 only if hosting and all players should play locally (on the same screen)");

		this.deckField = new JTextField();
		this.deckField.setEnabled(false);

		JButton browseDeck = new JButton("Browse");
		browseDeck.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fileChooser = new JFileChooser();
				if(null != Start.this.deckFile)
					fileChooser.setCurrentDirectory(Start.this.deckFile);

				if(JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(Start.this.frame))
				{
					Start.this.deckFile = fileChooser.getSelectedFile();
					Start.this.deckField.setText(Start.this.deckFile.getName());
				}
			}
		});

		JMenuItem optionsMenuItem = new JMenuItem("Settings");
		optionsMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ConfigurationFrame options = new ConfigurationFrame(Start.this.properties);
				options.addOptionPanel(new StartOptions());
				options.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				options.load();
				options.pack();
				options.setVisible(true);
			}
		});

		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// Change the accelerator below if this code changes
				Start.this.frame.dispose();
			}
		});
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		JMenu startMenu = new JMenu("jMagic");
		startMenu.add(optionsMenuItem);
		startMenu.add(exitMenuItem);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(startMenu);
		this.frame.setJMenuBar(menuBar);

		this.hostField = new JTextField();

		JButton connectButton = new JButton("Connect");
		connectButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				Start.this.connectFromEventThread(null, -1);
			}
		});

		JButton findGames = new JButton("Find Games");
		findGames.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				try
				{
					String location = Start.this.properties.getProperty(PROPERTIES_GAME_FINDER_LOCATION);
					final GameFinderDialog finder = new GameFinderDialog(location, Start.this.frame);
					finder.setGameFinderExceptionListener(new GameFinderDialog.ExceptionListener<GameFinder.GameFinderException>()
					{
						@Override
						public void handleException(GameFinder.GameFinderException e)
						{
							LOG.warning(e.getMessage() + "; can't use game finder");
							finder.dispose();
						}
					});
					finder.setIOExceptionListener(new GameFinderDialog.ExceptionListener<IOException>()
					{
						@Override
						public void handleException(IOException e)
						{
							LOG.log(Level.WARNING, "Error reading from game-finder; can't use game-finder", e);
							finder.dispose();
						}
					});
					finder.setJoinGameListener(new GameFinderDialog.JoinGameListener()
					{
						@Override
						public void joinGame(GameFinder.Game game)
						{
							Start.this.connectFromEventThread(game.IP, game.port);
							finder.dispose();
						}
					});

					// This will block until the dialog is invisible
					finder.setVisible(true);
				}
				catch(URISyntaxException e)
				{
					LOG.log(Level.WARNING, "Can't understand game-finder URL; can't use game-finder", e);
				}
				catch(IllegalArgumentException e)
				{
					LOG.log(Level.WARNING, "Game-finder URL is not absolute; can't use game-finder", e);
				}
				catch(MalformedURLException e)
				{
					LOG.log(Level.WARNING, "Game-finder URL is malformed; can't use game-finder", e);
				}
				catch(IOException e)
				{
					LOG.log(Level.WARNING, "Error reading from game-finder; can't use game-finder", e);
				}
				catch(GameFinder.GameFinderException e)
				{
					LOG.warning(e.getMessage());
				}
			}
		});

		JPanel connectPanel = new JPanel(new GridBagLayout());

		Insets insets = new Insets(1, 1, 1, 1);

		{
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy = 0;
			c.insets = insets;
			c.ipadx = 10;
			c.weightx = 0;
			c.weighty = 0;
			connectPanel.add(new JLabel("Host"), c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 1;
			c.gridy = 0;
			c.insets = insets;
			c.weightx = 1;
			c.weighty = 0;
			connectPanel.add(this.hostField, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 1;
			c.gridy = 1;
			c.insets = insets;
			c.weightx = 1;
			c.weighty = 0;
			connectPanel.add(connectButton, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 1;
			c.gridy = 2;
			c.insets = insets;
			c.weightx = 1;
			c.weighty = 0;
			connectPanel.add(findGames, c);
		}

		this.gameTypeComboBox = new JComboBox();
		for(GameType gameType: GameTypes.values())
			this.gameTypeComboBox.addItem(gameType);
		this.gameTypeComboBox.addItem(CUSTOM_GAME_TYPE_VALUE);

		final AtomicReference<Object> lastSelectedItem = new AtomicReference<Object>();
		this.gameTypeComboBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if(ItemEvent.DESELECTED == e.getStateChange())
					lastSelectedItem.set(e.getItem());
				// Must be SELECTED otherwise
				else if(e.getItem().equals(CUSTOM_GAME_TYPE_VALUE))
				{
					// Since showing a modal dialog blocks the current thread
					// until it is hidden, do so later so the rest of the
					// drop-down events can be processed (like hiding the
					// drop-down list)
					EventQueue.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							if(null == Start.this.gameTypeDialog)
							{
								Set<GameType> presets = new HashSet<GameType>();
								for(GameType t: GameTypes.values())
									presets.add(t);
								presets.addAll(Start.this.customGameTypes);
								Start.this.gameTypeDialog = new GameTypeDialog(Start.this.frame, presets);
							}

							Start.this.gameTypeDialog.setVisible(true);
							GameType custom = Start.this.gameTypeDialog.getGameType();
							if(null == custom)
								Start.this.gameTypeComboBox.setSelectedItem(lastSelectedItem.get());
							else
							{
								Start.this.gameType = custom;
								if(!custom.getName().equals(CUSTOM_GAME_TYPE_VALUE))
								{
									Start.this.customGameTypes.add(custom);
									Start.this.gameTypeComboBox.addItem(custom);
									Start.this.gameTypeComboBox.setSelectedItem(custom);
								}
							}
						}
					});
				}
				else
					Start.this.gameType = (GameType)(e.getItem());
			}
		});

		this.playersField = new JTextField();

		this.cardList = new JTextArea(30, 40);
		this.cardList.setEditable(false);
		this.cardList.setText("Loading cards...");

		JButton hostButton = new JButton("Host");
		hostButton.addActionListener(new ServerHost(false));

		JButton hostWithGameFinderButton = new JButton("Host Publicly");
		hostWithGameFinderButton.setToolTipText("Use the game-finding server (location specified in jMagic->Settings)");
		hostWithGameFinderButton.addActionListener(new ServerHost(true));

		JButton cardsButton = new JButton("Supported Cards");
		cardsButton.addActionListener(new ActionListener()
		{
			private List<Integer> indexes;
			private int lastIndex = -1;
			private Matcher search = null;
			private JTextField searchTextField = null;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				JScrollPane scrollPane = new JScrollPane(Start.this.cardList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

				this.searchTextField = new JTextField("Type a string and press enter to search");
				this.searchTextField.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						selectNext();
					}
				});
				this.searchTextField.getDocument().addDocumentListener(new DocumentListener()
				{
					@Override
					public void changedUpdate(DocumentEvent e)
					{
						// Simple-text fields don't call this function
					}

					@Override
					public void insertUpdate(DocumentEvent e)
					{
						updateMatcher();
					}

					@Override
					public void removeUpdate(DocumentEvent e)
					{
						updateMatcher();
					}
				});

				JDialog cardsDialog = new JDialog(Start.this.frame, "Cards supported while hosting", true);
				cardsDialog.add(scrollPane, BorderLayout.CENTER);
				cardsDialog.add(this.searchTextField, BorderLayout.PAGE_END);
				cardsDialog.pack();
				cardsDialog.setVisible(true);
			}

			private void selectNext()
			{
				if(0 == this.indexes.size())
					return;

				this.lastIndex = (this.lastIndex + 1) % this.indexes.size();
				Start.this.cardList.setCaretPosition(this.indexes.get(this.lastIndex));
			}

			private void updateMatcher()
			{
				Highlighter highlighter = Start.this.cardList.getHighlighter();
				highlighter.removeAllHighlights();

				this.indexes = new ArrayList<Integer>();
				this.search = Pattern.compile(this.searchTextField.getText(), Pattern.CASE_INSENSITIVE).matcher(Start.this.cardList.getText());
				try
				{
					while(this.search.find())
					{
						this.indexes.add(this.search.start());
						highlighter.addHighlight(this.search.start(), this.search.end(), DefaultHighlighter.DefaultPainter);
					}
				}
				catch(BadLocationException e)
				{
					LOG.log(Level.WARNING, "Invalid range for highlighting cards to search", e);
				}

				this.lastIndex = this.indexes.size() - 1;
				selectNext();
			}
		});
		cardsButton.setToolTipText("View all the cards that would be supported if you were hosting");

		JPanel hostPanel = new JPanel(new GridBagLayout());

		{
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy = 0;
			c.insets = insets;
			c.ipadx = 10;
			c.weightx = 0;
			c.weighty = 0;
			hostPanel.add(new JLabel("Game type"), c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 1;
			c.gridy = 0;
			c.insets = insets;
			c.weightx = 1;
			c.weighty = 0;
			hostPanel.add(this.gameTypeComboBox, c);
		}
		/*
		{
			java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
			c.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
			c.fill = java.awt.GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy = 1;
			c.insets = insets;
			c.ipadx = 10;
			c.weightx = 0;
			c.weighty = 0;
			hostPanel.add(new javax.swing.JLabel("Players"), c);
		}

		{
			java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
			c.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
			c.fill = java.awt.GridBagConstraints.BOTH;
			c.gridx = 1;
			c.gridy = 1;
			c.insets = insets;
			c.weightx = 1;
			c.weighty = 0;
			hostPanel.add(this.playersField, c);
		}
		*/
		{
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 1;
			c.gridy = 2;
			c.insets = insets;
			c.weightx = 1;
			c.weighty = 0;
			hostPanel.add(hostButton, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 1;
			c.gridy = 3;
			c.insets = insets;
			c.weightx = 1;
			c.weighty = 0;
			hostPanel.add(hostWithGameFinderButton, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 1;
			c.gridy = 4;
			c.insets = insets;
			c.weightx = 1;
			c.weighty = 0;
			hostPanel.add(cardsButton, c);
		}

		JTabbedPane hostOrConnect = new JTabbedPane();
		hostOrConnect.addTab("Connect", connectPanel);
		hostOrConnect.addTab("Host", hostPanel);

		this.frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				Start.this.splash.dispose();
			}
		});
		this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.frame.setLayout(new GridBagLayout());
		this.frame.setResizable(false);

		{
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy = 0;
			c.insets = insets;
			c.ipadx = 10;
			c.weightx = 0;
			c.weighty = 0;
			this.frame.add(new JLabel("Name"), c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 1;
			c.gridy = 0;
			c.insets = insets;
			c.weightx = 1;
			c.weighty = 0;
			this.frame.add(this.nameField, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy = 1;
			c.insets = insets;
			c.ipadx = 10;
			c.weightx = 0;
			c.weighty = 0;
			this.frame.add(new JLabel("Port"), c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 1;
			c.gridy = 1;
			c.insets = insets;
			c.weightx = 1;
			c.weighty = 0;
			this.frame.add(this.portField, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy = 2;
			c.insets = insets;
			c.ipadx = 10;
			c.weightx = 0;
			c.weighty = 0;
			this.frame.add(new JLabel("Deck"), c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 1;
			c.gridy = 2;
			c.insets = insets;
			c.weightx = 1;
			c.weighty = 0;
			this.frame.add(this.deckField, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 1;
			c.gridy = 3;
			c.insets = insets;
			c.weightx = 1;
			c.weighty = 0;
			this.frame.add(browseDeck, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.fill = GridBagConstraints.BOTH;
			c.gridwidth = 2;
			c.gridx = 0;
			c.gridy = 4;
			c.insets = insets;
			c.weightx = 1;
			c.weighty = 1;
			this.frame.add(hostOrConnect, c);
		}

		this.frame.pack();
	}

	private void connectFromEventThread(String host, int port)
	{
		final String newHost;
		if(null == host)
		{
			newHost = Start.this.hostField.getText();
			if(0 == newHost.length())
			{
				LOG.severe("Host must be specified");
				return;
			}
		}
		else
			newHost = host;

		final int newPort;
		if(-1 == port)
		{
			try
			{
				newPort = Integer.parseInt(Start.this.portField.getText());
				if((newPort < 0) || (65535 < newPort))
				{
					LOG.severe("Port must be between 0 and 65535");
					return;
				}
			}
			catch(NumberFormatException e)
			{
				LOG.log(Level.SEVERE, "Port must be an integer", e);
				return;
			}
		}
		else
			newPort = port;

		final String name = Start.this.nameField.getText();
		if(0 == name.length())
		{
			LOG.warning("Name must be specified");
			return;
		}

		LOG.fine("Starting connect thread");
		final Thread game = new Thread("Connect")
		{
			@Override
			public void run()
			{
				Deck deck = getDeck();
				// The errors are logged elsewhere, so don't say anything
				if(null == deck)
					return;

				try
				{
					SwingAdapter swing = new SwingAdapter(deck, name);
					Client client = new Client(newHost, newPort, name, createLocal(swing), swing.getChatCallback());
					swing.setMessagePoster(client.getMessagePoster());

					client.run();
					hideSplash();
				}
				catch(IOException e)
				{
					LOG.log(Level.SEVERE, "IOException while constructing client", e);
				}
				catch(CreateLocalException e)
				{
					// The error has already been logged; just quit
					return;
				}
			}
		};
		game.start();
		saveProperties();

		Start.this.splash.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				int option = JOptionPane.showConfirmDialog(Start.this.splash, "Are you sure you want to cancel connecting to the host?", "jMagic Interrupt", JOptionPane.YES_NO_OPTION);
				if(JOptionPane.YES_OPTION == option)
				{
					LOG.fine("Interrupting the connect thread");
					game.interrupt();
				}
			}
		});

		// This will block until the splash dialog is invisible
		Start.this.splash.setVisible(true);
	}

	private ConfigurableInterface createLocal(SwingAdapter swing) throws CreateLocalException
	{
		ConfigurableInterface local = new ConfigurableInterfaceDecorator(swing, new SimplePlayerInterface(swing)
		{
			@Override
			public void alertError(ErrorParameters parameters)
			{
				if(!this.disposed)
				{
					String message;
					if(parameters instanceof PlayerInterface.ErrorParameters.CardLoadingError)
						message = "The following cards weren't loaded properly: " + SeparatedList.get("and", ((PlayerInterface.ErrorParameters.CardLoadingError)parameters).cardNames);
					else if(parameters instanceof PlayerInterface.ErrorParameters.HostError)
						message = "The host has encountered an error. The current game will no longer continue.";
					else if(parameters instanceof PlayerInterface.ErrorParameters.IllegalCardsError)
						message = "The following cards aren't legal in a deck list: " + SeparatedList.get("and", ((PlayerInterface.ErrorParameters.IllegalCardsError)parameters).cardNames);
					else if(parameters instanceof PlayerInterface.ErrorParameters.DeckCheckError)
						message = "The following deck check failed: " + ((PlayerInterface.ErrorParameters.DeckCheckError)parameters).rule;
					else if(parameters instanceof PlayerInterface.ErrorParameters.CardCheckError)
						message = "The following card check failed: " + ((PlayerInterface.ErrorParameters.CardCheckError)parameters).card;
					else
						message = "An unknown error occurred in the host.";
					JOptionPane.showMessageDialog(Start.this.splash, message, "jMagic Error", JOptionPane.ERROR_MESSAGE);
					Start.this.hideSplash();
				}

				super.alertError(parameters);
			}

			@Override
			public void alertState(SanitizedGameState sanitizedGameState)
			{
				if(!this.disposed)
				{
					// When this occurs for the first time, assume the GUI is
					// about to pop up, so dispose of the Start frame
					EventQueue.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							Start.this.frame.dispose();
						}
					});

					this.disposed = true;
				}

				super.alertState(sanitizedGameState);
			}

			private boolean disposed = false;
		});
		local = new ConfigurableInterfaceDecorator(local, new MulticostAdapter(local));
		local = new ConfigurableInterfaceDecorator(local, new CountersAcrossCreaturesInterface(local));

		String[] adapters = this.properties.getProperty(PROPERTIES_ADAPTERS).split("[|]");
		char[] adapterStates = this.properties.getProperty(PROPERTIES_ADAPTER_STATE).toCharArray();

		for(int i = 0; i < adapters.length; ++i)
		{
			if(adapterStates[i] != '1')
				continue;

			try
			{
				Class<?> clazz = Class.forName(adapters[i]);
				if(ConfigurableInterface.class.isAssignableFrom(clazz))
				{
					local = (ConfigurableInterface)clazz.getConstructor(ConfigurableInterface.class).newInstance(local);
				}
				else if(PlayerInterface.class.isAssignableFrom(clazz))
				{
					PlayerInterface pi = (PlayerInterface)clazz.getConstructor(PlayerInterface.class).newInstance(local);
					local = new ConfigurableInterfaceDecorator(local, pi);
				}
				else
				{
					LOG.severe("Non-InterfaceAdapter class specified.");
					throw new CreateLocalException();
				}
			}
			catch(ClassNotFoundException e)
			{
				LOG.log(Level.SEVERE, "Could not find class " + adapters[i], e);
				throw new CreateLocalException(e);
			}
			catch(NoSuchMethodException e)
			{
				LOG.log(Level.SEVERE, adapters[i] + " does not define a useful constructor", e);
				throw new CreateLocalException(e);
			}
			catch(IllegalAccessException e)
			{
				LOG.log(Level.SEVERE, adapters[i] + "'s InterfaceAdapter constructor isn't public", e);
				throw new CreateLocalException(e);
			}
			catch(InstantiationException e)
			{
				LOG.log(Level.SEVERE, adapters[i] + " is not a concrete class for an instance to be created", e);
				throw new CreateLocalException(e);
			}
			catch(InvocationTargetException e)
			{
				LOG.log(Level.SEVERE, "An exception was thrown when calling " + adapters[i] + "'s InterfaceAdapter constructor", e);
				throw new CreateLocalException(e);
			}
		}

		local.setProperties(this.properties);
		return local;
	}

	private Deck getDeck()
	{
		if(null == this.deckFile)
		{
			LOG.severe("Null deck file!  Something is very wrong.");
			return null;
		}

		if(this.deckFile.getPath().isEmpty())
		{
			return new Deck();
		}

		if(!this.deckFile.canRead())
		{
			LOG.severe("Can't read deck file");
			return null;
		}

		try
		{
			return CardLoader.getDeck(new BufferedReader(new FileReader(this.deckFile.getAbsolutePath())));
		}
		catch(IOException e)
		{
			LOG.log(Level.SEVERE, "Error reading deck from file; see the log for details", e);
			return null;
		}
	}

	private void hideSplash()
	{
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				// Get rid of the window listeners host/connect added
				for(WindowListener l: Start.this.splash.getWindowListeners())
					Start.this.splash.removeWindowListener(l);
				Start.this.splash.setVisible(false);
			}
		});
		Start.this.splash.clear();
	}

	private void saveProperties()
	{
		this.properties.setProperty(PROPERTIES_DECK, this.deckFile.getAbsolutePath());
		this.properties.put(PROPERTIES_CUSTOM_GAME_TYPES, this.customGameTypes);
		// TODO: This is a hack; we don't want the custom game-type dialog
		// popping up immediately so don't save it if it isn't in the list of
		// defaults
		if(!this.gameType.getName().equals(CUSTOM_GAME_TYPE_VALUE))
			this.properties.setProperty(PROPERTIES_GAME_TYPE, this.gameType.toString());
		this.properties.setProperty(PROPERTIES_HOST, this.hostField.getText());
		this.properties.setProperty(PROPERTIES_NAME, this.nameField.getText());
		this.properties.setProperty(PROPERTIES_PLAYERS, this.playersField.getText());
		this.properties.setProperty(PROPERTIES_PORT, this.portField.getText());
	}

	private boolean setGameType(String gameTypeName)
	{
		for(int i = 0; i < this.gameTypeComboBox.getItemCount(); ++i)
		{
			Object item = this.gameTypeComboBox.getItemAt(i);
			if(item.toString().equals(gameTypeName))
			{
				this.gameType = (GameType)item;
				this.gameTypeComboBox.setSelectedIndex(i);
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public void setProperties(final SavedProperties properties)
	{
		this.properties = properties;

		for(Map.Entry<String, String> entry: defaultProperties.entrySet())
			if(!this.properties.containsKey(entry.getKey()))
				this.properties.setProperty(entry.getKey(), entry.getValue());

		this.portField.setText(this.properties.getProperty(PROPERTIES_PORT));
		this.deckFile = new File(this.properties.getProperty(PROPERTIES_DECK));
		this.deckField.setText(this.deckFile.getName());

		this.customGameTypes = (Set<GameType>)this.properties.get(PROPERTIES_CUSTOM_GAME_TYPES);
		if(null == this.customGameTypes)
			this.customGameTypes = new HashSet<GameType>();
		else
			for(GameType t: this.customGameTypes)
				this.gameTypeComboBox.addItem(t);

		String gameTypeName = this.properties.getProperty(PROPERTIES_GAME_TYPE);
		if(!this.setGameType(gameTypeName))
		{
			LOG.warning("Value " + gameTypeName + " of property " + PROPERTIES_GAME_TYPE + " isn't a GameType; setting it to the default");
			String defaultGameTypeName = defaultProperties.get(PROPERTIES_GAME_TYPE);
			if(!this.setGameType(defaultGameTypeName))
				LOG.severe("Default value " + defaultGameTypeName + " of property " + PROPERTIES_GAME_TYPE + " isn't a GameType! HALP!");
		}

		this.hostField.setText(this.properties.getProperty(PROPERTIES_HOST));
		this.nameField.setText(this.properties.getProperty(PROPERTIES_NAME));
		this.playersField.setText(this.properties.getProperty(PROPERTIES_PLAYERS));

		String cardImageLocation = properties.getProperty(PROPERTIES_CARD_IMAGE_LOCATION);
		if(cardImageLocation == null)
			properties.setProperty(PROPERTIES_CARD_IMAGE_LOCATION, "");
		else if(0 != cardImageLocation.length())
			CardGraphics.setCardImageLocation(cardImageLocation);

		this.cardThread = new Thread()
		{
			@Override
			public void run()
			{
				String[] packages = properties.getProperty(PROPERTIES_CARDS_PACKAGES).split("[|]");
				char[] loadPackage = Start.this.properties.getProperty(PROPERTIES_CARDS_PACKAGES_LOAD).toCharArray();
				for(int i = 0; i < packages.length; ++i)
					if(loadPackage[i] != '1')
						packages[i] = null;
				CardLoader.addPackages(packages);

				try
				{
					String jarProperty = properties.getProperty(PROPERTIES_EXTERNAL_JARS);
					if(0 != jarProperty.length())
					{
						URL rootURL = new URL("file:///" + System.getProperty("user.dir") + File.separator);
						String[] jarPaths = jarProperty.split("[|]");
						char[] loadJar = properties.getProperty(PROPERTIES_EXTERNAL_JARS_LOAD).toCharArray();
						URL[] jarURLs = new URL[jarPaths.length];
						for(int i = 0; i < jarPaths.length; ++i)
							if(loadJar[i] == '1')
								jarURLs[i] = new URL(rootURL, jarPaths[i]);
						CardLoader.addURLs(jarURLs);
					}
				}
				catch(MalformedURLException e)
				{
					LOG.log(Level.SEVERE, "External classpath URL malformed: ", e);
				}

				EventQueue.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						StringBuilder cards = null;
						for(String card: new TreeSet<String>(CardLoader.getAllCardNames()))
						{
							if(null == cards)
								cards = new StringBuilder(card);
							else
							{
								cards.append("\n");
								cards.append(card);
							}
						}

						Start.this.cardList.setText(cards.toString());
						Start.this.cardList.setCaretPosition(0);
					}
				});
			}
		};
		this.cardThread.start();
	}
}
