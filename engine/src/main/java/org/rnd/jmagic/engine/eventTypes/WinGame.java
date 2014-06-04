package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.sanitized.SanitizedEvent;

import java.util.Map;

public final class WinGame extends EventType
{	public static final EventType INSTANCE = new WinGame();

	 private WinGame()
	{
		super("WIN_GAME");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		SanitizedEvent sanitized = new SanitizedEvent(event);
		for(Player player: game.actualState.players)
			player.alert(sanitized);

		parameters.get(Parameter.PLAYER).getOne(Player.class).wonGame = true;
		throw new Game.GameOverException();
	}
}