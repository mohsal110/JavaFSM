package com.company;
import java.awt.*;
import java.util.*;

/**
* <IMG SRC="../doku/images/logo_klein.gif" ALT="JavaFSM"><BR>
* Canvas, drawing the impulsdiagram
*/
class ImpulsCanvas extends Canvas 
{
	private FSM fsm;
	private int takt,i;															// Schleifenvariablen
	private int abstandv;														// vertikaler Abstand zwischen den Takten
	private Integer val, oldval; 												// Werte der In/Outputs, aktuell und einer vorher
	private static final int offseth=70, offsetv=30, abstandh=50, fontSize=12;	// offset zum Frame-Rand, horizontaler Abstand zwischen Signalen
	private Vector zustandsfolge, inputs, outputs;
	private Signal signal;
	private Font minifont, normalfont;
	private Color col=Color.red;		// Farbe der einzelnen Signale
	// Farbwerte f�r das Impulsdiagramm, die col annehmen kann
	private Color Red		= Color.red.darker();
	private Color darkRed	= Red.darker();
	private Color lightRed	= Color.red;
	private Color Blue		= Color.blue.darker();
	private Color darkBlue	= Blue.darker();
	private Color lightBlue	= Color.blue;

	/** horizontaler Offset zum Scrollbalken */
	public int xoff=0;
	/** vertikaler Offset zum Scrollbalken */
	public int yoff=0;
	/** Virtuelle Breite des Canvas */
	public int virtWidth;
	/** Virtuelle H&ouml;he des Canvas */
	public int virtHeight;

	/**
	* Konstruktor
	* @param Fsm Automat, dessen Impulsdiagramm gezeigt werden soll (FSM)
	*/	
	public ImpulsCanvas(FSM Fsm)
	{	
		fsm=Fsm;
		setBackground(Color.white);
		int size			= fsm.inputs.size()+fsm.outputs.size();				// Anzahl der auszugebenden Signale
		minifont			= new Font("Helvetica", Font.PLAIN, 9);		
		normalfont			= new Font("Helvetica", Font.PLAIN, 12);
		val = new Integer(0); 
		oldval = new Integer(0);
		setIOColors();				// setzt die Farben der einzelnen Signale (rot, gr�n, blau), damit im Impulsdiagramm besser unterscheidbar
	}
	
