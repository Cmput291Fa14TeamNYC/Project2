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
	
	// to specify the file name for the table
	private static final String SAMPLE_TABLE = "/tmp/my_db/sample_table";
	private static final int NO_RECORDS = 100000;

	/*
	 *  the main function
	 */
	public static void main(String[] args) {

		try {

			// Create the database object.
			// There is no environment for this simple example.
			DatabaseConfig dbConfig = new DatabaseConfig();
			dbConfig.setType(DatabaseType.BTREE);
			dbConfig.setAllowCreate(true);
			Database my_table = new Database(SAMPLE_TABLE, null, dbConfig);
			System.out.println(SAMPLE_TABLE + " has been created");

			/* populate the new database with NO_RECORDS records */
			populateTable(my_table,NO_RECORDS);
			System.out.println(NO_RECORDS + " records inserted into" + SAMPLE_TABLE);
			
			String aKey = TEST_KEY;
			DatabaseEntry key = new DatabaseEntry();
		    DatabaseEntry data = new DatabaseEntry();
			key.setData(aKey.getBytes());
	        key.setSize(aKey.length());
	        
	        
	        long startTime = System.nanoTime();
	        if (my_table.get(null, key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
	        {
	          String b = new String (data.getData());
	          System.out.println("\nData = " + b);  
	        }
	        long endTime = System.nanoTime();

	        long duration = (endTime - startTime) / 1000;
	        System.out.println("Time to execute: " + duration + " microseconds"); 
	        
			/* close the database and the db environment */
			my_table.close();

			/* to remove the table */
			Database.remove(SAMPLE_TABLE,null,null);

		}
		catch (Exception e1) {
			System.err.println("Test failed: " + e1.toString());
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
				my_table.putNoOverwrite(null, kdbt, ddbt);
			}
		}
		catch (DatabaseException dbe) {
			System.err.println("Populate the table: "+dbe.toString());
			System.exit(1);
		}
	}
}
