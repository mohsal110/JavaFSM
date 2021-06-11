package com.company;
import java.awt.*;

/**
* <IMG SRC="../doku/images/logo_klein.gif" ALT="JavaFSM"><BR>
* yellow status line 
* at bottom of window 
*
*/
class Statuszeile extends Canvas 
{
	private String status;

	/** Konstruktor
	* @param Status String, auf den die Statuszeile gesetzt wird (String)
	*/
	public Statuszeile(String Status) 
	{
		super();
		status = Status;
		this.setBackground(Color.yellow);
		this.setForeground(Color.black);
		setFont(new Font("Helvetica",Font.PLAIN,12));
	}

	/** Konstruktor ohne String */
	public Statuszeile() 
	{
		super();
		status = "";
		this.setBackground(Color.yellow);
		this.setForeground(Color.black);
		setFont(new Font("Helvetica",Font.PLAIN,12));
	}

	/** weist der Statuszeile eine minimale Gr��e zu */
	public Dimension minimumSize()
	{
		return(new Dimension(1000, 15));
	}

	/** weist der Statuszeile einen neuen Text zu 
	* @param Status String, auf den die Statuszeile gesetzt wird (String)
	*/
	public void set(String Status)
	{
		status = Status;
		repaint();
	}

	/** zeichnet die Statuszeile */
	public void paint(Graphics g) 
	{	
		Dimension d = this.size();
		g.drawRect(0,0,d.width-1,d.height-1);
		g.drawString(status,5,d.height/5*4);
	}
}
