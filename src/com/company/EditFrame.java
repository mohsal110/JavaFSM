package com.company;
import java.awt.*;
import java.awt.List;
import java.util.*;


/**
* <IMG SRC="../doku/images/logo_klein.gif" ALT="JavaFSM"><BR>
* Editor panel with draw-sektion, Buttons, Statusline, etc.
*/
class EditFrame extends Frame {
	private  FSM fsm;
	private boolean mealy_machine;/** benutzte Instanz der Zeichenfl&auml;che */
	public  EditCanvas editCanvas;
	private Statuszeile status;	
	private Panel buttonPanel;	
	private Panel button2Panel;
	private Panel dataPanel, nonePanel, zustandMoorePanel;	
	private Panel zustandMealyPanel, transitionPanel, kommentarPanel;	
	private Scrollbar HBar,VBar;		
	private Button buttonMove;		
	private Button buttonZustand;
	private Button buttonTransition;
	private Button buttonKommentar;
	private Button buttonDelete;
	private Button buttonStart;
/*	private Button buttonPrint;*/
	private Button buttonTest;
	private Button kommentarOK;
	private Label noneLabel, zustMoLabel, zustMeLabel, transLabel, kommentarLabel; 
	private TextField zustMoField, zustMeField, outField, transField;	
	private TextArea kommentarArea;			
	private List signalListMo, signalListMe;
	private CheckboxGroup chkBoxGrp;			
	private Checkbox lowChkBox,highChkBox;		
	private Font plainFont,boldFont;		
	private CardLayout card;			

	/** Breite der Zeichenfl&auml;che */
	public final static int VirtWidth = 1000;	
	/** H&ouml;he der Zeichenfl&auml;che */
	public final static int VirtHeight = 1000;

