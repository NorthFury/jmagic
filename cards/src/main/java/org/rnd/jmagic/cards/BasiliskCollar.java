package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Deathtouch;
import org.rnd.jmagic.abilities.keywords.Equip;
import org.rnd.jmagic.abilities.keywords.Lifelink;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Basilisk Collar")
@Types({Type.ARTIFACT})
@SubTypes({SubType.EQUIPMENT})
@ManaCost("1")
@Printings({@Printings.Printed(ex = Expansion.WORLDWAKE, r = Rarity.RARE)})
@ColorIdentity({})
public final class BasiliskCollar extends Card
{
	public static final class HasAbilities extends StaticAbility
	{
		public HasAbilities(GameState state)
		{
			super(state, "Equipped creature has deathtouch and lifelink.");
			this.addEffectPart(addAbilityToObject(EquippedBy.instance(This.instance()), Deathtouch.class, Lifelink.class));
		}
	}

	public BasiliskCollar(GameState state)
	{
		super(state);

		// Equipped creature has deathtouch and lifelink.
		this.addAbility(new HasAbilities(state));

		// Equip (2)
		this.addAbility(new Equip(state, "(2)"));
	}
}
