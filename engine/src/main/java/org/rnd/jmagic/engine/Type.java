package org.rnd.jmagic.engine;

import java.util.EnumSet;
import java.util.Set;

/** Represents the different card types. */
public enum Type
{
	// Order is important here!
	TRIBAL
	{
		@Override
		public String toString()
		{
			return "Tribal";
		}
	},
	PLANESWALKER
	{
		@Override
		public String toString()
		{
			return "Planeswalker";
		}
	},
	ARTIFACT
	{
		@Override
		public String toString()
		{
			return "Artifact";
		}
	},
	ENCHANTMENT
	{
		@Override
		public String toString()
		{
			return "Enchantment";
		}
	},
	LAND
	{
		@Override
		public String toString()
		{
			return "Land";
		}
	},
	CREATURE
	{
		@Override
		public String toString()
		{
			return "Creature";
		}
	},
	INSTANT
	{
		@Override
		public String toString()
		{
			return "Instant";
		}
	},
	SORCERY
	{
		@Override
		public String toString()
		{
			return "Sorcery";
		}
	},
	PLANE
	{
		@Override
		public boolean isTraditional()
		{
			return false;
		}

		@Override
		public String toString()
		{
			return "Plane";
		}
	};

	public static Set<Type> permanentTypes()
	{
		return EnumSet.of(ARTIFACT, ENCHANTMENT, LAND, CREATURE, PLANESWALKER);
	}

	public boolean isTraditional()
	{
		return true;
	}
}
