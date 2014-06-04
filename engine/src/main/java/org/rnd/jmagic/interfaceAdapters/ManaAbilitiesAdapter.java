package org.rnd.jmagic.interfaceAdapters;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.sanitized.*;
import org.rnd.util.NumberRange;

import java.io.Serializable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * This class intercepts choices for activating a single mana ability and
 * replaces it by allowing the player to activate multiple mana abilities, and
 * returning them to the engine one at a time.
 */
public class ManaAbilitiesAdapter extends SimplePlayerInterface
{
	private Queue<SanitizedPlayerAction> choices;
	private boolean done;

	private static Set<PlayerInterface.ChoiceType> ignores;
	static
	{
		ignores = EnumSet.of( //
		PlayerInterface.ChoiceType.COLOR, // e.g. black lotus
		PlayerInterface.ChoiceType.COSTS, // mana ability with multiple costs
		PlayerInterface.ChoiceType.MANA_EXPLOSION, // e.g. filterlands
		PlayerInterface.ChoiceType.MANA_PAYMENT, // e.g. filterlands
		PlayerInterface.ChoiceType.MOVEMENT_GRAVEYARD // e.g. black lotus
		);
	}

	public ManaAbilitiesAdapter(PlayerInterface adapt)
	{
		super(adapt);
		this.choices = new LinkedList<SanitizedPlayerAction>();
		this.done = false;
	}

	@Override
	public <T extends Serializable> List<Integer> choose(ChooseParameters<T> parameterObject)
	{
		if(parameterObject.type == PlayerInterface.ChoiceType.ACTIVATE_MANA_ABILITIES)
		{
			if(this.done || parameterObject.choices.isEmpty())
				return Collections.emptyList();

			int index = -1;
			while(index == -1)
			{
				if(this.choices.isEmpty())
				{
					ChooseParameters<T> newParameters = new ChooseParameters<T>(parameterObject);
					newParameters.number = new MagicSet(new NumberRange(0, null));

					List<Integer> chosen = super.choose(newParameters);
					if(chosen.isEmpty())
						return chosen;
					for(Integer i: chosen)
						this.choices.add((SanitizedPlayerAction)newParameters.choices.get(i));
				}

				SanitizedPlayerAction choice = this.choices.remove();
				if(this.choices.isEmpty())
					this.done = true;

				// If the choice can't be found the loop will repeat.
				index = parameterObject.choices.indexOf(choice);
			}

			return Collections.singletonList(index);
		}

		if(!ignores.contains(parameterObject.type))
			this.done = false;
		return super.choose(parameterObject);
	}
}
