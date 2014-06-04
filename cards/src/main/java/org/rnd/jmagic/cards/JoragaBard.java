package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Vigilance;
import org.rnd.jmagic.engine.*;

@Name("Joraga Bard")
@Types({Type.CREATURE})
@SubTypes({SubType.ELF, SubType.ROGUE, SubType.ALLY})
@ManaCost("3G")
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.COMMON)})
@ColorIdentity({Color.GREEN})
public final class JoragaBard extends Card
{
	public static final class VigilantAllies extends EventTriggeredAbility
	{
		public VigilantAllies(GameState state)
		{
			super(state, "Whenever Joraga Bard or another Ally enters the battlefield under your control, you may have Ally creatures you control gain vigilance until end of turn.");

			this.addPattern(allyTrigger());
			EventFactory abilityFactory = addAbilityUntilEndOfTurn(ALLY_CREATURES_YOU_CONTROL, Vigilance.class, "Ally creatures you control gain vigilance until end of turn");
			this.addEffect(youMay(abilityFactory, "You may have Ally creatures you control gain vigilance until end of turn."));
		}
	}

	public JoragaBard(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(4);

		this.addAbility(new VigilantAllies(state));
	}
}