	/** 
	* Konstruktor
	* @param title Fenstertitel (String)
	* @param Fsm endlicher Automat, der editiert werden soll (FSM)
	*/
	public EditFrame(String title, FSM Fsm) {
		super(title);
		this.setBackground(Color.lightGray);
		mealy_machine = true;
	/* Parameter sichern */
		fsm = Fsm;
	/* Fonts f�r Modus-Buttons */
		plainFont = new Font("Helvetica",Font.PLAIN,12);
		boldFont = new Font("Helvetica",Font.BOLD,13);

	/* LayoutManager setzen */
		card = new CardLayout();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints gridcon = new GridBagConstraints();
		Insets ins = new Insets(0,0,0,0);
		this.setLayout(gridbag);

	/* das Panel f�r die Modus-Buttons */
		buttonPanel = new Panel3D(false);
		buttonPanel.setLayout(gridbag);
		gridcon.anchor		= GridBagConstraints.CENTER;
		gridcon.fill		= GridBagConstraints.HORIZONTAL;
		gridcon.weightx		= 0.0;
		gridcon.weighty		= 0.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 0;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 0;
		ins.left			= 5;
		ins.right			= 0;
		gridcon.insets		= ins;
		gridbag.setConstraints(buttonPanel,gridcon);

		buttonMove = new Button("Move");
		buttonMove.setFont(boldFont);
		gridcon.anchor		= GridBagConstraints.CENTER;
		gridcon.fill		= GridBagConstraints.BOTH;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 0.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 0;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 0;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(buttonMove,gridcon);

		buttonZustand = new Button("State");
		buttonZustand.setFont(plainFont);
		gridcon.anchor		= GridBagConstraints.CENTER;
		gridcon.fill		= GridBagConstraints.BOTH;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 0.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 1;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 0;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(buttonZustand,gridcon);

		buttonTransition = new Button("Transition");
		buttonTransition.setFont(plainFont);
		gridcon.anchor		= GridBagConstraints.CENTER;
		gridcon.fill		= GridBagConstraints.BOTH;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 0.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 2;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 0;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(buttonTransition,gridcon);

		buttonKommentar = new Button("Comment");
		buttonKommentar.setFont(plainFont);
		gridcon.anchor		= GridBagConstraints.CENTER;
		gridcon.fill		= GridBagConstraints.BOTH;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 0.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 3;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 0;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(buttonKommentar,gridcon);

		buttonDelete = new Button("Delete");
		buttonDelete.setFont(plainFont);
		gridcon.anchor		= GridBagConstraints.CENTER;
		gridcon.fill		= GridBagConstraints.BOTH;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 0.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 4;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 0;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(buttonDelete,gridcon);

		buttonStart = new Button("Start-State");
		buttonStart.setFont(plainFont);
		gridcon.anchor		= GridBagConstraints.CENTER;
		gridcon.fill		= GridBagConstraints.BOTH;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 0.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 5;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 5;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(buttonStart,gridcon);

/*		buttonPrint = new Button("Print");
		buttonPrint.setFont(plainFont);
		gridcon.anchor		= GridBagConstraints.CENTER;
		gridcon.fill		= GridBagConstraints.BOTH;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 0.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 6;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 5;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(buttonPrint,gridcon);	*/

	/* das Panel, das nur den "Test" Button enth�lt */
		button2Panel = new Panel3D(false);
		button2Panel.setLayout(gridbag);
		gridcon.anchor		= GridBagConstraints.CENTER;
		gridcon.fill		= GridBagConstraints.HORIZONTAL;
		gridcon.weightx		= 0.0;
		gridcon.weighty		= 0.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 1;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 0;
		ins.left			= 5;
		ins.right			= 0;
		gridcon.insets		= ins;
		gridbag.setConstraints(button2Panel,gridcon);

		buttonTest = new Button("FSM-test");
		buttonTest.setFont(plainFont);
		gridcon.anchor		= GridBagConstraints.CENTER;
		gridcon.fill		= GridBagConstraints.BOTH;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 0.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 0;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 5;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(buttonTest,gridcon);

	/* das Basis-CardLayout-Panel f�r die Details (none,Zustand,Transition) */
		dataPanel = new Panel();
		dataPanel.setLayout(card);
		gridcon.anchor		= GridBagConstraints.NORTH;
		gridcon.fill		= GridBagConstraints.BOTH;
		gridcon.weightx		= 0.0;
		gridcon.weighty		= 1.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 2;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 2;
		ins.left			= 5;
		ins.right			= 0;
		gridcon.insets		= ins;
		gridbag.setConstraints(dataPanel,gridcon);

	/* das CardLayout-Panel wenn nichts selektiert ist */
		nonePanel = new Panel3D(false);
		nonePanel.setLayout(gridbag);
		gridcon.anchor		= GridBagConstraints.NORTH;
		gridcon.fill		= GridBagConstraints.BOTH;
		gridcon.weightx		= 0.0;
		gridcon.weighty		= 1.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 0;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 2;
		ins.bottom			= 2;
		ins.left			= 2;
		ins.right			= 2;
		gridcon.insets		= ins;
		gridbag.setConstraints(nonePanel,gridcon);

		noneLabel = new Label("nothing selected",Label.CENTER);
		noneLabel.setFont(boldFont);
		gridcon.anchor		= GridBagConstraints.NORTH;
		gridcon.fill		= GridBagConstraints.HORIZONTAL;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 1.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 0;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 0;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(noneLabel,gridcon);

	/* das CardLayout-Panel wenn ein Zustand selektiert ist */
		zustandMealyPanel = new Panel3D(false);
		zustandMealyPanel.setLayout(gridbag);
		gridcon.anchor		= GridBagConstraints.NORTH;
		gridcon.fill		= GridBagConstraints.BOTH;
		gridcon.weightx		= 0.0;
		gridcon.weighty		= 1.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 0;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 2;
		ins.bottom			= 2;
		ins.left			= 2;
		ins.right			= 2;
		gridcon.insets		= ins;
		gridbag.setConstraints(zustandMealyPanel,gridcon);

		zustMeLabel = new Label("State",Label.CENTER);
		zustMeLabel.setFont(boldFont);
		gridcon.anchor		= GridBagConstraints.CENTER;
		gridcon.fill		= GridBagConstraints.HORIZONTAL;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 0.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 0;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 0;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(zustMeLabel,gridcon);

		zustMeField = new TextField(12);
		zustMeField.setFont(plainFont);
		gridcon.anchor		= GridBagConstraints.CENTER;
		gridcon.fill		= GridBagConstraints.HORIZONTAL;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 0.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 1;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 0;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(zustMeField,gridcon);

		signalListMe = new List(20,false);
		signalListMe.setFont(plainFont);
		gridcon.anchor		= GridBagConstraints.CENTER;
		gridcon.fill		= GridBagConstraints.BOTH;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 1.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 2;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 0;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(signalListMe,gridcon);

		outField = new TextField(12);
		outField.setFont(plainFont);
		gridcon.anchor		= GridBagConstraints.NORTH;
		gridcon.fill		= GridBagConstraints.HORIZONTAL;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 0.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 3;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 5;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(outField,gridcon);

	/* das gleiche Panel nochmal, aber anstatt eines TextFields (ganz unten) zwei radio-boxes */
		zustandMoorePanel = new Panel3D(false);
		zustandMoorePanel.setLayout(gridbag);
		gridcon.anchor		= GridBagConstraints.NORTH;
		gridcon.fill		= GridBagConstraints.BOTH;
		gridcon.weightx		= 0.0;
		gridcon.weighty		= 1.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 0;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 2;
		ins.bottom			= 2;
		ins.left			= 2;
		ins.right			= 2;
		gridcon.insets		= ins;
		gridbag.setConstraints(zustandMoorePanel,gridcon);

		zustMoLabel = new Label("State",Label.CENTER);
		zustMoLabel.setFont(boldFont);
		gridcon.anchor		= GridBagConstraints.CENTER;
		gridcon.fill		= GridBagConstraints.HORIZONTAL;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 0.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 0;
		gridcon.gridwidth	= 2;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 0;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(zustMoLabel,gridcon);

		zustMoField = new TextField(12);
		zustMoField.setFont(plainFont);
		gridcon.anchor		= GridBagConstraints.CENTER;
		gridcon.fill		= GridBagConstraints.HORIZONTAL;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 0.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 1;
		gridcon.gridwidth	= 2;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 0;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(zustMoField,gridcon);

		signalListMo = new List(20,false);
		signalListMo.setFont(plainFont);
		gridcon.anchor		= GridBagConstraints.CENTER;
		gridcon.fill		= GridBagConstraints.BOTH;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 1.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 2;
		gridcon.gridwidth	= 2;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 0;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(signalListMo,gridcon);

		chkBoxGrp = new CheckboxGroup();
		lowChkBox = new Checkbox("0",chkBoxGrp,true);
		lowChkBox.setFont(plainFont);
		gridcon.anchor		= GridBagConstraints.NORTH;
		gridcon.fill		= GridBagConstraints.HORIZONTAL;
		gridcon.weightx		= 0.5;
		gridcon.weighty		= 0.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 3;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 5;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(lowChkBox,gridcon);

		highChkBox = new Checkbox("1",chkBoxGrp,false);
		highChkBox.setFont(plainFont);
		gridcon.anchor		= GridBagConstraints.NORTH;
		gridcon.fill		= GridBagConstraints.HORIZONTAL;
		gridcon.weightx		= 0.5;
		gridcon.weighty		= 0.0;
		gridcon.gridx		= 1;
		gridcon.gridy		= 3;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 5;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(highChkBox,gridcon);

	/* das CardLayout-Panel wenn eine Transition selektiert ist */
		transitionPanel = new Panel3D(false);
		transitionPanel.setLayout(gridbag);
		gridcon.anchor		= GridBagConstraints.NORTH;
		gridcon.fill		= GridBagConstraints.BOTH;
		gridcon.weightx		= 0.0;
		gridcon.weighty		= 1.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 0;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 2;
		ins.bottom			= 2;
		ins.left			= 2;
		ins.right			= 2;
		gridcon.insets		= ins;
		gridbag.setConstraints(transitionPanel,gridcon);

		transLabel = new Label("Transition",Label.CENTER);
		transLabel.setFont(boldFont);
		gridcon.anchor		= GridBagConstraints.CENTER;
		gridcon.fill		= GridBagConstraints.HORIZONTAL;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 0.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 0;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 0;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(transLabel,gridcon);

		transField = new TextField(12);
		transField.setFont(plainFont);
		gridcon.anchor		= GridBagConstraints.NORTH;
		gridcon.fill		= GridBagConstraints.HORIZONTAL;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 1.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 1;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 5;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(transField,gridcon);

	/* das CardLayout-Panel wenn ein Kommentar selektiert ist */
		kommentarPanel = new Panel3D(false);
		kommentarPanel.setLayout(gridbag);
		gridcon.anchor		= GridBagConstraints.NORTH;
		gridcon.fill		= GridBagConstraints.BOTH;
		gridcon.weightx		= 0.0;
		gridcon.weighty		= 1.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 0;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 2;
		ins.bottom			= 2;
		ins.left			= 2;
		ins.right			= 2;
		gridcon.insets		= ins;
		gridbag.setConstraints(kommentarPanel,gridcon);

		kommentarLabel = new Label("Comment",Label.CENTER);
		kommentarLabel.setFont(boldFont);
		gridcon.anchor		= GridBagConstraints.CENTER;
		gridcon.fill		= GridBagConstraints.HORIZONTAL;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 0.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 0;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 0;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(kommentarLabel,gridcon);

		kommentarArea = new TextArea(5,5);
		kommentarArea.setFont(plainFont);
		gridcon.anchor		= GridBagConstraints.NORTH;
		gridcon.fill		= GridBagConstraints.BOTH;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 1.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 1;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 0;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(kommentarArea,gridcon);

		kommentarOK = new Button("Assume");
		kommentarOK.setFont(plainFont);
		gridcon.anchor		= GridBagConstraints.NORTH;
		gridcon.fill		= GridBagConstraints.HORIZONTAL;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 0.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 2;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 5;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(kommentarOK,gridcon);

	/* die Statuszeile ganz unten im Fenster */
		status = new Statuszeile("Move states with mouse");
		gridcon.anchor		= GridBagConstraints.CENTER;
		gridcon.fill		= GridBagConstraints.HORIZONTAL;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 0.0;
		gridcon.gridx		= 0;
		gridcon.gridy		= 4;
		gridcon.gridwidth	= 3;
		gridcon.gridheight	= 1;
		ins.top				= 5;
		ins.bottom			= 5;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(status,gridcon);

	/* die Zeichenfl�che f�r die FSM */
		editCanvas = new EditCanvas(fsm,this,status);
		gridcon.anchor		= GridBagConstraints.CENTER;
		gridcon.fill		= GridBagConstraints.BOTH;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 1.0;
		gridcon.gridx		= 1;
		gridcon.gridy		= 0;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 3;
		ins.top				= 5;
		ins.bottom			= 2;
		ins.left			= 5;
		ins.right			= 2;
		gridcon.insets		= ins;
		gridbag.setConstraints(editCanvas,gridcon);

	/* die Scrollbars neben der Zeichenfl�che */
		HBar = new Scrollbar(Scrollbar.HORIZONTAL);
		gridcon.anchor		= GridBagConstraints.CENTER;
		gridcon.fill		= GridBagConstraints.HORIZONTAL;
		gridcon.weightx		= 1.0;
		gridcon.weighty		= 0.0;
		gridcon.gridx		= 1;
		gridcon.gridy		= 3;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 1;
		ins.top				= 0;
		ins.bottom			= 5;
		ins.left			= 5;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(HBar,gridcon);

		VBar = new Scrollbar(Scrollbar.VERTICAL);
		gridcon.anchor		= GridBagConstraints.CENTER;
		gridcon.fill		= GridBagConstraints.VERTICAL;
		gridcon.weightx		= 0.0;
		gridcon.weighty		= 1.0;
		gridcon.gridx		= 2;
		gridcon.gridy		= 0;
		gridcon.gridwidth	= 1;
		gridcon.gridheight	= 3;
		ins.top				= 5;
		ins.bottom			= 5;
		ins.left			= 0;
		ins.right			= 5;
		gridcon.insets		= ins;
		gridbag.setConstraints(VBar,gridcon);

	/* jetzt noch alles zusammenbauen */
		buttonPanel.add(buttonMove);
		buttonPanel.add(buttonZustand);
		buttonPanel.add(buttonTransition);
		buttonPanel.add(buttonKommentar);
		buttonPanel.add(buttonDelete);
		buttonPanel.add(buttonStart);

		button2Panel.add(buttonTest);
		nonePanel.add(noneLabel);
		zustandMealyPanel.add(zustMeLabel);
		zustandMealyPanel.add(zustMeField);
		zustandMealyPanel.add(signalListMe);
		zustandMealyPanel.add(outField);
		zustandMoorePanel.add(zustMoLabel);
		zustandMoorePanel.add(zustMoField);
		zustandMoorePanel.add(signalListMo);
		zustandMoorePanel.add(lowChkBox);
		zustandMoorePanel.add(highChkBox);
		transitionPanel.add(transLabel);
		transitionPanel.add(transField);
		kommentarPanel.add(kommentarLabel);
		kommentarPanel.add(kommentarArea);
		kommentarPanel.add(kommentarOK);
		dataPanel.add("None",nonePanel);
		dataPanel.add("ZustandMealy",zustandMealyPanel);
		dataPanel.add("ZustandMoore",zustandMoorePanel);
		dataPanel.add("Transition",transitionPanel);
		dataPanel.add("Comment",kommentarPanel);
		this.add(buttonPanel);
		this.add(button2Panel);
		this.add(dataPanel);
		this.add(editCanvas);
		this.add(HBar);
		this.add(VBar);
		this.add(status);
		setPanel(null);		
		setBars();			
	}



