package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Protection;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Name("Apostle's Blessing")
@Types({Type.INSTANT})
@ManaCost("1(W/P)")
@Printings({@Printings.Printed(ex = Expansion.NEW_PHYREXIA, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class ApostlesBlessing extends Card
{
	public static final PlayerInterface.ChooseReason APOSTLES_BLESSING_REASON = new PlayerInterface.ChooseReason("ApostlesBlessing", "Choose artifact or a color.", true);

	/**
	 * @eparam CAUSE: the reason for choosing
	 * @eparam TARGET: the creature or artifact gaining protection
	 * @eparam PLAYER: the player choosing
	 * @eparam RESULT: empty
	 */
	public static final EventType CHOOSE_ARTIFACT_OR_A_COLOR = new EventType("CHOOSE_ARTIFACT_OR_A_COLOR")
	{
		@Override
		public Parameter affects()
		{
			return EventType.Parameter.PLAYER;
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			event.setResult(Empty.set);

			Set<Answer> choices = Answer.artifactOrAColorChoices();
			Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);

			List<Answer> chosenList = player.choose(1, choices, PlayerInterface.ChoiceType.ENUM, APOSTLES_BLESSING_REASON);
			if(chosenList.isEmpty())
				return false;

			Answer choice = chosenList.get(0);

			Class<? extends Protection> ability = null;
			switch(choice)
			{
			case ARTIFACT:
				ability = Protection.FromArtifacts.class;
				break;
			case WHITE:
				ability = Protection.FromWhite.class;
				break;
			case BLUE:
				ability = Protection.FromBlue.class;
				break;
			case BLACK:
				ability = Protection.FromBlack.class;
				break;
			case RED:
				ability = Protection.FromRed.class;
				break;
			case GREEN:
				ability = Protection.FromGreen.class;
				break;
			default:
				break;
			}

			ContinuousEffect.Part part = new ContinuousEffect.Part(ContinuousEffectType.ADD_ABILITY_TO_OBJECT);
			part.parameters.put(ContinuousEffectType.Parameter.OBJECT, Identity.instance(parameters.get(Parameter.TARGET)));
			part.parameters.put(ContinuousEffectType.Parameter.ABILITY, Identity.instance(new SimpleAbilityFactory(ability)));

			Map<Parameter, MagicSet> fceParameters = new HashMap<Parameter, MagicSet>();
			fceParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
			fceParameters.put(Parameter.EFFECT, new MagicSet(part));
			Event protection = createEvent(game, "Target artifact or creature you control gains protection from artifact or the color of your choice until end of turn.", EventType.CREATE_FLOATING_CONTINUOUS_EFFECT, fceParameters);
			protection.perform(event, false);

			return true;
		}
	};

	public ApostlesBlessing(GameState state)
	{
		super(state);

		// (((w/p)) can be paid with either (W) or 2 life.)

		// Target artifact or creature you control gains protection from
		// artifacts or from the color of your choice until end of turn.
		SetGenerator target = targetedBy(this.addTarget(Intersect.instance(Union.instance(ArtifactPermanents.instance(), CreaturePermanents.instance()), ControlledBy.instance(You.instance())), "target artifact or creature you control"));

		EventFactory effect = new EventFactory(CHOOSE_ARTIFACT_OR_A_COLOR, "Target artifact or creature you control gains protection from artifacts or from the color of your choice until end of turn.");
		effect.parameters.put(EventType.Parameter.CAUSE, This.instance());
		effect.parameters.put(EventType.Parameter.TARGET, target);
		effect.parameters.put(EventType.Parameter.PLAYER, You.instance());
		this.addEffect(effect);
	}
}
