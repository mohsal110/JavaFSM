package com.company;
import java.util.*;
import java.io.*;

/**
* <IMG SRC="../doku/images/logo_klein.gif" ALT="JavaFSM"><BR>
* Parser to calculate logical statements
*/
class Parser {
	private int curr_token;					// Aktuell eingelesenens Zeichen
	private static final int END=0, NAME=1, NUMBER=2, AND=3, OR=4, NOT=5, LP=6, RP=7; // m�gliche Operatoren
	private int number_value;				// Wert von NUMBER (0 oder 1)
	private String name_value, expression;	// Name des Inputs, logischer Ausdruck
	private PushbackInputStream	cin;		// Puffer zum Speichern des Ausdrucks
	private Vector Inputs;					// Inputvektor zum �berpr�fen der Namen

	/**
	* parst den &uuml;bergebenen Ausdruck
	* @param arg Ausdruck, der ausgewertet werden soll (String)
	* @param in vorhandene Inputs zum Nachschlagen der Werte (Vector)
	* @return boolean Wahrheitswert des logischen Ausdruckes
	* @exception BadExpressionException wirft diese Exception, wenn der Ausdruck fehlerhaft ist
	*/	
	public boolean parse(String arg, Vector in) throws BadExpressionException
	{
		if(arg!=null&in!=null)
		{
			Inputs=in;								// Inputvektor speichern
			expression=arg.trim();				// logischen Ausdruck speichern (Sonderzeichen abtrennen)
			cin=new PushbackInputStream(new StringBufferInputStream(expression+'\n'));	// expression in Puffer speichern
			get_token();							// n�chstes Zeichen einlesen
			return expr();							// mit expr() Ausdruck auswerten und zur�ckgeben
		}
		else throw new BadExpressionException("bad parameter");	// Fehler
	}

	// liest das n�chste Zeichen ein (bei Namen auch mehrere Zeichen)
	private int get_token()throws BadExpressionException 
	{
		char ch;
		try
		{
			do
			{
				if ((ch=(char)cin.read())=='\n')return (curr_token = END);  // falls Return (\n) dann Ende
			} while(ch==' ');													// entfernt Leerzeichen
			  
			switch(ch)
			{														// Zeichen auswerten
				case'|': return (curr_token = OR);
				case'&': return (curr_token = AND);
				case'!': return (curr_token = NOT);
				case'(': return (curr_token = LP);
				case')': return (curr_token = RP);
				case'0': number_value=0; return (curr_token = NUMBER);
				case'1': number_value=1; return (curr_token = NUMBER);
				default: 																// Name
				if ((ch>='A' && ch<='Z')||(ch>='a' && ch<='z')||(ch=='_')||(ch=='_'))
				{
					//f�r Name g�ltige Zeichen (als erster Buchstabe keine Zahl)
					name_value=""+ch;												// in name_value das Zeichen speichern
					while((ch=(char)cin.read())!='\n' &&((ch>='A' && ch<='Z')||(ch>='a' && ch<='z')||(ch>='0'&&ch<='9')||(ch=='_')||(ch=='_')))
					{ 
						// f�r Namen g�ltige Zeichen								
						name_value+=ch;											// Zeichen an name_value anh�ngen
					}
					cin.unread(ch);											// letztes Zeichen (geh�rt nicht mehr zu name) zur�ckspeichern
					return (curr_token=NAME);									// name zur�ckgeben
				}
				else 
				{
					throw new BadExpressionException(expression);	// Fehler, kein g�ltiges Zeichen
				}
			}
		}
		catch(IOException e)
		{
			System.out.println("Programmfehler: Parser Exception");
			return (curr_token=END);
		}
	}

// es wird im folgenden immer get_token aufgerufen, um bereits das n�chste Zeichen einzulesen (look_ahead) 

	private boolean expr()throws BadExpressionException {			// wertet OR aus
		boolean left=term();													// ruft f�r das erste Zeichen term und dann prim auf
		for(;;){
			if(curr_token==OR){												// falls das zweite Zeichen ein OR dann
				get_token();													// drittes Zeichen einlesen und
				left|=term();													// auswerten
			}
			else break;						// sonst beenden
		}			
		return left;						// left zur�ckgeben
	}
	
	private boolean term()throws BadExpressionException {			// wertet AND aus
		boolean left=prim();				// ruft f�r das erste Zeichen prim auf
		for(;;){
			if(curr_token==AND){			// falls das zweite Zeichen ein AND dann
				get_token();				// drittes Zeichen einlesen und
				left&=prim();				// auswerten
			}
			else break;						// sonst beenden
		}
		return left;						// left zur�ckgeben
	}

	private boolean prim() throws BadExpressionException{	// wertet Zeichen aus
		switch (curr_token){
			case NUMBER: get_token(); if(number_value==1) return true; else return false;  	// falls NUMBER dann number_value zur�ckgeben
			case NAME: get_token(); return look();															// falls NAME dann Wert nachsehen und zur�ckgeben
			case NOT: get_token(); return !prim();															// falls NOT dann inverses zur�ckgeben
			case LP: 																								// falls Klammer dann 
				get_token(); 																						// erstes Zeichen in der Klammer einlesen und
				boolean e = expr(); 																				// expr() aufrufen um Klammerinhalt auszuwerten
				if (curr_token!=RP)throw new BadExpressionException(expression);				// falls nach Aufruf von expr() nicht rechte Klammer, dann Fehler
				get_token(); 																						// erstes Zeichen nach der Klammer einlesen (look ahead)
				return e;																							// Klammerwert zur�ckgeben
			default: throw new BadExpressionException(expression);							// Fehler: kein sinnvolles Zeichen
		}
	}
	
	private boolean look() throws BadExpressionException{												// holt der Wert zu einem Namen
		for (int i = 0;i<Inputs.size();i++){																// falls name richtig
			if (((Signal)Inputs.elementAt(i)).name.equals(name_value)){								// Wert nachsehen
				if (((Signal)Inputs.elementAt(i)).value==Signal.HIGH) return true;
				else return false;
			}
		} 
		throw new BadExpressionException(expression, name_value);									// falls name nicht vorhanden
	}
}