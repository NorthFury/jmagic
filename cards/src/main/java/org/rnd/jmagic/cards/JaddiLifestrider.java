package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.util.NumberRange;

import java.util.HashMap;
import java.util.Map;

@Name("Jaddi Lifestrider")
@Types({Type.CREATURE})
@SubTypes({SubType.ELEMENTAL})
@ManaCost("4G")
@Printings({@Printings.Printed(ex = Expansion.RISE_OF_THE_ELDRAZI, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.GREEN})
public final class JaddiLifestrider extends Card
{
	/**
	 * @eparam CAUSE: lifestrider's triggered ability
	 * @eparam PLAYER: "you"
	 * @eparam CHOICE: untapped creatures PLAYER controls
	 */
	public static final EventType JADDI_LIFESTRIDER_EVENT = new EventType("JADDI_LIFESTRIDER_EVENT")
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

			Map<Parameter, MagicSet> tapParameters = new HashMap<Parameter, MagicSet>();
			tapParameters.putAll(parameters);
			tapParameters.put(Parameter.NUMBER, new MagicSet(new NumberRange(0, null)));
			Event tap = createEvent(game, "You may tap any number of untapped creatures you control.", EventType.TAP_CHOICE, tapParameters);
			tap.perform(event, true);
			MagicSet tappedThisWay = tap.getResult();

			MagicSet cause = parameters.get(Parameter.CAUSE);
			MagicSet you = parameters.get(Parameter.PLAYER);
			Map<Parameter, MagicSet> lifeParameters = new HashMap<Parameter, MagicSet>();
			lifeParameters.put(Parameter.CAUSE, cause);
			lifeParameters.put(Parameter.PLAYER, you);
			lifeParameters.put(Parameter.NUMBER, new MagicSet(2 * tappedThisWay.size()));
			Event gainLife = createEvent(game, "You gain 2 life for each creature tapped this way.", GAIN_LIFE, lifeParameters);
			gainLife.perform(event, true);

			return true;
		}
	};

	public static final class ETBTapLife extends EventTriggeredAbility
	{
		public ETBTapLife(GameState state)
		{
			super(state, "When Jaddi Lifestrider enters the battlefield, you may tap any number of untapped creatures you control. You gain 2 life for each creature tapped this way.");
			this.addPattern(whenThisEntersTheBattlefield());

			EventFactory effect = new EventFactory(JADDI_LIFESTRIDER_EVENT, "You may tap any number of untapped creatures you control. You gain 2 life for each creature tapped this way.");
			effect.parameters.put(EventType.Parameter.CAUSE, This.instance());
			effect.parameters.put(EventType.Parameter.PLAYER, You.instance());
			effect.parameters.put(EventType.Parameter.CHOICE, Intersect.instance(Untapped.instance(), CREATURES_YOU_CONTROL));
			this.addEffect(effect);
		}
	}

	public JaddiLifestrider(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(8);

		// When Jaddi Lifestrider enters the battlefield, you may tap any number
		// of untapped creatures you control. You gain 2 life for each creature
		// tapped this way.
		this.addAbility(new ETBTapLife(state));
	}
}
