package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.abilities.keywords.Protection;
import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.abilities.keywords.Vigilance;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Akroma, Angel of Wrath")
@SuperTypes({SuperType.LEGENDARY})
@Types({Type.CREATURE})
@SubTypes({SubType.ANGEL})
@ManaCost("5WWW")
@Printings({@Printings.Printed(ex = Expansion.TIME_SPIRAL, r = Rarity.SPECIAL), @Printings.Printed(ex = Expansion.LEGIONS, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE})
public final class AkromaAngelofWrath extends Card
{
	public AkromaAngelofWrath(GameState state)
	{
		super(state);

		this.setPower(6);
		this.setToughness(6);

		// Flying, first strike, vigilance, trample, haste, protection from
		// black and from red
		this.addAbility(new Flying(state));
		this.addAbility(new FirstStrike(state));
		this.addAbility(new Vigilance(state));
		this.addAbility(new Trample(state));
		this.addAbility(new Haste(state));
		this.addAbility(new Protection.From(state, HasColor.instance(Color.BLACK, Color.RED), "black and from red"));
	}
}
