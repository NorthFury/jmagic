package org.rnd.jmagic.cardscript;

import org.rnd.jmagic.*;
import org.rnd.jmagic.engine.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ObjectFramework
{
	/* Generic Properties */
	public String name = null;
	public String manaCost = null;
	public List<ModeFramework> modes = new LinkedList<ModeFramework>();
	public String objectType;
	public boolean isCard;

	/* Used to represent existing classes (keywords) */
	public Class<? extends Identified> clazz = null;
	public List<Object> parameters = new LinkedList<Object>();

	/* Card Properties */
	public Set<SuperType> superTypes = EnumSet.noneOf(SuperType.class);
	public Set<Type> types = EnumSet.noneOf(Type.class);
	public Set<SubType> subTypes = EnumSet.noneOf(SubType.class);
	public Map<Expansion, Rarity> printings = new TreeMap<Expansion, Rarity>();
	public List<ObjectFramework> abilities = new LinkedList<ObjectFramework>();
	public Integer power = null;
	public Integer toughness = null;

	/* Activated Ability Properties */
	public boolean costsTap = false;

	/* Triggered Ability Properties */
	public List<EventFramework> eventPatterns = new LinkedList<EventFramework>();

	public ObjectFramework(String type)
	{
		this.objectType = type;
		this.isCard = type.equals("Card");
	}

	public void printToStream(BufferedWriter out, int indentLevel) throws IOException
	{
		String className = CardLoader.formatName(this.name);

		String newline = "\r\n";
		String indent = newline;
		for(int i = 0; i < indentLevel; ++i)
			indent += "\t";

		if(this.isCard)
		{
			out.write(indent + "@Name(\"" + this.name + "\")");

			if(!this.superTypes.isEmpty())
			{
				out.write(indent + "@SuperTypes({");
				boolean first = true;
				for(SuperType type: this.superTypes)
				{
					if(first)
						first = false;
					else
						out.write(",");
					out.write("SuperType." + type.name());
				}
				out.write("})");
			}

			if(!this.types.isEmpty())
			{
				out.write(indent + "@Types({");
				boolean first = true;
				for(Type type: this.types)
				{
					if(first)
						first = false;
					else
						out.write(",");
					out.write("Type." + type.name());
				}
				out.write("})");
			}

			if(!this.subTypes.isEmpty())
			{
				out.write(indent + "@SubTypes({");
				boolean first = true;
				for(SubType type: this.subTypes)
				{
					if(first)
						first = false;
					else
						out.write(",");
					out.write("SubType." + type.name());
				}
				out.write("})");
			}

			if(this.manaCost != null)
			{
				out.write(indent + "@ManaCost(\"" + this.manaCost + "\")");
			}

			if(!this.printings.isEmpty())
			{
				out.write(indent + "@Printings({ ");
				boolean first = true;
				for(Map.Entry<Expansion, Rarity> entry: this.printings.entrySet())
				{
					if(!first)
						out.write(", ");
					else
						first = false;
					out.write("@Printings.Printed(ex = Expansion." + entry.getKey().name() + ", r = Rarity." + entry.getValue().name() + ")");
				}
				out.write(" })");
			}
		}

		out.write(indent + "public " + (indentLevel > 0 ? "static " : "") + "final class " + className + " extends " + this.objectType + (this.superTypes.contains(SuperType.BASIC) ? " implements AnyNumberInADeck" : "") + indent + "{");

		for(ObjectFramework ability: this.abilities)
			if(ability.clazz == null)
				ability.printToStream(out, indentLevel + 1);

		out.write(indent + "\tpublic " + className + "(GameState state)");
		out.write(indent + "\t{");
		out.write(indent + "\t\tsuper(state" + (this.isCard ? "" : ", \"" + this.name + "\"") + ");");

		if(!this.isCard && this.manaCost != null)
			out.write(newline + indent + "\t\tthis.setManaCost(new ManaPool(\"" + this.manaCost + "\"));");

		if(!this.eventPatterns.isEmpty())
		{
			int i = 0;
			for(EventFramework event: this.eventPatterns)
			{
				out.write(newline + indent + "\t\tSimpleEventPattern pattern" + i + " = new SimpleEventPattern(EventType." + event.type.toString() + ");");
				for(Map.Entry<EventType.Parameter, String> entry: event.parameters.entrySet())
					if(entry.getValue() != null)
						out.write(indent + "\t\tpattern" + i + ".put(EventType.Parameter." + entry.getKey().name() + ", " + entry.getValue() + ");");

				out.write(indent + "\t\tthis.addPattern(pattern" + i + ");");
				++i;
			}
		}

		if(this.power != null)
		{
			out.write(newline + indent + "\t\tthis.power = " + this.power + ";");
			out.write(indent + "\t\tthis.toughness = " + this.toughness + ";");
		}

		for(int modeNum = 0; modeNum < this.modes.size(); ++modeNum)
		{
			ModeFramework mode = this.modes.get(modeNum);
			if(!mode.targets.isEmpty())
			{
				out.write(newline);
				for(TargetFramework target: mode.targets)
				{
					out.write(indent + "\t\tTarget " + target.name + " = this.addTarget(" + (modeNum + 1) + ", " + target.generator + ", \"" + target.name + "\");");
					if(target.min != null)
						out.write(indent + "\t\t" + target.name + ".setNumber(" + target.min + ", " + (target.max == null ? "null" : target.max.intValue()) + ");" + newline);
					else if(target.max != null)
						throw new RuntimeException("really wasn't sure what to do here.  card example?");
				}
			}
		}

		for(ObjectFramework ability: this.abilities)
		{
			out.write(indent + "\t\tthis.addAbility(new ");
			if(ability.clazz == null)
				out.write(CardLoader.formatName(ability.name));
			else
				out.write(ability.clazz.getName().replace('$', '.'));

			out.write("(state");

			for(Object object: ability.parameters)
				out.write(", " + object.toString());
			out.write("));" + newline);
		}

		int i = 0;
		for(int modeNum = 0; modeNum < this.modes.size(); ++modeNum)
		{
			ModeFramework mode = this.modes.get(modeNum);
			for(Object text: mode.textbox)
			{
				out.write(newline);
				if(text instanceof EventFramework)
				{
					String name = "factory" + i;
					EventFramework event = (EventFramework)text;
					out.write(indent + "\t\tEventFactory " + name + " = new EventFactory(EventType." + event.type.toString() + ", \"TODO: figure out how to do event names\");");
					for(Map.Entry<EventType.Parameter, String> entry: event.parameters.entrySet())
						out.write(indent + "\t\t" + name + ".parameters.put(EventType.Parameter." + entry.getKey().name() + ", " + entry.getValue() + ");");
					out.write(indent + "\t\tthis.addEffect(" + (modeNum + 1) + ", " + name + ");");
				}
				else
				{
					throw new RuntimeException(text.getClass().getName() + " isn't handled when printing a textbox.");
				}
				++i;
			}
		}

		out.write(indent + "\t}");
		out.write(indent + "}" + newline);
	}

	public void writeCard()
	{
		String className = CardLoader.formatName(this.name);
		File card = new File("src\\org\\rnd\\jmagic\\cards\\generated\\" + className + ".java");
		if(card.exists())
			return;

		BufferedWriter out = null;
		try
		{
			out = new BufferedWriter(new FileWriter(card));
			out.write("package org.rnd.jmagic.cards.generated;\r\n\r\n");
			out.write("import static org.rnd.jmagic.Convenience.*;\r\n");
			out.write("import org.rnd.jmagic.engine.*;\r\n");
			out.write("import org.rnd.jmagic.engine.patterns.*;\r\n");
			out.write("import org.rnd.jmagic.engine.generators.*;\r\n\r\n");

			this.printToStream(out, 0);
			out.flush();
			out.close();
		}
		catch(IOException e)
		{
			if(out != null)
			{
				try
				{
					out.flush();
					out.close();
				}
				catch(IOException wehateyoutoojava)
				{
					// Just go back to the other catch block
				}
			}
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
