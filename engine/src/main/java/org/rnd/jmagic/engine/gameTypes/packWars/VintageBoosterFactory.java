package org.rnd.jmagic.engine.gameTypes.packWars;

import org.rnd.jmagic.CardLoader;
import org.rnd.jmagic.engine.*;
import org.rnd.util.Constructor;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Name("Vintage booster")
public class VintageBoosterFactory implements BoosterFactory
{
	private static Random rand = new Random();

	@Override
	public List<Card> createBooster(GameState state) throws CardLoader.CardLoaderException
	{
		List<Card> ret = new LinkedList<Card>();
		Set<Class<? extends Card>> pool = CardLoader.getAllCards();

		cardLoop: for(int i = 0; i < 15; ++i)
		{
			int k = rand.nextInt(pool.size());

			Iterator<Class<? extends Card>> iter = pool.iterator();

			for(int j = 1; j < k; ++j)
				iter.next();

			Class<? extends Card> card = iter.next();

			// Ignore any basics
			SuperTypes superTypes = card.getAnnotation(SuperTypes.class);
			if(superTypes != null)
				for(SuperType superType: superTypes.value())
					if(superType.equals(SuperType.BASIC))
					{
						--i;
						continue cardLoop;
					}

			// All the types should share 'Traditional-ness', so only check one.
			Types types = card.getAnnotation(Types.class);
			if(types != null && types.value()[0].isTraditional())
				ret.add(Constructor.construct(card, new Class<?>[] {GameState.class}, new Object[] {state}));
			else
				--i;
		}

		return ret;
	}
}
