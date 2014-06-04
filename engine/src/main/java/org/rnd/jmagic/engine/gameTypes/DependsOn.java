package org.rnd.jmagic.engine.gameTypes;

import org.rnd.jmagic.engine.GameType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DependsOn
{
	public Class<? extends GameType.GameTypeRule> value();
}
