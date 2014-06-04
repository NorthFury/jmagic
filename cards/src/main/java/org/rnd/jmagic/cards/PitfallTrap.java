package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.Trap;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Pitfall Trap")
@Types({Type.INSTANT})
@SubTypes({SubType.TRAP})
@ManaCost("2W")
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.WHITE})
public final class PitfallTrap extends Card
{
	public PitfallTrap(GameState state)
	{
		super(state);

		// If exactly one creature is attacking, you may pay (W) rather than pay
		// Pitfall Trap's mana cost.
		SetGenerator trapCondition = Intersect.instance(numberGenerator(1), Count.instance(Attacking.instance()));
		this.addAbility(new Trap(state, this.getName(), trapCondition, "If exactly one creature is attacking", "(W)"));

		// Destroy target attacking creature without flying.
		Target target = this.addTarget(RelativeComplement.instance(Attacking.instance(), HasKeywordAbility.instance(Flying.class)), "target attacking creature without flying");
		this.addEffect(destroy(targetedBy(target), "Destroy target attacking creature without flying."));
	}
}
