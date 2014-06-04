package org.rnd.jmagic;

import org.rnd.jmagic.engine.*;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;

public class CompareManaPools implements Comparator<ManaPool>, Serializable
{
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(ManaPool a, ManaPool b)
	{
		// bigger pools later
		if(a.converted() != b.converted())
			return a.converted() - b.converted();

		Iterator<ManaSymbol> x = a.iterator();
		Iterator<ManaSymbol> y = b.iterator();

		while(true)
		{
			// ensure equivalent pools compare properly
			if(!x.hasNext() && !y.hasNext())
				return 0;

			// Shorter before longer; i.e., "(6)" > "(4)(R)"
			if(!x.hasNext())
				return 1;
			if(!y.hasNext())
				return -1;

			ManaSymbol s = x.next();
			ManaSymbol t = y.next();
			if(s.toString().equals(t.toString()))
				continue;

			return s.compareTo(t);
		}
	}
}