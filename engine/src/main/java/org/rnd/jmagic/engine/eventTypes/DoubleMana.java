package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

public final class DoubleMana extends EventType
{	public static final EventType INSTANCE = new DoubleMana();

	 private DoubleMana()
	{
		super("DOUBLE_MANA");
	}

	@Override
	public boolean addsMana()
	{
		return true;
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean perform(Game game, Event event, java.util.Map<Parameter, MagicSet> parameters)
	{
		MagicSet result = new MagicSet();
		boolean success = true;

		for(Player player: parameters.get(Parameter.PLAYER).getAll(Player.class))
		{
			ManaPool oldMana = new ManaPool(parameters.get(Parameter.MANA).getAll(ManaSymbol.class));
			ManaPool newMana = new ManaPool();

			for(ManaSymbol mana: oldMana)
			{
				ManaSymbol.ManaType manaType = mana.getType();
				if(null != manaType)
				{
					switch(manaType)
					{
					case COLORLESS:
						ManaSymbol newSymbol = new ManaSymbol(Integer.toString(mana.colorless));
						newSymbol.colorless = mana.colorless;
						newMana.add(newSymbol);
						break;
					default:
						newMana.add(new ManaSymbol(manaType.getColor()));
						break;
					}
				}
			}

			java.util.Map<Parameter, MagicSet> manaParameters = new java.util.HashMap<Parameter, MagicSet>();
			manaParameters.put(Parameter.SOURCE, parameters.get(Parameter.SOURCE));
			manaParameters.put(Parameter.MANA, new MagicSet(newMana));
			manaParameters.put(Parameter.PLAYER, new MagicSet(player));
			Event manaEvent = createEvent(game, "Add " + newMana + " to " + player.getName() + "'s mana pool.", EventType.ADD_MANA, manaParameters);
			success = (manaEvent.perform(event, false) && success);
			result.addAll(manaEvent.getResult());
		}

		event.setResult(Identity.instance(result));
		return success;
	}
}