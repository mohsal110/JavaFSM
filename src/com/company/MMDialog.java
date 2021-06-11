package com.company;
import java.awt.*;


/**
* <IMG SRC="../doku/images/logo_klein.gif" ALT="JavaFSM"><BR>
* Dialog aking if
* Moore or Mealy
*/
class MMDialog extends Dialog {
	private FSM fsm;
	private Button Mealy, Moore;			// Auswahlbutton
	private Label text1, text2;				// Labels f�r Text
	/** speichert den aufrufenden Frame */
	public  MainFrame Parent;

	/** 
	* Konstruktor
	* @param p aufrufendes Fenster (MainFrame)
	* @param title Fenstertitel (String)
	* @param modal siehe java.awt.Dialog (booelan)
	*/
	public MMDialog(MainFrame p,String title, boolean modal) {
		super(p,title,modal);

		Parent = p;
		fsm = Parent.fsm;

		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		this.setLayout(gbl);
		Insets ins = new Insets(0,0,0,0);

		text1 = new Label("Please choose if you want a");
		text2 = new Label("Mealy- or Moore-Network:");
		text1.setAlignment(Label.CENTER);
		text2.setAlignment(Label.CENTER);
		Mealy = new Button("Mealy");		// Auswahlbutton Mealy
		Moore = new Button("Moore");		// Auswahlbutton Moore

	/* Layout setzen */
		gbc.gridx		= 0;
		gbc.gridy		= 0;
		gbc.gridwidth	= 2;
		gbc.gridheight	= 1;
		gbc.fill		= GridBagConstraints.BOTH;
		gbc.weightx		= 1;
		gbc.weighty		= 1;
		ins.top			= 5;
		ins.bottom		= 0;
		ins.left		= 5;
		ins.right		= 5;
		gbc.insets		= ins;
		gbl.setConstraints(text1,gbc);
		this.add(text1);

		gbc.gridx		= 0;
		gbc.gridy		= 1;
		gbc.gridwidth	= 2;
		gbc.gridheight	= 1;
		gbc.fill		= GridBagConstraints.BOTH;
		gbc.weightx		= 1;
		gbc.weighty		= 1;
		ins.top			= 0;
		ins.bottom		= 0;
		ins.left		= 5;
		ins.right		= 5;
		gbc.insets		= ins;
		gbl.setConstraints(text2,gbc);
		this.add(text2);

		gbc.gridx		= 0;
		gbc.gridy		= 2;
		gbc.gridwidth	= 1;
		gbc.gridheight	= 1;
		gbc.fill		= GridBagConstraints.HORIZONTAL;
		gbc.weightx		= 1;
		gbc.weighty		= 1;
		ins.top			= 5;
		ins.bottom		= 5;
		ins.left		= 5;
		ins.right		= 5;
		gbc.insets		= ins;
		gbl.setConstraints(Moore,gbc);
		this.add(Moore);

		gbc.gridx		= 1;
		gbc.gridy		= 2;
		gbc.gridwidth	= 1;
		gbc.gridheight	= 1;
		gbc.fill		= GridBagConstraints.HORIZONTAL;
		gbc.weightx		= 1;
		gbc.weighty		= 1;
		ins.top			= 5;
		ins.bottom		= 5;
		ins.left		= 5;
		ins.right		= 5;
		gbc.insets		= ins;
		gbl.setConstraints(Mealy,gbc);
		this.add(Mealy);
	}

	/** verarbeitet die Buttons (Mealy und Moore) */
     public boolean action(Event evt, Object arg) {				// Abfragen der buttons
		if (evt.target==Mealy) {								// falls Mealy ausgew�hlt wurde
			fsm.setMealy();										// den Modus auf MEALY setzen
			Parent.editFrame.setMachineType("MEALY");			// den Modus an Automateneditor weitergeben
			Parent.menItMoore.setState(false);					// Menu setzen (Haken vor den ausgew�hlten 
			Parent.menItMealy.setState(true);					//   Automatentyp
			Parent.simulation.repaint();
			this.dispose();										// Fenster schlie�en
			Parent.toFront();
			return true;
		}
		if (evt.target==Moore) {								// falls Moore ausgew�hlt wurde
			fsm.setMoore();				;						// den Modus auf MOORE setzen
			Parent.editFrame.setMachineType("MOORE");			// den Modus an Automateneditor weitergeben
			Parent.menItMoore.setState(true);					// Menu setzen (Haken vor den ausgew�hlten 
			Parent.menItMealy.setState(false);					//   Automatentyp
			Parent.simulation.repaint();
			this.dispose();										// Fenster schlie�en
			Parent.toFront();
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
		else return super.handleEvent(evt);
	}
}