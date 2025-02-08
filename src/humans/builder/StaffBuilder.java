package humans.builder;

import humans.Staff;

import java.util.Random;

public abstract class StaffBuilder<T extends StaffBuilder<T>> extends HumanBuilder<T> {
    protected String staffId;
    protected String title;
    protected String department;

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

        // Generate a random staff ID (e.g., "S12345")
        this.staffId = String.format("S%05d", new Random().nextInt(100000));

        // Use DataGenerator for title and department if available,
        // or set some default values
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

    /**
     * Creates a new Staff instance with the builder's properties.
     *
     * @return a new Staff instance
     */
    protected Staff buildStaff() {
        validateRequiredFields();
        return new Staff(
                name,
                dateOfBirth,
                nricFin,
                maritalStatus,
                residentialStatus,
                nationality,
                address,
                contact,
                sex,
                bloodType,
                isVaccinated,
                staffId,
                title,
                department
        );
    }
}