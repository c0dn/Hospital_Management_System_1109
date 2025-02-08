package humans;

import java.util.Random;

public class DoctorBuilder extends StaffBuilder<DoctorBuilder> {
    String mcr;

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

    @Override
    protected void validateRequiredFields() {
        super.validateRequiredFields();
        if (mcr == null || mcr.isEmpty()) {
            throw new IllegalStateException("MCR number is required for a doctor");
        }
    }

    @Override
    public Doctor build() {
        validateRequiredFields();
        return new Doctor(this);
    }

}
