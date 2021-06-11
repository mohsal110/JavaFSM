package com.company;
import java.util.Vector;
import java.awt.Color;

/**
* <IMG SRC="../doku/images/logo_klein.gif" ALT="JavaFSM"><BR>
* represents Inputs und Outputs
*/
class Signal extends Object	
{
	/** Name des Signals */
	public  String name;
	/** gibt an, ob es sich um einen Input oder Output handelt */
	public int in_out;
	/** Wert f�r in_out */
	public static final int IN=0;
	/** Wert f�r in_out */
	public static final int OUT=1;
	/** Initialwert nach Reset */
	public  int initial; 
	/** aktueller Wert */
	public  int value; 
	/** Wert f&uuml;r "initial" und "value" */
	public  static final int HIGH=1;
	/** Wert f&uuml;r "initial" und "value" */
	public  static final int LOW=0;
	/** Wert f&uuml;r "initial" und "value" */
	public  static final int UNDEF=2;
	/** Position des Signals im Schaltbild */
	public  int ixpos, iypos, ypos;
	/** Taktfolge von "value" bei der Simulation */
	public  Vector taktfolge;
	/** Farbe im Impulsdiagramm */
	public  Color col;

	/** Konstruktor
	* @param Name			Name des Signals (String)	
	* @param IN_OUT			gibt an, ob es sich um einen Input oder Output handelt (int) Es wird entweder IN f�r Input oder OUT f�r Output �bergeben.
	* @param Initial		Initialwert (int)
	*/
	public Signal (String Name, int IN_OUT, int Initial)
	{
		super();
		name		= Name;
		in_out		= IN_OUT;
		initial		= Initial;
		value		= Initial;
		taktfolge	= new Vector(10,10);
	}

	/** Konstruktor
	* @param IN_OUT		gibt an, ob es sich um einen Input oder Output handelt (int) Es wird entweder IN f�r Input oder OUT f�r Output �bergeben.
	*/
	public Signal (int IN_OUT)
	{
		super();
		in_out		= IN_OUT;
		taktfolge	= new Vector(10,10);
		name		= "";
		initial		= HIGH;
		value		= HIGH;
	}
}