	/** zeichnet das Impulsdiagramm */
	public void paint(Graphics g)
	{
		Dimension d = this.size();										
		takt=0;															// Schleifenvariable
		abstandv=20;													// vertikaler Abstand zwischen den Takten
		Zustand AktuellerZustand;	
		int takte = 0;
		virtWidth =(fsm.zustandsfolge.size()-takte+2)*abstandh;				// virtuelle Gr��e des Fensters
		if (virtWidth<d.width) virtWidth=d.width;						// maximale Gr��e zusichern
		virtHeight = ((fsm.inputs.size()+fsm.outputs.size()+4)*abstandv);		// virtuelle Gr��e des Fensters
		if (virtHeight<d.height)virtHeight=d.height;					// maximale Gr��e zusichern
		
		//Koordinaten zeichnen
		g.setColor(Color.black);
		g.drawLine(offseth,offsetv-5,offseth, d.height-offsetv);			// y-Koordinate
		g.drawLine(offseth, d.height-offsetv, d.width, d.height-offsetv);	// x-Koordinate
			//Pfeilspitzen
			g.drawLine(offseth,offsetv-5,offseth-2,offsetv+2-5);
			g.drawLine(offseth,offsetv-5,offseth+2,offsetv+2-5);
			g.drawLine(d.width-2, d.height-offsetv+2, d.width, d.height-offsetv);
			g.drawLine(d.width-2, d.height-offsetv-2, d.width, d.height-offsetv);
		//Koordinatenbeschriftung zeichnen
		g.drawString("Clock",d.width-offseth+15,d.height-2);
		g.drawString("State",10,d.height-5-offsetv);
		g.drawString("In-/Outputs",10,15);		
			//Inputs
		for (i=0;i<fsm.inputs.size();i++)
		{
			//alle Inputs durchlaufen
			signal=(Signal)fsm.inputs.elementAt(i);
			g.setColor(signal.col);
			signal.ypos=d.height-i*abstandv-offsetv-abstandv;//Position setzen
			if ((offsetv<signal.ypos-5-yoff)&&(signal.ypos-5-yoff<(d.height-offsetv-abstandv)))
			{
				//wenn innerhalb des sichtbaren Bereiches
				g.drawString(signal.name,10,signal.ypos-5-yoff);
				g.drawLine(offseth-2,signal.ypos-yoff, offseth+2,signal.ypos-yoff);
			}
		}
			//Outputs
		for (i=0;i<fsm.outputs.size();i++)
		{
			//alle Outputs durchlaufen
			signal=(Signal)fsm.outputs.elementAt(i);
			g.setColor(signal.col);
			signal.ypos=d.height-i*abstandv-offsetv-abstandv-fsm.inputs.size()*abstandv;//Position setzen
			if ((offsetv<signal.ypos-5-yoff)&&(signal.ypos-5-yoff<(d.height-offsetv-abstandv)))
			{
				//wenn innerhalb des sichtbaren Bereiches
				g.drawString(signal.name,10,signal.ypos-5-yoff);
				g.drawLine(offseth-2,signal.ypos-yoff,offseth+2,signal.ypos-yoff);
			}
		}
	
		//Zustand zeichnen
		g.setColor(Color.black);		
		for (takt=0;takt<fsm.zustandsfolge.size();takt++)
		{			//alle Takte durchlaufen und die Zust�nde und Taktstriche einzeichnen
			if (fsm.taktfolge.elementAt(takt).equals("true")) 
			{
				AktuellerZustand=(Zustand)fsm.zustandsfolge.elementAt(takt);
				g.setFont(minifont);	
				if (offseth+takt*abstandh+5-xoff>offseth)		//wenn im sichtbaren Bereich
					g.drawString(AktuellerZustand.name,offseth+takt*abstandh+5-xoff,d.height-offsetv-5);
				g.setFont(normalfont);
	
				//Taktstrich zeichnen
				if (offseth+takt*abstandh-xoff>offseth)		//wenn im sichtbaren Bereich
					g.drawLine(offseth+takt*abstandh-xoff,d.height-offsetv-5,offseth+takt*abstandh-xoff,d.height-offsetv+5);
				int t=0;
				for (int i=0;i<takt;i++)
				{
					if (fsm.taktfolge.elementAt(i).equals("true")) t++;
				}
				if (offseth+takt*abstandh-3-xoff>offseth)		//wenn im sichtbaren Bereich
					g.drawString(""+t,offseth+takt*abstandh-3-xoff,d.height-13);
			}
		}
		//Signale zeichnen
		//Inputs
		for (i=0; i<fsm.inputs.size(); i++)
		{
			signal=(Signal)fsm.inputs.elementAt(i);
			g.setColor(signal.col);
			for(takt=0;takt<fsm.zustandsfolge.size();takt++)
			{
				oldval=val;		
				val=(Integer)signal.taktfolge.elementAt(takt);
				if ((offseth+takt*abstandh-xoff>=offseth)&&(offsetv<signal.ypos-5-yoff)&&(signal.ypos-5-yoff<d.height-offsetv-abstandv))	//wenn innerhalb des sichtbaren Bereiches
					g.drawLine(offseth+takt*abstandh-xoff,signal.ypos-val.intValue()*abstandv/3*2-yoff,
							 offseth+(takt+1)*abstandh-xoff,signal.ypos-val.intValue()*abstandv/3*2-yoff);
				else if ((offseth+takt*abstandh-xoff<offseth)&&(offseth+takt*abstandh-xoff+offseth>offseth)&&(offseth+(takt+1)*abstandh-xoff>offseth)&&(offsetv<signal.ypos-5-yoff)&&(signal.ypos-5-yoff<(d.height-offsetv-abstandv)))
					g.drawLine(offseth,signal.ypos-val.intValue()*abstandv/3*2-yoff,
							 offseth+(takt+1)*abstandh-xoff,signal.ypos-val.intValue()*abstandv/3*2-yoff);
				if (!val.equals(oldval))if ((offseth+takt*abstandh-xoff>offseth)&&(offsetv<signal.ypos-5-yoff)&&(signal.ypos-5-yoff<(d.height-offsetv-abstandv)))
					g.drawLine(offseth+takt*abstandh-xoff,signal.ypos-abstandv/3*2-yoff,
						 offseth+takt*abstandh-xoff,signal.ypos-yoff);
			}
		}
		//Outputs
		for (i=0; i<fsm.outputs.size(); i++)
		{
			signal=(Signal)fsm.outputs.elementAt(i);
			g.setColor(signal.col);
			for(takt=0;takt<fsm.zustandsfolge.size();takt++)
			{
				oldval=val;
				val=(Integer)signal.taktfolge.elementAt(takt);			
				//Fallunterscheidung definiert/UNDEFiniert
				if (val.intValue()!=Signal.UNDEF)
				{
					//normal im sichtbaren Bereich
					if ((offseth+takt*abstandh-xoff>offseth)&&(offsetv<signal.ypos-5-yoff)&&(signal.ypos-5-yoff<(d.height-offsetv-abstandv)))	//wenn innerhalb des sichtbaren Bereiches
						g.drawLine(offseth+takt*abstandh-xoff,signal.ypos-val.intValue()*abstandv/3*2-yoff,
							 offseth+(takt+1)*abstandh-xoff,signal.ypos-val.intValue()*abstandv/3*2-yoff);			
					//nur teilweise sichtbar
					else if ((offseth+takt*abstandh-xoff<=offseth)&&(offseth+takt*abstandh-xoff+offseth>offseth)&&(offseth+(takt+1)*abstandh-xoff >offseth)&&(offsetv<signal.ypos-5-yoff)&&(signal.ypos-5-yoff<(d.height-offsetv-abstandv)))
						g.drawLine(offseth,signal.ypos-val.intValue()*abstandv/3*2-yoff,
							 offseth+(takt+1)*abstandh-xoff,signal.ypos-val.intValue()*abstandv/3*2-yoff);			
					//Verbindungslinie zwischen HIGH und LOW zeichnen, falls Wert gewechselt hat
					if (!val.equals(oldval))//falls vorher anderer Wert (aber nicht underfiniert)
						if ((offseth+takt*abstandh-xoff>offseth)&&(offsetv<signal.ypos-5-yoff)&&(signal.ypos-5-yoff<(d.height-offsetv-abstandv)))
							if (oldval.intValue()!=2)g.drawLine(offseth+takt*abstandh-xoff,signal.ypos-abstandv/3*2-yoff,
							 offseth+takt*abstandh-xoff,signal.ypos-yoff);
				}
				else	// UNDEFiniert
				{	
					//normal im sichtbaren Bereich (Kreuz zeichnen)
					if ((offseth+takt*abstandh-xoff>offseth)&&(offsetv<signal.ypos-5-yoff)&&(signal.ypos-5-yoff<(d.height-offsetv-abstandv)))	//wenn innerhalb des sichtbaren Bereiches
					{
						g.drawLine(offseth+takt*abstandh-xoff,signal.ypos-1*abstandv/3*2-yoff,
							 offseth+(takt+1)*abstandh-xoff,signal.ypos);			
						g.drawLine(offseth+takt*abstandh-xoff,signal.ypos,
							 offseth+(takt+1)*abstandh-xoff,signal.ypos-1*abstandv/3*2-yoff);			
					}
					//nur teilweise sichtbar
					else if ((offseth+takt*abstandh-xoff<=offseth)&&(offseth+takt*abstandh-xoff+offseth>offseth)&&(offseth+(takt+1)*abstandh-xoff >offseth)&&(offsetv<signal.ypos-5-yoff)&&(signal.ypos-5-yoff<(d.height-offsetv-abstandv)))
					{
						g.drawLine(offseth,signal.ypos-1*abstandv/3*2-yoff,  
							 offseth+(takt+1)*abstandh-xoff,signal.ypos);			
						g.drawLine(offseth,signal.ypos,
							 offseth+(takt+1)*abstandh-xoff,signal.ypos-1*abstandv/3*2-yoff);			

					}
				}
			}
		}
	}

