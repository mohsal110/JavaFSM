package com.company;
import java.awt.*;
import java.util.*;
import java.net.URL;
import java.io.*;


/**
* <IMG SRC="../doku/images/logo_klein.gif" ALT="JavaFSM"><BR>
* the JavaFSM main window
*/
class MainFrame extends Frame implements FilenameFilter
{	
	/** Verweis auf das Applet */
	public JavaFSM javafsm;
	/** der endliche Automat*/
	public  FSM fsm;
	/** Vector mit den Beispielen auf dem Server (Parameter im HTML-File) */	
	public  Vector beispiele;
	
	/** Editor f�r den Automaten */
	public  EditFrame 	editFrame;
	/** gibt an, ob der Editor ge�ffnet war, als in den Simulationsmodus gewechselt wurde */
	protected boolean edit;
	/** Impulsdiagramm */
	public  ImpulsFrame 	impulsFrame;
	/** About Fenster */
	public About about;
	
	/* Buttonleiste */
	private Panel			buttonleiste;
	/* Buttonleiste */
	private Panel			buttonleiste_edit;
	/* Buttonleiste */
	private Panel			buttonleiste_simu;
	/* CardLayout */
	private CardLayout		cardLayout;
	/** Statuszeile */
	public  Statuszeile 	status;
	/** Panel mit dem Schaltbild */
	public  Simulation 	simulation;
		
	/** Werte f&uuml;r "modus" */
	public  static final int EDITIEREN=0, SIMULIEREN=1;
	/** Modusvariable, kann die Werte "SIMULIEREN" oder "EDITIEREN" annehmen */
	public  int modus=EDITIEREN;


	/**
	*	Men&uuml;s:
	*	Hauptmen&uuml;s		Unterpunkte
	*	==========			===========
	*	Datei:				Neu,Laden, Speichern; Konvertieren; Beispiele; Beenden
	*	Signal:				Neuer Input, Neuer Output, &Auml;ndern, L&ouml;schen
	*	Automat:			Editor; Moore, Mealy
	*	Impulsdiagramm:		Anzeigen
	*	Simulation:			Simulieren, Editieren, Takt, Reset
	*/
	private MenuBar menuBar;
	private Menu menuFile, menuEdit, menuSignal, menuAutomat, menuImpulsd, menuSimulation, menuAbout; 
	private MenuItem menItNeuFile;
	private MenuItem menItSpeichernFile;
	private MenuItem menItLadenFile;
	private Menu menuKonvertieren;
	private MenuItem menItVerilog;
	private MenuItem menItSystemC;
	private MenuItem menItVHDL;
	private MenuItem menItKISS;
	
	private MenuItem menItCut;
	private MenuItem menItCopy;
	private MenuItem menItPaste;
	private MenuItem menItOptions;
	
	
	private Menu menuBeispiele;
	private MenuItem menItBeendenFile;
	private MenuItem menItNeuIn;
	private MenuItem menItAendern;
	private MenuItem menItLoeschen;
	private MenuItem menItNeuOut;
	
	private MenuItem menItName;
	private MenuItem menItEditAuto;
	/** Men&uuml;-Checkboxen zur Auswahl des Schaltwerktyps */
	public  CheckboxMenuItem menItMoore,menItMealy;
	private MenuItem menItAnzeigenImpuls;
	private MenuItem menItSimuSim;
	private MenuItem menItStopSim;
	private MenuItem menItTaktSim;
	private MenuItem menItResetSim;
	private MenuItem menItAbout;
	private MenuItem menItHelp;
	//mbgg com.ibm.jhelper.HelpContext ivjctxHelp;
	
	/* Buttons, die im Editiermodus sichtbar sind */
	private Button NeuIn, NeuOut, Aendern, Loeschen;
	/*	Buttons, die im Simuliermodus sichtbar sind */
	private Button Takt, Reset ;
	/** Statusstring */
	public  static final String status_eingeben			= "Design inputs, outputs and machines or change to simulation-mode";
	/** Statusstring */
	public  static final String status_inputneu			= "Enter a name and a value for the new input";
	/** Statusstring */
	public  static final String status_outputneu		= "Enter a name and a value for the new output";
	/** Statusstring */
	public  static final String status_keinesignale		= "No signals to delete";
	/** Statusstring */
	public  static final String status_keinenaendern	= "No signals to change";
	/** Statusstring */
	public  static final String status_loeschen			= "click on signal to delete";
	/** Statusstring */
	public  static final String status_aendern			= "click on signal to change";
	/** Statusstring */
	public  static final String status_keinStartzustand= "No start-state defined";
	/** Statusstring */
	public  static final String status_simulation		= "";
	/** Satusstring */
	public	static final String status_name				= "Enter a name for the machine";


