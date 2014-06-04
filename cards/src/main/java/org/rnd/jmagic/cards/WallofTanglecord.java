package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Defender;
import org.rnd.jmagic.abilities.keywords.Reach;
import org.rnd.jmagic.engine.*;

@Name("Wall of Tanglecord")
@Types({Type.ARTIFACT, Type.CREATURE})
@SubTypes({SubType.WALL})
@ManaCost("2")
@Printings({@Printings.Printed(ex = Expansion.SCARS_OF_MIRRODIN, r = Rarity.COMMON)})
@ColorIdentity({Color.GREEN})
public final class WallofTanglecord extends Card
{
	public static final class WallofTanglecordAbility1 extends ActivatedAbility
	{
		public WallofTanglecordAbility1(GameState state)
		{
			super(state, "(G): Wall of Tanglecord gains reach until end of turn.");
			this.setManaCost(new ManaPool("(G)"));
			this.addEffect(addAbilityUntilEndOfTurn(ABILITY_SOURCE_OF_THIS, Reach.class, "Wall of Tanglecord gains reach until end of turn."));
		}
	}

	public WallofTanglecord(GameState state)
	{
		super(state);

		this.setPower(0);
		this.setToughness(6);

		// Defender
		this.addAbility(new Defender(state));

		// (G): Wall of Tanglecord gains reach until end of turn. (It can block
		// creatures with flying.)
		this.addAbility(new WallofTanglecordAbility1(state));
	}
}
