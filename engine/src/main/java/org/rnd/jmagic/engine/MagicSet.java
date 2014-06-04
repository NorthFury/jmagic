package org.rnd.jmagic.engine;

import org.rnd.util.NumberRange;
import org.rnd.util.SeparatedList;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Represents a Magic-specific version of a mathematical set.
 * 
 * A mathematical set is a collection of things where duplicates are not
 * allowed. Java provides an interface at java.util.Set which represents a
 * contract for this functionality.
 * 
 * There are two main differences between this class and the mathematical set.
 * The first is that, while most things (GameObjects, Zones, etc.) may not
 * appear more than once, integers may. So a Set could (for example) contain [2,
 * 2, Battlefield], but not [2, Battlefield, Battlefield].
 * 
 * The other main difference is that only one NumberRange may appear in a Set.
 */
public class MagicSet implements Set<Object>, Serializable
{
	/**
	 * A special java.util.Set is required to hold multiple instances of Integer
	 * without the uniqueness condition kicking in.
	 */
	private static class IntSet implements Set<Integer>
	{
		private List<Integer> wrappers;

		public IntSet()
		{
			this.wrappers = new LinkedList<Integer>();
		}

		@Override
		public boolean add(Integer e)
		{
			return this.wrappers.add(e);
		}

		@Override
		public boolean addAll(Collection<? extends Integer> c)
		{
			return this.wrappers.addAll(c);
		}

		@Override
		public void clear()
		{
			this.wrappers.clear();
		}

		@Override
		public boolean contains(Object o)
		{
			return this.wrappers.contains(o);
		}

		@Override
		public boolean containsAll(Collection<?> c)
		{
			return this.wrappers.containsAll(c);
		}

		@Override
		public boolean isEmpty()
		{
			return this.wrappers.isEmpty();
		}

		@Override
		public Iterator<Integer> iterator()
		{
			return this.wrappers.iterator();
		}

		@Override
		public boolean remove(Object o)
		{
			return this.wrappers.remove(o);
		}

		@Override
		public boolean removeAll(Collection<?> c)
		{
			return this.wrappers.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c)
		{
			return this.wrappers.retainAll(c);
		}

		@Override
		public int size()
		{
			return this.wrappers.size();
		}

		@Override
		public Object[] toArray()
		{
			return this.wrappers.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a)
		{
			return this.wrappers.toArray(a);
		}
	}

	private static class IntWrapper implements Serializable
	{
		private static final long serialVersionUID = 1L;

		public final int value;

		public IntWrapper(int value)
		{
			this.value = value;
		}

		@Override
		public String toString()
		{
			return Integer.toString(this.value);
		}
	}

	/**
	 * Unmodifiable is a special Set that can't be modified, but otherwise acts
	 * like a Set.
	 */
	public static class Unmodifiable extends MagicSet
	{
		private static final long serialVersionUID = 1L;

		public Unmodifiable()
		{
			this.store = Collections.emptySet();
		}

		public Unmodifiable(int i)
		{
			this.store = Collections.<Object>singleton(new IntWrapper(i));
		}

		public Unmodifiable(Object o)
		{
			this.store = Collections.singleton(o);
		}

		public Unmodifiable(MagicSet s)
		{
			this.store = Collections.unmodifiableSet(s.store);
		}
	}

	protected Set<Object> store;

	private static final long serialVersionUID = 1L;

	/** Constructs an empty Set. */
	public MagicSet()
	{
		this.store = new HashSet<Object>();
	}

	/**
	 * Constructs a Set containing all the elements in a given collection.
	 * 
	 * @param c The collection.
	 */
	public MagicSet(Collection<?> c)
	{
		this();
		if(null == c)
			throw new UnsupportedOperationException("Attempt to initialize a Set with a null collection");

		boolean hasNumberRange = false;
		for(Object e: c)
			if(null != e)
				if(e instanceof Integer)
					this.store.add(new IntWrapper((Integer)e));
				else
				{
					if(e instanceof NumberRange)
					{
						if(hasNumberRange)
							throw new UnsupportedOperationException("Attempt to initialize a Set with a collection containing two NumberRange instances");
						hasNumberRange = true;
					}
					this.store.add(e);
				}
	}

