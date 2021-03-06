package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.abilities.keywords.Vigilance;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Selesnya Charm")
@Types({Type.INSTANT})
@ManaCost("GW")
@Printings({@Printings.Printed(ex = Expansion.RETURN_TO_RAVNICA, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.WHITE, Color.GREEN})
public final class SelesnyaCharm extends Card
{
	public SelesnyaCharm(GameState state)
	{
		super(state);

		// Choose one \u2014 Target creature gets +2/+2 and gains trample until
		// end of turn;
		SetGenerator t1 = targetedBy(this.addTarget(1, CreaturePermanents.instance(), "target creature"));
		this.addEffect(1, ptChangeAndAbilityUntilEndOfTurn(t1, +2, +2, "Target creature gets +2/+2 and gains trample until end of turn", Trample.class));

		// or exile target creature with power 5 or greater;
		SetGenerator bigGuys = Intersect.instance(CreaturePermanents.instance(), HasPower.instance(Between.instance(5, null)));
		SetGenerator t2 = targetedBy(this.addTarget(2, bigGuys, "target creature with power 5 or greater"));
		this.addEffect(2, exile(t2, "exile target creature with power 5 or greater"));

		// or put a 2/2 white Knight creature token with vigilance onto the
		// battlefield.
		CreateTokensFactory f = new CreateTokensFactory(1, 2, 2, "put a 2/2 white Knight creature token with vigilance onto the battlefield.");
		f.setColors(Color.WHITE);
		f.setSubTypes(SubType.KNIGHT);
		f.addAbility(Vigilance.class);
		this.addEffect(3, f.getEventFactory());
	}
}
