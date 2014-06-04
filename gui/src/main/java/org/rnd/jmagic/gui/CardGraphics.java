package org.rnd.jmagic.gui;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.sanitized.*;
import org.rnd.util.Graphics2DAdapter;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.font.GraphicAttribute;
import java.awt.font.ImageGraphicAttribute;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

public class CardGraphics extends Graphics2DAdapter
{
	public static final Dimension COLOR_INDICATOR = new Dimension(11, 11);

	public static final Dimension LARGE_CARD = new Dimension(223, 310);

	private static final int LARGE_CARD_ART_LEFT = 20;

	private static final int LARGE_CARD_ART_TOP = 36;

	private static final int LARGE_CARD_ART_WIDTH = 183;

	private static final int LARGE_CARD_ART_HEIGHT = 135;

	private static final int LARGE_CARD_LIFE_RIGHT = 19;

	public static final int LARGE_CARD_MANA_TOP = 19;

	public static final int LARGE_CARD_NAME_TOP = 18;

	public static final int LARGE_CARD_PADDING_LEFT = 22;

	public static final int LARGE_CARD_PADDING_RIGHT = 22;

	private static final int LARGE_CARD_POISON_BOTTOM = 17;

	private static final int LARGE_CARD_PT_BOX_LEFT = 170;

	private static final int LARGE_CARD_PT_BOX_TOP = 275;

	private static final int LARGE_CARD_PT_TEXT_LEFT = 174;

	private static final int LARGE_CARD_PT_TEXT_TOP = 278;

	private static final Dimension LARGE_CARD_PT_DIMENSIONS = new Dimension(32, 14);

	public static final int LARGE_CARD_TEXT_BOX_HEIGHT = 82;

	public static final int LARGE_CARD_TEXT_BOX_TOP = 193;

	public static final int LARGE_CARD_TEXT_LINE_HEIGHT = 14;

	public static final int LARGE_CARD_TEXT_WIDTH = LARGE_CARD.width - LARGE_CARD_PADDING_LEFT - LARGE_CARD_PADDING_RIGHT;

	public static final int LARGE_CARD_TYPE_TOP = 175;

	public static final int LARGE_CARD_TOTAL_TEXT_HEIGHT = LARGE_CARD_PT_TEXT_TOP - LARGE_CARD_NAME_TOP;

	public static final Dimension LARGE_MANA_SYMBOL = new Dimension(12, 12);

	public static final java.awt.Color LIFE_TOTAL_COLOR = java.awt.Color.GREEN.darker().darker();

	private static final java.awt.Color POISON_COUNTER_COLOR = java.awt.Color.RED.darker();

	private static final int POISON_LIFE_PADDING = 1;

	public static final Dimension SMALL_CARD = new Dimension(94, 130);

	private static final int SMALL_CARD_ART_LEFT = 8;

	private static final int SMALL_CARD_ART_TOP = 16;

	private static final int SMALL_CARD_ART_WIDTH = 77;

	private static final int SMALL_CARD_ART_HEIGHT = 56;

	private static final int SMALL_CARD_DAMAGE_BOTTOM = 27;

	private static final int SMALL_CARD_DAMAGE_RIGHT = 13;

	public static final Dimension SMALL_CARD_PADDING = new Dimension(5, 40);

	public static final int SMALL_CARD_PADDING_LEFT = 10;

	public static final int SMALL_CARD_PADDING_RIGHT = 11;

	public static final int SMALL_CARD_PADDING_TOP = 17;

	public static final int SMALL_CARD_ICON_HEIGHT = 14;

	public static final int SMALL_CARD_ICON_WIDTH = 14;

	public static final int SMALL_CARD_ICON_PADDING = 1;

	private static final int SMALL_CARD_POISON_BOTTOM = 12;

	private static final int SMALL_CARD_PT_BOX_LEFT = 53;

	private static final int SMALL_CARD_PT_BOX_TOP = 106;

	private static final int SMALL_CARD_PT_TEXT_LEFT = 55;

	private static final int SMALL_CARD_PT_TEXT_TOP = 107;

	// The inside of the small PT box is the same size as the inside of the
	// large PT box
	private static final Dimension SMALL_CARD_PT_DIMENSIONS = LARGE_CARD_PT_DIMENSIONS;

	public static final int SMALL_CARD_TEXT_WIDTH = SMALL_CARD.width - SMALL_CARD_PADDING_LEFT - SMALL_CARD_PADDING_RIGHT;

	public static final int SMALL_CARD_TOTAL_TEXT_HEIGHT = 108;

	public static final Dimension SMALL_MANA_SYMBOL = new Dimension(9, 9);

	private static final Map<String, Image> imageCache = new HashMap<String, Image>();

	public static SanitizedGameObject.CharacteristicSet getLargeCardDisplayOption(MouseEvent e, Point smallCardStart, SanitizedGameObject hoveredCard, boolean flipped)
	{
		int options = 0;
		for(SanitizedGameObject.CharacteristicSet option: CardGraphics.getLargeCardDisplayOptions(hoveredCard))
		{
			++options;
			if(getSmallCardOptionRect(hoveredCard.tapped, flipped, smallCardStart, options).contains(e.getPoint()))
				return option;
		}
		return SanitizedGameObject.CharacteristicSet.ACTUAL;
	}