	/** verarbeitet Events der Buttons, TextFields, etc. */
	public boolean action(Event event, Object arg) {
		if(event.target==buttonMove){		
			editCanvas.moveMode();
			buttonMove.setFont(boldFont);			
			buttonZustand.setFont(plainFont);
			buttonTransition.setFont(plainFont);
			buttonKommentar.setFont(plainFont);
			buttonDelete.setFont(plainFont);
			buttonStart.setFont(plainFont);
			return true;
		}
		else if(event.target==buttonZustand){		
			editCanvas.zustandMode();
			buttonMove.setFont(plainFont);
			buttonZustand.setFont(boldFont);		
			buttonTransition.setFont(plainFont);
			buttonKommentar.setFont(plainFont);
			buttonDelete.setFont(plainFont);
			buttonStart.setFont(plainFont);
			return true;
		}
		else if(event.target==buttonTransition){	
			editCanvas.transitionMode();
			buttonMove.setFont(plainFont);
			buttonZustand.setFont(plainFont);
			buttonTransition.setFont(boldFont);		
			buttonKommentar.setFont(plainFont);
			buttonDelete.setFont(plainFont);
			buttonStart.setFont(plainFont);
			return true;
		}
		else if(event.target==buttonKommentar){		
			editCanvas.commentMode();
			buttonMove.setFont(plainFont);
			buttonZustand.setFont(plainFont);
			buttonTransition.setFont(plainFont);
			buttonKommentar.setFont(boldFont);		
			buttonDelete.setFont(plainFont);
			buttonStart.setFont(plainFont);
			return true;
		}
		else if(event.target==buttonDelete){		
			editCanvas.deleteMode();
			buttonMove.setFont(plainFont);
			buttonZustand.setFont(plainFont);
			buttonTransition.setFont(plainFont);
			buttonKommentar.setFont(plainFont);
			buttonDelete.setFont(boldFont);			
			buttonStart.setFont(plainFont);
			return true;
		}
		else if(event.target==buttonStart){			
			editCanvas.startMode();
			buttonMove.setFont(plainFont);
			buttonZustand.setFont(plainFont);
			buttonTransition.setFont(plainFont);
			buttonKommentar.setFont(plainFont);
			buttonDelete.setFont(plainFont);
			buttonStart.setFont(boldFont);			
			return true;
		}
/*		else if(event.target==buttonPrint){
			PrintJob printjob = getToolkit().getPrintJob(this,"JavaFSM PrintJob",(Properties)null);
			if (printjob!=null) {
				Graphics g = printjob.getGraphics();
				Dimension dim = printjob.getPageDimension();
				editCanvas.drawFSM(0,0,dim.width,dim.height,g,null);
				printjob.end();
			}
			return true;
		} */
		else if(event.target==buttonTest){			
			FSMtester fsmtester = new FSMtester(this, fsm);
			fsmtester.resize(400,300);
			fsmtester.show();
			fsmtester.resize(400,300);
			return true;
		}
		else if(event.target==kommentarOK){			
			((Kommentar)editCanvas.getSelected()).setText(kommentarArea.getText());
			editCanvas.repaint();
			return true;
		}
		else if((event.target==zustMeField)&&(editCanvas.getSelected() instanceof Zustand)) {
			String name = zustMeField.getText();
			if (!name.trim().equals("") && (name.indexOf(',')<0)) {
				boolean found = false;
				for (int i=0; i<fsm.zustaende.size(); i++)
					if (((Zustand)fsm.zustaende.elementAt(i)).name.equals(name)) found=true;
				if (!found) 
				{
					((Zustand)editCanvas.getSelected()).name = name;						
					editCanvas.repaint();
					status.set("");
				}
				else status.set("Name existing!");
			}
			else status.set("Name contains errors!");
			return true;
		}
		else if((event.target==zustMoField)&&(editCanvas.getSelected() instanceof Zustand)) {	
			String name = zustMoField.getText();
			if (!name.trim().equals("") && (name.indexOf(',')<0)) {
				boolean found = false;
				for (int i=0; i<fsm.zustaende.size(); i++)
					if (((Zustand)fsm.zustaende.elementAt(i)).name.equals(name)) found=true;
				if (!found) 
				{
					((Zustand)editCanvas.getSelected()).name = name;						
					editCanvas.repaint();
					status.set("");
				}
				else status.set("Name existing!");
			}
			else status.set("Name contains errors!");
			return true;
		}
		else if((event.target==outField)&&(editCanvas.getSelected() instanceof Zustand)) {	
			int i = signalListMe.getSelectedIndex();														
			if (i >= 0) {
				((Zustand)editCanvas.getSelected()).outputHash.put(fsm.outputs.elementAt(i),outField.getText());	
				signalListMe.replaceItem(((Signal)fsm.outputs.elementAt(i)).name+"  -> "+outField.getText(),i);	
				signalListMe.select(i);														
			}
			return true;
		}
		else if((event.target==lowChkBox)&&(editCanvas.getSelected() instanceof Zustand)) {	
			int i = signalListMo.getSelectedIndex();														
			if (i >= 0) {
				((Zustand)editCanvas.getSelected()).outputHash.put(fsm.outputs.elementAt(i),"0");		
				signalListMo.replaceItem(((Signal)fsm.outputs.elementAt(i)).name+"  -> "+"0",i);		
				signalListMo.select(i);																			
				signalListMo.requestFocus();
			}
			return true;
		}
		else if((event.target==highChkBox)&&(editCanvas.getSelected() instanceof Zustand)) {	
			int i = signalListMo.getSelectedIndex();														
			if (i >= 0) {
				((Zustand)editCanvas.getSelected()).outputHash.put(fsm.outputs.elementAt(i),"1");		
				signalListMo.replaceItem(((Signal)fsm.outputs.elementAt(i)).name+"  -> "+"1",i);		
				signalListMo.select(i);																		
				signalListMo.requestFocus();
			}
			return true;
		}
		else if((event.target==transField)&&(editCanvas.getSelected() instanceof Transition)) {	
			((Transition)editCanvas.getSelected()).function = transField.getText();				
			editCanvas.repaint();																	
			return true;
		}
		else return super.action(event,arg);
	}



