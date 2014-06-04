package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Hypersonic Dragon")
@Types({Type.CREATURE})
@SubTypes({SubType.DRAGON})
@ManaCost("3UR")
@Printings({@Printings.Printed(ex = Expansion.RETURN_TO_RAVNICA, r = Rarity.RARE)})
@ColorIdentity({Color.BLUE, Color.RED})
public final class HypersonicDragon extends Card
{
	public static final class HypersonicDragonAbility1 extends StaticAbility
	{
		public HypersonicDragonAbility1(GameState state)
		{
			super(state, "You may cast sorcery spells as though they had flash.");

			SetGenerator youHavePriority = Intersect.instance(You.instance(), PlayerWithPriority.instance());

			ContinuousEffect.Part part = new ContinuousEffect.Part(ContinuousEffectType.MAY_CAST_TIMING);
			part.parameters.put(ContinuousEffectType.Parameter.OBJECT, HasType.instance(Type.SORCERY));
			part.parameters.put(ContinuousEffectType.Parameter.PERMISSION, Identity.instance(new PlayPermission(youHavePriority)));
			this.addEffectPart(part);
		}
	}

	public HypersonicDragon(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(4);

		// Flying, haste
		this.addAbility(new Flying(state));
		this.addAbility(new Haste(state));

		// You may cast sorcery spells as though they had flash. (You may cast
		// them any time you could cast an instant.)
		this.addAbility(new HypersonicDragonAbility1(state));
	}
}
