package org.rnd.jmagic.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 613.7. Within a layer or sublayer, determining which order effects are
 * applied in is sometimes done using a dependency system. If a dependency
 * exists, it will override the timestamp system.
 * 
 * 613.7a An effect is said to "depend on" another if (a) it's applied in the
 * same layer (and, if applicable, sublayer) as the other effect (see rules
 * 613.1 and 613.3); (b) applying the other would change the text or the
 * existence of the first effect, what it applies to, or what it does to any of
 * the things it applies to; and (c) neither effect is from a
 * characteristic-defining ability. Otherwise, the effect is considered to be
 * independent of the other effect.
 * 
 * 613.7b An effect dependent on one or more other effects waits to apply until
 * just after all of those effects have been applied. If multiple dependent
 * effects would apply simultaneously in this way, they're applied in timestamp
 * order relative to each other. If several dependent effects form a dependency
 * loop, then this rule is ignored and the effects in the dependency loop are
 * applied in timestamp order.
 * 
 * 613.7c After each effect is applied, the order of remaining effects is
 * reevaluated and may change if an effect that has not yet been applied becomes
 * dependent on or independent of one or more other effects that have not yet
 * been applied.
 */
public class DependencyCalculator
{
	private Map<ContinuousEffect, Set<ContinuousEffect>> calculatedDependencies;
	private Comparator<ContinuousEffect> comparator;
	private Map<ContinuousEffect, Collection<ContinuousEffect>> ignoredEffects;

	private GameState stateX;
	private GameState stateXY;
	private GameState stateY;

	public DependencyCalculator(Comparator<ContinuousEffect> comparator)
	{
		this.calculatedDependencies = null;
		this.comparator = comparator;
		this.ignoredEffects = new HashMap<ContinuousEffect, Collection<ContinuousEffect>>();

		this.stateX = null;
		this.stateXY = null;
		this.stateY = null;
	}

	public void applyEffectsInOrder(Map<ContinuousEffect, Identified> effects, GameState state, ContinuousEffectType.Layer layer)
	{
		if(effects.isEmpty())
			return;

		SortedMap<ContinuousEffect, Identified> ordered = new TreeMap<ContinuousEffect, Identified>(this.comparator);
		for(Map.Entry<ContinuousEffect, Identified> e: effects.entrySet())
			if(e.getKey().appliesIn(layer))
				ordered.put(e.getKey(), e.getValue());

		while(!ordered.isEmpty())
		{
			ContinuousEffect next = this.getNext(layer, ordered);
			state.copyForEditing(next).apply(layer, ordered.get(next));
			ordered.remove(next);
		}
	}

	public void applyEffectsInOrder(Map<ContinuousEffect, Identified> effects, GameState state, ContinuousEffectType.SubLayer subLayer)
	{
		SortedMap<ContinuousEffect, Identified> ordered = new TreeMap<ContinuousEffect, Identified>(this.comparator);
		for(Map.Entry<ContinuousEffect, Identified> e: effects.entrySet())
			if(e.getKey().appliesIn(subLayer))
				ordered.put(e.getKey(), e.getValue());

		while(!ordered.isEmpty())
		{
			ContinuousEffect next = this.getNext(subLayer, ordered);
			state.copyForEditing(next).apply(subLayer, ordered.get(next));
			ordered.remove(next);
		}
	}

	private Set<ContinuousEffect> getDependenciesFor(ContinuousEffectType.Layer layer, ContinuousEffectType.SubLayer sublayer, ContinuousEffect effectY, Map<ContinuousEffect, Identified> effects)
	{
		if(this.calculatedDependencies.containsKey(effectY))
			return this.calculatedDependencies.get(effectY);

		Set<ContinuousEffect> ret = new HashSet<ContinuousEffect>();
		ContinuousEffect.Part partY = sublayer == null ? effectY.partInLayer(layer) : effectY.partInSubLayer(sublayer);
		Identified sourceOfY = effects.get(effectY);
		boolean YcanApplyBefore = effectY.canApply();

		for(ContinuousEffect effectX: effects.keySet())
		{
			if(effectX == effectY)
				continue;

			ContinuousEffect.Part partX = sublayer == null ? effectX.partInLayer(layer) : effectX.partInSubLayer(sublayer);
			if(partX == null)
				continue;

			Identified sourceOfX = effects.get(effectX);

			if(this.stateX == null)
				this.stateX = effectY.game.actualState.clone(false);

			boolean rightCanApplyBefore = effectX.canApply();
			if(rightCanApplyBefore && checkForDependency(partY, effectY, sourceOfY, YcanApplyBefore, partX, effectX, sourceOfX, effectY.game.actualState))
				ret.add(effectX);
		}

		this.calculatedDependencies.put(effectY, ret);

		return ret;
	}

