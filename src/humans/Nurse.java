package humans;

import java.time.LocalDate;

/**
 * Represents the details of a nurse in the insurance system.
 */
public class Nurse extends Staff{

    /** The Registered Nurse ID (RNID) of a nurse. */
    private String rnid;


    /**
     * Constructs a Nurse instance using the provided NurseBuilder.
     * This constructor initializes the attributes of the Nurse class, including
     * the Registered Nurse ID (RNID) specific to nurses, while also inheriting
     * and initializing attributes from the Staff class.
     *
     * @param builder The builder instance of type {@code NurseBuilder} used to initialize
     *                the Nurse object. The builder provides the data necessary
     *                for populating the nurse-specific and inherited fields.
     */
    Nurse(NurseBuilder builder) {
        super(builder);
        this.rnid = builder.rnid;
    }


    public static NurseBuilder builder() {
        return new NurseBuilder();
    }



    /**
     * Gets the nurse's Registered Nurse ID.
     *
     * @return The RNID.
     */
    private String getRnid() { return rnid; }

    /**
     * Displays the nurse's Registered Nurse ID (RNID), including the inherited staff details.
     */
    @Override
    public void displayStaff() {
        super.displayStaff();
        System.out.format("RNID: %s%n", rnid);
    }
}