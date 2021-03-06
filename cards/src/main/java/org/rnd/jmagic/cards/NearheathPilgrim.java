package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.AbilityIfPaired;
import org.rnd.jmagic.abilities.keywords.Lifelink;
import org.rnd.jmagic.abilities.keywords.Soulbond;
import org.rnd.jmagic.engine.*;

@Name("Nearheath Pilgrim")
@Types({Type.CREATURE})
@SubTypes({SubType.CLERIC, SubType.HUMAN})
@ManaCost("1W")
@Printings({@Printings.Printed(ex = Expansion.AVACYN_RESTORED, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.WHITE})
public final class NearheathPilgrim extends Card
{
	public NearheathPilgrim(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(1);

		// Soulbond (You may pair this creature with another unpaired creature
		// when either enters the battlefield. They remain paired for as long as
		// you control both of them.)
		this.addAbility(new Soulbond(state));

		// As long as Nearheath Pilgrim is paired with another creature, both
		// creatures have lifelink.
		this.addAbility(new AbilityIfPaired.Final(state, "As long as Nearheath Pilgrim is paired with another creature, both creatures have lifelink.", Lifelink.class));
	}
}
