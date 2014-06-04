package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Enchant;
import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

@Name("Splinter Twin")
@Types({Type.ENCHANTMENT})
@SubTypes({SubType.AURA})
@ManaCost("2RR")
@Printings({@Printings.Printed(ex = Expansion.RISE_OF_THE_ELDRAZI, r = Rarity.RARE)})
@ColorIdentity({Color.RED})
public final class SplinterTwin extends Card
{
	public static final EventType SPLINTER_DIES = new EventType("SPLINTER_DIES")
	{

		@Override
		public Parameter affects()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			MagicSet thatCreature = parameters.get(Parameter.OBJECT);
			EventFactory exile = exile(Identity.instance(thatCreature), "Exile that token");
			Map<Parameter, MagicSet> triggerParameters = new HashMap<Parameter, MagicSet>();
			triggerParameters.put(EventType.Parameter.CAUSE, new MagicSet(event.getSource()));
			triggerParameters.put(EventType.Parameter.EVENT, new MagicSet(atTheBeginningOfTheEndStep()));
			triggerParameters.put(EventType.Parameter.EFFECT, new MagicSet(exile));
			Event delayedTrigger = createEvent(game, "Exile that token at the beginning of the next end step.", EventType.CREATE_DELAYED_TRIGGER, triggerParameters);
			delayedTrigger.perform(event, true);

			event.setResult(Empty.set);
			return true;

		}
	};

	public static final class SplinterMe extends ActivatedAbility
	{
		public SplinterMe(GameState state)
		{
			super(state, "(T): Put a token that's a copy of this creature onto the battlefield. That token has haste. Exile it at the beginning of the next end step.");
			this.costsTap = true;

			EventFactory copyMe = new EventFactory(EventType.CREATE_TOKEN_COPY, "Put a token that's a copy of this creature onto the battlefield.");
			copyMe.parameters.put(EventType.Parameter.CAUSE, This.instance());
			copyMe.parameters.put(EventType.Parameter.CONTROLLER, You.instance());
			copyMe.parameters.put(EventType.Parameter.OBJECT, ABILITY_SOURCE_OF_THIS);
			this.addEffect(copyMe);

			SetGenerator thatToken = NewObjectOf.instance(EffectResult.instance(copyMe));

			EventFactory haste = addAbilityUntilEndOfTurn(thatToken, Haste.class, "That token has haste.");
			haste.parameters.put(EventType.Parameter.EXPIRES, Identity.instance(Empty.instance()));
			this.addEffect(haste);

			EventFactory exile = exile(delayedTriggerContext(thatToken), "Exile it");
			EventFactory exileLater = new EventFactory(EventType.CREATE_DELAYED_TRIGGER, "Exile it at the beginning of the next ends step.");
			exileLater.parameters.put(EventType.Parameter.CAUSE, This.instance());
			exileLater.parameters.put(EventType.Parameter.EVENT, Identity.instance(atTheBeginningOfTheEndStep()));
			exileLater.parameters.put(EventType.Parameter.EFFECT, Identity.instance(exile));
			this.addEffect(exileLater);
		}
	}

	public static final class SplinterTwinAbility1 extends StaticAbility
	{
		public SplinterTwinAbility1(GameState state)
		{
			super(state, "Enchanted creature has \"(T): Put a token that's a copy of this creature onto the battlefield. That token has haste. Exile it at the beginning of the next end step.\"");
			this.addEffectPart(addAbilityToObject(EnchantedBy.instance(This.instance()), SplinterMe.class));
		}
	}

	public SplinterTwin(GameState state)
	{
		super(state);

		// Enchant creature
		this.addAbility(new Enchant.Creature(state));

		// Enchanted creature has
		// "(T): Put a token that's a copy of this creature onto the battlefield. That token has haste. Exile it at the beginning of the next end step."
		this.addAbility(new SplinterTwinAbility1(state));
	}
}
