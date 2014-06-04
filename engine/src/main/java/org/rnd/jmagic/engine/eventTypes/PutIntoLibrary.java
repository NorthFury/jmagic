package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class PutIntoLibrary extends EventType
{	public static final EventType INSTANCE = new PutIntoLibrary();

	 private PutIntoLibrary()
	{
		super("PUT_INTO_LIBRARY");
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
			Zone targetLibrary = object.getOwner(game.actualState).getLibrary(game.actualState);
			if(!objectMap.containsKey(targetLibrary))
				objectMap.put(targetLibrary, new MagicSet());
			objectMap.get(targetLibrary).add(object);
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
			if(parameters.containsKey(Parameter.INDEX))
				moveParameters.put(Parameter.INDEX, parameters.get(Parameter.INDEX));
			if(parameters.containsKey(Parameter.RANDOM))
				moveParameters.put(Parameter.RANDOM, Empty.set);

			Event moveEvent = createEvent(game, "Put " + theseObjects + " into " + to + ".", EventType.MOVE_OBJECTS, moveParameters);
			if(!moveEvent.perform(event, false))
				allMoved = false;

			result.addAll(moveEvent.getResult());
		}

		event.setResult(result);
		return allMoved;
	}
}