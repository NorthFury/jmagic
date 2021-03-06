package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.TapForAnyColor;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.jmagic.engine.patterns.*;

import java.util.Arrays;

@Name("Forbidden Orchard")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.CHAMPIONS_OF_KAMIGAWA, r = Rarity.RARE)})
@ColorIdentity({})
public final class ForbiddenOrchard extends Card
{
	public static final class EvilSpirits extends EventTriggeredAbility
	{
		public EvilSpirits(GameState state)
		{
			super(state, "Whenever you tap Forbidden Orchard for mana, put a 1/1 colorless Spirit creature token onto the battlefield under target opponent's control.");
			this.addPattern(tappedForMana(You.instance(), new SimpleSetPattern(ABILITY_SOURCE_OF_THIS)));

			Target t = this.addTarget(OpponentsOf.instance(You.instance()), "target opponent");

			EventFactory effect = new EventFactory(EventType.CREATE_TOKEN_ON_BATTLEFIELD, "Put a 1/1 colorless Spirit creature token onto the battlefield under target opponent's control.");
			effect.parameters.put(EventType.Parameter.CAUSE, This.instance());
			effect.parameters.put(EventType.Parameter.CONTROLLER, targetedBy(t));
			effect.parameters.put(EventType.Parameter.NUMBER, numberGenerator(1));
			effect.parameters.put(EventType.Parameter.POWER, numberGenerator(1));
			effect.parameters.put(EventType.Parameter.SUBTYPE, Identity.instance(Arrays.asList(SubType.SPIRIT)));
			effect.parameters.put(EventType.Parameter.TOUGHNESS, numberGenerator(1));
			effect.parameters.put(EventType.Parameter.TYPE, Identity.instance(Type.CREATURE));
			effect.parameters.put(EventType.Parameter.NUMBER, numberGenerator(1));
			this.addEffect(effect);
		}
	}

	public ForbiddenOrchard(GameState state)
	{
		super(state);

		// (T): Add one mana of any color to your mana pool.
		this.addAbility(new TapForAnyColor(state));

		// Whenever you tap Forbidden Orchard for mana, put a 1/1 colorless
		// Spirit creature token onto the battlefield under target opponent's
		// control.
		this.addAbility(new EvilSpirits(state));
	}
}
