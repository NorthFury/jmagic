package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Identity extends SetGenerator
{
	private Set<Object> nonIdentifieds;
	private Collection<Integer> ids;

	private boolean applyTextChanges = true;

	public static Identity instance(Collection<?> what)
	{
		return new Identity(new MagicSet(what));
	}

	public static Identity instance(Object... what)
	{
		return new Identity(new MagicSet(what));
	}

	private Identity(MagicSet what)
	{
		this.nonIdentifieds = new HashSet<Object>();
		this.ids = new LinkedList<Integer>();

		for(Identified i: what.getAll(Identified.class))
		{
			this.ids.add(i.ID);
			what.remove(i);
		}
		this.nonIdentifieds.addAll(what);
	}

	public void addAll(Identity from)
	{
		this.nonIdentifieds.addAll(from.nonIdentifieds);
		this.ids.addAll(from.ids);
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet(this.nonIdentifieds);

		for(int ID: this.ids)
		{
			// it's possible for someone to evaluate an identity on a state
			// prior to when it was created when checking for look-back-in-time
			// trigger conditions
			if(state.containsIdentified(ID))
				ret.add(state.get(ID));
		}

		if(this.applyTextChanges && !ret.isEmpty() && thisObject != null && thisObject.isGameObject())
		{
			GameObject o = (GameObject)thisObject;
			if(o.zoneID == -1 && (o.isActivatedAbility() || o.isTriggeredAbility()))
			{
				Identified source = ((NonStaticAbility)o).getSource(state);
				if(!source.isGameObject())
					return ret;
				o = ((GameObject)source);
			}

			// TODO : We need to be more selective about this. Things like the
			// card Story Circle and the keyword Fear should not be subject to
			// text change effects.
			for(TextChange textChange: o.textChanges)
				textChange.applyTo(ret);
		}

		return ret;
	}

	@Override
	public Set<ManaSymbol.ManaType> extractColors(Game game, GameObject thisObject, Set<SetGenerator> ignoreThese) throws NoSuchMethodException
	{
		Set<ManaSymbol.ManaType> types = new HashSet<ManaSymbol.ManaType>();

		for(Object o: this.nonIdentifieds)
			if(o instanceof Colorful)
				types.addAll(((Colorful)o).getManaTypes());

		return types;
	}

	@Override
	public boolean isGreaterThanZero(Game game, GameObject thisObject) throws NoSuchMethodException
	{
		return Sum.instance(this).evaluate(game, thisObject).getOne(Integer.class) > 0;
	}

	/**
	 * After this method is called, when evaluating this generator, text changes
	 * from <code>thisObject</code> won't be applied to the results.
	 * 
	 * @return <code>this</code>, for ease of use.
	 */
	public Identity noTextChanges()
	{
		this.applyTextChanges = false;
		return this;
	}
}
