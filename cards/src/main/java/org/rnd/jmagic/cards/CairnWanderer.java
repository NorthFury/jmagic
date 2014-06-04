package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Changeling;
import org.rnd.jmagic.abilities.keywords.Deathtouch;
import org.rnd.jmagic.abilities.keywords.DoubleStrike;
import org.rnd.jmagic.abilities.keywords.Fear;
import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.abilities.keywords.Landwalk;
import org.rnd.jmagic.abilities.keywords.Lifelink;
import org.rnd.jmagic.abilities.keywords.Protection;
import org.rnd.jmagic.abilities.keywords.Reach;
import org.rnd.jmagic.abilities.keywords.Shroud;
import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.abilities.keywords.Vigilance;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Name("Cairn Wanderer")
@Types({Type.CREATURE})
@SubTypes({SubType.SHAPESHIFTER})
@ManaCost("4B")
@Printings({@Printings.Printed(ex = Expansion.LORWYN, r = Rarity.RARE)})
@ColorIdentity({Color.BLACK})
public final class CairnWanderer extends Card
{
	private static class Filter extends SetGenerator
	{
		public static Filter instance(SetGenerator abilities, Set<Class<?>> toKeep)
		{
			return new Filter(abilities, toKeep);
		}

		private SetGenerator abilities;
		private Set<Class<?>> toKeep;

		private Filter(SetGenerator abilities, Set<Class<?>> toKeep)
		{
			this.abilities = abilities;
			this.toKeep = toKeep;
		}

		@Override
		public MagicSet evaluate(GameState state, Identified thisObject)
		{
			MagicSet abilities = this.abilities.evaluate(state, thisObject);
			Map<String, Identified> ret = new HashMap<String, Identified>();

			for(Keyword a: abilities.getAll(Keyword.class))
				for(Class<?> c: this.toKeep)
					if(c.isAssignableFrom(a.getClass()))
						ret.put(a.getName(), a);

			return new MagicSet(ret.values());
		}
	}

	public static final class CairnWandererAbility1 extends StaticAbility
	{
		public CairnWandererAbility1(GameState state)
		{
			super(state, "As long as a creature card with flying is in a graveyard, Cairn Wanderer has flying. The same is true for fear, first strike, double strike, deathtouch, haste, landwalk, lifelink, protection, reach, trample, shroud, and vigilance.");

			Set<Class<?>> abilityClasses = new LinkedHashSet<Class<?>>();
			abilityClasses.add(Flying.class);
			abilityClasses.add(Fear.class);
			abilityClasses.add(FirstStrike.class);
			abilityClasses.add(DoubleStrike.class);
			abilityClasses.add(Deathtouch.class);
			abilityClasses.add(Haste.class);
			abilityClasses.add(Landwalk.class);
			abilityClasses.add(Lifelink.class);
			abilityClasses.add(Protection.class);
			abilityClasses.add(Reach.class);
			abilityClasses.add(Trample.class);
			abilityClasses.add(Shroud.class);
			abilityClasses.add(Vigilance.class);

			SetGenerator inGraveyards = InZone.instance(GraveyardOf.instance(Players.instance()));
			SetGenerator creatureCardsInGraveyards = Intersect.instance(HasType.instance(Type.CREATURE), Cards.instance(), inGraveyards);
			SetGenerator abilitiesOfCreatureCardsInGraveyards = AbilitiesOf.instance(creatureCardsInGraveyards);
			SetGenerator abilities = Filter.instance(abilitiesOfCreatureCardsInGraveyards, abilityClasses);

			ContinuousEffect.Part part = new ContinuousEffect.Part(ContinuousEffectType.COPY_ABILITIES_TO_OBJECT);
			part.parameters.put(ContinuousEffectType.Parameter.OBJECT, This.instance());
			part.parameters.put(ContinuousEffectType.Parameter.ABILITY, abilities);
			this.addEffectPart(part);
		}
	}

	public CairnWanderer(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(4);

		// Changeling (This card is every creature type at all times.)
		this.addAbility(new Changeling(state));

		// As long as a creature card with flying is in a graveyard, Cairn
		// Wanderer has flying. The same is true for fear, first strike, double
		// strike, deathtouch, haste, landwalk, lifelink, protection, reach,
		// trample, shroud, and vigilance.
		this.addAbility(new CairnWandererAbility1(state));
	}
}
