package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.EntersTheBattlefieldTapped;
import org.rnd.jmagic.abilities.TapForMana;
import org.rnd.jmagic.engine.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Name("Creeping Tar Pit")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.WORLDWAKE, r = Rarity.RARE)})
@ColorIdentity({Color.BLUE, Color.BLACK})
public final class CreepingTarPit extends Card
{
	public static final class Animate extends ActivatedAbility
	{
		public Animate(GameState state)
		{
			super(state, "(1)(U)(B): Until end of turn, Creeping Tar Pit becomes a 3/2 blue and black Elemental creature and is unblockable. It's still a land.");
			this.setManaCost(new ManaPool("1UB"));

			Animator animator = new Animator(ABILITY_SOURCE_OF_THIS, 3, 2);
			animator.addColor(Color.BLUE);
			animator.addColor(Color.BLACK);
			animator.addSubType(SubType.ELEMENTAL);
			List<ContinuousEffect.Part> parts = new LinkedList<ContinuousEffect.Part>(Arrays.asList(animator.getParts()));
			parts.add(unblockable(ABILITY_SOURCE_OF_THIS));
			this.addEffect(createFloatingEffect("Until end of turn, Creeping Tar Pit becomes a 3/2 blue and black Elemental creature and is unblockable. It's still a land.", parts.toArray(new ContinuousEffect.Part[0])));
		}
	}

	public CreepingTarPit(GameState state)
	{
		super(state);

		// Creeping Tar Pit enters the battlefield tapped.
		this.addAbility(new EntersTheBattlefieldTapped(state, this.getName()));

		// (T): Add (U) or (B) to your mana pool.
		this.addAbility(new TapForMana.Final(state, "(UB)"));

		// (1)(U)(B): Until end of turn, Creeping Tar Pit beco)mes a 3/2 blue
		// and
		// black Elemental creature and is unblockable. It's still a land.
		this.addAbility(new Animate(state));
	}
}
