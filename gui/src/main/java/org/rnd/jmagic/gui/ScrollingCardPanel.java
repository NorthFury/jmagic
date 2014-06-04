package org.rnd.jmagic.gui;

import org.rnd.jmagic.sanitized.*;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ScrollingCardPanel extends JComponent
{
	public static abstract class InnerCardPanel<T> extends JPanel
	{
		private static final long serialVersionUID = 1L;

		// -1 is sentinel indicating that this hasn't been calculated yet. This
		// value will be calculated as soon as an instance of this is able to
		// calculate it using its font's metrics.
		private static int MAXIMUM_CARD_OVERLAP = -1;

		protected List<T> displayOrder;
		protected T heldCard;
		protected Rectangle heldRectangle;
		protected Play gui;
		private int cardSpacing;

		/**
		 * Constructs a new card panel that does not have a specific maximum
		 * width (for panels like the hand panel).
		 */
		public InnerCardPanel(Play gui)
		{
			super();
			if(MAXIMUM_CARD_OVERLAP == -1)
			{
				// We want to be able to see at least two characters of each
				// card. This is arbitrary. Feel free to change it to something
				// less arbitrary.
				int minimumVisibleSpace = CardGraphics.SMALL_CARD_PADDING_LEFT + this.getFontMetrics(this.getFont()).stringWidth("MM");
				MAXIMUM_CARD_OVERLAP = CardGraphics.SMALL_CARD.width - minimumVisibleSpace;
			}

			this.cardSpacing = CardGraphics.SMALL_CARD_PADDING.width;
			this.heldCard = null;
			this.gui = gui;
			this.displayOrder = new LinkedList<T>();
			this.heldRectangle = null;
			MouseInputAdapter mouseInputListener = new MouseInputAdapter()
			{
				private Point offset = null;
				private int insertIndex = -1;

				@Override
				public void mouseClicked(MouseEvent e)
				{
					T cardClicked = getHovered(e);
					if(cardClicked != null)
					{
						Serializable identified = InnerCardPanel.this.getIdentified(cardClicked);
						if(identified instanceof SanitizedIdentified)
							InnerCardPanel.this.gui.identifiedMouseEvent((SanitizedIdentified)identified, e);
					}
				}

				@Override
				public void mousePressed(MouseEvent e)
				{
					T cardClicked = getHovered(e);
					if(cardClicked != null)
					{
						Serializable identified = InnerCardPanel.this.getIdentified(cardClicked);
						if(identified instanceof SanitizedIdentified)
							InnerCardPanel.this.gui.identifiedMouseEvent((SanitizedIdentified)identified, e);
					}

					InnerCardPanel.this.heldCard = cardClicked;
					if(InnerCardPanel.this.heldCard != null)
					{
						this.insertIndex = InnerCardPanel.this.displayOrder.indexOf(InnerCardPanel.this.heldCard);
						InnerCardPanel.this.displayOrder.set(this.insertIndex, InnerCardPanel.this.heldCard);
						InnerCardPanel.this.heldRectangle = this.getCardRectangle(e);
						this.offset = new Point(e.getX() - InnerCardPanel.this.heldRectangle.x, e.getY() - InnerCardPanel.this.heldRectangle.y);
						InnerCardPanel.this.repaint();
					}
					else
						this.offset = null;
				}

				@Override
				public void mouseDragged(MouseEvent e)
				{
					if(InnerCardPanel.this.heldCard != null)
					{
						Rectangle movingArea = InnerCardPanel.this.heldRectangle;
						Rectangle newPosition = new Rectangle(e.getX() - this.offset.x, e.getY() - this.offset.y, movingArea.width, movingArea.height);
						InnerCardPanel.this.heldRectangle = newPosition;

						int nearestIndex = getNearestIndex(e);
						if(this.insertIndex != nearestIndex)
						{
							InnerCardPanel.this.displayOrder.set(this.insertIndex, InnerCardPanel.this.displayOrder.get(nearestIndex));
							InnerCardPanel.this.displayOrder.set(nearestIndex, InnerCardPanel.this.heldCard);
							this.insertIndex = nearestIndex;
						}

						InnerCardPanel.this.repaint();
					}
				}

				@Override
				public void mouseExited(MouseEvent e)
				{
					InnerCardPanel.this.gui.cardInfoPanel.clearArrows();
				}

				@Override
				public void mouseReleased(MouseEvent e)
				{
					T cardClicked = getHovered(e);
					if(cardClicked != null)
					{
						Serializable identified = InnerCardPanel.this.getIdentified(cardClicked);
						if(identified instanceof SanitizedIdentified)
							InnerCardPanel.this.gui.identifiedMouseEvent((SanitizedIdentified)identified, e);
					}

					if(InnerCardPanel.this.heldCard != null)
					{
						this.insertIndex = -1;

						this.offset = null;
						InnerCardPanel.this.heldCard = null;
						InnerCardPanel.this.heldRectangle = null;

						InnerCardPanel.this.repaint();
					}
				}

				@Override
				public void mouseMoved(MouseEvent e)
				{
					T hoveredCard = getHovered(e);
					if(hoveredCard != null)
					{
						SanitizedGameObject display = null;
						if(hoveredCard instanceof Integer)
							display = (SanitizedGameObject)InnerCardPanel.this.gui.state.get(hoveredCard);
						else if(hoveredCard instanceof SanitizedGameObject)
							display = (SanitizedGameObject)hoveredCard;

						if(display != null)
						{
							int index = InnerCardPanel.this.displayOrder.indexOf(hoveredCard);
							Point cardStart = new Point(index * (CardGraphics.SMALL_CARD.width + InnerCardPanel.this.cardSpacing), 0);
							SanitizedGameObject.CharacteristicSet displayOption = CardGraphics.getLargeCardDisplayOption(e, cardStart, display, false);
							InnerCardPanel.this.gui.cardInfoPanel.setFocusToGameObject(display, InnerCardPanel.this.gui.state, displayOption);
							return;
						}
					}

					InnerCardPanel.this.gui.cardInfoPanel.clearArrows();
				}

				/**
				 * Find the card in this hand which contains the current mouse
				 * position
				 */
				private T getHovered(MouseEvent e)
				{
					List<T> cards = InnerCardPanel.this.displayOrder;
					if(0 == cards.size())
						return null;

					// Hovering between cards
					int x = e.getX();
					if(InnerCardPanel.this.cardSpacing > 0 && CardGraphics.SMALL_CARD.width <= (x % (CardGraphics.SMALL_CARD.width + InnerCardPanel.this.cardSpacing)))
						return null;

					// Off the end?
					int index = x / (CardGraphics.SMALL_CARD.width + InnerCardPanel.this.cardSpacing);
					if(cards.size() <= index)
					{
						// Over the last card
						if(x < CardGraphics.SMALL_CARD.width + ((CardGraphics.SMALL_CARD.width + InnerCardPanel.this.cardSpacing) * (cards.size() - 1)))
							return cards.get(cards.size() - 1);
						// Off the end
						return null;
					}

					return cards.get(index);
				}

				private Rectangle getCardRectangle(MouseEvent e)
				{
					return new Rectangle(e.getX() - (e.getX() % (CardGraphics.SMALL_CARD.width + InnerCardPanel.this.cardSpacing)), 0, CardGraphics.SMALL_CARD.width, CardGraphics.SMALL_CARD.height);
				}

				private int getNearestIndex(MouseEvent e)
				{
					return Math.min(Math.max((e.getX() + (InnerCardPanel.this.cardSpacing / 2)) / (CardGraphics.SMALL_CARD.width + InnerCardPanel.this.cardSpacing), 0), InnerCardPanel.this.displayOrder.size() - 1);
				}
			};
			this.addMouseMotionListener(mouseInputListener);
			this.addMouseListener(mouseInputListener);
		}

		public List<T> getObjects()
		{
			return new LinkedList<T>(this.displayOrder);
		}

		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);

			T ignore = null;
			if(this.heldCard != null)
				ignore = this.heldCard;

			CardGraphics cg = new CardGraphics(g, this.gui.state);
			cg.pushTransform();

			for(T i: this.displayOrder)
			{
				if(ignore == null || i != ignore)
					cg.drawImage(getImage(i), 0, 0, null);
				cg.translate(this.cardSpacing + CardGraphics.SMALL_CARD.width, 0);
			}
			cg.popTransform();

			if(this.heldCard != null)
			{
				cg.drawImage(getImage(this.heldCard), this.heldRectangle.x, this.heldRectangle.y, null);
			}
		}

		public abstract Image getImage(T ref);

		public abstract Serializable getIdentified(T ref);

		private void recalcSize()
		{
			int minSpacing = -MAXIMUM_CARD_OVERLAP;
			int maxSpacing = CardGraphics.SMALL_CARD_PADDING.width;

			int totalCardWidth = this.displayOrder.size() * CardGraphics.SMALL_CARD.width;
			this.cardSpacing = (int)Math.floor((this.getParent().getWidth() - totalCardWidth) / (double)(this.displayOrder.size() - 1));
			if(this.cardSpacing < minSpacing)
				this.cardSpacing = minSpacing;
			else if(this.cardSpacing > maxSpacing)
				this.cardSpacing = maxSpacing;

			if(this.displayOrder.size() > 0)
			{
				int minimumWidth = (this.displayOrder.size() - 1) * (minSpacing + CardGraphics.SMALL_CARD.width) + CardGraphics.SMALL_CARD.width;
				int maximumWidth = this.displayOrder.size() * (CardGraphics.SMALL_CARD.width + CardGraphics.SMALL_CARD_PADDING.width) - CardGraphics.SMALL_CARD_PADDING.width;
				// System.out.println("Min: " + minimumWidth + ", Max: " +
				// maximumWidth);

				this.setMinimumSize(new Dimension(minimumWidth, CardGraphics.SMALL_CARD.height));

				Dimension preferredSize = new Dimension(maximumWidth, CardGraphics.SMALL_CARD.height);
				this.setPreferredSize(preferredSize);
				this.setMaximumSize(preferredSize);
			}
			else
			{
				Dimension preferredSize = new Dimension(0, CardGraphics.SMALL_CARD.height);

				this.setMinimumSize(preferredSize);
				this.setPreferredSize(preferredSize);
				this.setMaximumSize(preferredSize);
			}
		}

		public void update(List<T> objects, boolean resize)
		{
			// Remove cards that aren't here anymore
			Iterator<T> iter = this.displayOrder.iterator();
			while(iter.hasNext())
				if(!objects.contains(iter.next()))
					iter.remove();

			// Add any new cards to the display order
			for(T id: objects)
				if(!this.displayOrder.contains(id))
					this.displayOrder.add(id);

			if(resize)
			{
				this.recalcSize();
				this.revalidate();
			}
		}
	}

	private final static class JHandLayout implements LayoutManager
	{
		private LayoutManager _delegate;
		private ScrollingCardPanel view;

		public JHandLayout(ScrollingCardPanel view)
		{
			this._delegate = new BorderLayout();
			this.view = view;
		}

		@Override
		public void addLayoutComponent(String name, Component comp)
		{
			this._delegate.addLayoutComponent(name, comp);
		}

		@Override
		public void layoutContainer(Container parent)
		{
			this.view.view.recalcSize();

			boolean buttonsNeeded = (this.view.viewport.getView().getPreferredSize().width > this.view.viewport.getSize().width);
			this.view.left.setVisible(buttonsNeeded);
			this.view.right.setVisible(buttonsNeeded);

			this._delegate.layoutContainer(parent);
		}

		@Override
		public Dimension minimumLayoutSize(Container parent)
		{
			Dimension minimumLayoutSize = this._delegate.minimumLayoutSize(parent);
			return minimumLayoutSize;
		}

		@Override
		public Dimension preferredLayoutSize(Container parent)
		{
			return this._delegate.preferredLayoutSize(parent);
		}

		@Override
		public void removeLayoutComponent(Component comp)
		{
			this._delegate.removeLayoutComponent(comp);
		}
	}

	private final static class ScrollListener extends MouseAdapter implements ActionListener
	{
		private static final int SCROLL_DISTANCE = (CardGraphics.SMALL_CARD.width + CardGraphics.SMALL_CARD_PADDING.width) / 2;
		private static final int REPEAT_DISTANCE = 20;
		private static final int INITIAL_DELAY = 375;
		private static final int REPEAT_DELAY = 25;

		public static final int LEFT = -1;
		public static final int RIGHT = 1;

		private final int direction;
		private final JViewport viewport;
		private Timer timer;

		public ScrollListener(int direction, JViewport viewport)
		{
			this.direction = direction;
			this.viewport = viewport;
		}

		private void adjust(int amount)
		{
			// Translate origin by some amount
			Point origin = this.viewport.getViewPosition();
			origin.x += amount;
			if(origin.x > this.viewport.getViewSize().width - this.viewport.getExtentSize().width)
				origin.x = this.viewport.getViewSize().width - this.viewport.getExtentSize().width;
			if(origin.x < 0)
				origin.x = 0;
			// set new viewing origin
			this.viewport.setViewPosition(origin);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(e == null)
				this.adjust(this.direction * SCROLL_DISTANCE);
			else
				this.adjust(this.direction * REPEAT_DISTANCE);
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			this.actionPerformed(null);

			this.timer = new Timer(INITIAL_DELAY, this);
			this.timer.setRepeats(true);
			this.timer.setDelay(REPEAT_DELAY);
			this.timer.start();
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			this.timer.stop();
			this.timer = null;
		}
	}

	private static final long serialVersionUID = 1L;

	final JButton left;
	final JButton right;
	final JViewport viewport;
	final InnerCardPanel<?> view;

	public ScrollingCardPanel(InnerCardPanel<?> view)
	{
		super();

		this.setLayout(new JHandLayout(this));

		this.view = view;

		this.viewport = new JViewport();
		this.viewport.setView(view);
		this.add(this.viewport, BorderLayout.CENTER);

		this.left = new BasicArrowButton(SwingConstants.WEST);
		ScrollingCardPanel.ScrollListener leftListener = new ScrollListener(ScrollListener.LEFT, this.viewport);
		this.left.addMouseListener(leftListener);
		this.add(this.left, BorderLayout.LINE_START);

		this.right = new BasicArrowButton(SwingConstants.EAST);
		ScrollingCardPanel.ScrollListener rightListener = new ScrollListener(ScrollListener.RIGHT, this.viewport);
		this.right.addMouseListener(rightListener);
		this.add(this.right, BorderLayout.LINE_END);
	}
}
