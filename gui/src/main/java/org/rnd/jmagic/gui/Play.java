package org.rnd.jmagic.gui;

import org.rnd.jmagic.CompareCostCollections;
import org.rnd.jmagic.comms.ChatManager;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.Maximum;
import org.rnd.jmagic.engine.generators.Minimum;
import org.rnd.jmagic.gui.dialogs.*;
import org.rnd.jmagic.gui.event.*;
import org.rnd.jmagic.sanitized.*;
import org.rnd.util.CompareOnToString;
import org.rnd.util.NumberRange;
import org.rnd.util.SeparatedList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.lang.String;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;

public class Play
{
	public class ActionDialogListener implements GuiEventListener
	{
		@Override
		public void notify(GuiEvent e)
		{
			if(e.type.equals(ChooseActionEvent.TYPE))
			{
				if(Play.this.chooseOneAction)
					return;

				if(Play.this.actionsDialog == null)
				{
					Play.this.actionsDialog = new ActionsListDialog(Play.this);
					Play.this.pushPopup(Play.this.actionsDialog);
				}

				ChooseActionEvent event = (ChooseActionEvent)e;
				Play.this.actionsDialog.addAction(event.getAction());
			}
			else if(e.type.equals(DoneClickedEvent.TYPE))
			{
				Play.this.choiceReady();
				Play.this.actionsDialog = null;
			}
		}
	}

	/**
	 * This class handles indicating objects as a result of the user clicking on
	 * them.
	 */
	public class ObjectHighlighter implements GuiEventListener
	{
		@Override
		public void notify(GuiEvent e)
		{
			if(e.type.equals(ChooseActionEvent.TYPE))
				this.handleHighlight(((ChooseActionEvent)e).getIdentifiedMouseEvent(), false);
			else if(e.type.equals(ChooseIdentifiedEvent.TYPE))
				this.handleHighlight(((ChooseIdentifiedEvent)e).getIdentifiedMouseEvent(), true);
			else if(e.type.equals(DoneClickedEvent.TYPE))
				Play.this.clearIndicated();
		}

		private void handleHighlight(IdentifiedMouseEvent event, boolean checkChoices)
		{
			if(!Play.this.chooseOneAction)
			{
				int id = event.getIdentified().ID;
				if(Play.this.indicated.contains(id) && (Play.this.choiceType != PlayerInterface.ChoiceType.ACTIVATE_MANA_ABILITIES && Play.this.choiceType != PlayerInterface.ChoiceType.NORMAL_ACTIONS))
					Play.this.removeIndication(id);
				else if(checkChoices)
				{
					for(Object choice: Play.this.choices)
						if((choice instanceof SanitizedIdentified && ((SanitizedIdentified)choice).ID == id) || (choice instanceof SanitizedTarget && ((SanitizedTarget)choice).targetID == id))
						{
							Play.this.indicateObject(id);
							return;
						}
				}
				else
					Play.this.indicateObject(id);

			}
		}
	}

	public class DefaultChooseActionListener implements GuiEventListener
	{
		@Override
		public void notify(GuiEvent event)
		{
			if(!event.type.equals(ChooseActionEvent.TYPE))
				return;

			ChooseActionEvent e = (ChooseActionEvent)event;

			int index = Play.this.choices.indexOf(e.getAction());

			if(Play.this.chooseOneAction)
			{
				Play.this.choose = Collections.singletonList(index);
				Play.this.choiceReady();
				Play.this.choiceType = null;
				Play.this.popDoneListeners();
			}
			else
			{
				if(Play.this.choose == null)
					Play.this.choose = new LinkedList<Integer>();
				Play.this.choose.add(index);
			}
		}
	}

	public class DefaultChooseIdentifiedListener implements GuiEventListener
	{
		@Override
		public void notify(GuiEvent event)
		{
			if(!event.type.equals(ChooseIdentifiedEvent.TYPE))
				return;

			ChooseIdentifiedEvent e = (ChooseIdentifiedEvent)event;

			int index = Play.this.choices.indexOf(e.getIdentifiedMouseEvent().getIdentified());

			if(index == -1)
				return;

			if(Play.this.chooseOneAction)
			{
				Play.this.choose = Collections.singletonList(index);
				Play.this.choiceReady();
				return;
			}

			if(Play.this.choose.contains(index))
				Play.this.choose.remove((Object)index);
			else
				Play.this.choose.add(index);

			Play.this.mainWindow.repaint();
		}
	}

	public class ChoiceListener implements GuiEventListener
	{
		private final Comparator<JMenuItem> compareNames = new Comparator<JMenuItem>()
		{
			@Override
			public int compare(JMenuItem o1, JMenuItem o2)
			{
				return o1.getText().compareTo(o2.getText());
			}
		};

		@Override
		public void notify(GuiEvent event)
		{
			if(event.type.equals(BattlefieldClickEvent.TYPE))
			{
				// Only make a choice if something needs to be chosen
				if(null != Play.this.choiceType)
					chooseAction((BattlefieldClickEvent)event);
				return;
			}

			if(!event.type.equals(IdentifiedMouseEvent.TYPE))
				return;

			IdentifiedMouseEvent e = (IdentifiedMouseEvent)event;
			MouseEvent mouseEvent = e.getMouseEvent();
			boolean leftButton = (MouseEvent.BUTTON1 == mouseEvent.getButton());
			boolean clicked = (MouseEvent.MOUSE_CLICKED == mouseEvent.getID());

			if(Play.this.divisions != null)
			{
				if(!leftButton || !clicked)
					return;

				final SanitizedIdentified chosenIdentified = e.getIdentified();
				if(null == chosenIdentified)
					return;
				if(!Play.this.divisions.containsKey(chosenIdentified.ID))
					return;
				Play.this.dividingOn = chosenIdentified.ID;

				final JInternalFrame pop = new JInternalFrame(chosenIdentified.name);
				final JTextArea text = new JTextArea(Integer.toString(Play.this.divisions.get(Play.this.dividingOn)));
				pop.add(text);
				pop.setLocation(Play.this.cardLocations.get(Play.this.dividingOn).x - CardGraphics.SMALL_CARD.width / 2, Play.this.cardLocations.get(Play.this.dividingOn).y);
				text.selectAll();
				pop.setPreferredSize(new Dimension(CardGraphics.SMALL_CARD.width, pop.getPreferredSize().height));
				text.addFocusListener(new FocusListener()
				{
					@Override
					public void focusGained(FocusEvent e)
					{
						// Nothing
					}

					@Override
					public void focusLost(FocusEvent e)
					{
						Play.this.finishUpdateDivision(chosenIdentified.ID, text.getText(), pop);
					}
				});
				text.addKeyListener(new KeyAdapter()
				{
					@Override
					public void keyPressed(KeyEvent e)
					{
						if(e.getKeyCode() == KeyEvent.VK_ENTER)
							Play.this.finishUpdateDivision(chosenIdentified.ID, text.getText(), pop);
					}
				});

				((JComponent)Play.this.mainWindow.getGlassPane()).add(pop);
				pop.pack();
				pop.setVisible(true);
				text.requestFocus();
				Play.this.mainWindow.repaint();
			}
			else
			{
				// Only make a choice if something needs to be chosen
				if(null == Play.this.choiceType)
					return;

				switch(Play.this.choiceType)
				{
				case ACTIVATE_MANA_ABILITIES:
				case NORMAL_ACTIONS:
					if((leftButton && clicked) || mouseEvent.isPopupTrigger())
						this.chooseAction(e);
					return;

				case ATTACK:
				case ATTACK_WHAT:
				case BLOCK:
				case BLOCK_WHAT:
				case DAMAGE_SOURCE:
				case OBJECTS:
				case PLAYER:
				case SINGLE_TARGET:
				case TARGETS:
					if(leftButton && clicked)
						Play.this.alertGuiEvent(new ChooseIdentifiedEvent(e));
					return;

				default:
					break;
				}
			}
		}

