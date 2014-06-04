package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.StaticPTChange;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Ascendant Evincar")
@SuperTypes({SuperType.LEGENDARY})
@Types({Type.CREATURE})
@SubTypes({SubType.VAMPIRE})
@ManaCost("4BB")
@Printings({@Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.RARE), @Printings.Printed(ex = Expansion.NEMESIS, r = Rarity.RARE)})
@ColorIdentity({Color.BLACK})
public final class AscendantEvincar extends Card
{
	public AscendantEvincar(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(3);

		this.addAbility(new Flying(state));

		SetGenerator blackCreatures = Intersect.instance(CreaturePermanents.instance(), HasColor.instance(Color.BLACK));
		SetGenerator otherBlackCreatures = RelativeComplement.instance(blackCreatures, This.instance());
		this.addAbility(new StaticPTChange(state, otherBlackCreatures, "Other black creatures", +1, +1, true));

		SetGenerator nonBlackCreatures = RelativeComplement.instance(CreaturePermanents.instance(), HasColor.instance(Color.BLACK));
		this.addAbility(new StaticPTChange(state, nonBlackCreatures, "Nonblack creatures", -1, -1, true));
	}
}
