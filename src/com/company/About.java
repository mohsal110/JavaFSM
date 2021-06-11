package com.company;
import java.awt.*;
import java.applet.*;
import java.net.*;


/**
 * <IMG SRC="../doku/images/logo_klein.gif" ALT="JavaFSM"><BR>
 * About 
 * <BR>  Informationen over JavaFSM 
 */
class About extends Dialog
{
	private Button OK;
	private Label text0, text1, text2, text3, text4;			// Labels f�r Text
	private MainFrame parent;
	private Image i;
	
	/**
	* @param p aufrufendes Fenster (MainFrame)
	* @param title Fenstertitel	(String)
	* @param modal siehe java.awt.Dialog (boolean)
	* @see Dialog
	*/
	public About(MainFrame p,String title, boolean modal){
		super(p,title,modal);
		parent=p;

		if (parent.javafsm.baseUrl!=null)	// Applet oder Application(baseUrl=null)
		{
			try
			{
				i = parent.javafsm.getImage(parent.javafsm.getCodeBase(),"logo.gif"); // Logo vom Server laden
			}
			catch(Exception e){}
		}
		else
		{
			i = this.getToolkit().getImage("logo.gif");		// Logo als File laden

		}
		MediaTracker medtrack = new MediaTracker(this);
		medtrack.addImage(i,1);
		try
		{
			medtrack.waitForID(1);
		}
		catch (Exception e) {}

		Panel textpanel = new Panel();
		GridBagLayout gbl = new GridBagLayout();		//Layout 
		GridBagConstraints gbc = new GridBagConstraints();
		textpanel.setLayout(gbl);
		Insets ins = new Insets(0,0,0,0);

		text0 = new Label("Version: 2007.12.0");
		text1 = new Label("Original: Karola Kronert and Ulrich Dallmann");
		text2 = new Label("{2kroene,2dallman}@informatik.uni-hamburg.de");
		text3 = new Label("Modified: Verilog/systemC generation, Help,... by:");
		text4 = new Label("M.B. Ghaznavi-Ghoushchi ghaznavi@shahed.ac.ir");
				
		text0.setAlignment(Label.CENTER);
		text1.setAlignment(Label.CENTER);
		text2.setAlignment(Label.CENTER);		
		text3.setAlignment(Label.CENTER);
		text4.setAlignment(Label.CENTER);
		
		OK = new Button("OK");
		
		gbc.gridx		= 0;
		gbc.gridy		= 1;
		gbc.gridwidth	= 2;
		gbc.gridheight	= 1;
		gbc.fill		= GridBagConstraints.HORIZONTAL;
		gbc.weightx		= 1;
		gbc.weighty		= 0;
		ins.top			= 0;
		ins.bottom		= 0;
		ins.left		= 5;
		ins.right		= 5;
		gbc.insets		= ins;
		gbl.setConstraints(text0,gbc);
		textpanel.add(text0);

		gbc.gridx		= 0;
		gbc.gridy		= 2;
		gbc.gridwidth	= 2;
		gbc.gridheight	= 1;
		gbc.fill		= GridBagConstraints.HORIZONTAL;
		gbc.weightx		= 1;
		gbc.weighty		= 0;
		ins.top			= 0;
		ins.bottom		= 0;
		ins.left		= 5;
		ins.right		= 5;
		gbc.insets		= ins;
		gbl.setConstraints(text1,gbc);
		textpanel.add(text1);

		gbc.gridx		= 0;
		gbc.gridy		= 3;
		gbc.gridwidth	= 2;
		gbc.gridheight	= 1;
		gbc.fill		= GridBagConstraints.HORIZONTAL;
		gbc.weightx		= 1;
		gbc.weighty		= 0;
		ins.top			= 0;
		ins.bottom		= 0;
		ins.left		= 5;
		ins.right		= 5;
		gbc.insets		= ins;
		gbl.setConstraints(text2,gbc);
		textpanel.add(text2);

		gbc.gridx		= 0;
		gbc.gridy		= 4;
		gbc.gridwidth	= 2;
		gbc.gridheight	= 1;
		gbc.fill		= GridBagConstraints.HORIZONTAL;
		gbc.weightx		= 1;
		gbc.weighty		= 0;
		ins.top			= 0;
		ins.bottom		= 0;
		ins.left		= 5;
		ins.right		= 5;
		gbc.insets		= ins;
		gbl.setConstraints(text3,gbc);
		textpanel.add(text3);

		gbc.gridx		= 0;
		gbc.gridy		= 5;
		gbc.gridwidth	= 2;
		gbc.gridheight	= 1;
		gbc.fill		= GridBagConstraints.HORIZONTAL;
		gbc.weightx		= 1;
		gbc.weighty		= 0;
		ins.top			= 0;
		ins.bottom		= 0;
		ins.left		= 5;
		ins.right		= 5;
		gbc.insets		= ins;
		gbl.setConstraints(text4,gbc);
		textpanel.add(text4);
		
		gbc.gridx		= 1;
		gbc.gridy		= 6;
		gbc.gridwidth	= 1;
		gbc.gridheight	= 1;
		gbc.fill		= GridBagConstraints.NONE;
		gbc.weightx		= 0;
		gbc.weighty		= 0;
		ins.top			= 5;
		ins.bottom		= 5;
		ins.left		= 5;
		ins.right		= 5;
		gbc.insets		= ins;
		gbl.setConstraints(OK,gbc);
		textpanel.add(OK);

		this.setLayout(null);
		this.add(textpanel);
		textpanel.reshape(0,140,300,140);
		
	}		
	
	/** zeichnet das Logo */
	public void paint(Graphics g){
		g.drawImage(i,6,10,288,99,this);
	}

	/** f&auml;ngt WINDOW_DESTROY ab */
	public boolean handleEvent(Event evt) {
		if (evt.id == Event.WINDOW_DESTROY) {
			this.dispose();
			return true;
		}
		else return super.handleEvent(evt);
	}

	/** schlie&szlig;t bei OK das Fenster */
	public boolean action(Event evt, Object arg){
		if (evt.target==OK)
		{
			this.dispose();
			return true;
		}
		else return super.action(evt,arg);
	}
}

