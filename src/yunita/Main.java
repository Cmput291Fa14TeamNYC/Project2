package yunita;

import java.util.*;

public class Main {

	public Main(String args) {
		DataSource ds = new DataSource();
		try {
			while(true){
				this.displayMenus();
				System.out.print(">> ");
				Scanner s = new Scanner(System.in);
				int input = s.nextInt();
				
				switch (input) {
				case 1:
					System.out.println("1 Create and populate a database");
					ds.createDatabase(args);
					break;
				case 2:
					System.out.println("2 Retrieve records with a given key");
					System.out.println("Automatic random generate key -> value: ");
					ds.printKeyData();
					
					System.out.print("key >> ");
					Scanner s3 = new Scanner(System.in);
					String input2 = s3.nextLine();
					ds.searchByKey(input2);
					break;
				case 3:
					System.out.println("3 Retrieve records with a given data");
					System.out.println("Automatic random generate key -> value: ");
					ds.printKeyData();
					
					System.out.print("data >> ");
					Scanner s4 = new Scanner(System.in);
					String input3 = s4.nextLine();
					ds.searchByData(input3);
					break;
				case 4:
					System.out.println("4. Retrieve records with a given range of key values");
					Scanner s1 = new Scanner(System.in);
					Scanner s2 = new Scanner(System.in);
					
					System.out.print("Lower >> ");
					String lower = s1.nextLine();
					System.out.print("Upper >> ");
					String upper = s2.nextLine();
					
					if(args.equals("btree")){
						System.out.println("BTREE SEARCH");
						ds.rangeSearchBtree(lower, upper);
					} else if(args.equals("hash")){
						System.out.println("HASH SEARCH");
						ds.rangeSearchHash(lower, upper);
					}
					break;
				case 5:
					System.out.println("5. Destroy the database");
					ds.destroyDatabase();
					break;
				case 6:
					System.out.println("6. Quit");
					// terminate program
					System.out.println("Goodbye.");
					System.exit(0);
				}
			}
		} catch (Exception e) {
			System.out.println("Wrong input.");
		}
	}

	public void displayMenus(){
		String menus[] = { "1 Create and populate a database",
				"2 Retrieve records with a given key",
				"3 Retrieve records with a given data",
				"4. Retrieve records with a given range of key values",
				"5. Destroy the database",
				"6. Quit" };
			for(String menu : menus){
				System.out.println(menu);
		}
	}
	
	public static void main(String[] args) {
		System.out.println("Hello Yunita!");
		
		//Run -> Run Config -> Argument -> btree 
		Main m = new Main(args[0]);
	}

}
