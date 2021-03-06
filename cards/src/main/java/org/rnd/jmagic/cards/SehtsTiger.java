package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flash;
import org.rnd.jmagic.abilities.keywords.Protection;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

@Name("Seht's Tiger")
@Types({Type.CREATURE})
@SubTypes({SubType.CAT})
@ManaCost("2WW")
@Printings({@Printings.Printed(ex = Expansion.FUTURE_SIGHT, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE})
public final class SehtsTiger extends Card
{
	public static final class ProtectPlayer extends EventTriggeredAbility
	{
		/**
		 * @eparam CAUSE: the ability creating the protection effect
		 * @eparam PLAYER: the player choosing the color and being protected
		 * @eparam RESULT: empty
		 */
		public static final EventType SEHTS_TIGER_EVENT = new EventType("SEHTS_TIGER_EVENT")
		{
			@Override
			public Parameter affects()
			{
				return null;
			}

			@Override
			public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
			{
				Player player = parameters.get(EventType.Parameter.PLAYER).getOne(Player.class);
				Color choice = player.chooseColor(parameters.get(Parameter.CAUSE).getOne(NonStaticAbility.class).getSourceID());

				ContinuousEffect.Part part = new ContinuousEffect.Part(ContinuousEffectType.ADD_ABILITY_TO_PLAYER);
				part.parameters.put(ContinuousEffectType.Parameter.ABILITY, Identity.instance(new Protection.AbilityFactory(choice)));
				part.parameters.put(ContinuousEffectType.Parameter.PLAYER, Identity.instance(player));

				Map<EventType.Parameter, MagicSet> fceParameters = new HashMap<EventType.Parameter, MagicSet>();
				fceParameters.put(EventType.Parameter.CAUSE, parameters.get(EventType.Parameter.CAUSE));
				fceParameters.put(EventType.Parameter.EFFECT, new MagicSet(part));
				createEvent(game, "You gain protection from the color of your choice until end of turn.", EventType.CREATE_FLOATING_CONTINUOUS_EFFECT, fceParameters).perform(event, false);

				event.setResult(Empty.set);
				return true;
			}
		};

		public ProtectPlayer(GameState state)
		{
			super(state, "When Seht's Tiger enters the battlefield, you gain protection from the color of your choice until end of turn.");

			this.addPattern(whenThisEntersTheBattlefield());

			EventType.ParameterMap parameters = new EventType.ParameterMap();
			parameters.put(EventType.Parameter.CAUSE, This.instance());
			parameters.put(EventType.Parameter.PLAYER, You.instance());
			this.addEffect(new EventFactory(SEHTS_TIGER_EVENT, parameters, "You gain protection from the color of your choice until end of turn."));
		}
	}

	public SehtsTiger(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(3);

		this.addAbility(new Flash(state));

		this.addAbility(new ProtectPlayer(state));
	}
}
