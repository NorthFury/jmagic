package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Map;

public final class DeclareAttackers extends EventType
{	public static final EventType INSTANCE = new DeclareAttackers();

	 private DeclareAttackers()
	{
		super("DECLARE_ATTACKERS");
	}

	@Override
	public Parameter affects()
	{
		return null;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Player attacker = game.actualState.currentTurn().getOwner(game.actualState);

		// Keep the instance around because it calculates information needed
		// to determine legality in each perform
		DeclareAttackersAction declareAttackersAction = new DeclareAttackersAction(game, attacker, event);
		while(!declareAttackersAction.saveStateAndPerform())
			// Fix the actual state in case declaring attackers failed
			game.refreshActualState();

		MagicSet attackers = new MagicSet();
		for(int attackerID: declareAttackersAction.attackerIDs)
			attackers.add(game.actualState.getByIDObject(attackerID));
		event.setResult(Identity.instance(attackers));

		return true;
	}
}