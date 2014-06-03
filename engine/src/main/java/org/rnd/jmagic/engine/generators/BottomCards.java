package org.rnd.jmagic.engine.generators;

import static org.rnd.jmagic.Convenience.*;
import org.rnd.jmagic.engine.*;

/**
 * Evaluates to the bottom number of {@link Card}s of each given {@link Zone}.
 * If no number is specified (for instance if an {@link IndexOf} took an empty
 * set (see {@link org.rnd.jmagic.cards.Polymorph})) return all the cards in all
 * the given zones. {@link GameObject}s that are not {@link Card}s will not
 * count.
 */
public class BottomCards extends SetGenerator
{
	public static BottomCards instance(int number, SetGenerator zones)
	{
		return new BottomCards(numberGenerator(number), zones);
	}

	public static BottomCards instance(SetGenerator number, SetGenerator zones)
	{
		return new BottomCards(number, zones);
	}

	public static MagicSet get(int number, Zone zone)
	{
		MagicSet ret = new MagicSet();
		int count = 0;
		java.util.ListIterator<GameObject> i = zone.objects.listIterator(zone.objects.size());
		while(i.hasPrevious())
		{
			GameObject o = i.previous();
			if(count < number)
			{
				if(o.isCard())
				{
					ret.add(o);
					++count;
				}
			}
			else
				break;
		}
		return ret;
	}

	private final SetGenerator number;
	private final SetGenerator zones;

	private BottomCards(SetGenerator number, SetGenerator zones)
	{
		this.number = number;
		this.zones = zones;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet numberSet = this.number.evaluate(state, thisObject);

		if(numberSet.isEmpty())
		{
			MagicSet ret = new MagicSet();

			for(Zone zone: this.zones.evaluate(state, thisObject).getAll(Zone.class))
				ret.addAll(zone.objects);

			return ret;
		}

		int number = Maximum.get(numberSet);
		MagicSet ret = new MagicSet();
		for(Zone zone: this.zones.evaluate(state, thisObject).getAll(Zone.class))
			ret.addAll(get(number, zone));
		return ret;
	}
}
