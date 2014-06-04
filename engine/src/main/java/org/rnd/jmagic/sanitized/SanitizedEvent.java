package org.rnd.jmagic.sanitized;

import org.rnd.jmagic.engine.Event;
import org.rnd.jmagic.engine.GameObject;
import org.rnd.jmagic.engine.Player;
import org.rnd.jmagic.engine.ZoneChange;

import java.util.Collection;
import java.util.LinkedList;

public class SanitizedEvent extends SanitizedIdentified
{
	private static final long serialVersionUID = 2L;

	public final int sourceID;
	public final String type;

	public SanitizedEvent(Event e)
	{
		super(e);

		GameObject source = e.getSource();
		if(source == null)
			this.sourceID = -1;
		else
			this.sourceID = source.ID;

		this.type = e.type.toString();
	}

	public SanitizedEvent(Event e, String nameOverride)
	{
		super(e, nameOverride);

		GameObject source = e.getSource();
		if(source == null)
			this.sourceID = -1;
		else
			this.sourceID = source.ID;

		this.type = e.type.toString();
	}

	/**
	 * @param state State to pull information from, if needed.
	 */
	public String getDescription(SanitizedGameState state)
	{
		return this.toString();
	}

	public static class Reveal extends SanitizedEvent
	{
		private static final long serialVersionUID = 1L;

		public Collection<SanitizedGameObject> revealed;

		public Reveal(Event e, Collection<GameObject> toReveal, Player whoFor)
		{
			super(e);

			this.revealed = new LinkedList<SanitizedGameObject>();
			for(GameObject o: toReveal)
				this.revealed.add(new SanitizedGameObject(o, whoFor));
		}

		@Override
		public String getDescription(SanitizedGameState state)
		{
			return "Reveal " + this.revealed + ".";
		}
	}

	public static class Move extends SanitizedEvent
	{
		private static final long serialVersionUID = 1L;

		public Collection<SanitizedZoneChange> zoneChanges;

		public Move(Event e, Collection<ZoneChange> zoneChanges, Player whoFor)
		{
			super(e);

			this.zoneChanges = new LinkedList<SanitizedZoneChange>();
			for(ZoneChange zc: zoneChanges)
				this.zoneChanges.add(zc.sanitize(e.state, whoFor));
		}

		@Override
		public String getDescription(SanitizedGameState state)
		{
			StringBuilder ret = new StringBuilder();

			for(SanitizedZoneChange zc: this.zoneChanges)
			{
				String oldObjectName = null;
				String newObjectName = null;
				String destination = state.get(zc.destinationID).name;
				String source = state.get(zc.sourceID).name;

				if(state.containsKey(zc.oldID))
				{
					oldObjectName = state.get(zc.oldID).name;

					if(state.containsKey(zc.newID))
						newObjectName = state.get(zc.newID).name;
					else
						newObjectName = oldObjectName;
				}
				else if(state.containsKey(zc.newID))
					oldObjectName = newObjectName = state.get(zc.newID).name;
				else
					oldObjectName = newObjectName = "An object";

				ret.append(oldObjectName + " moved from " + source + " to " + destination + (oldObjectName.equals(newObjectName) ? "" : " and became " + newObjectName) + ". ");
			}
			return ret.toString();
		}
	}

	public static class Look extends SanitizedEvent
	{
		private static final long serialVersionUID = 1L;

		public Collection<SanitizedGameObject> lookedAt;
		public SanitizedPlayer player;

		public Look(Event e, Player p, Collection<GameObject> toLookAt)
		{
			super(e);

			this.player = new SanitizedPlayer(p);

			this.lookedAt = new LinkedList<SanitizedGameObject>();
			for(GameObject o: toLookAt)
				this.lookedAt.add(new SanitizedGameObject(o, p));
		}

		@Override
		public String getDescription(SanitizedGameState state)
		{
			return this.player + " looked at " + this.lookedAt + ".";
		}
	}
}
