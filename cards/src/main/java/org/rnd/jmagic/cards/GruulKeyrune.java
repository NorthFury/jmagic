package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.TapForMana;
import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.engine.*;

@Name("Gruul Keyrune")
@Types({Type.ARTIFACT})
@ManaCost("3")
@Printings({@Printings.Printed(ex = Expansion.GATECRASH, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.GREEN, Color.RED})
public final class GruulKeyrune extends Card
{
	public static final class GruulKeyruneAbility1 extends ActivatedAbility
	{
		public GruulKeyruneAbility1(GameState state)
		{
			super(state, "(R)(G): Gruul Keyrune becomes a 3/2 red and green Beast artifact creature with trample until end of turn.");
			this.setManaCost(new ManaPool("(R)(G)"));

			Animator animate = new Animator(ABILITY_SOURCE_OF_THIS, 3, 2);
			animate.addColor(Color.RED);
			animate.addColor(Color.GREEN);
			animate.addSubType(SubType.BEAST);
			animate.addType(Type.ARTIFACT);
			animate.addAbility(Trample.class);
			this.addEffect(createFloatingEffect("Gruul Keyrune becomes a 3/2 red and green Beast artifact creature with trample until end of turn.", animate.getParts()));
		}
	}

	public GruulKeyrune(GameState state)
	{
		super(state);

		// (T): Add (R) or (G) to your mana pool.
		this.addAbility(new TapForMana.Final(state, "(RG)"));

		// (R)(G): Gruul Keyrune becomes a 3/2 red and green Beast artifact
		// creature with trample until end of turn.
		this.addAbility(new GruulKeyruneAbility1(state));
	}
}
