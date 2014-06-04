package org.rnd.jmagic.engine;

import java.io.Serializable;

public interface Sanitizable
{
	/**
	 * Produce a sanitized version of this object (one that is safe to serialize
	 * and deserialize).
	 * 
	 * @param state The GameState to use to sanitize this object, if necessary
	 * @param whoFor The player who is being sent this object
	 * @return An object that is safe to serialize/deserialize
	 */
	public Serializable sanitize(GameState state, Player whoFor);
}
