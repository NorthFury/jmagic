package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Champion;
import org.rnd.jmagic.abilities.keywords.Changeling;
import org.rnd.jmagic.abilities.keywords.Lifelink;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.LinkedList;
import java.util.List;

@Name("Changeling Hero")
@Types({Type.CREATURE})
@SubTypes({SubType.SHAPESHIFTER})
@ManaCost("4W")
@Printings({@Printings.Printed(ex = Expansion.LORWYN, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.WHITE})
public final class ChangelingHero extends Card
{
	public static final class ChampionACreature extends Champion
	{
		public ChampionACreature(GameState state)
		{
			super(state, "Champion a creature");
		}

		@Override
		protected List<NonStaticAbility> createNonStaticAbilities()
		{
			List<NonStaticAbility> ret = new LinkedList<NonStaticAbility>();

			ret.add(new ExileACreature(this.state));
			ret.add(new ReturnACreature(this.state));

			return ret;
		}

		public static final class ExileACreature extends ChampionExileAbility
		{
			public ExileACreature(GameState state)
			{
				super(state, "creature", HasType.instance(Type.CREATURE), ReturnACreature.class);
			}
		}

		public static final class ReturnACreature extends ChampionReturnAbility
		{
			public ReturnACreature(GameState state)
			{
				super(state, ExileACreature.class);
			}
		}
	}

	public ChangelingHero(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(4);

		this.addAbility(new Changeling(state));
		this.addAbility(new ChampionACreature(state));
		this.addAbility(new Lifelink(state));
	}
}
