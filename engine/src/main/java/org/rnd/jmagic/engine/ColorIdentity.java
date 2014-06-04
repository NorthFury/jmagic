package org.rnd.jmagic.engine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class only represents the color identity of this particular class. To
 * get the complete color identity of a card, you'll also need to get this
 * annotation from any alternate face annotations.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ColorIdentity
{
	Color[] value();
}
