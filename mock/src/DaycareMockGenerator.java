import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
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
            // Source data is the National Records of Scotland
            // Filtered data for use in this utility
            // https://www.nrscotland.gov.uk/statistics-and-data/statistics/statistics-by-theme/vital-events/names/most-common-surnames/list-of-data-tables
            // https://www.nrscotland.gov.uk/statistics-and-data/statistics/statistics-by-theme/vital-events/names/babies-first-names/babies-first-names-summary-records-comma-separated-value-csv-format
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
            ArrayList<Integer> kidPicks = new ArrayList<>(numEntries);
            HashMap<Integer, Integer> families = new HashMap<>(numEntries);
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

                // keep track of families for transfers
                families.put(cid, pid);
            }

            // Generate category tuples
            outputStream.println("INSERT INTO CATEGORY VALUES ('DROPOFF', 'A child is entering the facility');");
            outputStream.println("INSERT INTO CATEGORY VALUES ('PICKUP', 'A child is leaving the facility');");
            outputStream.println("INSERT INTO CATEGORY VALUES ('TEST', 'This is a test entry and not valid');");
            outputStream.println("INSERT INTO CATEGORY VALUES ('OTHER', 'not a dropoff/pickup');");

            // Generate mood tuples, from Oxford Dictionary
            // https://languages.oup.com/google-dictionary-en/
            outputStream.println("INSERT INTO MOOD VALUES ('EXHAUSTED'," +
                    " 'drained of one''s physical or mental resources');");
            outputStream.println("INSERT INTO MOOD VALUES ('CONFUSED', " +
                    "'unable to think clearly; bewildered');");
            outputStream.println("INSERT INTO MOOD VALUES ('ECSTATIC', " +
                    "'overwhelming happiness or joyful excitement');");
            outputStream.println("INSERT INTO MOOD VALUES ('GUILTY', " +
                    "'culpable of or responsible for a specified wrongdoing');");
            outputStream.println("INSERT INTO MOOD VALUES ('SUSPICIOUS', " +
                    "'having or showing a cautious distrust of someone or something');");
            outputStream.println("INSERT INTO MOOD VALUES ('ANGRY', " +
                    "'feeling or showing strong annoyance, displeasure, or hostility');");
            outputStream.println("INSERT INTO MOOD VALUES ('HYSTERICAL', " +
                    "'deriving from or affected by uncontrolled extreme emotion');");
            outputStream.println("INSERT INTO MOOD VALUES ('FRUSTRATED', " +
                    "'feeling/expressing distress and annoyance, " +
                    "especially because of inability to change/achieve a thing');");
            outputStream.println("INSERT INTO MOOD VALUES ('SAD', " +
                    "'feeling or showing sorrow; unhappy');");
            outputStream.println("INSERT INTO MOOD VALUES ('CONFIDENT', " +
                    "'feeling or showing certainty about something; self-assured');");
            outputStream.println("INSERT INTO MOOD VALUES ('EMBARRASSED', " +
                    "'feeling or showing awkwardness or unease');");
            outputStream.println("INSERT INTO MOOD VALUES ('HAPPY', " +
                    "'feeling or showing pleasure or contentment');");
            outputStream.println("INSERT INTO MOOD VALUES ('MISCHIEVOUS', " +
                    "'causing or showing a fondness for causing trouble in a playful way');");
            outputStream.println("INSERT INTO MOOD VALUES ('DISGUSTED', " +
                    "'feeling or expressing revulsion or strong disapproval');");
            outputStream.println("INSERT INTO MOOD VALUES ('FRIGHTENED', " +
                    "'afraid or anxious');");
            outputStream.println("INSERT INTO MOOD VALUES ('ENRAGED', " +
                    "'very angry; furious');");
            outputStream.println("INSERT INTO MOOD VALUES ('ASHAMED', " +
                    "'embarrassed or guilty because of one''s actions, characteristics, or associations');");
            outputStream.println("INSERT INTO MOOD VALUES ('CAUTIOUS', " +
                    "'careful to avoid potential problems or dangers');");
            outputStream.println("INSERT INTO MOOD VALUES ('SMUG', " +
                    "'having or showing an excessive pride in oneself or one''s achievements');");
            outputStream.println("INSERT INTO MOOD VALUES ('DEPRESSED', " +
                    "'in a state of general unhappiness or despondency');");
            outputStream.println("INSERT INTO MOOD VALUES ('OVERWHELMED', " +
                    "'bury or drown beneath a huge mass');");
            outputStream.println("INSERT INTO MOOD VALUES ('HOPEFUL', " +
                    "'feeling or inspiring optimism about a future event');");
            outputStream.println("INSERT INTO MOOD VALUES ('LONELY', " +
                    "'sad because one has no friends or company');");
            outputStream.println("INSERT INTO MOOD VALUES ('LOVESTRUCK', " +
                    "'besotted or infatuated.');");
            outputStream.println("INSERT INTO MOOD VALUES ('BORED', " +
                    "'feeling weary because one is unoccupied or lacks interest in one''s current activity');");
            outputStream.println("INSERT INTO MOOD VALUES ('SURPRISED', " +
                    "'mild astonishment or shock');");
            outputStream.println("INSERT INTO MOOD VALUES ('ANXIOUS', " +
                    "'experiencing worry, unease, or nervousness');");
            outputStream.println("INSERT INTO MOOD VALUES ('SHOCKED', " +
                    "'surprised and upset');");
            outputStream.println("INSERT INTO MOOD VALUES ('SHY', " +
                    "'being reserved or having or showing nervousness or timidity');");

            // Create some sample Transfer tuples
            for (int i = 0; i < numEntries; i++) {
                LocalDate ld = LocalDate.now();
                LocalTime lt = LocalTime.now();
                int kid = rand.nextInt(numEntries);
                int parent= families.getOrDefault(kid, -1);
                if (parent == -1) {
                    i--;
                    continue;
                }
                String dmlStmt = "INSERT INTO TRANSFER VALUES (DEFAULT,'"+ld.toString()+"','"+lt.toString()+"');";
                System.out.println(dmlStmt);
                outputStream.println(dmlStmt);
                dmlStmt = "INSERT INTO ATTENDANCE VALUES ("+i+","+kid+");";
                System.out.println(dmlStmt);
                outputStream.println(dmlStmt);
                dmlStmt = "INSERT INTO PARENT_XCHGE VALUES ("+i+","+parent+");";
                System.out.println(dmlStmt);
                outputStream.println(dmlStmt);
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