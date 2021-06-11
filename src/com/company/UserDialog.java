package com.company;
import java.awt.*;
import java.util.*;
import java.net.*;
import java.io.*;

/**
* <IMG SRC="../doku/images/logo_klein.gif" ALT="JavaFSM"><BR>
* 
*/
class UserDialog extends Dialog {
	private Button OK,Cancel;
	private Label fileL, passwdL;
	public  TextField file, passwd;
	public  FSM fsm;
	public  MainFrame Parent;
	public  Statuszeile statusLS;
	private Vector Zustaende, Transitionen, inputs, outputs, Zustandsfolge;

   	/** 
	* Konstruktor
	* @param p aufrufendes Fenster (MainFrame)
	* @param title Fenstertitel (String)  
	* @param modal siehe java.awt.Dialog (booelan)
	*/
	public UserDialog(MainFrame p,String title, boolean modal, FSM Fsm) {
		super(p,title,modal);
		Parent = p;
		fsm=Fsm;

		Zustaende = fsm.zustaende;
		Transitionen = fsm.transitionen;
		inputs = fsm.inputs;
		outputs = fsm.outputs;
		Zustandsfolge = fsm.zustandsfolge;

		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		this.setLayout(gbl);
		Insets ins = new Insets(0,0,0,0);

		fileL = new Label("Name of File:");
		passwdL = new Label("Password");
		fileL.setAlignment(Label.CENTER);
		passwdL.setAlignment(Label.CENTER);
		file   = new TextField(10);
		passwd = new TextField(10);
		passwd.setEchoCharacter('*');
		OK = new Button(title);
		Cancel = new Button("Cancel");
		statusLS = new Statuszeile("Please enter");

		gbc.gridx		= 0;
		gbc.gridy		= 0;
		gbc.gridwidth	= 1;
		gbc.gridheight	= 1;
		gbc.fill			= GridBagConstraints.HORIZONTAL;
		gbc.weightx		= 1;
		gbc.weighty		= 1;
		ins.top			= 5;
		ins.bottom		= 5;
		ins.left			= 5;
		ins.right		= 5;
		gbc.insets		= ins;
		gbl.setConstraints(fileL,gbc);
		this.add(fileL);

		gbc.gridx		= 1;
		gbc.gridy		= 0;
		gbc.gridwidth	= 1;
		gbc.gridheight	= 1;
		gbc.fill			= GridBagConstraints.HORIZONTAL;
		gbc.weightx		= 1;
		gbc.weighty		= 1;
		ins.top			= 5;
		ins.bottom		= 5;
		ins.left			= 5;
		ins.right		= 5;
		gbc.insets		= ins;
		gbl.setConstraints(file,gbc);
		this.add(file);

		gbc.gridx		= 0;
		gbc.gridy		= 1;
		gbc.gridwidth	= 1;
		gbc.gridheight	= 1;
		gbc.fill			= GridBagConstraints.HORIZONTAL;
		gbc.weightx		= 1;
		gbc.weighty		= 1;
		ins.top			= 5;
		ins.bottom		= 5;
		ins.left			= 5;
		ins.right		= 5;
		gbc.insets		= ins;
		gbl.setConstraints(passwdL,gbc);
		this.add(passwdL);

		gbc.gridx		= 1;
		gbc.gridy		= 1;
		gbc.gridwidth	= 1;
		gbc.gridheight	= 1;
		gbc.fill			= GridBagConstraints.HORIZONTAL;
		gbc.weightx		= 1;
		gbc.weighty		= 1;
		ins.top			= 5;
		ins.bottom		= 5;
		ins.left			= 5;
		ins.right		= 5;
		gbc.insets		= ins;
		gbl.setConstraints(passwd,gbc);
		this.add(passwd);

		gbc.gridx		= 0;
		gbc.gridy		= 2;
		gbc.gridwidth	= 1;
		gbc.gridheight	= 1;
		gbc.fill			= GridBagConstraints.HORIZONTAL;
		gbc.weightx		= 1;
		gbc.weighty		= 1;
		ins.top			= 5;
		ins.bottom		= 5;
		ins.left			= 5;
		ins.right		= 5;
		gbc.insets		= ins;
		gbl.setConstraints(OK,gbc);
		this.add(OK);

		gbc.gridx		= 1;
		gbc.gridy		= 2;
		gbc.gridwidth	= 1;
		gbc.gridheight	= 1;
		gbc.fill			= GridBagConstraints.HORIZONTAL;
		gbc.weightx		= 1;
		gbc.weighty		= 1;
		ins.top			= 5;
		ins.bottom		= 5;
		ins.left			= 5;
		ins.right		= 5;
		gbc.insets		= ins;
		gbl.setConstraints(Cancel,gbc);
		this.add(Cancel);

		gbc.gridx		= 0;
		gbc.gridy		= 3;
		gbc.gridwidth	= 2;
		gbc.gridheight	= 1;
		gbc.fill			= GridBagConstraints.HORIZONTAL;
		gbc.weightx		= 0;
		gbc.weighty		= 1;
		ins.top			= 0;
		ins.bottom		= 0;
		ins.left			= 5;
		ins.right		= 5;
		gbc.insets		= ins;
		gbl.setConstraints(statusLS,gbc);
		this.add(statusLS);
	}

	/** verarbeitet die Buttons (OK und Cancel) */
   	public boolean action(Event evt, Object arg) {
		String error = "";
		if ((evt.target==OK)|((evt.id==Event.ACTION_EVENT)&&(evt.target==passwd))) {
			if (this.getTitle().equals("Save")) error=sendData();
			else if (this.getTitle().equals("Open")) error=loadData();
			if (error.equals("")) this.dispose();
			else statusLS.set(error);
			return true;
		}
		if (evt.target == Cancel) {
			this.dispose();
			return true;
		}
		else if (evt.target == file) {
			passwd.requestFocus();
			return true;
		}
		else return super.action(evt,arg);
	}

