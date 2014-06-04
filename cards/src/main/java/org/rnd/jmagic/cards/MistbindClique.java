package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Champion;
import org.rnd.jmagic.abilities.keywords.Flash;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.LinkedList;
import java.util.List;

@Name("Mistbind Clique")
@Types({Type.CREATURE})
@SubTypes({SubType.FAERIE, SubType.WIZARD})
@ManaCost("3U")
@Printings({@Printings.Printed(ex = Expansion.LORWYN, r = Rarity.RARE)})
@ColorIdentity({Color.BLUE})
public final class MistbindClique extends Card
{
	public static final class ChampionAFaerie extends Champion
	{
		public ChampionAFaerie(GameState state)
		{
			super(state, "Champion a Faerie");
		}

		@Override
		protected List<NonStaticAbility> createNonStaticAbilities()
		{
			List<NonStaticAbility> ret = new LinkedList<NonStaticAbility>();

			ret.add(new Exile(this.state));
			ret.add(new Return(this.state));

			return ret;
		}

		public static final class Exile extends ChampionExileAbility
		{
			public Exile(GameState state)
			{
				super(state, "Faerie", HasSubType.instance(SubType.FAERIE), Return.class);
			}
		}

		public static final class Return extends ChampionReturnAbility
		{
			public Return(GameState state)
			{
				super(state, Exile.class);
			}
		}
	}

	public static final class ManaDenial extends EventTriggeredAbility
	{
		public ManaDenial(GameState state)
		{
			super(state, "When a Faerie is championed with Mistbind Clique, tap all lands target player controls.");
			this.addPattern(whenSomethingIsChampioned(ABILITY_SOURCE_OF_THIS, HasSubType.instance(SubType.FAERIE)));
			Target target = this.addTarget(Players.instance(), "target player");
			this.addEffect(tap(Intersect.instance(LandPermanents.instance(), ControlledBy.instance(targetedBy(target))), "Tap all lands target player controls."));
		}
	}

	public MistbindClique(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(4);

		// Flash
		this.addAbility(new Flash(state));

		// Flying
		this.addAbility(new Flying(state));

		// Champion a Faerie
		this.addAbility(new ChampionAFaerie(state));

		// When a Faerie is championed with Mistbind Clique, tap all lands
		// target player controls.
		this.addAbility(new ManaDenial(state));
	}
}
