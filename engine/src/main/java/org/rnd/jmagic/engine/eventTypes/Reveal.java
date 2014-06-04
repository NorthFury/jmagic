package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.jmagic.sanitized.SanitizedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class Reveal extends EventType
{	public static final EventType INSTANCE = new Reveal();

	 private Reveal()
	{
		super("REVEAL");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet objectParameter = parameters.get(Parameter.OBJECT);
		Set<GameObject> objects = objectParameter.getAll(GameObject.class);
		for(Zone z: objectParameter.getAll(Zone.class))
			objects.addAll(z.objects);
		MagicSet ret = new MagicSet();

		MagicSet cause = parameters.get(Parameter.CAUSE);
		for(GameObject object: objects)
		{
			// Change the visibleTo property of the actual object. The FCE
			// will take over afterward.
			object = game.actualState.copyForEditing(object);
			for(Player player: game.actualState.players)
				object.setActualVisibility(player, true);
			ret.add(object);
		}

		for(Player player: game.actualState.players)
		{
			SanitizedEvent sanitized = new SanitizedEvent.Reveal(event, objects, player);
			player.alert(sanitized);
		}

		SetGenerator expiration;
		if(parameters.containsKey(Parameter.EFFECT))
		{
			expiration = parameters.get(Parameter.EFFECT).getOne(SetGenerator.class);
			if(expiration == null)
				throw new UnsupportedOperationException(cause + ": REVEAL.EFFECT didn't contain a SetGenerator!");
		}
		else
			expiration = Not.instance(Exists.instance(Identity.instance(cause)));

		ContinuousEffect.Part part = new ContinuousEffect.Part(ContinuousEffectType.REVEAL);
		part.parameters.put(ContinuousEffectType.Parameter.OBJECT, Identity.instance(ret));

		Map<Parameter, MagicSet> revealParameters = new HashMap<Parameter, MagicSet>();
		revealParameters.put(Parameter.CAUSE, cause);
		revealParameters.put(Parameter.EFFECT, new MagicSet(part));
		revealParameters.put(Parameter.EXPIRES, new MagicSet(expiration));
		createEvent(game, event.getName(), CREATE_FLOATING_CONTINUOUS_EFFECT, revealParameters).perform(event, false);

		event.setResult(Identity.instance(ret));
		return true;
	}
}