package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Set;

public class ChosenFor extends SetGenerator
{
	public static ChosenFor instance(SetGenerator what)
	{
		return new ChosenFor(what);
	}

	private SetGenerator what;

	private ChosenFor(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		for(Linkable link: this.what.evaluate(state, thisObject).getAll(Linkable.class))
		{
			Identified i = (Identified)link;
			if(i.isActivatedAbility() || i.isTriggeredAbility())
				link = (((NonStaticAbility)i).getPrintedVersion(state));
			MagicSet linkInformation = link.getLinkManager().getLinkInformation(state);
			if(linkInformation != null)
				ret.addAll(Identity.instance(linkInformation).evaluate(state, null));
		}
		return ret;
	}

	@Override
	public Set<ManaSymbol.ManaType> extractColors(Game game, GameObject thisObject, Set<SetGenerator> ignoreThese) throws NoSuchMethodException
	{
		return Identity.instance(this.evaluate(game, thisObject)).extractColors(game, thisObject, ignoreThese);
	}
}