	public ContinuousEffect getNext(ContinuousEffectType.Layer layer, SortedMap<ContinuousEffect, Identified> effects)
	{
		this.calculatedDependencies = new HashMap<ContinuousEffect, Set<ContinuousEffect>>();
		ContinuousEffect ret = getNextAux(layer, null, effects);
		this.stateX = null;
		this.stateY = null;
		this.stateXY = null;
		this.calculatedDependencies = null;
		return ret;
	}

	public ContinuousEffect getNext(ContinuousEffectType.SubLayer sublayer, SortedMap<ContinuousEffect, Identified> effects)
	{
		this.calculatedDependencies = new HashMap<ContinuousEffect, Set<ContinuousEffect>>();
		ContinuousEffect ret = getNextAux(null, sublayer, effects);
		this.stateX = null;
		this.stateY = null;
		this.stateXY = null;
		this.calculatedDependencies = null;
		return ret;
	}

	private ContinuousEffect getNextAux(ContinuousEffectType.Layer layer, ContinuousEffectType.SubLayer sublayer, SortedMap<ContinuousEffect, Identified> effects)
	{
		for(ContinuousEffect effect: effects.keySet())
		{
			if((sublayer == null && !effect.appliesIn(layer)) || (sublayer != null && !effect.appliesIn(sublayer)))
				continue;

			Set<ContinuousEffect> dependencies = new HashSet<ContinuousEffect>(getDependenciesFor(layer, sublayer, effect, effects));

			if(this.ignoredEffects.containsKey(effect))
				dependencies.removeAll(this.ignoredEffects.get(effect));

			if(dependencies.isEmpty())
				return effect;

			List<ContinuousEffect> currentPath = new LinkedList<ContinuousEffect>();
			currentPath.add(effect);
			Set<List<ContinuousEffect>> loops = checkForDependencyLoops(layer, sublayer, currentPath, effects);

			for(List<ContinuousEffect> loop: loops)
			{
				dependencies.removeAll(loop);

				ContinuousEffect depender = null;
				for(ContinuousEffect dependee: loop)
				{
					if(depender != null)
					{
						if(!this.ignoredEffects.containsKey(depender))
							this.ignoredEffects.put(depender, new HashSet<ContinuousEffect>());
						this.ignoredEffects.get(depender).add(dependee);
					}

					depender = dependee;
				}
			}

			if(dependencies.isEmpty())
				return effect;
		}

		throw new RuntimeException("I wasn't expecting this... go away...");
	}

	public Set<List<ContinuousEffect>> checkForDependencyLoops(ContinuousEffectType.Layer layer, ContinuousEffectType.SubLayer sublayer, List<ContinuousEffect> currentPath, Map<ContinuousEffect, Identified> effects)
	{
		Set<List<ContinuousEffect>> loops = new HashSet<List<ContinuousEffect>>();

		ContinuousEffect effect = currentPath.get(currentPath.size() - 1);

		Set<ContinuousEffect> dependencies = getDependenciesFor(layer, sublayer, effect, effects);

		for(ContinuousEffect dependency: dependencies)
		{
			int index = currentPath.indexOf(dependency);
			if(index > -1)
			{
				if(index == 0)
					loops.add(new LinkedList<ContinuousEffect>(currentPath));
			}
			else
			{
				currentPath.add(dependency);
				loops.addAll(checkForDependencyLoops(layer, sublayer, currentPath, effects));
				currentPath.remove(dependency);
			}
		}

		return loops;
	}

