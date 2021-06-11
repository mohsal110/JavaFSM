package com.company;
import java.awt.*;

/**
* <IMG SRC="../doku/images/logo_klein.gif" ALT="JavaFSM"><BR>
* warns the user when changing from simul to edit 
* because you're losing data
*/
class WarnDialog extends Dialog {				
	private Button OK, Cancel;
	private Label text1, text2, text3;			// Labels f�r Text
	public  MainFrame Parent;
	private FSM fsm;

	/** 
	* Konstruktor
	* @param p aufrufendes Fenster (MainFrame)
	* @param title Fenstertitel (String)  
	* @param modal siehe java.awt.Dialog (booelan)
	*/
	public WarnDialog(MainFrame p,String title, boolean modal){
		super(p,title,modal);
		Parent	= p;	
		fsm		= Parent.fsm;

		GridBagLayout gbl = new GridBagLayout();		//Layout 
		GridBagConstraints gbc = new GridBagConstraints();
		this.setLayout(gbl);
		Insets ins = new Insets(0,0,0,0);

		text1 = new Label("When changing to the edit-mode");
		text2 = new Label("all simulation data"); 
		text3 = new Label("gets lost.");
		text1.setAlignment(Label.CENTER);
		text2.setAlignment(Label.CENTER);		
		text3.setAlignment(Label.CENTER);
		OK = new Button("OK");
		Cancel = new Button("Cancel");

		gbc.gridx		= 0;
		gbc.gridy		= 0;
		gbc.gridwidth	= 2;
		gbc.gridheight	= 1;
		gbc.fill		= GridBagConstraints.BOTH;
		gbc.weightx	= 1;
		gbc.weighty	= 1;
		ins.top		= 0;
		ins.bottom	= 0;
		ins.left		= 5;
		ins.right	= 5;
		gbc.insets	= ins;
		gbl.setConstraints(text1,gbc);
		this.add(text1);
		gbc.gridx		= 0;
		gbc.gridy		= 1;
		gbc.gridwidth	= 2;
		gbc.gridheight	= 1;
		gbc.fill		= GridBagConstraints.BOTH;
		gbc.weightx	= 1;
		gbc.weighty	= 1;
		ins.top		= 0;
		ins.bottom	= 0;
		ins.left		= 5;
		ins.right	= 5;
		gbc.insets	= ins;
		gbl.setConstraints(text2,gbc);
		this.add(text2);
		gbc.gridx		= 0;
		gbc.gridy		= 2;
		gbc.gridwidth	= 2;
		gbc.gridheight	= 1;
		gbc.fill		= GridBagConstraints.BOTH;
		gbc.weightx	= 1;
		gbc.weighty	= 1;
		ins.top		= 0;
		ins.bottom	= 5;
		ins.left		= 5;
		ins.right		= 5;
		gbc.insets	= ins;
		gbl.setConstraints(text3,gbc);
		this.add(text3);
		gbc.gridx		= 0;
		gbc.gridy		= 3;
		gbc.gridwidth	= 1;
		gbc.gridheight	= 1;
		gbc.fill		= GridBagConstraints.HORIZONTAL;
		gbc.weightx	= 1;
		gbc.weighty	= 1;
		ins.top		= 5;
		ins.bottom	= 5;
		ins.left		= 5;
		ins.right		= 5;
		gbc.insets	= ins;
		gbl.setConstraints(OK,gbc);
		this.add(OK);
		gbc.gridx		= 1;
		gbc.gridy		= 3;
		gbc.gridwidth	= 1;
		gbc.gridheight	= 1;
		gbc.fill		= GridBagConstraints.HORIZONTAL;
		gbc.weightx	= 1;
		gbc.weighty	= 1;
		ins.top		= 5;
		ins.bottom	= 5;
		ins.left		= 5;
		ins.right		= 5;
		gbc.insets	= ins;
		gbl.setConstraints(Cancel,gbc);
		this.add(Cancel);
	}		

	/** verarbeitet WINDOW_DESTROY */
	public boolean handleEvent(Event evt) {
		if (evt.id == Event.WINDOW_DESTROY) {
			this.dispose();
			return true;
		}
		else return super.handleEvent(evt);
	}

	/** verarbeitet die Buttons (OK und Cancel) */
	public boolean action(Event evt, Object arg){
		if (evt.target==OK){									// falls OK Wechsel in Editiermodus
			fsm.reset();									// reset ausf�hren
			Parent.impulsFrame.resetScrollValue();													// macht ein impulsCanvas und impulsFrame repaint() und setzt die Scrollbars neu
			Parent.simulation.repaint();
			Parent.repaint();

			Parent.status.set("");							// Status setzen
			Parent.modus=Parent.EDITIEREN;
			Parent.simulation.modus=Parent.simulation.NORMAL;			
			Parent.setMenue();								// Buttons und Men�s setzen
			if (Parent.edit) Parent.editFrame.show();
			this.dispose();									// Dialogfenster schlie�en
			return true;
		}
		if (evt.target==Cancel){							// sonst den Modus nicht wechseln
			this.dispose();
			return true;
		}
		else return super.action(evt,arg);
	}
}

