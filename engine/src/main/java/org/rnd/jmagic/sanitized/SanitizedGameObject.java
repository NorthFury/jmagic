package org.rnd.jmagic.sanitized;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SanitizedGameObject extends SanitizedIdentified
{
	public static enum CharacteristicSet
	{
		ACTUAL, BACK_FACE, FLIP, PHYSICAL;
	}

	private static final long serialVersionUID = 10L;

	public final Map<CharacteristicSet, SanitizedCharacteristics> characteristics;

	public final int controllerID;
	public final int ownerID;
	public final int zoneID;

	public final List<Counter> counters;
	public final int damage;

	public final int attackingID;
	public final List<Integer> blockedByIDs;
	public final List<Integer> blockingIDs;
	public final Set<Integer> defendingIDs;

	// TODO: consider replacing all booleans with a bit field
	public final boolean flipped, faceDown, tapped, ghost, transformed, hasACopyEffect;

	public final Set<Color> canProduce;
	public final int valueOfX;

	public final Collection<Integer> attachments;
	public final int attachedTo;

	public final Collection<Integer> linkObjects;
	public final Collection<Object> otherLinks;

	public final boolean isCard;
	public final boolean isEmblem;

	public final int pairedWith;

	public SanitizedGameObject(GameObject go, Player whoFor)
	{
		super(go);

		this.controllerID = go.controllerID;
		this.ownerID = go.ownerID;
		this.zoneID = go.zoneID;

		this.characteristics = new HashMap<CharacteristicSet, SanitizedCharacteristics>();
		this.characteristics.put(CharacteristicSet.ACTUAL, go.getCharacteristics().sanitize(go.state, whoFor));

		this.counters = go.counters;
		this.damage = go.getDamage();

		this.attackingID = go.getAttackingID();
		this.blockedByIDs = go.getBlockedByIDs();
		this.blockingIDs = go.getBlockingIDs();
		this.defendingIDs = go.getDefendingIDs();

		this.flipped = go.isFlipped();
		this.faceDown = (null != go.faceDownValues);
		this.tapped = go.isTapped();
		this.ghost = go.isGhost();
		this.transformed = go.isTransformed();
		this.hasACopyEffect = go.hasACopyEffect;

		Set<Color> canProduce = EnumSet.noneOf(Color.class);
		for(ManaSymbol.ManaType manaType: CouldBeProducedBy.objectCouldProduce(go.game, go, new HashSet<SetGenerator>()))
		{
			if(manaType == ManaSymbol.ManaType.COLORLESS)
				continue;
			canProduce.add(manaType.getColor());
		}
		this.canProduce = canProduce;

		this.valueOfX = go.getValueOfX();

		this.attachments = go.getAttachments();
		this.attachedTo = go.getAttachedTo();

		Collection<Integer> linkObjects = new LinkedList<Integer>();
		Collection<Object> otherLinks = new LinkedList<Object>();
		for(NonStaticAbility a: go.getNonStaticAbilities())
		{
			MagicSet linkInformation = a.getLinkManager().getLinkInformation(a.state);
			if(linkInformation != null)
			{
				Set<Identified> objects = linkInformation.getAll(Identified.class);
				for(Identified link: objects)
					linkObjects.add(link.ID);
				linkInformation.removeAll(objects);
				otherLinks.addAll(linkInformation);
			}
		}
		for(StaticAbility a: go.getStaticAbilities())
		{
			MagicSet linkInformation = a.getLinkManager().getLinkInformation(a.state);
			if(linkInformation != null)
			{
				Set<Identified> objects = linkInformation.getAll(Identified.class);
				for(Identified link: objects)
					linkObjects.add(link.ID);
				linkInformation.removeAll(objects);
				otherLinks.addAll(linkInformation);
			}
		}
		this.linkObjects = linkObjects;
		this.otherLinks = otherLinks;

		this.isCard = go.isCard();
		// TODO : instanceof HATE
		this.isEmblem = go instanceof Emblem;

		GameObject pairedWith = go.getPairedWith(go.state);
		if(null == pairedWith)
			this.pairedWith = -1;
		else
			this.pairedWith = pairedWith.ID;

		// alternate characteristics
		if(!go.isCard())
			return;

		boolean actuallyVisible = go.isVisibleTo(whoFor);
		boolean storePhysical = false;
		if(this.transformed)
			storePhysical = true;
		else if(go.getBackFace() != null)
			this.characteristics.put(CharacteristicSet.BACK_FACE, go.getBackFace().sanitize(go.state, whoFor));

		boolean faceDown = go.faceDownValues != null;
		boolean physicallyVisible = go.getPhysical().isVisibleTo(whoFor);
		if((faceDown && physicallyVisible) || (!faceDown && actuallyVisible))
		{
			if(!this.flipped && go.getBottomHalf() != null)
				this.characteristics.put(CharacteristicSet.FLIP, go.getBottomHalf().sanitize(go.state, whoFor));
			if(go.hasACopyEffect || this.flipped)
				storePhysical = true;
		}

		if(storePhysical || (faceDown && physicallyVisible))
			this.characteristics.put(CharacteristicSet.PHYSICAL, go.getPhysical().getCharacteristics().sanitize(go.game.physicalState, whoFor));
	}

	public Collection<SanitizedIdentified> sanitizeAbilities(GameState state, Player whoFor)
	{
		Collection<SanitizedIdentified> ret = new LinkedList<SanitizedIdentified>();
		for(SanitizedCharacteristics characteristics: this.characteristics.values())
			for(int ID: characteristics.abilities)
			{
				if(ID == -1)
					continue;

				GameState sanitizeIn = state;
				if(!state.containsIdentified(ID))
					// this can happen if we are looking at a back face or
					// bottom half of a card that is front-face-up or unflipped.
					// Sanitizing such an object causes its
					// back-face/bottom-half abilities to be generated in the
					// physical state.
					sanitizeIn = state.game.physicalState;
				Identified ability = sanitizeIn.get(ID);
				Serializable sanitized = ((Sanitizable)ability).sanitize(sanitizeIn, whoFor);
				ret.add((SanitizedIdentified)sanitized);
			}
		return ret;
	}
}
