package yunita;

import java.util.*;

public class Main {

	public Main(String args) {
		DataSource ds = null;
		IndexFile in = null;

		if (args.equals("indexfile")) {
			in = new IndexFile();
		} else {
			ds = new DataSource();
		}

		while (true) {
			try {
				this.displayMenus();
				System.out.print(">> ");
				Scanner s = new Scanner(System.in);
				int input = s.nextInt();

				switch (input) {
				case 1:
					System.out.println("1 Create and populate a database");
					if (args.equals("indexfile")) {
						in.createDatabase();
					} else {
						ds.createDatabase(args);
					}
					break;
				case 2:
					System.out.println("2 Retrieve records with a given key");
					System.out
							.println("Automatic random generate key -> value: ");
					if (args.equals("indexfile")) {
						in.printKeyData();
					} else {
						ds.printKeyData();
					}
					System.out.print("Enter key >> ");
					Scanner s3 = new Scanner(System.in);
					String input2 = s3.nextLine();
					if (args.equals("indexfile")) {
						in.searchByKey(input2);
						;
					} else {
						ds.searchByKey(input2);
					}
					break;
				case 3:
					System.out.println("3 Retrieve records with a given data");
					System.out
							.println("Automatic random generate key -> value: ");
					if (args.equals("indexfile")) {
						in.printKeyData();
					} else {
						ds.printKeyData();
					}
					System.out.print("Enter data >> ");
					Scanner s4 = new Scanner(System.in);
					String input3 = s4.nextLine();
					if (args.equals("indexfile")) {
						in.searchByData(input3);
					} else {
						ds.searchByData(input3);
					}
					break;
				case 4:
					System.out
							.println("4. Retrieve records with a given range of key values");
					Scanner s1 = new Scanner(System.in);
					Scanner s2 = new Scanner(System.in);

					System.out.print("Enter lower >> ");
					String lower = s1.nextLine();
					System.out.print("Enter upper >> ");
					String upper = s2.nextLine();

					if (args.equals("btree")) {
						ds.rangeSearchBtree(lower, upper);
					} else if (args.equals("hash")) {
						ds.rangeSearchHash(lower, upper);
					} else {
						in.rangeSearchIndexFile(lower, upper);
					}
					break;
				case 5:
					System.out.println("5. Destroy the database");
					if (args.equals("indexfile")) {
						in.destroyDatabase();
					} else {
						ds.destroyDatabase();
					}
					break;
				case 6:
					System.out.println("6. Quit");
					// terminate program
					System.out.println("Goodbye.");
					System.exit(0);
				}
			} catch (Exception e) {
				System.out.println("Wrong input.");
			}
		}
	}

	public void displayMenus() {
		String menus[] = { "1 Create and populate a database",
				"2 Retrieve records with a given key",
				"3 Retrieve records with a given data",
				"4. Retrieve records with a given range of key values",
				"5. Destroy the database", "6. Quit" };
		for (String menu : menus) {
			System.out.println(menu);
		}
	}

	public void generateRandomKeyData() {

	}

	public static void main(String[] args) {
		System.out.println("Hello Yunita!");

		// Run -> Run Config -> Argument -> btree
		Main m = new Main(args[0]);
	}

}
