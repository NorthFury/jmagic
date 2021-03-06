package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.YouControlEnchantedCreature;
import org.rnd.jmagic.abilities.keywords.CumulativeUpkeep;
import org.rnd.jmagic.abilities.keywords.Enchant;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Mind Harness")
@Types({Type.ENCHANTMENT})
@SubTypes({SubType.AURA})
@ManaCost("U")
@Printings({@Printings.Printed(ex = Expansion.MIRAGE, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLUE})
public final class MindHarness extends Card
{
	public MindHarness(GameState state)
	{
		super(state);

		// Enchant red or green creature
		SetGenerator filter = Intersect.instance(HasColor.instance(Color.RED, Color.GREEN), CreaturePermanents.instance());
		this.addAbility(new Enchant.Final(state, "red or green creature", filter));

		// Cumulative upkeep (1) (At the beginning of your upkeep, put an age
		// counter on this permanent, then sacrifice it unless you pay its
		// upkeep cost for each age counter on it.)
		this.addAbility(new CumulativeUpkeep.ForMana(state, "(1)"));

		// You control enchanted creature.
		this.addAbility(new YouControlEnchantedCreature(state));
	}
}
