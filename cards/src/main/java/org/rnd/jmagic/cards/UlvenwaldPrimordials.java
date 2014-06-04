package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.Regenerate;
import org.rnd.jmagic.abilities.Werewolves;
import org.rnd.jmagic.engine.*;

@Name("Ulvenwald Primordials")
@Types({Type.CREATURE})
@SubTypes({SubType.WEREWOLF})
@Printings({@Printings.Printed(ex = Expansion.INNISTRAD, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.GREEN})
public final class UlvenwaldPrimordials extends AlternateCard
{
	public UlvenwaldPrimordials(GameState state)
	{
		super(state);

		this.setPower(5);
		this.setToughness(5);

		this.setColorIndicator(Color.GREEN);

		// (G): Regenerate Ulvenwald Primordials.
		this.addAbility(new Regenerate.Final(state, "(G)", this.getName()));

		// At the beginning of each upkeep, if a player cast two or more spells
		// last turn, transform Ulvenwald Primordials.
		this.addAbility(new Werewolves.BecomeHuman(state, this.getName()));
	}
}
