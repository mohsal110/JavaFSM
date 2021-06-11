package com.company;

import com.stevesoft.pat.*;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;

import java.awt.*;
import java.io.*;


/**
* <IMG SRC="../doku/images/logo_klein.gif" ALT="JavaFSM"><BR>
* Dialog to exportieren to Verilog / VHDL / KISS / SystemC
*/
class ExportFSM extends Dialog implements Runnable
{
	private Button ok;
	private Button save;
	private TextArea text;
	private MainFrame parent;
	public JavaFSM javafsm;
	private FSM fsm;
	private Parser parser = new Parser();
	private Thread export;
	private String s;
	public  Statuszeile 	status;
//	private Regex r1 = Regex.perlCode("s/\n/\r\n/");

	
/**
* Konstruktor, der Konvertierungs-Thread startet
* @param p aufrufendes Fenster (MainFrame)
* @param Fsm Automat, der exportiert werden soll (FSM)
* @param str "VHDL" oder "KISS" (String)
*/
	public ExportFSM(MainFrame p,FSM Fsm, String str){
		super(p,"JavaFSM Export Module: "+str,true);
		fsm		= Fsm;
		parent	= p;
		s		= str;

		GridBagLayout gbl = new GridBagLayout();
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

		ok = new Button("Cancel");
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
		gbl.setConstraints(ok,gbc);
		this.add(ok);

		save = new Button("Save");
		gbc.gridx		= 1;
		gbc.gridy		= 0;
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
		gbl.setConstraints(save,gbc);
		this.add(save);


		export = new Thread(this);
		export.start();
	}

/** run-Methode des Konvertierungs-Threads */
	public void run()
	{
		if (s.toUpperCase().equals("VERILOG")) 
			generateVerilog();
		else if (s.toUpperCase().equals("SYSTEMC")) 
			generateSystemC();
		else if (s.toUpperCase().equals("VHDL")) 
			generateVHDL();
		else if (s.toUpperCase().equals("KISS")) 
			generateKISS();
		else text.setText("intern error !");								// sollte eigentlich nie auftreten, aber wer weiss ...
	}

/** verarbeitet Cancel/OK Button */
	public boolean action(Event evt, Object arg) {
		if (evt.target==ok) {
			if (export!=null) export.stop();
			this.dispose();
			return true;
		}
		if (evt.target==save) {
			/*
			if (export!=null) export.stop();
			this.dispose();
			return true;
			*/
				FileDialog fileDialog = new FileDialog(parent,"Save",FileDialog.SAVE);
				fileDialog.setFilenameFilter(parent);
				fileDialog.resize(300,200);
				fileDialog.show();
				fileDialog.resize(300,200);
				
				if (fileDialog.getFile()!=null)
				{
					try
					{
						FileOutputStream fos = new FileOutputStream(fileDialog.getDirectory()+fileDialog.getFile());
						DataOutputStream dos = new DataOutputStream(fos);
						//dos.writeBytes(r1.replaceAll(text.getText()));
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
	     return true;
}	
	
/** verarbeitet WINDOW_DESTROY */
	public boolean handleEvent(Event evt) {
		if (evt.id == Event.WINDOW_DESTROY) {
			if (export!=null) export.stop();
			this.dispose();
			return true;
		}
		else return super.handleEvent(evt);
	}

	private void generateVerilog() {
		Signal sig;
		Zustand zu;
		Transition tr;
		String tmp;
		String typ = "MEALY";
		if (!fsm.getMachineType()) typ="MOORE";

		text.setText("// FSM -> Verilog Generated file\n");
		text.appendText("// Note: This file is generated by a modified version of javaFSM\n");
		text.appendText("// module" + fsm.name + "\n");
		text.appendText("module " + typ + " (");

                // List inputs and outputs in the line of module(...)
		for (int i=0; i<fsm.inputs.size(); i++)
			text.appendText(((Signal)fsm.inputs.elementAt(i)).name + ", ");
		text.appendText("CLOCK, ");

		for (int i=0; i<fsm.outputs.size()-1; i++)
			text.appendText(((Signal)fsm.outputs.elementAt(i)).name + ", ");
		text.appendText(((Signal)fsm.outputs.elementAt(fsm.outputs.size()-1)).name + "); ");			
		//Start list inputs 
		text.appendText("\n\n");
		for (int i=0; i<fsm.inputs.size(); i++)
			text.appendText("  input " + ((Signal)fsm.inputs.elementAt(i)).name + ";\n");
		text.appendText("  input CLOCK;\n");
		//Start list inputs 
		for (int i=0; i<fsm.outputs.size(); i++)
		{
			text.appendText("  output " + ((Signal)fsm.outputs.elementAt(i)).name + ";\n");
			text.appendText("  reg " + ((Signal)fsm.outputs.elementAt(i)).name + ";\n");
		}	
		text.appendText("\n\n");
		double f1 = (double) Math.log((double)fsm.zustaende.size());
		double f2 = (double) Math.log(2.0);
		float f3 = (float)Math.ceil(f1/f2);
		int states_size = Math.round(f3);
		states_size = states_size-1;
		for (int i=0; i<fsm.zustaende.size(); i++){
			text.appendText("  parameter[" + states_size + ":0]" + " S_" + StrConv(((Zustand)fsm.zustaende.elementAt(i)).name) + " = " + i  +";\n");
		}
		text.appendText("\n");		// letztes Komma entfernen !
		text.appendText("  reg[" + states_size + ":0]" + " CURRENT_STATE;\n");
		text.appendText("  reg[" + states_size + ":0]" + " NEXT_STATE;\n");
		text.appendText("  always @(CURRENT_STATE ");
		for (int i=0; i<fsm.inputs.size(); i++)
			text.appendText(" or " + ((Signal)fsm.inputs.elementAt(i)).name);
		text.appendText(")\n");
		text.appendText("\n  begin: COMBIN\n");
		text.appendText("    case (CURRENT_STATE) \n");
		for (int i=0; i<fsm.zustaende.size(); i++) {
			zu = (Zustand)fsm.zustaende.elementAt(i);
			text.appendText("      " + " S_" + StrConv(zu.name) + ":\n");
			text.appendText("      " + " begin" + "\n");	
			for (int j=0; j<fsm.outputs.size(); j++) {
				sig = (Signal)fsm.outputs.elementAt(j);
				tmp = ((String)((Zustand)fsm.zustaende.elementAt(i)).outputHash.get(sig)).trim();
				if (tmp.equals("0") || tmp.equals("1")) {
					// direkter Ausgabewert (nur "0" oder "1")
					text.appendText("          " + sig.name + " <= 1\'b" + tmp + ";\n");
				}
				else {
					// komplexe Ausgabe-Funktion ...
                    //mbgg till here converted
					text.appendText("          if (" + VerilogFuncConv(tmp) + ")\n");
					text.appendText("            " +"begin\n");
					text.appendText("            " + sig.name + " < 1\'b1;\n");
					text.appendText("            " +"end\n");
					text.appendText("          else\n");
					text.appendText("            " +"begin\n");
					text.appendText("            " + sig.name + " <= 1\'b0;\n");
					text.appendText("            " +"end\n");
					text.appendText("          \n");
				}
			}
			Transition stern = null;
			boolean added = false;
			text.appendText("          ");
			for (int k=0; k<fsm.transitionen.size(); k++) {
				tr = (Transition)fsm.transitionen.elementAt(k);
				if (zu==tr.von) {
					if (tr.function.trim().equals("*")) stern=tr;// als else-Zweig merken
					else {
						added = true;
						text.appendText("if (" + VerilogFuncConv(tr.function) + ") \n");
						text.appendText("          begin \n");
						text.appendText("            NEXT_STATE <= " + "S_" + StrConv(tr.nach.name) + ";\n");
						text.appendText("          end \n");
						// Funktionen umwandeln und '*' irgendwie als ELSE-Zweig eintragen !!!
						//text.appendText("          else ");
						//mbggtext.appendText("          ");
					}
				}
			}
			if (stern!=null) {		// eine "*" Funktion war enthalten
				if (added) text.appendText("e\n            ");
				text.appendText("NEXT_STATE <= " + " S_" + StrConv(stern.nach.name) + ";\n");
				if (added) text.appendText("          \n");
			}
			else {					// keine "*" Funktion
				//mbgg text.setText((text.getText()).substring(0,text.getText().length()-3));				
			}
			//text.appendText("      " + " end" + "\n");	
			text.appendText("       end" + "\n");	
			
		}
		text.appendText("     endcase\n");
		text.appendText("  end\n");
                //mbgg start converted section
		text.appendText("  //Process to hold synchronous elements (flip-flops)\n");
		text.appendText("  always\n");
		text.appendText("  begin: SYNCH\n");
		text.appendText("    @(posedge CLOCK);\n");
		text.appendText("    CURRENT_STATE <= NEXT_STATE;\n");
		text.appendText("  end \n");
		text.appendText("endmodule\n");
		//mbgg end converted section
		ok.setLabel("OK");
		save.setLabel("Save");
	}

	private void generateSystemC() {
		Signal sig;
		Zustand zu;
		Transition tr;
		String tmp;
		String typ = "MEALY";
		if (!fsm.getMachineType()) typ="MOORE";

		text.setText("// FSM -> SystemC Generated file\n");
		text.appendText("// Note: This file is generated by a modified version of javaFSM\n");
		text.appendText("#include \"systemc.h\"\n");
		text.appendText("// " + fsm.name + "\n");
		text.appendText("SC_MODULE(" + typ + "){");

                // List inputs and outputs in the line of module(...)
		for (int i=0; i<fsm.inputs.size(); i++){
				//mbgg text.appendText(((Signal)fsm.inputs.elementAt(i)).name + ", ");
		}
		for (int i=0; i<fsm.outputs.size()-1; i++){
			//mbgg text.appendText(((Signal)fsm.outputs.elementAt(i)).name + ", ");
		}
		//text.appendText(((Signal)fsm.outputs.elementAt(fsm.outputs.size()-1)).name + "); ");			
		//Start list inputs 
		text.appendText("\n\n");
		for (int i=0; i<fsm.inputs.size(); i++)
			text.appendText("  sc_in<bool> " + ((Signal)fsm.inputs.elementAt(i)).name + ";\n");
		text.appendText("  sc_in_clk CLOCK;\n");
		text.appendText("  sc_in<bool> _RESET;\n");
		
		//Start list inputs 
		for (int i=0; i<fsm.outputs.size(); i++)
		{
			text.appendText("  sc_out<bool> " + ((Signal)fsm.outputs.elementAt(i)).name + ";\n");
			//mbgg text.appendText("  sc_signal<state_t> " + ((Signal)fsm.outputs.elementAt(i)).name + ";\n");
		}	
		text.appendText("\n");
		double f1 = (double) Math.log((double)fsm.zustaende.size());
		double f2 = (double) Math.log(2.0);
		float f3 = (float)Math.ceil(f1/f2);
		int states_size = Math.round(f3);
		states_size = states_size-1;
		//start enumeration stage
		text.appendText("  enum state_t { // Enumerate states\n");
		for (int i=0; i<fsm.zustaende.size()-1; i++){
			text.appendText("  " + " S_" + StrConv(((Zustand)fsm.zustaende.elementAt(i)).name) + "= " + i + ",\n");
		}
		int t_i = fsm.zustaende.size()-1;
		text.appendText("  " + " S_" + StrConv(((Zustand)fsm.zustaende.elementAt(t_i)).name) + "=" + t_i + "\n");
		text.appendText(" }\n");		// letztes Komma entfernen !
		//end enumeration stage
		text.appendText("  sc_signal<state_t>" + " CURRENT_STATE;\n");
		text.appendText("  sc_signal<state_t>" + " NEXT_STATE;\n");
		//start prototypes
		text.appendText("  void ns_logic();\n");
		text.appendText("  void output_logic();\n");
		text.appendText("  void update_state();\n");
		//end prototypes
		//start constructor
		text.appendText("  SC_CTOR("+  typ +  "){\n");
		text.appendText("     SC_METHOD(update_state);\n");
		text.appendText("     sensitive_pos << CLOCK ;\n");
		text.appendText("     SC_METHOD(ns_state);\n");
		text.appendText("     sensitive_pos << CURRENT_STATE");
		for (int i=0; i<fsm.inputs.size(); i++)
			text.appendText("  << " + ((Signal)fsm.inputs.elementAt(i)).name);
		text.appendText(";\n");
		text.appendText("     SC_METHOD(output_logic);\n");
		text.appendText("     sensitive_pos << CURRENT_STATE");
		for (int i=0; i<fsm.inputs.size(); i++)
			text.appendText("  << " + ((Signal)fsm.inputs.elementAt(i)).name);
		text.appendText(";\n");
		text.appendText("  }\n};\n");
		//end constructor		
		
		//start update_logic
		text.appendText("\n  void "+ typ + "::update_logic() {\n");
		text.appendText("     if (_RESET.read() == true)\n");
		text.appendText("     	 CURRENT_STATE =" + " S_" + StrConv(((Zustand)fsm.zustaende.elementAt(0)).name) + ";\n");
		text.appendText("     else\n");
		text.appendText("     	 CURRENT_STATE = NEXT_STATE" + ";\n");
		text.appendText("  }\n");
		// end update_logic
		//start output_logic
		text.appendText("\n  void "+ typ + "::output_logic() { // Determine ouputs\n");
		text.appendText("     if (_RESET.read() == true)\n");
		text.appendText("     	 CURRENT_STATE =" + " S_" + StrConv(((Zustand)fsm.zustaende.elementAt(0)).name) + ";\n");
		text.appendText("     else\n");
		text.appendText("     	 CURRENT_STATE = NEXT_STATE" + ";\n");
		text.appendText("  }\n");
		// end output_logic
		//start ns_logic
		text.appendText("\n  void "+ typ + "::ns_logic() { // Next Stage Logic\n");
		text.appendText("     if (_RESET.read() == true)\n");
		text.appendText("     	 CURRENT_STATE =" + " S_" + StrConv(((Zustand)fsm.zustaende.elementAt(0)).name) + ";\n");
		text.appendText("     else\n");
		text.appendText("     	 CURRENT_STATE = NEXT_STATE" + ";\n");
		text.appendText("  }\n");
		// from here 
		text.appendText("    switch(CURRENT_STATE){ \n");
		for (int i=0; i<fsm.zustaende.size(); i++) {
			zu = (Zustand)fsm.zustaende.elementAt(i);
			text.appendText("      " + "case S_" + StrConv(zu.name) + ":\n");
			for (int j=0; j<fsm.outputs.size(); j++) {
				sig = (Signal)fsm.outputs.elementAt(j);
				tmp = ((String)((Zustand)fsm.zustaende.elementAt(i)).outputHash.get(sig)).trim();
				if (tmp.equals("0") || tmp.equals("1")) {
					// direkter Ausgabewert (nur "0" oder "1")
					text.appendText("          " + sig.name + " <= 1\'b" + tmp + ";\n");
				}
				else {
					// komplexe Ausgabe-Funktion ...
                    text.appendText("          if (" + VerilogFuncConv(tmp) + "){\n");
					text.appendText("            " + "NEXT_STATE= \n");
					text.appendText("            " +"end\n");
					text.appendText("          else\n");
					text.appendText("            " +"begin\n");
					text.appendText("            " + sig.name + " <= 1\'b0;\n");
					text.appendText("            " +"end\n");
					text.appendText("          \n");
				}
			}
			Transition stern = null;
			boolean added = false;
			text.appendText("          ");
			for (int k=0; k<fsm.transitionen.size(); k++) {
				tr = (Transition)fsm.transitionen.elementAt(k);
				if (zu==tr.von) {
					if (tr.function.trim().equals("*")) stern=tr;// als else-Zweig merken
					else {
						added = true;
						text.appendText("if (" + VerilogFuncConv(tr.function) + ") \n");
						text.appendText("          begin \n");
						text.appendText("            NEXT_STATE <= " + "S_" + StrConv(tr.nach.name) + ";\n");
						text.appendText("          end \n");
						// Funktionen umwandeln und '*' irgendwie als ELSE-Zweig eintragen !!!
						//text.appendText("          else ");
						//mbggtext.appendText("          ");
					}
				}
			}
			if (stern!=null) {		// eine "*" Funktion war enthalten
				if (added) text.appendText("e\n            ");
				text.appendText("NEXT_STATE <= " + " S_" + StrConv(stern.nach.name) + ";\n");
				if (added) text.appendText("          \n");
			}
			else {					// keine "*" Funktion
				//mbgg text.setText((text.getText()).substring(0,text.getText().length()-3));				
			}
			//text.appendText("      " + " end" + "\n");	
			text.appendText("       end" + "\n");	
			
		}
		text.appendText("     endcase\n");
		text.appendText("  end\n");
                //mbgg start converted section
		text.appendText("  //Process to hold synchronous elements (flip-flops)\n");
		text.appendText("  always\n");
		text.appendText("  begin: SYNCH\n");
		text.appendText("    @(posedge CLOCK);\n");
		text.appendText("    CURRENT_STATE <= NEXT_STATE;\n");
		text.appendText("  end \n");
		text.appendText("endmodule\n");
		//mbgg end converted section
		ok.setLabel("OK");
	}

	private void generateVHDL() {
		Signal sig;
		Zustand zu;
		Transition tr;
		String tmp;
		String typ = "MEALY";
		if (!fsm.getMachineType()) typ="MOORE";

		text.setText("Library IEEE;\n");
		text.appendText("use IEEE.std_logic_1164.all;\n\n");
		text.appendText("--" + fsm.name + "\n\n");
		text.appendText("entity " + typ + " is                    -- " + typ + " machine\n");
		text.appendText("  port(");
		for (int i=0; i<fsm.inputs.size(); i++)
			text.appendText(((Signal)fsm.inputs.elementAt(i)).name + ", ");
		text.appendText("CLOCK: in STD_LOGIC;\n");
		text.appendText("       ");
		for (int i=0; i<fsm.outputs.size(); i++)
			text.appendText(((Signal)fsm.outputs.elementAt(i)).name + ", ");
		text.setText((text.getText()).substring(0,text.getText().length()-2));		// letztes Komma entfernen !
		text.appendText(": out STD_LOGIC);\n");
		text.appendText("end;\n\n");
		text.appendText("architecture BEHAVIOR of " + typ + " is\n");
		text.appendText("  type STATE_TYPE is (");
		for (int i=0; i<fsm.zustaende.size(); i++)
			text.appendText(StrConv(((Zustand)fsm.zustaende.elementAt(i)).name) + ", ");
		text.setText((text.getText()).substring(0,text.getText().length()-2));		// letztes Komma entfernen !
		text.appendText(");\n  signal CURRENT_STATE, NEXT_STATE: STATE_TYPE;\n");
		text.appendText("begin\n\n");
		text.appendText("  -- Process to hold combinational logic\n");
		text.appendText("  COMBIN: process(CURRENT_STATE");
		for (int i=0; i<fsm.inputs.size(); i++)
			text.appendText(", " + ((Signal)fsm.inputs.elementAt(i)).name);
		text.appendText(")\n  begin\n");
		text.appendText("    case CURRENT_STATE is\n");
		for (int i=0; i<fsm.zustaende.size(); i++) {
			zu = (Zustand)fsm.zustaende.elementAt(i);
			text.appendText("      when " + StrConv(zu.name) + " =>\n");
			for (int j=0; j<fsm.outputs.size(); j++) {
				sig = (Signal)fsm.outputs.elementAt(j);
				tmp = ((String)((Zustand)fsm.zustaende.elementAt(i)).outputHash.get(sig)).trim();
				if (tmp.equals("0") || tmp.equals("1")) {
					// direkter Ausgabewert (nur "0" oder "1")
					text.appendText("          " + sig.name + " <= '" + tmp + "';\n");
				}
				else {
					// komplexe Ausgabe-Funktion ...
					text.appendText("          if (" + FuncConv(tmp) + ") then \n");
					text.appendText("            " + sig.name + " <= '1';\n");
					text.appendText("          else\n");
					text.appendText("            " + sig.name + " <= '0';\n");
					text.appendText("          end if;\n");
				}
			}
			Transition stern = null;
			boolean added = false;
			text.appendText("          ");
			for (int k=0; k<fsm.transitionen.size(); k++) {
				tr = (Transition)fsm.transitionen.elementAt(k);
				if (zu==tr.von) {
					if (tr.function.trim().equals("*")) stern=tr;// als else-Zweig merken
					else {
						added = true;
						text.appendText("if (" + FuncConv(tr.function) + ") then\n");
						text.appendText("            NEXT_STATE <= " + StrConv(tr.nach.name) + ";\n");
						// Funktionen umwandeln und '*' irgendwie als ELSE-Zweig eintragen !!!
						text.appendText("          els");
					}
				}
			}
			if (stern!=null) {		// eine "*" Funktion war enthalten
				if (added) text.appendText("e\n            ");
				text.appendText("NEXT_STATE <= " + StrConv(stern.nach.name) + ";\n");
				if (added) text.appendText("          end if;\n");
			}
			else {					// keine "*" Funktion
				text.setText((text.getText()).substring(0,text.getText().length()-3));
				text.appendText("end if;\n");
			}
		}
		text.appendText("    end case;\n");
		text.appendText("  end process;\n\n");
		text.appendText("  -- Process to hold synchronous elements (flip-flops)\n");
		text.appendText("  SYNCH: process\n");
		text.appendText("  begin\n");
		text.appendText("    wait until CLOCK'event and CLOCK = '1';\n");
		text.appendText("    CURRENT_STATE <= NEXT_STATE;\n");
		text.appendText("  end process;\n");
		text.appendText("end BEHAVIOR;\n");
		ok.setLabel("OK");
	}

	private void generateKISS() {
		Signal sig;
		Zustand zu;
		Transition tr;
		String tmp;
		boolean[] contains = new boolean[fsm.inputs.size()];

		String typ = "MEALY";
		if (fsm.mealy_moore==fsm.MOORE) typ="MOORE";
		text.setText("# Finite State Machine\n");
		text.appendText("# Automatically created by JavaFSM\n\n");
		text.appendText("# Design name\n");
		text.appendText(".design " + fsm.name + "\n");
		text.appendText("# Input port names\n");
		text.appendText(".inputnames clk reset");								// clk und reset sind vorgegeben
		for (int i=0; i<fsm.inputs.size(); i++)
			text.appendText(" "+StrConv(((Signal)fsm.inputs.elementAt(i)).name));	// restliche Eing�nge anh�ngen
		text.appendText("\n# Output port names\n");
		text.appendText(".outputnames");
		for (int i=0; i<fsm.outputs.size(); i++)
			text.appendText(" "+StrConv(((Signal)fsm.outputs.elementAt(i)).name));	// Ausg�nge anh�ngen
		text.appendText("\n# Clock signal's name and type\n");
		text.appendText(".clock clk rising_edge\n");								// Asynchroner Reset mit steigender Flanke
		text.appendText("# Reset signal's name, type and resulting state\n");
		text.appendText(".asynchronous_reset reset rising ");
		tmp = "";
		for (int i=0; i<fsm.zustaende.size(); i++) {
			zu = (Zustand)fsm.zustaende.elementAt(i);
			if (zu.isStart) tmp=StrConv(zu.name);						// Startzustand suchen
		}
		text.appendText(tmp + "\n\n");
		text.appendText("# State table body\n");
		for (int i=0; i<fsm.zustaende.size(); i++) {					// alle Zust�nde duchgehen und
			zu = (Zustand)fsm.zustaende.elementAt(i);
			// benutzte Inputs herausfinden
			for (int j=0; j<contains.length; j++) contains[j]=false;	// Liste l�schen
			for (int j=0; j<fsm.transitionen.size(); j++) {				// passende Transitionen suchen
				tr = (Transition)fsm.transitionen.elementAt(j);
				if (tr.von==zu) {										// gefunden ...
					for (int k=0; k<fsm.inputs.size(); k++) {
						if (tr.function.indexOf(((Signal)fsm.inputs.elementAt(k)).name)>=0) contains[k]=true;
					}
				}
			}
			for (int j=0; j<fsm.outputs.size(); j++) {
				tmp = (String)zu.outputHash.get(fsm.outputs.elementAt(j));			// Output-Funktion
				for (int k=0; k<fsm.inputs.size(); k++) {
					if (tmp.indexOf(((Signal)fsm.inputs.elementAt(k)).name)>=0) contains[k]=true;
				}
			}
			// benutzte Inputs in contains[] gekennzeichnet

			durchgehen(contains,0,zu);
			text.appendText("\n");
		}
		ok.setLabel("OK");
	}



	private void durchgehen(boolean contains[], int index, Zustand zu){ // relevante Inputs,index auf die Inputs , zu �berpr�fender Zustand
		if (index>=contains.length) {						// alle Inputs sind gesetzt, jetzt parsen
			// Input-Belegung erzeugen
			String tmp = "";
			for (int j=0; j<contains.length; j++) {
				if (contains[j]) {
					if (((Signal)fsm.inputs.elementAt(j)).value==0) tmp+="0";
					else tmp+="1";
				}
				else tmp+="-";
			}
			tmp += "\t"+StrConv(zu.name)+"\t";
			// Zustands�bergang berechnen -> neuer Zustand
			Transition stern = null;						// Transitionen mit * merken
			Transition aktiv = null;						// aktivierte Transition merken
			Transition t;
			for (int i=0; i<fsm.transitionen.size(); i++){					// alle Transitionen durchgehen
				t=(Transition)fsm.transitionen.elementAt(i);
				if (t.von==zu){								// falls t von zu �berpr�fenden Zustand ausgeht	
					if ( t.function.equals("*")) stern=t;	// auf stern �berpr�fen
					else{									// falls kein stern, parsen
						try {
							if(parser.parse(t.function,fsm.inputs)) {
								aktiv=t;
								break;						// gefunden; Schleife abbrechen
							}
						}
						catch (BadExpressionException e){}	// falls noch was falsch ist
					}
				}
			}
			if (aktiv != null) tmp+=StrConv(aktiv.nach.name) + "\t";
			else if (stern != null) tmp+=StrConv(stern.nach.name) + "\t";
				else tmp+="FEHLER\t";
			// Ausg�nge berechnen
			String fkt;
			for (int i=0; i<fsm.outputs.size(); i++) {
				fkt = (String)zu.outputHash.get(fsm.outputs.elementAt(i));	// Output-Funktion holen
				if (fkt.trim().equals("1")) tmp+="1";
				else if (fkt.trim().equals("0")) tmp+="0";
					else {
						try {
							if(parser.parse(fkt,fsm.inputs)) tmp+="1";
							else tmp+="0";
						}
						catch (BadExpressionException e){tmp+="?";}	// falls noch was falsch ist
					}
			}
			tmp+="\n";
			text.appendText(tmp);

		}
		else if (contains[index]) {					// benutzte Signale rekursiv setzen
			Signal input = (Signal)fsm.inputs.elementAt(index);

			input.value=0;
			durchgehen(contains, index+1, zu);

			input.value=1;
			durchgehen(contains, index+1, zu);
		}
		else durchgehen(contains, index+1, zu);
	}





	private String StrConv(String str) {
		char[] chars = str.toCharArray();
		for (int i=0; i<chars.length; i++)
			if (chars[i]==' ' || chars[i]==',' || chars[i]=='.') chars[i]='_';
		return new String(chars);
	}

	private String FuncConv(String str) {
		char ch;
		String name_value;
		String function="";
		PushbackInputStream cin = new PushbackInputStream(new StringBufferInputStream(str+'\n'));
		try{
			for (;;) {
				do{
					if ((ch=(char)cin.read())=='\n') return function;				// falls Return (\n) dann Ende
				} while(ch==' ');																// entfernt Leerzeichen
				switch(ch){																		// Zeichen auswerten
					case'|': function += " OR "; break;
					case'&': function += " AND "; break;
					case'!': function += " NOT "; break;
					case'(': function += "("; break;
					case')': function += ")"; break;
					case'0': function += "'0'"; break;
					case'1': function += "'1'"; break;
					default: 																	// Name
						if ((ch>='A' && ch<='Z')||(ch>='a' && ch<='z')||(ch=='_')||(ch=='_')){	//f�r Name g�ltige Zeichen (als erster Buchstabe keine Zahl)
							name_value=""+ch;													// in name_value das Zeichen speichern
							while((ch=(char)cin.read())!='\n' &&((ch>='A' && ch<='Z')||(ch>='a' && ch<='z')||(ch>='0'&&ch<='9')||(ch=='_')||(ch=='/'))){ // f�r Namen g�ltige Zeichen								
								name_value+=ch;												// Zeichen an name_value anh�ngen
							}
							function += name_value + "='1'";								// Vergleich dranh�ngen
							cin.unread(ch);													// letztes Zeichen (geh�rt nicht mehr zu name) zur�ckspeichern
						}
						else {
							return "FEHLER - in Switch: '"+ch+"' -> "+function;													// Fehler, kein g�ltiges Zeichen
						}
					}
			}
		}
		catch(IOException e){
			return "ERROR -  Exception";
		}
	}

    private String VerilogFuncConv(String str) {
		char ch;
		String name_value;
		String function="";
		PushbackInputStream cin = new PushbackInputStream(new StringBufferInputStream(str+'\n'));
		try{
			for (;;) {
				do{
					if ((ch=(char)cin.read())=='\n') return function;				// falls Return (\n) dann Ende
				} while(ch==' ');																// entfernt Leerzeichen
				switch(ch){																		// Zeichen auswerten
					case'|': function += " | "; break;
					case'&': function += " & "; break;
					case'!': function += " ~"; break;
					case'(': function += "("; break;
					case')': function += ")"; break;
					case'0': function += "'0'"; break;
					case'1': function += "'1'"; break;
					default: 																	// Name
						if ((ch>='A' && ch<='Z')||(ch>='a' && ch<='z')||(ch=='_')||(ch=='_')){	//f�r Name g�ltige Zeichen (als erster Buchstabe keine Zahl)
							name_value=""+ch;													// in name_value das Zeichen speichern
							while((ch=(char)cin.read())!='\n' &&((ch>='A' && ch<='Z')||(ch>='a' && ch<='z')||(ch>='0'&&ch<='9')||(ch=='_')||(ch=='/'))){ // f�r Namen g�ltige Zeichen								
								name_value+=ch;												// Zeichen an name_value anh�ngen
							}
							function += name_value + "==1\'b1";								// Vergleich dranh�ngen
							cin.unread(ch);													// letztes Zeichen (geh�rt nicht mehr zu name) zur�ckspeichern
						}
						else {
							return "FEHLER - in Switch: '"+ch+"' -> "+function;													// Fehler, kein g�ltiges Zeichen
						}
					}
			}
		}
		catch(IOException e){
			return "ERROR -  Exception";
		}
		
	
	}






}