	/** verarbeitet WINDOW_DESTROY */
	public boolean handleEvent(Event evt) {
		if (evt.id == Event.WINDOW_DESTROY) {
			this.dispose();
			return true;
		}
		else if (evt.id == Event.KEY_PRESS && evt.key=='\t') {
			if (evt.target==file) passwd.requestFocus();
			if (evt.target==passwd) OK.requestFocus();
			if (evt.target==OK) Cancel.requestFocus();
			if (evt.target==Cancel) file.requestFocus();
			return true;
		}
		else return super.handleEvent(evt);
	}

	private String sendData()										// FSM �ber den WWW-Server speichern
	{							
		String error = "";
		Signal sig;
		Zustand zu;
		Transition tran;
	
		String data = file.getText().toUpperCase()+"\n"+passwd.getText().toUpperCase()+"\n";	// Filename & Passwort
		data +=fsm.saveFSM();
		URL url = Parent.javafsm.baseUrl;							// URL des Home-WWW-Servers holen
		int port = url.getPort();
		if (port<0) port=80;										// falls kein Port definiert (-1): Port 80
		try
		{
			Socket s = new Socket(url.getHost(), port);				// Verbindung herstellen
			DataInputStream in = new DataInputStream(s.getInputStream());
			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			out.writeBytes("POST /cgi-bin/SaveFSM.exe HTTP/1.0\n");	// HTTP-Header senden
			out.writeBytes("Content-type: text/plain\n");			// Daten vom Typ ASCII senden
			out.writeBytes("Content-length: "+data.length()+"\n\n");// L�nge der gesamten Daten
			out.writeBytes(data);									// gesamte Daten senden
			while (!((in.readLine()).trim().equals(""))) {}			// auf Leerzeile als Trennung warten !!!
			String str = in.readLine().trim();						// Antwort holen
			if(!str.equals("OK")) 									// R�ckmeldung ist "OK" -> fertig !
			{
				error=str;
				if (str.equals("")) error="Error - no response"; // falls irgendwie keine Daten kommen!
				if (str.equals("<HTML>")) error="Server - Error";	// der WWW-Server hat geantwortet, NICHT das cgi-Programm!!!
			}
			out.close();
			in.close();
			s.close();
			return error;											// m�glichen Fehler zur�ckgeben, sonst leerer String ("")
		}
		catch (Exception e) {
			return ("Error: "+e.toString());
		}
	}

	protected String loadData() {					// eine FSM vom WWW-Server laden
		String error = "";							// zeigt einen Fehler an, falls ungleich ""
		String str;
		URL url = Parent.javafsm.baseUrl;			// URL vom Home-WWW-Server
		String data = file.getText().toUpperCase()+"\n"+passwd.getText().toUpperCase()+"\n"; // Filename & Passwort
		int port = url.getPort();
		if (port<0) port=80;						// wenn kein Port definiert (-1): Port 80
		try {
			Socket s = new Socket(url.getHost(), port);							// Verbindung herstellen
			DataInputStream in = new DataInputStream(s.getInputStream());
			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			out.writeBytes("POST /cgi-bin/LoadFSM.exe HTTP/1.0\n");				// HTTP-Header senden
			out.writeBytes("Content-type: text/plain\n");						// Daten vom Typ ASCII senden
			out.writeBytes("Content-length: "+data.length()+"\n\n");			// L�nge der Daten (hier nur Filename & Passwort !)
			out.writeBytes(data);												// Daten senden

			while (!(str=(in.readLine()).trim()).equals("")) { }				// auf Leerzeile als Trennung warten !!!
			str = (in.readLine()).trim();										// Antwort holen
			if (str.equals("OK")) 												// Wenn "OK", dann kommen jetzt Daten ...
			{
				str = fsm.loadFSM(in);
				if (str!=null)
				{
					fsm.newMachine();
				}
				else 
				{
					if (!fsm.getMachineType())
					{
						Parent.menItMoore.setState(false);			// Menu setzen (Haken vor den ausgew�hlten 
						Parent.menItMealy.setState(true);			// Automatentyp)
						Parent.editFrame.setMachineType("MOORE");
					}
					else 
					{
						Parent.menItMoore.setState(false);			// Menu setzen (Haken vor den ausgew�hlten 
						Parent.menItMealy.setState(true);			// Automatentyp)
						Parent.editFrame.setMachineType("MEALY");
					}
					Parent.impulsFrame.impulsCanvas.setIOColors();	// Farben f�r's ImpulsDiagramm verteilen
					str="file loaded";
				}
			}
			else 
			{
				if (str.equals("")) str="Error - no response";	// falls irgendwie keine Daten kommen!
				if (str.startsWith("<")) str="Server - Error";		// der WWW-Server hat geantwortet, NICHT das cgi-Programm!!!
			}
			out.close();
			in.close();
			s.close();
			Parent.setTitle("JavaFSM - "+fsm.getName());
			Parent.editFrame.setTitle("Editor - "+fsm.getName());
			Parent.impulsFrame.setTitle("Waveform - "+fsm.getName());
			Parent.editFrame.setPanel(null);
			Parent.simulation.repaint();
			Parent.repaint();
			Parent.impulsFrame.resetScrollValue();						// macht ein impulsCanvas und impulsFrame repaint() und setzt die Scrollbars neu
			return str;
		}
		catch (Exception e) {
			return ("Error: "+e.toString());
		}
	}
}
