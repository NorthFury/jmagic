package org.rnd.jmagic.gui.event;

import java.awt.event.MouseEvent;

public class BattlefieldClickEvent extends GuiEvent
{
	public static final Object TYPE = "BattlefieldClickEvent";

	private MouseEvent click;

	public BattlefieldClickEvent(MouseEvent click)
	{
		super(TYPE);
		this.click = click;
	}

	public MouseEvent getClick()
	{
		return this.click;
	}
}
