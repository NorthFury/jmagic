package org.rnd.jmagic.engine;

import org.rnd.jmagic.engine.generators.Identity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface Linkable
{
	public Manager getLinkManager();

	public Linkable getPhysical();

	public static class Manager implements Cloneable
	{
		/** Fields to store the linked ability */
		private MagicSet linkInformation;
		private Map<Class<? extends Linkable>, Integer> links;

		public Manager()
		{
			this.linkInformation = null;
			this.links = new HashMap<Class<? extends Linkable>, Integer>();
		}

		public void addLinkInformation(Object o)
		{
			if(this.linkInformation == null)
				this.linkInformation = new MagicSet();
			this.linkInformation.add(o);
		}

		@Override
		public Manager clone()
		{
			try
			{
				Manager ret = (Manager)super.clone();

				if(this.linkInformation != null)
					ret.linkInformation = new MagicSet(this.linkInformation);
				ret.links = new HashMap<Class<? extends Linkable>, Integer>(this.links);

				return ret;
			}
			catch(CloneNotSupportedException e)
			{
				throw new RuntimeException(e);
			}
		}

		public Linkable getLink(GameState state, Class<? extends Linkable> linkClass)
		{
			Integer id = this.links.get(linkClass);
			if(id == null || id.intValue() == -1)
				return null;
			Identified o = state.get(id.intValue());
			if(o instanceof Linkable)
				return (Linkable)o;
			return null;
		}

		public Set<Class<? extends Linkable>> getLinkClasses()
		{
			return Collections.unmodifiableSet(this.links.keySet());
		}

		/**
		 * Get any linked information.
		 * 
		 * @param state The state to check for ghostliness in.
		 * @return Any information stored between this and the other half of the
		 * link. ZoneChange are replaced with the new object in the change and
		 * GameObject which are ghosts aren't returned.
		 */
		public MagicSet getLinkInformation(GameState state)
		{
			if(this.linkInformation == null)
				return null;
			MagicSet ret = Identity.instance(this.linkInformation).evaluate(state, null);
			for(GameObject go: ret.getAll(GameObject.class))
				if(go.isGhost())
					ret.remove(go);
			for(ZoneChange change: ret.getAll(ZoneChange.class))
			{
				GameObject object = state.<GameObject>get(change.newObjectID);
				if(!object.isGhost())
					ret.add(object);
				ret.remove(change);
			}
			return ret;
		}

		public void setLink(Linkable link)
		{
			Class<? extends Linkable> linkClass = link.getClass();
			if(this.links.containsKey(linkClass))
			{
				this.links.put(linkClass, ((Identified)link).ID);
				this.linkInformation = new MagicSet();
			}
			else
				throw new UnsupportedOperationException("Attempting to link an instance of an ability of an unregistered class");
		}

		public void addLinkClass(Class<? extends Linkable> linkClass)
		{
			if(this.links.containsKey(linkClass))
				throw new UnsupportedOperationException("Adding the same link class twice to an object.");
			this.links.put(linkClass, null);
		}
	}
}
