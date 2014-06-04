package org.rnd.jmagic.sanitized;

import org.rnd.jmagic.engine.StaticAbility;

public class SanitizedStaticAbility extends SanitizedIdentified
{
	private static final long serialVersionUID = 1L;

	public final int sourceID;

	public SanitizedStaticAbility(StaticAbility a)
	{
		super(a);

		this.sourceID = a.sourceID;
	}
}
