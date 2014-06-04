package org.rnd.jmagic.abilities.keywords;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.engine.*;

import java.util.Collections;
import java.util.List;

public final class LevelUp extends Keyword
{
	private String manaCostString;

	public LevelUp(GameState state, String manaCostString)
	{
		super(state, "Level up " + manaCostString);

		this.manaCostString = manaCostString;
	}

	@Override
	public LevelUp create(Game game)
	{
		return new LevelUp(game.physicalState, this.manaCostString);
	}

	@Override
	public List<NonStaticAbility> createNonStaticAbilities()
	{
		return Collections.<NonStaticAbility>singletonList(new LevelUpAbility(this.state, this.manaCostString));
	}

	public static final class LevelUpAbility extends ActivatedAbility
	{
		private String manaCostString;

		public LevelUpAbility(GameState state, String manaCostString)
		{
			super(state, manaCostString + ": Put a level counter on this permanent. Activate this ability only any time you could cast a sorcery.");

			this.manaCostString = manaCostString;

			this.setManaCost(new ManaPool(manaCostString));

			this.addEffect(putCountersOnThis(1, Counter.CounterType.LEVEL, "this permanent"));

			this.activateOnlyAtSorcerySpeed();
		}

		@Override
		public LevelUpAbility create(Game game)
		{
			return new LevelUpAbility(game.physicalState, this.manaCostString);
		}
	}
}
