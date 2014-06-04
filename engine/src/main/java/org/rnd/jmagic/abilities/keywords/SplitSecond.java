package org.rnd.jmagic.abilities.keywords;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.jmagic.engine.patterns.*;

import java.util.LinkedList;
import java.util.List;

@Name("Split second")
public final class SplitSecond extends Keyword
{
	public SplitSecond(GameState state)
	{
		super(state, "Split second");
	}

	@Override
	protected List<StaticAbility> createStaticAbilities()
	{
		List<StaticAbility> ret = new LinkedList<StaticAbility>();
		ret.add(new SplitSecondAbility(this.state));
		return ret;
	}

	public static final class SplitSecondAbility extends StaticAbility
	{
		public SplitSecondAbility(GameState state)
		{
			super(state, "As long as this spell is on the stack, players can't cast other spells or activate abilities that aren't mana abilities.");
			this.canApply = THIS_IS_ON_THE_STACK;

			MultipleSetPattern cantPlay = new MultipleSetPattern(false);
			cantPlay.addPattern(SetPattern.CASTABLE);
			cantPlay.addPattern(SetPattern.NON_MANA_ACTIVATED_ABILITIES);

			SimpleEventPattern castSpellsOrActivateNonManaAbilities = new SimpleEventPattern(EventType.CAST_SPELL_OR_ACTIVATE_ABILITY);
			castSpellsOrActivateNonManaAbilities.put(EventType.Parameter.OBJECT, cantPlay);

			ContinuousEffect.Part part = new ContinuousEffect.Part(ContinuousEffectType.PROHIBIT);
			part.parameters.put(ContinuousEffectType.Parameter.PROHIBITION, Identity.instance(castSpellsOrActivateNonManaAbilities));
			this.addEffectPart(part);
		}
	}
}
