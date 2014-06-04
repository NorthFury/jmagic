package org.rnd.jmagic.gui;

import javax.swing.ButtonModel;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicMenuItemUI;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.TextLayout;
import java.text.AttributedString;

/**
 * This is the ui painter used for the action menu that pops up when choosing
 * from several player actions for the same object.
 */
public class ContextDialogPainter extends BasicMenuItemUI
{
	private String lastKnownText;
	private TextLayout lastLayout;

	public ContextDialogPainter()
	{
		this.lastKnownText = null;
		this.lastLayout = null;
	}

	public void drawString(Graphics g, String text, int x, int y)
	{
		if(text == null || text.length() <= 0)
			return;

		Graphics2D g2d = (Graphics2D)g;

		if(text.equals(this.lastKnownText))
		{
			this.lastLayout.draw(g2d, x, y);
		}
		else
		{
			AttributedString attrText = CardGraphics.getAttributedString(text, g2d.getFontMetrics(), true);
			TextLayout textLayout = new TextLayout(attrText.getIterator(), g2d.getFontRenderContext());
			textLayout.draw(g2d, x, y);
			this.lastKnownText = text;
			this.lastLayout = textLayout;
		}
	}

	@Override
	protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text)
	{
		ButtonModel model = menuItem.getModel();
		FontMetrics fm = g.getFontMetrics(menuItem.getFont());

		if(!model.isEnabled())
		{
			// *** paint the text disabled
			Color disabled = UIManager.getColor("MenuItem.disabledForeground");
			if(disabled != null)
			{
				g.setColor(disabled);
				drawString(g, text, textRect.x, (textRect.y + fm.getAscent()));
			}
			else
			{
				g.setColor(menuItem.getBackground().brighter());
				drawString(g, text, textRect.x, (textRect.y + fm.getAscent()));
				g.setColor(menuItem.getBackground().darker());
				drawString(g, text, (textRect.x - 1), (textRect.y + fm.getAscent() - 1));
			}
		}
		else
		{
			// *** paint the text normally
			if(model.isArmed())
			{
				g.setColor(this.selectionForeground); // Uses protected
				// field.
			}
			drawString(g, text, textRect.x, (textRect.y + fm.getAscent()));
		}
	}
}