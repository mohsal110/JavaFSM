package com.company;
/** 
* <IMG SRC="../doku/images/logo_klein.gif" ALT="JavaFSM"><BR>
* Exception, that shows, that the logic 
* expression is inkorrekt 
*/
class BadExpressionException extends Exception {
	/** contains bad expression */
	public String badExpression;
	/** evtl name of the input */
	public String input;

	/** Konstruktor without Parameter */
	public BadExpressionException(){}
	/** Konstruktor 
	* @param BadExpression enth&auml;lt fehlerhaften Ausdruck
	*/
	public BadExpressionException(String BadExpression){			
		// Fehlermeldung
		super("Bad expression: \""+ BadExpression +"\"");			
		badExpression = BadExpression;
		input = "";
	}
	/** Konstruktor 
	* @param BadExpression enth&auml;t fehlerhaften Ausdruck
	* @param Input Name des nicht gefundenen Inputs
	*/
	public BadExpressionException(String BadExpression, String Input){
		// Fehlermeldung mit falschem Input
		super("Bad input: \""+ Input +"\" in expression: \""+ BadExpression +"\"");	
		badExpression = BadExpression;
		input = Input;
	}
}