	/**
	 * @return Whether effect Y is dependent on effect X.
	 */
	private boolean checkForDependency(ContinuousEffect.Part partY, ContinuousEffect effectY, Identified sourceOfY, boolean YCanApplyBefore, ContinuousEffect.Part partX, ContinuousEffect effectX, Identified sourceOfX, GameState originalState)
	{
		GameObject sourceOfXinStateX = sourceOfX == null ? null : this.stateX.getByIDObject(sourceOfX.ID);
		ContinuousEffect effectXinStateX = this.stateX.get(effectX.ID);
		MagicSet objectsAffectedByEffectXinStateX = partX.apply(this.stateX, sourceOfXinStateX, effectXinStateX);

		// check whether the effect still exists after the other is
		// applied
		boolean effectYexistsAfterEffectX = false;
		// if it's floating, it exists
		if(effectY instanceof FloatingContinuousEffect)
			effectYexistsAfterEffectX = true;
		// if it's from a static ability, it exists if the ability does
		else if(this.stateX.containsIdentified(effectY.staticSourceID))
			effectYexistsAfterEffectX = true;
		// otherwise it doesn't exist anymore

		// 613.7a An effect is said to "depend on" another if ... applying the
		// other would change ... the existence of the first effect
		if(!effectYexistsAfterEffectX && effectY.game.actualState.containsIdentified(effectY.staticSourceID))
		{
			revertAffectedObjects(objectsAffectedByEffectXinStateX, originalState, this.stateX);
			return true;
		}

		// 613.7a An effect is said to "depend on" another if ... applying the
		// other would change the text ... of the first effect, what it applies
		// to, or what it does to any of the things it applies to
		boolean YCanApplyAfter = this.stateX.<ContinuousEffect>get(effectY.ID).canApply();
		if(YCanApplyBefore != YCanApplyAfter)
		{
			revertAffectedObjects(objectsAffectedByEffectXinStateX, originalState, this.stateX);
			return true;
		}

		// If Y still can't apply, there isn't a dependency
		if(!YCanApplyBefore && !YCanApplyAfter)
		{
			revertAffectedObjects(objectsAffectedByEffectXinStateX, originalState, this.stateX);
			return false;
		}

		// Check what it applies to.
		GameObject sourceOfYinStateX = sourceOfY == null ? null : this.stateX.getByIDObject(sourceOfY.ID);
		SetGenerator affectedByEffectY = partY.parameters.get(partY.type.affects());

		MagicSet affectedByYinOriginalState = affectedByEffectY.evaluate(originalState, sourceOfY);
		MagicSet affectedByYinStateX = affectedByEffectY.evaluate(this.stateX, sourceOfYinStateX);
		if(!affectedByYinOriginalState.equals(affectedByYinStateX))
		{
			revertAffectedObjects(objectsAffectedByEffectXinStateX, originalState, this.stateX);
			return true;
		}

		// If we've gotten this far, that means we need to compare how applying
		// the second effect changes what the first effect "does to" any of the
		// things it applies to. This means we actually need four states -- one
		// where the object is unmodified, one where only the first effect has
		// applied, one where only the second effect has applied, and one where
		// the second then the first were applied in that order.

		// Check what it "does to" any of the things it applies to.
		// In all the layers we care about, it's all GameObjects. Each
		// GameObject will be represented in both sets (since otherwise the
		// previous if-block would have already caused a return from this
		// function).

		// --- ORIGINAL STATE ---
		List<GameObject> freshInOrder = new ArrayList<GameObject>(affectedByYinOriginalState.getAll(GameObject.class));
		Collections.sort(freshInOrder);

		// --- STATE WHERE ONLY X HAS APPLIED ---
		List<GameObject> afterXinOrder = new ArrayList<GameObject>(affectedByYinStateX.getAll(GameObject.class));
		Collections.sort(afterXinOrder);

		// --- STATE WHERE ONLY Y HAS APPLIED ---
		if(this.stateY == null)
			this.stateY = originalState.clone(false);
		GameObject sourceOfYinStateY = this.stateY.<GameObject>get(sourceOfY.ID);
		MagicSet objectsAffectedByEffectYinStateY = partY.apply(this.stateY, sourceOfYinStateY, this.stateY.<ContinuousEffect>get(effectY.ID));

		MagicSet evalAfterY = affectedByEffectY.evaluate(this.stateY, sourceOfYinStateY);
		List<GameObject> afterYinOrder = new ArrayList<GameObject>(evalAfterY.getAll(GameObject.class));
		Collections.sort(afterYinOrder);

		// --- STATE WHERE X THEN Y HAVE APPLIED ---
		if(this.stateXY == null)
			this.stateXY = originalState.clone(false);

		GameObject sourceOfXinStateXY = this.stateXY.<GameObject>get(sourceOfX.ID);
		MagicSet objectsAffectedByEffectXinStateXY = partX.apply(this.stateXY, sourceOfXinStateXY, this.stateXY.<ContinuousEffect>get(effectX.ID));

		GameObject sourceOfYinStateXY = this.stateXY.<GameObject>get(sourceOfY.ID);
		MagicSet objectsAffectedByEffectYinStateXY = partY.apply(this.stateXY, sourceOfYinStateXY, this.stateXY.<ContinuousEffect>get(effectY.ID));

		MagicSet evalAfterBoth = affectedByEffectY.evaluate(this.stateXY, sourceOfYinStateXY);
		List<GameObject> afterBothInOrder = new ArrayList<GameObject>(evalAfterBoth.getAll(GameObject.class));
		Collections.sort(afterBothInOrder);

		ContinuousEffectType.Layer layer = partY.type.layer();
		ContinuousEffectType.SubLayer sublayer = partY.type.subLayer();
		Iterator<GameObject> freshIt = freshInOrder.iterator();
		Iterator<GameObject> afterYit = afterYinOrder.iterator();
		Iterator<GameObject> afterXit = afterXinOrder.iterator();
		Iterator<GameObject> afterBothIt = afterBothInOrder.iterator();
		try
		{
			while(true)
			{
				GameObject untouched = freshIt.next();
				GameObject afterX = afterXit.next();
				GameObject afterY = afterYit.next();
				GameObject afterXandY = afterBothIt.next();
				while(!untouched.equals(afterY))
					afterY = afterYit.next();
				while(!afterX.equals(afterXandY))
					afterXandY = afterBothIt.next();

				boolean dependency = false;
				if(layer == ContinuousEffectType.Layer.POWER_AND_TOUGHNESS)
				{
					dependency = sublayer.objectsIndicateDependency(untouched, afterX, afterY, afterXandY);
				}
				else
				{
					dependency = layer.objectsIndicateDependency(untouched, afterX, afterY, afterXandY);
				}

				if(dependency)
				{
					revertAffectedObjects(objectsAffectedByEffectXinStateX, originalState, this.stateX);
					revertAffectedObjects(objectsAffectedByEffectYinStateY, originalState, this.stateY);
					revertAffectedObjects(objectsAffectedByEffectYinStateXY, originalState, this.stateXY, true);
					revertAffectedObjects(objectsAffectedByEffectXinStateXY, originalState, this.stateXY);

					return true;
				}
			}
		}
		catch(NoSuchElementException e)
		{
			// intentionally left blank
		}

		revertAffectedObjects(objectsAffectedByEffectXinStateX, originalState, this.stateX);
		revertAffectedObjects(objectsAffectedByEffectYinStateY, originalState, this.stateY);
		revertAffectedObjects(objectsAffectedByEffectYinStateXY, originalState, this.stateXY, true);
		revertAffectedObjects(objectsAffectedByEffectXinStateXY, originalState, this.stateXY);
		return false;
	}

	private static void revertAffectedObjects(MagicSet affectedObjects, GameState originalState, GameState fakeState)
	{
		revertAffectedObjects(affectedObjects, originalState, fakeState, false);
	}

	private static void revertAffectedObjects(MagicSet affectedObjects, GameState originalState, GameState fakeState, boolean intermediate)
	{
		if(affectedObjects != null)
		{
			for(Identified i: affectedObjects.getAll(Identified.class))
			{
				fakeState.removeIdentified(i.ID);
				if(intermediate && !originalState.containsIdentified(i.ID))
					continue;
				originalState.<Identified>get(i.ID).clone(fakeState);
			}
			ContinuousEffectType.RemovedObjects extraObjects = affectedObjects.getOne(ContinuousEffectType.RemovedObjects.class);
			if(extraObjects != null)
				revertAffectedObjects(extraObjects, originalState, fakeState, intermediate);
		}
	}
}