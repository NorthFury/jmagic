package org.rnd.jmagic.engine.gameTypes.packWars;

import org.rnd.jmagic.cards.*;
import org.rnd.jmagic.engine.*;

@Name("Land booster")
public class LandBoosterFactory implements BoosterFactory
{
	private int landsOfEachBasicLandType;

	public LandBoosterFactory()
	{
		this.landsOfEachBasicLandType = 0;
	}

	/**
	 * @param landsOfEachBasicLandType The number of each basic land that are in
	 * a pack generated from this factory.
	 */
	public LandBoosterFactory(int landsOfEachBasicLandType)
	{
		this.landsOfEachBasicLandType = landsOfEachBasicLandType;
	}

	@Override
	@SuppressWarnings("unchecked")
	public java.util.List<Card> createBooster(GameState state)
	{
		java.util.List<Card> ret = new java.util.LinkedList<Card>();

		for(Class<?> clazz: new Class<?>[] {Plains.class, Island.class, Swamp.class, Mountain.class, Forest.class})
			for(int i = 0; i < this.landsOfEachBasicLandType; ++i)
			{
				Card instance = org.rnd.util.Constructor.construct((Class<? extends Card>)clazz, new Class<?>[] {GameState.class}, new Object[] {state});
				instance.setExpansionSymbol(Expansion.MAGIC_2011);
				ret.add(instance);
			}

		return ret;
	}

	public int getLandsOfEachBasicLandType()
	{
		return this.landsOfEachBasicLandType;
	}

	public void setLandsOfEachBasicLandType(int landsOfEachBasicLandType)
	{
		this.landsOfEachBasicLandType = landsOfEachBasicLandType;
	}
}
