package org.rnd.jmagic.gui;

import org.rnd.jmagic.sanitized.*;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ListIterator;

class MiscZonePanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private final Play gui;
	private SanitizedZone zone;
	public int zoneFocus;

	private JPanel zonePanel;
	private JComboBox zoneChooser;

	public MiscZonePanel(Play play, String zoneName)
	{
		super(new BorderLayout());
		this.gui = play;
		this.zone = null;

		// If the zone name isn't recognized, use any zone from the list.
		Integer focus = this.gui.zones.get(zoneName);

		// This handles "Opponent's library" by referencing the first library
		// that isn't yours
		// TODO: add correct handling for team games (non-opponent players)
		if(focus == null && zoneName.contains("Opponent"))
		{
			zoneName = zoneName.replace("Opponent", "");
			for(String key: this.gui.zones.keySet())
				if(key.endsWith(zoneName))
				{
					zoneName = key;
					focus = this.gui.zones.get(zoneName);
					break;
				}
		}

		if(focus != null)
			this.zoneFocus = focus;
		else
			this.zoneFocus = this.gui.zones.values().iterator().next();

		this.zonePanel = new JPanel()
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g)
			{
				super.paintComponent(g);

				// TODO: render damage? in the misc zone panel? this can
				// probably default to false right?
				boolean renderCounters = Boolean.parseBoolean(MiscZonePanel.this.gui.properties.getProperty(Play.PropertyKeys.RENDER_COUNTERS));

				List<Integer> cards = MiscZonePanel.this.zone.objects;
				CardGraphics cg = new CardGraphics(g, MiscZonePanel.this.gui.state);
				// Iterate through the zone backwards since the "top" of
				// the zone should be drawn last
				ListIterator<Integer> i = cards.listIterator(cards.size());
				while(i.hasPrevious())
				{
					SanitizedGameObject card = (SanitizedGameObject)(MiscZonePanel.this.gui.state.get(i.previous()));
					cg.drawImage(MiscZonePanel.this.gui.getSmallCardImage(card, false, renderCounters, this.getFont()), 0, 0, null);
					cg.translate(0, CardGraphics.SMALL_CARD_PADDING.height);
				}
			}
		};
		MouseInputAdapter mouseInputListener = new MouseInputAdapter()
		{
			/**
			 * @return The card the mouse is over; null if it's not over a card.
			 */
			private SanitizedGameObject getHoveredCard(MouseEvent e)
			{
				List<Integer> cardIDs = MiscZonePanel.this.zone.objects;
				int numCards = cardIDs.size();
				if(0 == numCards)
					return null;

				if(CardGraphics.SMALL_CARD.width <= e.getX())
					return null;

				int index = numCards - 1 - e.getY() / CardGraphics.SMALL_CARD_PADDING.height;
				if(index < 0)
				{
					// Hovering over any part of the last drawn card
					if(CardGraphics.SMALL_CARD_PADDING.height * (numCards - 1) + CardGraphics.SMALL_CARD.height < e.getY())
						return null;
					index = 0;
				}
				return (SanitizedGameObject)(MiscZonePanel.this.gui.state.get(cardIDs.get(index)));
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				SanitizedGameObject card = getHoveredCard(e);
				if(null != card)
					MiscZonePanel.this.gui.identifiedMouseEvent(card, e);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				MiscZonePanel.this.gui.cardInfoPanel.clearArrows();
			}

			@Override
			public void mouseMoved(MouseEvent e)
			{
				SanitizedGameObject hoveredCard = getHoveredCard(e);
				if(null != hoveredCard)
				{
					int reverseIndex = MiscZonePanel.this.zone.objects.size() - MiscZonePanel.this.zone.objects.indexOf(hoveredCard.ID) - 1;
					Point cardStart = new Point(0, reverseIndex * CardGraphics.SMALL_CARD_PADDING.height);
					SanitizedGameObject.CharacteristicSet displayOption = CardGraphics.getLargeCardDisplayOption(e, cardStart, hoveredCard, false);
					MiscZonePanel.this.gui.cardInfoPanel.setFocusToGameObject(hoveredCard, MiscZonePanel.this.gui.state, displayOption);
				}
				else
					MiscZonePanel.this.gui.cardInfoPanel.clearArrows();
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				SanitizedGameObject card = getHoveredCard(e);
				if(null != card)
					MiscZonePanel.this.gui.identifiedMouseEvent(card, e);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				SanitizedGameObject card = getHoveredCard(e);
				if(null != card)
					MiscZonePanel.this.gui.identifiedMouseEvent(card, e);
			}
		};
		this.zonePanel.addMouseListener(mouseInputListener);
		this.zonePanel.addMouseMotionListener(mouseInputListener);

		JScrollPane scroll = new JScrollPane(this.zonePanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getViewport().addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				MiscZonePanel.this.updateCardLocations();

				// Make sure CardInfoPanel isn't caching the arrows for any of
				// the cards this hid.
				MiscZonePanel.this.gui.cardInfoPanel.forceUpdate();
			}
		});
		scroll.getVerticalScrollBar().setUnitIncrement(CardGraphics.SMALL_CARD_PADDING.height / 2);

		this.add(scroll);

		this.zoneChooser = new JComboBox(MiscZonePanel.this.gui.zones.keySet().toArray());
		this.zoneChooser.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JComboBox source = (JComboBox)(e.getSource());
				MiscZonePanel.this.zoneFocus = MiscZonePanel.this.gui.zones.get(source.getSelectedItem());
				MiscZonePanel.this.update();
			}
		});
		this.zoneChooser.setSelectedItem(zoneName);

		EmptyBorder emptyBorder = new EmptyBorder(0, 0, 0, 0);
		this.setBorder(emptyBorder);
		this.zonePanel.setBorder(emptyBorder);
		this.zoneChooser.setBorder(emptyBorder);
		scroll.setBorder(emptyBorder);

		this.zoneChooser.setPreferredSize(new Dimension(CardGraphics.LARGE_CARD.width / 2, this.zoneChooser.getPreferredSize().height));
		this.add(this.zoneChooser, BorderLayout.PAGE_START);

		this.update();
	}

	public void setSelectedItem(Object item)
	{
		this.zoneChooser.setSelectedItem(item);
	}

	public void update()
	{
		this.updateCardLocations();
		int preferredHeight = CardGraphics.SMALL_CARD.height;
		if(0 < this.zone.objects.size())
			preferredHeight += CardGraphics.SMALL_CARD_PADDING.height * (this.zone.objects.size() - 1);
		this.zonePanel.setPreferredSize(new Dimension(CardGraphics.SMALL_CARD.width, preferredHeight));
		this.zonePanel.revalidate();
		this.repaint();
	}

	private void updateCardLocations()
	{
		int x = CardGraphics.SMALL_CARD.width / 2;
		this.zone = (SanitizedZone)(this.gui.state.get(this.zoneFocus));
		int numObjects = this.zone.objects.size();
		for(int i = 0; i < numObjects; i++)
		{
			int y = CardGraphics.SMALL_CARD_PADDING.height * i;
			int indexInZone = numObjects - 1 - i;
			if(indexInZone == 0)
				y += CardGraphics.SMALL_CARD.height / 2;
			else
				y += CardGraphics.SMALL_CARD_PADDING.height / 2;

			Point cardLocation = new Point(x, y);
			Point scrollModify = Play.getLocationInsideWindow(this.zonePanel);
			cardLocation.translate(scrollModify.x, scrollModify.y);
			this.gui.cardLocations.put(this.zone.objects.get(indexInZone), cardLocation);
		}
	}
}
