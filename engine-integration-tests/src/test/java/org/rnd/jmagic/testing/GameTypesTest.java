package org.rnd.jmagic.testing;

import org.junit.*;
import static org.junit.Assert.*;

import org.rnd.jmagic.cards.*;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.gameTypes.PackWars;
import org.rnd.jmagic.engine.gameTypes.X10;
import org.rnd.jmagic.engine.gameTypes.packWars.BoosterFactory;
import org.rnd.jmagic.engine.gameTypes.packWars.SpecificCardsBoosterFactory;

public class GameTypesTest extends JUnitTest
{
	@Test
	public void anyNumberInADeck()
	{
		this.addDeck(RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class, RelentlessRats.class);
		this.addDeck(Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class);
		startGame(GameTypes.VINTAGE);

		assertTrue(this.winner == null);
	}

	@Test(expected = AssertionError.class)
	public void tooManyNonBasic()
	{
		this.addDeck(Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class, Sprout.class);
		this.addDeck(Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class);
		startGame(GameTypes.VINTAGE);

		// assertTrue(this.winner.ID == player(1));
	}

	@Test
	public void x10()
	{
		this.addDeck(Plains.class);
		this.addDeck();

		BoosterFactory expansion = new SpecificCardsBoosterFactory("Memnite", "Memnite", "Memnite", "Memnite", "Memnite", "Memnite", "Memnite");

		GameType x10 = new GameType("X10");
		x10.addRule(new X10());
		x10.addRule(new PackWars(expansion));
		startGame(x10);

		respondWith(getPlayer(0));
		keep();
		keep();

		goToPhase(Phase.PhaseType.PRECOMBAT_MAIN);
	}
}
