package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Map;

public final class DealCombatDamage extends EventType
{	public static final EventType INSTANCE = new DealCombatDamage();

	 private DealCombatDamage()
	{
		super("DEAL_COMBAT_DAMAGE");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.TARGET;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		event.addDamage(parameters.get(Parameter.TARGET).getAll(DamageAssignment.class));

		event.setResult(Empty.set);
		return true;
	}
}