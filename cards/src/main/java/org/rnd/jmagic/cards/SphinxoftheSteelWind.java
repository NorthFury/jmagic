package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Lifelink;
import org.rnd.jmagic.abilities.keywords.Protection;
import org.rnd.jmagic.abilities.keywords.Vigilance;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.HasColor;

@Name("Sphinx of the Steel Wind")
@Types({Type.CREATURE, Type.ARTIFACT})
@SubTypes({SubType.SPHINX})
@ManaCost("5WUB")
@Printings({@Printings.Printed(ex = Expansion.ALARA_REBORN, r = Rarity.MYTHIC)})
@ColorIdentity({Color.BLUE, Color.WHITE, Color.BLACK})
public final class SphinxoftheSteelWind extends Card
{
	public SphinxoftheSteelWind(GameState state)
	{
		super(state);

		this.setPower(6);
		this.setToughness(6);

		this.addAbility(new Flying(state));

		this.addAbility(new FirstStrike(state));

		this.addAbility(new Vigilance(state));

		this.addAbility(new Lifelink(state));

		this.addAbility(new Protection.From(state, HasColor.instance(Color.RED, Color.GREEN), "red and from green"));
	}
}