	/**
	 * Constructs a Set containing all the elements in a given array.
	 * 
	 * @param c The array.
	 */
	public MagicSet(Object... c)
	{
		this();
		if(null == c)
			throw new UnsupportedOperationException("Attempt to initialize a Set with a null collection");

		boolean hasNumberRange = false;
		for(Object e: c)
		{
			if(null == e)
				throw new UnsupportedOperationException("Attempt to initialize a Set with a null element");
			if(e instanceof Integer)
				this.store.add(new IntWrapper((Integer)e));
			else
			{
				if(e instanceof NumberRange)
				{
					if(hasNumberRange)
						throw new UnsupportedOperationException("Attempt to initialize a Set with a collection containing two NumberRange instances");
					hasNumberRange = true;
				}
				this.store.add(e);
			}
		}
	}

	/**
	 * Adds an element to this Set.
	 * 
	 * @param e The element
	 */
	@Override
	public boolean add(Object e)
	{
		if(null == e)
			throw new UnsupportedOperationException("Attempt to add null to a Set");

		if(e instanceof Integer)
			return this.store.add(new IntWrapper((Integer)e));

		if(e instanceof NumberRange)
		{
			boolean hasNumberRange = false;
			for(Object o: this)
			{
				if(o instanceof NumberRange)
				{
					hasNumberRange = true;
					break;
				}
			}
			if(hasNumberRange)
				throw new UnsupportedOperationException("Attempt to add a second NumberRange to a Set");
		}
		return this.store.add(e);
	}

	/**
	 * Adds all the elements of a collection to this set.
	 * 
	 * @param c The collection.
	 * @return True if this Set changed as a result of this call; false
	 * otherwise.
	 */
	@Override
	public boolean addAll(Collection<?> c)
	{
		if(null == c)
			throw new UnsupportedOperationException("Attempt to add to a Set with a null collection");

		boolean hasNumberRange = false;
		for(Object o: this)
		{
			if(o instanceof NumberRange)
			{
				hasNumberRange = true;
				break;
			}
		}

		boolean changed = false;
		for(Object e: c)
		{
			if(null == e)
				throw new UnsupportedOperationException("Attempt to add null to a Set");

			if(e instanceof NumberRange)
			{
				if(hasNumberRange)
					throw new UnsupportedOperationException("Attempt to add a second NumberRange to a Set");
				hasNumberRange = true;
			}
			boolean isInt = e instanceof Integer;
			if((isInt && this.store.add(new IntWrapper((Integer)e))) || (!isInt && this.store.add(e)))
				changed = true;
		}
		return changed;
	}

	/** Removes all elements from this Set. */
	@Override
	public void clear()
	{
		this.store.clear();
	}

	/** @return True if this set contains the specified element; false otherwise. */
	@Override
	public boolean contains(Object arg0)
	{
		return this.store.contains(arg0);
	}

	/**
	 * @return True if this set contains all elements of the specified
	 * collection; false otherwise.
	 */
	@Override
	public boolean containsAll(Collection<?> arg0)
	{
		return this.store.containsAll(arg0);
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(!(obj instanceof MagicSet))
			return false;
		MagicSet other = (MagicSet)obj;
		if(this.store == null)
		{
			if(other.store != null)
				return false;
		}
		else if(!this.store.equals(other.store))
			return false;
		return true;
	}

	/**
	 * Gets all elements of a specific type from this Set.
	 * 
	 * @param <T> The type.
	 * @param c The type.
	 * @return The elements.
	 */
	@SuppressWarnings("unchecked")
	public <T> Set<T> getAll(Class<T> c)
	{
		if(c.equals(Integer.class))
		{
			IntSet ret = new IntSet();
			for(Object o: this)
				if(o instanceof IntWrapper)
					ret.add(((IntWrapper)o).value);
			return (Set<T>)ret;
		}
		Set<T> ret = new HashSet<T>();
		for(Object o: this)
		{
			if(c.isAssignableFrom(o.getClass()))
				ret.add((T)o);
		}
		return ret;
	}

