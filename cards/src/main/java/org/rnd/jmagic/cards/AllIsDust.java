package org.rnd.jmagic.cards;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

@Name("All Is Dust")
@Types({Type.TRIBAL, Type.SORCERY})
@SubTypes({SubType.ELDRAZI})
@ManaCost("7")
@Printings({@Printings.Printed(ex = Expansion.RISE_OF_THE_ELDRAZI, r = Rarity.MYTHIC)})
@ColorIdentity({})
public final class AllIsDust extends Card
{
	/**
	 * @eparam CAUSE: All Is Dust
	 * @eparam OBJECT: SplitOnController.instance(colored permanents)
	 * @eparam RESULT: empty
	 */
	public static final EventType ALL_IS_DUST_EVENT = new EventType("ALL_IS_DUST_EVENT")
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
			for(MagicSet objects: parameters.get(Parameter.OBJECT).getAll(MagicSet.class))
			{
				if(objects.isEmpty())
					continue;

				Player controller = objects.getOne(GameObject.class).getController(game.actualState);

				Map<Parameter, MagicSet> sacrificeParameters = new HashMap<Parameter, MagicSet>();
				sacrificeParameters.put(Parameter.CAUSE, cause);
				sacrificeParameters.put(Parameter.PLAYER, new MagicSet(controller));
				sacrificeParameters.put(Parameter.PERMANENT, objects);
				Event sacrifice = createEvent(game, controller + " sacrifices all colored permanents he or she controls", EventType.SACRIFICE_PERMANENTS, sacrificeParameters);
				sacrifice.perform(event, false);
			}

			event.setResult(Empty.set);
			return true;
		}
	};

	public AllIsDust(GameState state)
	{
		super(state);

		// Each player sacrifices all colored permanents he or she controls.
		EventFactory sacrifice = new EventFactory(ALL_IS_DUST_EVENT, "Each player sacrifices all colored permanents he or she controls.");
		sacrifice.parameters.put(EventType.Parameter.CAUSE, This.instance());
		sacrifice.parameters.put(EventType.Parameter.OBJECT, SplitOnController.instance(RelativeComplement.instance(Permanents.instance(), Colorless.instance())));
		this.addEffect(sacrifice);
	}
}
