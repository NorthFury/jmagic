package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.Regenerate;
import org.rnd.jmagic.engine.*;

@Name("Drudge Skeletons")
@Types({Type.CREATURE})
@SubTypes({SubType.SKELETON})
@ManaCost("1B")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2010, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.NINTH_EDITION, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.EIGHTH_EDITION, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.SEVENTH_EDITION, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.STARTER_2000, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.SIXTH_EDITION, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.FIFTH_EDITION, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.FOURTH_EDITION, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.REVISED, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.UNLIMITED, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.BETA, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.ALPHA, r = Rarity.COMMON)})
@ColorIdentity({Color.BLACK})
public final class DrudgeSkeletons extends Card
{
	public DrudgeSkeletons(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		this.addAbility(new Regenerate.Final(state, "(B)", this.getName()));
	}
}
