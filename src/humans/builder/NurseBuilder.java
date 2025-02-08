package humans.builder;

import humans.Nurse;

import java.util.Random;

public class NurseBuilder extends StaffBuilder<NurseBuilder> {
    private String rnid;

    public NurseBuilder rnid(String rnid) {
        this.rnid = rnid;
        return this;
    }

    @Override
    public NurseBuilder withRandomBaseData() {
        super.withRandomBaseData();
        // Generate a random RNID (RN12345B format)
        this.rnid = String.format("RN%05dB", new Random().nextInt(100000));
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
        return new Nurse(
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
                department,
                rnid
        );
    }
}