package brett;

import com.sleepycat.db.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;

import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.Environment;
import com.sleepycat.db.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;

public class Main {
	
	private static Environment myEnv;
	private static EntityStore store;

	public static void main(String[] args) {
		try {
		    EnvironmentConfig myEnvConfig = new EnvironmentConfig();
		    StoreConfig storeConfig = new StoreConfig();

		    myEnvConfig.setAllowCreate(true);
		    myEnvConfig.setInitializeCache(true);
		    storeConfig.setAllowCreate(true);  

		    try {
		        // Open the environment and entity store
		        myEnv = new Environment(new File("export/dbEnv"), myEnvConfig);
		        store = new EntityStore(myEnv, "EntityStore", storeConfig);
		    } catch (FileNotFoundException fnfe) {
		        System.err.println(fnfe.toString());
		        System.exit(-1);
		    }
		} catch(DatabaseException dbe) {
		    System.err.println("Error opening environment and store: " +
		                        dbe.toString());
		    System.exit(-1);
		} 
		
		try {
			PrimaryIndex<String,KeyValuePair> pi =
				    store.getPrimaryIndex(String.class, KeyValuePair.class);
			EntityCursor<KeyValuePair> cursor = 
				    pi.entities("dab", true, "dam", true);
			
			try {
			    for (KeyValuePair pair : cursor) {
			        System.out.println(pair.data);
			    }
			// Always make sure the cursor is closed when we are done with it.
			} finally {
			    cursor.close(); }
			
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				System.out.println(s);	

				/* to generate a data string */
				range = 64 + random.nextInt( 64 );
				s = "";
				for ( int j = 0; j < range; j++ ) {
					s+=(new Character((char)(97+random.nextInt(26)))).toString();
				}
				
				System.out.println(s);
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
