package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Evaluates to each attacking creature, or to each creature attacking a given
 * object. It is safe to assume that this will only return creatures.
 */
public class Attacking extends SetGenerator
{
	private static final Attacking _instance = new Attacking();

	public static Attacking instance()
	{
		return _instance;
	}

	public static Attacking instance(SetGenerator what)
	{
		return new Attacking(what);
	}

	private final SetGenerator what;

	private Attacking()
	{
		this.what = null;
	}

	private Attacking(SetGenerator what)
	{
		this.what = what;
	}

	public static MagicSet get(GameState state)
	{
		MagicSet ret = new MagicSet();
		for(GameObject o: state.battlefield())
			if(-1 != o.getAttackingID())
				ret.add(o);
		return ret;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet allAttackers = Attacking.get(state);
		if(this.what == null)
			return allAttackers;

		Set<Integer> beingAttackedIDs = new HashSet<Integer>();
		MagicSet evaluateWhat = this.what.evaluate(state, thisObject);
		for(GameObject o: evaluateWhat.getAll(GameObject.class))
			if(o.getTypes().contains(Type.PLANESWALKER))
				beingAttackedIDs.add(o.ID);
		for(Player p: evaluateWhat.getAll(Player.class))
			beingAttackedIDs.add(p.ID);

		Set<GameObject> attackingThisPlayer = allAttackers.getAll(GameObject.class);
		Iterator<GameObject> objectIterator = attackingThisPlayer.iterator();
		while(objectIterator.hasNext())
		{
			GameObject attacker = objectIterator.next();
			if(!beingAttackedIDs.contains(attacker.getAttackingID()))
				objectIterator.remove();
		}

		return new MagicSet(attackingThisPlayer);
	}
}
