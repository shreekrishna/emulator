/*
 *  DataBase.java
 *
 *  Created:	August 7, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.emulator.reader.thingmagic.database;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jerry Maine - jerry@pramari.com
 * The thing magic command loosely resemble SQL command that one
 * gives to a database. So, to mirror that here, we structure the code and data
 * in a way that 'looks' like a database.
 */
public class DataBase {
	public Map<String, IDBTable> tables = new HashMap<String, IDBTable>();
	
	public void addTable(String name, IDBTable table){
		tables.put(name, table);
	}
	public IDBTable getTable(String name){
		return tables.get(name);
	}

}
