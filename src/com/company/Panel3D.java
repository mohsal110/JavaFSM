package com.company;
import java.awt.*;

/**
* <IMG SRC="../doku/images/logo_klein.gif" ALT="JavaFSM"><BR>
* simples Panel, das eine automatische 3D-Umrandung besitzt
*/
class Panel3D extends Panel {				
	private boolean raised;

	/**
	* Konstruktor
	* @param raised true, wenn die Umrandung "erhoben" sein soll (boolean)
	* @see Graphics.draw3DRect()
	*/
	public Panel3D(boolean raised) {
		super();
		this.raised = raised;
	}

	/** zeichnet die Umrandung */
	public void paint(Graphics g) {
		Dimension dim = this.size();
		g.setColor(Color.lightGray);
		g.draw3DRect(0,0,dim.width-1,dim.height-1,this.raised);
	}
}
