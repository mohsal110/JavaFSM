package com.company;
import java.awt.*;
import java.util.Vector;

/**
* <IMG SRC="../doku/images/logo_klein.gif" ALT="JavaFSM"><BR>
* Dialog to enter Inputs / Outputs 
*/
class SignalDialog extends Dialog 
{
	private Label labelName, labelInitial;
	/** Textfeld, das den Namen des Inputs / Outputs enth&auml;lt */
	public  TextField namensfeld;
	/* Checkboxgroup zur Festlegung des Initialwertes	
		(nur bei Input)*/	
	private CheckboxGroup wert;
	/* Checkboxen zur Festlegung des Initialwertes	
		(nur bei Input)*/
	private Checkbox HIGHwert, LOWwert;
	private Button OK, Cancel;

	/** Das neu erstellte Signal */
	private Signal signal;
	// Name des Signals 
	private String name="";
	// Wert, den das Signal annimmt
	private int value;				
	
	/** Referenz auf den Automaten */
	private FSM fsm;
	// Referenz auf den aufrufenden Frame															
	private MainFrame parent;

	/** Status-String */
	private static final String status_eingeben="Add new Inputs/Outputs or change to simulation-mode";
	/** Status-String */
	private static final String status_ungueltig="Name contains invalid character";
	/** Status-String */
	private static final String status_namevergeben="Name is used already";

	/**
	* Konstruktor
	* @param mainFrame aufrufendes Fenster (MainFrame)
	* @param title Fenstertitel (String)
	* @param modal siehe java.awt.Dialog (boolean)
	* @param Fsm zu bearbeitender Automat (FSM)
	* @param neues_signal zu vervollst�ndigendes Signal (Signal)
	*/
	public SignalDialog(MainFrame mainFrame, String title, 
		boolean modal, FSM Fsm, Signal neues_signal) 
	{
		super(mainFrame, title, modal);
		parent  = mainFrame;
		fsm		= Fsm;
		signal	= neues_signal;
		this.setBackground(Color.lightGray);

		GridBagLayout gbl		= new GridBagLayout();
		GridBagConstraints gbc	= new GridBagConstraints();
		this.setLayout(gbl);
		Insets ins				= new Insets(0,0,0,0);
		
		labelName = new Label();
		if (signal.in_out==Signal.IN) labelName.setText("Input-name:");
		else labelName.setText("Output-name:");
		gbc.gridx		= 0;
		gbc.gridy		= 0;
		gbc.gridwidth	= 1;
		gbc.gridheight	= 1;
		gbc.fill		= GridBagConstraints.NONE;
		gbc.weightx		= 0;
		gbc.weighty		= 0;
		ins.top			= 2;
		ins.bottom		= 2;
		ins.left		= 2;
		ins.right		= 2;
		gbc.insets		= ins;
		gbl.setConstraints(labelName,gbc);
		this.add(labelName);
		
		namensfeld		= new TextField("",20);	
		gbc.gridx		= 1;
		gbc.gridy		= 0;
		gbc.gridwidth	= 2;
		gbc.gridheight	= 1;
		gbc.fill		= GridBagConstraints.HORIZONTAL;
		gbc.weightx		= 100;
		gbc.weighty		= 0;
		ins.top			= 2;
		ins.bottom		= 2;
		ins.left		= 2;
		ins.right		= 7;
		gbc.insets		= ins;
		gbl.setConstraints(namensfeld,gbc);
		this.add(namensfeld);

			wert			= new CheckboxGroup();
			HIGHwert		= new Checkbox("high", wert, true);
			LOWwert			= new Checkbox("low", wert, false);

			labelInitial	= new Label("Initial value:");
			gbc.gridx		= 0;
			gbc.gridy		= 1;
			gbc.gridwidth	= 1;
			gbc.gridheight	= 1;
			gbc.fill		= GridBagConstraints.NONE;
			gbc.weightx		= 0;
			gbc.weighty		= 0;
			ins.top			= 2;
			ins.bottom		= 10;
			ins.left		= 2;
			ins.right		= 2;
			gbc.insets		= ins;
			gbl.setConstraints(labelInitial,gbc);
			
			gbc.gridx		= 1;
			gbc.gridy		= 1;
			gbc.gridwidth	= 1;
			gbc.gridheight	= 1;
			gbc.fill		= GridBagConstraints.NONE;
			gbc.weightx		= 0;
			gbc.weighty		= 0;
			ins.top			= 2;
			ins.bottom		= 10;
			ins.left		= 2;
			ins.right		= 2;
			gbc.insets		= ins;
			gbl.setConstraints(HIGHwert,gbc);
			
			gbc.gridx		= 2;
			gbc.gridy		= 1;
			gbc.gridwidth	= 1;
			gbc.gridheight	= 1;
			gbc.fill		= GridBagConstraints.NONE;
			gbc.weightx		= 0;
			gbc.weighty		= 0;
			ins.top			= 2;
			ins.bottom		= 10;
			ins.left		= 2;
			ins.right		= 2;
			gbc.insets		= ins;
			gbl.setConstraints(LOWwert,gbc);

		if (signal.in_out==Signal.IN) 
		{
			this.add(labelInitial);
			this.add(HIGHwert);
			this.add(LOWwert);
		}

		OK = new Button("OK");
		gbc.gridx		= 0;
		gbc.gridy		= 2;
		gbc.gridwidth	= 1;
		gbc.gridheight	= 1;
		gbc.fill		= GridBagConstraints.HORIZONTAL;
		gbc.weightx		= 0;
		gbc.weighty		= 0;
		ins.top			= 10;
		ins.bottom		= 2;
		ins.left		= 30;
		ins.right		= 2;
		gbc.insets		= ins;
		gbl.setConstraints(OK,gbc);
		this.add(OK);

		Cancel = new Button("Cancel");
		gbc.gridx		= 1;
		gbc.gridy		= 2;
		gbc.gridwidth	= 2;
		gbc.gridheight	= 1;
		gbc.fill		= GridBagConstraints.HORIZONTAL;
		gbc.weightx		= 0;
		gbc.weighty		= 0;
		ins.top			= 10;
		ins.bottom		= 2;
		ins.left		= 40;
		ins.right		= 40;
		gbc.insets		= ins;
		gbl.setConstraints(Cancel,gbc);
		this.add(Cancel);

		if(signal.initial==Signal.HIGH)wert.setCurrent(HIGHwert);
		else wert.setCurrent(LOWwert);

		namensfeld.setText(signal.name);
	}

