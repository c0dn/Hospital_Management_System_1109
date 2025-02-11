package humans;

import java.util.Random;

/**
 * Builder class for constructing instances of {@link Nurse}.
 * <p>
 * This class extends {@link StaffBuilder} to inherit staff-related attributes
 * and provides additional methods for setting nurse-specific attributes,
 * such as the Registered Nurse ID (RNID). It follows the builder pattern
 * to facilitate object creation in a structured and readable manner.
 * </p>
 */
public class NurseBuilder extends StaffBuilder<NurseBuilder> {
    /** The Registered Nurse ID (RNID) assigned to the nurse. */
    String rnid;


     /**
     * Constructs a new instance of {@code NurseBuilder}.
     * <p>
     * This constructor is package-private to enforce instantiation
     * through the {@link Nurse#builder()} method.
     * </p>
     */
    NurseBuilder() {}


     /**
     * Sets the Registered Nurse ID (RNID) for the nurse.
     *
     * @param rnid The RNID to assign.
     * @return The current instance of {@code NurseBuilder} for method chaining.
     */
    public NurseBuilder rnid(String rnid) {
        this.rnid = rnid;
        return this;
    }

     /**
     * Populates the nurse's attributes with randomly generated data.
     * <p>
     * This method overrides the base method to include a randomly generated
     * RNID in the format {@code RN12345B}, along with a predefined
     * title ("Nurse") and department ("Nursing").
     * </p>
     *
     * @return The current instance of {@code NurseBuilder} for method chaining.
     */
    @Override
    public NurseBuilder withRandomBaseData() {
        super.withRandomBaseData();
        // Generate a random RNID (RN12345B format)
        this.rnid = String.format("RN%05dB", new Random().nextInt(100000));
        this.title = "Nurse";
        this.department = "Nursing";
        return self();
    }

     /**
     * Validates that all required fields have been set before building a {@link Nurse} instance.
     * <p>
     * This method ensures that the RNID is not null or empty before proceeding with
     * object creation. If a required field is missing, an {@code IllegalStateException}
     * is thrown.
     * </p>
     *
     * @throws IllegalStateException if RNID is missing.
     */
    @Override
    protected void validateRequiredFields() {
        super.validateRequiredFields();
        if (rnid == null || rnid.isEmpty()) {
            throw new IllegalStateException("Registered Nurse ID (RNID) is required");
        }
    }

     /**
     * Builds and returns a {@link Nurse} instance using the current state of the builder.
     * <p>
     * This method first calls {@link #validateRequiredFields()} to ensure all necessary
     * fields are populated before creating the {@code Nurse} object.
     * </p>
     *
     * @return A fully constructed {@link Nurse} object.
     * @throws IllegalStateException If any required field is missing.
     */
    @Override
    public Nurse build() {
        validateRequiredFields();
        return new Nurse(this);
    }
}