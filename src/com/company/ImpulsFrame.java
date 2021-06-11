package com.company;
import java.awt.*;
import java.util.*;

/**
* <IMG SRC="../doku/images/logo_klein.gif" ALT="JavaFSM"><BR>
* frame for impulsdiagram-window
*/
class ImpulsFrame extends Frame 
{
	/** Instanz des Zeichen-Canvas */
	public ImpulsCanvas impulsCanvas;
	private Scrollbar hBar, vBar;
	private Dimension d;
	
	/**
	* Konstruktor
	* @param name Fenstertitel (String)
	* @param fsm Automat, dessen Impulsdiagramm gezeigt werden soll (FSM)
	*/
	public ImpulsFrame(String name, FSM fsm) 
	{
		super(name);
		this.setBackground(Color.lightGray);
		this.setForeground(Color.black);
		impulsCanvas = new ImpulsCanvas(fsm);	

		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		this.setLayout(gbl);
		Insets ins = new Insets(0,0,0,0);

		gbc.gridx		= 1;
		gbc.gridy		= 0;
		gbc.gridwidth	= 1;
		gbc.gridheight	= 1;
		gbc.fill		= GridBagConstraints.BOTH;
		gbc.weightx		= 100;
		gbc.weighty		= 100;
		ins.top			= 0;
		ins.bottom		= 0;
		ins.left		= 0;
		ins.right		= 0;
		gbc.insets		= ins;
		gbl.setConstraints(impulsCanvas,gbc);
		this.add(impulsCanvas);

		//Scrollbars setzen
		vBar = new Scrollbar(Scrollbar.VERTICAL);
			gbc.gridx		= 0;
			gbc.gridy		= 0;
			gbc.gridwidth	= 1;
			gbc.gridheight	= 1;
			gbc.fill		= GridBagConstraints.VERTICAL;
			gbc.weightx		= 0;
			gbc.weighty		= 0;
			ins.top			= 0;
			ins.bottom		= 0;
			ins.left		= 0;
			ins.right		= 0;
			gbc.insets		= ins;
			gbl.setConstraints(vBar,gbc);
		
		hBar = new Scrollbar(Scrollbar.HORIZONTAL);
			gbc.gridx		= 1;
			gbc.gridy		= 1;
			gbc.gridwidth	= 1;
			gbc.gridheight	= 1;
			gbc.fill		= GridBagConstraints.HORIZONTAL;
			gbc.weightx		= 0;
			gbc.weighty		= 0;
			ins.top			= 0;
			ins.bottom		= 0;
			ins.left		= 0;
			ins.right		= 0;
			gbc.insets		= ins;
			gbl.setConstraints(hBar,gbc);
		this.add(vBar);
		this.add(hBar);
		resetScrollValue();
	}
	/** verarbeitet die Events der Scrollbalken und WINDOW_DESTROY*/
	public boolean handleEvent(Event evt) 
	{
		if (evt.id == Event.WINDOW_DESTROY) 
		{
			this.hide();
			return true; 
		}
		else if ((evt.id==Event.SCROLL_LINE_UP)||
					(evt.id==Event.SCROLL_LINE_DOWN)||
					(evt.id==Event.SCROLL_PAGE_UP)||
					(evt.id==Event.SCROLL_PAGE_DOWN)||
					(evt.id==Event.SCROLL_ABSOLUTE)){
						setBars();
						impulsCanvas.repaint();
						return true;
		}
		else return super.handleEvent(evt);	
	}

	/** aktualisiert die Werte der Scrollbalken */
	public void setBars()
	{
		d = impulsCanvas.size();
		hBar.setValues(hBar.getValue(), d.width,0, impulsCanvas.virtWidth-d.width);
		vBar.setValues(vBar.getValue(), d.height,-impulsCanvas.virtHeight+d.height,0);
		impulsCanvas.yoff=vBar.getValue();
		impulsCanvas.xoff=hBar.getValue();
		impulsCanvas.repaint();
		this.repaint();
		}

	/** initialisiert die Scrollbalken mit Ursprungswerten */
	public void resetScrollValue()
	{
		hBar.setValues(0,1,0,0);
		vBar.setValues(0,1,-1,0);
		setBars();
	}   
}
