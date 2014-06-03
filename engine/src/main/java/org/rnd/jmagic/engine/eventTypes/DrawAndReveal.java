package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

public final class DrawAndReveal extends EventType
{	public static final EventType INSTANCE = new DrawAndReveal();

	 private DrawAndReveal()
	{
		super("DRAW_AND_REVEAL");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean perform(Game game, Event event, java.util.Map<Parameter, MagicSet> parameters)
	{
		EventFactory drawFactory = parameters.get(Parameter.EVENT).getOne(EventFactory.class);
		Event draw = drawFactory.createEvent(game, event.getSource());
		boolean ret = draw.perform(event, true);
		SetGenerator result = NewObjectOf.instance(draw.getResultGenerator());
		event.setResult(result.evaluate(game, null));
		if(!ret)
			return false;

		java.util.Map<Parameter, MagicSet> revealParameters = new java.util.HashMap<Parameter, MagicSet>();
		revealParameters.put(Parameter.CAUSE, draw.parameters.get(Parameter.CAUSE).evaluate(game, draw.getSource()));
		revealParameters.put(Parameter.OBJECT, result.evaluate(game, null));
		Event reveal = createEvent(game, "Reveal the drawn cards", REVEAL, revealParameters);
		reveal.perform(event, true);

		return true;
	}
}