	/** verabeitet Events der Scrollbars und der List-Boxen*/
	public boolean handleEvent(Event evt) {
		if (evt.id==Event.WINDOW_DESTROY) {						
			this.hide();
			return true;
		}
		else if ((evt.id==Event.SCROLL_LINE_UP)||				
				 (evt.id==Event.SCROLL_LINE_DOWN)||
				 (evt.id==Event.SCROLL_PAGE_UP)||
				 (evt.id==Event.SCROLL_PAGE_DOWN)||
				 (evt.id==Event.SCROLL_ABSOLUTE)) {
			setBars();													
			editCanvas.repaint();									
			return true;
		}
		else if((evt.id==Event.LIST_SELECT)&&(evt.target==signalListMe)&&(editCanvas.getSelected() instanceof Zustand)){	
			if (signalListMe.getSelectedIndex() >= 0) {
				outField.setText((String)(((Zustand)editCanvas.getSelected()).outputHash.get(fsm.outputs.elementAt(signalListMe.getSelectedIndex()))));	
				outField.requestFocus();							
			}
			else outField.setText("");								
			return true;
		}
		else if((evt.id==Event.LIST_SELECT)&&(evt.target==signalListMo)&&(editCanvas.getSelected() instanceof Zustand)){	
			if (signalListMo.getSelectedIndex() >= 0) {
				if ("1".equals((String)(((Zustand)editCanvas.getSelected()).outputHash.get(fsm.outputs.elementAt(signalListMo.getSelectedIndex())))))	
					chkBoxGrp.setCurrent(highChkBox);
				else
					chkBoxGrp.setCurrent(lowChkBox);
			}
			return true;
		}
		else return super.handleEvent(evt);
	}



