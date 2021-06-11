package com.company;
import java.awt.*;

/*
 *
 * NameDialog
 *
 */
class NameDialog extends Dialog
{
 	private Button OK,Cancel;
	private Label nameL;
	public  TextField name;
//	public  FSM fsm;
	public  MainFrame Parent;

   	/** 
	* Konstruktor
	* @param p aufrufendes Fenster (MainFrame)
	* @param title Fenstertitel (String)  
	* @param modal siehe java.awt.Dialog (booelan)
	*/
	public NameDialog(MainFrame p,String title, boolean modal) {
		super(p,title,modal);
		Parent = p;

		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		this.setLayout(gbl);
		Insets ins = new Insets(0,0,0,0);

		nameL = new Label("FSM name:");

		nameL.setAlignment(Label.CENTER);
		name   = new TextField(20);
		name.setText(Parent.fsm.getName());
		OK = new Button("OK");
		Cancel = new Button("Cancel");

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
		gbl.setConstraints(nameL,gbc);
		this.add(nameL);

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
		gbl.setConstraints(name,gbc);
		this.add(name);

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
		gbl.setConstraints(OK,gbc);
		this.add(OK);

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
		gbl.setConstraints(Cancel,gbc);
		this.add(Cancel);

	}

	/** verarbeitet die Buttons (OK und Cancel) */
   	public boolean action(Event evt, Object arg) {
		if (evt.target==OK){
			Parent.fsm.name=name.getText();
			Parent.status.set("");
			Parent.setTitle("JavaFSM - "+Parent.fsm.getName());
			Parent.editFrame.setTitle("Editor - "+Parent.fsm.getName());
			Parent.impulsFrame.setTitle("Waveform - "+Parent.fsm.getName());
			this.dispose();
			return true;
		}
		if (evt.target == Cancel) {
			this.dispose();
			Parent.status.set("");
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
			if (evt.target==name) OK.requestFocus();
			if (evt.target==OK) Cancel.requestFocus();
			if (evt.target==Cancel) name.requestFocus();
			return true;
		}
		else return super.handleEvent(evt);
	}
}

