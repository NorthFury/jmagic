package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ClashWithAnOpponent extends EventType
{	public static final EventType INSTANCE = new ClashWithAnOpponent();

	 private ClashWithAnOpponent()
	{
		super("CLASH_WITH_AN_OPPONENT");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		// 701.20b "Clash with an opponent" means
		// "Choose an opponent. You and that opponent each clash."

		Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);

		Set<Player> choices = OpponentsOf.instance(Identity.instance(player)).evaluate(game, null).getAll(Player.class);
		List<Player> choice = player.sanitizeAndChoose(game.actualState, 1, choices, PlayerInterface.ChoiceType.PLAYER, PlayerInterface.ChooseReason.CLASH);

		MagicSet players = new MagicSet(choice);
		players.add(player);

		Map<Parameter, MagicSet> clashParameters = new HashMap<Parameter, MagicSet>();
		clashParameters.put(EventType.Parameter.CAUSE, parameters.get(Parameter.CAUSE));
		clashParameters.put(EventType.Parameter.PLAYER, players);
		Event clashEvent = createEvent(game, "You and an opponent each clash.", EventType.CLASH, clashParameters);
		clashEvent.perform(event, false);

		event.setResult(clashEvent.getResult());
		return true;
	}
}