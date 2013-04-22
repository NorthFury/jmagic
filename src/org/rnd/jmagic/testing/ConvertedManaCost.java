package org.rnd.jmagic.testing;

import org.junit.*;
import static org.junit.Assert.*;
import org.rnd.jmagic.cards.*;
import org.rnd.jmagic.engine.*;

public class ConvertedManaCost extends JUnitTest
{
	@Test
	public void monocoloredHybrid()
	{
		this.addDeck(BeseechtheQueen.class);
		this.addDeck();
		startGame(GameType.OPEN);

		assertEquals(6, getLibrary(0).objects.get(0).getConvertedManaCost());
	}
}
