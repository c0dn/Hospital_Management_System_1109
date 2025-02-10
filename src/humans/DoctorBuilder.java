package humans;

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
        this.mcr = dataGenerator.generateMCRNumber();
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
