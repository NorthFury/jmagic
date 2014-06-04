package org.rnd.jmagic.sanitized;

import org.rnd.jmagic.engine.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SanitizedCharacteristics implements Serializable
{
	private static final long serialVersionUID = 1L;

	public String name;
	public Collection<String> costs;
	public ManaPool manaCost;

	public Set<SuperType> superTypes;
	public Set<Type> types;
	public Set<SubType> subTypes;
	public Set<Color> colors;
	public Set<Color> colorIndicator;

	public int power, toughness;
	public int printedLoyalty;

	public List<Integer> abilities;
	public List<Integer> hiddenAbilities;

	public List<SanitizedMode> modes;
	public List<SanitizedMode> selectedModes;
	public MagicSet numModes;
	public final Map<SanitizedTarget, List<SanitizedTarget>> chosenTargets;

	// this method takes a game state because it needs to get the keyword
	// abilities from the object
	public SanitizedCharacteristics(Characteristics c, GameState state)
	{
		this.name = c.name;

		this.costs = new LinkedList<String>();
		for(EventFactory f: c.costs)
			this.costs.add(f.name);
		this.manaCost = c.manaCost;

		this.superTypes = c.superTypes;
		this.types = c.types;
		this.subTypes = c.subTypes;
		this.colors = EnumSet.copyOf(c.colors);
		this.colorIndicator = EnumSet.copyOf(c.colorIndicator);

		this.power = c.power;
		this.toughness = c.toughness;

		this.printedLoyalty = c.loyalty;

		this.abilities = new LinkedList<Integer>();
		this.hiddenAbilities = new LinkedList<Integer>();
		this.abilities.addAll(c.abilityIDsInOrder);

		List<Integer> keywordIDs = c.keywordAbilities;
		for(int keywordID: keywordIDs)
		{
			Keyword k = state.get(keywordID);
			for(Identified a: k.getAbilitiesGranted().getAll(Identified.class))
			{
				this.abilities.remove((Integer)(a.ID));
				this.hiddenAbilities.add(a.ID);
			}
		}

		this.modes = new LinkedList<SanitizedMode>();
		this.selectedModes = new LinkedList<SanitizedMode>();
		this.chosenTargets = new HashMap<SanitizedTarget, List<SanitizedTarget>>();
		int index = 0;
		for(Mode m: c.modes)
		{
			SanitizedMode sanitizedMode = new SanitizedMode(m, m.sourceID, index);
			this.modes.add(sanitizedMode);

			if(c.selectedModes.contains(m))
			{
				this.selectedModes.add(sanitizedMode);

				for(int targetIndex = 0; targetIndex < m.targets.size(); ++targetIndex)
				{
					Target target = m.targets.get(targetIndex);
					if(c.chosenTargets.containsKey(target))
					{
						List<SanitizedTarget> sanitizedChosenTargets = new LinkedList<SanitizedTarget>();
						for(Target t: c.chosenTargets.get(target))
							if(null != t)
								sanitizedChosenTargets.add(new SanitizedTarget(t));
						this.chosenTargets.put(sanitizedMode.targets.get(targetIndex), sanitizedChosenTargets);
					}
				}
			}

			++index;
		}
		this.numModes = c.numModes;
	}
}
