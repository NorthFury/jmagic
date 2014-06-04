package org.rnd.jmagic.engine;

import java.util.Collection;
import java.util.Iterator;

public class TextChange
{
	public Enum<?> from;
	public Enum<?> to;

	public TextChange(Enum<?> from, Enum<?> to)
	{
		this.from = from;
		this.to = to;
	}

	@SuppressWarnings("unchecked")
	public void applyTo(Collection<?> ret)
	{
		Iterator<?> i = ret.iterator();
		boolean removed = false;
		while(i.hasNext())
		{
			Object t = i.next();
			if(t.equals(this.from))
			{
				i.remove();
				removed = true;
				break;
			}
		}
		if(removed)
			((Collection<Enum<?>>)ret).add(this.to);
	}
}
