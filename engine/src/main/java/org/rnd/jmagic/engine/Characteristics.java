package org.rnd.jmagic.engine;

import org.rnd.jmagic.sanitized.SanitizedCharacteristics;
import org.rnd.util.Constructor;
import org.rnd.util.NumberRange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Characteristics implements Sanitizable, Cloneable
{
	public enum Characteristic
	{
		// name, mana cost, card type, subtype, supertype, expansion symbol,
		// rules text, power, toughness, and/or loyalty
		NAME, MANA_COST, TYPES, EXPANSION, RULES_TEXT, POWER, TOUGHNESS, LOYALTY, CHOICES_MADE_WHEN_PLAYING, COLOR
	}

	/**
	 * @param game The game to construct the new abilities in.
	 * @param original The Card to construct to get the copiable values from.
	 * @param target The object which will be receiving the values.
	 * @param include If true, the <code>types</code> parameter will specify
	 * exactly what copiable values to apply. Otherwise, it specifies exactly
	 * which copiable values <em>not</em> to apply.
	 * @param types A vararg that, combined with <code>include</code>, specifies
	 * which copiable values to apply.
	 */
	public static Characteristics createFromClass(Game game, Class<? extends Card> original, GameObject target, boolean include, Characteristic... types)
	{
		GameObject dummy = Constructor.construct(original, new Class[] {GameState.class}, new Object[] {game.physicalState});

		// we remove only the object and not any of its abilities
		// this is because those abilities are referenced by the characteristics
		// class by ID
		game.physicalState.removeIdentified(dummy.ID);

		Characteristics ret = dummy.getCharacteristics();
		for(int a: ret.nonStaticAbilities)
		{
			NonStaticAbility ability = game.physicalState.<NonStaticAbility>get(a);
			ability.sourceID = target.ID;
			game.actualState.put(ability);
		}
		for(int a: ret.staticAbilities)
		{
			StaticAbility ability = game.physicalState.<StaticAbility>get(a);
			ability.sourceID = target.ID;
			game.actualState.put(ability);
		}
		for(int a: ret.keywordAbilities)
			game.actualState.put(game.physicalState.<Keyword>get(a));

		return ret;
	}

	// 706.2. When copying an object, the copy acquires the copiable values of
	// the original object's characteristics and, for an object on the stack,
	// choices made when casting or activating it (mode, targets, the value of
	// X, whether a kicker cost was paid, how it will affect multiple targets,
	// and so on).

	public String name;
	public int power;
	public int toughness;
	public int loyalty;
	public int minimumX;
	protected Expansion symbol;
	public ManaPool manaCost;
	public List<Integer> abilityIDsInOrder;
	protected List<Integer> nonStaticAbilities;
	protected List<Integer> staticAbilities;
	public List<Integer> keywordAbilities;
	public List<EventFactory> costs;
	public List<Mode> modes;
	public Set<Color> colors;
	public Set<Color> colorIndicator;
	public Set<SuperType> superTypes;
	public Set<Type> types;
	public Set<SubType> subTypes;

	/**
	 * For each {@link Target} in any selected {@link Mode}, what {@link Target}
	 * were chosen during {@link #chosenTargets}. Multiple selected
	 * {@link Target} for each {@link Target} in a {@link Mode} are supported
	 * for {@link Card} like {@link org.rnd.jmagic.cards.Fireball} where the
	 * single {@link Target} becomes one or more. Any null-entry in the
	 * collection indicates a {@link Target} determined to be illegal.
	 */
	public Map<Target, List<Target>> chosenTargets;

	// stack stuff
	protected CostCollection alternateCost;
	protected Collection<CostCollection> optionalAdditionalCostsChosen;
	public List<Mode> selectedModes;
	public MagicSet numModes;
	protected int valueOfX;
	protected int sourceID;

	public Characteristics()
	{
		this.name = "";
		this.power = 0;
		this.toughness = 0;
		this.loyalty = 0;
		this.minimumX = 0;
		this.symbol = null;
		this.manaCost = null;
		this.abilityIDsInOrder = new LinkedList<Integer>();
		this.nonStaticAbilities = new LinkedList<Integer>();
		this.staticAbilities = new LinkedList<Integer>();
		this.keywordAbilities = new LinkedList<Integer>();
		this.costs = new LinkedList<EventFactory>();
		this.modes = new LinkedList<Mode>();
		this.colors = EnumSet.noneOf(Color.class);
		this.colorIndicator = EnumSet.noneOf(Color.class);
		this.superTypes = EnumSet.noneOf(SuperType.class);
		this.types = EnumSet.noneOf(Type.class);
		this.subTypes = EnumSet.noneOf(SubType.class);
		this.chosenTargets = new HashMap<Target, List<Target>>();

		// stack stuff
		this.alternateCost = null;
		this.optionalAdditionalCostsChosen = new LinkedList<CostCollection>();
		this.selectedModes = new LinkedList<Mode>();
		this.numModes = new MagicSet(new NumberRange(1, 1));
		this.valueOfX = -1;
		this.sourceID = 0;
	}

	public Characteristics create(GameObject target)
	{
		Characteristics ret = new Characteristics();

		ret.name = this.name;
		ret.power = this.power;
		ret.toughness = this.toughness;
		ret.loyalty = this.loyalty;
		ret.minimumX = this.minimumX;
		ret.symbol = this.symbol;

		if(this.manaCost == null)
			ret.manaCost = null;
		else
			ret.manaCost = new ManaPool(this.manaCost);

		ret.costs = new LinkedList<EventFactory>(this.costs);
		ret.alternateCost = this.alternateCost;
		ret.optionalAdditionalCostsChosen = new LinkedList<CostCollection>(this.optionalAdditionalCostsChosen);

		// Copying modes should also copy the selected targets, as well as
		// divisions of damage
		for(Mode mode: this.modes)
		{
			Mode newMode = new Mode(mode, target.ID);
			ret.modes.add(newMode);
			if(this.selectedModes.contains(mode))
				ret.selectedModes.add(newMode);
		}

		ret.colors = EnumSet.copyOf(this.colors);
		ret.colorIndicator = EnumSet.copyOf(this.colorIndicator);

		ret.superTypes = EnumSet.copyOf(this.superTypes);
		ret.types = EnumSet.copyOf(this.types);
		ret.subTypes = EnumSet.copyOf(this.subTypes);

		ret.valueOfX = this.valueOfX;

		ret.sourceID = this.sourceID;
		ret.numModes = this.numModes;

		// this isn't permanent, don't panic
		// the loops that follow look through this list and replace the IDs with
		// the right ones
		ret.abilityIDsInOrder = new ArrayList<Integer>(this.abilityIDsInOrder);

		// Map from ability ID on original to created ability on target
		Map<Integer, Identified> abilitiesCopied = new HashMap<Integer, Identified>();
		for(int abilityID: this.nonStaticAbilities)
		{
			NonStaticAbility ability = (NonStaticAbility)target.state.<NonStaticAbility>get(abilityID).create(target.game);
			ret.nonStaticAbilities.add(ability.ID);
			ret.abilityIDsInOrder.set(ret.abilityIDsInOrder.indexOf(abilityID), ability.ID);
			abilitiesCopied.put(abilityID, ability);
		}
		for(int abilityID: this.staticAbilities)
		{
			StaticAbility ability = target.state.<StaticAbility>get(abilityID).create(target.game);
			ability.sourceID = target.ID;
			ret.staticAbilities.add(ability.ID);
			ret.abilityIDsInOrder.set(ret.abilityIDsInOrder.indexOf(abilityID), ability.ID);
			abilitiesCopied.put(abilityID, ability);
		}
		for(int abilityID: this.keywordAbilities)
		{
			Keyword originalAbility = target.state.<Keyword>get(abilityID);

			Set<Integer> abilitiesGranted = new HashSet<Integer>();
			for(Identified abilityGranted: originalAbility.getAbilitiesGranted().getAll(Identified.class))
				abilitiesGranted.add(abilitiesCopied.get(abilityGranted.ID).ID);

			Keyword keyword = originalAbility.create(target.game);
			keyword.setAbilitiesGranted(abilitiesGranted);
			ret.keywordAbilities.add(keyword.ID);
			ret.abilityIDsInOrder.set(ret.abilityIDsInOrder.indexOf(originalAbility.ID), keyword.ID);
			abilitiesCopied.put(originalAbility.ID, keyword);
		}

		// while(this.abilityIDsInOrder.remove(null))
		// {
		// // Remove any abilities that weren't copied
		// }

		MagicSet allAbilities = new MagicSet(abilitiesCopied.values());

		// Set up the links for the physical versions of all the abilities
		for(Linkable link: allAbilities.getAll(Linkable.class))
		{
			Linkable.Manager manager = link.getLinkManager();

			for(Class<? extends Linkable> linkClass: manager.getLinkClasses())
				if(manager.getLink(target.game.physicalState, linkClass) == null)
				{
					Linkable linkTo = null;
					outer: for(Linkable potentialLink: allAbilities.getAll(Linkable.class))
					{
						if(!potentialLink.getClass().equals(linkClass))
							continue;

						Linkable.Manager potentialManager = potentialLink.getLinkManager();
						for(Class<? extends Linkable> potentialLinkClass: potentialManager.getLinkClasses())
							if(potentialManager.getLink(target.game.physicalState, potentialLinkClass) == null && potentialLinkClass.equals(link.getClass()))
							{
								linkTo = potentialLink;
								break outer;
							}
					}

					// TODO : I have a feeling Skill Borrower or Experiment Kraj
					// could possibly throw this exception, by picking up one of
					// a pair of linked abilities.
					if(linkTo == null)
						throw new RuntimeException("Ability (" + link + ") says that it links to an ability of type (" + linkClass + ") and was not linked to anything.");

					manager.setLink(linkTo);
					linkTo.getLinkManager().setLink(link);
				}
		}

		// Copy the abilities into the actual state
		for(Identified ability: allAbilities.getAll(Identified.class))
			ability.copy();

		for(Map.Entry<Target, List<Target>> entry: this.chosenTargets.entrySet())
			ret.chosenTargets.put(entry.getKey(), new LinkedList<Target>(entry.getValue()));

		return ret;
	}

	@Override
	public Characteristics clone()
	{
		Characteristics ret;
		try
		{
			ret = (Characteristics)super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw new InternalError("Clone not supported: " + this);
		}

		if(this.manaCost == null)
			ret.manaCost = null;
		else
			ret.manaCost = new ManaPool(this.manaCost);
		ret.abilityIDsInOrder = new LinkedList<Integer>(this.abilityIDsInOrder);
		ret.nonStaticAbilities = new LinkedList<Integer>(this.nonStaticAbilities);
		ret.staticAbilities = new LinkedList<Integer>(this.staticAbilities);
		ret.keywordAbilities = new LinkedList<Integer>(this.keywordAbilities);
		ret.costs = new LinkedList<EventFactory>(this.costs);
		ret.modes = new LinkedList<Mode>(this.modes);
		ret.colors = EnumSet.copyOf(this.colors);
		ret.colorIndicator = EnumSet.copyOf(this.colorIndicator);
		ret.superTypes = EnumSet.copyOf(this.superTypes);
		ret.types = EnumSet.copyOf(this.types);
		ret.subTypes = EnumSet.copyOf(this.subTypes);
		ret.alternateCost = this.alternateCost;
		ret.optionalAdditionalCostsChosen = new LinkedList<CostCollection>(this.optionalAdditionalCostsChosen);
		ret.selectedModes = new LinkedList<Mode>(this.selectedModes);
		ret.numModes = new MagicSet(this.numModes);

		ret.chosenTargets = new HashMap<Target, List<Target>>(this.chosenTargets);
		for(Map.Entry<Target, List<Target>> entry: this.chosenTargets.entrySet())
		{
			List<Target> targets = new LinkedList<Target>();
			for(Target t: entry.getValue())
			{
				if(null == t)
					targets.add(null);
				else
					targets.add(t.clone());
			}
			ret.chosenTargets.put(entry.getKey(), targets);
		}
		return ret;
	}

	void removeStaticAbility(int ID)
	{
		this.abilityIDsInOrder.remove((Object)ID);
		this.staticAbilities.remove((Object)ID);
	}

	@Override
	public SanitizedCharacteristics sanitize(GameState state, Player whoFor)
	{
		return new SanitizedCharacteristics(this, state);
	}

	@Override
	public String toString()
	{
		return "Copiable Values Of: " + this.name;
	}
}
