package yunita;

import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import com.sleepycat.db.*;

public class DataSource {
	// to specify the file name for the table
	private static final String SAMPLE_TABLE = "/Users/Yunita/My Documents/temp";
	private static final int NO_RECORDS = 100000;

	private Database my_table = null;
	private DatabaseEntry key = null;
	private DatabaseEntry data = null;

	private String[] keyValues;
	private String[] dataValues;

	private int randKeyValue1;

	public DataSource() {

	}

	// Method: createDatabase()
	// >> create database object
	public void createDatabase(String type) {
		try {

			// Create the database object.
			// There is no environment for this simple example.
			DatabaseConfig dbConfig = new DatabaseConfig();
			if (type.equals("btree")) {
				dbConfig.setType(DatabaseType.BTREE);
				System.out.println("Btree");
			} else if (type.equals("hash")) {
				dbConfig.setType(DatabaseType.HASH);
				System.out.println("Hash");
			}

			dbConfig.setAllowCreate(true);
			my_table = new Database(SAMPLE_TABLE, null, dbConfig);
			System.out.println(SAMPLE_TABLE + " has been created");

			/* populate the new database with NO_RECORDS records */
			populateTable(my_table, NO_RECORDS);
			System.out.println(NO_RECORDS + " records inserted into"
					+ SAMPLE_TABLE);

		} catch (Exception e1) {
			System.err.println("Test failed: " + e1.toString());
		}
	}

	// Method: populateTable
	// >> pouplate the given table with n records
	public void populateTable(Database my_table, int nrecs) {
		int range;
		// DatabaseEntry kdbt, ddbt;
		String s;

		keyValues = new String[4];
		dataValues = new String[4];
		int[] index = this.randomIndex();

		/*
		 * generate a random string with the length between 64 and 127,
		 * inclusive.
		 * 
		 * Seed the random number once and once only.
		 */
		Random random = new Random(1000000);

		try {

			for (int i = 0; i < nrecs; i++) {

				/* to generate a key string */
				range = 64 + random.nextInt(64);
				s = "";
				for (int j = 0; j < range; j++)
					s += (new Character((char) (97 + random.nextInt(26))))
							.toString();

				/* to create a DBT for key */
				key = new DatabaseEntry(s.getBytes());
				key.setSize(s.length());

				// to print out the key/data pair
				// System.out.println(s);

				/* to generate a data string */
				range = 64 + random.nextInt(64);
				s = "";
				for (int j = 0; j < range; j++) {
					s += (new Character((char) (97 + random.nextInt(26))))
							.toString();
				}

				// System.out.println(s);
				/* to create a DBT for data */
				data = new DatabaseEntry(s.getBytes());
				data.setSize(s.length());

				/* to insert the key/data pair into the database */
				my_table.putNoOverwrite(null, key, data);

				// get random records from database
				for (int j = 0; j < index.length; j++) {
					if (i == index[j]) {
						keyValues[j] = new String(key.getData());
						dataValues[j] = new String(data.getData());
					}
				}
			}
		} catch (DatabaseException dbe) {
			System.err.println("Populate the table: " + dbe.toString());
			System.exit(1);
		}
	}

	// Method: randomIndex
	// >> generate random index for key/data search
	public int[] randomIndex() {
		Random randKey1 = new Random();

		int index[] = new int[4];
		for (int k = 0; k < index.length; k++) {
			int randKeyValue1 = randKey1.nextInt(NO_RECORDS);
			index[k] = randKeyValue1;
			// System.out.println("Random value: " + index[k]);
		}
		return index;
	}

