package org.bee.hms.humans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bee.hms.auth.SystemUser;

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

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Staff extends Human implements SystemUser {
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
        this.humanType = "staff";
    }


    /**
     * Utility method to set common Staff fields on any builder that extends StaffBuilder.
     * This helps avoid repetition in factory methods for Staff subclasses.
     *
     * @param <T> The type of builder extending StaffBuilder
     * @param builder The builder instance with human fields already set
     * @param staffId The staff identifier
     * @param title The professional title
     * @param department The department
     * @return The same builder instance with staff fields set
     */
    protected static <T extends StaffBuilder<?>> T setStaffFields(
            T builder,
            String staffId,
            String title,
            String department
    ) {
        builder.staffId(staffId);
        builder.title(title);
        builder.department(department);

        return builder;
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

    @JsonIgnore
    @Override
    public String getUsername() {
        return staffId;
    }
}
