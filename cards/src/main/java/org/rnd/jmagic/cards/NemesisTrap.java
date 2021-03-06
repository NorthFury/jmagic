package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.Trap;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

@Name("Nemesis Trap")
@Types({Type.INSTANT})
@SubTypes({SubType.TRAP})
@ManaCost("4BB")
@Printings({@Printings.Printed(ex = Expansion.COMMANDER, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.WORLDWAKE, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLACK})
public final class NemesisTrap extends Card
{
	/**
	 * @eparam CAUSE: nemesis trap
	 * @eparam PLAYER: controller of cause
	 * @eparam TARGET: the object to exile and copy
	 * @eparam RESULT: empty
	 */
	public static final EventType NEMESIS_TRAP_EFFECT = new EventType("NEMESIS_TRAP_EFFECT")
	{

		@Override
		public Parameter affects()
		{
			return null;
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			MagicSet target = parameters.get(Parameter.TARGET);
			GameObject targeted = target.getOne(GameObject.class);

			// We will be killing this creature, THEN copying it. In order for
			// this to work, the game will need a snapshot of that creature's
			// copiable values.
			game.snapshotSoon(targeted);
			game.refreshActualState();

			Event exile = exile(Identity.instance(target), "Exile target attacking creature.").createEvent(game, event.getSource());
			exile.perform(event, true);

			MagicSet cause = parameters.get(Parameter.CAUSE);
			MagicSet you = parameters.get(Parameter.PLAYER);

			Map<Parameter, MagicSet> tokenParameters = new HashMap<Parameter, MagicSet>();
			tokenParameters.put(Parameter.CAUSE, cause);
			tokenParameters.put(Parameter.CONTROLLER, you);
			tokenParameters.put(Parameter.OBJECT, target);
			Event token = createEvent(game, "Put a token that's a copy of that creature onto the battlefield.", EventType.CREATE_TOKEN_COPY, tokenParameters);
			token.perform(event, true);

			ZoneChange tokenMovement = token.getResult().getOne(ZoneChange.class);
			GameObject created = game.actualState.getByIDObject(tokenMovement.newObjectID);

			EventFactory exileTheToken = exile(Identity.instance(created), "Exile it");

			Map<Parameter, MagicSet> triggerParameters = new HashMap<Parameter, MagicSet>();
			triggerParameters.put(Parameter.CAUSE, cause);
			triggerParameters.put(Parameter.EVENT, new MagicSet(atTheBeginningOfTheEndStep()));
			triggerParameters.put(Parameter.EFFECT, new MagicSet(exileTheToken));
			Event exileLater = createEvent(game, "Exile it at the beginning of the next end step.", EventType.CREATE_DELAYED_TRIGGER, triggerParameters);
			exileLater.perform(event, true);

			event.setResult(Empty.set);
			return true;
		}

	};

	public NemesisTrap(GameState state)
	{
		super(state);

		// If a white creature is attacking, you may pay (B)(B) rather than pay
		// Nemesis Trap's mana cost.
		SetGenerator whiteAttacking = Intersect.instance(HasColor.instance(Color.WHITE), Attacking.instance());
		this.addAbility(new Trap(state, this.getName(), whiteAttacking, "If a white creature is attacking", "(B)(B)"));

		// Exile target attacking creature. Put a token that's a copy of that
		// creature onto the battlefield. Exile it at the beginning of the next
		// end step.
		SetGenerator target = targetedBy(this.addTarget(Attacking.instance(), "target attacking creature"));

		EventFactory effect = new EventFactory(NEMESIS_TRAP_EFFECT, "Exile target attacking creature. Put a token that's a copy of that creature onto the battlefield. Exile it at the beginning of the next end step.");
		effect.parameters.put(EventType.Parameter.CAUSE, This.instance());
		effect.parameters.put(EventType.Parameter.PLAYER, You.instance());
		effect.parameters.put(EventType.Parameter.TARGET, target);
		this.addEffect(effect);
	}
}
