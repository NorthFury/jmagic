package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Lifelink;
import org.rnd.jmagic.abilities.keywords.Vigilance;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.jmagic.engine.patterns.*;

import java.util.HashMap;
import java.util.Map;

@Name("Angelic Skirmisher")
@Types({Type.CREATURE})
@SubTypes({SubType.ANGEL})
@ManaCost("4WW")
@Printings({@Printings.Printed(ex = Expansion.GATECRASH, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE})
public final class AngelicSkirmisher extends Card
{
	public static PlayerInterface.ChooseReason REASON = new PlayerInterface.ChooseReason("AngelicSkirmisher", "Choose first strike, vigilance, or lifelink.", true);

	public static final class AngelicSkirmisherAbility1 extends EventTriggeredAbility
	{
		public AngelicSkirmisherAbility1(GameState state)
		{
			super(state, "At the beginning of each combat, choose first strike, vigilance, or lifelink. Creatures you control gain that ability until end of turn.");

			SimpleEventPattern pattern = new SimpleEventPattern(EventType.BEGIN_STEP);
			pattern.put(EventType.Parameter.STEP, BeginningOfCombatStepOf.instance(Players.instance()));
			this.addPattern(pattern);

			Identity abilities = Identity.instance(FirstStrike.class, Vigilance.class, Lifelink.class);
			EventFactory choose = playerChoose(You.instance(), 1, abilities, PlayerInterface.ChoiceType.CLASS, REASON, "Choose first strike, vigilance, or lifelink.");
			this.addEffect(choose);

			Map<Class<? extends Keyword>, AbilityFactory> map = new HashMap<Class<? extends Keyword>, AbilityFactory>();
			map.put(FirstStrike.class, new SimpleAbilityFactory(FirstStrike.class));
			map.put(Vigilance.class, new SimpleAbilityFactory(Vigilance.class));
			map.put(Lifelink.class, new SimpleAbilityFactory(Lifelink.class));

			ContinuousEffect.Part part = new ContinuousEffect.Part(ContinuousEffectType.ADD_ABILITY_TO_OBJECT);
			part.parameters.put(ContinuousEffectType.Parameter.OBJECT, CREATURES_YOU_CONTROL);
			part.parameters.put(ContinuousEffectType.Parameter.ABILITY, MapGet.instance(EffectResult.instance(choose), map));

			EventFactory factory = new EventFactory(EventType.CREATE_FLOATING_CONTINUOUS_EFFECT, "Creatures you control gain that ability until end of turn.");
			factory.parameters.put(EventType.Parameter.CAUSE, This.instance());
			factory.parameters.put(EventType.Parameter.EFFECT, Identity.instance(part));
			this.addEffect(factory);
		}
	}

	public AngelicSkirmisher(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(4);

		// Flying
		this.addAbility(new Flying(state));

		// At the beginning of each combat, choose first strike, vigilance, or
		// lifelink. Creatures you control gain that ability until end of turn.
		this.addAbility(new AngelicSkirmisherAbility1(state));
	}
}
