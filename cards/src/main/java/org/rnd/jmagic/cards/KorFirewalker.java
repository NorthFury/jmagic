package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Protection;
import org.rnd.jmagic.abilityTemplates.ColorTriggeredLife;
import org.rnd.jmagic.engine.*;

@Name("Kor Firewalker")
@Types({Type.CREATURE})
@SubTypes({SubType.KOR, SubType.SOLDIER})
@ManaCost("WW")
@Printings({@Printings.Printed(ex = Expansion.WORLDWAKE, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.WHITE})
public final class KorFirewalker extends Card
{
	public static final class LifeGain extends ColorTriggeredLife
	{
		public LifeGain(GameState state)
		{
			super(state, Color.RED);
		}
	}

	public KorFirewalker(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// Protection from red
		this.addAbility(new Protection.FromRed(state));

		// Whenever a player casts a red spell, you may gain 1 life.
		this.addAbility(new LifeGain(state));
	}
}
