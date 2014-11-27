package brett;
/*
 *  A sample program to create an Database, and then 
 *  populate the db with 1000 records, using Berkeley DB
 *  
 *  Author: Prof. Li-Yan Yuan, University of Alberta
 * 
 *  A directory named "/tmp/my_db" must be created before testing this program.
 *  You may replace my_db with user_db, where user is your user name, 
 *  as required.
 * 
 *  Modified on March 30, 2007 for Berkeley DB 4.3.28
 *
 */
import java.util.Random;

import com.sleepycat.db.*;


public class Sample{

	private static final String TEST_KEY = "jfhxoqwmupwqulscczopqfclglsneokktzpoegoisxmihxeilbekgnyhryszbudxfizqknhevwtn";

	private static final String TEST_DATA = "bbmnfgntfghyxvcqyxfaquptpsjfbkxhbmieryrldlshglyocdrcvusmqmpcchkzoidslxqblghkyonajugpujoijhsupmo";
	private static final String UPPER_RANGE = "jab";
	private static final String LOWER_RANGE = "jax";

	private static final int PREFIX_LENGTH = 3;

	
	private static int[] keyValues;
	private static int[] dataValues;
	
	private static int randKeyValue1;
	
	// to specify the file name for the table
	private static final String SAMPLE_TABLE = "/tmp/user_db/sample_table";

	private static final int NO_RECORDS = 100000;