	/**
	* Konstruktor
	* @param parent aufrufendes Applet (JavaFSM)
	* @param title Fenstertitel (String)
	* @param Url URL, von der das Applet kommt (URL)
	* @param Beispiele Vektor, mit den vorhandenen Beipiel-Namen (Vector)
	*/
	public MainFrame(JavaFSM parent, String title, URL Url, Vector Beispiele) 
	{
		super(title);
		/* �bergebene Objekte speichern */
		javafsm		= parent;
		beispiele	= Beispiele;
		//mbgg ivjctxHelp = null;
		status		= new Statuszeile(status_eingeben);
		about		= new About(this,"About JavaFSM", true);
		about.resize(300,300);


		/* der endliche Automat */
		fsm = new FSM(status);
		/* Automaten-Editor-Fenster */
		editFrame = new EditFrame("Editor",fsm);
		editFrame.resize(600,470);
		/* Impulsdiagramm-Fenseter */
		impulsFrame = new ImpulsFrame("Impulsdiagramm", fsm);
		impulsFrame.resize(600,470);

		/*	Panels initialisieren	*/	
		buttonleiste		= new Panel();
		buttonleiste_edit	= new Panel();
		buttonleiste_simu	= new Panel();
		cardLayout			= new CardLayout();
		buttonleiste.setLayout(cardLayout);

		buttonleiste.setBackground(Color.lightGray);
		buttonleiste_edit.setBackground(Color.lightGray);
		buttonleiste_simu.setBackground(Color.lightGray);
		buttonleiste_edit.setLayout(new GridLayout(1,4));
		buttonleiste_simu.setLayout(new GridLayout(1,2));
		buttonleiste.add("edit", buttonleiste_edit);
		buttonleiste.add("simu", buttonleiste_simu);
		/* Panel im Hauptfenster, in dem das Schaltbild zu sehen ist */
		simulation 		= new Simulation(this, fsm, status, editFrame);

		this.setBackground(Color.lightGray);
		setFont(new Font("Helvetica",Font.PLAIN,12));

		/*	Men� hinzuf�gen */
		menuBar					= new MenuBar();
		menuFile				= new Menu("File");
		menuEdit				= new Menu("Edit");
 		menuSignal				= new Menu("Signals");
		menuAutomat				= new Menu("FSM");
		menuImpulsd				= new Menu("Waveform");
		menuSimulation			= new Menu("Simulation");
		menuAbout				= new Menu("Help");

		menItNeuFile			= new MenuItem("New");
		menItSpeichernFile		= new MenuItem("Save");
		menItLadenFile			= new MenuItem("Open");
		menuKonvertieren		= new Menu("Export");
		menItVerilog			= new MenuItem("Verilog");
		menItSystemC			= new MenuItem("SystemC");
		menItVHDL				= new MenuItem("VHDL");
		menItKISS				= new MenuItem("KISS");
		menuBeispiele			= new Menu("Examples");
		menItBeendenFile		= new MenuItem("Exit");

		menItCut			= new MenuItem("Cut");
		menItCopy			= new MenuItem("Copy");
		menItPaste			= new MenuItem("Paste");
		menItOptions		= new MenuItem("Options");

		
		menItNeuIn				= new MenuItem("New Input");
		menItNeuOut				= new MenuItem("New Output");
		menItAendern			= new MenuItem("Change");
		menItLoeschen			= new MenuItem("Delete");
		
		menItName				= new MenuItem("Name");
		menItEditAuto			= new MenuItem("FSM Editor");
		menItMoore				= new CheckboxMenuItem("Moore");
		menItMealy				= new CheckboxMenuItem("Mealy");
		
		menItAnzeigenImpuls		= new MenuItem("Show");
		
		menItStopSim			= new MenuItem("Stop Simulation");
		menItSimuSim			= new MenuItem("Simulate");
		menItTaktSim			= new MenuItem("Clock");		
		menItResetSim			= new MenuItem("Reset");
				
		menItHelp				= new MenuItem("Help Contents");
		menItAbout				= new MenuItem("About JavaFSM");
		menuFile.add(menItNeuFile);
		menuFile.add(menItLadenFile);
		menuFile.add(menItSpeichernFile);
		menuFile.addSeparator();
		menuKonvertieren.add(menItVerilog);
		menuKonvertieren.add(menItVHDL);
		menuKonvertieren.add(menItKISS);
		menuKonvertieren.add(menItSystemC);
		menuFile.add(menuKonvertieren);
		menuFile.addSeparator();
		if (beispiele.size()>0)
		{
			/* Je nach der Anzahl der im HTML-File �bergebenen Parameter f�r Beispiel-Automaten MenuItems hinzuf�gen */
			for (int i=0; i<beispiele.size(); i+=2)
			{
				menuBeispiele.add(new MenuItem ((String)(beispiele.elementAt(i))));
			}
			menuFile.add(menuBeispiele);
			menuFile.addSeparator();
		}
		menuFile.add(menItBeendenFile);

		menuEdit.add(menItCut);
		menuEdit.add(menItCopy);
		menuEdit.add(menItPaste);
		menuEdit.addSeparator();
		menuEdit.add(menItOptions);

		
		menuSignal.add(menItNeuIn);
		menuSignal.add(menItNeuOut);
		menuSignal.add(menItAendern);
		menuSignal.add(menItLoeschen);
		menuAutomat.add(menItName);
		menuAutomat.addSeparator();
		menuAutomat.add(menItEditAuto);
		menuAutomat.addSeparator();
		menuAutomat.add(menItMoore);
		menuAutomat.add(menItMealy);
		menuImpulsd.add(menItAnzeigenImpuls);
		menuSimulation.add(menItSimuSim);
		menuSimulation.add(menItStopSim);
		menuSimulation.add(menItTaktSim);
		menuSimulation.add(menItResetSim);
		menuAbout.add(menItHelp);
		menuAbout.add(menItAbout);
		/* setzt im Menu einen Haken vor den ausgew�hlten Automatentyp */
		menItMoore.setState(true);
		menItMealy.setState(false);
		menuBar.add(menuFile);
		menuBar.add(menuEdit);
		menuBar.add(menuSignal);	
		menuBar.add(menuAutomat);	
		menuBar.add(menuImpulsd);
		menuBar.add(menuSimulation);
		menuBar.add(menuAbout);
		this.setMenuBar(menuBar);

		/* Buttons hinzuf�gen */
		NeuIn = new Button("New Input");
		NeuIn.setForeground(Color.black);
		NeuIn.setBackground(Color.lightGray);
		buttonleiste_edit.add(NeuIn);		

		NeuOut = new Button("New Output");
		NeuOut.setForeground(Color.black);
		NeuOut.setBackground(Color.lightGray);
		buttonleiste_edit.add(NeuOut);		
		
		Aendern = new Button("Change");
		Aendern.setForeground(Color.black);
		Aendern.setBackground(Color.lightGray);
		buttonleiste_edit.add(Aendern);

		Loeschen = new Button("Delete");
		Loeschen.setForeground(Color.black);
		Loeschen.setBackground(Color.lightGray);
		buttonleiste_edit.add(Loeschen);	

		Takt = new Button("Clock");
		Takt.setForeground(Color.black);
		Takt.setBackground(Color.lightGray);
		buttonleiste_simu.add(Takt);
	
		Reset = new Button("Reset");
		Reset.setForeground(Color.black);
		Reset.setBackground(Color.lightGray);
		buttonleiste_simu.add(Reset);
		/* setzt je nach Modus (SIMULIEREN/EDITIEREN) das Men� und die Buttons */
		setMenue();

		/* Layout der Panels */
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		this.setLayout(gbl);
		Insets ins = new Insets(0,0,0,0);

		gbc.gridx		= 0;
		gbc.gridy		= 1;
		gbc.gridwidth	= 1;
		gbc.gridheight	= 1;
		gbc.fill			= GridBagConstraints.BOTH;
		gbc.weightx		= 60;
		gbc.weighty		= 100;
		ins.top			= 0;
		ins.bottom		= 5;
		ins.left			= 5;
		ins.right		= 0;
		gbc.insets		= ins;
		gbl.setConstraints(simulation,gbc);
		this.add(simulation);

		gbc.gridx		= 0;
		gbc.gridy		= 0;
		gbc.gridwidth	= 1;
		gbc.gridheight	= 1;
		gbc.fill			= GridBagConstraints.HORIZONTAL;
		gbc.weightx		= 100;
		gbc.weighty		= 0;
		ins.top			= 0;
		ins.bottom		= 0;
		ins.left			= 5;
		ins.right		= 5;
		gbc.insets		= ins;
		gbl.setConstraints(buttonleiste,gbc);
		this.add(buttonleiste);

		gbc.gridx		= 0;
		gbc.gridy		= 2;
		gbc.gridwidth	= 1;
		gbc.gridheight	= 1;
		gbc.fill			= GridBagConstraints.HORIZONTAL;
		gbc.weightx		= 100;
		gbc.weighty		= 0;
		ins.top			= 0;
		ins.bottom		= 0;
		ins.left			= 5;
		ins.right		= 5;
		gbc.insets		= ins;
		gbl.setConstraints(status,gbc);
		this.add(status);
		repaint();
	}