		public void chooseAction(final BattlefieldClickEvent event)
		{
			MouseEvent mouseEvent = event.getClick();

			final List<SanitizedPlayerAction> actions = new LinkedList<SanitizedPlayerAction>();
			for(Object choice: Play.this.choices)
			{
				if(choice instanceof SanitizedPlayerAction)
				{
					SanitizedPlayerAction action = (SanitizedPlayerAction)choice;
					if(action.actOnID == -1)
						actions.add((SanitizedPlayerAction)choice);
				}
			}

			// If there are no actions, do nothing.
			if(actions.isEmpty())
				return;

			List<JMenuItem> items = new LinkedList<JMenuItem>();
			for(final SanitizedPlayerAction a: actions)
			{
				JMenuItem item = new JMenuItem(a.toString());
				item.setUI(new ContextDialogPainter());
				item.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						Play.this.alertGuiEvent(new ChooseActionEvent(a));
					}
				});
				items.add(item);
			}
			Collections.sort(items, this.compareNames);

			JPopupMenu popup = new JPopupMenu();
			for(JMenuItem item: items)
				popup.add(item);
			popup.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
		}

		public void chooseAction(final IdentifiedMouseEvent event)
		{
			MouseEvent mouseEvent = event.getMouseEvent();

			final SanitizedIdentified chosenIdentified = event.getIdentified();

			final List<SanitizedPlayerAction> actionsForChosenObject = new LinkedList<SanitizedPlayerAction>();
			List<Integer> abilitiesOnChosenObject = new LinkedList<Integer>();
			if(chosenIdentified instanceof SanitizedGameObject)
			{
				SanitizedCharacteristics c = ((SanitizedGameObject)chosenIdentified).characteristics.get(SanitizedGameObject.CharacteristicSet.ACTUAL);
				abilitiesOnChosenObject.addAll(c.abilities);
				abilitiesOnChosenObject.addAll(c.hiddenAbilities);
			}
			else
			{
				abilitiesOnChosenObject.addAll(((SanitizedPlayer)chosenIdentified).nonStaticAbilities);
			}
			for(Object choice: Play.this.choices)
			{
				if(choice instanceof SanitizedPlayerAction)
				{
					SanitizedPlayerAction action = (SanitizedPlayerAction)choice;

					// if the action is from the clicked object
					if(action.actOnID == chosenIdentified.ID)
						actionsForChosenObject.add((SanitizedPlayerAction)choice);

					// if the action is from an ability of the clicked object
					if((abilitiesOnChosenObject.contains(action.actOnID)))
						actionsForChosenObject.add((SanitizedPlayerAction)choice);
				}
			}

			// If there are no actions, do nothing.
			if(actionsForChosenObject.isEmpty())
				return;

			// Skip showing the context menu if there is exactly one choice, and
			// the user left-clicked, and they don't have "always show actions
			// popup" selected.
			if(!Play.this.alwaysShowActionsPopup() && actionsForChosenObject.size() == 1 && !mouseEvent.isPopupTrigger())
			{
				Play.this.alertGuiEvent(new ChooseActionEvent(event, actionsForChosenObject.get(0)));
				return;
			}

			List<JMenuItem> items = new LinkedList<JMenuItem>();
			for(final SanitizedPlayerAction a: actionsForChosenObject)
			{
				JMenuItem item = new JMenuItem(a.toString());
				item.setUI(new ContextDialogPainter());
				item.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						Play.this.alertGuiEvent(new ChooseActionEvent(event, a));
					}
				});
				items.add(item);
			}
			Collections.sort(items, this.compareNames);

			JPopupMenu popup = new JPopupMenu();
			for(JMenuItem item: items)
				popup.add(item);
			popup.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
		}
	}

	static final class PropertyKeys
	{
		public static final String ACTIONS_POPUP = "Play.ActionsPopup";
		public static final String BEEP_ON_YOUR_TURN = "Play.BeepOnYourTurn";

		public static final String HIGHLIGHT_SELECTED_CARDS = "Play.HightlightSelectedCards";

		public static final String MISC_ZONE_L = "Play.MiscZone.Left";
		public static final String MISC_ZONE_R = "Play.MiscZone.Right";
		public static final String RENDER_DAMAGE = "Play.Battlefield.RenderDamage";
		public static final String RENDER_COUNTERS = "Play.Battlefield.RenderCounters";
		public static final String ROTATE_OPP_CARDS = "Play.RotateOppCards";
		public static final String CARD_IMAGE_LOCATION = "Play.CardImageLocation";

		public static final String getArrowColorKey(Arrow.ArrowType type)
		{
			return "Play.Arrow.Color." + type.name();
		}

		public static final String getArrowEnabledKey(Arrow.ArrowType type)
		{
			return "Play.Arrow.Enabled." + type.name();
		}

		private PropertyKeys()
		{
			// Purely static classes don't need to be constructed
		}

		public static String colorToString(Color color)
		{
			return String.format("0x%1$06x", color.getRGB() & 0x00ffffff);
		}

		public static Color colorFromString(String color)
		{
			return Color.decode(color);
		}
	}

	private static Map<PlayerInterface.ChooseReason, String> customQueries;
	static
	{
		customQueries = new HashMap<PlayerInterface.ChooseReason, String>();
		customQueries.put(PlayerInterface.ChooseReason.DAMAGE_ASSIGNMENT_ORDER, PlayerInterface.ChooseReason.DAMAGE_ASSIGNMENT_ORDER.query + " The creature on the left will be first.");
		customQueries.put(PlayerInterface.ChooseReason.FIRST_PLAYER, "Click on the player card of the player who will take the first turn.");
		customQueries.put(PlayerInterface.ChooseReason.ORDER_LIBRARY_BOTTOM, PlayerInterface.ChooseReason.ORDER_LIBRARY_BOTTOM.query + " The card on the right will be on the bottom.");
		customQueries.put(PlayerInterface.ChooseReason.ORDER_LIBRARY_TOP, PlayerInterface.ChooseReason.ORDER_LIBRARY_TOP.query + " The card on the right will be on top.");
		customQueries.put(PlayerInterface.ChooseReason.ORDER_STACK, PlayerInterface.ChooseReason.ORDER_STACK.query + " The card on the right will be on top.");
		customQueries.put(PlayerInterface.ChooseReason.STACK_TRIGGERS, PlayerInterface.ChooseReason.STACK_TRIGGERS.query + " The ability on the right will be on top.");
		customQueries.put(PlayerInterface.ChooseReason.SPLICE_ORDER, PlayerInterface.ChooseReason.SPLICE_ORDER.query + " The instructions of card on the left will be first.");
	}

	private static final int labelPaddingAmount = 3;

	private static final String noResponsesText = "You wish to pass priority until the stack is empty or something new is added to it.";

	protected static Point getLocationInsideWindow(Component component)
	{
		Point point = new Point(0, 0);
		while(component.getParent() != null && !(component.getParent() instanceof JRootPane))
		{
			Point location = component.getLocation();
			point.translate(location.x, location.y);
			component = component.getParent();
		}
		return point;
	}

	private static <T extends Serializable> String getQuery(PlayerInterface.ChooseParameters<T> parameterObject)
	{
		if(customQueries.containsKey(parameterObject.reason))
			return customQueries.get(parameterObject.reason);
		if(parameterObject.type == PlayerInterface.ChoiceType.TARGETS)
			return "Choose " + ((SanitizedTarget)parameterObject.choices.get(0)).name + ".";
		return parameterObject.reason.query;
	}

	private ActionsListDialog actionsDialog = null;

	protected Map<Integer, Collection<Arrow>> arrows;

	private BattlefieldPanel battlefieldPanel;

	CardInfoPanel cardInfoPanel;

	protected Map<Integer, Point> cardLocations;

	public List<Object> choices;

	public PlayerInterface.ChoiceType choiceType;

	private boolean chooseOneAction;

	ConfigurationFrame configuration;

	private Thread creatingThread;

	protected int dividingOn;

	protected Map<Integer, Integer> divisions;

	private List<GuiEventListener> guiEventListeners;

	// this keys on integers because the display type of a small card is always
	// the same
	private Map<Integer, Image> smallImageCache;

	private List<Integer> indicated;

	private LogPanel logPanel;

	public volatile JFrame mainWindow;

	public ChatManager.MessagePoster messagePoster;

	DoneButton noResponsesButton;

	private Collection<Integer> notRespondingTo;

	DoneButton passButton;

	public int playerID;

	List<PlayerPanel> playerPanels;

	private Stack<JInternalFrame> popups;

	protected Properties properties;

	/**
	 * Represents the old player id when the game is being restarted. If the
	 * game isn't restarting, this will be -1.
	 */
	private int restarting;

	private JSplitPane splitPane;

	public SanitizedGameState state;

	protected MultilineLabel statusLine;

	private StepLabel stepLabel;

	private JLabel turnLabel;

	private List<MiscZonePanel> zoneContentsPanels;

	/**
	 * The zones for the zone selector of this GUI, in a specific order. Keys
	 * are display names. Values are zone IDs.
	 */
	LinkedHashMap<String, Integer> zones;

	public boolean choiceReady;
	public List<Integer> choose;
	public int chooseNumber;

	/**
	 * A sorted collection of settings panels. This is sorted so that they
	 * appear in the configuration frame in a consistent order.
	 */
	public SortedSet<ConfigurationFrame.OptionPanel> options;

	/**
	 * Construct a new Play GUI.
	 */
	public Play(SortedSet<ConfigurationFrame.OptionPanel> options)
	{
		this.arrows = new HashMap<Integer, Collection<Arrow>>();

		this.configuration = null;

		this.battlefieldPanel = null;

		this.cardInfoPanel = null;

		this.cardLocations = new HashMap<Integer, Point>();

		this.choices = null;

		this.choiceType = null;

		this.chooseOneAction = false;

		this.creatingThread = Thread.currentThread();

		this.divisions = null;

		this.guiEventListeners = new LinkedList<GuiEventListener>();
		this.guiEventListeners.add(new ActionDialogListener());
		this.guiEventListeners.add(new ChoiceListener());
		this.guiEventListeners.add(new DefaultChooseActionListener());
		this.guiEventListeners.add(new DefaultChooseIdentifiedListener());
		this.guiEventListeners.add(new ObjectHighlighter());

		this.dividingOn = -1;

		this.smallImageCache = new HashMap<Integer, Image>();

		this.indicated = new LinkedList<Integer>();

		this.mainWindow = null;

		this.logPanel = null;

		this.notRespondingTo = Collections.emptyList();

		this.popups = new Stack<JInternalFrame>();

		this.playerID = -1;

		this.playerPanels = new LinkedList<PlayerPanel>();

		this.properties = null;

		this.restarting = -1;

		this.state = null;

		this.stepLabel = null;

		this.turnLabel = null;

		this.zoneContentsPanels = new LinkedList<MiscZonePanel>();

		this.zones = new LinkedHashMap<String, Integer>();

		this.choiceReady = false;
		this.choose = null;
		this.chooseNumber = -1;

		this.options = options;
		this.options.add(this.getOptionPanel());
	}

	public void alertChoice(final int playerID, final PlayerInterface.ChooseParameters<?> choice)
	{
		switch(choice.type)
		{
		case NORMAL_ACTIONS:
			if(choice.choices.isEmpty())
				return;
			break;

		case ACTIVATE_MANA_ABILITIES:
		case COSTS:
		case MANA_EXPLOSION:
		case MANA_PAYMENT:
		case MOVEMENT_LIBRARY:
		case MOVEMENT_GRAVEYARD:
		case TRIGGERS:
			return;

		default:
		}

		final StringBuilder line = new StringBuilder();
		switch(choice.type)
		{
		case NORMAL_ACTIONS:
			line.append(this.state.get(playerID) + ": " + choice.choices.iterator().next());
			break;

		case PLAYER:
			line.append(this.state.get(playerID) + " chose " + choice.choices.iterator().next());
			if(choice.reason.equals(PlayerInterface.ChooseReason.FIRST_PLAYER))
				line.append(" to play first.");
			break;

		case TARGETS:
			if(choice.choices.isEmpty())
			{
				line.append(this.state.get(playerID) + " chose zero targets");
			}
			else
			{
				String targetDescription = choice.choices.iterator().next().toString();
				line.append(Character.toUpperCase(targetDescription.charAt(0)));
				line.append(targetDescription.substring(1) + ": ");
				List<SanitizedIdentified> chosenTargets = new LinkedList<SanitizedIdentified>();
				for(Object t: choice.choices)
					chosenTargets.add(this.state.get(((SanitizedTarget)t).targetID));
				line.append(SeparatedList.get("and", chosenTargets));
			}
			break;

		default:
			String query = choice.reason.query;
			if(query.contains("~"))
			{
				if(choice.thisID != -1)
				{
					SanitizedIdentified object = this.state.get(choice.thisID);
					if(null != object)
						query = query.replace("~", object.name);
				}
				else if(null != choice.replacement)
				{
					query = query.replace("~", choice.replacement);
				}
			}

			line.append(this.state.get(playerID) + " was asked '" + query + "' and chose " + choice.choices);
		}

		this.logPanel.addLine(line.toString());
	}

	public void alertEvent(final SanitizedEvent event)
	{
		String description = event.getDescription(this.state);

		if(this.logPanel != null)
		{
			this.logPanel.addLine(description);
			if(event.type.equals(EventType.GAME_OVER.toString()))
			{
				this.displayChoiceText(description, false);
			}
			else if(event.type.equals(EventType.RESTART_THE_GAME.toString()))
			{
				this.restarting = this.playerID;
				// this.mainWindow.dispose();
				// this.mainWindow = null;
			}
		}
	}

	public void alertGuiEvent(GuiEvent event)
	{
		for(GuiEventListener listener: this.guiEventListeners)
			listener.notify(event);
	}

	public void alertState(final SanitizedGameState state)
	{
		if(this.restarting != -1)
		{
			// During the execution of this block, Play.state is the pre-restart
			// gamestate, while Play.playerID is the post-restart playerID. The
			// post-restart gamestate is the parameter to this method. The
			// pre-restart playerID is this.restarting.

			// this.battlefieldPanel does not need to be reset. Its update
			// method reads in all necessary information from the state.
			int offset = state.players.indexOf(this.playerID) - this.state.players.indexOf(this.restarting);
			int numPlayers = state.players.size();
			for(PlayerPanel p: this.playerPanels)
			{
				int index = this.state.players.indexOf(p.getPlayer());
				// Add numPlayers before modulus to ensure a non-negative
				// result.
				p.setPlayer(state.players.get((index + offset + numPlayers) % numPlayers));
			}
			this.setUpMiscZonePanels(state);
			this.cardInfoPanel.setFocusToNonGameObject(-1, state);
		}

		if(this.beepOnYourTurn())
		{
			if(this.state != null && this.state.turn != this.playerID && state.turn == this.playerID)
				Toolkit.getDefaultToolkit().beep();
		}

		this.state = state;

		if(null == state)
			return;

		if(null == this.mainWindow)
			this.create();

		this.smallImageCache.clear();
		this.cardLocations.clear();

		if(-1 == state.turn)
			this.turnLabel.setText("No turn");
		else if(state.turn == this.playerID)
			this.turnLabel.setText("Your turn");
		else
			this.turnLabel.setText(state.get(state.turn).name + "'s turn");

		this.stepLabel.highlight(state.step);

		if(this.restarting != -1)
		{
			this.zoneContentsPanels.get(0).setSelectedItem(this.properties.getProperty(PropertyKeys.MISC_ZONE_L));
			this.zoneContentsPanels.get(1).setSelectedItem(this.properties.getProperty(PropertyKeys.MISC_ZONE_R));
			this.restarting = -1;
		}

		this.battlefieldPanel.update();
		for(PlayerPanel p: this.playerPanels)
			p.update(true);
		for(MiscZonePanel zoneContents: this.zoneContentsPanels)
			zoneContents.update();
		this.cardInfoPanel.update();

		this.clearIndicated();
	}

	public void alertWaiting(SanitizedPlayer who)
	{
		this.displayChoiceText("Waiting for " + (who == null ? "the game" : who.toString()) + "...", false);
	}

	/**
	 * Notifies the listeners that choices have been made, and removes all
	 * popups from the dialog.
	 */
	public synchronized void choiceReady()
	{
		this.choiceReady = true;
		this.alertWaiting(null);
		if(this.choices != null)
			this.choices.clear();
		this.notify();
		while(!this.popups.isEmpty())
			this.popPopup();
	}

	@SuppressWarnings("unchecked")
	public <T extends Serializable> void choose(final PlayerInterface.ChooseParameters<T> parameterObject)
	{
		// Go through all the choices, and for any identifieds that aren't in
		// the state, put them in the state.
		for(T choice: parameterObject.choices)
			if(choice instanceof SanitizedIdentified)
			{
				SanitizedIdentified sanitized = (SanitizedIdentified)choice;
				if(!this.state.containsKey(sanitized.ID))
					this.state.put(sanitized.ID, sanitized);
			}

		// The caller may also specify objects that need to be in the state
		for(SanitizedIdentified i: parameterObject.ensurePresent)
			if(!this.state.containsKey(i.ID))
				this.state.put(i.ID, i);

		Integer upperBound = Maximum.get(parameterObject.number);
		Integer lowerBound = Minimum.get(parameterObject.number);
		if(lowerBound == null)
			lowerBound = 0;

		// this will be set by the first block in the switch below if it needs
		// to be
		this.chooseOneAction = false;
		this.choose = null;

		String query = getQuery(parameterObject);

		if(query.contains("~"))
		{
			if(parameterObject.thisID != -1)
			{
				SanitizedIdentified object = this.state.get(parameterObject.thisID);
				if(object != null)
				{
					if(object instanceof SanitizedNonStaticAbility)
						query = query.replace("~", ((SanitizedNonStaticAbility)object).shortName);
					else
						query = query.replace("~", object.name);
				}
			}
			else if(parameterObject.replacement != null)
			{
				query = query.replace("~", parameterObject.replacement);
			}
		}

		if(parameterObject.type.equals(PlayerInterface.ChoiceType.NORMAL_ACTIONS))
		{
			for(SanitizedPlayerAction action: (Collection<SanitizedPlayerAction>)parameterObject.choices)
				if(action.actOnID == -1)
				{
					query = query + "  (At least one action is accessible by clicking an empty area of the battlefield.)";
					break;
				}
		}

		this.displayChoiceText(query, true);

		this.clearIndicated();
		if(parameterObject.whatForID != -1)
			this.indicateObject(parameterObject.whatForID);

		if(parameterObject.type.isOrdered())
		{
			final JInternalFrame chooseDialog = new JInternalFrame();
			chooseDialog.setLayout(new BorderLayout());

			final ScrollingCardPanel.InnerCardPanel<Serializable> choicesPanel = new ScrollingCardPanel.InnerCardPanel<Serializable>(this)
			{
				private static final long serialVersionUID = 1L;

				@Override
				public Image getImage(Serializable ref)
				{
					if(ref instanceof SanitizedGameObject)
						return CardGraphics.getSmallCard((SanitizedGameObject)ref, this.gui.state, false, false, this.getFont());
					else if(ref instanceof SanitizedIdentified)
						return CardGraphics.renderTextAsSmallCard(((SanitizedIdentified)ref).name, this.getFont());
					return CardGraphics.renderTextAsSmallCard(ref.toString(), this.getFont());
				}

				@Override
				public Serializable getIdentified(Serializable ref)
				{
					return ref;
				}
			};
			ScrollingCardPanel scroll = new ScrollingCardPanel(choicesPanel);
			scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, this.mainWindow.getWidth() - 100));
			chooseDialog.add(scroll, BorderLayout.CENTER);
			choicesPanel.update(new LinkedList<Serializable>(parameterObject.choices), true);

			JButton doneButton = new JButton("Done");
			doneButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					for(Object object: choicesPanel.getObjects())
					{
						int index = parameterObject.choices.indexOf(object);
						if(index != -1)
							Play.this.choose.add(index);
					}
					Play.this.alertGuiEvent(new DoneClickedEvent());
				}
			});

			FlowLayout flow = new FlowLayout();
			flow.setAlignment(FlowLayout.RIGHT);
			JPanel bottomPanel = new JPanel(flow);
			bottomPanel.add(doneButton);
			chooseDialog.add(bottomPanel, BorderLayout.PAGE_END);

			this.choose = new LinkedList<Integer>();
			this.pushPopup(chooseDialog);
			return;
		}

		switch(parameterObject.type)
		{
		case NORMAL_ACTIONS:
		case ACTIVATE_MANA_ABILITIES:
			this.choices = new LinkedList<Object>(parameterObject.choices);
			this.choose = null;
			this.choiceType = parameterObject.type;
			this.chooseOneAction = (upperBound != null && upperBound == 1);

			if(parameterObject.type == PlayerInterface.ChoiceType.NORMAL_ACTIONS)
			{
				// has the user indicated they have no responses to anything
				// currently on the stack?
				if(!this.notRespondingTo.isEmpty())
				{
					// if the stack is empty, the "no responses period" is
					// finished and the user should get priority
					SanitizedZone stack = (SanitizedZone)this.state.get(this.state.stack);
					boolean noResponses = !stack.objects.isEmpty();

					// if the stack contains something the user hasn't
					// indicated they have no responses to, give the user
					// priority
					for(int ID: stack.objects)
						if(!this.notRespondingTo.contains(ID))
						{
							noResponses = false;
							break;
						}

					// if the user has no responses, pass priority on their
					// behalf
					if(noResponses)
					{
						this.pass();
						return;
					}

					// otherwise, clear the no responses list
					this.notRespondingTo = Collections.emptyList();
				}

				this.noResponsesButton.pushListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						Play.this.noResponses();
						Play.this.popDoneListeners();
					}
				}, "No Responses");
			}

			this.passButton.pushListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if(Play.this.choose == null)
						Play.this.choose = Collections.emptyList();
					else
						Play.this.clearIndicated();
					Play.this.popDoneListeners();
				}
			}, parameterObject.type == PlayerInterface.ChoiceType.NORMAL_ACTIONS ? "Pass Priority" : "Done");

			return;

		case ALTERNATE_COST:
		{
			this.chooseText(parameterObject, new CompareOnToString<T>());
			return;
		}
		case COLOR:
		{
			if(lowerBound != 1 || upperBound != 1)
				break;

			ColorChoosePanel colorPanel = new ColorChoosePanel(this, (List<Color>)parameterObject.choices);
			JInternalFrame colorWindow = new JInternalFrame();
			colorWindow.add(colorPanel);
			this.pushPopup(colorWindow);
			return;
		}

		case MANA_EXPLOSION:
		{
			this.chooseText((PlayerInterface.ChooseParameters<SanitizedCostCollection>)parameterObject, new CompareCostCollections());
			return;
		}

		case MANA_PAYMENT:
			this.choiceType = PlayerInterface.ChoiceType.MANA_PAYMENT;
			this.choose = new LinkedList<Integer>();
			final JPanel manaPanel = this.playerPanels.get(0).manaPanel;
			final ManaPool pool = ((SanitizedPlayer)this.state.get(this.playerID)).pool;
			final MouseInputListener mouse = new MouseInputAdapter()
			{
				private Set<Integer> addedNow = null;
				private Boolean adding = null;

				private int getIndex(MouseEvent e)
				{
					int y = e.getY();
					y /= (CardGraphics.LARGE_MANA_SYMBOL.height + 1);
					if(y < 0 || y >= pool.converted())
						return -1;
					return y;
				}

				@Override
				public void mouseDragged(MouseEvent e)
				{
					int hover = getIndex(e);
					if(hover != -1 && !this.addedNow.contains(hover))
					{
						if(this.adding == null)
							this.adding = !Play.this.choose.contains(hover);
						this.addedNow.add(hover);
						selectSymbol(hover, this.adding);
					}
				}

				@Override
				public void mousePressed(MouseEvent e)
				{
					this.addedNow = new HashSet<Integer>();

					this.mouseDragged(e);
				}

				@Override
				public void mouseReleased(MouseEvent e)
				{
					this.addedNow = null;
					this.adding = null;
				}

				private void selectSymbol(int index, boolean adding)
				{
					if(index == -1)
						return;

					if(Play.this.choose.contains(index))
					{
						if(!adding)
						{
							// Cast this to an object to remove that index
							// from the list, not the object at that index.
							Play.this.choose.remove((Object)index);
							manaPanel.repaint();
						}
					}
					else
					{
						if(adding)
						{
							Play.this.choose.add(index);
							manaPanel.repaint();
						}
					}
				}
			};
			manaPanel.addMouseListener(mouse);
			manaPanel.addMouseMotionListener(mouse);

			this.passButton.pushListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					Play.this.passButton.popListener();
					manaPanel.removeMouseListener(mouse);
					manaPanel.removeMouseMotionListener(mouse);
				}
			}, "Done");

			return;

		case MODE:
			ModeChoicePanel panel = new ModeChoicePanel(this, (List<SanitizedMode>)parameterObject.choices, parameterObject.number);
			JInternalFrame window = new JInternalFrame(JMagicTextPane.getModeChoiceText(parameterObject.number) + ".");
			window.add(panel);
			this.pushPopup(window);
			return;

		case ATTACK:
		case BLOCK:
		case DAMAGE_SOURCE:
		case OBJECTS:
			this.chooseObjectOrPlayer(parameterObject);
			return;

		case ATTACK_WHAT:
		case BLOCK_WHAT:
			this.clearIndicated();
			this.indicateObject(parameterObject.thisID);
			this.chooseObjectOrPlayer(parameterObject);
			return;

		case PLAYER:
			this.chooseObjectOrPlayer(parameterObject);
			return;

		case TARGETS:
			this.chooseTarget(parameterObject);
			return;

		case SINGLE_TARGET:
			// TODO :
			// this.chooseSingleTarget(parameterObject);
			this.chooseText(parameterObject, new CompareOnToString<T>());
			return;

		case PILE: // new dialog similar to object chooser but with check boxes
			// instead of clickable cards
			this.choosePile(parameterObject);
			return;

			// things we'll just handle with the Text dialog:
		case CLASS:
		case STRING:
		case ENUM:

			// TODO :
		case ACTION: // Text is probably fine
		case DAMAGE: // Text is fine? gotta identify the objects involved
		case EVENT: // Text is probably fine
		case COIN_FLIP: // use enum for this?
		case CREATURE_TYPE: // enum?
		case REPLACEMENT_EFFECT:

			// ordered types:
		case COSTS:
		case MOVEMENT_GRAVEYARD:
		case MOVEMENT_LIBRARY:
		case MOVEMENT_STACK:
		case OBJECTS_ORDERED:
		case TIMESTAMPS:
		case TRIGGERS:
		case ENUM_ORDERED:
			// all these choice types are ordered and are handled by the
			// "parameterObject.type.isordered" block at the top of this
			// method
		}

		this.chooseText(parameterObject, new CompareOnToString<T>());
	}

	private <T extends Serializable> void chooseText(final PlayerInterface.ChooseParameters<T> parameterObject, Comparator<T> comparator)
	{
		Integer lowerBound = Minimum.get(parameterObject.number);
		Integer upperBound = Maximum.get(parameterObject.number);
		boolean mustChooseExactlyOne = ((lowerBound == 1) && (upperBound == 1));
		TextChoicePanel panel = new TextChoicePanel(this, parameterObject.choices, comparator, mustChooseExactlyOne);
		JInternalFrame window = new JInternalFrame();
		window.add(panel);
		this.pushPopup(window);
	}

	public void chooseNumber(final NumberRange range, String description)
	{
		final TextField number = new TextField(6);

		final JInternalFrame window = new JInternalFrame();
		window.setLayout(new BorderLayout());
		window.add(number, BorderLayout.CENTER);

		this.displayChoiceText(description, true);
		this.mainWindow.toFront();

		this.passButton.setEnabled(false);

		ActionListener actionListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String input = number.getText();

				if((0 == input.length()) || input.matches(".*\\D.*"))
					return;

				int value = -1;

				try
				{
					value = Integer.parseInt(input);

					if(range.contains(value))
					{
						Play.this.chooseNumber = value;
						Play.this.alertGuiEvent(new DoneClickedEvent());
					}
				}
				catch(NumberFormatException ex)
				{
					// Illegal number, ignore it.
				}
			}
		};
		number.addActionListener(actionListener);

		final JButton doneButton = new JButton("Done");
		doneButton.addActionListener(actionListener);

		number.addTextListener(new TextListener()
		{
			@Override
			public void textValueChanged(TextEvent e)
			{
				String text = number.getText();
				if((0 == text.length()) || text.matches(".*\\D.*"))
					doneButton.setEnabled(false);
				else
					doneButton.setEnabled(true);
			}
		});

		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.RIGHT);
		JPanel bottomPanel = new JPanel(flow);
		bottomPanel.add(doneButton);
		window.add(bottomPanel, BorderLayout.PAGE_END);

		this.pushPopup(window);
	}

	private <T extends Serializable> void chooseObjectOrPlayer(PlayerInterface.ChooseParameters<T> parameterObject) throws InternalError
	{
		this.choices = new LinkedList<Object>(parameterObject.choices);
		this.choose = new LinkedList<Integer>();
		this.choiceType = parameterObject.type;

		List<Integer> choiceIDsForDialog = new LinkedList<Integer>();
		Collection<Integer> hands = new LinkedList<Integer>();
		for(int player: this.state.players)
			hands.add(((SanitizedPlayer)this.state.get(player)).hand);
		boolean showDialog = false;
		for(T t: parameterObject.choices)
		{
			if(!(t instanceof SanitizedGameObject))
				continue;

			SanitizedGameObject object = (SanitizedGameObject)t;
			choiceIDsForDialog.add(object.ID);

			if(object.ghost)
				showDialog = true;

			if(object.zoneID == this.state.battlefield)
				continue;

			if(hands.contains(object.zoneID))
				continue;

			showDialog = true;
		}

		if(showDialog)
		{
			ObjectChoosePanel objectPanel = new ObjectChoosePanel(this, choiceIDsForDialog);
			final JInternalFrame objectWindow = new JInternalFrame();
			objectWindow.setLayout(new BorderLayout());
			objectWindow.add(objectPanel, BorderLayout.CENTER);

			FlowLayout flow = new FlowLayout();
			flow.setAlignment(FlowLayout.RIGHT);
			JPanel bottomPanel = new JPanel(flow);

			JButton doneButton = new JButton("Done");
			doneButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					Play.this.alertGuiEvent(new DoneClickedEvent());
				}
			});
			bottomPanel.add(doneButton);

			objectWindow.add(bottomPanel, BorderLayout.PAGE_END);

			this.pushPopup(objectWindow);
			return;
		}

		this.passButton.pushListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Play.this.choiceType = null;
				Play.this.passButton.popListener();
			}
		}, "Done");
	}

	// TODO : This is a copy+paste of chooseObject with tweaks. How much of this
	// can be extracted?
	private <T extends Serializable> void chooseTarget(PlayerInterface.ChooseParameters<T> parameterObject) throws InternalError
	{
		this.choices = new LinkedList<Object>();
		for(Object t: parameterObject.choices)
			this.choices.add(this.state.get(((SanitizedTarget)t).targetID));

		this.choose = new LinkedList<Integer>();
		this.choiceType = parameterObject.type;

		List<Integer> choiceIDsForDialog = new LinkedList<Integer>();
		Collection<Integer> hands = new LinkedList<Integer>();
		for(int player: this.state.players)
			hands.add(((SanitizedPlayer)this.state.get(player)).hand);
		boolean showDialog = false;
		for(T t: parameterObject.choices)
		{
			SanitizedTarget target = (SanitizedTarget)t;
			SanitizedIdentified targetted = this.state.get(target.targetID);
			choiceIDsForDialog.add(targetted.ID);

			if(targetted instanceof SanitizedGameObject)
			{
				SanitizedGameObject object = (SanitizedGameObject)targetted;
				if(object.zoneID == this.state.battlefield)
					continue;

				if(hands.contains(object.zoneID))
					continue;

				showDialog = true;
			}
		}

		if(showDialog)
		{
			ObjectChoosePanel objectPanel = new ObjectChoosePanel(this, choiceIDsForDialog);
			final JInternalFrame objectWindow = new JInternalFrame();
			objectWindow.setLayout(new BorderLayout());
			objectWindow.add(objectPanel, BorderLayout.CENTER);

			JButton doneButton = new JButton("Done");
			doneButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					Play.this.alertGuiEvent(new DoneClickedEvent());
				}
			});

			FlowLayout flow = new FlowLayout();
			flow.setAlignment(FlowLayout.RIGHT);
			JPanel bottomPanel = new JPanel(flow);
			bottomPanel.add(doneButton);
			objectWindow.add(bottomPanel, BorderLayout.PAGE_END);

			this.pushPopup(objectWindow);
			return;
		}

		this.passButton.pushListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Play.this.choiceType = null;
				Play.this.passButton.popListener();
			}
		}, "Done");

		return;
	}

	@SuppressWarnings({"unchecked", "unused"})
	private <T extends Serializable> void chooseSingleTarget(final PlayerInterface.ChooseParameters<T> parameterObject)
	{
		this.choiceType = PlayerInterface.ChoiceType.SINGLE_TARGET;

		final JInternalFrame chooseDialog = new JInternalFrame();
		chooseDialog.setLayout(new BoxLayout(chooseDialog.getContentPane(), BoxLayout.Y_AXIS));

		final ScrollingCardPanel.InnerCardPanel<SanitizedTarget> cardPanel = new ScrollingCardPanel.InnerCardPanel<SanitizedTarget>(this)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public Image getImage(SanitizedTarget target)
			{
				Image smallCard = null;
				SanitizedIdentified ref = this.gui.state.get(target.targetID);
				if(ref instanceof SanitizedGameObject)
					smallCard = CardGraphics.getSmallCard(ref, this.gui.state, false, false, this.getFont());
				else
					smallCard = CardGraphics.renderTextAsSmallCard(ref.name, this.getFont());

				CardGraphics g2 = new CardGraphics(smallCard.getGraphics(), this.gui.state);
				g2.setBackground(Color.WHITE);
				g2.setColor(Color.BLACK);
				g2.clearRect(CardGraphics.SMALL_CARD_PADDING_LEFT, CardGraphics.SMALL_CARD_PADDING_TOP, CardGraphics.SMALL_CARD_TEXT_WIDTH, CardGraphics.SMALL_CARD_TOTAL_TEXT_HEIGHT);
				g2.drawRect(CardGraphics.SMALL_CARD_PADDING_LEFT, CardGraphics.SMALL_CARD_PADDING_TOP, CardGraphics.SMALL_CARD_TEXT_WIDTH, CardGraphics.SMALL_CARD_TOTAL_TEXT_HEIGHT);
				g2.drawCardText(target.name, g2.getFont(), CardGraphics.SMALL_CARD_PADDING_LEFT, CardGraphics.SMALL_CARD_PADDING_TOP, new Dimension(CardGraphics.SMALL_CARD_TEXT_WIDTH, CardGraphics.SMALL_CARD_TOTAL_TEXT_HEIGHT), false, true);

				return smallCard;
			}

			@Override
			public SanitizedIdentified getIdentified(SanitizedTarget ref)
			{
				return this.gui.state.get(ref.targetID);
			}
		};
		ScrollingCardPanel scroll = new ScrollingCardPanel(cardPanel);
		scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, this.mainWindow.getWidth() - 100));
		cardPanel.update(new LinkedList<SanitizedTarget>((Collection<SanitizedTarget>)parameterObject.choices), true);
		chooseDialog.add(scroll);

		JButton doneButton = new JButton("Done");
		doneButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Play.this.alertGuiEvent(new DoneClickedEvent());
			}
		});

		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.RIGHT);
		JPanel bottomPanel = new JPanel(flow);
		bottomPanel.add(doneButton);
		chooseDialog.add(bottomPanel);

		this.choose = new LinkedList<Integer>();
		this.pushPopup(chooseDialog);
	}

	@SuppressWarnings("unchecked")
	private <T extends Serializable> void choosePile(final PlayerInterface.ChooseParameters<T> parameterObject)
	{
		final JInternalFrame chooseDialog = new JInternalFrame();
		chooseDialog.setLayout(new BoxLayout(chooseDialog.getContentPane(), BoxLayout.Y_AXIS));

		boolean chooseExactlyOne = Minimum.get(parameterObject.number) == 1 && Maximum.get(parameterObject.number) == 1;
		ButtonGroup buttons = chooseExactlyOne ? new ButtonGroup() : null;
		final List<JToggleButton> toggles = new LinkedList<JToggleButton>();

		final boolean renderCounters = Boolean.parseBoolean(this.properties.getProperty(PropertyKeys.RENDER_COUNTERS));
		for(T pile: parameterObject.choices)
		{
			final ScrollingCardPanel.InnerCardPanel<SanitizedIdentified> pilePanel = new ScrollingCardPanel.InnerCardPanel<SanitizedIdentified>(this)
			{
				private static final long serialVersionUID = 1L;

				@Override
				public Image getImage(SanitizedIdentified ref)
				{
					if(ref instanceof SanitizedGameObject)
						return CardGraphics.getSmallCard(ref, this.gui.state, false, renderCounters, this.getFont());
					return CardGraphics.renderTextAsSmallCard(ref.name, this.getFont());
				}

				@Override
				public SanitizedIdentified getIdentified(SanitizedIdentified ref)
				{
					return ref;
				}
			};
			ScrollingCardPanel scroll = new ScrollingCardPanel(pilePanel);
			scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, this.mainWindow.getWidth() - 100));
			pilePanel.update(new LinkedList<SanitizedIdentified>((Collection<SanitizedIdentified>)pile), true);

			Box container = Box.createHorizontalBox();

			JToggleButton button = null;
			if(chooseExactlyOne)
			{
				button = new JRadioButton();
				buttons.add(button);
			}
			else
				button = new JCheckBox();
			toggles.add(button);
			container.add(button);
			container.add(scroll);

			chooseDialog.add(container);
		}

		JButton doneButton = new JButton("Done");
		doneButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Play.this.choose.clear();
				for(int i = 0; i < toggles.size(); ++i)
				{
					JToggleButton toggle = toggles.get(i);
					if(toggle.isSelected())
						Play.this.choose.add(i);
				}
				Play.this.alertGuiEvent(new DoneClickedEvent());
			}
		});

		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.RIGHT);
		JPanel bottomPanel = new JPanel(flow);
		bottomPanel.add(doneButton);
		chooseDialog.add(bottomPanel);

		this.choose = new LinkedList<Integer>();
		this.pushPopup(chooseDialog);
	}

	private void populateScrollListener(Component c, MouseWheelListener l)
	{
		c.addMouseWheelListener(l);
		if(c instanceof Container)
			for(Component c2: ((Container)c).getComponents())
				populateScrollListener(c2, l);
	}

	public void setUpMiscZonePanels(SanitizedGameState state)
	{
		this.zones.clear();
		this.zones.put("Stack", state.stack);
		this.zones.put("Exile zone", state.exileZone);
		this.zones.put("Command zone", state.commandZone);
		SanitizedPlayer playing = (SanitizedPlayer)(state.get(this.playerID));
		this.zones.put("Your library", playing.library);
		this.zones.put("Your graveyard", playing.graveyard);
		this.zones.put("Your sideboard", playing.sideboard);
		for(Integer i: state.players)
		{
			if(i == this.playerID)
				continue;

			SanitizedPlayer p = (SanitizedPlayer)(state.get(i));
			this.zones.put(p.name + "'s library", p.library);
			this.zones.put(p.name + "'s graveyard", p.graveyard);
			this.zones.put(p.name + "'s sideboard", p.sideboard);
		}

		zoneLoop: for(SanitizedZone zone: new MagicSet(state.values()).getAll(SanitizedZone.class))
			if(!this.zones.containsValue(zone.ID) && zone.ID != state.battlefield)
			{
				for(Integer i: state.players)
					if(((SanitizedPlayer)state.get(i)).hand == zone.ID)
						continue zoneLoop;
				this.zones.put(zone.name, zone.ID);
			}
	}

	public void create()
	{
		this.configuration = new ConfigurationFrame(this.properties);
		for(ConfigurationFrame.OptionPanel optionPanel: this.options)
			this.configuration.addOptionPanel(optionPanel);

		this.cardInfoPanel = new CardInfoPanel(this);
		MouseWheelListener scrollListener = this.cardInfoPanel.scroll.getMouseWheelListeners()[0];

		this.statusLine = new MultilineLabel(4);
		this.statusLine.setText(" ");
		this.statusLine.setOpaque(true);
		Font font = new JLabel().getFont();
		this.statusLine.setFont(font.deriveFont(Font.BOLD, 1.25F * font.getSize()));
		this.statusLine.setMaxWidth(CardGraphics.LARGE_CARD.width);

		this.passButton = new DoneButton("Pass Priority");
		this.passButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Play.this.alertGuiEvent(new DoneClickedEvent());
			}
		});

		this.noResponsesButton = new DoneButton("No Responses");
		this.noResponsesButton.setToolTipText(noResponsesText);

		this.setUpMiscZonePanels(this.state);

		MiscZonePanel stackPanel = new MiscZonePanel(this, this.properties.getProperty(PropertyKeys.MISC_ZONE_L));
		this.zoneContentsPanels.add(stackPanel);

		MiscZonePanel exilePanel = new MiscZonePanel(this, this.properties.getProperty(PropertyKeys.MISC_ZONE_R));
		this.zoneContentsPanels.add(exilePanel);

		JPanel miscObjectsPanel = new JPanel(new GridBagLayout());
		// Don't define an inset on the bottom except for the last component
		Insets normalInsets = new Insets(labelPaddingAmount, labelPaddingAmount, 0, labelPaddingAmount);
		Insets bottomInsets = new Insets(labelPaddingAmount, labelPaddingAmount, labelPaddingAmount, labelPaddingAmount);

		{
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.CENTER;
			c.gridwidth = 2;
			c.gridx = 0;
			c.gridy = 0;
			c.insets = normalInsets;
			c.weightx = 0;
			c.weighty = 0;
			miscObjectsPanel.add(this.cardInfoPanel, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 2;
			c.gridx = 0;
			c.gridy = 1;
			c.insets = normalInsets;
			c.weightx = 1;
			c.weighty = 0;
			miscObjectsPanel.add(this.statusLine, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 2;
			c.insets = normalInsets;
			c.weightx = 1;
			c.weighty = 0;
			miscObjectsPanel.add(this.passButton, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 2;
			c.insets = normalInsets;
			c.weightx = 1;
			c.weighty = 0;
			miscObjectsPanel.add(this.noResponsesButton, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.VERTICAL;
			c.gridx = 0;
			c.gridy = 3;
			c.insets = bottomInsets;
			c.weightx = 0;
			c.weighty = 1;
			miscObjectsPanel.add(stackPanel, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.VERTICAL;
			c.gridx = 1;
			c.gridy = 3;
			c.insets = bottomInsets;
			c.weightx = 0;
			c.weighty = 1;
			miscObjectsPanel.add(exilePanel, c);
		}

		PlayerPanel playerPanel = new PlayerPanel(this, this.playerID);
		this.playerPanels.add(playerPanel);

		JPanel opponentsPanel = new JPanel(new GridLayout(1, 0));
		Iterator<Integer> playerCycle = this.state.getPlayerCycle(this.playerID).iterator();
		// Don't process the first player (should be the user)
		playerCycle.next();
		while(playerCycle.hasNext())
		{
			PlayerPanel opponentPanel = new PlayerPanel(this, playerCycle.next());
			this.playerPanels.add(opponentPanel);
			opponentsPanel.add(opponentPanel);
		}

		this.battlefieldPanel = new BattlefieldPanel(this);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(opponentsPanel, BorderLayout.PAGE_START);
		mainPanel.add(this.battlefieldPanel);
		mainPanel.add(playerPanel, BorderLayout.PAGE_END);

		this.mainWindow = new JFrame("jMagic");
		JComponent glassPane = new JComponent()
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g)
			{
				super.paintComponent(g);

				Map<Arrow.ArrowType, Boolean> arrowEnabled = new HashMap<Arrow.ArrowType, Boolean>();
				Map<Arrow.ArrowType, Color> arrowColor = new HashMap<Arrow.ArrowType, Color>();

				int arrowFocus = Play.this.cardInfoPanel.getArrowFocus();
				Collection<Arrow> focusArrows = Play.this.arrows.get(arrowFocus);
				if(focusArrows != null && !focusArrows.isEmpty())
				{
					CardGraphics cg = new CardGraphics(g, Play.this.state);

					for(Arrow arrow: focusArrows)
					{
						if(!arrowEnabled.containsKey(arrow.type))
						{
							boolean enabled = Boolean.parseBoolean(Play.this.properties.getProperty(PropertyKeys.getArrowEnabledKey(arrow.type)));
							arrowEnabled.put(arrow.type, enabled);

							if(enabled)
							{
								Color color = PropertyKeys.colorFromString(Play.this.properties.getProperty(PropertyKeys.getArrowColorKey(arrow.type)));
								arrowColor.put(arrow.type, color);
							}
						}

						if(arrowEnabled.get(arrow.type))
						{
							Point source = Play.this.cardLocations.get(arrow.sourceID);
							Point target = Play.this.cardLocations.get(arrow.targetID);
							if(target != null && source != null)
								cg.drawArrow(source, target, arrowColor.get(arrow.type), arrow.type.isHollow());
						}
					}
				}
			}
		};
		this.mainWindow.setGlassPane(glassPane);
		glassPane.setVisible(true);
		this.mainWindow.addMouseWheelListener(scrollListener);
		this.mainWindow.add(miscObjectsPanel, BorderLayout.LINE_START);

		this.logPanel = new LogPanel();

		final JTextField messageField = new JTextField();
		messageField.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Play.this.messagePoster.postMessage(messageField.getText());
				messageField.setText("");
			}
		});

		this.turnLabel = new JLabel(" ");
		this.turnLabel.setFont(this.turnLabel.getFont().deriveFont(Font.BOLD));

		this.stepLabel = new StepLabel();

		JPanel choiceInfoPanel = new JPanel(new GridBagLayout());

		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy = 0;
			c.insets = normalInsets;
			c.weightx = 1;
			c.weighty = 1;
			choiceInfoPanel.add(this.logPanel, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy = 1;
			c.insets = normalInsets;
			c.weightx = 1;
			c.weighty = 0;
			choiceInfoPanel.add(messageField, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 2;
			c.insets = normalInsets;
			c.weightx = 1;
			c.weighty = 0;
			choiceInfoPanel.add(this.turnLabel, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 3;
			c.insets = bottomInsets;
			c.weightx = 1;
			c.weighty = 0;
			choiceInfoPanel.add(this.stepLabel, c);
		}

		this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, mainPanel, choiceInfoPanel);
		this.splitPane.setResizeWeight(1);
		final BasicSplitPaneDivider divider = ((BasicSplitPaneUI)this.splitPane.getUI()).getDivider();
		divider.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent me)
			{
				Play.this.mainWindow.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent me)
			{
				Play.this.mainWindow.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});

		this.mainWindow.add(this.splitPane);

		this.mainWindow.add(new MainMenu(this), BorderLayout.PAGE_START);

		populateScrollListener(this.mainWindow, scrollListener);
		this.mainWindow.getGlassPane().removeMouseWheelListener(scrollListener);

		this.mainWindow.pack();

		this.mainWindow.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent event)
			{
				for(PlayerPanel p: Play.this.playerPanels)
					p.update(false);
			}
		});
		this.mainWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.mainWindow.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				synchronized(Play.this.creatingThread)
				{
					Play.this.creatingThread.interrupt();
				}
			}
		});
		this.mainWindow.setVisible(true);
	}

	public void displayChoiceText(String text, boolean active)
	{
		this.statusLine.setText(0 == text.length() ? " " : text);

		if(active)
		{
			this.statusLine.setBackground(UIManager.getColor("EditorPane.selectionBackground"));
			this.statusLine.setForeground(UIManager.getColor("EditorPane.selectionForeground"));
		}
		else
		{
			this.statusLine.setBackground(UIManager.getColor("Label.background"));
			this.statusLine.setForeground(UIManager.getColor("Label.foreground"));
		}

		this.mainWindow.setTitle("jMagic" + (0 == text.length() ? "" : (" - " + text)));
	}

	/**
	 * Causes the user to divide a quantity across objects or players.
	 * 
	 * @param quantity What quantity is being divided
	 * @param minimum The minimum that each object or player must receive
	 * @param whatFrom ID of an object that is causing the division (creature in
	 * combat, spell/ability distributing damage or counters, etc)
	 * @param beingDivided the thing being divided, i.e., "damage", "counters",
	 * etc
	 * @param targets [in] The objects and/or players over which to divide
	 * <code>quantity</code> [out] Same as in, with
	 * {@link SanitizedTarget#division} set for each object or player indicating
	 * how much of <code>quantity</code> that object or player has received
	 */
	public void divide(int quantity, int minimum, int whatFrom, String beingDivided, final List<SanitizedTarget> targets)
	{
		final String oldTitle = this.mainWindow.getTitle();
		this.displayChoiceText("Divide " + quantity + " " + beingDivided + ".", true);
		this.mainWindow.toFront();

		this.divisions = new HashMap<Integer, Integer>();
		this.divideSomewhatSensibly(quantity, minimum, targets);

		this.clearIndicated();
		this.indicateObject(whatFrom);
		this.mainWindow.repaint();

		this.passButton.pushListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Play.this.passButton.popListener();
				synchronized(this)
				{
					for(SanitizedTarget target: targets)
						target.division = Play.this.divisions.get(target.targetID);
					Play.this.choiceReady();
					Play.this.clearIndicated();
					Play.this.divisions = null;
					Play.this.mainWindow.setTitle(oldTitle);
				}
			}
		}, "Done");
	}

	/**
	 * Not a perfect division algorithm (which doesn't exist), but sensible in
	 * trying to assign damage
	 */
	private void divideSomewhatSensibly(int quantity, int minimum, List<SanitizedTarget> targets)
	{
		List<SanitizedTarget> dump = new LinkedList<SanitizedTarget>();
		int total = 0;

		// Assign the minimum to every target
		for(int i = 0; i < targets.size(); ++i)
		{
			SanitizedTarget target = targets.get(i);
			if(this.state.get(target.targetID) instanceof SanitizedPlayer)
				dump.add(target);
			this.divisions.put(target.targetID, minimum);
			total += minimum;
		}

		// Assign as much damage as is required to kill any GameObject
		// targets
		for(SanitizedTarget target: targets)
		{
			SanitizedIdentified thing = this.state.get(target.targetID);
			if(thing instanceof SanitizedGameObject)
			{
				SanitizedGameObject object = (SanitizedGameObject)thing;
				int assigned = this.divisions.get(target.targetID);
				int toughness = object.characteristics.get(SanitizedGameObject.CharacteristicSet.ACTUAL).toughness;
				int extra = Math.max(0, toughness - object.damage - assigned);
				total += extra;

				if(total > quantity)
				{
					extra -= total - quantity;
					total = quantity;
				}

				this.divisions.put(target.targetID, assigned + extra);
			}
		}

		// Divide whatever's left among the dump targets
		if(dump.isEmpty())
			dump = targets;
		int extra = (quantity - total) / dump.size();
		if(extra * dump.size() != quantity - total)
			extra = extra + 1;
		for(SanitizedTarget target: dump)
		{
			int division = this.divisions.get(target.targetID) + extra;
			total += extra;

			if(total > quantity)
			{
				division -= total - quantity;
				total = quantity;
			}
			this.divisions.put(target.targetID, division);
		}
	}

	private void finishUpdateDivision(int id, String text, JInternalFrame pop)
	{
		try
		{
			this.divisions.put(id, Integer.parseInt(text));
		}
		catch(NumberFormatException ex)
		{
			// Unparseable number, ignore it
		}
		((JComponent)this.mainWindow.getGlassPane()).remove(pop);
		pop.dispose();
		if(this.dividingOn == id)
			this.dividingOn = -1;
		this.mainWindow.repaint();
	}

	public ChatManager.Callback getChatCallback()
	{
		return new ChatManager.Callback()
		{
			@Override
			public void gotMessage(String message)
			{
				Play.this.logPanel.addLine(message);
			}
		};
	}

	public ConfigurationFrame.OptionPanel getOptionPanel()
	{
		return new PlayOptions();
	}

	public Image getSmallCardImage(SanitizedIdentified object, boolean renderDamage, boolean renderCounters, Font font)
	{
		if(object == null)
			return CardGraphics.getSmallCard(null, this.state, renderDamage, renderCounters, font);

		int key = System.identityHashCode(object);

		if(!this.smallImageCache.containsKey(key))
			this.smallImageCache.put(key, CardGraphics.getSmallCard(object, this.state, renderDamage, renderCounters, font));

		int index = -1;
		if(this.choices != null)
			index = this.choices.indexOf(object);
		if((this.choose != null && this.choose.contains(index)) || (this.indicated.contains(object.ID)))
		{
			BufferedImage ret = new BufferedImage(CardGraphics.SMALL_CARD.width, CardGraphics.SMALL_CARD.height, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = ret.createGraphics();
			graphics.drawImage(this.smallImageCache.get(key), 0, 0, null);
			graphics.drawImage(CardGraphics.getImage("smallframes/select.png"), 0, 0, null);

			return ret;
		}

		return this.smallImageCache.get(key);
	}

	void battlefieldClicked(MouseEvent mouseEvent)
	{
		this.alertGuiEvent(new BattlefieldClickEvent(mouseEvent));
	}

	void identifiedMouseEvent(final SanitizedIdentified chosenIdentified, MouseEvent mouseEvent)
	{
		this.alertGuiEvent(new IdentifiedMouseEvent(chosenIdentified, mouseEvent));
	}

	/**
	 * Removes all objects from the list of things to indicate.
	 */
	public void clearIndicated()
	{
		this.indicated.clear();
	}

	/**
	 * Graphically "indicates" the object with the specified ID, in addition to
	 * the other objects already indicated.
	 * 
	 * @param ID The ID of the object to indicate.
	 */
	public void indicateObject(int ID)
	{
		this.indicated.add(ID);
	}

	public boolean isReady()
	{
		return this.mainWindow.isVisible();
	}

	private void noResponses()
	{
		SanitizedIdentified stack = this.state.get(this.state.stack);
		this.notRespondingTo = new LinkedList<Integer>(((SanitizedZone)stack).objects);
		this.pass();
	}

	void pass()
	{
		this.choose = new LinkedList<Integer>();
		this.choiceReady();
	}

	void popDoneListeners()
	{
		if(this.passButton.hasListeners())
			this.passButton.popListener();
		if(this.noResponsesButton.hasListeners())
			this.noResponsesButton.popListener();
	}

	public void popPopup()
	{
		JInternalFrame window = this.popups.pop();
		((JComponent)this.mainWindow.getGlassPane()).remove(window);
		window.dispose();
		this.mainWindow.repaint();
	}

	public void pushPopup(JInternalFrame window)
	{
		window.setMaximumSize(new Dimension(this.mainWindow.getWidth() - 100, this.mainWindow.getHeight() - 100));
		this.popups.push(window);
		((JComponent)this.mainWindow.getGlassPane()).add(window);
		window.pack();
		Dimension popupSize = window.getSize();
		Dimension totalSize = this.mainWindow.getSize();
		window.setLocation((totalSize.width - popupSize.width) / 2, (totalSize.height - popupSize.height) / 2);
		window.setVisible(true);
	}

	boolean alwaysShowActionsPopup()
	{
		return Boolean.parseBoolean(this.properties.getProperty(PropertyKeys.ACTIONS_POPUP));
	}

	boolean beepOnYourTurn()
	{
		return Boolean.parseBoolean(this.properties.getProperty(PropertyKeys.BEEP_ON_YOUR_TURN));
	}

	public void removeIndication(int ID)
	{
		this.indicated.remove((Object)ID);
	}

	public void setMessagePoster(ChatManager.MessagePoster messagePoster)
	{
		this.messagePoster = messagePoster;
	}

	public void setPlayerID(int playerID)
	{
		this.playerID = playerID;
	}

	public void setProperties(Properties properties)
	{
		this.properties = properties;
		for(Arrow.ArrowType type: Arrow.ArrowType.values())
		{
			String colorKey = PropertyKeys.getArrowColorKey(type);
			if(properties.getProperty(colorKey) == null)
				properties.setProperty(colorKey, PropertyKeys.colorToString(type.getDefaultColor()));

			String enabledKey = PropertyKeys.getArrowEnabledKey(type);
			if(properties.getProperty(enabledKey) == null)
				properties.setProperty(enabledKey, Boolean.toString(true));
		}
		if(properties.getProperty(PropertyKeys.ROTATE_OPP_CARDS) == null)
			properties.setProperty(PropertyKeys.ROTATE_OPP_CARDS, Boolean.toString(false));
		if(properties.getProperty(PropertyKeys.ACTIONS_POPUP) == null)
			properties.setProperty(PropertyKeys.ACTIONS_POPUP, Boolean.toString(true));
		if(properties.getProperty(PropertyKeys.BEEP_ON_YOUR_TURN) == null)
			properties.setProperty(PropertyKeys.BEEP_ON_YOUR_TURN, Boolean.toString(false));
		if(properties.getProperty(PropertyKeys.MISC_ZONE_L) == null)
			properties.setProperty(PropertyKeys.MISC_ZONE_L, "Stack");
		if(properties.getProperty(PropertyKeys.MISC_ZONE_R) == null)
			properties.setProperty(PropertyKeys.MISC_ZONE_R, "Your graveyard");
		if(properties.getProperty(PropertyKeys.HIGHLIGHT_SELECTED_CARDS) == null)
			properties.setProperty(PropertyKeys.HIGHLIGHT_SELECTED_CARDS, Boolean.toString(true));
		if(properties.getProperty(PropertyKeys.RENDER_DAMAGE) == null)
			properties.setProperty(PropertyKeys.RENDER_DAMAGE, Boolean.toString(true));
		if(properties.getProperty(PropertyKeys.RENDER_COUNTERS) == null)
			properties.setProperty(PropertyKeys.RENDER_COUNTERS, Boolean.toString(true));
	}

	/**
	 * Alert the GUI to an error in the engine.
	 * 
	 * @param parameters Not currently used, but would hold additional
	 * information if any were available
	 */
	public void alertError(PlayerInterface.ErrorParameters parameters)
	{
		if(null != this.mainWindow && this.mainWindow.isVisible())
		{
			String message = "An unknown error occurred in the host.";
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
			JOptionPane.showMessageDialog(this.mainWindow, message, "jMagic Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void alertStateReversion(PlayerInterface.ReversionParameters parameters)
	{
		StringBuilder str = new StringBuilder();
		str.append(parameters.message);
		str.append(" The game state was reverted.");
		this.logPanel.addLine(str.toString());
	}
}
