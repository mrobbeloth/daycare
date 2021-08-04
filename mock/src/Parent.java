public class Parent extends Person{

    private static int gPID = 0;
    private int pid;

    public Parent() {
        super();
    }

    public Parent(String firstName, String lastName, String gender) {
        super(firstName, lastName, gender);
        pid = ++gPID;
    }

    public int getPid() {
        return pid;
    }
}