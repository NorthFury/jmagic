package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Collections;
import java.util.Set;

/**
 * If there is a current turn, the current turn is owned by the player with
 * priority, the current phase is a main phase, and the stack is empty,
 * evaluates to the player with priority. Otherwise, evaluates to empty.
 */
public class PlayerCanPlaySorcerySpeed extends SetGenerator
{
	public static Set<Player> get(GameState state)
	{
		if(state.currentTurn() == null)
			return Collections.emptySet();

		if(state.currentTurn().ownerID != state.playerWithPriorityID)
			return Collections.emptySet();

		if(state.currentPhase().type != Phase.PhaseType.PRECOMBAT_MAIN && state.currentPhase().type != Phase.PhaseType.POSTCOMBAT_MAIN)
			return Collections.emptySet();

		if(!state.stack().objects.isEmpty())
			return Collections.emptySet();

		return Collections.singleton(state.<Player>get(state.playerWithPriorityID));
	}

	public static SetGenerator instance()
	{
		return new PlayerCanPlaySorcerySpeed();
	}

	private PlayerCanPlaySorcerySpeed()
	{
		// Just to keep the constructor private
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		return new MagicSet(get(state));
	}
}
