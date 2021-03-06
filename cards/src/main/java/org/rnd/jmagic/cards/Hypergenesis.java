package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Suspend;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Name("Hypergenesis")
@Types({Type.SORCERY})
@Printings({@Printings.Printed(ex = Expansion.TIME_SPIRAL, r = Rarity.RARE)})
@ColorIdentity({Color.GREEN})
public final class Hypergenesis extends Card
{
	/**
	 * @eparam CAUSE: hypergenesis
	 * @eparam PLAYER: you
	 */
	public static final EventType HYPERGENESIS_EVENT = new EventType("HYPERGENESIS_EVENT")
	{
		@Override
		public Parameter affects()
		{
			return null;
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			MagicSet hypergenesis = parameters.get(Parameter.CAUSE);

			Player you = parameters.get(Parameter.PLAYER).getOne(Player.class);
			List<Player> players = game.actualState.getPlayerCycle(you);
			while(true)
			{
				boolean noOnePutACardOntoTheBattlefield = true;
				for(Player player: players)
				{
					MagicSet available = new MagicSet();
					for(GameObject card: player.getHand(game.actualState).objects)
						if(card.getTypes().contains(Type.ARTIFACT) || card.getTypes().contains(Type.CREATURE) || card.getTypes().contains(Type.ENCHANTMENT) || card.getTypes().contains(Type.LAND))
							available.add(card);

					EventFactory putOntoBattlefield = new EventFactory(PUT_ONTO_BATTLEFIELD_CHOICE, "Put an artifact, creature, enchantment, or land card from your hand onto the battlefield");
					putOntoBattlefield.parameters.put(Parameter.CAUSE, Identity.instance(hypergenesis));
					putOntoBattlefield.parameters.put(Parameter.CONTROLLER, Identity.instance(player));
					putOntoBattlefield.parameters.put(Parameter.OBJECT, Identity.instance(available));

					Map<Parameter, MagicSet> mayParameters = new HashMap<Parameter, MagicSet>();
					mayParameters.put(Parameter.PLAYER, new MagicSet(player));
					mayParameters.put(Parameter.EVENT, new MagicSet(putOntoBattlefield));
					Event mayPut = createEvent(game, player + " may put an artifact, creature, enchantment, or land card from his or her hand onto the battlefield.", PLAYER_MAY, mayParameters);
					mayPut.perform(event, true);

					if(mayPut.getResult().contains(Answer.YES))
						noOnePutACardOntoTheBattlefield = false;
				}

				if(noOnePutACardOntoTheBattlefield)
					break;
			}

			event.setResult(Empty.set);
			return true;
		}
	};

	public Hypergenesis(GameState state)
	{
		super(state);

		this.setColorIndicator(Color.GREEN);

		// Suspend 3\u2014(1)(G)(G)
		this.addAbility(new Suspend(state, 3, "(1)(G)(G)"));

		// Starting with you, each player may put an artifact, creature,
		// enchantment, or land card from his or her hand onto the battlefield.
		// Repeat this process until no one puts a card onto the battlefield.
		EventFactory effect = new EventFactory(HYPERGENESIS_EVENT, "Starting with you, each player may put an artifact, creature, enchantment, or land card from his or her hand onto the battlefield. Repeat this process until no one puts a card onto the battlefield.");
		effect.parameters.put(EventType.Parameter.CAUSE, This.instance());
		effect.parameters.put(EventType.Parameter.PLAYER, You.instance());
		this.addEffect(effect);
	}
}
