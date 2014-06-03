package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

public final class Look extends EventType
{	public static final EventType INSTANCE = new Look();

	 private Look()
	{
		super("LOOK");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean perform(Game game, Event event, java.util.Map<Parameter, MagicSet> parameters)
	{
		MagicSet objects = parameters.get(Parameter.OBJECT);
		java.util.Set<GameObject> gameObjects = objects.getAll(GameObject.class);
		for(Zone z: objects.getAll(Zone.class))
			gameObjects.addAll(z.objects);
		java.util.Set<Player> players = parameters.get(Parameter.PLAYER).getAll(Player.class);
		MagicSet ret = new MagicSet();

		for(Player player: players)
		{
			org.rnd.jmagic.sanitized.SanitizedEvent sanitized = new org.rnd.jmagic.sanitized.SanitizedEvent.Look(event, player, gameObjects);
			player.alert(sanitized);
		}

		GameObject cause = parameters.get(Parameter.CAUSE).getOne(GameObject.class);
		for(GameObject object: gameObjects)
		{
			// Change the visibleTo property of the actual object. If there
			// is a duration, the FCE will take over. Otherwise it will
			// revert next time the game state refreshes.
			object = game.actualState.copyForEditing(object);
			for(Player player: players)
				object.setActualVisibility(player, true);
			ret.add(object);
		}

		SetGenerator expiration;
		if(parameters.containsKey(Parameter.EFFECT))
		{
			expiration = parameters.get(Parameter.EFFECT).getOne(SetGenerator.class);
			if(expiration == null)
				throw new UnsupportedOperationException(cause + ": LOOK.EFFECT didn't contain a SetGenerator");
		}
		else
			expiration = Not.instance(Exists.instance(Identity.instance(cause)));

		ContinuousEffect.Part part = new ContinuousEffect.Part(ContinuousEffectType.LOOK);
		part.parameters.put(ContinuousEffectType.Parameter.OBJECT, Identity.instance(objects));
		part.parameters.put(ContinuousEffectType.Parameter.PLAYER, Identity.instance(players));

		java.util.Map<Parameter, MagicSet> lookParameters = new java.util.HashMap<Parameter, MagicSet>();
		lookParameters.put(Parameter.CAUSE, new MagicSet(cause));
		lookParameters.put(Parameter.EFFECT, new MagicSet(part));
		lookParameters.put(Parameter.EXPIRES, new MagicSet(expiration));
		createEvent(game, event.getName(), CREATE_FLOATING_CONTINUOUS_EFFECT, lookParameters).perform(event, false);

		event.setResult(Identity.instance(ret));
		return true;
	}
}