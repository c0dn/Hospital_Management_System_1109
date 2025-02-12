package humans;

/**
 * Represents staff in the insurance system.
 * <p>
 * Staff members include:
 * </p>
 * <ul>
 *     <li>Doctors</li>
 *     <li>Nurses</li>
 *     <li>Administrative personnel</li>
 * </ul>
 */

public class Staff extends Human {
    /** The unique identifier for the staff. */
    private String staffId;
    /** The title of the staff. */
    private String title;
    /** The department of the staff. */
    private String department;


    /**
     * Constructs a Staff object using the provided StaffBuilder.
     * This constructor initializes the attributes of the Staff class, including staff-specific
     * details such as staff ID, title, and department, based on the values provided in the builder.
     * The constructor also leverages the parent Human class constructor to set generic human
     * attributes such as name, date of birth, and contact information.
     * Package-private constructor
     *
     * @param builder The builder instance of type {@code StaffBuilder<?>} used to initialize
     *                the Staff object.
     *                The builder provides the data necessary for populating
     *                the staff-specific and inherited fields.
     */
    Staff(StaffBuilder<?> builder) {
        super(builder);
        this.staffId = builder.staffId;
        this.title = builder.title;
        this.department = builder.department;
    }


    /**
     * Displays staff information.
     */
    public void displayHuman() {
        super.displayHuman();
//        System.out.format("Name: %s%n", name);
//        System.out.format("Title: %s%n", title);
//        System.out.format("Department: %s%n", department);
//        System.out.format("Staff ID: %s%n", staffId);

        System.out.printf("%n%n");
        System.out.println("STAFF DETAILS");
        System.out.println("---------------------------------------------------------------------");

//        System.out.printf("%nName: " + name);
        System.out.printf("Staff ID: " + staffId);
        System.out.printf("\nDepartment: " + department);
        System.out.printf("\nTitle: " + title);
    }
}
