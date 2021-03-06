package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Exalted;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilityTemplates.ExaltedBase;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Nefarox, Overlord of Grixis")
@SuperTypes({SuperType.LEGENDARY})
@Types({Type.CREATURE})
@SubTypes({SubType.DEMON})
@ManaCost("4BB")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2013, r = Rarity.RARE)})
@ColorIdentity({Color.BLACK})
public final class NefaroxOverlordofGrixis extends Card
{
	public static final class NefaroxOverlordofGrixisAbility2 extends ExaltedBase
	{
		public NefaroxOverlordofGrixisAbility2(GameState state)
		{
			super(state, "Whenever Nefarox, Overlord of Grixis attacks alone, defending player sacrifices a creature.");

			SetGenerator defendingPlayer = EventParameter.instance(TriggerEvent.instance(This.instance()), EventType.Parameter.DEFENDER);
			this.addEffect(sacrifice(defendingPlayer, 1, CreaturePermanents.instance(), "Defending player sacrifices a creature."));
		}
	}

	public NefaroxOverlordofGrixis(GameState state)
	{
		super(state);

		this.setPower(5);
		this.setToughness(5);

		// Flying
		this.addAbility(new Flying(state));

		// Exalted (Whenever a creature you control attacks alone, that creature
		// gets +1/+1 until end of turn.)
		this.addAbility(new Exalted(state));

		// Whenever Nefarox, Overlord of Grixis attacks alone, defending player
		// sacrifices a creature.
		this.addAbility(new NefaroxOverlordofGrixisAbility2(state));
	}
}
