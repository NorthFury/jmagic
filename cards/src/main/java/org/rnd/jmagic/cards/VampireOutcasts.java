package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Bloodthirst;
import org.rnd.jmagic.abilities.keywords.Lifelink;
import org.rnd.jmagic.engine.*;

@Name("Vampire Outcasts")
@Types({Type.CREATURE})
@SubTypes({SubType.VAMPIRE})
@ManaCost("2BB")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2012, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLACK})
public final class VampireOutcasts extends Card
{
	public VampireOutcasts(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// Bloodthirst 2 (If an opponent was dealt damage this turn, this
		// creature enters the battlefield with two +1/+1 counters on it.)
		this.addAbility(new Bloodthirst.Final(state, 2));

		// Lifelink (Damage dealt by this creature also causes you to gain that
		// much life.)
		this.addAbility(new Lifelink(state));
	}
}
