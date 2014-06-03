package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import java.util.*;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.MagicSet;
import org.rnd.jmagic.engine.generators.*;

@Name("Doubling Chant")
@Types({Type.SORCERY})
@ManaCost("5G")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2012, r = Rarity.RARE)})
@ColorIdentity({Color.GREEN})
public final class DoublingChant extends Card
{
	/**
	 * @eparam PLAYER: you
	 * @eparam OBJECT: your creatures
	 * @eparam RESULT: creature cards found
	 */
	public static final EventType DOUBLING_CHANT_SEARCH = new EventType("DOUBLING_CHANT_SEARCH")
	{
		@Override
		public Parameter affects()
		{
			return null;
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			MagicSet cause = new MagicSet(event.getSource());
			MagicSet you = parameters.get(Parameter.PLAYER);
			Zone library = you.getOne(Player.class).getLibrary(game.actualState);
			MagicSet creatures = parameters.get(Parameter.OBJECT);
			MagicSet found = new MagicSet();
			for(GameObject creature: creatures.getAll(GameObject.class))
			{
				MagicSet findable = new MagicSet();
				String creatureName = creature.getName();
				for(GameObject o: library.objects)
					if(o.getTypes().contains(Type.CREATURE) && o.getName().equals(creatureName) && !found.contains(o))
						findable.add(o);

				java.util.Map<Parameter, MagicSet> searchParameters = new java.util.HashMap<Parameter, MagicSet>();
				searchParameters.put(EventType.Parameter.CAUSE, cause);
				searchParameters.put(EventType.Parameter.PLAYER, you);
				searchParameters.put(EventType.Parameter.NUMBER, ONE);
				searchParameters.put(EventType.Parameter.CARD, new MagicSet(library));
				searchParameters.put(EventType.Parameter.TYPE, new MagicSet(Identity.instance(findable)));
				Event search = createEvent(game, "Search your library for a creature card named " + creatureName + ".", EventType.SEARCH, searchParameters);
				search.perform(event, false);
				found.addAll(search.getResult());
			}

			event.setResult(found);
			return true;
		}
	};

	public DoublingChant(GameState state)
	{
		super(state);

		// For each creature you control, you may search your library for a
		// creature card with the same name as that creature.
		EventFactory search = new EventFactory(DOUBLING_CHANT_SEARCH, "For each creature you control, you may search your library for a creature card with the same name as that creature.");
		search.parameters.put(EventType.Parameter.PLAYER, You.instance());
		search.parameters.put(EventType.Parameter.OBJECT, CREATURES_YOU_CONTROL);
		this.addEffect(search);

		SetGenerator thoseCards = EffectResult.instance(search);

		// Put those cards onto the battlefield, then shuffle your library.
		this.addEffect(putOntoBattlefield(thoseCards, "Put those cards onto the battlefield,"));

		this.addEffect(shuffleYourLibrary("then shuffle your library."));
	}
}
