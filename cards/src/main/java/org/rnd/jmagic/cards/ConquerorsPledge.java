package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Kicker;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Conqueror's Pledge")
@Types({Type.SORCERY})
@ManaCost("2WWW")
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE})
public final class ConquerorsPledge extends Card
{
	public ConquerorsPledge(GameState state)
	{
		super(state);

		Kicker ability = new Kicker(state, "6");
		this.addAbility(ability);

		// Kicker (6) (You may pay an additional (6) as you cast this spell.)
		CostCollection kickerCost = ability.costCollections[0];

		// Put six 1/1 white Kor Soldier creature tokens onto the battlefield.
		// If Conqueror's Pledge was kicked, put twelve of those tokens onto the
		// battlefield instead.
		SetGenerator numTokens = IfThenElse.instance(ThisSpellWasKicked.instance(kickerCost), numberGenerator(12), numberGenerator(6));
		String effectName = "Put six 1/1 white Kor Soldier creature tokens onto the battlefield. If Conqueror's Pledge was kicked, put twelve of those tokens onto the battlefield instead.";
		CreateTokensFactory tokens = new CreateTokensFactory(numTokens, numberGenerator(1), numberGenerator(1), effectName);
		tokens.setColors(Color.WHITE);
		tokens.setSubTypes(SubType.KOR, SubType.SOLDIER);
		this.addEffect(tokens.getEventFactory());
	}
}
