package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.abilities.keywords.Protection;
import org.rnd.jmagic.abilities.keywords.Vigilance;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Elite Inquisitor")
@Types({Type.CREATURE})
@SubTypes({SubType.SOLDIER, SubType.HUMAN})
@ManaCost("WW")
@Printings({@Printings.Printed(ex = Expansion.INNISTRAD, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE})
public final class EliteInquisitor extends Card
{
	public static final class EliteInquisitorAbility1 extends Protection
	{
		private static SetGenerator from()
		{
			return Intersect.instance(HasSubType.instance(SubType.VAMPIRE, SubType.WEREWOLF, SubType.ZOMBIE), Permanents.instance());
		}

		public EliteInquisitorAbility1(GameState state)
		{
			super(state, from(), "Vampires, from Werewolves, and from Zombies");
		}
	}

	public EliteInquisitor(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// First strike, vigilance
		this.addAbility(new FirstStrike(state));
		this.addAbility(new Vigilance(state));

		// Protection from Vampires, from Werewolves, and from Zombies
		this.addAbility(new EliteInquisitorAbility1(state));
	}
}
