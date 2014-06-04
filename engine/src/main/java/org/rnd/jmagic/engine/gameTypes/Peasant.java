package org.rnd.jmagic.engine.gameTypes;

import org.rnd.jmagic.engine.*;

import java.util.List;
import java.util.Map;

@Name("Peasant")
@Description("All rares are disallowed.  Only 5 uncommons are allowed.")
public class Peasant extends GameType.SimpleGameTypeRule
{
	@Override
	public boolean checkCard(Class<? extends Card> card)
	{
		Printings printings = card.getAnnotation(Printings.class);
		for(Printings.Printed printing: printings.value())
			if(printing.r().equals(Rarity.COMMON) || printing.r().equals(Rarity.UNCOMMON))
				return true;
		return false;
	}

	@Override
	public boolean checkDeck(Map<String, List<Class<? extends Card>>> deck)
	{
		int uncommons = 0;

		for(List<Class<? extends Card>> cards: deck.values())
		{
			cardLoop: for(Class<? extends Card> card: cards)
			{
				boolean uncommonFound = false;
				Printings printings = card.getAnnotation(Printings.class);
				for(Printings.Printed printing: printings.value())
				{
					if(printing.r().equals(Rarity.COMMON))
						continue cardLoop;
					if(printing.r().equals(Rarity.UNCOMMON))
						uncommonFound = true;
				}
				if(uncommonFound == false || ++uncommons > 5)
					return false;
			}
		}

		return true;
	}

}
