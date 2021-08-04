import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class DaycareMockGenerator {
        public static void main (String[] args) {
            /* 1. Ask for number of entries to generate
            *  2. Read CSV file with names
            *  3. Generate data for each table in relation
            *  3. Write generate data to DML script files*/

            // Ask for number of entries to generate
            Scanner kb = new Scanner(System.in);
            System.out.print("Number of Entries: ");
            String numEntriesStr = kb.nextLine();
            int numEntries = Integer.parseInt(numEntriesStr);

            // Read CSV file with names and store into in-memory data structure
            String fName = "mock_data.csv";
            String fName2 = "mock_data_surnames.csv";
            String dmlOutput = "insert_daycare_log_data.sql";
            ArrayList<String> surnames = new ArrayList<>(100);
            ArrayList<Person> people = new ArrayList<>(100);
            ArrayList<Child> children = new ArrayList<>(100);
            ArrayList<Parent> parents = new ArrayList<>(100);
            Scanner inputStream = null;
            Scanner inputStream2 = null;
            Random rand = new Random();

            // Read csv files and process
            try {
                inputStream = new Scanner(new File(fName));
                inputStream2 = new Scanner(new File(fName2));

                // skip header lines
                String line = inputStream.nextLine();
                inputStream2.nextLine();

                // Read in last names
                while (inputStream2.hasNextLine()) {
                    surnames.add(inputStream2.nextLine());
                }

                int numSurNames = surnames.size();
                while(inputStream.hasNextLine()) {
                    line = inputStream.nextLine();
                    String[] ary = line.split(",");
                    int randSurNameCh = rand.nextInt(numSurNames);
                    Person p = new Person(ary[0],  surnames.get(randSurNameCh), ary[1]);
                    people.add(p);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            /* Generate data for each table in relation */
            PrintWriter outputStream = null;
            try {
                outputStream = new PrintWriter(dmlOutput);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // Generate children
            int entries = people.size();
            for(int i = 0; i < numEntries; i++) {
                int randChoice = rand.nextInt(entries);
                Person randPerson = people.get(randChoice);
                String dmlStmt = "INSERT INTO CHILD VALUES (DEFAULT,'" +randPerson.getFirstName()+ "','" +
                         randPerson.getLastName() + "','"
                        +"1/2/2021','"
                        +randPerson.getGender()+ "');";
                System.out.println(dmlStmt);
                if (outputStream != null) {
                    outputStream.println(dmlStmt);
                }

                // add to children arraylist for later use
                Child c = new Child(randPerson.getFirstName(), randPerson.getFirstName(), randPerson.getGender());
                children.add(c);
            }

            // Generate Parents
            for(int i = 0; i < numEntries / 2; i++) {
                int randChoice = rand.nextInt(entries);
                Person randPerson = people.get(randChoice);
                String dmlStmt = "INSERT INTO PARENT VALUES (DEFAULT,'" +randPerson.getFirstName()+ "','" +
                        randPerson.getLastName() + "','"
                        +"1/2/1990','"
                        +randPerson.getGender()+ "');";
                System.out.println(dmlStmt);
                if (outputStream != null) {
                    outputStream.println(dmlStmt);
                }

                // add to children arraylist for later use
                Parent p = new Parent(randPerson.getFirstName(), randPerson.getFirstName(), randPerson.getGender());
                parents.add(p);
            }

            // Assign children to parents
            ArrayList<Integer> kidPicks = new ArrayList<>(entries);
            int curParent = 0;
            int childALSize = children.size();
            for (int i = 0; curParent < parents.size(); i++) {
                int randChild = rand.nextInt(childALSize);
                while (kidPicks.contains(randChild)) {
                    randChild = rand.nextInt(childALSize);
                }
                kidPicks.add(randChild);
                int cid = children.get(randChild).getCid();
                int pid = parents.get(curParent).getPid();
                String dmlStmt = "INSERT INTO FAMILY VALUES ('"+cid+"','"+pid+"');";
                System.out.println(dmlStmt);
                if (outputStream != null) {
                    outputStream.println(dmlStmt);
                }

                if (i%2 == 0)  {
                    curParent++;
                }
            }

            // Release resources
            if (inputStream != null) {
                inputStream.close();
            }
            if (inputStream2 != null) {
                inputStream2.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
}