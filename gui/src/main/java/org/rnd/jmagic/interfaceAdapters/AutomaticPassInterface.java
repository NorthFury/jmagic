package org.rnd.jmagic.interfaceAdapters;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.gui.dialogs.ConfigurationFrame;
import org.rnd.jmagic.sanitized.*;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import java.io.Serializable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * An adapter interface to automatically pass during certain steps/phases/turns.
 * 
 * Defaults:
 * 
 * <pre>
 *           Your turn   Opponents' turns
 * Untap         Yes           Yes
 * Upkeep        Yes           Yes
 * Draw          Yes           Yes
 * Pre-C. Main    No           Yes
 * Beg. C.       Yes           Yes
 * Decl. A.       No            No
 * Decl. B.       No            No
 * C. Damage     Yes           Yes
 * End C.        Yes           Yes
 * Post-C. Main   No           Yes
 * End            No            No
 * Cleanup        No            No
 * </pre>
 */
public class AutomaticPassInterface extends SimpleConfigurableInterface
{
	private final static class APIOptionPanel extends ConfigurationFrame.OptionPanel
	{
		private static final long serialVersionUID = 1L;

		private Map<Step.StepType, JCheckBox> playerOptions;
		private Map<Step.StepType, JCheckBox> opponentOptions;

		public APIOptionPanel()
		{
			super("Automatic Passes");

			this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

			this.playerOptions = new HashMap<Step.StepType, JCheckBox>();
			this.opponentOptions = new HashMap<Step.StepType, JCheckBox>();

			Box player = Box.createVerticalBox();
			player.setBorder(BorderFactory.createTitledBorder("Player"));

			Box opponent = Box.createVerticalBox();
			opponent.setBorder(BorderFactory.createTitledBorder("Opponent"));

			for(Step.StepType step: Step.StepType.values())
			{
				JCheckBox playerBox = new JCheckBox(step.toString());
				this.playerOptions.put(step, playerBox);
				player.add(playerBox);

				JCheckBox opponentBox = new JCheckBox(step.toString());
				this.opponentOptions.put(step, opponentBox);
				opponent.add(opponentBox);
			}

			Box hBox = Box.createHorizontalBox();
			hBox.add(player);
			hBox.add(opponent);

			Box vBox = Box.createVerticalBox();
			vBox.add(hBox);
			vBox.add(Box.createVerticalGlue());

			this.add(vBox);
		}

		@Override
		public void loadSettings(Properties properties)
		{
			for(Step.StepType step: Step.StepType.values())
			{
				this.playerOptions.get(step).setSelected(AutomaticPassInterface.getAutomaticPassOnPlayerStep(properties, step));
				this.opponentOptions.get(step).setSelected(AutomaticPassInterface.getAutomaticPassOnOpponentStep(properties, step));
			}
		}

		@Override
		public void saveChanges(Properties properties)
		{
			for(Step.StepType step: Step.StepType.values())
			{
				AutomaticPassInterface.setAutomaticPassOnPlayerStep(properties, step, this.playerOptions.get(step).isSelected());
				AutomaticPassInterface.setAutomaticPassOnOpponentStep(properties, step, this.opponentOptions.get(step).isSelected());
			}
		}
	}

	private static final String getKey(Step.StepType type, boolean player)
	{
		return "AutomaticPassInterface.PassStep." + (player ? "Player" : "Opponent") + "." + type.name();
	}

	private static final Set<Step.StepType> playerDefault = EnumSet.of(Step.StepType.UNTAP, Step.StepType.UPKEEP, Step.StepType.DRAW, Step.StepType.BEGINNING_OF_COMBAT, Step.StepType.COMBAT_DAMAGE, Step.StepType.END_OF_COMBAT);
	private static final Set<Step.StepType> opponentDefault = EnumSet.of(Step.StepType.UNTAP, Step.StepType.UPKEEP, Step.StepType.DRAW, Step.StepType.PRECOMBAT_MAIN, Step.StepType.BEGINNING_OF_COMBAT, Step.StepType.COMBAT_DAMAGE, Step.StepType.END_OF_COMBAT, Step.StepType.POSTCOMBAT_MAIN);

	private SanitizedGameState state;
	private int lastPassedTurn;
	private Step.StepType lastPassedStep;
	private int playerID;
	private Properties properties;

	public AutomaticPassInterface(ConfigurableInterface adapt)
	{
		super(adapt);

		this.state = null;
		this.lastPassedTurn = -1;
		this.lastPassedStep = null;
		this.playerID = -1;
		this.properties = null;
	}

	@Override
	public void alertState(SanitizedGameState sanitizedGameState)
	{
		this.state = sanitizedGameState;
		super.alertState(sanitizedGameState);
	}

	@Override
	public <T extends Serializable> List<Integer> choose(ChooseParameters<T> parameterObject)
	{
		if(parameterObject.type == ChoiceType.NORMAL_ACTIONS)
		{
			SanitizedZone stack = (SanitizedZone)(this.state.get(this.state.stack));
			if(stack.objects.isEmpty())
			{
				boolean pass = false;

				if(this.lastPassedTurn != this.state.turn || this.lastPassedStep != this.state.step)
				{
					if(this.state.turn == this.playerID)
					{
						if(getAutomaticPassOnPlayerStep(this.properties, this.state.step))
							pass = true;
					}
					else
					{
						if(getAutomaticPassOnOpponentStep(this.properties, this.state.step))
							pass = true;
					}
				}

				this.lastPassedTurn = this.state.turn;
				this.lastPassedStep = this.state.step;

				if(pass)
					return Collections.emptyList();
			}
			else
			{
				// The player might want to respond after the spell resolves.
				this.lastPassedTurn = this.state.turn;
				this.lastPassedStep = this.state.step;
			}
		}
		return super.choose(parameterObject);
	}

	@Override
	public void setPlayerID(int playerID)
	{
		this.playerID = playerID;
		super.setPlayerID(playerID);
	}

	@Override
	public void setProperties(Properties properties)
	{
		this.properties = properties;

		for(Step.StepType step: Step.StepType.values())
		{
			String playerKey = getKey(step, true);
			if(!properties.containsKey(playerKey))
				properties.setProperty(playerKey, Boolean.toString(playerDefault.contains(step)));

			String oppKey = getKey(step, false);
			if(!properties.containsKey(oppKey))
				properties.setProperty(oppKey, Boolean.toString(opponentDefault.contains(step)));
		}

		super.setProperties(properties);
	}

	public static boolean getAutomaticPassOnPlayerStep(Properties properties, Step.StepType type)
	{
		return Boolean.parseBoolean(properties.getProperty(getKey(type, true)));
	}

	public static boolean getAutomaticPassOnOpponentStep(Properties properties, Step.StepType type)
	{
		return Boolean.parseBoolean(properties.getProperty(getKey(type, false)));
	}

	public static void setAutomaticPassOnPlayerStep(Properties properties, Step.StepType type, Boolean pass)
	{
		String key = getKey(type, true);
		if(pass == null)
			properties.setProperty(key, Boolean.toString(playerDefault.contains(type)));
		else
			properties.setProperty(key, Boolean.toString(pass));
	}

	public static void setAutomaticPassOnOpponentStep(Properties properties, Step.StepType type, Boolean pass)
	{
		String key = getKey(type, false);
		if(pass == null)
			properties.setProperty(key, Boolean.toString(opponentDefault.contains(type)));
		else
			properties.setProperty(key, Boolean.toString(pass));
	}

	@Override
	public APIOptionPanel getOptionPanel()
	{
		return new APIOptionPanel();
	}
}
