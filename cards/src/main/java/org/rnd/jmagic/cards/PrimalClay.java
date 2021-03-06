package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Defender;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Name("Primal Clay")
@Types({Type.ARTIFACT, Type.CREATURE})
@SubTypes({SubType.SHAPESHIFTER})
@ManaCost("4")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2013, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.SIXTH_EDITION, r = Rarity.RARE), @Printings.Printed(ex = Expansion.FIFTH_EDITION, r = Rarity.RARE), @Printings.Printed(ex = Expansion.FOURTH_EDITION, r = Rarity.RARE), @Printings.Printed(ex = Expansion.REVISED, r = Rarity.RARE), @Printings.Printed(ex = Expansion.ANTIQUITIES, r = Rarity.UNCOMMON)})
@ColorIdentity({})
public final class PrimalClay extends Card
{
	public static final PlayerInterface.ChooseReason REASON = new PlayerInterface.ChooseReason("PrimalClay", "Choose Primal Clay's characteristics", true);

	public static final class PrimalClayAbility0 extends StaticAbility
	{
		private static String vanillaText = "3/3 artifact creature";
		private static String flyingText = "2/2 artifact creature with flying";
		private static String defenderText = "1/6 Wall artifact creature with defender";

		private static Map<String, Object> makeMap(Object vanilla, Object flying, Object defender)
		{
			Map<String, Object> ret = new HashMap<String, Object>();
			ret.put(vanillaText, vanilla);
			ret.put(flyingText, flying);
			ret.put(defenderText, defender);
			return ret;
		}

		public PrimalClayAbility0(GameState state)
		{
			super(state, "As Primal Clay enters the battlefield, it becomes your choice of a 3/3 artifact creature, a 2/2 artifact creature with flying, or a 1/6 Wall artifact creature with defender in addition to its other types.");
			this.canApply = NonEmpty.instance();

			ZoneChangeReplacementEffect replacement = new ZoneChangeReplacementEffect(this.game, "Choose 3/3 artifact creature, 2/2 artifact creature with flying, or 1/6 Wall artifact creature with defender");
			replacement.addPattern(asThisEntersTheBattlefield());

			EventFactory choice = new EventFactory(EventType.PLAYER_CHOOSE, "Choose 3/3 artifact creature, 2/2 artifact creature with flying, or 1/6 Wall artifact creature with defender");
			choice.parameters.put(EventType.Parameter.PLAYER, You.instance());
			choice.parameters.put(EventType.Parameter.NUMBER, numberGenerator(1));
			choice.parameters.put(EventType.Parameter.CHOICE, Identity.instance(vanillaText, flyingText, defenderText));
			choice.parameters.put(EventType.Parameter.TYPE, Identity.instance(PlayerInterface.ChoiceType.STRING, REASON));
			replacement.addEffect(choice);

			SetGenerator chosen = EffectResult.instance(choice);
			Collection<?> artifactCreature = EnumSet.of(Type.ARTIFACT, Type.CREATURE);
			MagicSet wall = new MagicSet();
			wall.addAll(artifactCreature);
			wall.add(SubType.WALL);

			AbilityFactory flyingFactory = new SimpleAbilityFactory(Flying.class);
			AbilityFactory defenderFactory = new SimpleAbilityFactory(Defender.class);

			ContinuousEffect.Part part = new ContinuousEffect.Part(ContinuousEffectType.SET_COPIABLE_CHARACTERISTICS);
			part.parameters.put(ContinuousEffectType.Parameter.OBJECT, This.instance());
			part.parameters.put(ContinuousEffectType.Parameter.POWER, MapGet.instance(chosen, makeMap(3, 2, 1)));
			part.parameters.put(ContinuousEffectType.Parameter.TOUGHNESS, MapGet.instance(chosen, makeMap(3, 2, 6)));
			part.parameters.put(ContinuousEffectType.Parameter.TYPE, ExplodeCollections.instance(MapGet.instance(chosen, makeMap(artifactCreature, artifactCreature, wall))));
			part.parameters.put(ContinuousEffectType.Parameter.ABILITY, MapGet.instance(chosen, makeMap(0, flyingFactory, defenderFactory)));
			replacement.addEffect(createFloatingEffect(Empty.instance(), "Primal Clay becomes your choice of a 3/3 artifact creature, a 2/2 artifact creature with flying, or a 1/6 Wall artifact creature with defender in addition to its other types.", part));

			this.addEffectPart(replacementEffectPart(replacement));
		}
	}

	public PrimalClay(GameState state)
	{
		super(state);

		this.setPower(0);
		this.setToughness(0);

		// As Primal Clay enters the battlefield, it becomes your choice of a
		// 3/3 artifact creature, a 2/2 artifact creature with flying, or a 1/6
		// Wall artifact creature with defender in addition to its other types.
		// (A creature with defender can't attack.)
		this.addAbility(new PrimalClayAbility0(state));
	}
}
