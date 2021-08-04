public class Child extends Person{

    private static int gCID = 0;
    private int cid;

    public Child() {
        super();
    }

    public Child(String firstName, String lastName, String gender) {
        super(firstName, lastName, gender);
        cid = ++gCID;
    }

    public int getCid() {
        return cid;
    }
}