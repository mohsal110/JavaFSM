package com.company;
/**
* <IMG SRC="../doku/images/logo_klein.gif" ALT="JavaFSM"><BR>
* Transition in machine */
class Transition extends Object 
{
	/** Ausgangszustand */
	public Zustand von;
	/** Folgezustand */
	public Zustand nach;
	/** �bergangsfunktion */
	public String function;

	/** Konstruktor f�r eine Transition ohne �bergangsfunktion */
	public Transition(Zustand Von, Zustand Nach) 
	{
		super();
		von = Von;
		nach = Nach;
		function="0";
	}

	/** Konstruktor f�r eine Transition mit �bergangsfunktion 
	* @param Von		1. Transition		(Zustand)
	* @param Nach		2. Transition		(Zustand)
	* @param Function	�bergangsfunktion	(String)
	*/
	public Transition(Zustand Von, Zustand Nach, String Function) 
	{
		super();
		von = Von;
		nach = Nach;
		function = Function;
	}
}