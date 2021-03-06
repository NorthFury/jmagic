package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public final class Attach extends EventType
{	public static final EventType INSTANCE = new Attach();

	 private Attach()
	{
		super("ATTACH");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		AttachableTo targetObject = parameters.get(Parameter.TARGET).getOne(AttachableTo.class);

		if(targetObject == null)
			return false;

		for(GameObject object: parameters.get(Parameter.OBJECT).getAll(GameObject.class))
			if(!object.canAttachTo(game, targetObject))
				return false;

		return true;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		boolean attachedAll = true;
		Set<GameObject> attachments = parameters.get(Parameter.OBJECT).getAll(GameObject.class);
		AttachableTo target = parameters.get(Parameter.TARGET).getOne(AttachableTo.class);
		if(target == null || (target.isGameObject() && ((GameObject)target).isGhost()))
		{
			event.setResult(Empty.set);
			return true;
		}
		int attachmentID = ((Identified)target).ID;

		// a collection of things that are already attached to the specified
		// object or player
		Collection<Integer> reAttachments = new LinkedList<Integer>();
		MagicSet detachables = new MagicSet();
		for(GameObject o: attachments)
			if(o.getAttachedTo() != -1)
			{
				if(o.getAttachedTo() == attachmentID)
					reAttachments.add(o.ID);
				else
					detachables.add(o);
			}

		Iterator<GameObject> iter = attachments.iterator();
		while(iter.hasNext())
		{
			GameObject o = iter.next();
			if(!o.canAttachTo(game, target))
			{
				detachables.remove(o);
				iter.remove();
				attachedAll = false;
			}
		}

		if(!detachables.isEmpty())
		{
			Map<Parameter, MagicSet> detachParameters = new HashMap<Parameter, MagicSet>();
			detachParameters.put(Parameter.OBJECT, detachables);
			createEvent(game, "Unattach before attaching", EventType.UNATTACH, detachParameters).perform(event, false);
		}

		for(GameObject o: attachments)
		{
			target.getPhysical().addAttachment(o.ID);
			o.getPhysical().setAttachedTo(attachmentID);

			// 613.6d If an Aura, Equipment, or Fortification becomes
			// attached to an object or player, the Aura, Equipment, or
			// Fortification receives a new timestamp at that time.
			if(!reAttachments.contains(o.ID))
				event.addToNeedsNewTimestamps(o);
		}
		event.setResult(Identity.instance(target));
		return attachedAll;
	}
}