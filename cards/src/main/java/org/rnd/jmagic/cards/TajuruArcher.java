package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Tajuru Archer")
@Types({Type.CREATURE})
@SubTypes({SubType.ELF, SubType.ALLY, SubType.ARCHER})
@ManaCost("2G")
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.GREEN})
public final class TajuruArcher extends Card
{
	public static final class AllyArchery extends EventTriggeredAbility
	{
		public AllyArchery(GameState state)
		{
			super(state, "Whenever Tajuru Archer or another Ally enters the battlefield under your control, you may have Tajuru Archer deal damage to target creature with flying equal to the number of Allies you control.");
			this.addPattern(allyTrigger());

			Target target = this.addTarget(Intersect.instance(CreaturePermanents.instance(), HasKeywordAbility.instance(Flying.class)), "target creature with flying");

			EventFactory damage = permanentDealDamage(Count.instance(ALLIES_YOU_CONTROL), targetedBy(target), "Tajuru Archer deals damage to target creature with flying equal to the number of Allies you control.");
			this.addEffect(youMay(damage, "You may have Tajuru Archer deal damage to target creature with flying equal to the number of Allies you control."));
		}
	}

	public TajuruArcher(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(2);

		// Whenever Tajuru Archer or another Ally enters the battlefield under
		// your control, you may have Tajuru Archer deal damage to target
		// creature with flying equal to the number of Allies you control.
		this.addAbility(new AllyArchery(state));
	}
}
