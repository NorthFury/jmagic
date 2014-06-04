package org.rnd.jmagic.abilities.keywords;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.EntersTheBattlefieldWithCounters;
import org.rnd.jmagic.engine.*;

import java.util.Collections;
import java.util.List;

public final class Fading extends Keyword
{
	private final int N;

	public Fading(GameState state)
	{
		this(state, 0);
	}

	public Fading(GameState state, int N)
	{
		super(state, "Fading " + N);
		this.N = N;
	}

	@Override
	public Fading create(Game game)
	{
		return new Fading(game.physicalState, this.N);
	}

	@Override
	protected List<StaticAbility> createStaticAbilities()
	{
		if(this.N == 0)
			return super.createStaticAbilities();
		return Collections.<StaticAbility>singletonList(new EntersTheBattlefieldWithCounters(this.state, "This permanent", this.N, Counter.CounterType.FADE));
	}

	public static final class RemoveCounter extends EventTriggeredAbility
	{
		public RemoveCounter(GameState state)
		{
			super(state, "At the beginning of your upkeep, remove a fade counter from this permanent. If you can't, sacrifice the permanent.");

			this.addPattern(atTheBeginningOfYourUpkeep());

			EventFactory removeCounter = removeCountersFromThis(1, Counter.CounterType.FADE, "this permanent");
			EventFactory sacrifice = sacrificeThis("this permanent");

			this.addEffect(ifThenElse(removeCounter, null, sacrifice, "Remove a fade counter from this permanent. If you can't, sacrifice the permanent."));
		}
	}

	@Override
	protected List<NonStaticAbility> createNonStaticAbilities()
	{
		return Collections.<NonStaticAbility>singletonList(new RemoveCounter(this.state));
	}
}