	/**
	* setzt je nach Modus(EDITIEREN/SIMULIEREN) das Menue 
	* (graut nicht aktive Optionen im Men&uuml; aus und 
	* zeichnet nur aktive Buttons)
	*/
	public void setMenue()
	{
		/* Eingabemodus: Buttons zum Festlegen der In/Outputs, Automateneditor */
		if (modus==EDITIEREN) 
		{
			menItNeuFile.enable();
			menItLadenFile.enable();
			menItSpeichernFile.enable();
			menuKonvertieren.enable();
			menuBeispiele.enable();
			menItNeuIn.enable();
			menItNeuOut.enable();
			menItAendern.enable();
			menItLoeschen.enable();
			menItEditAuto.enable();
			menItMoore.enable();
			menItMealy.enable();
			menItAnzeigenImpuls.disable();
			menItSimuSim.enable();
			menItStopSim.disable();
			menItTaktSim.disable();
			menItResetSim.disable();
			cardLayout.show(buttonleiste, "edit");
		}

		/* Simulationsmodus: Buttons Takt und Reset, Impulsdiagramm */
		else 
		{
			menItNeuFile.disable();
			menItLadenFile.disable();
			menItSpeichernFile.disable();
			menuKonvertieren.disable();
			menuBeispiele.disable();
			menItNeuIn.disable();
			menItNeuOut.disable();
			menItAendern.disable();
			menItLoeschen.disable();
			menItEditAuto.disable();
			menItMoore.disable();
			menItMealy.disable();
			menItAnzeigenImpuls.enable();
			menItSimuSim.disable();
			menItStopSim.enable();
			menItTaktSim.enable();
			menItResetSim.enable();
			cardLayout.show(buttonleiste, "simu");
		}
	}