	private void setBars() {											
		Dimension d = editCanvas.size();
		HBar.setValues(HBar.getValue(),d.width,0,VirtWidth-d.width);
		VBar.setValues(VBar.getValue(),d.height,0,VirtHeight-d.height);
		editCanvas.xOffset = HBar.getValue();
		editCanvas.yOffset = VBar.getValue();
		HBar.setLineIncrement(50);									
		VBar.setLineIncrement(50);
		HBar.setPageIncrement(100);
		VBar.setPageIncrement(100);
	}



	/**
	* aktualisiert die Eigenschaften-Anzeige
	* @param selected Objekt (Zustand, Transition oder null), dessen Eigenschaften angezeigt werden sollen (Object)
	*/
	public void setPanel(Object selected) {						
		if (selected instanceof Zustand) {
			if (mealy_machine) {
				card.show(dataPanel,"ZustandMealy");			
				zustMeField.setText(((Zustand)selected).name);
/*				zustMeField.requestFocus();			*/			
				signalListMe.clear();							
				for (int i=0; i<fsm.outputs.size(); i++)		
					signalListMe.addItem(((Signal)fsm.outputs.elementAt(i)).name+"  -> "+((Zustand)selected).outputHash.get(fsm.outputs.elementAt(i)));

				if (signalListMe.getSelectedIndex() >= 0)		
					outField.setText((String)(((Zustand)selected).outputHash.get(fsm.outputs.elementAt(signalListMe.getSelectedIndex()))));
				else outField.setText("");
			}
			else {
				card.show(dataPanel,"ZustandMoore");			
				zustMoField.setText(((Zustand)selected).name);
/*				zustMoField.requestFocus();			*/			
				signalListMo.clear();							
				for (int i=0; i<fsm.outputs.size(); i++)		
					signalListMo.addItem(((Signal)fsm.outputs.elementAt(i)).name+"  -> "+((Zustand)selected).outputHash.get(fsm.outputs.elementAt(i)));
			}
		}
		else if (selected instanceof Transition) {
			card.show(dataPanel,"Transition");					
			transField.setText(((Transition)selected).function);
			transField.requestFocus();							
		}
		else if (selected instanceof Kommentar) {
			card.show(dataPanel,"Comment");					
			kommentarArea.setText(((Kommentar)selected).getText());
/*			kommentarArea.requestFocus();	*/					
		}
		else card.show(dataPanel,"None");						
	}




	/**
	* setzt den Automaten-Typ
	* @param s String, der entweder "MOORE" oder "MEALY" enth&auml;lt
	*/
	public void setMachineType(String s) {
		if (s.toUpperCase().equals("MOORE")) 		mealy_machine = false;
		else if (s.toUpperCase().equals("MEALY"))	mealy_machine = true;
		setPanel((Zustand)editCanvas.getSelected());
		editCanvas.repaint();
	}

}
