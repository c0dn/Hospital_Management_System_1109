package humans;

public class NurseBuilder extends StaffBuilder<NurseBuilder> {
    String rnid;

    NurseBuilder() {}

    public NurseBuilder rnid(String rnid) {
        this.rnid = rnid;
        return this;
    }

    @Override
    public NurseBuilder withRandomBaseData() {
        super.withRandomBaseData();
        this.rnid = dataGenerator.generateRNIDNumber();
        this.title = "Nurse";
        this.department = "Nursing";
        return self();
    }

    @Override
    protected void validateRequiredFields() {
        super.validateRequiredFields();
        if (rnid == null || rnid.isEmpty()) {
            throw new IllegalStateException("Registered Nurse ID (RNID) is required");
        }
    }

    @Override
    public Nurse build() {
        validateRequiredFields();
        return new Nurse(this);
    }
}
