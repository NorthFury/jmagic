package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Map;

public final class CreateToken extends EventType
{	public static final EventType INSTANCE = new CreateToken();

	 private CreateToken()
	{
		super("CREATE_TOKEN");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet ret = new MagicSet();
		MagicSet abilities = parameters.get(Parameter.ABILITY);
		String name = parameters.get(Parameter.NAME).getOne(String.class);
		int number = Sum.get(parameters.get(Parameter.NUMBER));
		for(int i = 0; i < number; ++i)
		{
			Token token = new Token(game.physicalState, abilities, name);
			ret.add(token);
		}
		event.setResult(ret);
		return true;
	}
}