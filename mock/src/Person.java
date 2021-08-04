public class Person {
    private enum Gender {
        MALE("male"), FEMALE("female"), OTHER("OTHER");

        private final String genderLbl;

        private Gender(String genderLabel) {
            genderLbl = genderLabel;
        }

        public String getGenderLbl() {
            return genderLbl;
        }
    }

    private final String firstName;
    private final String lastName;
    private final Gender genderLbl;

    public Person() {
        firstName = "James";
        lastName = "Kirk";
        genderLbl = Gender.MALE;
    }

    public Person(String firstName, String lastName, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        switch(gender.toLowerCase().charAt(0)) {
            case 'f':
                this.genderLbl = Gender.FEMALE;
                break;
            case 'm':
                this.genderLbl = Gender.MALE;
                break;
            default:
                this.genderLbl = Gender.OTHER;
                break;
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return genderLbl.toString();
    };
}