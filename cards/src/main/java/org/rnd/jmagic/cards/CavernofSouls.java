package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.AsThisEntersTheBattlefieldChooseACreatureType;
import org.rnd.jmagic.abilities.TapFor1;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.jmagic.engine.patterns.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

@Name("Cavern of Souls")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.AVACYN_RESTORED, r = Rarity.RARE)})
@ColorIdentity({})
public final class CavernofSouls extends Card
{
	public static final class ChooseType extends AsThisEntersTheBattlefieldChooseACreatureType
	{
		public ChooseType(GameState state)
		{
			super(state, "As Cavern of Souls enters the battlefield, choose a creature type.");
			this.getLinkManager().addLinkClass(CavernofSoulsAbility2.class);
		}
	}

	private static final class CavernofSoulsMana extends ManaSymbol
	{
		private static final long serialVersionUID = 1L;

		private transient SetGenerator type;
		private int abilityID;

		private CavernofSoulsMana(SetGenerator type, int abilityID, int cavernOfSoulsID)
		{
			super("Cavern of Souls mana");
			this.colors = Color.allColors();
			this.type = type;
			this.abilityID = abilityID;
			this.sourceID = cavernOfSoulsID;
		}

		@Override
		public CavernofSoulsMana create()
		{
			CavernofSoulsMana s = new CavernofSoulsMana(this.type, this.abilityID, this.sourceID);
			s.colors = this.colors;
			s.colorless = this.colorless;
			return s;
		}

		@Override
		public boolean pays(GameState state, ManaSymbol cost)
		{
			if(!super.pays(state, cost))
				return false;

			if(cost.sourceID == -1)
				return false;

			GameObject source = state.getByIDObject(cost.sourceID);
			SubType type = this.type.evaluate(state, state.<GameObject>get(this.abilityID)).getOne(SubType.class);
			return source.isSpell() && source.getTypes().contains(Type.CREATURE) && source.getSubTypes().contains(type);
		}

		@Override
		public String toString()
		{
			return "(" + this.name + ")";
		}
	}

	/** IDs of spells and abilities on which a Cavern of Souls mana was spent */
	public static final class CavernofSoulsTracker extends Tracker<Collection<Integer>>
	{
		private HashSet<Integer> value = new HashSet<Integer>();
		private Collection<Integer> unmodifiable = Collections.unmodifiableSet(this.value);

		@Override
		@SuppressWarnings("unchecked")
		public CavernofSoulsTracker clone()
		{
			CavernofSoulsTracker ret = (CavernofSoulsTracker)super.clone();
			ret.value = (HashSet<Integer>)this.value.clone();
			ret.unmodifiable = Collections.unmodifiableSet(ret.value);
			return ret;
		}

		@Override
		protected Collection<Integer> getValueInternal()
		{
			return this.unmodifiable;
		}

		@Override
		protected boolean match(GameState state, Event event)
		{
			if(event.type != EventType.PAY_MANA)
				return false;

			MagicSet spent = event.getResult(state);
			return spent.getOne(CavernofSoulsMana.class) != null;
		}

		@Override
		protected void reset()
		{
			this.value.clear();
		}

		@Override
		protected void update(GameState state, Event event)
		{
			GameObject spentOn = event.parameters.get(EventType.Parameter.OBJECT).evaluate(state, null).getOne(GameObject.class);
			this.value.add(spentOn.ID);
		}
	}

	public static final class CavernofSoulsManaSpentOn extends SetGenerator
	{
		private static SetGenerator _instance = null;

		public static SetGenerator instance()
		{
			if(_instance == null)
				_instance = new CavernofSoulsManaSpentOn();
			return _instance;
		}

		private CavernofSoulsManaSpentOn()
		{
			// singleton
		}

		@Override
		public MagicSet evaluate(GameState state, Identified thisObject)
		{
			MagicSet ret = new MagicSet();

			Collection<Integer> trackerValue = state.getTracker(CavernofSoulsTracker.class).getValue(state);
			for(int id: trackerValue)
				ret.add(state.<GameObject>get(id));

			return ret;
		}
	}

	public static final class CavernofSoulsAbility2 extends ActivatedAbility
	{
		public CavernofSoulsAbility2(GameState state)
		{
			super(state, "(T): Add one mana of any color to your mana pool. Spend this mana only to cast a creature spell of the chosen type, and that spell can't be countered.");
			this.costsTap = true;

			this.getLinkManager().addLinkClass(ChooseType.class);
			SetGenerator type = ChosenFor.instance(LinkedTo.instance(This.instance()));
			SetGenerator mana = Identity.instance(new CavernofSoulsMana(type, this.ID, this.sourceID));
			this.addEffect(addManaToYourManaPoolFromAbility(mana, "Add one mana of any color to your mana pool. Spend this mana only to cast a creature spell of the chosen type,"));

			state.ensureTracker(new CavernofSoulsTracker());
			SetGenerator thatSpell = CavernofSoulsManaSpentOn.instance();

			SimpleEventPattern counterThatSpell = new SimpleEventPattern(EventType.COUNTER);
			counterThatSpell.put(EventType.Parameter.OBJECT, thatSpell);

			ContinuousEffect.Part part = new ContinuousEffect.Part(ContinuousEffectType.PROHIBIT);
			part.parameters.put(ContinuousEffectType.Parameter.PROHIBITION, Identity.instance(counterThatSpell));

			SetGenerator youHaveMana = ManaInPool.instance(You.instance());
			SetGenerator keepEffect = Union.instance(youHaveMana, thatSpell);

			EventFactory shelter = new EventFactory(EventType.CREATE_FLOATING_CONTINUOUS_EFFECT, "and that spell can't be countered.");
			shelter.parameters.put(EventType.Parameter.CAUSE, This.instance());
			shelter.parameters.put(EventType.Parameter.EFFECT, Identity.instance(part));
			shelter.parameters.put(EventType.Parameter.EXPIRES, Identity.instance(Not.instance(keepEffect)));
			this.addEffect(shelter);

		}
	}

	public CavernofSouls(GameState state)
	{
		super(state);

		// As Cavern of Souls enters the battlefield, choose a creature type.
		this.addAbility(new ChooseType(state));

		// (T): Add (1) to your mana pool.
		this.addAbility(new TapFor1(state));

		// (T): Add one mana of any color to your mana pool. Spend this mana
		// only to cast a creature spell of the chosen type, and that spell
		// can't be countered.
		this.addAbility(new CavernofSoulsAbility2(state));
	}
}
