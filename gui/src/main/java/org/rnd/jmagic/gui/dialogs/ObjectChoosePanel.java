package org.rnd.jmagic.gui.dialogs;

import org.rnd.jmagic.gui.*;
import org.rnd.jmagic.sanitized.*;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Image;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ObjectChoosePanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	public static final int ZONE_PADDING = 15;

	private Dimension maxSize;

	public ObjectChoosePanel(Play gui, Collection<Integer> choices)
	{
		this.maxSize = null;

		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		// This order is somewhat arbitrary, and I'm not passionate about any
		// particular ordering. However I do believe there should be *some*
		// consistent order here, just so that the player isn't disoriented by
		// having different zones be in different orders between resolving
		// multiple Cranial Extractions. (for example) -RulesGuru
		List<Integer> zones = new LinkedList<Integer>();
		Map<Integer, String> zoneNames = new HashMap<Integer, String>();
		zones.add(gui.state.battlefield);
		zoneNames.put(gui.state.battlefield, "Battlefield");
		zones.add(gui.state.stack);
		zoneNames.put(gui.state.stack, "Stack");
		zones.add(gui.state.exileZone);
		zoneNames.put(gui.state.exileZone, "Exile zone");
		zones.add(gui.state.commandZone);
		zoneNames.put(gui.state.commandZone, "Command zone");

		SanitizedPlayer you = (SanitizedPlayer)gui.state.get(gui.playerID);
		zones.add(you.library);
		zoneNames.put(you.library, "Your library");
		zones.add(you.graveyard);
		zoneNames.put(you.graveyard, "Your graveyard");
		zones.add(you.hand);
		zoneNames.put(you.hand, "Your hand");
		zones.add(you.sideboard);
		zoneNames.put(you.sideboard, "Your sideboard");
		for(int playerID: gui.state.players)
		{
			if(playerID == gui.playerID)
				continue;

			SanitizedPlayer player = (SanitizedPlayer)gui.state.get(playerID);
			zones.add(player.library);
			zoneNames.put(player.library, player.name + "'s library");
			zones.add(player.graveyard);
			zoneNames.put(player.graveyard, player.name + "'s graveyard");
			zones.add(player.hand);
			zoneNames.put(player.hand, player.name + "'s hand");
			zones.add(player.sideboard);
			zoneNames.put(player.sideboard, player.name + "'s sideboard");
		}

		zones.add(-1);
		zoneNames.put(-1, "Other");

		// keys are zone IDs, values are lists of choices in that zone
		Map<Integer, List<Integer>> objects = new HashMap<Integer, List<Integer>>();
		for(int choice: choices)
		{
			SanitizedGameObject object = (SanitizedGameObject)gui.state.get(choice);
			int zoneID = (zones.contains(object.zoneID) ? object.zoneID : -1);
			if(!objects.containsKey(zoneID))
				objects.put(zoneID, new LinkedList<Integer>());
			objects.get(zoneID).add(choice);
		}

		int maxPanelWidth = gui.mainWindow.getWidth() - 100;
		boolean firstZone = true;
		for(int zoneID: zones)
		{
			if(!objects.containsKey(zoneID))
				continue;

			if(!firstZone)
				this.add(Box.createRigidArea(new Dimension(0, ZONE_PADDING)));

			String zoneName = zoneNames.get(zoneID);
			JLabel zoneLabel = new JLabel(zoneName);
			zoneLabel.setAlignmentX(CENTER_ALIGNMENT);
			this.add(zoneLabel);

			List<Integer> theseObjects = objects.remove(zoneID);

			ScrollingCardPanel.InnerCardPanel<Integer> thisZone = new ScrollingCardPanel.InnerCardPanel<Integer>(gui)
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
			ScrollingCardPanel scrollingPanel = new ScrollingCardPanel(thisZone);
			scrollingPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, maxPanelWidth));
			thisZone.update(theseObjects, true);
			scrollingPanel.setAlignmentX(CENTER_ALIGNMENT);
			this.add(scrollingPanel);

			firstZone = false;
		}

		this.setMaximumSize(new Dimension(maxPanelWidth, this.getMaximumSize().height));
	}

	@Override
	public Dimension getPreferredSize()
	{
		if(this.maxSize == null)
			return super.getPreferredSize();
		Dimension prefer = super.getPreferredSize();
		Dimension max = this.maxSize;
		return new Dimension(Math.min(prefer.width, max.width), Math.min(prefer.height, max.height));
	}

	@Override
	public void setMaximumSize(Dimension size)
	{
		super.setMaximumSize(size);
		this.maxSize = size;
	}
}
