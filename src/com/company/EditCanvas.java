package com.company;
import java.awt.*;
import java.util.*;

/**
* <IMG SRC="../doku/images/logo_klein.gif" ALT="JavaFSM"><BR>
* grafic display to build and define machine
*/
class EditCanvas extends Canvas {
	/** horizontaler Offset zum Zustandsmittelpunkt (beim Verschieben) */
	public int xOffset = 0;
	/** vertikaler Offset zum Zustandsmittelpunkt (beim Verschieben) */
	public int yOffset = 0;
	private final static int MOVE = 0;		// Verschiebe - Modus
	private final static int LOCK = 1;		// Fest - Modus (nicht implementiert)
	private final static int STATE = 2;		// neue Zust�nde einf�gen
	private final static int TRANSITION = 3;// neue Transitionen einf�gen
	private final static int KOMMENTAR = 4;	// neue Kommentare einf�gen
	private final static int DELETE = 5;	// Zust�nde/Transitionen l�schen
	private final static int START = 6;		// Startzustand markieren
	private int modus = MOVE;


	private static int r = 16;				// Radius der Zustandskreise
	private static int d = 6;				// Abstand von entgegengesetzten Transitionen
	private FSM fsm;						// das FSM-Objekt
	private EditFrame parentFrame;			// Aufrufendes (Parent)-EditFrame
	private Zustand zustand;				// Puffervariablen f�r eine Zustand
	private Transition transition;			// bzw. eine Transition
	private Kommentar kommentar;			// bzw. einen Kommentar
	private Object selected;				// der angeklickte Zustand / Transition / Kommentar
	private int dx,dy;						// der Offset dazu zum Mauszeiger
	private Statuszeile status;
	private Font zu_font, tr_font;
	private FontMetrics zu_fm, tr_fm;
	private int zu_font_height, tr_font_height;

	/** 
	* Konstruktor
	* @param Fsm endlicher Automat (FSM)
	* @param editFrame aufrufendes Fenster (EditFrame)
	* @param statuszeile externe Statuszeile, f&uuml;r Meldungen (Statuszeile)
	*/
	public EditCanvas(FSM Fsm, EditFrame editFrame, Statuszeile statuszeile) {
		super();
		fsm = Fsm;
		parentFrame = editFrame;
		selected = null;
		status = statuszeile;
		zu_font = new Font("Helvetica",Font.BOLD,11);
		tr_font = new Font("Helvetica",Font.PLAIN,9);
		zu_fm = getFontMetrics(zu_font);
		tr_fm = getFontMetrics(tr_font);
		zu_font_height = zu_fm.getAscent();
		tr_font_height = tr_fm.getAscent();
	}

// **********************************************************************************************************************

