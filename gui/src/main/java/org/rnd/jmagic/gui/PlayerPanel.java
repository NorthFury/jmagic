package org.rnd.jmagic.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.gui.ScrollingCardPanel.*;
import org.rnd.jmagic.sanitized.*;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;

class PlayerPanel extends JPanel
{
	private static Dimension PREFERRED_VIEWPORT_SIZE = null;

	private final class ManaPanel extends JPanel implements Scrollable
	{
		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);

			CardGraphics cg = new CardGraphics(g, PlayerPanel.this.gui.state);
			int i = 0;
			for(ManaSymbol s: PlayerPanel.this.player.pool)
			{
				if(PlayerPanel.this.gui.choiceType == PlayerInterface.ChoiceType.MANA_PAYMENT && PlayerPanel.this.gui.choose != null && PlayerPanel.this.gui.choose.contains(i))
					cg.drawImage(CardGraphics.getImage("icons/select.png"), 0, 0, null);
				cg.drawManaSymbol(s);
				cg.translate(0, CardGraphics.LARGE_MANA_SYMBOL.height + PlayerPanel.POOL_MANA_SYMBOL_PADDING.height);
				i++;
			}
		}

		@Override
		public Dimension getPreferredScrollableViewportSize()
		{
			if(PREFERRED_VIEWPORT_SIZE == null)
			{
				Container viewport = this.getParent();
				JComponent scrollPane = (JComponent)viewport.getParent();
				Insets borderInsets = scrollPane.getBorder().getBorderInsets(scrollPane);
				PREFERRED_VIEWPORT_SIZE = new Dimension(CardGraphics.LARGE_MANA_SYMBOL.width, CardGraphics.SMALL_CARD.height - borderInsets.top - borderInsets.bottom);
			}
			return PREFERRED_VIEWPORT_SIZE;
		}

		@Override
		public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
		{
			return CardGraphics.LARGE_MANA_SYMBOL.height * 5;
		}

		@Override
		public boolean getScrollableTracksViewportHeight()
		{
			return false;
		}

		@Override
		public boolean getScrollableTracksViewportWidth()
		{
			return true;
		}

		@Override
		public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
		{
			return CardGraphics.LARGE_MANA_SYMBOL.height + PlayerPanel.POOL_MANA_SYMBOL_PADDING.height;
		}
	}

	static final int CARDS_IN_HAND = 7;
	static final int MANA_SYMBOLS_IN_POOL = 5;
	static final Dimension POOL_MANA_SYMBOL_PADDING = new Dimension(1, 1);
	private static final long serialVersionUID = 1L;
	private final Play gui;
	final InnerCardPanel<Integer> handPanel;
	final JPanel manaPanel;
	final JPanel playerInfo;
	private SanitizedPlayer player;
	private int playerFocus;

	public PlayerPanel(final Play play, int playerID)
	{
		super(new BorderLayout());
		this.gui = play;
		this.player = null;
		this.playerFocus = playerID;

		this.handPanel = new ScrollingCardPanel.InnerCardPanel<Integer>(play)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public Image getImage(Integer ref)
			{
				return this.gui.getSmallCardImage(this.gui.state.get(ref), false, false, this.getFont());
			}

			@Override
			public SanitizedIdentified getIdentified(Integer ref)
			{
				return this.gui.state.get(ref);
			}
		};
		this.handPanel.setPreferredSize(new Dimension((CardGraphics.SMALL_CARD.width + CardGraphics.SMALL_CARD_PADDING.width) * PlayerPanel.CARDS_IN_HAND - CardGraphics.SMALL_CARD_PADDING.width, CardGraphics.SMALL_CARD.height));

		this.manaPanel = new ManaPanel();
		this.manaPanel.setPreferredSize(new Dimension(CardGraphics.LARGE_MANA_SYMBOL.width, (CardGraphics.LARGE_MANA_SYMBOL.height + CardGraphics.SMALL_CARD_PADDING.height) * PlayerPanel.MANA_SYMBOLS_IN_POOL - CardGraphics.SMALL_CARD_PADDING.height));

		this.playerInfo = new JPanel()
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g)
			{
				super.paintComponent(g);

				CardGraphics cg = new CardGraphics(g, PlayerPanel.this.gui.state);
				cg.drawImage(PlayerPanel.this.gui.getSmallCardImage(PlayerPanel.this.gui.state.get(PlayerPanel.this.playerFocus), false, false, this.getFont()), 0, 0, null);

				if(PlayerPanel.this.gui.divisions != null && PlayerPanel.this.gui.divisions.containsKey(PlayerPanel.this.playerFocus))
				{
					int division = PlayerPanel.this.gui.divisions.get(PlayerPanel.this.playerFocus);

					cg.drawDivision(division);
				}
			}
		};
		this.playerInfo.setPreferredSize(CardGraphics.SMALL_CARD);
		this.playerInfo.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				PlayerPanel.this.gui.identifiedMouseEvent(PlayerPanel.this.player, e);
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				PlayerPanel.this.gui.identifiedMouseEvent(PlayerPanel.this.player, e);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				PlayerPanel.this.gui.identifiedMouseEvent(PlayerPanel.this.player, e);
			}
		});
		this.playerInfo.addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseMoved(MouseEvent e)
			{
				PlayerPanel.this.gui.cardInfoPanel.setFocusToNonGameObject(PlayerPanel.this.playerFocus, PlayerPanel.this.gui.state);
			}
		});

		JPanel manaAndInfo = new JPanel();
		manaAndInfo.setLayout(new BoxLayout(manaAndInfo, BoxLayout.X_AXIS));
		manaAndInfo.add(this.playerInfo);
		manaAndInfo.add(Box.createHorizontalStrut(5));
		manaAndInfo.add(new JScrollPane(this.manaPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.LINE_START);

		this.add(new ScrollingCardPanel(this.handPanel));
		this.add(manaAndInfo, BorderLayout.LINE_START);

		this.update(false);
	}

	public int getPlayer()
	{
		return this.playerFocus;
	}

	public void setPlayer(int player)
	{
		this.playerFocus = player;
	}

	public void update(boolean resize)
	{
		this.player = (SanitizedPlayer)(this.gui.state.get(this.playerFocus));

		Point playerLocation = Play.getLocationInsideWindow(this);
		playerLocation.translate(CardGraphics.SMALL_CARD.width / 2, CardGraphics.SMALL_CARD.height / 2);
		this.gui.cardLocations.put(PlayerPanel.this.playerFocus, playerLocation);

		this.handPanel.update(((SanitizedZone)this.gui.state.get(this.player.hand)).objects, resize);

		List<Integer> displayOrder = this.handPanel.getObjects();
		for(int i = 0; i < displayOrder.size(); i++)
		{
			Point topLeft = new Point(i * (CardGraphics.SMALL_CARD.width + CardGraphics.SMALL_CARD_PADDING.width), 0);
			Point modify = playerLocation;
			topLeft.translate(modify.x, modify.y);
			this.gui.cardLocations.put(displayOrder.get(i), topLeft);
		}

		if(resize)
		{
			this.manaPanel.setPreferredSize(new Dimension(CardGraphics.LARGE_MANA_SYMBOL.width, (CardGraphics.LARGE_MANA_SYMBOL.height + PlayerPanel.POOL_MANA_SYMBOL_PADDING.height) * PlayerPanel.this.player.pool.converted()));
			this.manaPanel.revalidate();
		}

		this.repaint();
	}
}
