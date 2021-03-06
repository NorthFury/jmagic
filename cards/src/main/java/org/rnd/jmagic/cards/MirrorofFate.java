package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Name("Mirror of Fate")
@Types({Type.ARTIFACT})
@ManaCost("5")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2010, r = Rarity.RARE)})
@ColorIdentity({})
public final class MirrorofFate extends Card
{
	public static final PlayerInterface.ChooseReason REASON = new PlayerInterface.ChooseReason("MirrorofFate", "Choose up to seven face-up exiled cards you own.", true);

	public static final class Destiny extends ActivatedAbility
	{
		/**
		 * @eparam CAUSE: mirror of fate's ability
		 * @eparam PLAYER: the player choosing
		 * @eparam RESULT: empty
		 */
		public static final EventType MIRROR_OF_FATE_EVENT = new EventType("MIRROR_OF_FATE_EVENT")
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
				Zone library = player.getLibrary(game.actualState);
				MagicSet choices = RelativeComplement.instance(Intersect.instance(OwnedBy.instance(Identity.instance(player)), InZone.instance(ExileZone.instance()), Cards.instance()), Revealed.instance()).evaluate(game, null);

				List<?> chosenCards = player.sanitizeAndChoose(game.actualState, 0, 7, choices, PlayerInterface.ChoiceType.OBJECTS, REASON);

				Map<Parameter, MagicSet> exileParameters = new HashMap<Parameter, MagicSet>();
				exileParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
				exileParameters.put(Parameter.TO, new MagicSet(game.actualState.exileZone()));
				exileParameters.put(Parameter.OBJECT, new MagicSet(library.objects));
				Event exileEvent = createEvent(game, "Exile all the cards from your library.", EventType.MOVE_OBJECTS, exileParameters);
				boolean ret = exileEvent.perform(event, true);

				Map<Parameter, MagicSet> libraryParameters = new HashMap<Parameter, MagicSet>();
				libraryParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
				libraryParameters.put(Parameter.OBJECT, new MagicSet(chosenCards));
				Event libraryEvent = createEvent(game, "Put the chosen cards on top of your library.", EventType.PUT_INTO_LIBRARY, libraryParameters);
				ret = libraryEvent.perform(event, true) && ret;

				event.setResult(Empty.set);

				return ret;
			}
		};

		public Destiny(GameState state)
		{
			super(state, "(T), Sacrifice Mirror of Fate: Choose up to seven face-up exiled cards you own. Exile all the cards from your library, then put the chosen cards on top of your library.");

			this.costsTap = true;

			this.addCost(sacrificeThis("Mirror of Fate"));

			EventType.ParameterMap parameters = new EventType.ParameterMap();
			parameters.put(EventType.Parameter.CAUSE, This.instance());
			parameters.put(EventType.Parameter.PLAYER, You.instance());
			this.addEffect(new EventFactory(MIRROR_OF_FATE_EVENT, parameters, "Choose up to seven face-up exiled cards you own. Exile all the cards from your library, then put the chosen cards on top of your library."));
		}
	}

	public MirrorofFate(GameState state)
	{
		super(state);

		this.addAbility(new Destiny(state));
	}
}
