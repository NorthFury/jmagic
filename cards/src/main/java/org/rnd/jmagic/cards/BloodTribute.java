package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Kicker;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

@Name("Blood Tribute")
@Types({Type.SORCERY})
@ManaCost("4BB")
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.RARE)})
@ColorIdentity({Color.BLACK})
public final class BloodTribute extends Card
{
	/**
	 * @eparam CAUSE: Blood Tribute
	 * @eparam TARGET: target of CAUSE
	 * @eparam PLAYER: controller of CAUSE
	 * @eparam EFFECT: whether CAUSE was kicked
	 */
	public static EventType BLOOD_TRIBUTE_EVENT = new EventType("BLOOD_TRIBUTE_EVENT")
	{
		@Override
		public Parameter affects()
		{
			return null;
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{

			MagicSet cause = parameters.get(Parameter.CAUSE);
			Player target = parameters.get(Parameter.TARGET).getOne(Player.class);
			int lifeTotalBefore = target.lifeTotal;
			int number = DivideBy.get(lifeTotalBefore, 2, true);

			Map<Parameter, MagicSet> loseLifeParameters = new HashMap<Parameter, MagicSet>();
			loseLifeParameters.put(Parameter.CAUSE, cause);
			loseLifeParameters.put(Parameter.PLAYER, new MagicSet(target));
			loseLifeParameters.put(Parameter.NUMBER, new MagicSet(number));
			Event loseLife = createEvent(game, "Target opponent loses half his or her life, rounded up", EventType.LOSE_LIFE, loseLifeParameters);
			loseLife.perform(event, true);

			if(!parameters.get(Parameter.EFFECT).isEmpty())
			{
				target = target.getActual();
				int lifeLostThisWay = lifeTotalBefore - target.lifeTotal;

				Map<Parameter, MagicSet> gainLifeParameters = new HashMap<Parameter, MagicSet>();
				gainLifeParameters.put(Parameter.CAUSE, cause);
				gainLifeParameters.put(Parameter.PLAYER, parameters.get(Parameter.PLAYER));
				gainLifeParameters.put(Parameter.NUMBER, new MagicSet(lifeLostThisWay));
				Event gainLife = createEvent(game, "You gain life equal to the life lost this way.", EventType.GAIN_LIFE, gainLifeParameters);
				gainLife.perform(event, true);
			}

			event.setResult(Empty.set);
			return true;
		}
	};

	public BloodTribute(GameState state)
	{
		super(state);

		// Kicker - Tap an untapped Vampire you control. (You may tap a Vampire
		// you control in addition to any other costs as you cast this spell.)
		SetGenerator yourVampires = Intersect.instance(ControlledBy.instance(You.instance()), HasSubType.instance(SubType.VAMPIRE));
		SetGenerator yourUntappedVampires = Intersect.instance(Untapped.instance(), yourVampires);

		EventFactory tapAVampire = new EventFactory(EventType.TAP_CHOICE, "Tap an untapped Vampire you control");
		tapAVampire.parameters.put(EventType.Parameter.CAUSE, This.instance());
		tapAVampire.parameters.put(EventType.Parameter.PLAYER, You.instance());
		tapAVampire.parameters.put(EventType.Parameter.NUMBER, numberGenerator(1));
		tapAVampire.parameters.put(EventType.Parameter.CHOICE, yourUntappedVampires);
		CostCollection kickerCost = new CostCollection(Kicker.COST_TYPE, tapAVampire);
		this.addAbility(new Kicker(state, kickerCost));

		// Target opponent loses half his or her life, rounded up. If Blood
		// Tribute was kicked, you gain life equal to the life lost this way.
		Target target = this.addTarget(OpponentsOf.instance(You.instance()), "target opponent");

		EventFactory effect = new EventFactory(BLOOD_TRIBUTE_EVENT, "Target opponent loses half his or her life, rounded up. If Blood Tribute was kicked, you gain life equal to the life lost this way.");
		effect.parameters.put(EventType.Parameter.CAUSE, This.instance());
		effect.parameters.put(EventType.Parameter.TARGET, targetedBy(target));
		effect.parameters.put(EventType.Parameter.PLAYER, You.instance());
		effect.parameters.put(EventType.Parameter.EFFECT, ThisSpellWasKicked.instance(kickerCost));
		this.addEffect(effect);
	}
}
