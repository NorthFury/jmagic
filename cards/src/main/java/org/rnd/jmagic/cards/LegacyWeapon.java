package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.ColossusShuffle;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Legacy Weapon")
@SuperTypes({SuperType.LEGENDARY})
@Types({Type.ARTIFACT})
@ManaCost("7")
@Printings({@Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.RARE), @Printings.Printed(ex = Expansion.APOCALYPSE, r = Rarity.RARE)})
@ColorIdentity({Color.BLUE, Color.WHITE, Color.GREEN, Color.BLACK, Color.RED})
public final class LegacyWeapon extends Card
{
	public static final class Removal extends ActivatedAbility
	{
		public Removal(GameState state)
		{
			super(state, "(W)(U)(B)(R)(G): Exile target permanent.");
			this.setManaCost(new ManaPool("WUBRG"));
			Target target = this.addTarget(InZone.instance(Battlefield.instance()), "target permanent");
			this.addEffect(exile(targetedBy(target), "Exile target permanent."));
		}
	}

	public LegacyWeapon(GameState state)
	{
		super(state);

		this.addAbility(new Removal(state));
		this.addAbility(new ColossusShuffle(state, this.getName()));
	}
}
