package com.company;
import java.awt.*;
import java.util.*;
 /**
* <IMG SRC="../doku/images/logo_klein.gif" ALT="JavaFSM"><BR>
* the network with in and outputs and the machine in the delta-network
*         AAAAAAAAAAAAAAAAAAAA---------
*         A   ---------       |       | *****   R1: Delta-Schaltwerk
*   ***** A   |       |       |   R3  | *****	R2: Taktglied
*   ***** A   |  R1   |       |       | *****	R3: Lambda-Schaltnetz
*   ***** ACCC|       |BBBB   ---------
*         A   ---------   B						A, B, C: Verbindungspfeile zwischen den Schaltnetzen
*         A   ---------   B						***** :	Signallinien f�r Inputs und Outputs
*         AAAA|  R2   |BBBB 
*             ---------
*/
class Simulation extends Panel 
{
	/** der bearbeitete Automat */
	private FSM fsm;
	/** Puffer-Variablen */
	private Signal input, output, Selected;				// Singnale zum Zwischenspeichern

	/** Konstante f&uuml;r modus */
	public static final int NORMAL = 0;					// NORMAL: value des angeklickten Signals wechseln
	/** Konstante f&uuml;r modus */
	public static final int DELETE = 1;					// DELETE: angeklicktes Signal l�schen
	/** Konstante f&uuml;r modus */
	public static final int CHANGE = 2;					// CHANGE: angeklicktes Signal �ndern
	/** Modus beim Klicken mit der Maus, kann die Werte NORMAL, DELETE, CHANGE annehmen */
	public int modus = NORMAL;											// aktueller value

	private int line_length_li, line_length_re;						// L�nge der Signallinien f�r Inputs(li) und Outputs(re), wird beim berechnen des angeklickten Signals ben�tig, daher global

	private Font 			font;
	private FontMetrics 	fm;
	private Parser parser;

	private SignalDialog	signalDialog;
	private MainFrame 		parent;
	private EditFrame 		editFrame;
	private Statuszeile 	status;

	// Statusstring
	private static final String status_eingeben = "Add new Inputs/Outputs or change to simualtion-mode";

	/**
	* Konstruktor
	* @param mainFrame aufrufendes Fenster (MainFrame)
	* @param Fsm der zu bearbeitende Automat (FSM)
	* @param Status Statuszeile zur Ausgabe von Meldungen (Statuszeile)
	* @param eFrame Editor-Fenster (EditFrame)
	*/
	public Simulation(MainFrame mainFrame, FSM Fsm, Statuszeile Status, EditFrame eFrame) 
	{
		super();
		/* �bergebene Objekte speichern */
		fsm		= Fsm;
		//Outputs = fsm.outputs;
		//Inputs  = fsm.inputs;
		//zustandsfolge = fsm.zustandsfolge;
		parent = mainFrame;
		editFrame = eFrame;
		status = Status;
		parser = new Parser();

	/* Farben und Fonts setzen */
		this.setBackground(Color.lightGray);
		this.setForeground(Color.black);
		this.setLayout(null);
		font = new Font("Helvetica", Font.PLAIN, 12);
		this.setFont(font);
		fm = getFontMetrics(font);
	}