	// method: searchByKey
	// >> retrieve records with a given key
	public void searchByKey(String input) {
		try {
			key = new DatabaseEntry();
			data = new DatabaseEntry();
			key.setData(input.getBytes());
			key.setSize(input.length());

			// Time how long it takes to find the key and get its data
			long startTime = System.nanoTime();

			if (my_table.get(null, key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {

				// Get time of success
				long endTime = System.nanoTime();

				String b = new String(data.getData());
				System.out.println("Data From Key Search Found:" + b);

				// Output the time of operation for get by key
				long duration = (endTime - startTime) / 1000;
				System.out.println("Time to execute: " + duration
						+ " microseconds");
			}
		} catch (Exception e1) {
			System.err.println("Test failed: " + e1.toString());
		}
	}

	// Method: searchByData
	// >> retrieve records with a given data
	public void searchByData(String input) {
		try {
			Cursor cursor = my_table.openCursor(null, null);

			key = new DatabaseEntry();
			data = new DatabaseEntry();

			long startTime = System.nanoTime();
			while (cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				String keyString = new String(key.getData());
				String dataString = new String(data.getData());
				if (dataString.equals(input)) {
					long endTime = System.nanoTime();
					System.out.println("Key | Data : " + keyString + " | "
							+ dataString + "");
					long duration = (endTime - startTime) / 1000;
					System.out.println("Time to execute: " + duration
							+ " microseconds\n");
				}
				key = new DatabaseEntry();
				data = new DatabaseEntry();
			}

			cursor.close();
		} catch (Exception e1) {
			System.err.println("Test failed: " + e1.toString());
		}
	}

	public void printKeyData() {
		String s = "";
		for (int i = 0; i < keyValues.length; i++) {
			System.out.println(keyValues[i] + " -> " + dataValues[i]);
		}
	}

	// Method: rangeSearchHash
	// >> find all the records whose key values are within a given range
	// and return the number of records for hash
	public void rangeSearchHash(String lower, String upper) {
		try {
			Cursor cursor = my_table.openCursor(null, null);
			key = new DatabaseEntry();
			data = new DatabaseEntry();

			long startTime = System.nanoTime();
			int counter = 0;

			while (cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				String printKey = new String(key.getData());
				if ((printKey.compareTo(lower) > 0)
						&& (printKey.compareTo(upper) < 0)) {
					System.out
							.println("Found key in Range Search: " + printKey);
					counter++;
				}
				key = new DatabaseEntry();
				data = new DatabaseEntry();
			}

			System.out.println("Keys found: " + counter);

			long endTime = System.nanoTime();
			long duration = (endTime - startTime) / 1000;
			System.out
					.println("Time to execute: " + duration + " microseconds");

			cursor.close();
		} catch (Exception e1) {
			System.err.println("Test failed: " + e1.toString());
		}
	}

	// Method: rangeSearchBtree
	// >> find all the records whose key values are within a given range
	// and return the number of records for btree
	public void rangeSearchBtree(String lower, String upper) {
		try {
				Cursor cursor = my_table.openCursor(null, null);
				key = new DatabaseEntry();
			    data = new DatabaseEntry();
				key.setData(lower.getBytes());
		        key.setSize(lower.length());
		        
		        
		        int counter = 0;
		        long startTime = System.nanoTime();
		        
		        if (cursor.getSearchKeyRange(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
		        	// Print first match
		        	String printKey = new String(key.getData());
		        	System.out.println("Found key in New Range Search: " + printKey);
		        	counter++;
				    
		        	// Find all next matches
		        	key = new DatabaseEntry();
				    data = new DatabaseEntry();
		        	while (cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
		        		printKey = new String(key.getData());
		        		
		        		if ( (printKey.compareTo(lower) > 0) && (printKey.compareTo(upper) < 0) ) {
			        		System.out.println("Found key in New Range Search: " + printKey);
			        		counter ++;
			        	} else {
			        		break;
			        	}
		        		
		        		key = new DatabaseEntry();
				        data = new DatabaseEntry();
		        	}
		        	
		        }
		        
		        System.out.println("Keys found: " + counter);
		        long endTime = System.nanoTime();
		        long duration = (endTime - startTime) / 1000;
		        System.out.println("Time to execute: " + duration + " microseconds");

		        cursor.close();
		} catch (Exception e1) {
			System.err.println("Test failed: " + e1.toString());
		}
	}

	// Method: destroyMethod
	// >> close the database and the db environment and remove the table.
	public void destroyDatabase() {
		try {
			/* close the database and the db environment */
			my_table.close();

			/* to remove the table */
			Database.remove(SAMPLE_TABLE, null, null);
			System.out.println(SAMPLE_TABLE + " has been destroyed.");

		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + e.toString());
		} catch (DatabaseException e) {
			System.err.println("Closing database: " + e.toString());
		}
	}

}
