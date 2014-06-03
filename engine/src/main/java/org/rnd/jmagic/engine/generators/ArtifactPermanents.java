package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class ArtifactPermanents extends SetGenerator
{
	private static final SetGenerator _instance = Intersect.instance(Permanents.instance(), HasType.instance(Type.ARTIFACT));

	public static SetGenerator instance()
	{
		return _instance;
	}

	private ArtifactPermanents()
	{
		// Private Constructor
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();

		for(GameObject object: state.battlefield().objects)
			if(object.getTypes().contains(Type.ARTIFACT))
				ret.add(object);

		return ret;
	}
}
