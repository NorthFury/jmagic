package org.rnd.jmagic.engine;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Pile implements Set<GameObject>, Sanitizable
{
	private Set<GameObject> delegate;

	public Pile()
	{
		this.delegate = new HashSet<GameObject>();
	}

	@Override
	public boolean add(GameObject e)
	{
		return this.delegate.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends GameObject> c)
	{
		return this.delegate.addAll(c);
	}

	@Override
	public void clear()
	{
		this.delegate.clear();
	}

	@Override
	public boolean contains(Object o)
	{
		return this.delegate.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		return this.delegate.containsAll(c);
	}

	@Override
	public boolean isEmpty()
	{
		return this.delegate.isEmpty();
	}

	@Override
	public Iterator<GameObject> iterator()
	{
		return this.delegate.iterator();
	}

	@Override
	public boolean remove(Object o)
	{
		return this.delegate.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		return this.delegate.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		return this.delegate.retainAll(c);
	}

	@Override
	public Serializable sanitize(GameState state, Player whoFor)
	{
		HashSet<Serializable> ret = new HashSet<Serializable>();
		for(GameObject o: this.delegate)
			ret.add(o.sanitize(state, whoFor));
		return ret;
	}

	@Override
	public int size()
	{
		return this.delegate.size();
	}

	@Override
	public Object[] toArray()
	{
		return this.delegate.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		return this.delegate.toArray(a);
	}
}
