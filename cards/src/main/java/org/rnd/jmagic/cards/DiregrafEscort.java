package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.AbilityIfPaired;
import org.rnd.jmagic.abilities.keywords.Protection;
import org.rnd.jmagic.abilities.keywords.Soulbond;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Diregraf Escort")
@Types({Type.CREATURE})
@SubTypes({SubType.CLERIC, SubType.HUMAN})
@ManaCost("G")
@Printings({@Printings.Printed(ex = Expansion.AVACYN_RESTORED, r = Rarity.COMMON)})
@ColorIdentity({Color.GREEN})
public final class DiregrafEscort extends Card
{
	public static final class ProtectionFromZombies extends Protection
	{
		public ProtectionFromZombies(GameState state)
		{
			super(state, HasSubType.instance(SubType.ZOMBIE), "Zombies");
		}
	}

	public DiregrafEscort(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		// Soulbond (You may pair this creature with another unpaired creature
		// when either enters the battlefield. They remain paired for as long as
		// you control both of them.)
		this.addAbility(new Soulbond(state));

		// As long as Diregraf Escort is paired with another creature, both
		// creatures have protection from Zombies.
		this.addAbility(new AbilityIfPaired.Final(state, "As long as Diregraf Escort is paired with another creature, both creatures have protection from Zombies.", ProtectionFromZombies.class));
	}
}
