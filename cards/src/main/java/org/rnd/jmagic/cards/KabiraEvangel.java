package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Protection;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Name("Kabira Evangel")
@Types({Type.CREATURE})
@SubTypes({SubType.ALLY, SubType.HUMAN, SubType.CLERIC})
@ManaCost("2W")
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE})
public final class KabiraEvangel extends Card
{
	/**
	 * @eparam CAUSE: kabira evangel's trigger
	 * @eparam PLAYER: controller of CAUSE
	 * @eparam OBJECT: allies PLAYER controls
	 */
	public static final EventType KABIRA_EVANGEL_EVENT = new EventType("KABIRA_EVANGEL_EVENT")
	{
		@Override
		public Parameter affects()
		{
			return null;
		}

		@Override
		public void makeChoices(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			Player you = parameters.get(Parameter.PLAYER).getOne(Player.class);
			Color color = you.chooseColor(parameters.get(Parameter.CAUSE).getOne(NonStaticAbility.class).getSourceID());
			event.putChoices(you, Collections.singleton(color));
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			MagicSet cause = parameters.get(Parameter.CAUSE);
			Player you = parameters.get(Parameter.PLAYER).getOne(Player.class);
			MagicSet allies = parameters.get(Parameter.OBJECT);

			Color color = event.getChoices(you).getOne(Color.class);

			ContinuousEffect.Part protection = new ContinuousEffect.Part(ContinuousEffectType.ADD_ABILITY_TO_OBJECT);
			protection.parameters.put(ContinuousEffectType.Parameter.OBJECT, Identity.instance(allies));
			protection.parameters.put(ContinuousEffectType.Parameter.ABILITY, Identity.instance(new Protection.AbilityFactory(color)));

			Map<Parameter, MagicSet> fceParameters = new HashMap<Parameter, MagicSet>();
			fceParameters.put(Parameter.CAUSE, cause);
			fceParameters.put(Parameter.EFFECT, new MagicSet(protection));
			createEvent(game, "Allies you control gain protection from the chosen color until end of turn", EventType.CREATE_FLOATING_CONTINUOUS_EFFECT, fceParameters).perform(event, true);

			event.setResult(Empty.set);
			return true;
		}
	};

	public static final class AllyProtection extends EventTriggeredAbility
	{
		public AllyProtection(GameState state)
		{
			super(state, "Whenever Kabira Evangel or another Ally enters the battlefield under your control, you may choose a color. If you do, Allies you control gain protection from the chosen color until end of turn.");

			this.addPattern(allyTrigger());

			SetGenerator alliesYouControl = Intersect.instance(HasSubType.instance(SubType.ALLY), ControlledBy.instance(You.instance()));

			EventFactory effect = new EventFactory(KABIRA_EVANGEL_EVENT, "Choose a color. Allies you control gain protection from the chosen color until end of turn.");
			effect.parameters.put(EventType.Parameter.CAUSE, This.instance());
			effect.parameters.put(EventType.Parameter.PLAYER, You.instance());
			effect.parameters.put(EventType.Parameter.OBJECT, alliesYouControl);

			this.addEffect(youMay(effect, "You may choose a color. If you do, Allies you control gain protection from the chosen color until end of turn."));
		}
	}

	public KabiraEvangel(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(3);

		// Whenever Kabira Evangel or another Ally enters the battlefield under
		// your control, you may choose a color. If you do, Allies you control
		// gain protection from the chosen color until end of turn.
		this.addAbility(new AllyProtection(state));
	}
}
