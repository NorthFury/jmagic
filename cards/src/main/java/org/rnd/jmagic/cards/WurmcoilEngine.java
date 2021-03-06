package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Deathtouch;
import org.rnd.jmagic.abilities.keywords.Lifelink;
import org.rnd.jmagic.engine.*;

@Name("Wurmcoil Engine")
@Types({Type.ARTIFACT, Type.CREATURE})
@SubTypes({SubType.WURM})
@ManaCost("6")
@Printings({@Printings.Printed(ex = Expansion.SCARS_OF_MIRRODIN, r = Rarity.MYTHIC)})
@ColorIdentity({})
public final class WurmcoilEngine extends Card
{
	public static final class WurmcoilEngineAbility1 extends EventTriggeredAbility
	{
		public WurmcoilEngineAbility1(GameState state)
		{
			super(state, "When Wurmcoil Engine dies, put a 3/3 colorless Wurm artifact creature token with deathtouch and a 3/3 colorless Wurm artifact creature token with lifelink onto the battlefield.");

			this.addPattern(whenThisDies());

			CreateTokensFactory deathToken = new CreateTokensFactory(1, 3, 3, "Put a 3/3 colorless Wurm artifact creature token with deathtouch");
			deathToken.setSubTypes(SubType.WURM);
			deathToken.setArtifact();
			deathToken.addAbility(Deathtouch.class);

			CreateTokensFactory lifeToken = new CreateTokensFactory(1, 3, 3, "and a 3/3 colorless Wurm artifact creature token with lifelink onto the battlefield.");
			lifeToken.setSubTypes(SubType.WURM);
			lifeToken.setArtifact();
			lifeToken.addAbility(Lifelink.class);

			this.addEffect(simultaneous(deathToken.getEventFactory(), lifeToken.getEventFactory()));
		}
	}

	public WurmcoilEngine(GameState state)
	{
		super(state);

		this.setPower(6);
		this.setToughness(6);

		// Deathtouch, lifelink
		this.addAbility(new Deathtouch(state));
		this.addAbility(new Lifelink(state));

		// When Wurmcoil Engine is put into a graveyard from the battlefield,
		// put a 3/3 colorless Wurm artifact creature token with deathtouch and
		// a 3/3 colorless Wurm artifact creature token with lifelink onto the
		// battlefield.
		this.addAbility(new WurmcoilEngineAbility1(state));
	}
}
