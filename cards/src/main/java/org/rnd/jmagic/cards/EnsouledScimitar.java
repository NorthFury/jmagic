package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.StaticPTChange;
import org.rnd.jmagic.abilities.keywords.Equip;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Ensouled Scimitar")
@Types({Type.ARTIFACT})
@SubTypes({SubType.EQUIPMENT})
@ManaCost("3")
@Printings({@Printings.Printed(ex = Expansion.FIFTH_DAWN, r = Rarity.UNCOMMON)})
@ColorIdentity({})
public final class EnsouledScimitar extends Card
{
	public static final class Animate extends ActivatedAbility
	{
		public Animate(GameState state)
		{
			super(state, "(3): Ensouled Scimitar becomes a 1/5 Spirit artifact creature with flying until end of turn.");
			this.setManaCost(new ManaPool("(3)"));

			Animator animator = new Animator(ABILITY_SOURCE_OF_THIS, 1, 5);
			animator.addColor(Color.BLUE);
			animator.addSubType(SubType.SPIRIT);
			animator.addType(Type.ARTIFACT);
			animator.removeOldTypes();
			animator.addAbility(Flying.class);
			this.addEffect(createFloatingEffect("Ensouled Scimitar becomes a 1/5 Spirit artifact creature with flying until end of turn.", animator.getParts()));
		}
	}

	public EnsouledScimitar(GameState state)
	{
		super(state);

		// (3): Ensouled Scimitar becomes a 1/5 Spirit artifact creature with
		// flying until end of turn.
		this.addAbility(new Animate(state));

		// Equipped creature gets +1/+5.
		this.addAbility(new StaticPTChange(state, EquippedBy.instance(This.instance()), "Equipped creature", +1, +5, false));

		this.addAbility(new Equip(state, "(2)"));
	}
}
