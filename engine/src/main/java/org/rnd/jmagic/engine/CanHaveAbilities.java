package org.rnd.jmagic.engine;

import java.util.Collection;

public interface CanHaveAbilities
{
	public Collection<NonStaticAbility> getNonStaticAbilities();

	public Collection<Keyword> getKeywordAbilities();

	public Collection<StaticAbility> getStaticAbilities();
}