	private static Set<SanitizedGameObject.CharacteristicSet> getLargeCardDisplayOptions(SanitizedGameObject object)
	{
		Set<SanitizedGameObject.CharacteristicSet> ret = EnumSet.noneOf(SanitizedGameObject.CharacteristicSet.class);
		if(object.characteristics.containsKey(SanitizedGameObject.CharacteristicSet.PHYSICAL))
			ret.add(SanitizedGameObject.CharacteristicSet.PHYSICAL);
		if(!object.flipped && object.characteristics.containsKey(SanitizedGameObject.CharacteristicSet.FLIP))
			ret.add(SanitizedGameObject.CharacteristicSet.FLIP);
		if(!object.transformed && object.characteristics.containsKey(SanitizedGameObject.CharacteristicSet.BACK_FACE))
			ret.add(SanitizedGameObject.CharacteristicSet.BACK_FACE);
		return ret;
	}

	private static Rectangle getSmallCardOptionRect(boolean tapped, boolean flipped, Point smallCardStart, int optionsSoFar)
	{
		int x, y;
		if(flipped && tapped)
		{
			x = (int)(smallCardStart.getX()) + SMALL_CARD.height - ((SMALL_CARD_ICON_HEIGHT + SMALL_CARD_ICON_PADDING) * optionsSoFar) + SMALL_CARD_ICON_PADDING;
			y = (int)(smallCardStart.getY()) + SMALL_CARD.width - SMALL_CARD_ICON_WIDTH;
		}
		else if(flipped)
		{
			x = (int)(smallCardStart.getX()) + SMALL_CARD.width - SMALL_CARD_ICON_WIDTH;
			y = (int)(smallCardStart.getY()) + (SMALL_CARD_ICON_HEIGHT + SMALL_CARD_ICON_PADDING) * (optionsSoFar - 1);
		}
		else if(tapped)
		{
			x = (int)(smallCardStart.getX()) + (SMALL_CARD_ICON_HEIGHT + SMALL_CARD_ICON_PADDING) * (optionsSoFar - 1);
			y = (int)(smallCardStart.getY());
		}
		else
		{
			x = (int)(smallCardStart.getX());
			y = (int)(smallCardStart.getY()) + SMALL_CARD.height - (SMALL_CARD_ICON_HEIGHT + SMALL_CARD_ICON_PADDING) * optionsSoFar + SMALL_CARD_ICON_PADDING;
		}
		return new Rectangle(x, y, SMALL_CARD_ICON_WIDTH, SMALL_CARD_ICON_HEIGHT);
	}

	private static File cardArts = null;

	public static File getCardImageLocation()
	{
		return cardArts;
	}

	public static void setCardImageLocation(String location)
	{
		setCardImageLocation(new File(location));
	}

	public static void setCardImageLocation(File location)
	{
		if(location.exists() && location.isDirectory())
			cardArts = location;
		else
			cardArts = null;
	}

	public static AttributedString getAttributedString(String text, FontMetrics font, boolean replaceIcons)
	{
		Map<Integer, ImageGraphicAttribute> charReplacements = new HashMap<Integer, ImageGraphicAttribute>();

		if(replaceIcons)
			while(text.contains("("))
			{
				int index = text.indexOf('(');
				String replace = text.substring(index, text.indexOf(')') + 1);
				text = text.substring(0, index) + "X" + text.substring(index + replace.length());
				charReplacements.put(index, new ImageGraphicAttribute(getIcon(replace, true), GraphicAttribute.CENTER_BASELINE, 0f, CardGraphics.SMALL_MANA_SYMBOL.height / 2f));
			}

		AttributedString attrText = new AttributedString(text);

		attrText.addAttribute(TextAttribute.FONT, font.getFont());

		if(replaceIcons)
			for(Map.Entry<Integer, ImageGraphicAttribute> replacement: charReplacements.entrySet())
			{
				int index = replacement.getKey();
				attrText.addAttribute(TextAttribute.CHAR_REPLACEMENT, replacement.getValue(), index, replacement.getKey() + 1);
			}

		return attrText;
	}

	private static String getCardFrameString(SanitizedIdentified i, SanitizedGameObject.CharacteristicSet option)
	{
		if(null == i)
			return "back.png";
		if(null == option && i instanceof SanitizedGameObject && ((SanitizedGameObject)i).faceDown)
			return "back.png";

		if(i instanceof SanitizedPlayer)
			return "frame_ability.png";

		if(!(i instanceof SanitizedGameObject))
			return "frame_ability.png";

		SanitizedGameObject o = (SanitizedGameObject)i;
		if(o instanceof SanitizedNonStaticAbility || o.isEmblem)
			return "frame_ability.png";

		Set<Color> colors = null;

		SanitizedCharacteristics c = o.characteristics.get(option);

		if(c.types.contains(Type.LAND))
			colors = o.canProduce;
		else
			colors = c.colors;

		String colorString = getColorString(colors, false);

		StringBuffer baseFrame = new StringBuffer();
		if(c.types.contains(Type.LAND))
			baseFrame.append("l");
		else if(c.types.contains(Type.ARTIFACT))
			baseFrame.append("a");
		else if(colors.size() == 0)
			baseFrame.append("a");

		if(colors.size() > 2)
			baseFrame.append("m");
		else if(colors.size() > 1 && c.manaCost != null)
			for(ManaSymbol m: c.manaCost)
				if(m.colors.size() == 1)
				{
					baseFrame.append("m");
					break;
				}
		return "frame_" + baseFrame + colorString + ".png";
	}

