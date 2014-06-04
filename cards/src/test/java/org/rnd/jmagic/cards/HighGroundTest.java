package org.rnd.jmagic.cards;

import static org.junit.Assert.*;

import org.junit.*;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.testing.*;

import java.util.HashMap;
import java.util.Map;

public class HighGroundTest extends JUnitTest
{
	@Test
	public void highGround()
	{
		this.addDeck(HighGround.class, WhiteKnight.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class);
		this.addDeck(RagingGoblin.class, RagingGoblin.class, RagingGoblin.class, Plains.class, Plains.class, Plains.class, Plains.class, Plains.class);
		this.startGame(GameTypes.OPEN);

		this.respondWith(this.getPlayer(0));
		this.keep();
		this.keep();

		this.goToPhase(Phase.PhaseType.PRECOMBAT_MAIN);

		this.respondWith(this.getSpellAction(WhiteKnight.class));
		this.addMana("WW");
		this.donePlayingManaAbilities();
		this.pass();
		this.pass();

		this.respondWith(this.getSpellAction(HighGround.class));
		this.addMana("W");
		this.donePlayingManaAbilities();
		this.pass();
		this.pass();

		this.goToPhase(Phase.PhaseType.ENDING);
		this.goToPhase(Phase.PhaseType.PRECOMBAT_MAIN);

		this.respondWith(this.getSpellAction(RagingGoblin.class));
		this.addMana("R");
		this.donePlayingManaAbilities();
		this.pass();
		this.pass();
		Identified RagingGoblinA = this.game.physicalState.battlefield().objects.get(0);

		this.respondWith(this.getSpellAction(RagingGoblin.class));
		this.addMana("R");
		this.donePlayingManaAbilities();
		this.pass();
		this.pass();
		Identified RagingGoblinB = this.game.physicalState.battlefield().objects.get(0);

		this.respondWith(this.getSpellAction(RagingGoblin.class));
		this.addMana("R");
		this.donePlayingManaAbilities();
		this.pass();
		this.pass();
		Identified RagingGoblinC = this.game.physicalState.battlefield().objects.get(0);

		this.goToStep(Step.StepType.DECLARE_ATTACKERS);
		this.respondWith(this.getChoice(RagingGoblinA), this.getChoice(RagingGoblinB), this.getChoice(RagingGoblinC));

		this.goToStep(Step.StepType.DECLARE_BLOCKERS);
		this.respondWith(this.pullChoice(WhiteKnight.class));
		this.respondWith(this.getChoice(RagingGoblinA), this.getChoice(RagingGoblinB));
		this.respondArbitrarily();

		this.goToStep(Step.StepType.COMBAT_DAMAGE);
		Map<Integer, Integer> divisions = new HashMap<Integer, Integer>();
		divisions.put(RagingGoblinA.ID, 1);
		divisions.put(RagingGoblinB.ID, 1);
		this.divide(divisions);
		// order the goblins in the yard

		this.goToPhase(Phase.PhaseType.POSTCOMBAT_MAIN);
		assertEquals(19, this.player(0).lifeTotal);
		// one goblin, high ground, white knight
		assertEquals(3, this.game.actualState.battlefield().objects.size());
	}
}