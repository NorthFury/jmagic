package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Equip;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.jmagic.engine.patterns.BecomesTheTargetPattern;

@Name("Livewire Lash")
@Types({Type.ARTIFACT})
@SubTypes({SubType.EQUIPMENT})
@ManaCost("2")
@Printings({@Printings.Printed(ex = Expansion.SCARS_OF_MIRRODIN, r = Rarity.RARE)})
@ColorIdentity({})
public final class LivewireLash extends Card
{
	public static final class Livewire extends EventTriggeredAbility
	{
		public Livewire(GameState state)
		{
			super(state, "Whenever this creature becomes the target of a spell, this creature deals 2 damage to target creature or player.");
			this.addPattern(new BecomesTheTargetPattern(ABILITY_SOURCE_OF_THIS, Spells.instance()));
			SetGenerator target = targetedBy(this.addTarget(CREATURES_AND_PLAYERS, "target creature or player"));
			this.addEffect(permanentDealDamage(2, target, "This creature deals 2 damage to target creature or player."));
		}
	}

	public static final class LivewireLashAbility0 extends StaticAbility
	{
		public LivewireLashAbility0(GameState state)
		{
			super(state, "Equipped creature gets +2/+0 and has \"Whenever this creature becomes the target of a spell, this creature deals 2 damage to target creature or player.\"");
			SetGenerator equipped = EquippedBy.instance(This.instance());
			this.addEffectPart(modifyPowerAndToughness(equipped, +2, +0));
			this.addEffectPart(addAbilityToObject(equipped, Livewire.class));
		}
	}

	public LivewireLash(GameState state)
	{
		super(state);

		// Equipped creature gets +2/+0 and has
		// "Whenever this creature becomes the target of a spell, this creature deals 2 damage to target creature or player."
		this.addAbility(new LivewireLashAbility0(state));

		// Equip (2)
		this.addAbility(new Equip(state, "(2)"));
	}
}