	/**
	 * Returns a string representing the given colors. One lowercase letter per
	 * color, in the "right" order (i.e., wu, gw). If there are no colors, three
	 * colors, or four colors, returns the empty string. If fiveColor is false,
	 * also returns the empty string for five colors.
	 */
	private static String getColorString(Set<Color> colors, boolean fiveColor)
	{
		if(colors.size() == 0 || colors.size() == 3 || colors.size() == 4)
			return "";
		if(colors.size() == 1)
			return colors.iterator().next().getLetter().toLowerCase();
		else if(colors.size() == 2)
		{
			boolean w = colors.contains(Color.WHITE);
			boolean u = colors.contains(Color.BLUE);
			boolean b = colors.contains(Color.BLACK);
			boolean r = colors.contains(Color.RED);
			boolean g = colors.contains(Color.GREEN);

			if(w && u)
				return "wu";
			else if(u && b)
				return "ub";
			else if(b && r)
				return "br";
			else if(r && g)
				return "rg";
			else if(g && w)
				return "gw";
			else if(w && b)
				return "wb";
			else if(u && r)
				return "ur";
			else if(b && g)
				return "bg";
			else if(r && w)
				return "rw";
			else if(g && u)
				return "gu";
		}
		return fiveColor ? "wubrg" : "";
	}

	public static Image getIcon(String iconName, boolean little)
	{
		Image icon = null;

		String iconSize = "";
		if(little)
			iconSize = "little/";

		if(iconName.equals("(T)"))
		{
			icon = getImage("icons/" + iconSize + "t.png");
		}
		else if(iconName.equals("(Q)"))
		{
			icon = getImage("icons/" + iconSize + "q.png");
		}
		else if(iconName.equals("(C)"))
		{
			icon = getImage("icons/" + iconSize + "c.png");
		}
		// this has toUpperCase because most of the time gatherer's phyrexian
		// symbols have lowercase letters.
		else if(iconName.toUpperCase().equals("(P)"))
		{
			icon = getImage("icons/" + iconSize + "p.png");
		}
		else if(iconName.equals("(X)"))
		{
			icon = getImage("icons/" + iconSize + "x.png");
		}
		else
		{
			try
			{
				icon = getImage("icons/" + iconSize + getManaSymbolString(new ManaPool(iconName).iterator().next()) + ".png");
			}
			catch(NoSuchElementException e)
			{
				icon = null;
			}
		}

		if(icon == null)
			icon = getImage("noimage.png");

		return icon;
	}

	public static Image getImage(String name)
	{
		URL location = CardGraphics.class.getResource(name);
		if(null == location)
		{
			location = CardGraphics.class.getResource("noimage.png");
			if(null == location)
				throw new RuntimeException("Could not find resource \"noimage.png\"");
		}

		if(!imageCache.containsKey(name))
		{
			try
			{
				imageCache.put(name, ImageIO.read(location));
			}
			catch(IOException e)
			{
				throw new RuntimeException("Could not read image \"" + name + "\"");
			}
		}

		return imageCache.get(name);
	}

	public static Image getLargeCard(SanitizedIdentified object, SanitizedGameObject.CharacteristicSet option, SanitizedGameState state, Font font)
	{
		BufferedImage image = new BufferedImage(LARGE_CARD.width, LARGE_CARD.height, BufferedImage.TYPE_INT_RGB);

		CardGraphics cg = new CardGraphics(image, state, font);
		cg.drawLargeCard(object, option);

		return image;
	}

	private static String getManaSymbolString(ManaSymbol s)
	{
		if(s.isX)
			return "x";

		String manaString = "";

		if(0 < s.colorless)
			manaString += Integer.toString(s.colorless);

		switch(s.colors.size())
		{
		case 0:
			if(0 == s.colorless)
				return "0";
			break;
		case 1:
		case 2:
			manaString += getColorString(s.getColors(), false);
			break;
		case 5:
			manaString = "wubrg";
			break;
		default:
			manaString = "m"; // this should be impossible...
			break;
		}

		if(s.isPhyrexian)
			manaString += "p";

		return manaString;
	}

	public static Image getSmallCard(SanitizedIdentified object, SanitizedGameState state, boolean renderDamage, boolean renderCounters, Font font)
	{
		BufferedImage image = new BufferedImage(SMALL_CARD.width, SMALL_CARD.height, BufferedImage.TYPE_INT_ARGB);

		CardGraphics cg = new CardGraphics(image, state, font);
		cg.drawSmallCard(object, renderDamage, renderCounters);

		if(object instanceof SanitizedGameObject && ((SanitizedGameObject)object).ghost)
		{
			BufferedImage alphaImage = new BufferedImage(SMALL_CARD.width, SMALL_CARD.height, BufferedImage.TYPE_INT_ARGB);
			CardGraphics alphaCG = new CardGraphics(alphaImage, state, font);

			float[] scales = {1f, 1f, 1f, 0.5f};
			float[] offsets = new float[4];
			RescaleOp rop = new RescaleOp(scales, offsets, null);

			alphaCG.drawImage(image, rop, 0, 0);
			return alphaImage;
		}

		return image;
	}

	private static String getTypeString(SanitizedCharacteristics c)
	{
		StringBuilder ret = new StringBuilder();

		// localized to facilitate use of the map that will be in place later
		for(SuperType superType: c.superTypes)
			ret.append(superType + " ");

		for(Type type: c.types)
			ret.append(type + " ");

		List<Enum<?>> subTypes = new LinkedList<Enum<?>>();
		boolean allCreatureTypes = false;
		subTypes.addAll(c.subTypes);
		if(SubType.getSubTypesFor(Type.CREATURE, c.subTypes).size() == SubType.getAllCreatureTypes().size())
		{
			allCreatureTypes = true;
			subTypes.removeAll(SubType.getAllCreatureTypes());
		}
		if(subTypes.isEmpty() && !allCreatureTypes)
			return ret.toString().trim();

		ret.append('\u2014');
		if(allCreatureTypes)
			ret.append(" (all creature types)");
		for(Enum<?> subType: subTypes)
			ret.append(" " + subType.toString());
		return ret.toString();
	}

