package org.rnd.jmagic.abilities.keywords;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.jmagic.engine.patterns.*;

import java.util.LinkedList;
import java.util.List;

@Name("Hexproof")
public final class Hexproof extends Keyword
{
	public Hexproof(GameState state)
	{
		super(state, "Hexproof");
	}

	@Override
	protected List<StaticAbility> createStaticAbilities()
	{
		List<StaticAbility> ret = new LinkedList<StaticAbility>();
		ret.add(new HexproofAbility(this.state));
		return ret;
	}

	public static final class HexproofAbility extends StaticAbility
	{
		public HexproofAbility(GameState state)
		{
			super(state, "This permanent or player can't be the target of spells or abilities your opponents control.");

			SetGenerator thisIsAPlayer = Intersect.instance(This.instance(), Players.instance());
			SetGenerator you = IfThenElse.instance(thisIsAPlayer, This.instance(), You.instance());
			SetGenerator stuffOpponentsControl = ControlledBy.instance(OpponentsOf.instance(you), Stack.instance());
			SetGenerator minusExemptions = RelativeComplement.instance(stuffOpponentsControl, ExemptionsFor.instance(This.instance(), Hexproof.class));

			ContinuousEffect.Part part = new ContinuousEffect.Part(ContinuousEffectType.CANT_BE_THE_TARGET_OF);
			part.parameters.put(ContinuousEffectType.Parameter.OBJECT, This.instance());
			part.parameters.put(ContinuousEffectType.Parameter.RESTRICTION, Identity.instance(new SimpleSetPattern(minusExemptions)));
			this.addEffectPart(part);
		}
	}
}
