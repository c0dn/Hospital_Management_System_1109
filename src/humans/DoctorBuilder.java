package humans;

import java.util.Random;

/**
 * Builder class for creating instances of {@link Doctor}.
 * Extends {@link StaffBuilder} to include additional attributes and validation specific to doctors.
 */
public class DoctorBuilder extends StaffBuilder<DoctorBuilder> {
    /** The Medical Council Registration (MCR) number of the doctor. */
    String mcr;

    /**
     * Default constructor for {@code DoctorBuilder}.
     * It is package-private to enforce controlled instantiation via {@link StaffBuilder}.
     */
    DoctorBuilder() {}


    /**
     * Sets the Medical Council Registration (MCR) number for the doctor.
     *
     * @param mcr The MCR number to be assigned to the doctor.
     * @return The current instance of the {@code DoctorBuilder} to allow method chaining.
     */
    public DoctorBuilder mcr(String mcr) {
        this.mcr = mcr;
        return this;
    }

    /**
     * Populates the builder with randomly generated base data, including a random MCR number.
     * This method:
     * <ul>
     *   <li>Generates an MCR number in the format {@code M12345A}.</li>
     *   <li>Sets the title to "Doctor".</li>
     *   <li>Assigns the department as "Medical".</li>
     * </ul>
     *
     * @return The current instance of the {@code DoctorBuilder} to allow method chaining.
     */
    @Override
    public DoctorBuilder withRandomBaseData() {
        super.withRandomBaseData();
        // Generate a random MCR number (M12345A format)
        this.mcr = String.format("M%05dA", new Random().nextInt(100000));
        // Set the appropriate title and department for a doctor
        this.title = "Doctor";
        this.department = "Medical";
        return self();
    }

    /**
     * Validates that all required fields for a doctor are set before building the object.
     * This includes:
     * <ul>
     *   <li>Ensuring that the MCR number is not {@code null} or empty.</li>
     * </ul>
     *
     * @throws IllegalStateException if the MCR number is missing.
     */
    @Override
    protected void validateRequiredFields() {
        super.validateRequiredFields();
        if (mcr == null || mcr.isEmpty()) {
            throw new IllegalStateException("MCR number is required for a doctor");
        }
    }

    /**
     * Builds and returns a {@link Doctor} object using the configured attributes.
     * This method validates the required fields before creating the object.
     *
     * @return A fully constructed {@link Doctor} instance.
     * @throws IllegalStateException if required fields are missing.
     */
    @Override
    public Doctor build() {
        validateRequiredFields();
        return new Doctor(this);
    }

}