	/**
	 * Gets all classes from this Set which are assignable to a specific class.
	 * 
	 * @param c The specific class to check against.
	 * @return The elements.
	 */
	@SuppressWarnings("unchecked")
	public <T> Set<Class<? extends T>> getAllClasses(Class<T> c)
	{
		Set<Class<? extends T>> ret = new HashSet<Class<? extends T>>();
		for(Class<?> in: this.getAll(Class.class))
			if(c.isAssignableFrom(in))
				ret.add((Class<T>)in);
		return ret;
	}

	/**
	 * Gets a single element out of this Set.
	 * 
	 * @param <T> The kind of item to get.
	 * @param c The kind of item to get.
	 * @return An arbitrary element of type <T> from this Set if it contains
	 * one; null otherwise.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getOne(Class<T> c)
	{
		if(c.equals(Integer.class))
		{
			for(Object o: this)
				if(o instanceof IntWrapper)
					return (T)(Integer)(((IntWrapper)o).value);
		}
		else
		{
			for(Object o: this)
				if(c.isAssignableFrom(o.getClass()))
					return (T)o;
		}
		return null;
	}

	@Override
	public int hashCode()
	{
		return this.store.hashCode();
	}

	/**
	 * @return True if this Set contains no elements; false if it contains at
	 * least one.
	 */
	@Override
	public boolean isEmpty()
	{
		return this.store.isEmpty();
	}

	/** @return An iterator over the elements of this Set. */
	@Override
	public Iterator<Object> iterator()
	{
		return this.store.iterator();
	}

	/**
	 * Removes the specified element from this Set (assuming this set contains
	 * the specified element).
	 * 
	 * @param arg0 The element to remove.
	 * @return Whether this Set changed as a result of this call.
	 */
	@Override
	public boolean remove(Object arg0)
	{
		return this.store.remove(arg0);
	}

	/**
	 * Removes all elements in the specified collection from this set.
	 * 
	 * @param arg0 A collection containing the elements to remove.
	 * @return Whether this Set changed as a result of this call.
	 */
	@Override
	public boolean removeAll(Collection<?> arg0)
	{
		return this.store.removeAll(arg0);
	}

	/**
	 * Removes all elements in this Set that are not in the specified
	 * collection.
	 * 
	 * @param arg0 The elements to retain in this Set.
	 * @return Whether this Set changed as a result of this call.
	 */
	@Override
	public boolean retainAll(Collection<?> arg0)
	{
		return this.store.retainAll(arg0);
	}

	/** @return The number of elements in this Set. */
	@Override
	public int size()
	{
		return this.store.size();
	}

	/**
	 * @return An array containing all of the elements in this Set. No
	 * references to the returned array are maintained by this Set.
	 */
	@Override
	public Object[] toArray()
	{
		return this.store.toArray();
	}

	/**
	 * Returns an array containing all of the elements in this Set; the runtime
	 * type of the returned array is that of the specified array. If the set
	 * fits in the specified array, it is returned therein. Otherwise, a new
	 * array is allocated with the runtime type of the specified array and the
	 * size of this set.
	 * 
	 * If this Set fits in the specified array with room to spare, the element
	 * in the array immediately following the end of the Set is set to
	 * <code>null</code>.
	 * 
	 * @param arg0 The array into which the elements of this Set are to be
	 * stored, if it is big enough; otherwise, a new array of the same runtime
	 * type is allocated for this purpose.
	 * @return An array containing all the elements in this Set.
	 */
	@Override
	public <T> T[] toArray(T[] arg0)
	{
		return this.store.toArray(arg0);
	}

	/** @return A string representation of this Set. */
	@Override
	public String toString()
	{
		return SeparatedList.get("and", this).toString();
	}
}
