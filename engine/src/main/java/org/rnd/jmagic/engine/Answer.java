package org.rnd.jmagic.engine;

import org.rnd.util.CamelCase;

import java.util.EnumSet;
import java.util.Set;

/**
 * Represent possible responses the player may give when queried
 */
public enum Answer
{
	LAND, LOSE, NO, NONLAND, WIN, YES, ARTIFACT, WHITE, BLUE, BLACK, RED, GREEN;

	/**
	 * Get the possible values when choosing either artifact, or a color.
	 * 
	 * @return A set containing ARTIFACT and the colors.
	 */
	public static final Set<Answer> artifactOrAColorChoices()
	{
		return EnumSet.of(ARTIFACT, WHITE, BLUE, BLACK, RED, GREEN);
	}

	/**
	 * Get the possible values of a called coin flip.
	 * 
	 * @return A set containing WIN and LOSE
	 */
	public static final Set<Answer> calledCoinFlipChoices()
	{
		return EnumSet.of(WIN, LOSE);
	}

	/**
	 * Get the possible responses to a yes/no question
	 * 
	 * @return A set containing YES and NO
	 */
	public static final Set<Answer> mayChoices()
	{
		return EnumSet.of(YES, NO);
	}

	@Override
	public final String toString()
	{
		return CamelCase.enumValueToDisplay(this.name());
	}
}
