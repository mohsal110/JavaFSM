package com.company;
import java.util.Hashtable;

/**
* <IMG SRC="../doku/images/logo_klein.gif" ALT="JavaFSM"><BR>
* State of machine 
*/
class Zustand extends Object 
{
	/** Name des Zustandes */
	public String name;
	/** Koordinaten des Zustandes im Editor */
	public int x,y;
	/** gibt an, ob der Zustand Startzustand ist */
	public boolean isStart;
	/** enth�lt die Funktionen f�r die Outputs in diesem Zustand */
	public Hashtable outputHash;

	/** Konstruktor f�r einen Zustand 
	* @param	Name	Name des Zustands (String)
	* @param	xPos	x-Koordinate im Editor (int)
	* @param	yPos	y-Koordinate im Editor (int)
	*/
	public Zustand(String Name, int xPos, int yPos)
	{
		super();
		name = Name;
		x = xPos;
		y = yPos;
		isStart=false;
		outputHash = new Hashtable();
	}

	/** Konstruktor f�r einen Zustand ohne Namen 
	* @param	xPos	x-Koordinate im Editor (int)
	* @param	yPos	y-Koordinate im Editor (int)
	*
	*/
	public Zustand(int xPos, int yPos) 
	{
		super();
		name="";
		x = xPos;
		y = yPos;
		isStart=false;
		outputHash = new Hashtable();
	}

	/** macht den Zustand zum Startzustand */
	public void setStart()
	{
		isStart=true;
	}
	/** macht den Zustand zum Nicht-Startzustand */
	public void setNotStart()
	{
		isStart = false;
	}
	/** gibt bei Aufruf den Namen des Zustands zur�ck 
	* @return	String	Name des Zustands 
	*/
	public String toString()
	{
		return name;
	}

}

