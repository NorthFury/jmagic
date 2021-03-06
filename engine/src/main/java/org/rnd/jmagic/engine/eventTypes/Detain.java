package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.jmagic.engine.patterns.*;

import java.util.HashMap;
import java.util.Map;

public final class Detain extends EventType
{	public static final EventType INSTANCE = new Detain();

	 private Detain()
	{
		super("DETAIN");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.CARD;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		GameObject cause = parameters.get(EventType.Parameter.CAUSE).getOne(GameObject.class);
		SetGenerator expires = Not.instance(Intersect.instance(Identity.instance(cause), DetainGenerator.instance()));

		for(GameObject object: parameters.get(EventType.Parameter.PERMANENT).getAll(GameObject.class))
		{
			Identity thatObject = Identity.instance(object);

			// can't attack
			ContinuousEffect.Part attack = new ContinuousEffect.Part(ContinuousEffectType.ATTACKING_RESTRICTION);
			attack.parameters.put(ContinuousEffectType.Parameter.RESTRICTION, Identity.instance(Intersect.instance(thatObject, Attacking.instance())));

			// or block,
			ContinuousEffect.Part block = new ContinuousEffect.Part(ContinuousEffectType.BLOCKING_RESTRICTION);
			block.parameters.put(ContinuousEffectType.Parameter.RESTRICTION, Identity.instance(Intersect.instance(thatObject, Blocking.instance())));

			// and its activated abilities can't be activated.
			SimpleEventPattern prohibitPattern = new SimpleEventPattern(EventType.CAST_SPELL_OR_ACTIVATE_ABILITY);
			prohibitPattern.put(EventType.Parameter.OBJECT, new ActivatedAbilitiesOfPattern(thatObject));

			ContinuousEffect.Part prohibition = new ContinuousEffect.Part(ContinuousEffectType.PROHIBIT);
			prohibition.parameters.put(ContinuousEffectType.Parameter.PROHIBITION, Identity.instance(prohibitPattern));

			Map<Parameter, MagicSet> lookParameters = new HashMap<Parameter, MagicSet>();
			lookParameters.put(Parameter.CAUSE, new MagicSet(cause));
			lookParameters.put(Parameter.EFFECT, new MagicSet(attack, block, prohibition));
			lookParameters.put(Parameter.EXPIRES, new MagicSet(expires));
			createEvent(game, event.getName(), CREATE_FLOATING_CONTINUOUS_EFFECT, lookParameters).perform(event, false);
		}

		event.setResult(Identity.instance());
		return true;
	}
}