package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Defender;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Manor Gargoyle")
@Types({Type.ARTIFACT, Type.CREATURE})
@SubTypes({SubType.GARGOYLE})
@ManaCost("5")
@Printings({@Printings.Printed(ex = Expansion.INNISTRAD, r = Rarity.RARE)})
@ColorIdentity({})
public final class ManorGargoyle extends Card
{
	public static final class ManorGargoyleAbility1 extends StaticAbility
	{
		public ManorGargoyleAbility1(GameState state)
		{
			super(state, "Manor Gargoyle is indestructible as long as it has defender.");

			this.addEffectPart(indestructible(This.instance()));

			this.canApply = Both.instance(this.canApply, Intersect.instance(HasKeywordAbility.instance(Defender.class), This.instance()));
		}
	}

	public static final class ManorGargoyleAbility2 extends ActivatedAbility
	{
		public ManorGargoyleAbility2(GameState state)
		{
			super(state, "(1): Until end of turn, Manor Gargoyle loses defender and gains flying.");
			this.setManaCost(new ManaPool("(1)"));

			ContinuousEffect.Part part = new ContinuousEffect.Part(ContinuousEffectType.REMOVE_ABILITY_FROM_OBJECT);
			part.parameters.put(ContinuousEffectType.Parameter.OBJECT, ABILITY_SOURCE_OF_THIS);
			part.parameters.put(ContinuousEffectType.Parameter.ABILITY, Identity.instance(Defender.class));

			this.addEffect(createFloatingEffect("Manor Gargoyle loses defender and gains flying.", part, addAbilityToObject(ABILITY_SOURCE_OF_THIS, Flying.class)));
		}
	}

	public ManorGargoyle(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(4);

		// Defender
		this.addAbility(new Defender(state));

		// Manor Gargoyle is indestructible as long as it has defender.
		this.addAbility(new ManorGargoyleAbility1(state));

		// (1): Until end of turn, Manor Gargoyle loses defender and gains
		// flying.
		this.addAbility(new ManorGargoyleAbility2(state));
	}
}
