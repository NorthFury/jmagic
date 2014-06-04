package org.rnd.jmagic.cards;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.util.NumberRange;

import java.util.HashMap;
import java.util.Map;

@Name("Scapeshift")
@Types({Type.SORCERY})
@ManaCost("2GG")
@Printings({@Printings.Printed(ex = Expansion.MORNINGTIDE, r = Rarity.RARE)})
@ColorIdentity({Color.GREEN})
public final class Scapeshift extends Card
{
	/**
	 * @eparam CAUSE: scapeshift
	 * @eparam PLAYER: you
	 * @eparam RESULT: empty
	 */
	public static final EventType SCAPESHIFT_EVENT = new EventType("SCAPESHIFT_EVENT")
	{
		@Override
		public Parameter affects()
		{
			return null;
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);

			Map<Parameter, MagicSet> sacParameters = new HashMap<Parameter, MagicSet>();
			sacParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
			sacParameters.put(Parameter.NUMBER, new MagicSet(new NumberRange(0, null)));
			sacParameters.put(Parameter.CHOICE, HasType.instance(Type.LAND).evaluate(game, null));
			sacParameters.put(Parameter.PLAYER, new MagicSet(player));
			Event sacEvent = createEvent(game, "Sacrifice any number of lands.", EventType.SACRIFICE_CHOICE, sacParameters);
			sacEvent.perform(event, true);

			Map<Parameter, MagicSet> searchParameters = new HashMap<Parameter, MagicSet>();
			searchParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
			searchParameters.put(Parameter.CONTROLLER, new MagicSet(player));
			searchParameters.put(Parameter.PLAYER, new MagicSet(player));
			searchParameters.put(Parameter.NUMBER, new MagicSet(new NumberRange(0, sacEvent.getResult().size())));
			searchParameters.put(Parameter.TO, new MagicSet(game.actualState.battlefield()));
			searchParameters.put(Parameter.TAPPED, Empty.set);
			searchParameters.put(Parameter.TYPE, new MagicSet(HasType.instance(Type.LAND)));
			Event searchEvent = createEvent(game, "Search your library for that many land cards, put them onto the battlefield tapped, then shuffle your library.", EventType.SEARCH_LIBRARY_AND_PUT_INTO, searchParameters);
			searchEvent.perform(event, true);

			event.setResult(Empty.set);

			return true;
		}
	};

	public Scapeshift(GameState state)
	{
		super(state);

		EventFactory factory = new EventFactory(SCAPESHIFT_EVENT, "Sacrifice any number of lands. Search your library for that many land cards, put them onto the battlefield tapped, then shuffle your library.");
		factory.parameters.put(EventType.Parameter.CAUSE, This.instance());
		factory.parameters.put(EventType.Parameter.PLAYER, You.instance());
		this.addEffect(factory);
	}
}