	/**
	* zeichnet den gesamten Automaten neu
	*/
	public void paint(Graphics g) {
		int i,j;
		Point p1,p2;
		Dimension dim = this.size();
		g.setColor(Color.lightGray);
		g.draw3DRect(0,0,dim.width-1,dim.height-1,false);
		g.setFont(zu_font);
		for (i=0; i<fsm.zustaende.size(); i++) {
			zustand = (Zustand)fsm.zustaende.elementAt(i);		// nur kurz zwischenspeichern
			if (zustand==selected) g.setColor(Color.red);
				else g.setColor(Color.black);
			g.drawOval((zustand.x-r-xOffset),(zustand.y-r-yOffset),(r+r),(r+r));
			g.drawOval((zustand.x-r-xOffset+1),(zustand.y-r-yOffset+1),(r+r-2),(r+r-2));
			if (zustand.isStart) {
				g.drawOval((zustand.x-r-xOffset+4),(zustand.y-r-yOffset+4),(r+r-8),(r+r-8));
				g.drawOval((zustand.x-r-xOffset+5),(zustand.y-r-yOffset+5),(r+r-10),(r+r-10));
			}
			g.setColor(Color.black);
			g.drawString(zustand.name,zustand.x-xOffset+r+5,zustand.y-yOffset+(zu_font_height/2));
		}
		g.setFont(tr_font);
		for (i=0; i<fsm.transitionen.size(); i++) {
			transition = (Transition)fsm.transitionen.elementAt(i);// nur kurz zwischenspeichern
			if (transition==selected) g.setColor(Color.red);
				else g.setColor(Color.black);
			p1=new Point(transition.von.x-xOffset,transition.von.y-yOffset);
			p2=new Point(transition.nach.x-xOffset,transition.nach.y-yOffset);
			if (transition.von==transition.nach) {				// Transition auf sich selbst
				p1.x-=r/2;
				p2.x=p1.x;
				p1.y-=r-1;
				p2.y-=r+r;
				g.drawLine((p1.x+r),p1.y,(p1.x+r),p2.y);		// rechte Linie
				g.drawArc(p1.x,(p2.y-(r/2)),r,r,0,180);			// oberer Halbkreis
				p2.y-=r+1;										// Anpassung f�r drawArrow-Funktion
				p1.y+=r+1;
				drawArrow(p2,p1,g);								// linke Linie (Pfeil)
				g.drawString(transition.function,p2.x+r+5,p2.y+r);
			}
			else {
				int dx=p2.x-p1.x;
				int dy=p2.y-p1.y;
				int h=(int) Math.sqrt(dx*dx + dy*dy);
				if (fsm.findTransition(transition.nach,transition.von)>=0) {	// Pfeil etwas versetzen ...
					p1.x+=(dy*d)/h;
					p2.x+=(dy*d)/h;
					p1.y-=(dx*d)/h;
					p2.y-=(dx*d)/h;
					if (dy>=0) g.drawString(transition.function,p1.x+((p2.x-p1.x)/2)+(dy*8/h),p1.y+((p2.y-p1.y)/2)+(tr_font_height/2));
					else g.drawString(transition.function,p1.x+((p2.x-p1.x)/2)+(dy*8/h)-tr_fm.stringWidth(transition.function),p1.y+((p2.y-p1.y)/2)+(tr_font_height/2));
				}
				else g.drawString(transition.function,p1.x+((p2.x-p1.x)/2)+(Math.abs(dy*8/h)),p1.y+((p2.y-p1.y)/2)+(tr_font_height/2));
				drawArrow(p1,p2,g);
			}
		}
		g.setFont(zu_font);
		for (i=0; i<fsm.kommentare.size(); i++) {
			kommentar = (Kommentar)fsm.kommentare.elementAt(i);	// nur kurz zwischenspeichern
			if (kommentar==selected) g.setColor(Color.red);
				else g.setColor(Color.lightGray);
			g.draw3DRect(kommentar.x-xOffset, kommentar.y-yOffset, kommentar.getWidth(zu_fm)+10, kommentar.getHeight(zu_fm)+10, false);
			if (kommentar==selected) g.setColor(Color.red);
				else g.setColor(Color.black);
			String[] lines = kommentar.getLines();
			int yp = kommentar.y-yOffset+zu_fm.getAscent()+5;
			if (lines[0]!=null)
				for (j=0; j<lines.length; j++)
				{
					g.drawString(lines[j], kommentar.x-xOffset+5, yp);
					yp += zu_fm.getHeight();
				}
		}
	}

// **********************************************************************************************************************