	/*
	 *  the main function
	 */
	public static void main(String[] args) {

		try {

			// Create the database object.
			// There is no environment for this simple example.
			DatabaseConfig dbConfig = new DatabaseConfig();
			dbConfig.setType(DatabaseType.HASH);
			dbConfig.setAllowCreate(true);
			Database my_table = new Database(SAMPLE_TABLE, null, dbConfig);
			System.out.println(SAMPLE_TABLE + " has been created");

			/* populate the new database with NO_RECORDS records */
			populateTable(my_table,NO_RECORDS);
			System.out.println(NO_RECORDS + " records inserted into" + SAMPLE_TABLE);
			
			/*
			 * KEY SEARCH
			 */
			
			// Generate Key to search for
			String aKey = TEST_KEY;
			DatabaseEntry key = new DatabaseEntry();
		    DatabaseEntry data = new DatabaseEntry();
			key.setData(aKey.getBytes());
	        key.setSize(aKey.length());
	        

	        // Time how long it takes to find the key and get its data
	        long startTime = System.nanoTime();
	        if (my_table.get(null, key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
	        	
	        	//Get time of success
	        	long endTime = System.nanoTime();
	        	
	        	String b = new String (data.getData());
	        	System.out.println("\nData From Key Search Found: " + b); 

		        // Output the time of operation for get by key
		        long duration = (endTime - startTime) / 1000;
		        System.out.println("Time to execute: " + duration + " microseconds");
		        
	        }
	        
	        /*
	         * Data Search
	         */
	        
	        Cursor cursor = my_table.openCursor(null, null);
	        key = new DatabaseEntry();
	        data = new DatabaseEntry();
	        
	        startTime = System.nanoTime();
	        while (cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
	        	
	        	String printData = new String(data.getData());
	        	
	        	if (printData.equals(TEST_DATA)) {
	        		long endTime = System.nanoTime();
	        		System.out.println("\nData from Data Search Found: " + printData);
	        		
	        		long duration = (endTime - startTime) / 1000;
			        System.out.println("Time to execute: " + duration + " microseconds\n");
			        break;
	        	}
	        	
	        }
	        
	        cursor.close();
	        
	        /*
	         * Range Search -- USE FOR HASH
	         */
	        
	        cursor = my_table.openCursor(null, null);
	        key = new DatabaseEntry();
	        data = new DatabaseEntry();
	        
	        startTime = System.nanoTime();
	        int counter = 0;
	        while (cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
	        	
	        	String printKey = new String(key.getData());
	        
	        	if ( (printKey.compareTo(LOWER_RANGE) > 0) && (printKey.compareTo(UPPER_RANGE) < 0) ) {
	        		System.out.println("Found key in Range Search: " + printKey);
	        		counter ++;
	        	}
	        	
	        }
	        
	        System.out.println("Keys found: " + counter);
	        long endTime = System.nanoTime();
	        long duration = (endTime - startTime) / 1000;
	        System.out.println("Time to execute: " + duration + " microseconds\n");

	        cursor.close();
	        
	        /*
	         * New Range Search -- ONLY WORKS ON BTREE
	         */
	        
	        aKey = LOWER_RANGE;
			key = new DatabaseEntry();
		    data = new DatabaseEntry();
			key.setData(aKey.getBytes());
	        key.setSize(aKey.length());
	        
	        cursor = my_table.openCursor(null, null);
	        counter = 0;
	        startTime = System.nanoTime();
	        
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
	        		
	        		if ( (printKey.compareTo(LOWER_RANGE) > 0) && (printKey.compareTo(UPPER_RANGE) < 0) ) {
		        		System.out.println("Found key in Range Search: " + printKey);
		        		counter ++;
		        	} else {
		        		break;
		        	}
	        	}
	        	
	        }
	        
	        System.out.println("Keys found: " + counter);
	        endTime = System.nanoTime();
	        duration = (endTime - startTime) / 1000;
	        System.out.println("Time to execute: " + duration + " microseconds\n");

	        cursor.close();
	        
			/* close the database and the db environment */
			my_table.close();

			/* to remove the table */
			Database.remove(SAMPLE_TABLE,null,null);

		}
		catch (Exception e1) {
			System.err.println("Test failed: " + e1.toString());
			e1.printStackTrace();
		}
	}


	/*
	 *  To pouplate the given table with nrecs records
	 */
	static void populateTable(Database my_table, int nrecs ) {
		int range;
		DatabaseEntry kdbt, ddbt;
		String s;

		/*  
		 *  generate a random string with the length between 64 and 127,
		 *  inclusive.
		 *
		 *  Seed the random number once and once only.
		 */
		
		Random randKey1 = new Random();
		
		
		keyValues = new int[4];
		dataValues = new int[4];
		
		
		
		Random random = new Random(1000000);

		try {
			
			
			
			for(int k = 0; k < keyValues.length; k++){
				randKeyValue1 = randKey1.nextInt(nrecs);	
				keyValues[k] = randKeyValue1;

			    System.out.println("KEY " + keyValues[k]);
			}
			
			for (int i = 0; i < nrecs; i++) {

				/* to generate a key string */
				range = 64 + random.nextInt( 64 );
				s = "";
				for ( int j = 0; j < range; j++ ) 
					s+=(new Character((char)(97+random.nextInt(26)))).toString();

				/* to create a DBT for key */
				kdbt = new DatabaseEntry(s.getBytes());
				kdbt.setSize(s.length()); 
				
				
				// to print out the key/data pair
				 //System.out.println(s);	

				/* to generate a data string */
				range = 64 + random.nextInt( 64 );
				s = "";
				for ( int j = 0; j < range; j++ ) {
					s+=(new Character((char)(97+random.nextInt(26)))).toString();
				}
				
				// System.out.println(s);
				/* to create a DBT for data */
				ddbt = new DatabaseEntry(s.getBytes());
				ddbt.setSize(s.length()); 

				/* to insert the key/data pair into the database */
				my_table.putNoOverwrite(null, kdbt, ddbt);
				
				for (int j = 0; j < keyValues.length; j++) {
					if (i == keyValues[j]) {
						String key = new String(kdbt.getData());
						String data = new String(ddbt.getData());
						System.out.println(key + " " + data);
					}
				}
				
			}
			
		}
		catch (DatabaseException dbe) {
			System.err.println("Populate the table: "+dbe.toString());
			System.exit(1);
		}
	}
	
	static void populateTableIndexFile(Database db1, Database db2, int nrecs ) {
		int range;
		DatabaseEntry kdbt, ddbt;
		String s;

		/*  
		 *  generate a random string with the length between 64 and 127,
		 *  inclusive.
		 *
		 *  Seed the random number once and once only.
		 */
		Random random = new Random(1000000);

		try {
			for (int i = 0; i < nrecs; i++) {

				/* to generate a key string */
				range = 64 + random.nextInt( 64 );
				s = "";
				for ( int j = 0; j < range; j++ ) 
					s+=(new Character((char)(97+random.nextInt(26)))).toString();

				/* to create a DBT for key */
				kdbt = new DatabaseEntry(s.getBytes());
				kdbt.setSize(s.length()); 

				// to print out the key/data pair
				// System.out.println(s);	

				/* to generate a data string */
				range = 64 + random.nextInt( 64 );
				s = "";
				for ( int j = 0; j < range; j++ ) {
					s+=(new Character((char)(97+random.nextInt(26)))).toString();
				}
				
				// System.out.println(s);
				/* to create a DBT for data */
				ddbt = new DatabaseEntry(s.getBytes());
				ddbt.setSize(s.length()); 

				/* to insert the key/data pair into the database */
				db1.putNoOverwrite(null, kdbt, ddbt);
				db2.put(null, ddbt, kdbt);
			}
		}
		catch (DatabaseException dbe) {
			System.err.println("Populate the table: "+dbe.toString());
			System.exit(1);
		}
	}
}
