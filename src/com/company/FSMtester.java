package com.company;
import java.awt.*;
import java.util.*;

/**
* <IMG SRC="../doku/images/logo_klein.gif" ALT="JavaFSM"><BR>
* Dialog testing machine
* and displaying the results
*/				 
class FSMtester extends Dialog implements Runnable {					// Dialog-Fenster zum Durchtesten einer FSM
	private Button cancel;
	private Thread tester;
	private TextArea text;
	private Parser parser;
	private FSM fsm;
	private int OK=0;
	
	/**
	* Konstruktor, der Tester-Thread startet
	* @param p aufrufendes Fenster (Frame)
	* @param Fsm Automat, der &uuml;berpr&uuml;ft werden soll (FSM)
	*/
	public FSMtester(Frame p, FSM Fsm){
		super(p,"FSM-test",true);
		fsm = Fsm;
		parser = new Parser();

		GridBagLayout gbl = new GridBagLayout();		//Layout
		GridBagConstraints gbc = new GridBagConstraints();
		this.setLayout(gbl);
		Insets ins = new Insets(0,0,0,0);


		text = new TextArea();
		gbc.gridx		= 0;
		gbc.gridy		= 0;
		gbc.gridwidth	= 1;
		gbc.gridheight	= 1;
		gbc.fill		= GridBagConstraints.BOTH;
		gbc.weightx	= 1;
		gbc.weighty	= 1;
		ins.top		= 5;
		ins.bottom	= 5;
		ins.left		= 5;
		ins.right	= 5;
		gbc.insets	= ins;
		gbl.setConstraints(text,gbc);
		this.add(text);

		cancel = new Button("Cancel");
		gbc.gridx		= 0;
		gbc.gridy		= 1;
		gbc.gridwidth	= 1;
		gbc.gridheight	= 1;
		gbc.fill		= GridBagConstraints.HORIZONTAL;
		gbc.weightx	= 1;
		gbc.weighty	= 0;
		ins.top		= 5;
		ins.bottom	= 5;
		ins.left		= 5;
		ins.right	= 5;
		gbc.insets	= ins;
		gbl.setConstraints(cancel,gbc);
		this.add(cancel);

		tester= new Thread (this);
		tester.start();
	}		

	/** run-Methode des Tester-Threads */
	public void run(){
		Zustand zu;
		Transition tr;
		boolean error, start;
		OK=0;

		text.setText("Testing states ...\n");
		error = false;
		start = false;
		if (fsm.zustaende.size()==0) 
		{
			text.appendText("No states to test\n");
			OK++;
		}
		else {
			for (int i=0; i<fsm.zustaende.size(); i++) 
			{							// alle Ausdr�cke in den Zust�nden einmal durchparsen
				zu = (Zustand)fsm.zustaende.elementAt(i);							// Zustand zwischenspeichern
				if (zu.isStart) start=true;							// auf Startzustand �berpr�fen
				tester.yield();											// den Thread unterbrechen, um fair zu sein
				for (int j=0; j<fsm.outputs.size(); j++) 
				{														// alle Outputs durchgehen
					try 
					{																	// parse wirft BadExpressionException
						parser.parse((String)zu.outputHash.get((Signal)fsm.outputs.elementAt(j)),fsm.inputs);
					}
					catch (BadExpressionException e) 
					{													// Fehler in einer �bergangsfunktion
						text.appendText("Error: "+zu.name+" in Output-Fkt for "+((Signal)fsm.outputs.elementAt(j)).name+"\n");
						error = true;
						OK++;
					}
				}
			}
			if (!start) 
			{
				text.appendText("No start-state defined\n");
				OK++;
			}
			else if (!error) text.appendText("OK\n");
		}
		text.appendText("\nTesting transitions ...\n");
		error = false;
		if (fsm.transitionen.size()==0) 
		{
			text.appendText("No transitionens to test\n");
			OK++;
		}
		else 
		{
			for (int i=0; i<fsm.transitionen.size(); i++) 
			{												// alle Ausdr�cke in den Transitionen einmal durchparsen
				tr = (Transition)fsm.transitionen.elementAt(i);
				tester.yield();
				if (!tr.function.equals("*"))
				{ 
					try 
					{
						parser.parse(tr.function, fsm.inputs);
					}
					catch (BadExpressionException e) 
					{					// Fehler in einer �bergangsfunktion
						text.appendText("Error: Transition-function from "+tr.von.name+" to "+ tr.nach.name + "\n");
						error = true;
						OK++;
					}
				}
			}
			if (!error) text.appendText("OK\n");
		}
  
		text.appendText("\nTesting conditions ...\n");
		Vector relevant = new Vector(10,10);

		for (int i=0; i<fsm.zustaende.size(); i++) {
			//for (int j=0; j<fsm.transitionen.size(); j++) 				// alle relevanten Inputs in relevant speichern
			relevant=fsm.inputs;
			int index=0;
			error=false;
			zu = (Zustand)fsm.zustaende.elementAt(i);
			if(!durchgehen(relevant, index, zu, error))
			{
				text.appendText(zu.name+ " OK\n");
			}
			tester.yield();
		}
		text.appendText("Ready!\n");
		if (OK==0) text.appendText("No errors");
		else text.appendText("There are "+OK+" errors");
		cancel.setLabel("OK");		
	}

