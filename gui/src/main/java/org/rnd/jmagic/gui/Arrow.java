package org.rnd.jmagic.gui;

import java.awt.Color;

public class Arrow
{
	public enum ArrowType
	{
		ATTACHMENT(Color.BLUE, "Attachments"), //
		ATTACKING(Color.ORANGE, "Attacking"), //
		BLOCKING(Color.GREEN, "Blocking"), //
		CAUSE(Color.MAGENTA, "Cause", true), //
		CONTROLLER(Color.DARK_GRAY, "Controller", true), //
		LINK(Color.MAGENTA, "Linked Objects"), //
		PAIR(Color.MAGENTA, "Paired Creatures"), //
		SOURCE(new Color(0, 51, 102), "Source of Ability", true), //
		TARGET(Color.RED, "Targetting"), //
		;

		private final Color defaultColor;
		public final String description;
		private final boolean hollow;

		ArrowType(Color color, String description)
		{
			this(color, description, false);
		}

		ArrowType(Color defaultColor, String description, boolean hollow)
		{
			this.defaultColor = defaultColor;
			this.description = description;
			this.hollow = hollow;
		}

		public boolean isHollow()
		{
			return this.hollow;
		}

		public Color getDefaultColor()
		{
			return this.defaultColor;
		}
	}

	public final int sourceID;
	public final int targetID;
	public final ArrowType type;

	public Arrow(int source, int target, ArrowType type)
	{
		this.sourceID = source;
		this.targetID = target;
		this.type = type;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + this.sourceID;
		result = prime * result + this.targetID;
		result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		Arrow other = (Arrow)obj;
		if(this.sourceID != other.sourceID)
			return false;
		if(this.targetID != other.targetID)
			return false;
		if(this.type == null)
		{
			if(other.type != null)
				return false;
		}
		else if(!this.type.equals(other.type))
			return false;
		return true;
	}
}
