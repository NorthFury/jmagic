package org.rnd.jmagic.sanitized;

import org.rnd.jmagic.engine.*;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class SanitizedMode implements Serializable
{
	private static final long serialVersionUID = 1L;

	public final List<String> effects;
	public final int index;
	public final int sourceID;
	public final List<SanitizedTarget> targets;
	public final String toString;

	public SanitizedMode(Mode m, int sourceID, int index)
	{
		StringBuilder toString = null;
		this.effects = new LinkedList<String>();
		for(EventFactory f: m.effects)
		{
			if(f.hidden)
				continue;
			if(toString == null)
				toString = new StringBuilder();
			else
				toString.append(' ');
			toString.append(f.name);
			this.effects.add(f.name);
		}

		this.index = index;

		this.sourceID = sourceID;

		this.targets = new LinkedList<SanitizedTarget>();
		for(Target t: m.targets)
			this.targets.add(new SanitizedTarget(t));

		if(toString == null)
			// Auras have a mode (because they have a target), but have no
			// effects.
			this.toString = "";
		else
			this.toString = toString.toString();
	}

	@Override
	public String toString()
	{
		return this.toString;
	}
}
