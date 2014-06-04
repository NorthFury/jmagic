package org.rnd.jmagic.engine;

import org.rnd.jmagic.sanitized.SanitizedCostCollection;
import org.rnd.util.SeparatedList;

import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class CostCollection implements Sanitizable
{
	public static final String TYPE_ALTERNATE = "Alternate cost";
	public static final String TYPE_MANA = "Mana cost";
	public static final String TYPE_STOP_TRIGGER = "Cost to stop a trigger";
	public static final String TYPE_REDUCE_COST = "Reduction";

	/**
	 * What kind of cost this is. This string will be used to compare costs that
	 * are otherwise identical (i.e., Kicker U vs. Buyback U), and also will be
	 * given to interfaces in order to help their users make decisions about
	 * alternate or additional costs. It is idiomatic to start this string with
	 * a capital letter. (TODO : should we make it idiomatic to have this string
	 * contain no capital letters, and have interfaces capitalize as desired?)
	 */
	public final Object type;

	/**
	 * Whether to allow this cost to be chosen multiple times during the prompt
	 * for additional costs.
	 */
	public final boolean allowMultiples;

	public final ManaPool manaCost;
	public final Set<EventFactory> events;

	public CostCollection(CostCollection copy)
	{
		this.type = copy.type;
		this.allowMultiples = copy.allowMultiples;
		this.manaCost = new ManaPool(copy.manaCost);
		this.events = Collections.unmodifiableSet(new HashSet<EventFactory>(copy.events));
	}

	public CostCollection(Object type, EventFactory... events)
	{
		this.type = type;
		this.allowMultiples = false;
		this.manaCost = new ManaPool();
		this.events = eventSet(events);
	}

	public CostCollection(Object type, ManaPool manaCost, EventFactory... events)
	{
		this.type = type;
		this.allowMultiples = false;
		this.manaCost = new ManaPool(manaCost);
		this.events = eventSet(events);
	}

	public CostCollection(Object type, String manaCost, EventFactory... events)
	{
		this.type = type;
		this.allowMultiples = false;
		this.manaCost = new ManaPool(manaCost);
		this.events = eventSet(events);
	}

	public CostCollection(Object type, boolean allowMultiples, String manaCost, EventFactory... events)
	{
		this.type = type;
		this.allowMultiples = allowMultiples;
		this.manaCost = new ManaPool(manaCost);
		this.events = eventSet(events);
	}

	/**
	 * Returns a set representing all the events and mana symbols for this cost.
	 * Useful for, say, passing to CAST_SPELL_OR_ACTIVATE_ABILITY
	 */
	public MagicSet getSet()
	{
		MagicSet ret = new MagicSet();
		ret.addAll(this.manaCost);
		ret.addAll(this.events);
		return ret;
	}

	@Override
	public int hashCode()
	{
		int result = 31 + ((this.events == null) ? 0 : this.events.hashCode());
		result = 31 * result + ((this.type == null) ? 0 : this.type.hashCode());
		result = 31 * result + ((this.manaCost == null) ? 0 : this.manaCost.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(!(obj instanceof CostCollection))
			return false;
		CostCollection other = (CostCollection)obj;
		if(this.type == null)
		{
			if(other.type != null)
				return false;
		}
		else if(!this.type.equals(other.type))
			return false;
		if(this.events == null)
		{
			if(other.events != null)
				return false;
		}
		else if(!this.events.equals(other.events))
			return false;
		if(this.manaCost == null)
		{
			if(other.manaCost != null)
				return false;
		}
		else if(!this.manaCost.equals(other.manaCost))
			return false;
		return true;
	}

	private static Set<EventFactory> eventSet(EventFactory[] events)
	{
		Set<EventFactory> eventsSet = new HashSet<EventFactory>();
		for(EventFactory factory: events)
			eventsSet.add(factory);
		return Collections.unmodifiableSet(eventsSet);
	}

	@Override
	public String toString()
	{
		if(this.manaCost.isEmpty() && this.events.isEmpty())
			return "(0)";

		// TODO : Make this be ordered "correctly" based on the order of the
		// events passed to this object's constructor. (Possibly use
		// "A, B, and C" style syntax.) Do the same for the sanitized version of
		// this class.
		Collection<Object> convert = new LinkedList<Object>();
		if(!this.manaCost.isEmpty())
			convert.add(this.manaCost);
		convert.addAll(this.events);
		return SeparatedList.get("", convert).toString();
	}

	@Override
	public Serializable sanitize(GameState state, Player whoFor)
	{
		return new SanitizedCostCollection(this);
	}
}
