package org.rnd.jmagic.sanitized;

import org.rnd.jmagic.engine.Counter;
import org.rnd.jmagic.engine.ManaPool;
import org.rnd.jmagic.engine.Player;
import org.rnd.jmagic.engine.generators.OpponentsOf;
import org.rnd.jmagic.engine.generators.This;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SanitizedPlayer extends SanitizedIdentified
{
	public static final long serialVersionUID = 2L;

	public final Collection<Integer> attachments;
	public final Collection<Integer> defendingIDs;
	public final int lifeTotal;
	public final int poisonCounters;
	public final List<Counter> nonPoisonCounters;

	public final List<Integer> nonStaticAbilities;
	public final List<Integer> staticAbilities;
	public final List<Integer> keywordAbilities;
	public final ManaPool pool;

	public final Set<Integer> opponents;

	public final int library, graveyard, hand, sideboard;

	public SanitizedPlayer(Player p)
	{
		super(p);

		this.attachments = new LinkedList<Integer>(p.attachments);
		this.defendingIDs = new LinkedList<Integer>(p.defendingIDs);

		this.lifeTotal = p.lifeTotal;
		this.poisonCounters = p.countPoisonCounters();

		this.nonPoisonCounters = new LinkedList<Counter>();
		for(Counter c: p.counters)
			if(c.getType() != Counter.CounterType.POISON)
				this.nonPoisonCounters.add(c);

		this.nonStaticAbilities = SanitizedGameState.IDs(p.getNonStaticAbilities());
		this.staticAbilities = SanitizedGameState.IDs(p.getStaticAbilities());
		this.keywordAbilities = SanitizedGameState.IDs(p.getKeywordAbilities());

		this.pool = p.pool;

		this.library = p.getLibraryID();
		this.graveyard = p.getGraveyardID();
		this.hand = p.getHandID();
		this.sideboard = p.getSideboardID();

		this.opponents = new HashSet<Integer>();
		for(Player opp: OpponentsOf.instance(This.instance()).evaluate(p.state, p).getAll(Player.class))
			this.opponents.add(opp.ID);
	}
}