	public static Image renderTextAsLargeCard(String cardText, Font font)
	{
		cardText = cardText.substring(0, 1).toUpperCase() + cardText.substring(1);

		BufferedImage image = new BufferedImage(LARGE_CARD.width, LARGE_CARD.height, BufferedImage.TYPE_INT_RGB);

		CardGraphics cg = new CardGraphics(image, null, font);
		cg.drawImage(CardGraphics.getImage("largeframes/frame_ability.png"), 0, 0, null);

		cg.drawCardText(cardText, cg.getFont(), LARGE_CARD_PADDING_LEFT, LARGE_CARD_NAME_TOP, new Dimension(LARGE_CARD_TEXT_WIDTH, LARGE_CARD_TOTAL_TEXT_HEIGHT), false, true);

		return image;
	}

	public static Image renderTextAsSmallCard(String cardText, Font font)
	{
		cardText = cardText.substring(0, 1).toUpperCase() + cardText.substring(1);

		BufferedImage image = new BufferedImage(SMALL_CARD.width, SMALL_CARD.height, BufferedImage.TYPE_INT_RGB);

		CardGraphics g = new CardGraphics(image, null, font);
		g.drawImage(CardGraphics.getImage("smallframes/frame_ability.png"), 0, 0, null);

		int nameX = SMALL_CARD_PADDING_LEFT;
		int nameY = SMALL_CARD_PADDING_TOP;
		g.drawCardText(cardText, g.getFont(), nameX, nameY, new Dimension(SMALL_CARD_TEXT_WIDTH, SMALL_CARD_TOTAL_TEXT_HEIGHT), false, true);

		return image;
	}

	private SanitizedGameState state;

	private Stack<AffineTransform> transformStack;

	/**
	 * Construct a CardGraphics by casting a {@link Graphics} to a
	 * {@link Graphics2D} and keeping the result as the delegate.
	 * 
	 * @param graphics The {@link Graphics} to cast
	 * @throws ClassCastException If <code>graphics</code> is not a
	 * {@link Graphics2D} object
	 */
	public CardGraphics(Graphics graphics, SanitizedGameState state)
	{
		this((Graphics2D)graphics, state);
	}

	public CardGraphics(Graphics2D graphics, SanitizedGameState state)
	{
		super(graphics);
		this.state = state;
		this.transformStack = new Stack<AffineTransform>();
	}

	/**
	 * Constructs a new CardGraphics using the Graphics object from a buffered
	 * image, then sets the font of this object to the default font of the
	 * look-and-feel. This method exists because, since BufferedImages' graphics
	 * aren't usually used to draw text, they don't specify their fonts
	 * "correctly".
	 * 
	 * @param image The image whose graphics to create this CardGraphics from.
	 */
	public CardGraphics(BufferedImage image, SanitizedGameState state, Font font)
	{
		this(image.createGraphics(), state);
		this.setFont(font);
	}

	@Override
	public CardGraphics create()
	{
		return new CardGraphics(this, this.state);
	}

	public void drawArrow(Point source, Point target, java.awt.Color color, boolean borderOnly)
	{
		int dx = target.x - source.x;
		int dy = target.y - source.y;
		double distance = Math.sqrt((dx * dx) + (dy * dy));
		double angle = Math.atan(dy / (double)dx);
		if(dx < 0)
			angle += Math.PI;

		int width = 12;
		int headExtension = 10;
		int padding = 5;

		int headLength = width / 2 + headExtension;

		// If the arrow is less than twice the length of an arrowhead, shrink
		// everything so it's twice the length of an arrowhead.
		int arrowLength = 2 * padding + 2 * headLength;
		if(arrowLength > distance)
		{
			double factor = distance / arrowLength;

			width *= factor;
			headExtension *= factor;
			padding *= factor;
			headLength = width / 2 + headExtension;
		}

		Polygon arrow = new Polygon();

		arrow.addPoint(padding + headLength, width / 2);
		arrow.addPoint((int)distance - headLength - padding, width / 2);
		arrow.addPoint((int)distance - headLength - padding, width / 2 + headExtension);
		arrow.addPoint((int)distance - padding, 0);
		arrow.addPoint((int)distance - headLength - padding, -width / 2 - headExtension);
		arrow.addPoint((int)distance - headLength - padding, -width / 2);
		arrow.addPoint(padding + headLength, -width / 2);

		this.pushTransform();
		this.translate(source.x, source.y);
		this.rotate(angle);

		this.setColor(new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue(), 128));
		if(borderOnly)
			this.drawPolygon(arrow);
		else
			this.fillPolygon(arrow);

