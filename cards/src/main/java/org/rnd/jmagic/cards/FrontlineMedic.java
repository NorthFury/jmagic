package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Frontline Medic")
@Types({Type.CREATURE})
@SubTypes({SubType.CLERIC, SubType.HUMAN})
@ManaCost("2W")
@Printings({@Printings.Printed(ex = Expansion.GATECRASH, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE})
public final class FrontlineMedic extends Card
{
	public static final class FrontlineMedicAbility0 extends EventTriggeredAbility
	{
		public FrontlineMedicAbility0(GameState state)
		{
			super(state, "Whenever Frontline Medic and at least two other creatures attack, creatures you control are indestructible this turn.");
			this.addPattern(battalion());
			this.addEffect(createFloatingEffect("Creatures you control are indestructible this turn.", indestructible(ControlledBy.instance(You.instance()))));
		}
	}

	public static final class FrontlineMedicAbility1 extends ActivatedAbility
	{
		public FrontlineMedicAbility1(GameState state)
		{
			super(state, "Sacrifice Frontline Medic: Counter target spell with (X) in its mana cost unless its controller pays (3).");
			this.addCost(sacrificeThis("Frontline Medic"));

			Target target = this.addTarget(Intersect.instance(Spells.instance(), ManaCostContainsX.instance()), "target spell with (X) in its mana cost");

			this.addEffect(counterTargetUnlessControllerPays("(3)", target));
		}
	}

	public FrontlineMedic(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(3);

		// Battalion \u2014 Whenever Frontline Medic and at least two other
		// creatures attack, creatures you control are indestructible this turn.
		this.addAbility(new FrontlineMedicAbility0(state));

		// Sacrifice Frontline Medic: Counter target spell with (X) in its mana
		// cost unless its controller pays (3).
		this.addAbility(new FrontlineMedicAbility1(state));
	}
}