	/**
	* verarbeitet Modus-abh&auml;ngig einen Mausklick
	* @see moveMode()
	* @see zustandMode()
	* @see transitionMode()
	* @see kommentarMode()
	* @see deleteMode()
	* @see startMode()
	*/
	public boolean mouseDown(Event e,int x,int y) {
		if (modus==MOVE) {										// MOVE - Modus ???
			selected=findObject(x+xOffset,y+yOffset);
			parentFrame.setPanel(selected);
		}
		else if (modus==STATE) {								// MODUS - Status einf�gen ???
			// Abfrage, ob zu nahe an anderem Zustand fehlt noch !?
			zustand = fsm.newZustand(freeName(),x+xOffset,y+yOffset); // neuen Zustand generieren
			if (zustand.x<r) zustand.x=r;
			if (zustand.y<r) zustand.y=r;
			if (zustand.x>(parentFrame.VirtWidth-r)) zustand.x=parentFrame.VirtWidth-r;
			if (zustand.y>(parentFrame.VirtHeight-r)) zustand.y=parentFrame.VirtHeight-r;
			selected=zustand;									// neuen Zustand gleich aktivieren
			parentFrame.setPanel(selected);
//			dx = x+xOffset - zustand.x;							// Verschiebung zwischen Klickpunkt und 
//			dy = y+yOffset - zustand.y;							// Mittelpunkt ist (dx,dy)
		}
		else if (modus==TRANSITION) {							// MODUS - Transition einf�gen
			if (selected instanceof Zustand) {					// ist schon ein Zustand aktiviert ???
				Object selected2=findObject(x+xOffset,y+yOffset);// dann den zweiten ermitteln ...
			 	if (selected2 instanceof Zustand)				// Wenn einer gefunden, dann Transition hinzuf�gen
					fsm.newTransition((Zustand)selected,(Zustand)selected2); // FSM pr�ft, ob schon vorhanden
				selected=selected2;								// zweites Objekt aktivieren
				parentFrame.setPanel(selected);
				transitionMode();								// Statuszeile neu setzen
			}
			else {												// noch kein Zustand aktiv -> ersten ausw�hlen !
				selected=findObject(x+xOffset,y+yOffset);
				parentFrame.setPanel(selected);
				transitionMode();								// Statuszeile neu setzen
			}
		}
		else if (modus==KOMMENTAR) {							// MODUS - Kommentare einf�gen
			kommentar = fsm.newKommentar("",x+xOffset,y+yOffset); // neuen Kommentar generieren
			int w = kommentar.getWidth(zu_fm);
			int h = kommentar.getHeight(zu_fm);
			if (kommentar.x<5) kommentar.x=5;
			if (kommentar.y<5) kommentar.y=5;
			if (kommentar.x>(parentFrame.VirtWidth-w-5)) kommentar.x=parentFrame.VirtWidth-w-5;
			if (kommentar.y>(parentFrame.VirtHeight-h-5)) kommentar.y=parentFrame.VirtHeight-h-5;
			selected = kommentar;								// neuen Kommentar gleich aktivieren
			parentFrame.setPanel(selected);
//			dx = x+xOffset - kommentar.x;						// Verschiebung zwischen Klickpunkt und 
//			dy = y+yOffset - kommentar.y;						// Eckpunkt ist (dx,dy)
		}
		else if (modus==DELETE) {
			selected=findObject(x+xOffset,y+yOffset);
			if (selected instanceof Zustand) fsm.deleteZustand((Zustand)selected);
			else if (selected instanceof Transition) fsm.deleteTransition((Transition)selected);//Transition kann einfach geloescht werden
			else if (selected instanceof Kommentar) fsm.deleteKommentar((Kommentar)selected);	//Transition kann einfach geloescht werden
			selected=null;
			parentFrame.setPanel(null);
		}
		else if (modus==START) {
			selected=findObject(x+xOffset,y+yOffset);
			if (selected instanceof Zustand) fsm.setStart((Zustand)selected);
			parentFrame.setPanel(selected);
		}
		repaint();
		return true;
	}

// **********************************************************************************************************************

