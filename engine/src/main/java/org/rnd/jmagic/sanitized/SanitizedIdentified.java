package org.rnd.jmagic.sanitized;

import org.rnd.jmagic.abilities.keywords.Level;
import org.rnd.jmagic.engine.Identified;

import java.io.Serializable;

public class SanitizedIdentified implements Serializable
{
	private static final long serialVersionUID = 1L;

	public final int ID;
	public final boolean isKeyword;
	public final String name;

	public SanitizedIdentified(Identified i)
	{
		this(i, i.getName());
	}

	public SanitizedIdentified(Identified i, String nameOverride)
	{
		this.ID = i.ID;
		this.isKeyword = i.isKeyword() && !(i instanceof Level);
		this.name = nameOverride;
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
		SanitizedIdentified other = (SanitizedIdentified)obj;
		if(this.ID != other.ID)
			return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		return this.ID;
	}

	@Override
	public String toString()
	{
		return this.name;
	}
}
