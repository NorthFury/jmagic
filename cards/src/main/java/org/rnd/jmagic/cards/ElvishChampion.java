package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.StaticPTChange;
import org.rnd.jmagic.abilities.keywords.Landwalk;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Elvish Champion")
@Types({Type.CREATURE})
@SubTypes({SubType.ELF})
@ManaCost("1GG")
@Printings({@Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.RARE), @Printings.Printed(ex = Expansion.NINTH_EDITION, r = Rarity.RARE), @Printings.Printed(ex = Expansion.EIGHTH_EDITION, r = Rarity.RARE), @Printings.Printed(ex = Expansion.SEVENTH_EDITION, r = Rarity.RARE), @Printings.Printed(ex = Expansion.INVASION, r = Rarity.RARE)})
@ColorIdentity({Color.GREEN})
public final class ElvishChampion extends Card
{

	public ElvishChampion(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		SetGenerator otherElfCreatures = RelativeComplement.instance(Intersect.instance(CreaturePermanents.instance(), HasSubType.instance(SubType.ELF)), This.instance());
		this.addAbility(new StaticPTChange(state, otherElfCreatures, "Other Elf creatures", +1, +1, Landwalk.Forestwalk.class, true));
	}
}