	/** Zeichnen des Schaltbildes */
	public void paint(Graphics g) 
	{
		Dimension d = this.size();								// Gr��e des Panels
		g.setColor(Color.lightGray);
		g.draw3DRect(0,0,d.width-1,d.height-1, false);	// 3D-Umrandung zeichnen
		g.setColor(Color.black);

/*	Aufbau des Schaltbildes:

			  AAAAAAAAAAAAAAAAAAAA---------
			  A	---------       |       | *****    	R1: Delta-Schaltnetz
	  ***** A   |       |       |   R3  | *****		R2: Taktglied
	  ***** A	|  R1   |       |       | *****		R3: Lambda-Schaltnetz
	  ***** ACCC|       |BBBB   ---------
			  A	---------   B								A, B, C: Verbindungspfeile zwischen den Schaltnetzen
			  A   ---------   B		|-------|				***** :	Signallinien f�r Inputs und Outputs
			  AAAA|  R2   |BBBB 		|legende|
				   ---------			|-------|
*/

		Polygon polyA, polyB, polyC;				// Verbindungspfeile zwischen den Schaltnetzen
		int rand_hor		= 1*d.width/21;		// horizontaler Abstand vom Rand 
		int rand_ver		= 1*d.height/21;		// vertikaler   Abstand vom Rand
		int abst_hor		= 1*d.width/21;		// horizontaler Abstand zwischen Rechtecken
		int abst_ver		= 1*d.height/21;		// vertikaler   Abstand zwischen Rechtecken
		int r_breite_li	= 6*d.width/21;		// Breite der linken Rechtecke (R1 und R2)
		int r_breite_re	= 4*d.width/21;		// Breite des rechten Rechtecks (R3)
		int r_hoehe_gr		= 11*d.height/21;		// H�he der gro�en Rechtecke (R1 und R3)
		int r_hoehe_kl		= 3*d.height/21;		// H�he des kleinen Rechtecks (R2)
		int r_offset		= 4*d.height/21;		// vertikale Verschiebung zwischen den beiden gro�en Rechtecken
		line_length_li		= 4*d.width/21;		// L�nge der Input-Linien, bereits oben deklariert, da sp�ter noch ben�tigt
		line_length_re		= 4*d.width/21;		// L�nge der Output-Linien, bereits oben deklariert, da sp�ter noch ben�tigt


		int r1x = rand_hor+line_length_li;								// Koordinaten linke obere Ecke R1
		int r1y = rand_ver+r_offset;
		int r2x = r1x;															// Koordinaten linke obere Ecke R2
		int r2y = r1y+r_hoehe_gr+abst_ver;
		int r3x = rand_hor+line_length_li+r_breite_li+abst_hor;	// Koordinaten linke obere Ecke R3
		int r3y = rand_ver;

		int border = 20;														// Rand im Delta-Schaltnetz zum Automaten

	/* Legende zeichnen */
		g.setColor(Color.lightGray);		
		int legx = d.width-line_length_li-rand_hor-r_breite_re/3;
		int legy = d.height-(rand_ver+r_hoehe_kl+abst_ver+r_offset/2)-5;
		int leg_width = line_length_li+r_breite_re/3;
		int leg_height = r_hoehe_kl+abst_ver+10;
				
		g.draw3DRect(legx,legy,leg_width,leg_height, false);										// Rahmen f�r die Legende
		g.setColor(Color.red);
		g.drawLine(legx+10, legy+leg_height/4, legx+leg_width-50, legy+leg_height/4);		// rote Linie
		g.setColor(Color.black);
		g.drawString("high", legx+leg_width-40, legy+leg_height/4+3);							// high
		g.setColor(Color.blue);
		g.drawLine(legx+10, legy+2*leg_height/4, legx+leg_width-50, legy+2*leg_height/4);// blaue Linie
		g.setColor(Color.black);
		g.drawString("low", legx+leg_width-40, legy+2*leg_height/4+3);							// low
		g.setColor(Color.green);
		g.drawLine(legx+10, legy+3*leg_height/4, legx+leg_width-50, legy+3*leg_height/4);// blaue Linie
		g.setColor(Color.black);
		g.drawString("undef", legx+leg_width-40, legy+3*leg_height/4+3);							// low

		

	/* Verbindungspfeile zeichnen */
		g.setColor(Color.white);

		polyA = new Polygon();					// Pfeil vom Taktglied zum Lamda-Schaltnetz	
		polyB = new Polygon();					// Pfeil vom Delta-Schaltnetz zum Taktglied
		polyC = new Polygon();					// Pfeil in das Delta-Schaltnetz

	
	if (!fsm.getMachineType())
	{
		/* falls MOORE, dann Pfeil in die Mitte zeichnen */
		/*   3    45
		      ----->6
				|9  87
				|            A
				|10 11
				-----
           2     1	
				*/
		polyA.addPoint(r2x, d.height-(rand_ver+r_offset/2)+5);									// 1
		polyA.addPoint(rand_hor+2*line_length_li/3-5, d.height-(rand_ver+r_offset/2)+5);	// 2
		polyA.addPoint(rand_hor+2*line_length_li/3-5, rand_ver+r_offset/2-5);				// 3
			polyA.addPoint(r3x-10, rand_ver+r_offset/2-5);											// 4, Pfeilspitze
			polyA.addPoint(r3x-10, rand_ver+r_offset/2-15); 										// 5
			polyA.addPoint(r3x, rand_ver+r_offset/2);													// 6
			polyA.addPoint(r3x-10, rand_ver+r_offset/2+15);											// 7
			polyA.addPoint(r3x-10, rand_ver+r_offset/2+5);											// 8
		polyA.addPoint(rand_hor+2*line_length_li/3+5, rand_ver+r_offset/2+5);				// 9
		polyA.addPoint(rand_hor+2*line_length_li/3+5, d.height-(rand_ver+r_offset/2)-5);	// 10
		polyA.addPoint(r2x, d.height-(rand_ver+r_offset/2)-5);									// 11
		g.fillPolygon(polyA);
	}
	
	if (fsm.getMachineType())
	{		
		/* falls MEALY, dann Pfeil weiter untern zeichnen um f�r Pfeil von den Inputs Platz zu lassen */
		/*   3    45
		      ----->6
				|9  87
				|          A
				|10 11
				-----
           2     1	
				*/
		polyA.addPoint(r2x, d.height-(rand_ver+r_offset/2)+5);									// 1
		polyA.addPoint(rand_hor+2*line_length_li/3-5, d.height-(rand_ver+r_offset/2)+5);	// 2
		polyA.addPoint(rand_hor+2*line_length_li/3-5, rand_ver+r_offset/4*3-5);				// 3
			polyA.addPoint(r3x-10, rand_ver+r_offset/4*3-5);											// 4, Pfeilspitze
			polyA.addPoint(r3x-10, rand_ver+r_offset/4*3-15); 										// 5
			polyA.addPoint(r3x, rand_ver+r_offset/4*3);													// 6
			polyA.addPoint(r3x-10, rand_ver+r_offset/4*3+15);											// 7
			polyA.addPoint(r3x-10, rand_ver+r_offset/4*3+5);											// 8
		polyA.addPoint(rand_hor+2*line_length_li/3+5, rand_ver+r_offset/4*3+5);				// 9
		polyA.addPoint(rand_hor+2*line_length_li/3+5, d.height-(rand_ver+r_offset/2)-5);	// 10
		polyA.addPoint(r2x, d.height-(rand_ver+r_offset/2)-5);									// 11
		g.fillPolygon(polyA);
	}


		/*    1     2
				-----|
				11	10|
					  |        B
				78	 9|
			  6<----|
      		54    3
			*/
		polyB.addPoint(r2x+r_breite_li,  d.height-(rand_ver+r_hoehe_kl+abst_ver+r_offset/2)-5 );									// 1
		polyB.addPoint(r2x+r_breite_li+abst_ver+r_breite_re/2+5, d.height-(rand_ver+r_hoehe_kl+abst_ver+r_offset/2)-5 ); 	// 2
		polyB.addPoint(r2x+r_breite_li+abst_ver+r_breite_re/2+5, d.height-(rand_ver+r_offset/2)+5);								// 3
			polyB.addPoint(r2x+r_breite_li+11, d.height-(rand_ver+r_offset/2)+5); 														// 4, Pfeilspitze
			polyB.addPoint(r2x+r_breite_li+11, d.height-(rand_ver+r_offset/2)+15);														// 5
			polyB.addPoint(r2x+r_breite_li+1, d.height-(rand_ver+r_offset/2));															// 6
			polyB.addPoint(r2x+r_breite_li+11, d.height-(rand_ver+r_offset/2)-15);														// 7
			polyB.addPoint(r2x+r_breite_li+11, d.height-(rand_ver+r_offset/2)-5);														// 8
		polyB.addPoint(r2x+r_breite_li+abst_ver+r_breite_re/2-5, d.height-(rand_ver+r_offset/2)-5);								// 9
		polyB.addPoint(r2x+r_breite_li+abst_ver+r_breite_re/2-5, d.height-(rand_ver+r_hoehe_kl+abst_ver+r_offset/2)+5 );	// 10
		polyB.addPoint(r2x+r_breite_li,  d.height-(rand_ver+r_hoehe_kl+abst_ver+r_offset/2)+5 );									// 11
		g.fillPolygon(polyB);

	/*
	   1    23
		----->4           C
	   7    65
			*/
		polyC.addPoint(rand_hor+2*line_length_li/3,d.height-(rand_ver+r_hoehe_kl+abst_ver+r_offset/2)-5);	// 1
			polyC.addPoint(r2x-10,d.height-(rand_ver+r_hoehe_kl+abst_ver+r_offset/2)-5);							// 2
			polyC.addPoint(r2x-10,d.height-(rand_ver+r_hoehe_kl+abst_ver+r_offset/2)-15);							// 3
			polyC.addPoint(r2x,d.height-(rand_ver+r_hoehe_kl+abst_ver+r_offset/2));									// 4
			polyC.addPoint(r2x-10,d.height-(rand_ver+r_hoehe_kl+abst_ver+r_offset/2)+15);							// 5
			polyC.addPoint(r2x-10,d.height-(rand_ver+r_hoehe_kl+abst_ver+r_offset/2)+5);							// 6
		polyC.addPoint(rand_hor+2*line_length_li/3,d.height-(rand_ver+r_hoehe_kl+abst_ver+r_offset/2)+5);	// 7
		g.fillPolygon(polyC);

		
	

	/* falls MEALY, dann noch Inputs in Lambda-Schaltnetz f�hren */
	if (fsm.getMachineType())
	{
		g.setColor(Color.white);
		Polygon polyD = new Polygon();					// Pfeil von den Inputs in das Lambda-Schaltnetz		

		/*   3    45
		      ----->6
			   |9  87
			   |            D
            |
           1 10
	                        */
		polyD.addPoint(rand_hor-5,r1y+r_hoehe_gr);						 							// 1
		polyD.addPoint(rand_hor-5, rand_ver+r_offset/4-5);											// 3
			polyD.addPoint(r3x-10, rand_ver+r_offset/4-5);											// 4, Pfeilspitze
			polyD.addPoint(r3x-10, rand_ver+r_offset/4-15); 										// 5
			polyD.addPoint(r3x, rand_ver+r_offset/4);													// 6
			polyD.addPoint(r3x-10, rand_ver+r_offset/4+15);											// 7
			polyD.addPoint(r3x-10, rand_ver+r_offset/4+5);											// 8
		polyD.addPoint(rand_hor+5, rand_ver+r_offset/4+5);											// 9
		polyD.addPoint(rand_hor+5,r1y+r_hoehe_gr);						 							// 10


		g.fillPolygon(polyD);
	}



	/* Inputs zeichnen */
		for (int i=0; i<fsm.getInputSize(); i++) 
		{						// alle Inputs durchgehen
			input = (Signal)fsm.inputs.elementAt(i);
			/* Farbe setzen */
			if (input.value == Signal.HIGH) g.setColor(Color.red);		// rot f�r HIGH
			else g.setColor(Color.blue);								// blau f�r LOW
			/* Position berechnen */
			input.ixpos = rand_hor;										// X-Position: Entfernung vom Rand
			input.iypos = r1y + i*(r_hoehe_gr / fsm.getInputSize())+((r_hoehe_gr / fsm.getInputSize() )/2);	// Y-Position: H�he von R1 auf die Inputs verteilen
			/* Zeichnen */
			g.drawLine(input.ixpos, input.iypos, input.ixpos+line_length_li, input.iypos);	// Linie zeichnen
			g.drawString(input.name, input.ixpos+5, input.iypos-1 );									// Name des Inputs
			g.fillOval(input.ixpos-3, input.iypos-3,6,6);												// Kreis am Anfang der Linie
		}

	/* Outputs zeichnen */
		for (int i=0; i<fsm.getOutputSize(); i++) 
		{
			// alle Outputs durchgehen
			output = (Signal)fsm.outputs.elementAt(i);
			if (fsm.getMachineType())
			{									// Wenn Mealy, dann auch beim �ndern der Inputs ggf. Outputs �ndern
				// outputs parsen 
				if (fsm.zustandsfolge.size()>0)
				{	
					String s=(String)((Zustand)(fsm.zustandsfolge.lastElement())).outputHash.get(output); // s enth�lt den logischen Ausdruck, der im aktuellen Zustand f�r den Output "signal" steht
					try 
					{
						if (parser.parse(s, fsm.inputs)) output.value=Signal.HIGH;		// s parsen und wenn true, dann value auf HIGH setzen
						else output.value=Signal.LOW;										// wenn false, dann value auf LOW setzen
					}
					catch (BadExpressionException e) 
					{									// falls Ausdruck im Zustand nicht korrekt
						status.set("Output "+output.name+" im Zustand "+((Zustand)fsm.zustandsfolge.lastElement()).name+" nicht richtig definiert!"); 
						output.value=Signal.UNDEF;											// value auf UNDEFiniert setzen
					}
				}
			}
			if (output.value == Signal.HIGH) g.setColor(Color.red);			// rot f�r HIGH
			else if (output.value == Signal.LOW) g.setColor(Color.blue);	// blau f�r LOW
			else g.setColor(Color.green);									// gr�n f�r undefiniert
			
		/* Position berechnen */
			output.ixpos=d.width-rand_hor;								// X-Position: Entfernung vom Rand
			output.iypos=r3y + i*(r_hoehe_gr / fsm.getOutputSize())+((r_hoehe_gr / fsm.getOutputSize() )/2);	// Y-Position: H�he von R3 auf die Inputs verteilen
		/* Zeichnen */
			g.drawLine(output.ixpos-line_length_re, output.iypos, output.ixpos, output.iypos);		// Linie zeichnen
			g.drawString(output.name, output.ixpos-fm.stringWidth(output.name)-5, output.iypos-1);	// Name des Inputs
			g.fillOval(output.ixpos-3, output.iypos-3,6,6);														// Kreis am Anfang der Linie
		}
	
	/* Rechtecke zeichnen */
	g.setColor(Color.lightGray);
	g.draw3DRect(r1x,r1y,r_breite_li, r_hoehe_gr, true);	// R1
	g.draw3DRect(r2x,r2y,r_breite_li, r_hoehe_kl, true);	// R2
	g.draw3DRect(r3x,r3y,r_breite_re, r_hoehe_gr, true);	// R3

	/* Automat einzeichnen */
	if (parent.modus==parent.SIMULIEREN)
	{
		Zustand lastZustand=(Zustand)fsm.zustandsfolge.lastElement();	// enth�lt da im Simulationsmodus mindestens den Startzustand
		fsm.calculateTransition();	
		// Zeichenroutine wird aufgerufen mit last Zustand (entweder Startuzustand oder null)
		editFrame.editCanvas.drawFSM(r1x+border,r1y+border,
			r_breite_li-2*border,r_hoehe_gr-2*border,g,lastZustand, fsm.aktivierteTransitionen);
	}
	// Zeichenroutine wird aufgerufen (ohne Zustand und Transition)
	else editFrame.editCanvas.drawFSM(r1x+border,r1y+border,r_breite_li-2*border,r_hoehe_gr-2*border,g,null);
	}

