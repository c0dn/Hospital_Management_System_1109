package humans;

public abstract class StaffBuilder<T extends StaffBuilder<T>> extends HumanBuilder<T> {
    String staffId;
    String title;
    String department;

    // Package-private constructor
    StaffBuilder() {}

    public T staffId(String staffId) {
        this.staffId = staffId;
        return self();
    }

    public T title(String title) {
        this.title = title;
        return self();
    }

    public T department(String department) {
        this.department = department;
        return self();
    }

    /**
     * Builds a Staff object with random data for testing purposes.
     *
     * @return this builder
     */
    @Override
    public T withRandomBaseData() {
        super.withRandomBaseData();

        // Generate a random staff ID using DataGenerator
        this.staffId = dataGenerator.generateStaffId();
        this.title = "Staff Member";
        this.department = "General";

        return self();
    }

    /**
     * Validates that all required fields are set.
     *
     * @throws IllegalStateException if any required field is missing
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

    // Remove buildStaff() as each concrete builder will implement build()
    @Override
    protected abstract Staff build();
}
