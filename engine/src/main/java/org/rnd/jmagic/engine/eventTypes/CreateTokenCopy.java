package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

public final class CreateTokenCopy extends EventType
{	public static final EventType INSTANCE = new CreateTokenCopy();

	 private CreateTokenCopy()
	{
		super("CREATE_TOKEN_COPY");
	}

	@Override
	public Parameter affects()
	{
		return null;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		GameObject original = parameters.get(Parameter.OBJECT).getOne(GameObject.class);
		if(original == null)
		{
			event.setResult(Empty.set);
			return false;
		}

		MagicSet tokenCopies = new MagicSet();

		// 110.5a A token is both owned and controlled by the player
		// under whose control it entered the battlefield.
		Player owner = parameters.get(Parameter.CONTROLLER).getOne(Player.class);

		int number = 1;
		if(parameters.containsKey(Parameter.NUMBER))
			number = Sum.get(parameters.get(Parameter.NUMBER));

		Map<Parameter, MagicSet> tokenParameters = new HashMap<Parameter, MagicSet>();
		tokenParameters.put(EventType.Parameter.ABILITY, new MagicSet());
		tokenParameters.put(EventType.Parameter.NAME, new MagicSet(""));
		tokenParameters.put(EventType.Parameter.NUMBER, new MagicSet(number));
		tokenParameters.put(EventType.Parameter.CONTROLLER, new MagicSet(owner));
		Event createTokens = createEvent(game, "", CREATE_TOKEN, tokenParameters);

		if(createTokens.perform(event, false))
			for(Token copy: createTokens.getResultGenerator().evaluate(game.physicalState, null).getAll(Token.class))
			{
				copy.ownerID = owner.ID;
				game.physicalState.exileZone().addToTop(copy);
				tokenCopies.add(copy);

				// to keep Identity happy
				game.actualState.put(copy);
			}

		MagicSet result = new MagicSet();
		boolean status = true;

		for(GameObject tokenCopy: tokenCopies.getAll(GameObject.class))
		{
			Map<Parameter, MagicSet> putAsCopyParameters = new HashMap<Parameter, MagicSet>();
			putAsCopyParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
			putAsCopyParameters.put(Parameter.CONTROLLER, parameters.get(Parameter.CONTROLLER));
			putAsCopyParameters.put(Parameter.OBJECT, new MagicSet(tokenCopy));
			putAsCopyParameters.put(Parameter.SOURCE, parameters.get(Parameter.OBJECT));
			if(parameters.containsKey(Parameter.TYPE))
				putAsCopyParameters.put(Parameter.TYPE, parameters.get(Parameter.TYPE));
			putAsCopyParameters.put(Parameter.TO, new MagicSet(game.actualState.battlefield()));
			Event putAsCopy = createEvent(game, "Put " + (number == 1 ? "a token" : number + " tokens") + " onto the battlefield copying " + original + ".", EventType.PUT_INTO_ZONE_AS_A_COPY_OF, putAsCopyParameters);
			if(!putAsCopy.perform(event, false))
				status = false;

			result.addAll(putAsCopy.getResult());
		}

		event.setResult(result);
		return status;
	}
}