	/** �ndert den Modus auf CHANGE */
	public void Aendern() 
	{
		modus = CHANGE;		// Modus auf �ndern setzen
	}

	/** �ndert den Modus auf DELETE */
	public void Loeschen() 
	{
		modus = DELETE;		// Modus auf l�schen setzen
	}

	/** verarbeitet je nach Modus einen Mausklick */
	public boolean mouseDown(Event e,int x,int y) 
	{
		Selected = null;													// ausgew�hltes Signal auf null setzen
		/* zeigt Maus auf einen Input? */
		int i = 0;
		while (i<fsm.getInputSize())
		{
			input = (Signal)fsm.inputs.elementAt(i);					// nur kurz zwischenspeichern
			if (input.ixpos-4<=x && x<=input.ixpos+line_length_li &&
			input.iypos+3>=y && y>=input.iypos-10) 
			{				// zeigt (x,y) auf einen Input?
				Selected = input;											// diesen Input merken
				break;														// while Schleife verlassen
			}
			i++;
		}
	/* zeigt Maus auf einen Output? */
		i = 0;
		while (i<fsm.getOutputSize()) 
		{
			output = (Signal)fsm.outputs.elementAt(i);				// nur kurz zwischenspeichern
			if (output.ixpos-line_length_re<=x && x<=output.ixpos+4 &&
			output.iypos+3>=y && y>=output.iypos-10) 
			{			// zeigt (x,y) auf einen Output?
				Selected = output;										// diesen Output merken
				break;														// while Schleife verlassen
			}
			i++;
		}
	/* je nach Modus das ausgew�hlte Signal bearbeiten */
	/* Modus DELETE */
		if ((modus==DELETE)&&(Selected!=null)) 
		{						// falls Modus DELETE und ein Signal ausgew�hlt
			//if ((Selected.in_out==Signal.IN)&&(fsm.inputs.contains(Selected)))			// falls Input angew�hlt (als Button und angeklickt)
			if (Selected.in_out==Signal.IN)									// falls Input angew�hlt (als Button und angeklickt)
			{
				fsm.deleteInput(Selected);							// Input l�schen
				parent.editFrame.setPanel(parent.editFrame.editCanvas.getSelected());
			}
			else if (Selected.in_out==Signal.OUT)	// falls Output angew�hlt (als Button und angeklickt)
			{
				fsm.deleteOutput(Selected);							// Output l�schen
				parent.editFrame.setPanel(parent.editFrame.editCanvas.getSelected());
			}
			modus = NORMAL;													// danach Modus wieder auf NORMAL setzen
			Selected = null;													// Selected wieder auf null setzen
			status.set(status_eingeben);									// den Status wieder zur�cksetzen
			repaint();
			return true;
		}
	/* Modus CHANGE */		
		else if ((modus==CHANGE)&&(Selected!=null)) 
		{
			// falls Modus CHANGE und ein Signal ausgew�hlt
			if (Selected.in_out==Signal.IN)
			{		
				// falls Input angew�hlt
				SignalDialog signalDialog = new SignalDialog	
					(parent, "Input �ndern", true, fsm, Selected);	// das Dialog-Fenster zum �ndern des Inputs aufrufen
				signalDialog.resize(300,200);
				signalDialog.show();
				signalDialog.resize(300,200);
			}
			else if (Selected.in_out==Signal.OUT) 
			{
				// falls Output angew�hlt
				SignalDialog signalDialog = new SignalDialog
					(parent, "Change Output", true, fsm, Selected);	// das Dialog-Fenster zum �ndern des Outputs aufrufen
				signalDialog.resize(300,200);
				signalDialog.show();
				signalDialog.resize(300,200);
			}
			modus = NORMAL;													// danach Modus wieder auf NORMAL setzen
			Selected = null;													// Selected wieder auf null setzen
			status.set(status_eingeben);									// den Status wieder zur�cksetzen
			repaint();
			return true;
		}
		/* Modus NORMAL */		
		else if ((modus==NORMAL)&&(Selected!=null)) 
		{
			// Bei Anklicken Wechsel des Wertes / der Farbe
			if (fsm.inputs.contains(Selected)) 
			{
				// aber nur bei Inputs
				if (Selected.value == Signal.HIGH) Selected.value=Signal.LOW;	// Wert durch anklicken �ndern
				else Selected.value=Signal.HIGH;
				if ((parent.modus==parent.SIMULIEREN)&&(fsm.getMachineType()))	// Bei Mealy beim Wechseln des Eingangswertes Zwischentakt generieren
				{
					fsm.zwischentakt();
					parent.impulsFrame.setBars();	// Scrollbalken setzen
				}
			}
			Selected = null;												// Selected wieder auf null setzen
			status.set(status_eingeben);								// den Status wieder zur�cksetzen
			repaint();
			return true;
		}
		else {
			modus = NORMAL;												// Modus wieder auf NORMAL setzen
			status.set(status_eingeben);								// den Status wieder zur�cksetzen
			return super.mouseDown(e, x, y);
		}
	}
}
