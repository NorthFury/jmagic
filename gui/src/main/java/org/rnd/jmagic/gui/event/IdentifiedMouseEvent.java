package org.rnd.jmagic.gui.event;

import org.rnd.jmagic.sanitized.*;

import java.awt.event.MouseEvent;

public class IdentifiedMouseEvent extends GuiEvent
{
	public static final Object TYPE = "IdentifiedMouseEvent";

	private SanitizedIdentified identified;
	private MouseEvent mouseEvent;

	public IdentifiedMouseEvent(SanitizedIdentified identified, MouseEvent click)
	{
		super(TYPE);
		this.identified = identified;
		this.mouseEvent = click;
	}

	public SanitizedIdentified getIdentified()
	{
		return this.identified;
	}

	public MouseEvent getMouseEvent()
	{
		return this.mouseEvent;
	}
}
