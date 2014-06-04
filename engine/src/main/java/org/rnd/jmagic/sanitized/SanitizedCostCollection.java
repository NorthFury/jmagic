package org.rnd.jmagic.sanitized;

import org.rnd.jmagic.engine.CostCollection;
import org.rnd.jmagic.engine.EventFactory;
import org.rnd.jmagic.engine.ManaPool;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SanitizedCostCollection implements Serializable
{

	public static class SanitizedEventFactory implements Serializable
	{
		private static final long serialVersionUID = 1L;

		public final String name;
		// public final org.rnd.jmagic.engine.EventType type;
		public final String type;

		public SanitizedEventFactory(EventFactory ef)
		{
			this.name = ef.name;
			this.type = ef.type.toString();
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
			result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if(this == obj)
				return true;
			if(obj == null)
				return false;
			if(getClass() != obj.getClass())
				return false;
			SanitizedEventFactory other = (SanitizedEventFactory)obj;
			if(this.name == null)
			{
				if(other.name != null)
					return false;
			}
			else if(!this.name.equals(other.name))
				return false;
			if(this.type == null)
			{
				if(other.type != null)
					return false;
			}
			else if(!this.type.equals(other.type))
				return false;
			return true;
		}
	}

	private static final long serialVersionUID = 2L;

	public final String type;
	public final boolean allowMultiples;
	public final Set<SanitizedEventFactory> events;
	public final ManaPool manaCost;

	private final String toString;

	public SanitizedCostCollection(CostCollection c)
	{
		Set<SanitizedEventFactory> events = new HashSet<SanitizedEventFactory>();

		for(EventFactory ef: c.events)
			events.add(new SanitizedEventFactory(ef));

		this.type = c.type.toString();

		this.allowMultiples = c.allowMultiples;

		this.events = Collections.unmodifiableSet(events);

		this.manaCost = new ManaPool(c.manaCost);

		this.toString = this.type + ": " + c.toString();
	}

	public SanitizedCostCollection(String type, ManaPool mana)
	{
		this.type = type;
		this.allowMultiples = false;
		this.events = Collections.emptySet();
		this.manaCost = mana;
		this.toString = mana.toString();
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
		if(getClass() != obj.getClass())
			return false;
		SanitizedCostCollection other = (SanitizedCostCollection)obj;
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

	@Override
	public String toString()
	{
		return this.toString;
	}
}
