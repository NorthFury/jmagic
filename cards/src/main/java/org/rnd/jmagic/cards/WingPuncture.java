package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Wing Puncture")
@Types({Type.INSTANT})
@ManaCost("G")
@Printings({@Printings.Printed(ex = Expansion.SCARS_OF_MIRRODIN, r = Rarity.COMMON)})
@ColorIdentity({Color.GREEN})
public final class WingPuncture extends Card
{
	public WingPuncture(GameState state)
	{
		super(state);

		SetGenerator yourCritter = targetedBy(this.addTarget(CREATURES_YOU_CONTROL, "target creature you control"));
		SetGenerator flyer = targetedBy(this.addTarget(Intersect.instance(CreaturePermanents.instance(), HasKeywordAbility.instance(Flying.class)), "target creature with flying"));

		// Target creature you control deals damage equal to its power to target
		// creature with flying.
		EventFactory factory = permanentDealDamage(PowerOf.instance(yourCritter), flyer, "Target creature you control deals damage equal to its power to target creature with flying.");
		factory.parameters.put(EventType.Parameter.SOURCE, yourCritter);
		this.addEffect(factory);
	}
}
