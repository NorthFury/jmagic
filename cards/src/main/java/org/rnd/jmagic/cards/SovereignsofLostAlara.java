package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Exalted;
import org.rnd.jmagic.abilityTemplates.ExaltedBase;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

@Name("Sovereigns of Lost Alara")
@Types({Type.CREATURE})
@SubTypes({SubType.SPIRIT})
@ManaCost("4WU")
@Printings({@Printings.Printed(ex = Expansion.ALARA_REBORN, r = Rarity.RARE)})
@ColorIdentity({Color.BLUE, Color.WHITE})
public final class SovereignsofLostAlara extends Card
{
	/**
	 * @eparam CAUSE: sovereigns' ability
	 * @eparam PLAYER: controller of CAUSE
	 * @eparam ATTACKER: creature that attacked to trigger CAUSE
	 * @eparam CARD: all the cards in PLAYER's library
	 */
	public static final EventType SOVEREIGNS_OF_LOST_ALARA_EVENT = new EventType("SOVEREIGNS_OF_LOST_ALARA_EVENT")
	{
		@Override
		public Parameter affects()
		{
			return null;
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			event.setResult(Empty.set);

			// Search your library for an Aura card that could enchant that
			// creature,
			GameObject thatCreature = parameters.get(Parameter.ATTACKER).getOne(GameObject.class);
			MagicSet cardsInLibrary = parameters.get(Parameter.CARD);
			MagicSet choices = new MagicSet();
			for(GameObject o: cardsInLibrary.getAll(GameObject.class))
				if(o.getSubTypes().contains(SubType.AURA))
				{
					Map<Parameter, MagicSet> attachParameters = new HashMap<Parameter, MagicSet>();
					attachParameters.put(EventType.Parameter.OBJECT, new MagicSet(o));
					attachParameters.put(EventType.Parameter.TARGET, new MagicSet(thatCreature));
					if(ATTACH.attempt(game, event, attachParameters))
						choices.add(o);
				}

			MagicSet cause = parameters.get(Parameter.CAUSE);
			MagicSet you = parameters.get(Parameter.PLAYER);

			Map<Parameter, MagicSet> searchParameters = new HashMap<Parameter, MagicSet>();
			searchParameters.put(EventType.Parameter.CAUSE, cause);
			searchParameters.put(EventType.Parameter.PLAYER, you);
			searchParameters.put(EventType.Parameter.NUMBER, ONE);
			searchParameters.put(EventType.Parameter.CARD, cardsInLibrary);
			searchParameters.put(EventType.Parameter.TYPE, new MagicSet(Identity.instance(choices)));
			Event search = createEvent(game, "Search your library for an Aura card that could enchant that creature", SEARCH, searchParameters);
			search.perform(event, true);

			// put it onto the battlefield attached to that creature,
			Map<Parameter, MagicSet> moveParameters = new HashMap<Parameter, MagicSet>();
			moveParameters.put(EventType.Parameter.CAUSE, cause);
			moveParameters.put(EventType.Parameter.CONTROLLER, you);
			moveParameters.put(EventType.Parameter.OBJECT, search.getResult());
			moveParameters.put(EventType.Parameter.TARGET, new MagicSet(thatCreature));
			Event move = createEvent(game, "Put it onto the battlefield attached to that creature", PUT_ONTO_BATTLEFIELD_ATTACHED_TO, moveParameters);
			move.perform(event, true);

			// then shuffle your library.
			Map<Parameter, MagicSet> shuffleParameters = new HashMap<Parameter, MagicSet>();
			shuffleParameters.put(EventType.Parameter.CAUSE, cause);
			shuffleParameters.put(EventType.Parameter.PLAYER, you);
			Event shuffle = createEvent(game, "Shuffle your library", SHUFFLE_LIBRARY, shuffleParameters);
			shuffle.perform(event, true);

			return true;
		}
	};

	public static final class GetEldraziConscription extends ExaltedBase
	{
		public GetEldraziConscription(GameState state)
		{
			super(state, "Whenever a creature you control attacks alone, you may search your library for an Aura card that could enchant that creature, put it onto the battlefield attached to that creature, then shuffle your library.");

			EventFactory effect = new EventFactory(SOVEREIGNS_OF_LOST_ALARA_EVENT, "Search your library for an Aura card that could enchant that creature, put it onto the battlefield attached to that creature, then shuffle your library.");
			effect.parameters.put(EventType.Parameter.CAUSE, This.instance());
			effect.parameters.put(EventType.Parameter.PLAYER, You.instance());
			effect.parameters.put(EventType.Parameter.ATTACKER, EventParameter.instance(TriggerEvent.instance(This.instance()), EventType.Parameter.OBJECT));
			effect.parameters.put(EventType.Parameter.CARD, InZone.instance(LibraryOf.instance(You.instance())));
			this.addEffect(youMay(effect, "You may search your library for an Aura card that could enchant that creature, put it onto the battlefield attached to that creature, then shuffle your library."));
		}
	}

	public SovereignsofLostAlara(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(5);

		// Exalted
		this.addAbility(new Exalted(state));

		// Whenever a creature you control attacks alone, you may search your
		// library for an Aura card that could enchant that creature, put it
		// onto the battlefield attached to that creature, then shuffle your
		// library.
		this.addAbility(new GetEldraziConscription(state));
	}
}