	/**
	* verschiebt den selektierten Zustand bzw. Kommentar
	*/
	public boolean mouseDrag(Event e, int x, int y) {
		if (modus==MOVE && selected instanceof Zustand) {
			int a = x+xOffset - dx;						// (dx,dy):Verschiebung zwischen Klickpunkt und Mittelpunkt
			int b = y+yOffset - dy;						// (a,b): neuer Mittelpunkt des Zustandes (falls nicht tooClose)
			int d1,d2;

			if (a<r) a=r;								// ausserhalb der virtuellen Fl�che ?
			else if (a>=parentFrame.VirtWidth-r) a=parentFrame.VirtWidth-r-1;
			if (b<r) b=r;
			else if (b>=parentFrame.VirtHeight-r) b=parentFrame.VirtHeight-r-1;

			boolean tooClose = false;
			if ((selected!=null)&&(selected instanceof Zustand)) {
				for (int i=0; i<fsm.zustaende.size(); i++) {
					zustand = (Zustand)fsm.zustaende.elementAt(i); // nur kurz zwischenspeichern
					d1 = a - zustand.x;					// (d1,d2) Abstand zwischen den zwei Zustandsmittelpunkten
					d2 = b - zustand.y;					// darf nicht <2r sein
					if ((zustand!=selected)&&(Math.sqrt( d1*d1 + d2*d2 )<(2*r))) tooClose=true;
				}
				if (!tooClose) {
					((Zustand)selected).x = a;			// wenn nicht tooClose, wird (a,b) neuer Mittelpunkt
					((Zustand)selected).y = b;
				}
				repaint();
			}
		}
		else if (modus==MOVE && selected instanceof Kommentar) {
			Kommentar komm = (Kommentar)selected;
			int w = komm.getWidth(zu_fm);
			int h = komm.getHeight(zu_fm);
			int a = x+xOffset - dx;						// (dx,dy):Verschiebung zwischen Klickpunkt und Eckpunkt
			int b = y+yOffset - dy;						// (a,b): neuer Eckpunkt des Kommentares
			if (a<5) a=5;								// ausserhalb der virtuellen Fl�che ?
			if (b<5) b=5;								// ausserhalb der virtuellen Fl�che ?
			if (a>parentFrame.VirtWidth-5-w) a=parentFrame.VirtWidth-5-w;	// ausserhalb der virtuellen Fl�che ?
			if (b>parentFrame.VirtHeight-5-h) b=parentFrame.VirtHeight-5-h;	// ausserhalb der virtuellen Fl�che ?
			komm.x = a;
			komm.y = b;
			repaint();
		}
		return true;
	}

// **********************************************************************************************************************

	private Object findObject(int x, int y) {
		int i;
		Object sel=null;
		for (i=0; i<fsm.zustaende.size(); i++) {		// Zustaende pr�fen
			zustand = (Zustand)fsm.zustaende.elementAt(i);	// nur kurz zwischenspeichern
			dx = x - zustand.x;							// Verschiebung zwischen Klickpunkt und 
			dy = y - zustand.y;							// Mittelpunkt ist (dx,dy)
			if ((Math.sqrt( dx*dx + dy*dy ))<r) { // wenn Strecke zwischen Klickpunkt und Mittelpunkt < r,
														// dann liegt Klickpunkt im Zustand
				sel = zustand;							// diesen Zustand merken
				break;									// Schleife verlassen
			}
		}
		if (sel==null) {								// falls kein Zustand, dann nahe einer Transition ???
			int x1,x2,y1,y2;
			double j;
			for (i=0; i<fsm.transitionen.size(); i++) {
				transition = (Transition)fsm.transitionen.elementAt(i); // nur kurz zwischenspeichern
				if (transition.von==transition.nach) {	// Schleife abtesten...
					x1=transition.von.x;
					y1=transition.nach.y;
					if ((x>=(x1-(r/2)-2))&&(x<=(x1+(r/2)+2))&&(y<=y1)&&(y>=(y1-((int)(3.5*r))))) {
						sel=transition;					// ist innerhalb der Schleife !
						break;
					}
				}
				else {
					x1=transition.von.x;
					y1=transition.von.y;
					x2=transition.nach.x;
					y2=transition.nach.y;
					if (fsm.findTransition((Zustand)transition.nach,(Zustand)transition.von)>=0) {
						int sx=x2-x1;					// X-Abstand
						int sy=y2-y1;					// Y-Abstand
						int h=(int) Math.sqrt(sx*sx + sy*sy);		// L�nge der Diagonalen
						x1+=(sy*d)/h;					// x-koord. entsprechend dem y-Verh�ltnis verschieben
						x2+=(sy*d)/h;					// ...
						y1-=(sx*d)/h;
						y2-=(sx*d)/h;
					}
					j = Math.sqrt((x-x1)*(x-x1)+(y-y1)*(y-y1));	// Abstand x/y zu Zustand1
					j+= Math.sqrt((x-x2)*(x-x2)+(y-y2)*(y-y2));	// Abstand x/y zu Zustand2
					if (j<=(Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1))+0.5)) {
												// Vergleich der Strecke zwischen den Zust�nden und der
												// Summe der Strecken zwischen Zustand.vor
												// und Klickpunkt und Klickpunkt und Zustand.nach
						sel=transition;			// Transition gefunden !!!
						break;
					}
				}
			}
		}
		if (sel==null) {						// falls kein Zustand und keine Transition gefunden
			for (i=0; i<fsm.kommentare.size(); i++)
			{
				kommentar=(Kommentar)fsm.kommentare.elementAt(i);
				if ((kommentar.x<x)&&(kommentar.x+kommentar.getWidth(zu_fm)+10>x)&&
					(kommentar.y<y)&&(kommentar.y+kommentar.getHeight(zu_fm)+10>y))
				{
					sel = kommentar;
					dx = x - kommentar.x;			// Verschiebung zwischen Klickpunkt und 
					dy = y - kommentar.y;			// Mittelpunkt ist (dx,dy)
					break;
				}
			}
		}
		return sel;
	}

