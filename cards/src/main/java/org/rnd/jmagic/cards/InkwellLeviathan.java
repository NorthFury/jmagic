package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Landwalk;
import org.rnd.jmagic.abilities.keywords.Shroud;
import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.engine.*;

@Name("Inkwell Leviathan")
@Types({Type.ARTIFACT, Type.CREATURE})
@SubTypes({SubType.LEVIATHAN})
@ManaCost("7UU")
@Printings({@Printings.Printed(ex = Expansion.CONFLUX, r = Rarity.RARE)})
@ColorIdentity({Color.BLUE})
public final class InkwellLeviathan extends Card
{
	public InkwellLeviathan(GameState state)
	{
		super(state);

		this.setPower(7);
		this.setToughness(11);

		this.addAbility(new Landwalk.Islandwalk(state));

		this.addAbility(new Trample(state));

		this.addAbility(new Shroud(state));
	}
}
