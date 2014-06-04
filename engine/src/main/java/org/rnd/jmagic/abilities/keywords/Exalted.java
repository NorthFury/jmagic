package org.rnd.jmagic.abilities.keywords;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilityTemplates.ExaltedBase;
import org.rnd.jmagic.engine.*;

import java.util.LinkedList;
import java.util.List;

@Name("Exalted")
public final class Exalted extends Keyword
{
	public Exalted(GameState state)
	{
		super(state, "Exalted");
	}

	@Override
	protected List<NonStaticAbility> createNonStaticAbilities()
	{
		List<NonStaticAbility> ret = new LinkedList<NonStaticAbility>();
		ret.add(new ExaltedTrigger(this.state));
		return ret;
	}

	public static final class ExaltedTrigger extends ExaltedBase
	{
		public ExaltedTrigger(GameState state)
		{
			super(state, "Whenever a creature you control attacks alone, that creature gets +1/+1 until end of turn.");

			this.addEffect(ptChangeUntilEndOfTurn(this.thatCreature, +1, +1, "That creature gets +1/+1 until end of turn."));
		}
	}
}