	// rekursiv alle Eing�nge durchgehen, um alle Inputbelegungen zu testen
	private boolean durchgehen(Vector relevant, int index, Zustand zu, boolean error){ // relevante Inputs,index auf die Inputs , zu �berpr�fender Zustand
		if (index==relevant.size()) {							// alle Inputs sind gesetzt, jetzt parsen
			int aktiviert = 0;									// Anzahl der aktivierten Transitionen
			int stern = 0;											// Anzahl der Transitionen mit * in der �bergangsbedingung
			for (int i=0; i<fsm.transitionen.size(); i++){					// alle Transitionen durchgehen
				Transition t=(Transition)fsm.transitionen.elementAt(i);
				if (t.von==zu){									// falls t von zu �berpr�fenden Zustand ausgeht	
					if ( t.function.equals("*")) stern++;	// auf stern �berpr�fen
					else{												// falls kein stern, parsen
						try {
							if(parser.parse(t.function,fsm.inputs)) aktiviert++;
						}
						catch (BadExpressionException e){} 	// wurde oben bereits ausgeschlossen
					}
				}
			}
			if (stern>1) {
				text.appendText("in "+zu+" more than one tTransition have a * in their funktion\n");
				error=true;
				OK++;
			}
			else if (aktiviert==0) {
				if (stern!=1) {
					text.appendText("in "+ zu.name +" under this condition ");
					for (int i=0; i<relevant.size(); i++) {
						text.appendText(((Signal)relevant.elementAt(i)).name + "=" + ((Signal)relevant.elementAt(i)).value +",  ");
					}
					text.appendText("no transition is activated\n");
					error=true;
					OK++;
				}
			}
			else if (aktiviert>1) {
				text.appendText("in "+ zu.name +" under this condition are ");
				for (int i=0; i<relevant.size(); i++) {
					text.appendText(((Signal)relevant.elementAt(i)).name + "=" + ((Signal)relevant.elementAt(i)).value +",  ");
				}
				text.appendText(aktiviert+" Transitions aktivated\n");
				error=true;
				OK++;
			}
			return error;
		}
		else {
			Signal input = (Signal)relevant.elementAt(index);

			input.value=0;
			error=durchgehen(relevant, index+1, zu, error);

			input.value=1;
			error=durchgehen(relevant, index+1, zu, error);
			return error;
		}
	}


	/** verarbeitet Cancel/OK Button */
	public boolean action(Event evt, Object arg) {
		if (evt.target==cancel) {
			if (tester!=null) tester.stop();
			this.dispose();
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
