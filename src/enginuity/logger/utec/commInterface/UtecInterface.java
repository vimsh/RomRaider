/*
 * Created on May 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package enginuity.logger.utec.commInterface;


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
//import javax.comm.CommPortIdentifier
import gnu.io.*;

import javax.swing.*;

import enginuity.logger.utec.gui.realtimeData.*;
import enginuity.logger.utec.mapData.GetMapFromUtecListener;
import enginuity.logger.utec.mapData.UtecMapData;
import enginuity.logger.utec.properties.UtecProperties;
import enginuity.logger.utec.comm.*;
import enginuity.logger.utec.commEvent.LoggerEvent;
import enginuity.logger.utec.commEvent.LoggerListener;
import enginuity.logger.utec.commEvent.UtecTimerTaskManager;
/**
 * @author emorgan
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class UtecInterface{
	//Store string vector of known system comm ports
	private static Vector portChoices =  listPortChoices();
	private static UtecSerialListener serialEventListener = new UtecSerialListener();
	private static Vector<LoggerListener> loggerListeners = new Vector<LoggerListener>();

	/**
	 * Initial setup of the class
	 *
	 */
	public static void init(){
		UtecSerialConnection.init(serialEventListener);
	}

	/**
	 * Method sends string data to the utec
	 * @param mapData
	 */
	public static void sendDataToUtec(StringBuffer commandData){
		UtecTimerTaskManager.execute(commandData);
	}
	
	/**
	 * Transmit a map to the utec
	 * 
	 * @param mapNumber
	 * @param listener
	 */
	public static void sendMapData(int mapNumber, StringBuffer mapData) {

		// Sanity check
		if (mapNumber < 1 || mapNumber > 5) {
			System.err.println("Map selection out of range.");
			return;
		}
		
		String[] commandList = UtecProperties.getProperties("utec.startMapDownload");
		if(commandList == null){
			System.err.println("Command string in properties file for utec.startMapUpload not found.");
			return;
		}
		
		resetUtec();
		System.out.println("UtecControl, sending map:" + mapNumber);
		
		// Iterate through command string
		int starCounter = 0;
		for(int i = 0; i < commandList.length ; i++){
			if(commandList[i].equalsIgnoreCase("*")){
				if(starCounter == 0){
					// Select map
					
					if (mapNumber == 1) {
						UtecTimerTaskManager.execute(33);
						System.out.println("Requested Map 1");
					}
					if (mapNumber == 2) {
						UtecTimerTaskManager.execute(64);
						System.out.println("Requested Map 2");
					}
					if (mapNumber == 3) {
						UtecTimerTaskManager.execute(35);
						System.out.println("Requested Map 3");
					}
					if (mapNumber == 4) {
						UtecTimerTaskManager.execute(36);
						System.out.println("Requested Map 4");
					}
					if (mapNumber == 5) {
						UtecTimerTaskManager.execute(37);
						System.out.println("Requested Map 5");
					}
				}else if(starCounter == 1){
					UtecTimerTaskManager.execute(mapData);
					
				}else{
					System.err.println("No operation supported for properties value '*'");
				}
				
				starCounter++;
			}else{
				// Send parsed command to the utec
				UtecTimerTaskManager.execute(Integer.parseInt(commandList[i]));
			}
		}
	}
	

	/**
	 * Get UTEC to send logger data data
	 * 
	 */
	public static void startLoggerDataFlow() {
		System.out.println("Starting data flow from UTEC");
		
		String[] commandList = UtecProperties.getProperties("utec.startLogging");
		if(commandList == null){
			System.err.println("Command string in properties file for utec.startLogging not found.");
			return;
		}
		
		resetUtec();
		for(int i = 0; i < commandList.length ; i++){
			// Send parsed command to the utec
			UtecTimerTaskManager.execute(Integer.parseInt(commandList[i]));
		}
	}

	/**
	 * Reset UTEC to main screen
	 * 
	 */
	public static void resetUtec() {
		System.out.println("Utec reset called.");

		String[] commandList = UtecProperties.getProperties("utec.resetUtec");
		if(commandList == null){
			System.err.println("Command string in properties file for utec.resetUtec not found.");
			return;
		}
		
		for(int i = 0; i < commandList.length ; i++){
			// Send parsed command to the utec
			UtecTimerTaskManager.execute(Integer.parseInt(commandList[i]));
		}
	}

	/**
	 * Get map data from map number passed in
	 * 
	 * @param mapNumber
	 */
	
	public static void pullMapData(int mapNumber) {
		// Sanity check
		if (mapNumber < 1 || mapNumber > 5) {
			System.err.println("Map selection out of range.");
			return;
		}
		
		String[] commandList = UtecProperties.getProperties("utec.startMapDownload");
		if(commandList == null){
			System.err.println("Command string in properties file for utec.startMapDownload not found.");
			return;
		}
		
		resetUtec();
		System.out.println("UtecControl, getting map:" + mapNumber);

		// Null out any previously loaded map
		//serialEventListener.currentMap = null;

		// Setup map transfer prep state
		//serialEventListener.isMapFromUtecPrep = true;
		//serialEventListener.isMapFromUtec = false;
		
		// Iterate through command string
		int starCounter = 0;
		for(int i = 0; i < commandList.length ; i++){
			if(commandList[i].equalsIgnoreCase("*")){
				if(starCounter == 0){
					// Select map
					
					if (mapNumber == 1) {
						UtecTimerTaskManager.execute(33);
						System.out.println("Requested Map 1");
					}
					if (mapNumber == 2) {
						UtecTimerTaskManager.execute(64);
						System.out.println("Requested Map 2");
					}
					if (mapNumber == 3) {
						UtecTimerTaskManager.execute(35);
						System.out.println("Requested Map 3");
					}
					if (mapNumber == 4) {
						UtecTimerTaskManager.execute(36);
						System.out.println("Requested Map 4");
					}
					if (mapNumber == 5) {
						UtecTimerTaskManager.execute(37);
						System.out.println("Requested Map 5");
					}
				}else if(starCounter == 1){

					// Make this class receptive to map transfer
					//serialEventListener.isMapFromUtec = true;

					// No longer map prep
					//serialEventListener.isMapFromUtecPrep = false;
					
				}else{
					System.err.println("No operation supported for properties value '*'");
				}
				
				starCounter++;
			}else{
				// Send parsed command to the utec
				UtecTimerTaskManager.execute(Integer.parseInt(commandList[i]));
			}
		}
	}

	
	/**
	 * Get a list of all the ports available
	 * @return
	 */
	public static Vector getPortsVector(){
		return portChoices;
	}
	
	/**
	 * Get name of currently used port
	 * @return
	 */
	public static String getPortChoiceUsed(){
		return UtecSerialConnection.parameters.getPortName();
	}

	
	/**
	 * Pass a single command char to the UTEC
	 * @param charValue
	 */
	public static void sendCommandToUtec(int charValue){
		UtecTimerTaskManager.execute(charValue);
	}
	
	/**
	 * PRIVATE
	 * 
	 * Open a port as per names defined in get port names method
	 * 
	 * @param portName
	 */
	 private static void openConnection(){
		 if(UtecSerialConnection.isOpen()){
			 System.out.println("Port is already open.");
			 return;
		 }
		 
	 	
	 	//Attempt to make connection
	 	try{
	 		UtecSerialConnection.openConnection();
	 	}catch(SerialConnectionException e){
	 		System.err.println("Error opening serial port connection");
	 		e.printStackTrace();
	 		return;
	 	}
	 	
	 }
	
	/**
	 * Close connection to the currently opened port
	 *
	 */
	public static void closeConnection(){
		UtecSerialConnection.closeConnection();	
	}
	
	/**
	 * Open selected port.
	 * @param port
	 */
	public static void setPortChoice(String port){
		UtecSerialConnection.closeConnection();
		UtecSerialConnection.parameters.setPortName(port);
		openConnection();
	}
	
	/**
	 * Method returns a vector of all available serial ports found
	 * 
	 * @return
	 */
	private static Vector listPortChoices() {
		Vector theChoices = new Vector();
		CommPortIdentifier portId;
		
		Enumeration en = CommPortIdentifier.getPortIdentifiers();
		if (!en.hasMoreElements()) {
			System.err.println("No Valid ports found, check Java installation");
		}
			
		//Iterate through the ports
		while (en.hasMoreElements()) {
			portId = (CommPortIdentifier) en.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				System.out.println("Port found on system: "+portId.getName());
				theChoices.addElement(portId.getName());
			}
		}
		return theChoices;
	}
	
	/**
	 * Add a listener to logger data
	 * 
	 * @param ll
	 */
	public static void addLoggerListener(LoggerListener ll){
		loggerListeners.add(ll);
	}

	public static Vector<LoggerListener> getLoggerListeners() {
		return loggerListeners;
	}
}