		this.popTransform();
	}

	/**
	 * Draws text.
	 * 
	 * @param text The text to draw.
	 * @param x The leftmost pixel of text.
	 * @param y The top of the text.
	 * @param color
	 */
	private void drawCardText(TextLayout text, int x, int y, java.awt.Color color)
	{
		java.awt.Color oldColor = this.getColor();
		this.setColor(color);
		text.draw(this, x, y + this.getFontMetrics().getAscent());
		this.setColor(oldColor);
	}

	/**
	 * Draws black card text to fit within a specific boundary. The text is
	 * drawn at the default size if it will fit; otherwise, the text is shrunk
	 * by half-point increments until it fits.
	 * 
	 * @param text The text to draw.
	 * @param font The font with which to render the text.
	 * @param x The leftmost pixel of text.
	 * @param y The top of the text.
	 * @param bound The boundary within which the text is to be drawn.
	 * @param centered Whether to horizontally and vertically center the text
	 * within the boundary.
	 * @param replaceIcons Whether to replace icons in the text (e.g., "(T)" or
	 * "(1)" or "(R)") with graphics.
	 */
	public void drawCardText(String text, Font font, int x, int y, Dimension bound, boolean centered, boolean replaceIcons)
	{
		this.drawCardText(text, font, x, y, bound, centered, replaceIcons, java.awt.Color.BLACK);
	}

	/**
	 * Draws card text to fit within a specific boundary. The text is drawn at
	 * the default size if it will fit; otherwise, the text is shrunk by
	 * half-point increments until it fits.
	 * 
	 * @param text The text to draw.
	 * @param font The font with which to render the text.
	 * @param x The leftmost pixel of text.
	 * @param y The top of the text.
	 * @param bound The boundary within which the text is to be drawn.
	 * @param centered Whether to horizontally and vertically center the text
	 * within the boundary.
	 * @param replaceIcons Whether to replace icons in the text (e.g., "(T)" or
	 * "(1)" or "(R)") with graphics.
	 * @param color What color to draw the text in.
	 */
	private void drawCardText(String text, Font font, int x, int y, Dimension bound, boolean centered, boolean replaceIcons, java.awt.Color color)
	{
		Font oldFont = this.getFont();
		this.setFont(font);
		FontMetrics metrics = this.getFontMetrics();
		int lineHeight = metrics.getHeight();
		List<String> paragraphs = new LinkedList<String>();

		while(text.contains("\n"))
		{
			int index = text.indexOf("\n");
			paragraphs.add(text.substring(0, index));
			text = text.substring(index + 1);
		}
		paragraphs.add(text);

		List<TextLayout> lines = new LinkedList<TextLayout>();

		boolean first = true;
		int height = 0;
		boolean tooBig = false;
		for(String paragraph: paragraphs)
		{
			if(first)
				first = false;
			else
			{
				lines.add(null);
				height += (lineHeight / 2);
			}

			AttributedCharacterIterator attrIter = CardGraphics.getAttributedString(paragraph, getFontMetrics(), replaceIcons).getIterator();
			LineBreakMeasurer lbm = new LineBreakMeasurer(attrIter, getFontRenderContext());

			int endIndex = attrIter.getEndIndex();
			while(lbm.getPosition() != endIndex)
			{
				TextLayout nextLayout = lbm.nextLayout(bound.width, endIndex, true);
				if(nextLayout == null)
				{
					tooBig = true;
					break;
				}
				height += lineHeight;
				if(height > bound.height)
				{
					tooBig = true;
					break;
				}
				lines.add(nextLayout);
			}
		}

		if(tooBig)
		{
			float newPoint = font.getSize2D() - 0.5f;
			if(newPoint <= 0)
				return;
			this.setFont(font.deriveFont(newPoint));

			this.drawCardText(text, font.deriveFont(newPoint), x, y, bound, centered, replaceIcons, color);
		}
		else
		{
			for(TextLayout line: lines)
			{
				if(null == line)
				{
					// Treat this as a paragraph break
					y += lineHeight / 2;
				}
				else
				{
					if(centered)
					{
						x += (int)((bound.width - line.getAdvance()) / 2);
						y += (int)((bound.height - line.getAscent() - line.getDescent()) / 2);
					}
					this.drawCardText(line, x, y, color);
					y += lineHeight;
				}
			}
		}
		this.setFont(oldFont);
	}

	/**
	 * Draws a single line of right-aligned text.
	 * 
	 * @param text The text to draw.
	 * @param x The x-coordinate of rightmost pixel of text.
	 * @param y The y-coordinate of the top of the text.
	 */
	private void drawCardTextRightAligned(String text, int x, int y, java.awt.Color color)
	{
		int textWidth = this.getFontMetrics().stringWidth(text);
		this.drawCardText(new TextLayout(CardGraphics.getAttributedString(text, getFontMetrics(), false).getIterator(), getFontRenderContext()), x - textWidth, y, color);
	}

	private void drawLargeCard(SanitizedIdentified i, SanitizedGameObject.CharacteristicSet option)
	{
		String cardFrameString = getCardFrameString(i, option);
		java.awt.Color textColor = cardFrameString.equals("back.png") ? java.awt.Color.WHITE : java.awt.Color.BLACK;
		this.drawImage(CardGraphics.getImage("largeframes/" + cardFrameString), 0, 0, null);

		if(null == i)
			return;

		if(i instanceof SanitizedPlayer)
		{
			SanitizedPlayer player = (SanitizedPlayer)i;
			StringBuilder text = new StringBuilder(i.name);

			text.append("\n");
			int cardsInHand = ((SanitizedZone)this.state.get(player.hand)).objects.size();
			text.append(cardsInHand);
			text.append(cardsInHand == 1 ? " card in hand" : " cards in hand");

			text.append("\n");
			int cardsInYard = ((SanitizedZone)this.state.get(player.graveyard)).objects.size();
			text.append(cardsInYard);
			text.append(cardsInYard == 1 ? " card in graveyard" : " cards in graveyard");

			text.append("\n");
			int cardsInLibrary = ((SanitizedZone)this.state.get(player.library)).objects.size();
			text.append(cardsInLibrary);
			text.append(cardsInLibrary == 1 ? " card in library" : " cards in library");

			if(player.nonPoisonCounters.size() > 0)
			{
				Map<Counter.CounterType, Integer> counterQuantities = new HashMap<Counter.CounterType, Integer>();
				for(Counter counter: player.nonPoisonCounters)
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
					text.append("\n" + count + " " + type + (count == 1 ? "" : "s"));
				}
			}

			for(int k: player.keywordAbilities)
			{
				text.append("\n");
				text.append(this.state.get(k));
			}

			for(int a: player.nonStaticAbilities)
			{
				text.append("\n");
				text.append(this.state.get(a));
			}

			this.drawCardText(text.toString(), getFont(), LARGE_CARD_PADDING_LEFT, LARGE_CARD_NAME_TOP, new Dimension(LARGE_CARD_TEXT_WIDTH, LARGE_CARD_TOTAL_TEXT_HEIGHT), false, true);

			FontMetrics f = getFontMetrics();
			int x = LARGE_CARD.width - LARGE_CARD_LIFE_RIGHT;
			int y = LARGE_CARD.height - LARGE_CARD_POISON_BOTTOM - f.getHeight();
			if(player.poisonCounters == 1)
				this.drawCardTextRightAligned("1 poison counter", x, y, POISON_COUNTER_COLOR);
			else if(player.poisonCounters > 1)
				this.drawCardTextRightAligned(Integer.toString(player.poisonCounters) + " poison counters", x, y, POISON_COUNTER_COLOR);
			y = y - POISON_LIFE_PADDING - f.getHeight();
			this.drawCardTextRightAligned(Integer.toString(player.lifeTotal) + " life", x, y, LIFE_TOTAL_COLOR);
			return;
		}

		SanitizedGameObject object = (SanitizedGameObject)i;
		SanitizedCharacteristics c = object.characteristics.get(option);
		boolean isAbility = i instanceof SanitizedNonStaticAbility;

		String name;
		Dimension nameDimensions;
		if(isAbility)
		{
			// The "short" name of an ability more represents what we want for
			// the name text
			name = ((SanitizedNonStaticAbility)i).shortName;
			// Allow names to wrap into the art box
			nameDimensions = new Dimension(LARGE_CARD_TEXT_WIDTH, LARGE_CARD_ART_TOP + LARGE_CARD_ART_HEIGHT);
		}
		else
		{
			name = c.name;
			nameDimensions = new Dimension(LARGE_CARD_TEXT_WIDTH, LARGE_CARD_TEXT_LINE_HEIGHT);
		}
		if(0 != name.length())
			this.drawCardText(name, getFont(), LARGE_CARD_PADDING_LEFT, LARGE_CARD_NAME_TOP, nameDimensions, false, false, textColor);

		if(!isAbility && (null != c.manaCost))
		{
			this.pushTransform();
			this.translate(LARGE_CARD.width - LARGE_MANA_SYMBOL.width - LARGE_CARD_PADDING_RIGHT, LARGE_CARD_MANA_TOP);
			for(ManaSymbol s: c.manaCost.getDisplayOrder())
			{
				this.drawImage(CardGraphics.getImage("icons/" + getManaSymbolString(s) + ".png"), 0, 0, null);
				this.translate(0 - LARGE_MANA_SYMBOL.width, 0);
			}
			this.popTransform();
		}

		Image art = getCardArt(c.name, true);
		if(art != null)
			this.drawImage(art, LARGE_CARD_ART_LEFT, LARGE_CARD_ART_TOP, LARGE_CARD_ART_WIDTH, LARGE_CARD_ART_HEIGHT, null);

		Dimension typeBound = new Dimension(LARGE_CARD_TEXT_WIDTH, LARGE_CARD_TEXT_LINE_HEIGHT);
		int typeLeft = LARGE_CARD_PADDING_LEFT;
		if(!c.colorIndicator.isEmpty())
		{
			Image icon = getImage("icons/" + getColorString(c.colorIndicator, true) + "Indicator.png");
			this.drawImage(icon, LARGE_CARD_PADDING_LEFT, LARGE_CARD_TYPE_TOP + 1, COLOR_INDICATOR.width, COLOR_INDICATOR.height, null);
			int pad = COLOR_INDICATOR.width + 2;
			typeBound.width -= pad;
			typeLeft += pad;
		}

		String typeString = getTypeString(c);
		if(0 != typeString.length())
			this.drawCardText(typeString, getFont(), typeLeft, LARGE_CARD_TYPE_TOP, typeBound, false, false, textColor);

		boolean isCreature = c.types.contains(Type.CREATURE);
		if(isCreature)
		{
			this.drawPowerToughnessBox(c, LARGE_CARD_PT_BOX_LEFT, LARGE_CARD_PT_BOX_TOP, false);
			this.drawCardText(c.power + "/" + c.toughness, getFont(), LARGE_CARD_PT_TEXT_LEFT, LARGE_CARD_PT_TEXT_TOP, LARGE_CARD_PT_DIMENSIONS, true, false);
		}
		if(c.types.contains(Type.PLANESWALKER))
		{
			int offset = isCreature ? 45 : 0;
			this.drawImage(getImage("largeframes/loyaltybox.png"), LARGE_CARD_PT_BOX_LEFT - offset, LARGE_CARD_PT_BOX_TOP, null);
			this.drawCardText(Integer.toString(loyaltyOf(object, option)), getFont(), LARGE_CARD_PT_TEXT_LEFT - offset, LARGE_CARD_PT_TEXT_TOP, LARGE_CARD_PT_DIMENSIONS, true, false, java.awt.Color.WHITE);
		}
	}

	public void drawManaSymbol(ManaSymbol s)
	{
		this.drawImage(CardGraphics.getImage("icons/" + getManaSymbolString(s) + ".png"), 0, 0, null);
	}

	public void drawPowerToughnessBox(SanitizedCharacteristics c, int x, int y, boolean small)
	{
		String imageName = null;

		if(c.colors.size() == 1)
			imageName = "ptbox_" + c.colors.iterator().next().getLetter().toLowerCase();
		else if(c.colors.size() > 1 && c.manaCost != null)
		{
			for(ManaSymbol m: c.manaCost)
				if(m.colors.size() == 1)
				{
					imageName = "ptbox_m";
					break;
				}
			if(imageName == null) // it's hybrid
				imageName = "ptbox_misc";
		}
		// no colors, or multiple colors and no mana cost
		else if(c.types.contains(Type.ARTIFACT))
			imageName = "ptbox_a";
		else
			imageName = "ptbox_misc";

		String directory = small ? "smallframes/" : "largeframes/";
		this.drawImage(getImage(directory + imageName + ".png"), x, y, null);
	}

	private void drawSmallCard(SanitizedIdentified o, boolean renderDamage, boolean renderCounters)
	{
		this.drawImage(CardGraphics.getImage("smallframes/" + getCardFrameString(o, SanitizedGameObject.CharacteristicSet.ACTUAL)), 0, 0, null);

		if(null == o)
			return;

		// if we have an ability, just print some basic info about it on the
		// small frame
		if(o instanceof SanitizedNonStaticAbility)
		{
			String text = ((SanitizedNonStaticAbility)o).shortName;
			this.drawCardText(text, getFont(), SMALL_CARD_PADDING_LEFT, SMALL_CARD_PADDING_TOP, new Dimension(SMALL_CARD_TEXT_WIDTH, SMALL_CARD_TOTAL_TEXT_HEIGHT), false, true);
			return;
		}
		if(o instanceof SanitizedGameObject && ((SanitizedGameObject)o).isEmblem)
		{
			this.drawCardText("Emblem", getFont(), SMALL_CARD_PADDING_LEFT, SMALL_CARD_PADDING_TOP, new Dimension(SMALL_CARD_TEXT_WIDTH, SMALL_CARD_TOTAL_TEXT_HEIGHT), false, true);
			return;
		}
		// for players, print the name and life total
		if(o instanceof SanitizedPlayer)
		{
			SanitizedPlayer player = (SanitizedPlayer)o;
			String text = player.name;

			int nameX = SMALL_CARD_PADDING_LEFT;
			int nameY = SMALL_CARD_PADDING_TOP;
			this.drawCardText(text, getFont(), nameX, nameY, new Dimension(SMALL_CARD_TEXT_WIDTH, SMALL_CARD_TOTAL_TEXT_HEIGHT), false, false);

			FontMetrics f = this.getFontMetrics();
			int x = SMALL_CARD.width - SMALL_CARD_DAMAGE_RIGHT;
			int y = SMALL_CARD.height - SMALL_CARD_POISON_BOTTOM - f.getHeight();
			if(player.poisonCounters > 0)
				this.drawCardTextRightAligned(Integer.toString(player.poisonCounters) + " poison", x, y, POISON_COUNTER_COLOR);
			y = y - POISON_LIFE_PADDING - f.getHeight();
			this.drawCardTextRightAligned(Integer.toString(player.lifeTotal) + " life", x, y, LIFE_TOTAL_COLOR);

			return;
		}

		if(o instanceof SanitizedGameObject)
		{
			SanitizedGameObject object = (SanitizedGameObject)o;
			SanitizedCharacteristics characteristics = object.characteristics.get(SanitizedGameObject.CharacteristicSet.ACTUAL);

			Image art = getCardArt(characteristics.name, false);
			if(0 != characteristics.name.length())
			{
				if(art == null)
					this.drawCardText(characteristics.name, getFont(), SMALL_CARD_PADDING_LEFT, SMALL_CARD_PADDING_TOP, new Dimension(SMALL_CARD_TEXT_WIDTH, SMALL_CARD_TOTAL_TEXT_HEIGHT), false, false);
				else
				{
					this.drawImage(art, SMALL_CARD_ART_LEFT, SMALL_CARD_ART_TOP, SMALL_CARD_ART_WIDTH, SMALL_CARD_ART_HEIGHT, null);
					this.drawCardText(characteristics.name, getFont(), SMALL_CARD_PADDING_LEFT, SMALL_CARD_PADDING_TOP, new Dimension(SMALL_CARD_TEXT_WIDTH, SMALL_CARD_TOTAL_TEXT_HEIGHT), false, false);
					this.drawCardText(characteristics.name, getFont(), SMALL_CARD_PADDING_LEFT - 1, SMALL_CARD_PADDING_TOP - 1, new Dimension(SMALL_CARD_TEXT_WIDTH, SMALL_CARD_TOTAL_TEXT_HEIGHT), false, false, java.awt.Color.WHITE);
				}
			}

			boolean isCreature = characteristics.types.contains(Type.CREATURE);
			if(isCreature)
			{
				this.drawPowerToughnessBox(characteristics, SMALL_CARD_PT_BOX_LEFT, SMALL_CARD_PT_BOX_TOP, true);
				this.drawCardText(characteristics.power + "/" + characteristics.toughness, getFont(), SMALL_CARD_PT_TEXT_LEFT, SMALL_CARD_PT_TEXT_TOP, SMALL_CARD_PT_DIMENSIONS, true, false);
			}
			if(characteristics.types.contains(Type.PLANESWALKER))
			{
				int offset = isCreature ? 39 : 0;
				this.drawImage(getImage("smallframes/loyaltybox.png"), SMALL_CARD_PT_BOX_LEFT - offset, SMALL_CARD_PT_BOX_TOP, null);
				this.drawCardText(Integer.toString(loyaltyOf(object, SanitizedGameObject.CharacteristicSet.ACTUAL)), getFont(), SMALL_CARD_PT_TEXT_LEFT - offset, SMALL_CARD_PT_TEXT_TOP, SMALL_CARD_PT_DIMENSIONS, true, false, java.awt.Color.WHITE);
			}

			if(renderDamage && object.damage > 0)
			{
				FontMetrics f = this.getFontMetrics();
				int x = SMALL_CARD.width - SMALL_CARD_DAMAGE_RIGHT;
				int y = SMALL_CARD.height - SMALL_CARD_DAMAGE_BOTTOM - f.getHeight();
				this.drawCardTextRightAligned(Integer.toString(((SanitizedGameObject)o).damage), x, y, java.awt.Color.RED);
			}

			if(renderCounters)
			{
				int countersPerRow = 10;
				int maxRows = 3;
				List<Counter> counters = ((SanitizedGameObject)o).counters;
				if(counters.size() > maxRows * countersPerRow)
				{
					int x = SMALL_CARD_PADDING_LEFT;
					int height = this.getFontMetrics().getHeight();
					int y = SMALL_CARD_ART_TOP + SMALL_CARD_ART_HEIGHT - height - 1;
					this.drawCardText(counters.size() + " counters", this.getFont(), x, y, new Dimension(SMALL_CARD_TEXT_WIDTH, height), false, false);
					this.drawCardText(counters.size() + " counters", this.getFont(), x - 1, y - 1, new Dimension(SMALL_CARD_TEXT_WIDTH, height), false, false, java.awt.Color.WHITE);
				}
				else
				{
					Collections.sort(counters);

					java.awt.Color oldColor = this.getColor();
					int counterSize = 5;
					int counterSpace = 7;
					int i = 0;
					for(Counter c: counters)
					{
						// TODO : customize these colors
						java.awt.Color color = java.awt.Color.WHITE;
						if(c.getType() == Counter.CounterType.PLUS_ONE_PLUS_ONE)
							color = java.awt.Color.GREEN;
						else if(c.getType() == Counter.CounterType.MINUS_ONE_MINUS_ONE)
							color = java.awt.Color.RED;

						int col = i % countersPerRow;
						int x = SMALL_CARD_ART_LEFT + SMALL_CARD_ART_WIDTH - (col + 1) * counterSpace;

						int row = i / countersPerRow;
						int y = SMALL_CARD_ART_TOP + SMALL_CARD_ART_HEIGHT - (row + 1) * counterSpace;

						this.setColor(color);
						fillRect(x, y, counterSize, counterSize);
						this.setColor(java.awt.Color.BLACK);
						drawRect(x, y, counterSize, counterSize);

						i++;
					}
					this.setColor(oldColor);
				}
			}

			int options = 0;
			for(SanitizedGameObject.CharacteristicSet option: getLargeCardDisplayOptions(object))
			{
				++options;

				Rectangle rect = getSmallCardOptionRect(false, false, new Point(0, 0), options);
				this.drawImage(getImage("icons/" + option.name().toLowerCase() + ".png"), (int)(rect.getX()), (int)(rect.getY()), null);
			}
		}
	}

	private static Image getCardArt(String cardName, boolean getLargeArt)
	{
		if(cardArts == null)
			return null;

		final String fileName = cardName + ".jpg";

		if(!imageCache.containsKey(fileName))
		{
			boolean fileFound = false;

			for(File file: cardArts.listFiles(new FilenameFilter()
			{
				@Override
				public boolean accept(File dir, String name)
				{
					return name.equalsIgnoreCase(fileName);
				}
			}))
			{

				Image image = null;
				try
				{
					image = ImageIO.read(file);
				}
				catch(IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(image != null)
				{
					// Cache both a small version and a large version of the
					// card art to save processor power resizing repeatedly
					// later.
					BufferedImage large = new BufferedImage(LARGE_CARD_ART_WIDTH, LARGE_CARD_ART_HEIGHT, BufferedImage.TYPE_INT_RGB);
					large.getGraphics().drawImage(image, 0, 0, LARGE_CARD_ART_WIDTH, LARGE_CARD_ART_HEIGHT, null);
					imageCache.put(fileName, large);

					BufferedImage small = new BufferedImage(SMALL_CARD_ART_WIDTH, SMALL_CARD_ART_HEIGHT, BufferedImage.TYPE_INT_RGB);
					small.getGraphics().drawImage(image, 0, 0, SMALL_CARD_ART_WIDTH, SMALL_CARD_ART_HEIGHT, null);
					imageCache.put("*" + fileName, small);

					fileFound = true;
					break;
				}
			}

			if(!fileFound)
			{
				// If we don't find the file the first time, assume we won't
				// find it any other time and save some directory reads.
				imageCache.put(fileName, null);
				imageCache.put("*" + fileName, null);
			}
		}

		// Small images are prepended with an *
		return imageCache.get((getLargeArt ? "" : "*") + fileName);
	}

	private int loyaltyOf(SanitizedGameObject o, SanitizedGameObject.CharacteristicSet set)
	{
		if(o.zoneID != this.state.battlefield)
			return o.characteristics.get(set).printedLoyalty;

		int loyaltyCounters = 0;
		for(Counter c: o.counters)
			if(c.getType() == Counter.CounterType.LOYALTY)
				loyaltyCounters++;
		return loyaltyCounters;
	}

	public void popTransform()
	{
		this.setTransform(this.transformStack.pop());
	}

	public void pushTransform()
	{
		this.transformStack.push(this.getTransform());
	}

	/**
	 * Draws a large number, indicating that the user is to be dividing
	 * something. This function assumes that the graphics have been translated
	 * to the upper left corner of the rectangle representing the card or
	 * card-like object over which the number is to be drawn.
	 * 
	 * @param division The number to draw.
	 */
	public void drawDivision(int division)
	{
		Dimension cardBounds = new Dimension(SMALL_CARD_TEXT_WIDTH, SMALL_CARD_TOTAL_TEXT_HEIGHT);
		this.drawCardText(Integer.toString(division), getFont().deriveFont(104f), SMALL_CARD_PADDING_LEFT, SMALL_CARD_PADDING_TOP, cardBounds, true, false);
	}
}