// **********************************************************************************************************************

	/**
	* liefert das derzeit selektierte Objekt (Zustand, Transition oder null)
	* @return Object selektiertes Objekt
	*/
	public Object getSelected() {
		return selected;
	}

// **********************************************************************************************************************

	/**
	* wechselt in den Verschiebe-Modus
	*/
	public void moveMode() {
		modus = MOVE;
		status.set("Move objecs with mouse");
	}

// **********************************************************************************************************************

	/**
	* wechselt in den Einf&uuml;ge-Modus f&uuml;r Zust&auml;nde
	*/
	public void zustandMode() {
		modus = STATE;
		status.set("New state: click on desired Position");
	}

// **********************************************************************************************************************

	/**
	* wechselt in den Einf&uuml;ge-Modus f&uuml;r Transitionen
	*/
	public void transitionMode() {
		modus = TRANSITION;
		if (selected instanceof Zustand) status.set("New Transition: click on 2nd state");
		else status.set("New Transition: click on first state");
	}

// **********************************************************************************************************************

	/**
	* wechselt in den Einf&uuml;ge-Modus f&uuml;r Kommentare
	*/
	public void commentMode() {
		modus = KOMMENTAR;
		status.set("New Comment: click on desired Position");
	}

// **********************************************************************************************************************

	/**
	* wechselt in den L&ouml;sch-Modus
	*/
	public void deleteMode() {
		modus = DELETE;
		status.set("Delete: click on object to delete");
	}

// **********************************************************************************************************************

	/**
	* wechselt in den Modus zum Festlegen des Startzustandes
	*/
	public void startMode() {
		modus = START;
		status.set("Click on new start-state");
	}

// **********************************************************************************************************************

	private String freeName() {
		Zustand zustand;
		String str = "";
		boolean isFree;
		for (int i=1; i<=(fsm.zustaende.size()+1); i++) {
			str = "State " + i;
			isFree = true;
			for (int j=0; j<fsm.zustaende.size(); j++) {
				zustand = (Zustand)fsm.zustaende.elementAt(j);
				if (zustand.name.toUpperCase().equals(str.toUpperCase())) isFree = false;
			}
			if (isFree) break;
		}
		return str;
	}

