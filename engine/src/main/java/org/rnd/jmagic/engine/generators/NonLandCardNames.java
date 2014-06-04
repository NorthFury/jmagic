package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.CardLoader;
import org.rnd.jmagic.engine.*;

public class NonLandCardNames extends SetGenerator
{
	private static final NonLandCardNames _instance = new NonLandCardNames();

	// I really don't want to do this work twice, so we'll store the result here
	// the first time.
	private static MagicSet set = null;

	public static NonLandCardNames instance()
	{
		return _instance;
	}

	public static MagicSet get()
	{
		if(set == null)
		{
			MagicSet ret = new MagicSet();
			cardLoop: for(Class<? extends Card> card: CardLoader.getAllCards())
			{
				Types types = card.getAnnotation(Types.class);
				if(types != null)
				{
					for(Type type: types.value())
					{
						if(type.equals(Type.LAND))
							continue cardLoop;
					}
					Name name = card.getAnnotation(Name.class);
					if(name != null)
						ret.add(name.value());
				}
			}
			set = new MagicSet.Unmodifiable(ret);
		}
		return set;
	}

	private NonLandCardNames()
	{
		// Singleton Constructor
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		return get();
	}

}
