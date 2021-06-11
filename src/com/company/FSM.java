package com.company;
import java.awt.*;
import java.applet.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.Integer;

/**
* <IMG SRC="../doku/images/logo_klein.gif" ALT="JavaFSM"><BR>
* Finite State Machine - Endlicher Automat
*/
 class FSM {
	/** Name des Automaten */
	public String name;
	/** Vektor, der die  Inputs enth&auml;lt */
	public Vector inputs;
	/** Vektor, der die  Outputs enth&auml;lt */
	public Vector outputs;
	/** Vektor, der die  Zust&auml;nde enth&auml;lt */
	public Vector zustaende;
	/** Vektor, der die  Transitionen enth&auml;lt */
	public Vector transitionen;
	/** Vektor, der die  Kommentare enth&auml;lt */
	public Vector kommentare;
	/** Vektor, der die Zustandsfolge bei der Simulation enth&auml;lt; das letzte 
		Element ist der aktuelle Zustand */
	public Vector zustandsfolge;
	/** Vektor, der die bei der aktuellen Belegung 
	aktivierten Transitionen enth&auml;lt <BR> wird in calculateTransitionen berechnet, nachdem Input-Werte ver&auml;ndert wurden*/
	public Vector aktivierteTransitionen;
	/** Vektor, der f&uuml;r echte Takte den String "true" und f&uuml;r Zwischentakte bei Mealy den String "false" enth&auml;lt */
	public Vector taktfolge;
	/** gibt den Machine-Type an (kann die Werte MEALY oder MOORE annehmen) */
	public int mealy_moore;
	/** Konstante f&uuml;r Machine-Type = Moore */
	public final static int MOORE=0;
	/** Konstante f&uuml;r Machine-Type = Mealy */
	public final static int MEALY=1;
	/** Parser zum Parsen der logischen Ausdr&uuml;cke */
	private Parser parser;
	/** Statuszeile im MainFrame */
	private Statuszeile status;

	/**
	* Konstruktor
	* @param Status Statuszeile zur Ausgabe von Fehlermeldungen (Statuszeile)
	*/
	public FSM(Statuszeile Status)
	{
		name					= "No Name";
		status					= Status;
		inputs 					= new Vector(10,10);			
		outputs 				= new Vector(10,10);
		zustaende 				= new Vector(10,10);
		transitionen 			= new Vector(20,20);
		kommentare	 			= new Vector(10,10);
		zustandsfolge	 		= new Vector(10,10);			
		taktfolge				= new Vector(10,10);
		aktivierteTransitionen 	= new Vector(5,3);			
		
		/* Parser zum Parsen der logischen Ausdr�cke */
		parser = new Parser();
	}
	
	/** L&ouml;scht den bisherigen Automaten */
	public void newMachine()
	{
		name = "No Name";
		inputs.removeAllElements();
		outputs.removeAllElements();
		zustaende.removeAllElements();
		transitionen.removeAllElements();
		kommentare.removeAllElements();
		zustandsfolge.removeAllElements();
		taktfolge.removeAllElements();
		aktivierteTransitionen.removeAllElements();
	}

	/**
	* f&uuml;gt einen neuen Input hinzu
	* @return Signal Instanz des neuen Inputs
	*/
	public Signal newInput()
	{
		Signal s = new Signal(Signal.IN);
		inputs.addElement(s);
		validateSignals();	
		return s;
	}

	/**
	* f&uuml;gt einen neuen Output hinzu
	* @return Signal Instanz des neuen Outputs
	*/
	public Signal newOutput()
	{
		Signal s = new Signal(Signal.OUT);
		outputs.addElement(s);
		validateSignals();	
		return s;
	}
	
	/**
	* f&uuml;gt einen neuen Zustand hinzu
	* @param name Name des neuen Zustandes (String)
	* @param x horizontale Position des Zustandes
	* @param y vertikale Position des Zustandes
	* @return Zustand Instanz des neuen Zustandes
	*/
	public Zustand newZustand(String name, int x, int y)
	{
		Zustand zustand = new Zustand(name,x,y);				// neuen Zustand generieren
		for (int i=0; i<outputs.size(); i++)
			zustand.outputHash.put(outputs.elementAt(i),"0");	// per Default nur mit Nullfkt. initialisieren
		zustaende.addElement(zustand);							// und speichern
		return zustand;
	}

	/**
	* f&uuml;gt eine neue Transition hinzu
	* @param z1 Ausgangszustand der Transition (Zustand)
	* @param z2 Zielzustand der Transition (Zustand)
	* @return Transition Instanz der neuen Transitionen
	*/
	public Transition newTransition(Zustand z1, Zustand z2)
	{
		if (findTransition((Zustand)z1,(Zustand)z2)<0)			// �berpr�fen, ob schon vorhanden
		{
			Transition transition = new Transition(z1,z2);
			transitionen.addElement(transition);
			return transition;
		}
		else return null;										// Transition bereits vorhanden
	}

	/**
	* f&uuml;gt einen neuen Kommentar hinzu
	* @param txt Kommentar-Text (String)
	* @param x horiz. Position des Kommentares (int)
	* @param y vert. Position des Kommentares (int)
	* @return Kommentar Instanz des neuen Kommentares
	*/
	public Kommentar newKommentar(String txt, int x, int y)
	{
		Kommentar kommentar = new Kommentar(txt, x, y);
		kommentare.addElement(kommentar);
		return kommentar;
	}

	/**
	* sucht eine bestimmte Transition
	* @param z1 Ausgangszustand der Transition (Zustand)
	* @param z2 Zielzustand der Transition (Zustand)
	* @return int Nummer der Transitionen im Vector oder -1, wenn nicht vorhanden
	*/
	public int findTransition(Zustand z1, Zustand z2) {
		int found=-1;
		Transition tr;
		for (int k=0; k<transitionen.size(); k++) {			// �berpr�fen, ob schon vorhanden
			tr = (Transition)transitionen.elementAt(k);		// nur kurz zwischenspeichern
			if ((tr.von==z1)&&(tr.nach==z2)) found=k;
		}
		return found;
	}

	/**
	* l&ouml;scht einen Input
	* @param in Input, der gel&ouml;scht werden soll
	*/
	public void deleteInput(Signal in)
	{
		inputs.removeElement(in);
		validateSignals();	
	}
	
	/**
	* l&ouml;scht einen Output
	* @param out Output, der gel&ouml;scht werden soll
	*/
	public void deleteOutput(Signal out)
	{
		outputs.removeElement(out);
		validateSignals();	
	}

	/**
	* l&ouml;scht einen Zustand und alle anliegenden Transitionen
	* @param z Zustand, der gel&ouml;scht werden soll
	*/
	public void deleteZustand(Zustand z)
	{
		for (int i=0; i<transitionen.size(); i++) 
		{
			Transition transition = (Transition)transitionen.elementAt(i); // nur kurz zwischenspeichern
			if ((transition.von==z)||(transition.nach==z)) transitionen.removeElementAt(i--);							// durch das L�schen das gleiche i nochmal !!! (wird erst nach dem Loeschen dekrementiert)
		}
		zustaende.removeElement(z);
	}

	/**
	* l&ouml;scht eine Transition
	* @param t Transition, die gel&ouml;scht werden soll
	*/
	public void deleteTransition(Transition t)
	{
		transitionen.removeElement(t);
	}

	/**
	* l&ouml;scht einen Kommentar
	* @param k Kommentar, der gel&ouml;scht werden soll
	*/
	public void deleteKommentar(Kommentar k)
	{
		kommentare.removeElement(k);
	}

	/** aktualisiert die Hashtables der Zust&auml;nde */
	private void validateSignals() {								// nach dem Einf�gen bzw. L�schen von Outputs die Hashtables aller Zust�nde anpassen
		if ((outputs.size()>0)&&(zustaende.size()>0)) {	// �berhaupt Outputs und Zust�nde vorhanden ???
			Signal sig = null;
			if (outputs.size()<((Zustand)zustaende.elementAt(0)).outputHash.size()) { // ein Output wurde entfernt! -> �berall l�schen
				Enumeration my_enum = ((Zustand)zustaende.elementAt(0)).outputHash.keys();
				while (my_enum.hasMoreElements()) {				// in Hashtables eingetragene Outputs durchgehen
					sig = (Signal)my_enum.nextElement();
					if (!outputs.contains(sig))				// gel�schten Output gefunden !
						for (int i=0; i<zustaende.size(); i++)
							((Zustand)zustaende.elementAt(i)).outputHash.remove(sig); // in allen Zust�nden diesen Output l�schen
				}
			}
			else if (outputs.size()>((Zustand)zustaende.elementAt(0)).outputHash.size()) { // ein neuer Output wurde erzeugt -> �berall eintragen
				for (int i=0; i<outputs.size(); i++) {		// alle Outputs durchgehen, um neuen zu finden
					sig = (Signal)outputs.elementAt(i);
					if (!((Zustand)zustaende.elementAt(0)).outputHash.containsKey(sig)) break; // wenn gefunden, dann abbrechen ...
				}
				for (int i=0; i<zustaende.size(); i++)
					((Zustand)zustaende.elementAt(i)).outputHash.put(sig,"0");			// und in allen Hastables eintragen
			}
		}
	}

	/**
	* gibt den Automaten-Namen zur&uuml;ck
	* @return Automaten-Name (String)
	*/
	public String getName()
	{
		return name;
	}

	/**
	* gibt die Anzahl der Eing&auml;nge zur&uuml;ck
	* @return int
	*/
	public int getInputSize()
	{
		return inputs.size();
	}

	/**
	* gibt die Anzahl der Ausg&auml;nge zur&uuml;ck
	* @return int
	*/
	public int getOutputSize()
	{
		return outputs.size();
	}

	/**
	* gibt die Anzahl der Zust&auml;nde zur&uuml;ck
	* @return int
	*/
	public int getZustaendeSize()
	{
		return zustaende.size();
	}
	
	/**
	* gibt die Gr&ouml;&szlig;e des Vektors "zustandsfolge" zur&uuml;ck (Anzahl der bisherigen Takte)
	* @return int
	*/
	public int getZustandsfolgeSize()
	{
		return zustandsfolge.size();
	}

	/** generiert einen Takt, berechnet den Folgezustand und setzt daraufhin die 
		Outputwerte */
	public void takt() 
	{					
		int takte = zustandsfolge.size();										// Anzahl der bisherigen Takte
		calculateZustand();																// Nachfolgezustand berechnen
		taktfolge.addElement("true");
		// Wurde ein Nachfolgezustand berechnet oder hat es einen Fehler gegeben
		if (takte < zustandsfolge.size()) 
		{										
			for (int i=0;i<inputs.size();i++) 
			{								
				// dann f�r alle Inputs
				Signal signal =(Signal)inputs.elementAt(i);
				signal.taktfolge.addElement(new Integer(signal.value));		// den aktuellen Wert in die Taktfolge �bernehmen
			}
			for (int i=0;i<outputs.size();i++) 
			{							
				// f�r alle Outputs den aktuellen Wert in die Taktfolge �bernehmen
				Signal signal =(Signal)outputs.elementAt(i);								// den jeweiligen Output in "signal" zwischenspeichern
				String s=(String)((Zustand)(zustandsfolge.lastElement())).outputHash.get(signal); // s enth�lt den logischen Ausdruck, der im aktuellen Zustand f�r den Output "signal" steht
				try 
				{
					if (parser.parse(s, inputs)) signal.value=Signal.HIGH;	// s parsen und wenn true, dann value auf HIGH setzen
					else signal.value=Signal.LOW;										// wenn false, dann value auf LOW setzen
					signal.taktfolge.addElement(new Integer(signal.value));	// den aktuellen Wert in die Taktfolge �bernehmen
				}
				catch (BadExpressionException e) 
				{									
					// falls Ausdruck im Zustand nicht korrekt
					status.set("Ausgang "+signal.name+" im Zustand "+((Zustand)zustandsfolge.lastElement()).name+" nicht richtig definiert!"); 
					signal.value=Signal.UNDEF;											// value auf UNDEFiniert setzen
					signal.taktfolge.addElement(new Integer(Signal.UNDEF));	// den aktuellen Wert in die Taktfolge �bernehmen
				}
			}
		}
	}	

		/** generiert beim Mealy Automaten einen Zwischen-Takt, wenn die Eingangswerte ver&auml;ndert werden, und setzt daraufhin die 
		Outputwerte */
	public void zwischentakt() 
	{					
		if (mealy_moore==MEALY) 
		{	
			zustandsfolge.addElement(zustandsfolge.lastElement());
			taktfolge.addElement("false");
			for (int i=0;i<inputs.size();i++) 
			{								
				// dann f�r alle Inputs
				Signal signal =(Signal)inputs.elementAt(i);
				signal.taktfolge.addElement(new Integer(signal.value));		// den aktuellen Wert in die Taktfolge �bernehmen
			}
			for (int i=0;i<outputs.size();i++) 
			{							
				// f�r alle Outputs den aktuellen Wert in die Taktfolge �bernehmen
				Signal signal =(Signal)outputs.elementAt(i);								// den jeweiligen Output in "signal" zwischenspeichern
				String s=(String)((Zustand)(zustandsfolge.lastElement())).outputHash.get(signal); // s enth�lt den logischen Ausdruck, der im aktuellen Zustand f�r den Output "signal" steht
				try 
				{
					if (parser.parse(s, inputs)) signal.value=Signal.HIGH;	// s parsen und wenn true, dann value auf HIGH setzen
					else signal.value=Signal.LOW;										// wenn false, dann value auf LOW setzen
					signal.taktfolge.addElement(new Integer(signal.value));	// den aktuellen Wert in die Taktfolge �bernehmen
				}
				catch (BadExpressionException e) 
				{									
					// falls Ausdruck im Zustand nicht korrekt
					status.set("Output "+signal.name+" in state "+((Zustand)zustandsfolge.lastElement()).name+" not properly defined!"); 
					signal.value=Signal.UNDEF;											// value auf UNDEFiniert setzen
					signal.taktfolge.addElement(new Integer(Signal.UNDEF));	// den aktuellen Wert in die Taktfolge �bernehmen
				}
			}
		}
	}	
	
	/** f&uuml;hrt einen Reset aus setzt Eing&auml;nge auf ihre Initialwerte 
		zur&uuml;ck und den Startzustand als aktuellen Zustand, 
		bisherige Zustandsfolgen werden gel&ouml;scht */
	public void reset() 
	{
		for (int i=0; i<inputs.size(); i++) 
		{
			Signal input = (Signal)inputs.elementAt(i);
			input.value = input.initial;													// die Werte aller Inputs auf initial setzen
			input.taktfolge.removeAllElements();										// die Taktfolge l�schen
			input.taktfolge.addElement(new Integer(input.initial));			 	// und mit initial f�llen
		}
		zustandsfolge.removeAllElements();											// die Zustandsfolge l�schen
		for (int i=0; i<zustaende.size(); i++) 
		{										
			// und mit dem Startzustand f�llen
			if (((Zustand)zustaende.elementAt(i)).isStart) zustandsfolge.addElement(zustaende.elementAt(i));
		}
		for (int i=0; i<outputs.size(); i++) 
		{
			Signal output = (Signal)outputs.elementAt(i);
			for (int j=0; j<zustaende.size(); j++)
			{									
				// je nach Startzustand die Initialwerte der Outputs setzen
				Zustand zustand = (Zustand)zustaende.elementAt(j);
				if (zustand.isStart)
				{
					String s=(String)zustand.outputHash.get(output);			 	// s enth�lt den logischen Ausdruck, der im aktuellen Zustand f�r den Output "signal" steht
					try 
					{
						if (parser.parse(s, inputs)) output.initial=Signal.HIGH;	// s parsen und wenn true, dann value auf HIGH setzen
						else output.initial=Signal.LOW;									// wenn false, dann value auf LOW setzen
					}
					catch (BadExpressionException e) 
					{									
						// falls Ausdruck im Zustand nicht korrekt
						status.set("Output "+output.name+" in state "+((Zustand)zustandsfolge.lastElement()).name+" not properly defined!"); 
						output.initial = Signal.UNDEF;											// value auf UNDEFiniert setzen
					}
				}
			}
			output.value = output.initial;												// die Werte aller Outputs auf initialsetzen
			output.taktfolge.removeAllElements();										// die Taktfolge l�schen
			output.taktfolge.addElement(new Integer(output.initial));			// und mit initial f�llen
		}
		taktfolge.removeAllElements();
		taktfolge.addElement("true");
	}
	
	/** setzt den Automaten-Typ auf Mealy*/
	public void setMealy()
	{							
		mealy_moore=MEALY;
	}

	/** setzt den Automaten-Typ auf Moore*/
	public void setMoore()
	{	
		mealy_moore=MOORE;
		for (int i=0; i<zustaende.size();i++) 
		{
			/* alle Outputfunktionen auf 0/1 �berpr�fen */
			Zustand z = (Zustand)zustaende.elementAt(i);
			for (int j=0; j<outputs.size();j++) 
			{
				Signal out=(Signal)outputs.elementAt(j);
				/* Falls nicht 1 dann in jedem Fall auf 0 setzen */
				if(!((String)(z.outputHash.get(out))).equals("1")) 
					z.outputHash.put(out,"0");
			}
		}
	}	

	/** 
	* ermittelt den Automaten-Typ
	* @return boolean true=Mealy / false=Moore
	*/
	public boolean getMachineType()
	{				
		if (mealy_moore==MEALY) return true;
		else return false;
	}

	/**
	* definiert einen Zustand als Startzustand
	* @param z Zustand, der als Startzustand definiert werden soll (Zustand)
	*/
	public void setStart(Zustand z)
	{
		for (int i=0; i<zustaende.size(); i++) 
			((Zustand)zustaende.elementAt(i)).isStart = false;	// ALLE auf false setzen
		z.isStart = true;				// den selektierten Zustand zum Startzustand machen
	}

	/**
	* ermittelt, ob bereits ein Startzustand definiert wurde
	* @return boolean false, wenn noch kein Startzustand definiert wurde
	*/
	public boolean existsStartzustand()
	{
		for (int i=0;i<zustaende.size();i++)
		{
			// alle Zust�nde durchgehen und nach dem Startzustand suchen
			Zustand z = (Zustand)zustaende.elementAt(i);
			if (z.isStart) return true;
		}
		return false;
	}

	/**
	* liefert den derzeit definierten Startzustand
	* @return Zustand Startzustand
	*/
	public Zustand getStartzustand()
	{
		for (int i=0;i<zustaende.size();i++)
		{
			// alle Zust�nde durchgehen und nach dem Startzustand suchen
			Zustand z = (Zustand)zustaende.elementAt(i);
			if (z.isStart) return z;
		}
		return null;
	}

	/** berechnet den Folgezustand (wird zum Vektor zustandsfolge addiert) */
	public void calculateZustand()
	{
		// den Nachfolgezustand berechnen
		int aktiviert = 0;								// ist ein Zustand aktiviert?
		int stern = 0;									// ist in einem Ausdruck der Stern angegeben? (steht f�r alle noch nicht angegebenen Belegungen)	
		aktivierteTransitionen.removeAllElements();		// Vektor aktivierteTransitionen leeren
		Zustand nextZustand = null;						// der n�chste Zustand
		Zustand defaultZustand = null;					// der default Zustand, wird durch * in der �bergangsbedingung angegeben
		Transition t = null;							// hier werden die Transitionen zwischengespeichert
		try 
		{												// parse wirft BadExpressionException
			for (int i=0; i<transitionen.size(); i++) 
			{
				// alle Transitionen durchgehen
				t = (Transition)transitionen.elementAt(i);	// Transition zwischenspeichern
				if (t.von==zustandsfolge.lastElement()) 
				{
					// geht die Transition von aktuellen Zustand aus?
					if (t.function.equals("*")) 
					{
						stern++;							// falls Stern, dann merken
						defaultZustand = t.nach;			// Zustand auch merken
					}
					else if (parser.parse(t.function,inputs)) 
					{	
						// logischen Ausdruck der Transition parsen 
						aktiviert++;						// falls Ausdruck true, dann ist Transition aktiviert
						nextZustand = t.nach;				// und der "nach"-Zustand der Transition wird Nachfolgezustand
					}
				}
			}
			if (aktiviert==1) 
			{											
				// genau ein Nachfolge-Zustand
				zustandsfolge.addElement(nextZustand);		// Nachfolgezustand einf�gen
			}
			else if (stern==1) 
			{							
				// genau ein Default-Zustand
				zustandsfolge.addElement(defaultZustand);	// Nachfolgezustand einf�gen			
			}
			else if (stern>1) 
			{					
				// Fehler: mehr als eine �bergangsbedingung enth�lt Stern
				status.set("Only one transition may contain a star *");
			}
			else 
			{															// Fehler: �bergang nicht eindeutig definiert
				status.set("Transition not properly defined");
			}
		}
		catch (BadExpressionException e) 
		{							// Fehler in einer �bergangsfunktion
			status.set("Bad transition-function!");
		}
	}


	/** berechnet die bei der aktuellen Belegung 
		aktivierten Transitionen (enthalten im Vektor aktivierteTransitionen) */
	public void calculateTransition()
	{
		// die aktivierten Transitionen berechnen
		aktivierteTransitionen.removeAllElements();		// Vektor aktivierteTransitionen leeren
		Transition t=null;								// hier werden die Transitionen zwischengespeichert
		int stern = 0;
		Transition def=null;							// Default Transition mit *
		try 
		{												// parse wirft BadExpressionException
			for (int i=0; i<transitionen.size(); i++) 
			{			
				// alle Transitionen durchgehen
				t = (Transition)transitionen.elementAt(i);	// Transition zwischenspeichern
				if (t.von==zustandsfolge.lastElement()) 
				{		
					// geht die Transition von aktuellen Zustand aus?
					if (t.function.equals("*")) 
					{					  
						stern++;						// falls Stern, dann merken und nicht parsen
						def = t;						// und als Default merken
					}
					else if (parser.parse(t.function,inputs)) 
					{	
						// logischen Ausdruck der Transition parsen 
						aktivierteTransitionen.addElement(t);	// falls aktiviert, in den Vektor der aktivierten Transitionen hinzuf�gen						
					}
				}
			}
			if (aktivierteTransitionen.size()==0&&stern==1)
			{
				// falls keine Transition aktiviert und eine Transition * als �bergangsfunktion hat
				aktivierteTransitionen.addElement(def);		// diese in den Vektor der aktivierten Transitionen hinzuf�gen									
			}
		}
		catch (BadExpressionException e) 
		{							
			// Fehler in einer �bergangsfunktion
			status.set("Bad transition-function!");
		}
	}

/**
* Liefert die Automatenbeschreibung als String zur�ck
* @return String Automatenbeschreibung
*/
	public String saveFSM()
	{
		String data="";
		Signal sig;
		Zustand zu;
		Transition tran;
		Kommentar kom;
		data +=("[JavaFSM V1.0]\n");
		if (!getMachineType()) data +=("[MOORE]\n");
		else data +=("[MEALY]\n");
		data += "[Name]\n"+name+"\n";						// Name des Automaten
		data +=("[Inputs "+inputs.size()+"]\n");			// Anzahl der Inputs
			for (int i=0; i<inputs.size(); i++) {
				sig=(Signal)inputs.elementAt(i);
				data+=sig.name+","+sig.initial+"\n";		// Format f�r Inputs: Name,Initialwert
			}
		data +=("[Outputs "+outputs.size()+"]\n");			// Anzahl der Outputs
			for (int i=0;i<outputs.size();i++){
				sig=(Signal)outputs.elementAt(i);
				data+=sig.name+"\n";						// Format f�r Outputs: Name	(Funktionen sind in Zust�nden!)
			}
		data +=("[Zustaende "+zustaende.size()+"]\n");		// Anzahl der Zust�nde
			for (int i=0;i<zustaende.size();i++){
				zu=(Zustand)zustaende.elementAt(i);
				data+=zu.name+","+zu.x+","+zu.y+","+zu.isStart;// Format der Zust�nde: Name,X-Pos,Y-Pos,Startzustand(true/false),f�r jeden Output eine Funktion
				for (int j=0;j<outputs.size();j++) {
					data+=(","+(String)zu.outputHash.get(outputs.elementAt(j)));
				}
				data+="\n";
			}
		data +=("[Transitionen "+transitionen.size()+"]\n");// Anzahl der Transitionen
			for (int i=0;i<transitionen.size();i++){
				tran=(Transition)transitionen.elementAt(i);
				data+=tran.von.name+","+tran.nach.name+","+tran.function+"\n";// Format f�r Transitionen: Quell-Zustandsname,Ziel-Zustandsname,�bergangsfunktion
			}
		data +=("[Kommentare "+kommentare.size()+"]\n");	// Anzahl der Kommentare
			for (int i=0;i<kommentare.size();i++){
				kom=(Kommentar)kommentare.elementAt(i);
				data+=kom.x+","+kom.y+","+CodeString(kom.getText())+"\n";// Format der Kommentare: X-Pos,Y-Pos,Kommentartext
			}
//		data +=("[Simulation "+Zustandsfolge.size()+"]");
		data +=("[ENDE]\n");								// das Ende zur Kontrolle kennzeichnen
		return data;

	}




/**
* L&auml;dt einen Automaten aus einem Stream
* @param in Stream, aus dem die Automatenbeschreibung gelesen wird (DataInputStream)
* @return String Fehlermeldung oder null, wenn keine Fehler aufgetreten sind
*/
	public String loadFSM(DataInputStream in)
	{
		try
		{
			String str;
			
			if (((in.readLine()).trim()).equals("[JavaFSM V1.0]") ||	// Bei Dateien, die als Applet erstellt wurden, aber lokal
				((in.readLine()).trim()).equals("[JavaFSM V1.0]"))		// geladen werden, steht in der ersten Zeile noch das Passwort!
			{
				int k;											// Variable zum Zwischenspeichern
				newMachine();									// erst jetzt alle Vektoren l�schen
				str=in.readLine().trim();
				if (str.equals("[MOORE]"))	
				{												// Auswahl Mealy / Moore 
					setMoore();
				}
				else if (str.equals("[MEALY]")) 
				{
					setMealy();
				}
				else return "Error in file-format";			// kein [MEALY] oder [MOORE]
	
				str=in.readLine().trim();
				if (str.equals("[Name]")) name=in.readLine().trim();// Namenszeile lesen
				else return "Error in file-format";

				str=in.readLine().trim();
				if (str.startsWith("[Inputs"))
				{
					k = getNumber(str);							// Anzahl der Inputs in "[Inputs x]"
					for (int i=0; i<k; i++)
					{
						str=in.readLine().trim();
						if (!str.startsWith("[") && (countChar(str,',')==1))	// nicht mit '[' beginnen und genau 1 Komma
							inputs.addElement(stringToInput(str));				// jede Zeile ist ein Input
						else return "Error in file-format";
					}
				}
				else return "Error in file-format";

				str=in.readLine().trim();
				if (str.startsWith("[Outputs"))
				{
					k = getNumber(str);											// Anzahl der Outputs in "[Outputs x]"
					for (int i=0; i<k; i++)
					{
						str=in.readLine().trim();
						if (!str.startsWith("[") && (countChar(str,',')==0))	// nicht mit '[' beginnen und kein Komma
							outputs.addElement(stringToOutput(str));			// jede Zeile ist ein Output
						else return "Error in file-format";
					}
				}
				else return "Error in file-format";
	
				str=in.readLine().trim();
				if (str.startsWith("[Zustaende"))
				{
					k = getNumber(str);											// Anzahl der Zust�nde in "[Zustaende x]"
					for (int i=0; i<k; i++)
					{
						str=in.readLine().trim();
						if (!str.startsWith("[") && (countChar(str,',')==3+outputs.size()))	// nicht mit '[' beginnen und genau 3+Outputs Kommata
							zustaende.addElement(stringToZustand(str));			// jede Zeile ist ein Zustand
						else return "Error in file-format";
					}
				}
				else return "Error in file-format";
	
				str=in.readLine().trim();
				if (str.startsWith("[Transitionen"))
				{
					k = getNumber(str);											// Anzahl der Transitionen in "[Transitionen x]"
					for (int i=0; i<k; i++)
					{
						str=in.readLine().trim();
						if (!str.startsWith("[") && (countChar(str,',')==2))	// nicht mit '[' beginnen und genau 2 Kommata
							transitionen.addElement(stringToTransition(str));	// jede Zeile ist eine Transition
						else return "Error in file-format";
					}
				}
				else return "Error in file-format";
	
				str=in.readLine().trim();
				if (str.startsWith("[Kommentare"))
				{
					k = getNumber(str);											// Anzahl der Kommentare in "[Kommentare x]"
					for (int i=0; i<k; i++)
					{
						str=in.readLine().trim();
						if (!str.startsWith("[") && (countChar(str,',')==2))	// nicht mit '[' beginnen und genau 2 Kommata
							kommentare.addElement(stringToKommentar(str));		// jede Zeile ist ein Kommentar
						else return "Error in file-format";
					}
				}
				else return "Error in file-format";

				str=in.readLine().trim();
				if (!((str.startsWith("[Simulation"))||(str.equals("[ENDE]"))))	// Entweder Ende oder noch Simulationsdaten
					return "Error in file-format";										// irgendwas lief schief
			}	
			else return "Wrong version";													// nicht "[JavaFSM V1.0]"
		}
		catch (Exception e)
		{
			return "Readerror - IO-Exception";
		}
		return null;				//alles OK
	}
	
	// von LoadFSM benutzte Methoden
	private int countChar(String s, char c)				// z�hlt den char c in einem String
	{
		int count = 0;
		for (int j=0; j<s.length(); j++)				// gesamten String durchgehen
			if (s.charAt(j)==c) count++;				// einzelne chars vergleichen
		return count;
	}
	private int getNumber(String str)					// holt Anzahl x aus "[irgendwas x]"
	{
		try 
		{
			return (Integer.parseInt(str.substring(str.indexOf(' ')+1,str.indexOf(']'))));
		}
		catch(Exception e)
		{
			return 0;
		}
	}
	private Signal stringToInput(String str)			// Input aus Zeile generieren
	{
		int p = str.indexOf(',');
		return (new Signal(str.substring(0,p), Signal.IN, Integer.parseInt(str.substring(p+1))));
	}
	private Signal stringToOutput(String str)			// Output aus Zeile generieren
	{
		return (new Signal(str, Signal.OUT, 0));
	}
	private Zustand stringToZustand(String str)			// Zustand aus Zeile generieren
	{
		int p1 = str.indexOf(',');
		int p2 = str.indexOf(',',p1+1);
		int p3 = str.indexOf(',',p2+1);
		Zustand zu = new Zustand(str.substring(0,p1),Integer.parseInt(str.substring(p1+1,p2)),Integer.parseInt(str.substring(p2+1,p3)));
		if (str.substring(p3+1,p3+2).equals("t")) zu.isStart = true;
		p2=str.indexOf(',',p3+1);
		for (int i=0;i<outputs.size();i++) 
		{
			p1 = p2;
			p2 = str.indexOf(',',p2+1);
			if (p2>=0) zu.outputHash.put(outputs.elementAt(i),str.substring(p1+1,p2));
			else zu.outputHash.put(outputs.elementAt(i),str.substring(p1+1));
		}
		return zu;
	}
	private Transition stringToTransition(String str) 	// Transition aus Zeile generieren
	{
		int p1 = str.indexOf(',');
		int p2 = str.indexOf(',',p1+1);
		String von = str.substring(0,p1);
		String nach = str.substring(p1+1,p2);
		Zustand zu;
		Zustand vonZ  = null;
		Zustand nachZ = null;
		Transition tr = null;
		for (int i=0;i<zustaende.size();i++) 
		{
			zu = (Zustand)zustaende.elementAt(i);
			if (zu.name.equals(von)) vonZ=zu;
			if (zu.name.equals(nach)) nachZ=zu;
		}
		if ((vonZ!=null)&&(nachZ!=null)) tr = new Transition(vonZ,nachZ,str.substring(p2+1));
		return tr;
	}

	private Kommentar stringToKommentar(String str) 	// Kommentar aus Zeile generieren
	{
		int p1 = str.indexOf(',');
		int p2 = str.indexOf(',',p1+1);
		return new Kommentar(DecodeString(str.substring(p2+1)),Integer.parseInt(str.substring(0,p1)),Integer.parseInt(str.substring(p1+1,p2)));
	}

	private String CodeString(String str) {				// Kodiert einen String (�hnlich URL-Code)
		String Result = new String ("");
		char c;
		for (int x=0; x<str.length(); x++) {
			c = str.charAt(x);
			if ((c<' ')||(c==',')||(c=='%'))	{
				Result += "%" + (char)((c/10)+'0') + (char)((c%10)+'0');
				}
			else	{
				Result += "" + c;
				}
			}
		return Result;
	}

	private String DecodeString(String str) {		// Dekodiert einen String (�hnlich URL-Code)
		String Result = new String ("");
		char c,c1,c2;
		for (int x=0; x<str.length(); x++) {
			c = str.charAt(x);
			if (c=='%')	{
				c1 = (char) (str.charAt(++x) - '0');
				c2 = (char) (str.charAt(++x) - '0');
				Result += "" + (char) ((c1*10) + c2);
				}
			else	{
				Result += "" + c;
				}
			}
		return Result;
	}

}
	
