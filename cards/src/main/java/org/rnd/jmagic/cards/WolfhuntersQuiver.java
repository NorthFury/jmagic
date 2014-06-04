package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.Ping;
import org.rnd.jmagic.abilities.keywords.Equip;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Wolfhunter's Quiver")
@Types({Type.ARTIFACT})
@SubTypes({SubType.EQUIPMENT})
@ManaCost("1")
@Printings({@Printings.Printed(ex = Expansion.DARK_ASCENSION, r = Rarity.UNCOMMON)})
@ColorIdentity({})
public final class WolfhuntersQuiver extends Card
{
	public static final class ShootWerewolves extends ActivatedAbility
	{
		public ShootWerewolves(GameState state)
		{
			super(state, "(T): This creature deals 3 damage to target Werewolf creature.");
			this.costsTap = true;

			SetGenerator werewolves = Intersect.instance(HasSubType.instance(SubType.WEREWOLF), CreaturePermanents.instance());
			SetGenerator target = targetedBy(this.addTarget(werewolves, "target Werewolf creature"));
			this.addEffect(permanentDealDamage(1, target, "This creature deals 3 damage to target Werewolf creature."));
		}
	}

	public static final class WolfhuntersQuiverAbility0 extends StaticAbility
	{
		public WolfhuntersQuiverAbility0(GameState state)
		{
			super(state, "Equipped creature has \"(T): This creature deals 1 damage to target creature or player\" and \"(T): This creature deals 3 damage to target Werewolf creature.\"");

			this.addEffectPart(addAbilityToObject(EquippedBy.instance(This.instance()), Ping.class, ShootWerewolves.class));
		}
	}

	public WolfhuntersQuiver(GameState state)
	{
		super(state);

		// Equipped creature has
		// "(T): This creature deals 1 damage to target creature or player" and
		// "(T): This creature deals 3 damage to target Werewolf creature."
		this.addAbility(new WolfhuntersQuiverAbility0(state));

		// Equip (5)
		this.addAbility(new Equip(state, "(5)"));
	}
}
