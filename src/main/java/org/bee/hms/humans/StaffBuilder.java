package org.bee.hms.humans;

/**
 * An abstract builder class for creating Staff objects.
 * This class extends {@link HumanBuilder} and provides methods for setting attributes
 * related to staff members, such as staff ID, title, and department.
 *
 * @param <T> The type of the concrete builder extending this class
 */
public abstract class StaffBuilder<T extends StaffBuilder<T>> extends HumanBuilder<T> {
    /** The unique identifier for a staff member. */
    String staffId;
    /** The title or designation of the staff member. */
    String title;
    /** The department to which the staff member belongs. */
    String department;

    /** Package-private constructor to prevent direct instantiation. */
    StaffBuilder() {}

    /**
     * Sets the staff ID for the staff member.
     *
     * @param staffId The unique identifier for the staff member
     * @return This builder instance
     */
    public T staffId(String staffId) {
        this.staffId = staffId;
        return self();
    }

    /**
     * Sets the title of the staff member.
     *
     * @param title The title or designation of the staff member
     * @return This builder instance
     */
    public T title(String title) {
        this.title = title;
        return self();
    }

    /**
     * Sets the department of the staff member.
     *
     * @param department The department in which the staff member works
     * @return This builder instance
     */
    public T department(String department) {
        this.department = department;
        return self();
    }

    /**
     * Builds a Staff object with random data for testing purposes.
     *
     * @return This builder
     */
    @Override
    public T withRandomBaseData() {
        super.withRandomBaseData();
        

        this.staffId = dataGenerator.generateStaffId();
        this.title = "Staff Member";
        this.department = "General";

        return self();
    }

    /**
     * Validates that all required fields are set.
     *
     * @throws IllegalStateException If any required field is missing
     */
    @Override
    protected void validateRequiredFields() {
        super.validateRequiredFields();
        if (staffId == null || staffId.isEmpty()) {
            throw new IllegalStateException("Staff ID is required");
        }
        if (title == null || title.isEmpty()) {
            throw new IllegalStateException("Title is required");
        }
        if (department == null || department.isEmpty()) {
            throw new IllegalStateException("Department is required");
        }
    }

    /**
     * Builds and returns a concrete Staff object.
     * Concrete subclasses must implement this method.
     *
     * @return the constructed Staff object
     */
    @Override
    protected abstract Staff build();
}
