package org.rnd.jmagic.abilities.keywords;

import static org.rnd.jmagic.Convenience.*;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class Cascade extends Keyword
{
	public Cascade(GameState state)
	{
		super(state, "Cascade");
	}

	@Override
	protected List<NonStaticAbility> createNonStaticAbilities()
	{
		return Collections.<NonStaticAbility>singletonList(new CascadeAbility(this.state));
	}

	/**
	 * @eparam CAUSE: the cascade trigger
	 * @eparam NUMBER: the converted mana cost of the spell that was cast to
	 * trigger cascade
	 * @eparam PLAYER: the controller of CAUSE
	 * @eparam RESULT: empty
	 */
	public static EventType CASCADE_EVENT = new EventType("CASCADE_EVENT")
	{

		@Override
		public Parameter affects()
		{
			return null;
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			MagicSet cascade = parameters.get(Parameter.CAUSE);
			int cmc = Sum.get(parameters.get(Parameter.NUMBER));
			MagicSet you = parameters.get(Parameter.PLAYER);
			event.setResult(Empty.set);

			GameObject hitCard = null;
			List<GameObject> cardsExiledThisWay = new LinkedList<GameObject>();
			Player player = you.getOne(Player.class);
			Zone library = player.getLibrary(game.actualState);
			while(!library.objects.isEmpty())
			{
				Map<Parameter, MagicSet> exileParameters = new HashMap<Parameter, MagicSet>();
				exileParameters.put(Parameter.CAUSE, cascade);
				exileParameters.put(Parameter.TO, new MagicSet(game.actualState.exileZone()));
				exileParameters.put(Parameter.OBJECT, new MagicSet(library.objects.get(0)));
				Event exile = createEvent(game, "Exile the top card of your library", EventType.MOVE_OBJECTS, exileParameters);

				exile.perform(event, true);
				player = player.getActual();
				library = library.getActual();

				GameObject exiled = game.actualState.get(exile.getResult().getOne(ZoneChange.class).newObjectID);
				cardsExiledThisWay.add(exiled);
				if(!exiled.getTypes().contains(Type.LAND) && exiled.getConvertedManaCost() < cmc)
				{
					hitCard = exiled;
					break;
				}
			}

			if(hitCard != null)
			{
				Map<Parameter, MagicSet> mayParameters = new HashMap<Parameter, MagicSet>();
				mayParameters.put(Parameter.CAUSE, cascade);
				mayParameters.put(Parameter.PLAYER, you);
				mayParameters.put(Parameter.OBJECT, new MagicSet(hitCard));
				Event mayCast = createEvent(game, "You may cast " + hitCard + " without paying its mana cost.", PLAY_WITHOUT_PAYING_MANA_COSTS, mayParameters);

				mayCast.perform(event, true);
			}

			Map<Parameter, MagicSet> bottomParameters = new HashMap<Parameter, MagicSet>();
			bottomParameters.put(Parameter.CAUSE, cascade);
			bottomParameters.put(Parameter.INDEX, new MagicSet(-1));
			bottomParameters.put(Parameter.OBJECT, new MagicSet(cardsExiledThisWay));
			bottomParameters.put(Parameter.RANDOM, Empty.set);
			Event bottom = createEvent(game, "Put the rest of those cards on the bottom of your libarary in a random order", EventType.PUT_INTO_LIBRARY, bottomParameters);
			bottom.perform(event, true);

			return true;
		}
	};

	public final static class CascadeAbility extends EventTriggeredAbility
	{
		public CascadeAbility(GameState state)
		{
			super(state, "When you cast this spell, exile cards from the top of your library until you exile a nonland card that costs less. You may cast it without paying its mana cost. Put the exiled cards on the bottom in a random order.");
			this.triggersFromStack();

			this.addPattern(whenYouCastThisSpell());

			EventFactory cascade = new EventFactory(CASCADE_EVENT, "Exile cards from the top of your library until you exile a nonland card that costs less. You may cast it without paying its mana cost. Put the exiled cards on the bottom in a random order.");
			cascade.parameters.put(EventType.Parameter.CAUSE, This.instance());
			cascade.parameters.put(EventType.Parameter.NUMBER, ConvertedManaCostOf.instance(ABILITY_SOURCE_OF_THIS));
			cascade.parameters.put(EventType.Parameter.PLAYER, You.instance());
			this.addEffect(cascade);
		}
	}
}
