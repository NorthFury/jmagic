package org.rnd.jmagic.gui;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

public class MultilineLabel extends JComponent
{
	private static final long serialVersionUID = 1L;

	private String text;
	private int lines;
	private int maxWidth = Integer.MAX_VALUE;

	public MultilineLabel(int lines)
	{
		this.lines = lines;
	}

	public String getText()
	{
		return this.text;
	}

	public void setText(String text)
	{
		if(text == null || (0 == text.length()))
			text = " ";

		String old = this.text;
		this.text = text;
		firePropertyChange("text", old, this.text);
		if(old == null || !old.equals(text))
		{
			revalidate();
			repaint();
		}
	}

	public int getMaxWidth()
	{
		return this.maxWidth;
	}

	public void setMaxWidth(int maxWidth)
	{
		if(maxWidth <= 0)
			throw new IllegalArgumentException();
		int old = this.maxWidth;
		this.maxWidth = maxWidth;
		firePropertyChange("maxWidth", old, this.maxWidth);
		if(old != this.maxWidth)
		{
			revalidate();
			repaint();
		}
	}

	@Override
	public Dimension getPreferredSize()
	{
		return paintOrGetSize((Graphics2D)this.getGraphics(), getMaxWidth(), false);
	}

	@Override
	public Dimension getMinimumSize()
	{
		return getPreferredSize();
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		paintOrGetSize((Graphics2D)g, getWidth(), true);
	}

	private Dimension paintOrGetSize(Graphics2D g, int width, boolean paint)
	{
		// WHY THE HELL WON'T THE BACKGROUND JUST EFFING DRAW CORRECTLY?
		// TODO : Anyone who can figure out how to get it to just draw right,
		// please change this. -RulesGuru
		if(paint)
		{
			Color old = g.getColor();
			g.setColor(this.getBackground());
			g.fillRect(0, 0, width, this.getHeight());
			g.setColor(old);
		}

		Insets insets = getInsets();
		width -= insets.left + insets.right + 4;
		float w = insets.left + insets.right;
		float y = insets.top;
		float height = -1;
		if(width > 0 && this.text != null && this.text.length() > 0)
		{
			AttributedString as = new AttributedString(getText());
			as.addAttribute(TextAttribute.FONT, getFont());
			AttributedCharacterIterator aci = as.getIterator();
			LineBreakMeasurer lbm = new LineBreakMeasurer(aci, g.getFontRenderContext());
			float max = 0;
			while(lbm.getPosition() < aci.getEndIndex())
			{
				TextLayout textLayout = lbm.nextLayout(width);
				if(paint)
					textLayout.draw(g, 2 + insets.left, y + textLayout.getAscent());
				y += textLayout.getDescent() + textLayout.getLeading() + textLayout.getAscent();
				max = Math.max(max, textLayout.getVisibleAdvance());

				// this line will be hit multiple times, but it's unavoidable
				// since textLayout only exists inside this loop.
				height = this.lines * (textLayout.getDescent() + textLayout.getLeading() + textLayout.getAscent());
			}
			w += max;
		}
		return new Dimension((int)Math.ceil(w), (int)Math.ceil(height) + insets.bottom);
	}
}
