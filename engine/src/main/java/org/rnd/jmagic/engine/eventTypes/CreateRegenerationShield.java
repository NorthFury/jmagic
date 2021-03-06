package org.rnd.jmagic.engine.eventTypes;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.jmagic.engine.patterns.*;

import java.util.HashMap;
import java.util.Map;

public final class CreateRegenerationShield extends EventType
{	public static final EventType INSTANCE = new CreateRegenerationShield();

	 private CreateRegenerationShield()
	{
		super("CREATE_REGENERATION_SHIELD");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet result = new MagicSet();
		for(GameObject object: parameters.get(Parameter.OBJECT).getAll(GameObject.class))
		{
			SetGenerator thisObject = IdentifiedWithID.instance(object.ID);

			SimpleEventPattern destroyThis = new SimpleEventPattern(EventType.DESTROY_ONE_PERMANENT);
			destroyThis.put(Parameter.PERMANENT, thisObject);

			EventFactory factory = new EventFactory(EventType.REGENERATE, ("Regenerate " + object.getName()));
			factory.parameters.put(Parameter.CAUSE, Identity.instance(parameters.get(Parameter.CAUSE)));
			factory.parameters.put(Parameter.OBJECT, thisObject);

			EventReplacementEffect regenerate = new EventReplacementEffect(game, "Regenerate " + object.getName(), destroyThis);
			regenerate.addEffect(factory);

			ContinuousEffect.Part part = replacementEffectPart(regenerate);

			Map<Parameter, MagicSet> FCEparameters = new HashMap<Parameter, MagicSet>();
			FCEparameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
			FCEparameters.put(Parameter.EFFECT, new MagicSet(part));
			FCEparameters.put(Parameter.USES, ONE);
			Event createShield = createEvent(game, "Create regeneration shields", EventType.CREATE_FLOATING_CONTINUOUS_EFFECT, FCEparameters);
			createShield.perform(event, false);
			result.addAll(createShield.getResult());
		}

		event.setResult(result);
		return true;
	}
}