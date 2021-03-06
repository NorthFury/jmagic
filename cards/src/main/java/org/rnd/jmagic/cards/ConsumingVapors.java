package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Rebound;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

@Name("Consuming Vapors")
@Types({Type.SORCERY})
@ManaCost("3B")
@Printings({@Printings.Printed(ex = Expansion.RISE_OF_THE_ELDRAZI, r = Rarity.RARE)})
@ColorIdentity({Color.BLACK})
public final class ConsumingVapors extends Card
{
	/**
	 * @eparam CAUSE: consuming vapors
	 * @eparam TARGET: player targetted by CAUSE
	 * @eparam OBJECT: creature permanents
	 * @eparam PLAYER: controller of CAUSE
	 * @eparam RESULT: empty
	 */
	public static final EventType CONSUMING_VAPORS_EFFECT = new EventType("CONSUMING_VAPORS_EFFECT")
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

			MagicSet cause = parameters.get(Parameter.CAUSE);
			MagicSet creatures = parameters.get(Parameter.OBJECT);
			MagicSet target = parameters.get(Parameter.TARGET);

			Map<Parameter, MagicSet> sacParameters = new HashMap<Parameter, MagicSet>();
			sacParameters.put(Parameter.CAUSE, cause);
			sacParameters.put(Parameter.NUMBER, ONE);
			sacParameters.put(Parameter.CHOICE, creatures);
			sacParameters.put(Parameter.PLAYER, target);
			Event sacrifice = createEvent(game, "Target player sacrifices a creature.", EventType.SACRIFICE_CHOICE, sacParameters);
			sacrifice.perform(event, true);

			ZoneChange sacResult = sacrifice.getResult().getOne(ZoneChange.class);
			if(sacResult == null)
				return true;

			GameObject thatCreature = game.actualState.get(sacResult.oldObjectID);
			MagicSet you = parameters.get(Parameter.PLAYER);

			Map<Parameter, MagicSet> gainParameters = new HashMap<Parameter, MagicSet>();
			gainParameters.put(Parameter.CAUSE, cause);
			gainParameters.put(Parameter.PLAYER, you);
			gainParameters.put(Parameter.NUMBER, new MagicSet(thatCreature.getToughness()));
			Event gainLife = createEvent(game, "You gain life equal to that creature's toughness.", EventType.GAIN_LIFE, gainParameters);
			gainLife.perform(event, true);

			return true;
		}
	};

	public ConsumingVapors(GameState state)
	{
		super(state);

		// Target player sacrifices a creature. You gain life equal to that
		// creature's toughness.
		Target target = this.addTarget(Players.instance(), "target player");

		EventFactory effect = new EventFactory(CONSUMING_VAPORS_EFFECT, "Target player sacrifices a creature. You gain life equal to that creature's toughness.");
		effect.parameters.put(EventType.Parameter.CAUSE, This.instance());
		effect.parameters.put(EventType.Parameter.TARGET, targetedBy(target));
		effect.parameters.put(EventType.Parameter.OBJECT, CreaturePermanents.instance());
		effect.parameters.put(EventType.Parameter.PLAYER, You.instance());
		this.addEffect(effect);

		// Rebound
		this.addAbility(new Rebound(state));
	}
}
