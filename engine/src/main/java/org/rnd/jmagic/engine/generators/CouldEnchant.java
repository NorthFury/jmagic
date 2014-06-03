package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class CouldEnchant extends SetGenerator
{
	public static SetGenerator instance(SetGenerator what)
	{
		return new CouldEnchant(what);
	}

	private SetGenerator what;

	private CouldEnchant(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		MagicSet what = this.what.evaluate(state, thisObject);
		auras: for(GameObject aura: state.getAllObjects())
		{
			if(!aura.getSubTypes().contains(SubType.AURA))
				continue;

			for(Keyword ability: aura.getKeywordAbilities())
				if(ability.isEnchant())
				{
					org.rnd.jmagic.abilities.keywords.Enchant e = (org.rnd.jmagic.abilities.keywords.Enchant)ability;
					MagicSet intermediate = new MagicSet(what);
					intermediate.retainAll(e.filter.evaluate(state, aura));
					for(GameObject o: intermediate.getAll(GameObject.class))
						if(!o.cantBeAttachedBy().match(state, thisObject, new MagicSet(aura)))
						{
							ret.add(aura);
							continue auras;
						}
				}
		}
		return ret;
	}
}
