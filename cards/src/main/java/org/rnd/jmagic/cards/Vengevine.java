package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Name("Vengevine")
@Types({Type.CREATURE})
@SubTypes({SubType.ELEMENTAL})
@ManaCost("2GG")
@Printings({@Printings.Printed(ex = Expansion.RISE_OF_THE_ELDRAZI, r = Rarity.MYTHIC)})
@ColorIdentity({Color.GREEN})
public final class Vengevine extends Card
{
	/**
	 * Keys are player IDs, values are IDs of the creature spells that player
	 * has cast this turn, in order
	 */
	public static final class CreatureSpells extends Tracker<Map<Integer, List<Integer>>>
	{
		private HashMap<Integer, List<Integer>> value = new HashMap<Integer, List<Integer>>();
		private Map<Integer, List<Integer>> unmodifiable = Collections.unmodifiableMap(this.value);

		@Override
		@SuppressWarnings("unchecked")
		public CreatureSpells clone()
		{
			CreatureSpells ret = (CreatureSpells)super.clone();
			ret.value = (HashMap<Integer, List<Integer>>)this.value.clone();
			ret.unmodifiable = Collections.unmodifiableMap(ret.value);
			return ret;
		}

		@Override
		protected Map<Integer, List<Integer>> getValueInternal()
		{
			return this.unmodifiable;
		}

		@Override
		protected boolean match(GameState state, Event event)
		{
			if(event.type != EventType.BECOMES_PLAYED)
				return false;

			GameObject played = event.parameters.get(EventType.Parameter.OBJECT).evaluate(state, null).getOne(GameObject.class);
			return played.getTypes().contains(Type.CREATURE) && played.isSpell();
		}

		@Override
		protected void reset()
		{
			this.value.clear();
		}

		@Override
		protected void update(GameState state, Event event)
		{
			GameObject played = event.parameters.get(EventType.Parameter.OBJECT).evaluate(state, null).getOne(GameObject.class);
			Player who = event.parameters.get(EventType.Parameter.PLAYER).evaluate(state, null).getOne(Player.class);

			if(!this.value.containsKey(who.ID))
				this.value.put(who.ID, new LinkedList<Integer>());
			this.value.get(who.ID).add(played.ID);
		}
	}

	public static final class SecondCreatureSpell extends SetGenerator
	{
		private SetGenerator who;

		public static SecondCreatureSpell instance(SetGenerator who)
		{
			return new SecondCreatureSpell(who);
		}

		private SecondCreatureSpell(SetGenerator who)
		{
			this.who = who;
		}

		@Override
		public MagicSet evaluate(GameState state, Identified thisObject)
		{
			Map<Integer, List<Integer>> flagValue = state.getTracker(CreatureSpells.class).getValue(state);
			MagicSet who = this.who.evaluate(state, thisObject);

			MagicSet ret = new MagicSet();
			for(Player player: who.getAll(Player.class))
				if(flagValue.containsKey(player.ID))
				{
					List<Integer> thatPlayersSpells = flagValue.get(player.ID);
					if(thatPlayersSpells.size() >= 2)
						ret.add(state.get(thatPlayersSpells.get(1)));
				}
			return ret;
		}
	}

	public static final class Vengeful extends EventTriggeredAbility
	{
		public Vengeful(GameState state)
		{
			super(state, "Whenever you cast a spell, if it's the second creature spell you cast this turn, you may return Vengevine from your graveyard to the battlefield.");

			this.addPattern(whenYouCastASpell());

			state.ensureTracker(new CreatureSpells());
			SetGenerator thatSpell = EventParameter.instance(TriggerEvent.instance(This.instance()), EventType.Parameter.OBJECT);
			SetGenerator yourSecondCreature = SecondCreatureSpell.instance(You.instance());
			this.interveningIf = Intersect.instance(thatSpell, yourSecondCreature);

			EventFactory putOntoBattlefield = new EventFactory(EventType.PUT_ONTO_BATTLEFIELD, "Return Vengevine from your graveyard to the battlefield");
			putOntoBattlefield.parameters.put(EventType.Parameter.CAUSE, This.instance());
			putOntoBattlefield.parameters.put(EventType.Parameter.CONTROLLER, You.instance());
			putOntoBattlefield.parameters.put(EventType.Parameter.OBJECT, ABILITY_SOURCE_OF_THIS);
			this.addEffect(youMay(putOntoBattlefield, "You may return Vengevine from your graveyard to the battlefield."));

			this.triggersFromGraveyard();
		}
	}

	public Vengevine(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(3);

		// Haste
		this.addAbility(new Haste(state));

		// Whenever you cast a spell, if it's the second creature spell you cast
		// this turn, you may return Vengevine from your graveyard to the
		// battlefield.
		this.addAbility(new Vengeful(state));
	}
}
