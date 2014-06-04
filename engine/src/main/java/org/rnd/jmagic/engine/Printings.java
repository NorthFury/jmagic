package org.rnd.jmagic.engine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Printings
{
	@Target({ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Printed
	{
		Expansion ex();

		Rarity r();

	}

	Printed[] value();
}
