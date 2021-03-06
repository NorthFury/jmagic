package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Collection;
import java.util.EnumSet;

/**
 * Evaluates to each given players phase of the given PhaseType
 */
public class PhaseOf extends SetGenerator
{
	public static PhaseOf instance(SetGenerator players, Phase.PhaseType phaseType)
	{
		return new PhaseOf(players, phaseType);
	}

	protected PhaseOf(SetGenerator players, Phase.PhaseType phaseType)
	{
		this.players = players;
		this.phaseTypes = EnumSet.of(phaseType);
	}

	PhaseOf(SetGenerator players, Phase.PhaseType precombatMain, Phase.PhaseType postcombatMain)
	{
		this.players = players;
		this.phaseTypes = EnumSet.of(precombatMain, postcombatMain);
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet returnValue = new MagicSet();
		for(Player player: this.players.evaluate(state, thisObject).getAll(Player.class))
		{
			if((state.currentPhase() != null) && (state.currentPhase().ownerID == player.ID) && (this.phaseTypes.contains(state.currentPhase().type)))
				returnValue.add(state.currentPhase());

			if(state.currentTurn().ownerID == player.ID)
				for(Phase phase: state.currentTurn())
					if(this.phaseTypes.contains(phase.type))
						returnValue.add(phase);

			for(Turn turn: state.futureTurns)
				if(turn.ownerID == player.ID)
					for(Phase phase: turn)
						if(this.phaseTypes.contains(phase.type))
							returnValue.add(phase);
		}
		return returnValue;
	}

	private final SetGenerator players;
	private final Collection<Phase.PhaseType> phaseTypes;
}