// **********************************************************************************************************************

	/**
	* zeichnet eine Transition (von Rand zu Rand der Zust&auml;nde)
	* @param p1 Mittelpunkt	des ersten Zustandes (Point)
	* @param p2 Mittelpunkt	des zweiten Zustandes (Point)
	* @param g grafischer Kontext, in den gezeichnet werden soll (Graphics)
	*/
	private void drawArrow(Point p1, Point p2, Graphics g) {
		drawScaledArrow(p1,p2,1,g);
	}

	private void drawScaledArrow(Point p1, Point p2, double q, Graphics g) {
		//                   PA2
		//                   |\
		//                   |  \
		//                   |    \
		//   ---------------PA3    * PA1
		//                   |    /
		//                   |  /
		//                   |/
		//                   PA4

		Point a1, a2, a3, a4;	/* corners of this arrow in world coords, wird noch um Offset verschoben */
		Point o;				/* Punkt als Offset zum Kreisrand */
		double dx, dy;
		double nx, ny;
		double ox, oy;
		double sqnorm;
		double length = 10.0 * q;	/* Gr��e der Spitze am Pfeil (a1<->a3) */
		double width = 2.5 * q;		/* "Spitzheit" des Pfeils -> je gr��er, desto spitzer(a2<->a3)*/
		if (q<0.25) {				// Pfeilspitze nicht kleiner als ein Minimum ...
			length = 10.0 * 0.25;
			width = 2.5 * 0.25;
			}
		dx = p2.x - p1.x;
		dy = p2.y - p1.y;
		sqnorm = Math.sqrt( dx*dx + dy*dy );
		if (sqnorm == 0.0) sqnorm = 1;
	// normalized vector and orthogonal vector for this line
		nx =  dx / sqnorm;
		ny =  dy / sqnorm;
		ox = -dy / sqnorm;
		oy =  dx / sqnorm;
	// calculate arrow point coordinates
		a1 = new Point( p2.x, p2.y );
	//	a3 = new Point( (int) (p2.x - length*nx + 0.5),(int) (p2.y - length*ny + 0.5) );
		a2 = new Point( (int) (p2.x - (length*nx) + (width*ox) + 0.5),(int) (p2.y - (length*ny) + (width*oy) + 0.5)  );
		a4 = new Point( (int) (p2.x - (length*nx) - (width*ox) + 0.5),(int) (p2.y - (length*ny) - (width*oy) + 0.5)  );
	// calculate Offset to Circleline
		o = new Point((int)((dx*((r*q)+1))/sqnorm),(int)((dy*((r*q)+1))/sqnorm));
	// draw arrow on screen
		g.drawLine( p1.x+o.x, p1.y+o.y,   a1.x-o.x, a1.y-o.y);
		g.drawLine( a2.x-o.x, a2.y-o.y,   a1.x-o.x, a1.y-o.y);
		g.drawLine( a4.x-o.x, a4.y-o.y,   a1.x-o.x, a1.y-o.y);
	}

