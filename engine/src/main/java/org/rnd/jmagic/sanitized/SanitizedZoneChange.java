package org.rnd.jmagic.sanitized;

import org.rnd.jmagic.engine.GameState;
import org.rnd.jmagic.engine.Player;
import org.rnd.jmagic.engine.ReplacementEffect;
import org.rnd.jmagic.engine.ZoneChange;

import java.io.Serializable;

public class SanitizedZoneChange implements Serializable
{
	private static final long serialVersionUID = 1L;

	public int controllerID;
	public int destinationID;
	public int sourceID;
	public int index;
	public int newID;
	public int oldID;

	public SanitizedZoneChange(ZoneChange zc)
	{
		this.controllerID = zc.controllerID;
		this.destinationID = zc.destinationZoneID;
		this.sourceID = zc.sourceZoneID;
		this.index = zc.index;
		this.newID = zc.newObjectID;
		this.oldID = zc.oldObjectID;
	}

	public static class Replacement implements SanitizedReplacement
	{
		private static final long serialVersionUID = 1L;

		public SanitizedReplacementEffect effect;
		public SanitizedZoneChange zoneChange;

		private String text;

		public Replacement(ReplacementEffect effect, ZoneChange zoneChange, GameState s, Player whoFor)
		{
			this.effect = effect.sanitize(s, whoFor);
			this.zoneChange = zoneChange.sanitize(s, whoFor);

			this.text = "Apply effect \"" + effect.name + "\" to " + s.get(zoneChange.oldObjectID) + " moving from " + s.get(zoneChange.sourceZoneID) + " to " + s.get(zoneChange.destinationZoneID);
		}

		@Override
		public boolean isOptionalForMe()
		{
			return this.effect.isOptionalForMe();
		}

		@Override
		public String toString()
		{
			return this.text;
		}
	}
}
