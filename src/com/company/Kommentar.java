package com.company;

/**
* <IMG SRC="../doku/images/logo_klein.gif" ALT="JavaFSM"><BR>
* Kommentar-Feld im Automaten / comment
*/

class Kommentar
{
	/** Koordinaten des Kommentars im Editor */
	public int x,y;

	private String text;
	private String[] lines;


	/** Konstruktor f�r einen Kommentar
	* @param	xPos	x-Koordinate im Editor (int)
	* @param	yPos	y-Koordinate im Editor (int)
	* @param	txt		Kommentar-Text (String)
	*/
	public Kommentar(String txt, int xPos, int yPos)
	{
		super();
		x		= xPos;
		y		= yPos;
		setText(txt);
	}

	/** Setzt des Kommentar-Text neu
	* @param	txt		Kommentar-Text (String)
	*/
	public void setText(String txt)
	{
		int i,h,w;
		text = txt;
		h = 1;
		for (i=0; i<text.length(); i++)
			if (text.charAt(i)=='\n') h++;			// Zeilen z�hlen
		lines = new String[h];
		h = 0;
		w = 0;
		for (i=0; i<text.length(); i++)
		{
			if (text.charAt(i)=='\n')
			{
				lines[h]=text.substring(w,i);		// Zeile merken
				w=i+1;								// Position merken
				h++;
			}
			lines[h]=text.substring(w);				// letzte Zeile
		}
	}

	/** Liefert die einzelnen Zeilen des Kommentares 
	* @return	String[]	Kommentar-Zeilen
	*/
	public String[] getLines()
	{
		return (this.lines);
	}

	/** Liefert den Kommentar-Text
	* @param	fm			Schrift-Eigenschaften (FontMetrics)
	* @return	String		Kommentar-Text
	*/
	public String getText()
	{
		return text;
	}

	/** Liefert die H�he des Kommentars in Pixeln
	* @param	fm			Schrift-Eigenschaften (FontMetrics)
	* @return	int			Kommentarh�he
	*/
	public int getHeight(java.awt.FontMetrics fm)
	{
		return lines.length*fm.getHeight();
	}

	/** Liefert die Breite des Kommentars in Pixeln
	* @param	fm			Schrift-Eigenschaften (FontMetrics)
	* @return	int			Kommentarbreite
	*/
	public int getWidth(java.awt.FontMetrics fm)
	{
		if (lines[0]!=null)
		{
			int tmp = 0;
			int max = 0;
			for (int i=0; i<lines.length; i++)
				if ((tmp=fm.stringWidth(this.lines[i]))>max) max=tmp;
			return max;
		}
		else return 0;
  }
}

