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
            int numEntries = Integer.valueOf(numEntriesStr);

            // Read CSV file with names and store into in-memory data structure
            String fName = "mock_data.csv";
            String fName2 = "mock_data_surnames.csv";
            String dmlOutput = "insert_daycare_log_data.sql";
            ArrayList<String> surnames = new ArrayList<>(100);
            ArrayList<Person> people = new ArrayList<>(100);
            Scanner inputStream = null;
            Scanner inputStream2 = null;
            Random rand = new Random();

            // Read csv files and process
            try {
                inputStream = new Scanner(new File(fName));
                inputStream2 = new Scanner(new File(fName2));

                // skip header
                String line = inputStream.nextLine();
                String line2 = inputStream2.nextLine();

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

            int entries = people.size();
            for(int i = 0; i < numEntries; i++) {
                int randChoice = rand.nextInt(entries);
                Person randPerson = people.get(randChoice);
                String dmlStmt = "INSERT INTO CHILD VALUES ('" +randPerson.getFirstName()+ "','" +
                         randPerson.getLastName() + "','"
                        +"1/2/2021',"
                        +randPerson.getGender()+ "');";
                System.out.println(dmlStmt);
                outputStream.println(dmlStmt);
            }

            // Release resources
            inputStream.close();
            inputStream2.close();
            outputStream.close();
        }
}