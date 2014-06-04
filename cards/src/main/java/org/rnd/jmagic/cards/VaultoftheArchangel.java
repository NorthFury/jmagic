package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.TapFor1;
import org.rnd.jmagic.abilities.keywords.Deathtouch;
import org.rnd.jmagic.abilities.keywords.Lifelink;
import org.rnd.jmagic.engine.*;

@Name("Vault of the Archangel")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.DARK_ASCENSION, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE, Color.BLACK})
public final class VaultoftheArchangel extends Card
{
	public static final class VaultoftheArchangelAbility1 extends ActivatedAbility
	{
		public VaultoftheArchangelAbility1(GameState state)
		{
			super(state, "(2)(W)(B), (T): Creatures you control gain deathtouch and lifelink until end of turn.");
			this.setManaCost(new ManaPool("(2)(W)(B)"));
			this.costsTap = true;

			ContinuousEffect.Part part = addAbilityToObject(CREATURES_YOU_CONTROL, Deathtouch.class, Lifelink.class);
			this.addEffect(createFloatingEffect("Creatures you control gain deathtouch and lifelink until end of turn.", part));
		}
	}

	public VaultoftheArchangel(GameState state)
	{
		super(state);

		// (T): Add (1) to your mana pool.
		this.addAbility(new TapFor1(state));

		// (2)(W)(B), (T): Creatures you control gain deathtouch and lifelink
		// until end of turn.
		this.addAbility(new VaultoftheArchangelAbility1(state));
	}
}
