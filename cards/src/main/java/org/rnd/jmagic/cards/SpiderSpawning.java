package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flashback;
import org.rnd.jmagic.abilities.keywords.Reach;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Spider Spawning")
@Types({Type.SORCERY})
@ManaCost("4G")
@Printings({@Printings.Printed(ex = Expansion.INNISTRAD, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLACK, Color.GREEN})
public final class SpiderSpawning extends Card
{
	public SpiderSpawning(GameState state)
	{
		super(state);

		// Put a 1/2 green Spider creature token with reach onto the
		// battlefieldfor each creature card in your graveyard.
		SetGenerator number = Count.instance(Intersect.instance(HasType.instance(Type.CREATURE), InZone.instance(GraveyardOf.instance(You.instance()))));
		CreateTokensFactory factory = new CreateTokensFactory(number, numberGenerator(1), numberGenerator(2), "Put a 1/2 green Spider creature token with reach onto the battlefield for each creature card in your graveyard.");
		factory.setColors(Color.GREEN);
		factory.setSubTypes(SubType.SPIDER);
		factory.addAbility(Reach.class);
		this.addEffect(factory.getEventFactory());

		// Flashback (6)(B) (You may cast this card from your graveyard for its
		// flashback cost. Then exile it.)
		this.addAbility(new Flashback(state, "(6)(B)"));
	}
}