	/* das FilenameFilter Interface erfordert diese Methode -> .fsm Dateien
	* @param dir das File-Objekt, das gepr&uuml;ft werden soll (File)
	* @param name Filename (String)
	* @return boolean ob das File angezeigt werden soll
	*/
	public boolean accept(File dir, String name) 
	{
		return (dir.getName().endsWith(".fsm") && dir.isFile());
	}

	/** behandelt die Buttons und Men�s */
	public boolean action(Event event, Object arg) 
	{
		/* Buttons */
		if (event.target instanceof Button) 
		{
			String label = (String) arg;
			if (label.equals("New Input")) 
			{
				/* Status setzen und neuesSignal aufrufen */
				status.set(status_inputneu);
				SignalDialog signalDialog = new SignalDialog	
					(this,"New Input", true, fsm, fsm.newInput());
				signalDialog.resize(300,200);
				signalDialog.show();
				signalDialog.resize(300,200);
				signalDialog.namensfeld.requestFocus();
				return true;
			}
			if (label.equals("New Output")) 
			{
				/* Status setzen und neuesSignal aufrufen */
				status.set(status_outputneu);
				SignalDialog signalDialog = new SignalDialog
					(this,"New Output", true, fsm, fsm.newOutput());
				signalDialog.resize(300,200);
				signalDialog.show();
				signalDialog.resize(300,200);
				signalDialog.namensfeld.requestFocus();
				return true;
			}
			if (label.equals("Change")) 
			{
				/* falls kein Input/Output vorhanden, Status setzen; sonst Status setzen und Aendern aufrufen */
				if (((Vector)fsm.inputs).size()==0&&((Vector)fsm.outputs).size()==0) status.set(status_keinenaendern);
				else 
				{
					status.set(status_aendern);
					simulation.Aendern();
				}
				return true;
			}
			if (label.equals("Delete")) 
			{
				/* falls kein Input/Output vorhanden, Status setzen; sonst Status setzen und Loeschen aufrufen */
				if (((Vector)fsm.inputs).size()==0&&((Vector)fsm.outputs).size()==0)  status.set(status_keinesignale);
				else 
				{ 
					status.set(status_loeschen);
					simulation.Loeschen();
				}
				return true;
			}
			if (label.equals("Reset")) 
			{
				/* reset aufrufen */
				fsm.reset();
				impulsFrame.resetScrollValue();													// macht ein impulsCanvas und impulsFrame repaint() und setzt die Scrollbars neu
				simulation.repaint();
				this.repaint();
				return true;
			}
			if (label.equals("Clock")) 
			{
				/* takt aufrufen */
				fsm.takt();
				impulsFrame.setBars();	// Scrollbalken setzen
				simulation.repaint();
				this.repaint();
				return true;
			}
		}
		/* Men� bearbeiten */
		/* Neuer Automat */
		if (event.target==menItNeuFile)
		{
			fsm.newMachine();
			editFrame.repaint();
			editFrame.editCanvas.repaint();
			impulsFrame.resetScrollValue();													// macht ein impulsCanvas und impulsFrame repaint() und setzt die Scrollbars neu
			simulation.repaint();
			status.set(status_eingeben);
			this.repaint();
			return true;
		}

		/* Laden */
		if (event.target==menItLadenFile) 
		{
			if (javafsm.baseUrl != null)
			{
				/* UserDialog aufrufen, um ein gespeicherten Automaten zu laden */
				UserDialog userDialog = new UserDialog(this,"Open", true, fsm);
				userDialog.resize(300,200);
				userDialog.show();
				userDialog.resize(300,200);
				return true;
			}
			else
			{
				FileDialog fileDialog = new FileDialog(this,"Open",FileDialog.LOAD);
				fileDialog.setFilenameFilter(this);
				fileDialog.resize(300,200);
				fileDialog.show();
				fileDialog.resize(300,200);
				if (fileDialog.getFile()!=null)
				{
					try
					{
						FileInputStream fis = new FileInputStream(fileDialog.getDirectory()+fileDialog.getFile());
						DataInputStream dis = new DataInputStream(fis);
						String str = fsm.loadFSM(dis);
						dis.close();
						fis.close();
						if (str!=null) status.set(str);
					}
					catch(Exception e)
					{
						status.set("Cannot open file");
					}
				}
				editFrame.setPanel(null);
				impulsFrame.impulsCanvas.setIOColors();		// Farben f�r's ImpulsDiagramm verteilen
				simulation.repaint();
				this.repaint();
				impulsFrame.resetScrollValue();				// macht ein impulsCanvas und impulsFrame repaint() und setzt die Scrollbars neu
				this.setTitle("JavaFSM - "+fsm.getName());
				editFrame.setTitle("Editor - "+fsm.getName());
				impulsFrame.setTitle("Waveform - "+fsm.getName());
				return true;
			}
		}
		/* Speichern */
		if (event.target==menItSpeichernFile) 
		{
			if (javafsm.baseUrl != null)
			{
				/* UserDialog aufrufen, um einen Automaten zu speichern */
				UserDialog userDialog = new UserDialog(this,"Save", true, fsm);
				userDialog.resize(300,200);
				userDialog.show();
				userDialog.resize(300,200);
				return true;
			}
			else
			{
				FileDialog fileDialog = new FileDialog(this,"Save",FileDialog.SAVE);
				fileDialog.setFilenameFilter(this);
				fileDialog.resize(300,200);
				fileDialog.show();
				fileDialog.resize(300,200);
				if (fileDialog.getFile()!=null)
				{
					try
					{
						FileOutputStream fos = new FileOutputStream(fileDialog.getDirectory()+fileDialog.getFile());
						DataOutputStream dos = new DataOutputStream(fos);
						dos.writeBytes(fsm.saveFSM());
						dos.close();
						fos.close();
					}
					catch(Exception e)
					{
						status.set("Cannot save file");
					}
				}
				return true;
			}

		}
		/* Konvertieren nach Verilog */
		if (event.target==menItVerilog) 
		{
			/* Instanz von ExportFSM aufrufen mit String "VHDL" */
			ExportFSM export = new ExportFSM(this, fsm, "Verilog");
			export.resize(500,500);
			export.show();
			export.resize(500,500);
		}
		/* Konvertieren nach Verilog */
		if (event.target==menItSystemC) 
		{
			/* Instanz von ExportFSM aufrufen mit String "VHDL" */
			ExportFSM export = new ExportFSM(this, fsm, "SystemC");
			export.resize(500,500);
			export.show();
			export.resize(500,500);
		}
		/* Konvertieren nach VHDL */
		if (event.target==menItVHDL) 
		{
			/* Instanz von ExportFSM aufrufen mit String "VHDL" */
			ExportFSM export = new ExportFSM(this, fsm, "VHDL");
			export.resize(500,500);
			export.show();
			export.resize(500,500);
		}
		/* Konvertieren nach KISS*/
		if (event.target==menItKISS) 
		{
			/* Instanz von ExportFSM aufrufen mit String "KISS" */
			ExportFSM export = new ExportFSM(this, fsm, "KISS");
			export.resize(500,500);
			export.show();
			export.resize(500,500);
		}
		/* Beispiele */
		for (int i=0;i<beispiele.size();i+=2)
		{	
			if ( event.arg.equals((String)beispiele.elementAt(i))) 
			{
				/* MenuItem nur �ber Men�string greifbar */
				UserDialog userDialog = new UserDialog(this,"Laden", true, fsm);
				userDialog.file.setText((String)beispiele.elementAt(i));
				userDialog.passwd.setText((String)beispiele.elementAt(i+1));
				status.set(userDialog.loadData());
			}
		}
		/* Beenden */
		if (event.target==menItBeendenFile) 
		{
			/* Programm beenden, alle Fenster schlie�en */
			editFrame.dispose();
			impulsFrame.dispose();
			this.dispose();
			if (javafsm.baseUrl == null) System.exit(0);
			return true;
		}
		/* neuer Input */
		if (event.target==menItNeuIn)
		{
				/* Status setzen und neuesSignal aufrufen */
				status.set(status_inputneu);
				SignalDialog signalDialog = new SignalDialog
					(this,"New Input", true, fsm, fsm.newInput());
				signalDialog.resize(300,200);
				signalDialog.show();
				signalDialog.resize(300,200);
				signalDialog.namensfeld.requestFocus();
				return true;
		}
		/* neuer Output */
		if (event.target==menItNeuOut) 
		{
				/* Status setzen und neuesSignal aufrufen */
				status.set(status_outputneu);
				SignalDialog signalDialog = new SignalDialog
					(this,"New Output", true, fsm, fsm.newOutput());
				signalDialog.resize(300,200);
				signalDialog.show();
				signalDialog.resize(300,200);
				signalDialog.namensfeld.requestFocus();
				return true;
		}
		/* Signal �ndern */
		if (event.target==menItAendern) 
		{
			/* falls kein Signal vorhanden, Status setzen; sonst Status setzen und Aendern aufrufen */	
			if (((Vector)fsm.inputs).size()==0&&((Vector)fsm.outputs).size()==0) status.set(status_keinenaendern);
			else 
			{ 
				status.set(status_aendern);
				simulation.Aendern();
			}
			return true;
		}
		/* Signal l�schen */
		if (event.target==menItLoeschen) 
		{
			/* falls kein Signal vorhanden, Status setzen; sonst Status setzen und Loeschen aufrufen */
			if (((Vector)fsm.inputs).size()==0&&((Vector)fsm.outputs).size()==0)  status.set(status_keinesignale);
			else
			{
				status.set(status_loeschen);
				simulation.Loeschen();
			}
			return true;
		}
		/* Automaten Name editieren */
		if (event.target==menItName) 
		{
			/* Status setzen und NameDialog aufrufen */
			status.set(status_name);
			NameDialog nameDialog = new NameDialog(this, "FSM name - Please do not use blank spaces", true);
			nameDialog.resize(300,200);			
			nameDialog.show();
			nameDialog.resize(300,200);			
			return true;
		}
		/* Automat editieren */
		if (event.target==menItEditAuto) 
		{
			/* Status setzen und Automateneditor aufrufen */
			status.set(status_eingeben);
			editFrame.show();
			editFrame.toFront();
			return true;
		}
		/* Moore */
		if (event.target==menItMoore)
		{
			menItMoore.setState(true);
			menItMealy.setState(false);
			fsm.setMoore();
			/* den Modus an Automateneditor weitergeben */
			editFrame.setMachineType("MOORE");
			simulation.repaint();
			return true;
		}
		/* Mealy */	
		if (event.target==menItMealy)
		{
			menItMoore.setState(false);
			menItMealy.setState(true);
			fsm.setMealy();
			/* den Modus an Automateneditor weitergeben */
			editFrame.setMachineType("MEALY");
			simulation.repaint();
			return true;
		}
		/* Impulsdiagramm anzeigen */
		if (event.target==menItAnzeigenImpuls) 
		{
			/* Status setzen und Impulsdiagramm aufrufen */
			impulsFrame.show();
			impulsFrame.toFront();
			return true;
		}
		/* in den Simulationsmodus wechseln */
		if (event.target==menItSimuSim) 
		{
			if (fsm.existsStartzustand())
			{
				// Wenn ein Startzustand definiert ist...
				modus = SIMULIEREN;									// Modus auf SIMULIEREN setzen
				edit=editFrame.isVisible();
				editFrame.hide();											// Automateneditor schlie�en, damit am Automaten keine ver�nderungen vorgenommen werden k�nnen
				status.set(status_simulation);						// Status setzen
				simulation.modus = simulation.NORMAL;				// In Simulation den Modus auf normal setzen
				setMenue();													// die Men�s und Buttons setzen, Button Takt und Reset anzeigen, die anderen entfernen
				fsm.reset();														// vorsichtshalber mal einen Reset durchf�hren
				impulsFrame.resetScrollValue();													// macht ein impulsCanvas und impulsFrame repaint() und setzt die Scrollbars neu
				simulation.repaint();
				this.repaint();
			}
			else status.set(status_keinStartzustand);				// Wenn noch kein Startzustand definiert, im Editiermodus bleiben und Status setzen
			return true;
		}
		/* in den Editiermodus wechseln */
		if (event.target==menItStopSim) 
		{
			/* erstmal den User warnen, dass alle Daten verlorengehen */
			WarnDialog warnDialog = new WarnDialog(this, "Warning", true);
			warnDialog.resize(300,200);			
			warnDialog.show();
			warnDialog.resize(300,200);			
			return true;
		}
		/* Takt */
		if (event.target==menItTaktSim) 
		{
			/* takt aufrufen */
			fsm.takt();
			impulsFrame.setBars();	// Scrollbalken setzen
			simulation.repaint();
			this.repaint();
			return true;
		}
		/* Reset */
		if (event.target==menItResetSim) 
		{
			/* reset aufrufen */
			fsm.reset();
			impulsFrame.resetScrollValue();													// macht ein impulsCanvas und impulsFrame repaint() und setzt die Scrollbars neu
			simulation.repaint();
			this.repaint();
			return true;
		}
		/* alle anderen F�lle hoch reichen */
		if (event.target==menItHelp) 
		{
				/* Status setzen und neuesSignal aufrufen */
				this.HelpFSM();
				return true;
		}
		/* alle anderen F�lle hoch reichen */
		if (event.target==menItAbout) 
		{
				/* Status setzen und neuesSignal aufrufen */
				about.show();
				about.resize(300,300);
				return true;
		}
		
		else return super.action(event,arg);
	}


	/** behandelt WINDOW_DESTROY */
	public boolean handleEvent(Event evt) 
	{
		if (evt.id == Event.WINDOW_DESTROY) 
		{
			/* Alle Fenster schlie�en */
			editFrame.dispose();
			impulsFrame.dispose();
			this.dispose();
			if (javafsm.baseUrl == null) System.exit(0);
			return true; 
		}
		else return super.handleEvent(evt);
	}
	/** JavaFSM Main Help */
	public void HelpFSM()
    {
    	try
		{
			//mbgg ivjctxHelp = new com.ibm.jhelper.HelpContext();
			//mbgg ivjctxHelp.setHelpTopic("/docs/sim0001.html");
			//mbgg ivjctxHelp.setContentsTopic("/docs/sim0001.html");
			//mbgg ivjctxHelp.setIndexTopic("/docs/sim0001.html");
			//mbgg ivjctxHelp.activate();
			
		}
		catch (Throwable ivjExc)
		{
		}
		
	}	
}
