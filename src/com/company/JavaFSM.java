package com.company;
import java.awt.*;
import java.applet.*;
import java.net.URL;
import java.util.Vector;


/**
* <IMG SRC="../doku/images/logo_klein.gif" ALT="JavaFSM"><BR>
* design and simulatin of finitstatemachines    Schaltwerksdesign<BR>
* as prameters exemples are handed over in the HTML-File,  
* that can be loaded from the server with cgi
*/
public class JavaFSM extends Applet {
	/** main window */
	private MainFrame frame;
	/** URL vom Server des Applets */
	public URL baseUrl;
	/** Vector with the examples (Parameter im HTML-File) */
	private Vector beispiele;

	/**
	* startet JavaFSM als Applet (in eigenem Fenster)
	* @see main()
	*/
	public void init() 
	{
		/* Home-URL remember for save\load */
		baseUrl				= this.getDocumentBase();

		/* read examples and save */
		beispiele = new Vector();
		String s1="", s2="";
		int i=1;
		for (;;)
		{
			s1 = getParameter("file"+i);
			s2 = getParameter("passwd"+i);
			if (s1!=null && s2!=null)
			{
				beispiele.addElement(s1);
				beispiele.addElement(s2);
				i++;
			}
			else break;
		}
		/* Instanz von MainFrame (Hauptfenster) */
		frame = new MainFrame(this, "JavaFSM", baseUrl, beispiele);	
		frame.resize(600,470);
		frame.show();
		frame.resize(600,470);

	}

	/**
	* starts Java as Applikation
	* @see init()
	*/
	public static void main(String args[])
	{
		JavaFSM myFSM = new JavaFSM();
		/* Home-URL remember for load\save */
		myFSM.baseUrl	= null;
		/* examples */
		myFSM.beispiele = new Vector();
		/* Instanz von MainFrame (Hauptfenster) */
		myFSM.frame = new MainFrame(myFSM, "JavaFSM", myFSM.baseUrl, myFSM.beispiele);	
		myFSM.frame.resize(600,470);
		myFSM.frame.show();
		myFSM.frame.resize(600,470);

	}
}