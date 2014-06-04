package org.rnd.jmagic.gui;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.jmagic.sanitized.*;
import org.rnd.util.NumberNames;
import org.rnd.util.SeparatedList;

import javax.swing.ImageIcon;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.lang.String;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JMagicTextPane extends JTextPane
{
	private static final long serialVersionUID = 1L;

	private final boolean largeIcons;

	public JMagicTextPane()
	{
		this(false);
	}

	public JMagicTextPane(boolean largeIcons)
	{
		this.largeIcons = largeIcons;
		this.setBorder(new EmptyBorder(0, 0, 0, 0));
		this.setEditable(false);

		DefaultCaret caret = (DefaultCaret)(this.getCaret());
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
	}

	public static String getModeChoiceText(MagicSet number)
	{
		Integer lower = Minimum.get(number);
		Integer upper = Maximum.get(number);
		if(null == lower)
			lower = 1;

		if(lower.equals(upper))
			return "Choose " + NumberNames.get(lower);
		else if(upper == null)
			return "Choose " + NumberNames.get(lower) + " or more";
		else if(lower == 1 && upper == 2)
			return "Choose one or both";
		else
			return "Choose between " + NumberNames.get(lower) + " and " + NumberNames.get(upper);
	}

	@Override
	public boolean isOpaque()
	{
		return false;
	}

	@Override
	public void setText(String text)
	{
		this.setText(text, null);
	}

	public void setText(String text, Map<Integer, Integer> bolds)
	{
		super.setText(text);

		StyledDocument document = this.getStyledDocument();
		Map<String, Style> styles = new HashMap<String, Style>();

		// Use the style from the first position in the text before any styles
		// have been applied
		Style defaultStyle = document.getLogicalStyle(0);
		Style halfSize = document.addStyle("half-size", defaultStyle);
		StyleConstants.setFontSize(halfSize, StyleConstants.getFontSize(defaultStyle) / 2);

		int leftParenthesis = text.indexOf('(');
		while(-1 != leftParenthesis)
		{
			int rightParenthesis = text.indexOf(')', leftParenthesis);
			String symbol = text.substring(leftParenthesis, rightParenthesis + 1);

			Style style;
			if(styles.containsKey(symbol))
				style = styles.get(symbol);
			else
			{
				style = document.addStyle(symbol, null);
				StyleConstants.setIcon(style, new ImageIcon(CardGraphics.getIcon(symbol, !this.largeIcons)));
				styles.put(symbol, style);
			}

			document.setCharacterAttributes(leftParenthesis, rightParenthesis - leftParenthesis + 1, style, true);
			leftParenthesis = text.indexOf('(', leftParenthesis + 1);
		}

		if(null != bolds)
		{
			for(Map.Entry<Integer, Integer> e: bolds.entrySet())
			{
				Style boldStyle = document.addStyle("bold", null);
				StyleConstants.setBold(boldStyle, true);
				document.setCharacterAttributes(e.getKey(), e.getValue() - e.getKey() + 1, boldStyle, false);
			}
		}

		int doubleLineBreak = text.indexOf("\n\n");
		while(-1 != doubleLineBreak)
		{
			document.setCharacterAttributes(doubleLineBreak, 2, halfSize, true);
			doubleLineBreak = text.indexOf("\n\n", doubleLineBreak + 1);
		}
	}

	public void setText(SanitizedGameObject o, SanitizedGameState state, SanitizedGameObject.CharacteristicSet displayOption)
	{
		StringBuilder textBuilder = new StringBuilder();
		boolean firstLine = true;

		if(o.counters.size() > 0)
		{
			Map<Counter.CounterType, Integer> counterQuantities = new HashMap<Counter.CounterType, Integer>();
			for(Counter counter: o.counters)
			{
				if(counterQuantities.containsKey(counter.getType()))
					counterQuantities.put(counter.getType(), counterQuantities.get(counter.getType()) + 1);
				else
					counterQuantities.put(counter.getType(), 1);
			}

			// Use the EnumSet to order the output consistently
			for(Map.Entry<Counter.CounterType, Integer> counterQuantity: counterQuantities.entrySet())
			{
				Counter.CounterType type = counterQuantity.getKey();
				Integer count = counterQuantity.getValue();
				textBuilder.append((firstLine ? "" : "\n\n") + count + " " + type + (count == 1 ? "" : "s"));
				firstLine = false;
			}
		}

		if(o.zoneID == state.stack && o.valueOfX != -1)
		{
			textBuilder.append((firstLine ? "" : "\n\n") + "X is " + o.valueOfX + ".");
			firstLine = false;
		}

		if(!o.otherLinks.isEmpty())
		{
			textBuilder.append((firstLine ? "" : "\n\n") + o.otherLinks);
			firstLine = false;
		}

		SanitizedCharacteristics c = o.characteristics.get(displayOption);

		if(o instanceof SanitizedActivatedAbility)
		{
			SanitizedActivatedAbility a = (SanitizedActivatedAbility)o;
			if(!firstLine)
				textBuilder.append("\n\n");

			boolean firstCost = true;
			// Activated abilities always have mana costs, so don't bother with
			// the null check
			if(0 != c.manaCost.size())
			{
				textBuilder.append(c.manaCost);
				firstCost = false;
			}

			if(a.costsTap)
			{
				if(!firstCost)
					textBuilder.append(", ");
				textBuilder.append("(T)");
				firstCost = false;
			}

			if(a.costsUntap)
			{
				if(!firstCost)
					textBuilder.append(", ");
				textBuilder.append("(U)");
				firstCost = false;
			}

			for(String cost: c.costs)
			{
				if(!firstCost)
					textBuilder.append(", ");
				textBuilder.append(cost);
				firstCost = false;
			}

			if(firstCost && (0 == c.manaCost.size()))
			{
				textBuilder.append(c.manaCost);
				firstCost = false;
			}

			textBuilder.append(": ");
			// Leave firstLine true since we don't want the effects that make up
			// the ability to break any lines
		}
		else if(0 != c.costs.size())
		{
			if(!firstLine)
				textBuilder.append("\n\n");
			textBuilder.append("As an additional cost to cast " + o.name + ",");

			for(String cost: c.costs)
				textBuilder.append(" " + cost);

			textBuilder.append(".");
			firstLine = false;
		}

		Map<Integer, Integer> bolds = new HashMap<Integer, Integer>();
		List<String> keywordStrings = new LinkedList<String>();
		abilityLoop: for(int abilityID: c.abilities)
		{
			// -1 is a marker for where on the card the modes with effects are
			if(abilityID == -1)
			{
				if(!firstLine)
					textBuilder.append("\n\n");
				firstLine = false;

				String betweenModes = "\n\n";
				String preceedingFinalMode = "";
				if(c.modes.size() != 1 && c.modes.size() != c.selectedModes.size())
				{
					textBuilder.append(getModeChoiceText(c.numModes) + " \u2014 ");

					Integer maximum = Maximum.get(c.numModes);
					if((null == maximum) || (maximum == c.modes.size()))
					{
						betweenModes = "; ";
						preceedingFinalMode = "; and/or ";
					}
					else
					{
						betweenModes = "; or ";
						preceedingFinalMode = "; or ";
					}
				}

				int modesCounted = 0;
				for(SanitizedMode mode: c.modes)
				{
					if(modesCounted != 0)
					{
						if(modesCounted == c.modes.size() - 1)
							textBuilder.append(preceedingFinalMode);
						else
							textBuilder.append(betweenModes);
					}

					StringBuffer effects = new StringBuffer();
					for(String effect: mode.effects)
					{
						if(!(effect.isEmpty()))
						{
							if(effects.length() != 0)
								effects.append(' ');
							effects.append(effect);
						}
					}

					int boldStart = -1;
					if(c.modes.size() > 1 && c.selectedModes.contains(mode))
						boldStart = textBuilder.length();
					textBuilder.append(effects);
					if(-1 != boldStart)
						bolds.put(boldStart, textBuilder.length());

					modesCounted++;
				}
				continue;
			}

			SanitizedIdentified ability = state.get(abilityID);
			if(ability.isKeyword)
			{
				if(keywordStrings.isEmpty())
					keywordStrings.add(ability.name);
				else
					keywordStrings.add(ability.name.substring(0, 1).toLowerCase() + ability.name.substring(1));
				continue abilityLoop;
			}

			if(!keywordStrings.isEmpty())
			{
				StringBuilder keywordText = SeparatedList.get("", keywordStrings);
				textBuilder.append((firstLine ? "" : "\n\n") + keywordText);
				firstLine = false;
				keywordStrings.clear();
			}
			textBuilder.append((firstLine ? "" : "\n\n") + ability.name);
			firstLine = false;
		}

		if(!keywordStrings.isEmpty())
		{
			StringBuilder keywordText = SeparatedList.get("", keywordStrings);
			textBuilder.append((firstLine ? "" : "\n\n") + keywordText);
			firstLine = false;
			keywordStrings.clear();
		}

		this.setText(textBuilder.toString().trim(), bolds);
	}
}
