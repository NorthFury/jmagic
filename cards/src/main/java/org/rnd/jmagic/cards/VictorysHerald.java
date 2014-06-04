package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Lifelink;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Victory's Herald")
@Types({Type.CREATURE})
@SubTypes({SubType.ANGEL})
@ManaCost("3WWW")
@Printings({@Printings.Printed(ex = Expansion.MIRRODIN_BESIEGED, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE})
public final class VictorysHerald extends Card
{
	public static final class VictorysHeraldAbility1 extends EventTriggeredAbility
	{
		public VictorysHeraldAbility1(GameState state)
		{
			super(state, "Whenever Victory's Herald attacks, attacking creatures gain flying and lifelink until end of turn.");
			this.addPattern(whenThisAttacks());
			this.addEffect(createFloatingEffect("Attacking creatures gain flying and lifelink until end of turn.", addAbilityToObject(Attacking.instance(), Flying.class, Lifelink.class)));
		}
	}

	public VictorysHerald(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(4);

		// Flying
		this.addAbility(new Flying(state));

		// Whenever Victory's Herald attacks, attacking creatures gain flying
		// and lifelink until end of turn.
		this.addAbility(new VictorysHeraldAbility1(state));
	}
}
