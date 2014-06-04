package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Rebound;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Name("Surreal Memoir")
@Types({Type.SORCERY})
@ManaCost("3R")
@Printings({@Printings.Printed(ex = Expansion.RISE_OF_THE_ELDRAZI, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.RED})
public final class SurrealMemoir extends Card
{
	public static final EventType SURREAL_MEMOIR_EFFECT = new EventType("SURREAL_MEMOIR_EFFECT")
	{
		@Override
		public Parameter affects()
		{
			return null;
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			Player you = parameters.get(Parameter.PLAYER).getOne(Player.class);
			Zone graveyard = you.getGraveyard(game.actualState);

			List<GameObject> instants = new LinkedList<GameObject>();
			for(GameObject card: graveyard)
				if(card.getTypes().contains(Type.INSTANT))
					instants.add(card);
			if(!instants.isEmpty())
			{
				Collections.shuffle(instants);

				Map<Parameter, MagicSet> moveParameters = new HashMap<Parameter, MagicSet>();
				moveParameters.put(Parameter.CAUSE, new MagicSet(event.getSource()));
				moveParameters.put(Parameter.TO, new MagicSet(you.getHand(game.actualState)));
				moveParameters.put(Parameter.OBJECT, new MagicSet(instants.iterator().next()));
				createEvent(game, "Return an instant card at random from your graveyard to your hand.", EventType.MOVE_OBJECTS, moveParameters).perform(event, true);
			}

			event.setResult(Empty.set);
			return true;
		}
	};

	public SurrealMemoir(GameState state)
	{
		super(state);

		// Return an instant card at random from your graveyard to your hand.
		EventFactory effect = new EventFactory(SURREAL_MEMOIR_EFFECT, "Return an instant card at random from your graveyard to your hand.");
		effect.parameters.put(EventType.Parameter.PLAYER, You.instance());
		this.addEffect(effect);

		// Rebound
		this.addAbility(new Rebound(state));
	}
}
