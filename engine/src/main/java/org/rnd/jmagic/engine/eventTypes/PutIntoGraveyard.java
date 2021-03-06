package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class PutIntoGraveyard extends EventType
{	public static final EventType INSTANCE = new PutIntoGraveyard();

	 private PutIntoGraveyard()
	{
		super("PUT_INTO_GRAVEYARD");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		for(GameObject object: parameters.get(Parameter.OBJECT).getAll(GameObject.class))
			if(object.isGhost())
				return false;
		return true;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		boolean allMoved = true;

		Set<GameObject> objects = parameters.get(Parameter.OBJECT).getAll(GameObject.class);
		if(objects.isEmpty())
		{
			event.setResult(Empty.set);
			return true;
		}

		Map<Zone, MagicSet> objectMap = new HashMap<Zone, MagicSet>();
		for(GameObject object: objects)
		{
			MagicSet mappedSet = null;
			Zone targetGraveyard = object.getOwner(game.actualState).getGraveyard(game.actualState);
			if(objectMap.containsKey(targetGraveyard))
				mappedSet = objectMap.get(targetGraveyard);
			else
				mappedSet = new MagicSet();

			mappedSet.add(object);
			objectMap.put(targetGraveyard, mappedSet);
		}

		MagicSet result = new MagicSet();
		for(Map.Entry<Zone, MagicSet> toEntry: objectMap.entrySet())
		{
			Zone to = toEntry.getKey();
			MagicSet theseObjects = toEntry.getValue();

			Map<Parameter, MagicSet> moveParameters = new HashMap<Parameter, MagicSet>();
			moveParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
			moveParameters.put(Parameter.TO, new MagicSet(to));
			moveParameters.put(Parameter.OBJECT, theseObjects);

			Event moveEvent = createEvent(game, "Put " + theseObjects + " into " + to + ".", EventType.MOVE_OBJECTS, moveParameters);
			if(!moveEvent.perform(event, false))
				allMoved = false;

			result.addAll(moveEvent.getResult());
		}

		event.setResult(result);
		return allMoved;
	}
}