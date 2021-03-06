package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Kicker;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Strength of the Tajuru")
@Types({Type.INSTANT})
@ManaCost("XGG")
@Printings({@Printings.Printed(ex = Expansion.WORLDWAKE, r = Rarity.RARE)})
@ColorIdentity({Color.GREEN})
public final class StrengthoftheTajuru extends Card
{
	public StrengthoftheTajuru(GameState state)
	{
		super(state);

		// Multikicker (1)
		Kicker ability = new Kicker(state, true, "(1)");
		this.addAbility(ability);

		// Choose target creature, then choose another target creature for each
		// time Strength of the Tajuru was kicked.
		Target target = this.addTarget(CreaturePermanents.instance(), "any number of target creatures");
		SetGenerator timesKicked = ThisSpellWasKicked.instance(ability.costCollections[0]);
		target.setSingleNumber(Sum.instance(Union.instance(numberGenerator(1), timesKicked)));

		// Put X +1/+1 counters on each of them.
		this.addEffect(putCounters(ValueOfX.instance(This.instance()), Counter.CounterType.PLUS_ONE_PLUS_ONE, targetedBy(target), "Choose target creature, then choose another target creature for each time Strength of the Tajuru was kicked. Put X +1/+1 counters on each of them."));
	}
}