// **********************************************************************************************************************

	/**
	* zeichnet einen skalierten Automaten ohne Beschriftung (max. ein aktives Element)
	* @param xPos horizontale Position, ab der gezeichnet wird (int)
	* @param yPos vertikale Position, ab der gezeichnet wird (int)
	* @param width Breite, die zur Verf&uuml;gung steht (int)
	* @param height H&ouml;he, die zur Verf&uuml;gung steht (int)
	* @param g graphischer Kontext, in den gezeichnet werden soll (Graphics)
	* @param active Objekt (Zustand, Transition oder null), das rot (aktiviert) gezeichnet wird (Object)
	*/
	public void drawFSM(int xPos, int yPos, int width, int height, Graphics g, Object active) {
		drawFSM(xPos, yPos, width, height, g, active, null);
	}

	/**
	* zeichnet einen skalierten Automaten ohne Beschriftung	(mit mehreren aktivierten Transitionen)
	* @param xPos horizontale Position, ab der gezeichnet wird (int)
	* @param yPos vertikale Position, ab der gezeichnet wird (int)
	* @param width Breite, die zur Verf&uuml;gung steht (int)
	* @param height H&ouml;he, die zur Verf&uuml;gung steht (int)
	* @param g graphischer Kontext, in den gezeichnet werden soll (Graphics)
	* @param active Objekt (Zustand, Transition oder null), das rot (aktiviert) gezeichnet wird (Object)
	* @param active_trans Vector, der die aktivierten Transitionen enth&auml;lt (Vector)
	*/
	public void drawFSM(int xPos, int yPos, int width, int height, Graphics g, Object active, Vector active_trans) {
		Zustand zustand;
		Transition transition;
		int i;
		Point p1,p2;
		double q,qx,qy;
		int xMin = parentFrame.VirtWidth;
		int xMax = 0;
		int yMin = parentFrame.VirtHeight;
		int yMax = 0;
		for (i=0; i<fsm.zustaende.size(); i++) {			// benutzen Bereich berechnen
			zustand = (Zustand)fsm.zustaende.elementAt(i);	// nur kurz zwischenspeichern
			if (zustand.x<xMin) xMin=zustand.x;
			if (zustand.x>xMax) xMax=zustand.x;
			if (zustand.y<yMin) yMin=zustand.y;
			if (zustand.y>yMax) yMax=zustand.y;
		}
		xMin -= r;
		xMax += r;
		yMin -= r;
		yMax += r;
		qx = (double)width / (xMax - xMin);					// horizontales Verh�ltnis
		qy = (double)height / (yMax - yMin);				// vertikales Verh�ltnis
		if (qx<qy) q=qx;									// kleineres Verh�ltnis benutzen
			else q=qy;
		if (q>0.7) q=0.7;									// nicht vergr��ern !
		for (i=0; i<fsm.zustaende.size(); i++) {
			zustand = (Zustand)fsm.zustaende.elementAt(i);	// nur kurz zwischenspeichern
			if (zustand==active) g.setColor(Color.red);
				else g.setColor(Color.black);
			g.drawOval((int)((zustand.x-r-xMin)*q)+xPos,(int)((zustand.y-r-yMin)*q)+yPos,(int)((r+r)*q),(int)((r+r)*q));
			if (zustand.isStart) g.drawOval((int)((zustand.x-r-xMin)*q)+xPos+2,(int)((zustand.y-r-yMin)*q)+yPos+2,(int)((r+r)*q)-4,(int)((r+r)*q)-4);
		}
		for (i=0; i<fsm.transitionen.size(); i++) {
			transition = (Transition)fsm.transitionen.elementAt(i);	// nur kurz zwischenspeichern
			if ((active_trans!=null)&&(active_trans.contains(transition))) g.setColor(Color.red);
				else g.setColor(Color.black);
			p1=new Point((int)((transition.von.x-xMin)*q)+xPos,(int)((transition.von.y-yMin)*q)+yPos);
			p2=new Point((int)((transition.nach.x-xMin)*q)+xPos,(int)((transition.nach.y-yMin)*q)+yPos);
			if (transition.von==transition.nach) {
				p1.x-=(new Double((r/2)*q)).intValue();
				p2.x= p1.x;
				p1.y-=(new Double((r-1)*q)).intValue();
				p2.y-=(new Double((r+r)*q)).intValue();
				g.drawLine((int)(p1.x+(r*q)),p1.y,(int)(p1.x+(r*q)),p2.y);			// rechte Linie

				g.drawArc(p1.x,(int)(p2.y-(r/2*q)),(int)(r*q),(int)(r*q),0,180+20);	// oberer Halbkreis
				p2.y-=(new Double((r+1)*q)).intValue();											// Anpassung f�r drawArrow-Funktion
				p1.y+=(new Double((r+1)*q)).intValue();
				drawScaledArrow(p2,p1,q,g);								// linke Linie (Pfeil)
			}
			else {
				if (fsm.findTransition(transition.nach,transition.von)>=0) {
					int dx=p2.x-p1.x;
					int dy=p2.y-p1.y;
					int h=(int) Math.sqrt(dx*dx + dy*dy);
					p1.x+=(new Double(((dy*d)/h)*q)).intValue();
					p2.x+=(new Double(((dy*d)/h)*q)).intValue();
					p1.y-=(new Double(((dx*d)/h)*q)).intValue();
					p2.y-=(new Double(((dx*d)/h)*q)).intValue();
				}
				drawScaledArrow(p1,p2,q,g);
			}
		}
	}
}

