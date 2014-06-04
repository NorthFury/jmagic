package org.rnd.jmagic.interfaceAdapters;

import org.rnd.jmagic.engine.Answer;
import org.rnd.jmagic.engine.EventType;
import org.rnd.jmagic.engine.MagicSet;
import org.rnd.jmagic.engine.PlayerInterface;
import org.rnd.jmagic.engine.generators.Maximum;
import org.rnd.jmagic.engine.generators.Minimum;
import org.rnd.jmagic.sanitized.SanitizedEvent;
import org.rnd.jmagic.sanitized.SanitizedReplacement;
import org.rnd.jmagic.sanitized.SanitizedTarget;
import org.rnd.util.NumberRange;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * An adapter interface to "filter out" questions that either have only one
 * answer (dividing a quantity over one target) or are widely seen as completely
 * irrelevant (ordering the timestamps of multiple permanents simultaneously
 * changing zones).
 */
public class ShortcutInterface extends SimplePlayerInterface
{
	enum ReplacementDeclination
	{
		NONE, LOTS, TWO, ONE;
	}

	private ReplacementDeclination replacementDeclination;

	public ShortcutInterface(PlayerInterface adapt)
	{
		super(adapt);
		this.replacementDeclination = ReplacementDeclination.NONE;
	}

	// We use Strings here in situations where the client wants to reference an
	// event type define on the server, but not in the client's code.
	private static final Set<String> FINAL_EVENT_TYPES;
	static
	{
		FINAL_EVENT_TYPES = new HashSet<String>();
		FINAL_EVENT_TYPES.add(EventType.PUT_INTO_HAND.toString());
		FINAL_EVENT_TYPES.add(EventType.PUT_INTO_HAND_CHOICE.toString());
		FINAL_EVENT_TYPES.add(EventType.DESTROY_PERMANENTS.toString());
		FINAL_EVENT_TYPES.add(EventType.DISCARD_CARDS.toString());
		FINAL_EVENT_TYPES.add(EventType.DISCARD_CHOICE.toString());
		FINAL_EVENT_TYPES.add(EventType.DISCARD_RANDOM.toString());
		FINAL_EVENT_TYPES.add(EventType.DRAW_CARDS.toString());
		FINAL_EVENT_TYPES.add(EventType.EXILE_CHOICE.toString());
		FINAL_EVENT_TYPES.add(EventType.MILL_CARDS.toString());
		FINAL_EVENT_TYPES.add(EventType.MOVE_CHOICE.toString());
		FINAL_EVENT_TYPES.add(EventType.MOVE_OBJECTS.toString());
		FINAL_EVENT_TYPES.add(EventType.SACRIFICE_CHOICE.toString());
		FINAL_EVENT_TYPES.add(EventType.SACRIFICE_PERMANENTS.toString());
	}

	@Override
	public void alertChoice(int playerID, ChooseParameters<?> choice)
	{
		if(!choice.reason.equals(ChooseReason.SPLICE_OBJECTS))
			super.alertChoice(playerID, choice);
	}

	@Override
	public <T extends Serializable> List<Integer> choose(ChooseParameters<T> parameterObject)
	{
		switch(parameterObject.type)
		{
		case ACTIVATE_MANA_ABILITIES:
		case NORMAL_ACTIONS:
			// Things we always pass through
			return super.choose(parameterObject);

		case MOVEMENT_GRAVEYARD:
		case TIMESTAMPS:
		{
			// Things we just pick for them
			List<Integer> ret = new LinkedList<Integer>();
			for(int i = 0; i < parameterObject.choices.size(); ++i)
				ret.add(i);
			return ret;
		}

		// ... Everything else
		case COSTS:
		{
			Collection<Integer> sacrifices = new LinkedList<Integer>();
			{
				int i = 0;
				for(T choice: parameterObject.choices)
				{
					SanitizedEvent event = (SanitizedEvent)choice;
					if(FINAL_EVENT_TYPES.contains(event.type))
						sacrifices.add(i);
					i++;
				}
			}

			// Auto-order the costs with sacrifices and exiles last.
			List<Integer> ret = new LinkedList<Integer>();
			for(int i = 0; i < parameterObject.choices.size(); i++)
				if(!sacrifices.contains(i))
					ret.add(i);
			ret.addAll(sacrifices);
			return ret;
		}

		case REPLACEMENT_EFFECT:
		{
			if(this.replacementDeclination == ReplacementDeclination.NONE)
			{
				for(T choice: parameterObject.choices)
				{
					if(!((SanitizedReplacement)choice).isOptionalForMe())
					{
						this.replacementDeclination = ReplacementDeclination.NONE;
						return super.choose(parameterObject);
					}
				}

				PlayerInterface.ChooseParameters<T> newParams = new ChooseParameters<T>(parameterObject);
				newParams.number = new MagicSet(new NumberRange(0, 1));
				List<Integer> chosen = super.choose(newParams);

				if(!chosen.isEmpty())
					return chosen;
				this.replacementDeclination = ReplacementDeclination.LOTS;
			}

			if(parameterObject.choices.size() == 2)
				this.replacementDeclination = ReplacementDeclination.TWO;
			return Collections.singletonList(0);
		}

		case ENUM:
		{
			switch(this.replacementDeclination)
			{
			case NONE:
				return super.choose(parameterObject);
			case TWO:
				this.replacementDeclination = ReplacementDeclination.ONE;
				break;
			case ONE:
				this.replacementDeclination = ReplacementDeclination.NONE;
				break;
			case LOTS:
				// don't care
			}
			return Collections.singletonList(parameterObject.choices.indexOf(Answer.NO));
		}

		default:
		}

		// TODO: this logic needs to be in here somewhere
		// if(!parameterObject.reason.isPublic &&
		// !parameterObject.type.isOrdered())
		// return super.choose(parameterObject);

		Integer lowerBound = Minimum.get(parameterObject.number);
		Integer upperBound = Maximum.get(parameterObject.number);

		List<Integer> ret = new LinkedList<Integer>();
		if(null != upperBound && 0 == upperBound && parameterObject.reason.isPublic)
			return ret;
		if((parameterObject.choices.size() == lowerBound) && ((1 == upperBound && 1 == lowerBound) || !parameterObject.type.isOrdered()))
		{
			for(int i = 0; i < parameterObject.choices.size(); ++i)
			{
				if(ret.size() == lowerBound)
					break;
				ret.add(i);
			}
			return ret;
		}

		return super.choose(parameterObject);
	}

	@Override
	public int chooseNumber(NumberRange range, String description)
	{
		Integer lower = range.getLower();
		if(lower != null && lower.equals(range.getUpper()))
			return lower;

		return super.chooseNumber(range, description);
	}

	@Override
	public void divide(int quantity, int minimum, int whatFrom, String beingDivided, List<SanitizedTarget> targets)
	{
		if(targets.size() == 1)
		{
			targets.get(0).division = quantity;
			return;
		}

		super.divide(quantity, minimum, whatFrom, beingDivided, targets);
	}
}
