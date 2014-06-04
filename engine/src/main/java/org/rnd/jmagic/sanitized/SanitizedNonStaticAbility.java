package org.rnd.jmagic.sanitized;

import org.rnd.jmagic.engine.*;

public class SanitizedNonStaticAbility extends SanitizedGameObject
{
	private static final long serialVersionUID = 2L;

	public final int sourceID;
	public final String shortName;
	public final int causeID;

	public SanitizedNonStaticAbility(NonStaticAbility a, Player whoFor)
	{
		super(a, whoFor);

		this.sourceID = a.sourceID;
		if(a.sourceID == -1)
		{
			if(a.isActivatedAbility())
				this.shortName = "Activated ability";
			else
				this.shortName = "Triggered ability";
			this.causeID = -1;
			return;
		}

		String sourceName = this.sourceName(a);

		if(a.isActivatedAbility())
		{
			this.shortName = "Activated ability from " + sourceName;
			this.causeID = -1;
		}
		else
		{
			if(a instanceof EventTriggeredAbility)
			{
				EventTriggeredAbility trigger = (EventTriggeredAbility)a;
				if(trigger.zoneChangeCause == null)
					this.causeID = -1;
				else
					this.causeID = trigger.zoneChangeCause.newObjectID;
			}
			else
				this.causeID = -1;
			this.shortName = "Triggered ability from " + sourceName;
		}
	}

	private String sourceName(NonStaticAbility a)
	{
		Identified source = a.getSource(a.game.actualState);
		if(source.isActivatedAbility() || source.isTriggeredAbility())
			return this.sourceName((NonStaticAbility)source);
		return source.getName();
	}
}
