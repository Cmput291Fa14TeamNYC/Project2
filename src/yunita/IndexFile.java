package yunita;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;

public class IndexFile {

	// to specify the file name for the table
	private static final String SAMPLE_TABLE = "/Users/Yunita/My Documents/temp";
	private static final String INDEX_TABLE = "/Users/Yunita/My Documents/index";
	private static final int NO_RECORDS = 100000;

	// database properties
	private Database my_table = null;
	private Database index_table = null;
	private DatabaseEntry key = null;
	private DatabaseEntry data = null;

	private String[] keyValues;
	private String[] dataValues;
	private int randKeyValue1;

	// I/O properties
	private File file = null;
	private FileWriter fw = null;
	private BufferedWriter bw = null;

	public IndexFile() {
		try {
			file = new File("answers");

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsolutePath());
			bw = new BufferedWriter(fw);

		} catch (IOException e) {
			System.out.println("Cannot write a file.");
		}
	}

	// Method: createDatabase()
	// >> create database object
	public void createDatabase() {
		try {

			// Create the database object.
			// There is no environment for this simple example.
			System.out.println("INDEX FILE");

			DatabaseConfig dbConfig = new DatabaseConfig();
			dbConfig.setType(DatabaseType.BTREE);
			dbConfig.setAllowCreate(true);
			my_table = new Database(SAMPLE_TABLE, null, dbConfig);
			System.out.println(SAMPLE_TABLE + " has been created");

			DatabaseConfig indexDbConfig = new DatabaseConfig();
			indexDbConfig.setSortedDuplicates(true);
			indexDbConfig.setType(DatabaseType.BTREE);
			indexDbConfig.setAllowCreate(true);
			index_table = new Database(INDEX_TABLE, null, indexDbConfig);
			System.out.println(INDEX_TABLE + " has been created");

			/* populate the new database with NO_RECORDS records */
			populateTable(my_table, index_table, NO_RECORDS);
			System.out.println(NO_RECORDS + " records inserted into"
					+ SAMPLE_TABLE);

		} catch (Exception e1) {
			System.err.println("Test failed: " + e1.toString());
		}
	}

	/*
	 * To populate the given table with nrecs records
	 */
	public void populateTable(Database my_table, Database index_table, int nrecs) {
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

				/************************************
				 * IMPLEMENTATION FOR INDEX FILE*****
				 ************************************/
				if (index_table != null) {
					index_table.put(null, data, key);
				}

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
			String result = "";
			int count = 0;

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

				count++;
				
				// write result into file
				bw.write(input +"\n" + b + "\n\n");
				System.out
						.println("Succesfully write the result into the file.");
			}
			System.out.println("Key found: " + count);
		} catch (Exception e1) {
			System.err.println("Test failed: " + e1.toString());
		}
	}

	/************************************
	 * IMPLEMENTATION FOR INDEX FILE*****
	 ************************************/
	public void searchByData(String input) {
		try {
			boolean isFound = false;
			String result = "";

			key = new DatabaseEntry();
			data = new DatabaseEntry();

			key.setData(input.getBytes());
			key.setSize(input.length());

			Cursor indexCursor = index_table.openCursor(null, null);
			int counter = 0;

			long startTime = System.nanoTime();
			if (indexCursor.getSearchKey(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {

				String printKey = new String(key.getData());
				String printData = new String(data.getData());

				counter++;

				key = new DatabaseEntry();
				data = new DatabaseEntry();

				while (indexCursor.getNextDup(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {

					printKey = new String(key.getData());
					printData = new String(data.getData());

					counter++;

					data = new DatabaseEntry();
				}
				isFound = true;
				result +=  printData+ "\n" + input + "\n\n";
			}
			System.out.println("Datas found in IndexFile Data Search: "
					+ counter);

			long endTime = System.nanoTime();
			long duration = (endTime - startTime) / 1000;
			System.out.println("Time to execute: " + duration
					+ " microseconds\n");

			if (!isFound) {
				System.out.println("Data not found.");
			} else {
				// write the result into file
				bw.write(result);
				System.out
						.println("Succesfully write the result into the file.");
			}
			indexCursor.close();
		} catch (Exception e1) {
			System.err.println("Test failed: " + e1.toString());
		}
	}

	public void rangeSearchIndexFile(String lower, String upper) {
		try {
			String result = "";
			
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
				String printData = new String(data.getData());
				System.out
						.println("Found key in New Range Search: " + printKey);
				counter++;

				// Find all next matches
				key = new DatabaseEntry();
				data = new DatabaseEntry();
				while (cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
					printKey = new String(key.getData());
					printData = new String(data.getData());
					if ((printKey.compareTo(lower) >= 0)
							&& (printKey.compareTo(upper) <= 0)) {
						//System.out.println("Found key in New Range Search: "+ printKey);
						counter++;
						result += printKey + "\n" + printData + "\n\n";
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
			System.out
					.println("Time to execute: " + duration + " microseconds");

			// write result into file
			bw.write(result);
			System.out.println("Succesfully write the result into the file.");

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

	// Method: destroyMethod
	// >> close the database and the db environment and remove the table.
	public void destroyDatabase() {
		try {
			/* close the writer */
			bw.close();

			/* close the database and the db environment */
			my_table.close();
			index_table.close();

			/* to remove the table */
			Database.remove(SAMPLE_TABLE, null, null);
			Database.remove(INDEX_TABLE, null, null);

			System.out.println(SAMPLE_TABLE + " has been destroyed.");
			System.out.println(INDEX_TABLE + " has been destroyed.");

		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + e.toString());
		} catch (DatabaseException e) {
			System.err.println("Closing database: " + e.toString());
		} catch (IOException e) {
			System.out.println("Cannot close the file.");
		}
	}

}
