package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Tectonic Rift")
@Types({Type.SORCERY})
@ManaCost("3R")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2012, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.RED})
public final class TectonicRift extends Card
{
	public TectonicRift(GameState state)
	{
		super(state);

		// Destroy target land.
		Target target = this.addTarget(LandPermanents.instance(), "target land");
		this.addEffect(destroy(targetedBy(target), "Destroy target land."));

		// Creatures without flying can't block this turn.
		SetGenerator withoutFlying = RelativeComplement.instance(CreaturePermanents.instance(), HasKeywordAbility.instance(Flying.class));

		ContinuousEffect.Part part = new ContinuousEffect.Part(ContinuousEffectType.BLOCKING_RESTRICTION);
		part.parameters.put(ContinuousEffectType.Parameter.RESTRICTION, Identity.instance(Intersect.instance(Blocking.instance(), withoutFlying)));
		this.addEffect(createFloatingEffect("Creatures without flying can't block this turn.", part));
	}
}
