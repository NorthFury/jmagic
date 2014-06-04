package org.rnd.jmagic.gui.dialogs;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.gui.*;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorChoosePanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	public final Map<Arc2D, Color> colors;
	private Color choice;
	public boolean choiceReady;

	public ColorChoosePanel(final Play gui, final List<Color> choices)
	{
		this.choiceReady = false;
		this.choice = null;
		this.colors = new HashMap<Arc2D, Color>();

		this.setSize(100, 100);
		this.setPreferredSize(new Dimension(100, 100));

		EnumSet<Color> colorsSet = EnumSet.copyOf(choices);

		int wedgeDegrees = -360 / colorsSet.size();
		int initAngle = 90 - (wedgeDegrees / 2);
		int i = 0;
		for(Color color: colorsSet)
			this.colors.put(new Arc2D.Float(0, 0, this.getWidth(), this.getHeight(), initAngle + i++ * wedgeDegrees, wedgeDegrees, Arc2D.PIE), color);

		this.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				for(Arc2D arc: ColorChoosePanel.this.colors.keySet())
				{
					if(arc.contains(e.getPoint()))
					{
						Color color = ColorChoosePanel.this.colors.get(arc);

						int i = choices.indexOf(color);

						if(i != -1)
							gui.choose = Collections.singletonList(i);
						else
							gui.choose = Collections.emptyList();
						gui.choiceReady();
					}
				}
			}
		});
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D)g;
		for(Arc2D arc: this.colors.keySet())
		{
			java.awt.Color newColor = null;
			switch(this.colors.get(arc))
			{
			case WHITE:
				newColor = new java.awt.Color(255, 251, 213);
				break;
			case BLUE:
				newColor = new java.awt.Color(58, 183, 243);
				break;
			case BLACK:
				newColor = new java.awt.Color(46, 46, 46);
				break;
			case RED:
				newColor = new java.awt.Color(242, 77, 21);
				break;
			case GREEN:
				newColor = new java.awt.Color(68, 155, 99);
				break;
			}
			g2.setColor(newColor);
			g2.fill(arc);
		}
	}

	public Color getChoice()
	{
		return this.choice;
	}
}
