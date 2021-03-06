/*
 *  DBTagDataRow.java
 *
 *  Created:	August 7, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.emulator.reader.thingmagic.database.impl.row;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.emulator.reader.thingmagic.database.IDBRow;
import org.rifidi.emulator.reader.thingmagic.database.impl.DBTagID;
import org.rifidi.services.tags.enums.TagGen;
import org.rifidi.services.tags.impl.RifidiTag;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
//TODO implement this better.
public class DBTagIDRow implements IDBRow {
	private static Log logger = LogFactory.getLog(DBTagID.class);

	static private String ID = "id";
	static private String ANTENNA_ID = "antenna_id";
	static private String READ_COUNT = "read_count";
	static private String PROTOCOL_ID = "protocol_id";
	static private String KILLED = "killed";
	static private String PASSWORD = "password";
	static private String LOCKED = "locked";
	static private String FREQUENCY = "frequency";
	static private String DSPMICROS = "dspmicros";
	static private String TIMESTAMP = "timestamp";

	static private Set<String> columns = new HashSet<String>();
	static private Set<String> readable = new HashSet<String>();
	static private Set<String> writable = new HashSet<String>();

	private static boolean initialized = false;

	/*
	 * Here is a list of real frequency data returned from the thing magic
	 * reader. When the user requests frequency data... we just return one of
	 * these at random. Note: Some times the reader will return the same
	 * frequency data for two tags of the same Gen type... but this is not
	 * implemented.
	 * 
	 * This is an 8x8 block of numbers--should provide "random" enough numbers
	 * for the casual user.
	 */
	// TODO Possibly add more numbers to this list.
	private static int FREQUENCIES[] = { 914750, 918250, 915250, 926750,
			904250, 925250, 917250, 909750, 907250, 909250, 910250, 910250,
			908250, 906750, 908750, 912750, 926250, 921750, 913750, 910750,
			911750, 914750, 919250, 914250, 922750, 912750, 911250, 925250,
			903750, 922250, 907250, 909250,

			916750, 903250, 907750, 917750, 912250, 926250, 905750, 913750,
			910750, 906250, 914750, 918250, 916750, 924250, 902750, 905250,
			911250, 913750, 923250, 903750, 919750, 906250, 914750, 908250,
			912250, 908750, 913250, 915250, 913750, 904250, 910750, 903750 };

	/*
	 * Here are the lowest and highest values of dspmicros (the number of
	 * microseconds to read one tag) from 28 tries with 5 tags in front of the
	 * reader antenna. The idea is that we fake this number by picking a random
	 * number between the highest and the lowest and return it.
	 */
	private static int DSPMICROS_LOW = 8798;
	private static int DSPMICROS_HIGH = 73132;

	private RifidiTag tag;

	private Random random = new Random();

	/**
	 * This creates a map of tag data keys (based on the way the Mercury 4,
	 * ThingMagic reader defines them) and their respective values.
	 * 
	 * @param tag
	 *            The RFID tag to get the tag data from
	 */
	public DBTagIDRow(RifidiTag tag) {
		logger.debug("Creating tag row data");
		this.tag = tag;

		initialize();
	}

	/* we only need to do this once... and be done with it. */
	private static void initialize() {
		if (!initialized) {
			columns.add(ID);
			columns.add(ANTENNA_ID);
			columns.add(READ_COUNT);
			columns.add(PROTOCOL_ID);
			columns.add(KILLED);
			columns.add(PASSWORD);
			columns.add(LOCKED);
			columns.add(FREQUENCY);
			columns.add(DSPMICROS);
			columns.add(TIMESTAMP);

			readable.add(ID);
			readable.add(ANTENNA_ID);
			readable.add(READ_COUNT);
			readable.add(PROTOCOL_ID);
			readable.add(FREQUENCY);
			readable.add(DSPMICROS);
			readable.add(LOCKED);
			readable.add(TIMESTAMP);

			// TODO: Add the writables.

			initialized = true;
		}
	}

	public RifidiTag getTag() {
		return this.tag;
	}

	@Override
	public boolean containsColumn(String key) {
		return columns.contains(key);
	}

	@Override
	public String get(String key) {
		// TODO Auto-generated method stub
		if (key.equals(ID)) {
			return "0x" + tag.toString().replace(" ", "");
		}

		//TODO Match this with what is displayed on the gui (antenna_id is 1 to 4, inclusive)
		if (key.equals(ANTENNA_ID)) {
			Integer antenna = tag.getAntennaLastSeen();
			antenna++;
			return antenna.toString();
		}

		if (key.equals(READ_COUNT)) {
			/*
			 * For the Mercury 4, Thing Magic reader, the read count is how many
			 * times from the start of a command to the end of it that it has
			 * read it in the antenna field of view. Which for this emulator is
			 * once and only once.
			 */
			return "1";
		}

		if (key.equals(PROTOCOL_ID)) {
			String protocolID = "0";
			if (tag.getTagGen() == TagGen.GEN1) {
				protocolID = "1";
			}
			if (tag.getTagGen() == TagGen.GEN2) {
				protocolID = "12";
			}
			return protocolID;
		}

		if (key.equals(TIMESTAMP)) {
			/*
			 * timestamp Time stamp is 'seconds from epoc'.'microseconds' 1) Get
			 * the current time in milliseconds and convert it to a String. 2)
			 * Insert the period to make it seconds from epoc--not milliseconds
			 * from epoc. 3) Append three '0's to fake microseconds. And now we
			 * have a passable thingmagic timestamp.
			 */
			StringBuilder timeStamp = new StringBuilder(Long.toString(System
					.currentTimeMillis()));
			timeStamp.insert(timeStamp.length() - 3, '.');
			timeStamp.append("000");

			return timeStamp.toString();
		}

		if (key.equals(FREQUENCY)) {
			/*
			 * pick a random number from the list of frequencies. Note:
			 * Random.nextInt(value) returns a value from a range of [0, value)
			 * -- perfect for selecting a random number from an array in Java.
			 * :)
			 */
			return Integer.toString(FREQUENCIES[random
					.nextInt(FREQUENCIES.length)]);
		}

		if (key.equals(DSPMICROS)) {
			/*
			 * grab the number of integers from low and high dspmicros numbers.
			 * We add one to make sure we get the numbers inclusive on both ends
			 * of the range.
			 */
			int range = (DSPMICROS_HIGH - DSPMICROS_LOW) + 1;
			/*
			 * now we pick a random number from that range. -- [0, range-1)
			 */
			int rand = random.nextInt(range);

			/*
			 * Then we add the offset (dspmicrosLow) to the range, convert it to
			 * a string, and return the result.
			 */
			return Integer.toString(DSPMICROS_LOW + rand);
		}

		if (key.equals(LOCKED)) {
			/*
			 * return "1" for always locked.
			 */
			// TODO: This is related to the DBTagDataRow.... which is not
			// implemented.
			return "1";
		}

		/*
		 * Best effort to complete the command... If it is not readable or not
		 * there, we return an empty String.
		 */
		return "";
	}

	@Override
	public String put(String key, String value) {
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public boolean isReadable(String column) {
		/*
		 * this method must be called if get(column) can be called
		 */
		return readable.contains(column);
	}

	@Override
	public boolean isWritable(String column) {
		/*
		 * this method must be called if put(column, value) can be called
		 */
		return writable.contains(column);
	}

	/*
	 * On the next two methods we simple call the equivalent method of the
	 * internal tag object.
	 */

	@Override
	public boolean equals(Object o) {
		if (o instanceof DBTagIDRow) {
			return tag.equals(((DBTagIDRow) o).getTag());
		}

		return false;
	}

	@Override
	public int hashCode() {
		return tag.hashCode();
	}

	@Override
	public int compareToValue(String key, String testValue) {
		// TODO Auto-generated method stub
		
		//TODO add a check to see if the format is correct for ID.
		if (key.equals(ID)){
			BigInteger id  = new BigInteger(tag.toString().replace(" ", ""), 16);
			
			BigInteger id2 = new BigInteger(testValue.substring(2), 16);
			
			return id.compareTo(id2);
		}
		
		if (key.equals(PROTOCOL_ID)){
			int protocolID = 0;
			switch(tag.getTag().getTagGeneration()){
				case GEN1:
					protocolID = 1;
				break;
				case GEN2:
					protocolID = 12;
			}
			
			int protocolIDTest = 0;
			if (testValue.equalsIgnoreCase("'GEN2'")){
				protocolIDTest = 12;
			} else if (testValue.equalsIgnoreCase("'EPC1'")){
				protocolIDTest = 1;
			} else {
				//TODO: Deal with when this fails
				try{
					protocolIDTest = Integer.valueOf(testValue);
				} catch (NumberFormatException e){
					//TODO Deal with this.
					logger.debug(e);
				}
			}
			
			return protocolID - protocolIDTest;
		}
		
		logger.debug("Value: " + get(key));
		logger.debug("Test Value: " + testValue);
		
		return Integer.valueOf(get(key)) - Integer.valueOf(testValue);
	}
}
