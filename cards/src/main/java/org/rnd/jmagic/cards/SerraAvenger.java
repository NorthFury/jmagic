package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Vigilance;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.jmagic.engine.patterns.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Name("Serra Avenger")
@Types({Type.CREATURE})
@SubTypes({SubType.ANGEL})
@ManaCost("WW")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2013, r = Rarity.RARE), @Printings.Printed(ex = Expansion.TIME_SPIRAL, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE})
public final class SerraAvenger extends Card
{
	public static final class TurnTracker extends Tracker<Map<Integer, Integer>>
	{
		private HashMap<Integer, Integer> counts = new HashMap<Integer, Integer>();
		private Map<Integer, Integer> unmodifiable = Collections.unmodifiableMap(this.counts);

		@SuppressWarnings("unchecked")
		@Override
		public TurnTracker clone()
		{
			TurnTracker ret = (TurnTracker)super.clone();
			ret.counts = (HashMap<Integer, Integer>)this.counts.clone();
			ret.unmodifiable = Collections.unmodifiableMap(ret.counts);
			return ret;
		}

		@Override
		protected Map<Integer, Integer> getValueInternal()
		{
			return this.unmodifiable;
		}

		@Override
		protected void reset()
		{
			// do nothing
		}

		@Override
		protected boolean match(GameState state, Event event)
		{
			return event.type == EventType.BEGIN_TURN;
		}

		@Override
		protected void update(GameState state, Event event)
		{
			Turn turn = event.parametersNow.get(EventType.Parameter.TURN).evaluate(state, null).getOne(Turn.class);
			Player owner = turn.getOwner(state);
			if(this.counts.containsKey(owner.ID))
				this.counts.put(owner.ID, this.counts.get(owner.ID) + 1);
			else
				this.counts.put(owner.ID, 1);
		}
	}

	public static final class PlayersNotPastThirdTurn extends SetGenerator
	{
		private static PlayersNotPastThirdTurn _instance = null;

		public static PlayersNotPastThirdTurn instance()
		{
			if(_instance == null)
				_instance = new PlayersNotPastThirdTurn();
			return _instance;
		}

		private PlayersNotPastThirdTurn()
		{
			// singleton constructor
		}

		@Override
		public MagicSet evaluate(GameState state, Identified thisObject)
		{
			MagicSet ret = new MagicSet();
			Map<Integer, Integer> tracker = state.getTracker(TurnTracker.class).getValue(state);

			for(Player player: state.players)
				if(!tracker.containsKey(player.ID) || tracker.get(player.ID) <= 3)
					ret.add(player);

			return ret;
		}
	}

	public static final class SerraAvengerAbility0 extends StaticAbility
	{
		public SerraAvengerAbility0(GameState state)
		{
			super(state, "You can't cast Serra Avenger during your first, second, or third turns of the game.");

			state.ensureTracker(new TurnTracker());
			SimpleEventPattern castSpell = new SimpleEventPattern(EventType.CAST_SPELL_OR_ACTIVATE_ABILITY);
			castSpell.put(EventType.Parameter.PLAYER, PlayersNotPastThirdTurn.instance());
			castSpell.put(EventType.Parameter.OBJECT, This.instance());

			ContinuousEffect.Part part = new ContinuousEffect.Part(ContinuousEffectType.PROHIBIT);
			part.parameters.put(ContinuousEffectType.Parameter.PROHIBITION, Identity.instance(castSpell));
			this.addEffectPart(part);

			this.canApply = NonEmpty.instance();
		}
	}

	public SerraAvenger(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(3);

		// You can't cast Serra Avenger during your first, second, or third
		// turns of the game.
		this.addAbility(new SerraAvengerAbility0(state));

		// Flying
		this.addAbility(new Flying(state));

		// Vigilance (Attacking doesn't cause this creature to tap.)
		this.addAbility(new Vigilance(state));
	}
}