	/** fragt WINDOW_DESTROY ab */
	public boolean handleEvent(Event evt) 
	{
		if (evt.id == Event.WINDOW_DESTROY) 
		{
			if (signal.name.equals(""))		// wenn NEU dann wieder l�schen (beim �ndern nat�rlich nicht l�schen
			{
				if (signal.in_out==Signal.IN)
				{
					fsm.deleteInput(signal);
					parent.editFrame.setPanel(parent.editFrame.editCanvas.getSelected());
				}
				else 
				{
					fsm.deleteOutput(signal);
					parent.editFrame.setPanel(parent.editFrame.editCanvas.getSelected());
				}
			}
			parent.status.set(status_eingeben);
			this.dispose();
			parent.simulation.repaint();
			return true;
		}
		else if (evt.id == Event.KEY_PRESS && evt.key=='\t') {
			if (evt.target==namensfeld) OK.requestFocus();
			if (evt.target==OK) Cancel.requestFocus();
			if (evt.target==Cancel) namensfeld.requestFocus();
			return true;
		}
		else return super.handleEvent(evt);
	}

	/** Fragt Action-Events ab */
	public boolean action(Event evt, Object arg)
	{
		// Cancel
		if (evt.target==Cancel) 
		{
			if (signal.name.equals(""))		// wenn NEU dann wieder l�schen (beim �ndern nat�rlich nicht l�schen
			{
				if (signal.in_out==Signal.IN) 
				{
					fsm.deleteInput(signal);
					parent.editFrame.setPanel(parent.editFrame.editCanvas.getSelected());
				}
				else 
				{
					fsm.deleteOutput(signal);
					parent.editFrame.setPanel(parent.editFrame.editCanvas.getSelected());
				}
			}
			parent.status.set(status_eingeben);
			this.dispose();
			parent.simulation.repaint();
			return true;
		}
		// OK oder Return im textfeld
		else if ((evt.target==OK)||((evt.id==Event.ACTION_EVENT)&&(evt.target==namensfeld))) 
		{
			name=namensfeld.getText();
			// ist der Name gueltig?
			// nur erlaubte Zeichen enthalten? bei modus=NEU: ist der Name bereits vergeben?
			boolean valid=true;			
			// name in CharArray speichern
			char[] cin=name.toCharArray();
			/* f�r Name g�ltige Zeichen (als erster Buchstabe 
				keine Zahl) falls ung�ltige Zeichen, dann valid=false*/
			if (!name.equals("") && (!((cin[0]>='A' && cin[0]<='Z')||(cin[0]>='a' && cin[0]<='z')||(cin[0]=='_')||(cin[0]=='_'))))
				valid=false;	
			// das Array durchlaufen
			for (int i=1;i<cin.length; i++){							
				if (!((cin[i]>='A' && cin[i]<='Z')||(cin[i]>='a' && cin[i]<='z')||(cin[i]=='_')||(cin[i]=='_')||(cin[i]>='0' && cin[i]<='9')))
				{	
					valid=false;	
				}
			}
			if (!valid){
				// falls ung�ltig, status setzen
				parent.status.set(status_ungueltig);				
			}
			// hinzuf�gen
			else 
			{
				boolean exists = false;				
				// �berpr�fung, ob Name bereits vergeben
				// wenn kein Name angegeben wurde, auf ung�ltig setzen
				if (name.equals("")) exists=true;
				// Inputs �berpr�fen
				Vector v = (Vector)fsm.inputs;
				for (int i=0; i<v.size(); i++) {		
					Signal s = (Signal)v.elementAt(i);
					/* wenn Name bereits existiert und nicht diesem 
						Signal geh�rt, dann ung�ltig */
					if (name.equals(s.name)&& (!s.equals(signal))) exists=true;
				}
				// Outputs �berpr�fen
				v = (Vector)fsm.outputs;
				for (int i=0; i< v.size(); i++){		
					Signal s = (Signal)v.elementAt(i);
					/* wenn Name bereits existiert und nicht diesem 
						Signal geh�rt, dann ung�ltig */
					if (name.equals(s.name)&& (!s.equals(signal))) exists=true;
				}
				// falls bereits vorhanden, Status setzen und nicht einf�gen
				if (exists==true) parent.status.set(status_namevergeben);
				// sonst einf�gen
				else 
				{
					if (HIGHwert.getState()==true) value=Signal.HIGH;
					else value		= Signal.LOW;
					signal.name		= name;
					signal.initial	= value;
					signal.value	= value;
					name			= "";

					this.hide();
					parent.status.set(status_eingeben);
				}
				fsm.reset();
				parent.simulation.repaint();
				parent.impulsFrame.impulsCanvas.setIOColors();
				parent.impulsFrame.impulsCanvas.repaint();
				parent.impulsFrame.repaint();
				parent.editFrame.setPanel(parent.editFrame.editCanvas.getSelected());
				parent.repaint();
			}
		}
		return true;
	}
}
