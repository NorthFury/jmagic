package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.jmagic.sanitized.SanitizedEvent;

import java.util.Map;

public final class GameOver extends EventType
{	public static final EventType INSTANCE = new GameOver();

	 private GameOver()
	{
		super("GAME_OVER");
	}

	@Override
	public Parameter affects()
	{
		return null;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		SanitizedEvent sanitized = new SanitizedEvent(event);
		for(Player player: game.actualState.players)
			player.alert(sanitized);

		event.setResult(Empty.set);
		return true;
	}
}