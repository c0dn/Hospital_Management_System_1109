package org.bee.hms.humans;

/**
 * Represents the details of a nurse in the insurance system.
 * <p>
 * This class extends {@link Staff} and adds attributes specific to nurses,
 * such as the Registered Nurse ID (RNID). It is constructed using the
 * {@link NurseBuilder} to ensure proper initialization of required fields.
 * </p>
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

    /**
     * Creates and returns a new instance of {@code NurseBuilder}.
     * <p>
     * This method provides a fluent interface for constructing
     * a {@code Nurse} object using the builder pattern.
     * </p>
     *
     * @return A new instance of {@link NurseBuilder} to facilitate nurse creation.
     */
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
     * Displays the nurse's Registered Nurse ID (RNID) along with the inherited staff details.
     * <p>
     * This method first calls {@code displayStaff()} from the {@link Staff} class
     * and then prints the RNID specific to the nurse.
     * </p>
     */
    @Override
    public void displayHuman() {
        super.displayHuman();
//        System.out.format("RNID: %s%n", rnid);

        System.out.println("\n\nROLE: NURSE");
        System.out.println("---------------------------------------------------------------------");
        System.out.format("RNID: %s%n", rnid);
        System.out.println("=====================================================================");
    }

    public void printAsAttending() {
        System.out.printf("  - Attending Nurse: %s (RNID: %s)%n", name, rnid);
    };
}
