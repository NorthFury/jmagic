package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Defender;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.jmagic.engine.patterns.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Name("Cathedral Membrane")
@Types({Type.ARTIFACT, Type.CREATURE})
@SubTypes({SubType.WALL})
@ManaCost("1(W/P)")
@Printings({@Printings.Printed(ex = Expansion.NEW_PHYREXIA, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.WHITE})
public final class CathedralMembrane extends Card
{
	public static final class DiesDuringCombat implements ZoneChangePattern
	{
		private ZoneChangePattern pattern;

		public DiesDuringCombat()
		{
			this.pattern = new SimpleZoneChangePattern(Battlefield.instance(), GraveyardOf.instance(Players.instance()), ABILITY_SOURCE_OF_THIS, true);
		}

		@Override
		public boolean looksBackInTime()
		{
			return true;
		}

		@Override
		public boolean match(ZoneChange zoneChange, Identified thisObject, GameState state)
		{
			if(state.currentPhase().type.equals(Phase.PhaseType.COMBAT))
				return this.pattern.match(zoneChange, thisObject, state);
			return false;

		}
	}

	public static final class BlockTracker extends Tracker<Map<Integer, Set<Integer>>>
	{
		private HashMap<Integer, Set<Integer>> ids = new HashMap<Integer, Set<Integer>>();
		private Map<Integer, Set<Integer>> unmodifiable = Collections.unmodifiableMap(this.ids);

		@SuppressWarnings("unchecked")
		@Override
		public BlockTracker clone()
		{
			BlockTracker ret = (BlockTracker)super.clone();
			ret.ids = (HashMap<Integer, Set<Integer>>)this.ids.clone();
			ret.unmodifiable = Collections.unmodifiableMap(ret.ids);
			return ret;
		}

		@Override
		protected Map<Integer, Set<Integer>> getValueInternal()
		{
			return this.unmodifiable;
		}

		@Override
		protected void reset()
		{
			this.ids.clear();
		}

		@Override
		protected boolean match(GameState state, Event event)
		{
			return event.type == EventType.DECLARE_ONE_BLOCKER;
		}

		@Override
		protected void update(GameState state, Event event)
		{
			int attacker = event.parameters.get(EventType.Parameter.ATTACKER).evaluate(state, null).getOne(GameObject.class).ID;
			int blocker = event.parameters.get(EventType.Parameter.OBJECT).evaluate(state, null).getOne(GameObject.class).ID;

			if(!this.ids.containsKey(blocker))
				this.ids.put(blocker, new HashSet<Integer>());
			this.ids.get(blocker).add(attacker);
		}
	}

	public static final class DeclaredBlockedBy extends SetGenerator
	{
		public static DeclaredBlockedBy instance(SetGenerator what)
		{
			return new DeclaredBlockedBy(what);
		}

		private SetGenerator what;

		private DeclaredBlockedBy(SetGenerator what)
		{
			this.what = what;
		}

		@Override
		public MagicSet evaluate(GameState state, Identified thisObject)
		{
			MagicSet ret = new MagicSet();

			BlockTracker tracker = state.getTracker(BlockTracker.class);
			Map<Integer, Set<Integer>> values = tracker.getValue(state);
			for(GameObject object: this.what.evaluate(state, thisObject).getAll(GameObject.class))
				if(values.containsKey(object.ID))
					for(Integer id: values.get(object.ID))
						ret.add(state.get(id));

			return ret;
		}
	}

	public static final class CathedralMembraneAbility1 extends EventTriggeredAbility
	{
		public CathedralMembraneAbility1(GameState state)
		{
			super(state, "When Cathedral Membrane dies during combat, it deals 6 damage to each creature it blocked this combat.");

			this.addPattern(new DiesDuringCombat());

			this.addEffect(permanentDealDamage(6, DeclaredBlockedBy.instance(ABILITY_SOURCE_OF_THIS), "It deals 6 damage to each creature it blocked this combat."));

			state.ensureTracker(new BlockTracker());
		}
	}

	public CathedralMembrane(GameState state)
	{
		super(state);

		this.setPower(0);
		this.setToughness(3);

		// Defender
		this.addAbility(new Defender(state));

		// When Cathedral Membrane is put into a graveyard from the battlefield
		// during combat, it deals 6 damage to each creature it blocked this
		// combat.
		this.addAbility(new CathedralMembraneAbility1(state));
	}
}