	/** setzt die Farben der Inputs und Outputs im Impulsdiagramm (rot, gruen, blau, abwechselnd) */
	public void setIOColors()
	{
		col = darkBlue;
		for (i=0;i<fsm.inputs.size();i++)
		{											// Inputs, Farben setzen
			signal=(Signal)fsm.inputs.elementAt(i);
			col = switchColorIn(col);					// wechselt jeweils die Farbe
			signal.col= col;						// setzt die Farbe des Inputs auf diese Farbe
		}
		col = darkRed;		
		for (i=0;i<fsm.outputs.size();i++)
		{											// Outputs, Farben setzen
			signal=(Signal)fsm.outputs.elementAt(i);
			col = switchColorOut(col);					// wechselt jeweils die Farbe
			signal.col= col;						// setzt die Farbe des Outputs auf diese Farbe
		}
	}
	
	/* wechselt zwischen den Farben rot, blau und gr�n hin und her */
	private Color switchColorIn(Color col)
	{
		if (col==Blue) {col=darkBlue;return col;} 
		else if (col==darkBlue){ col=lightBlue; return col;}
		else if (col==lightBlue){ col=Blue; return col;}
		else col=Color.black;
		return col;
	}
	private Color switchColorOut(Color col)
	{
		if (col==Red) {col=darkRed;return col;} 
		else if (col==darkRed){ col=lightRed; return col;}
		else if (col==lightRed){ col=Red; return col;}
		else col=Color.black;
		return col;
	}
}
