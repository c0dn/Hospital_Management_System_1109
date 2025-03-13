package org.bee.humans;

/**
 * Represents a doctor in the insurance system.
 * <p>
 *     A doctor is a specialized type of {@link Staff} with an additional Medical Council Registration (MCR) number.
 * </p>
 */
public class Doctor extends Staff {

    /**
     * The Medical Council Registration (MCR) number of the doctor.
     */
    private final String mcr;


    /**
     * Constructs a Doctor object using the provided DoctorBuilder.
     * This constructor initializes the doctor-specific details,
     * including the Medical Council Registration (MCR) number,
     * as well as all inherited attributes from the {@link Staff} and {@link Human} classes.
     *
     * @param builder The {@code DoctorBuilder} instance used to initialize the Doctor object.
     *                Contains the data required to populate both doctor-specific fields such as the MCR number,
     *                and inherited attributes of the Doctor class.
     */
    Doctor(DoctorBuilder builder) {
        super(builder);
        this.mcr = builder.mcr;
    }


    /**
     * Creates and returns a new {@code DoctorBuilder} instance.
     * This builder can be used to construct instances of the {@code Doctor} class.
     *
     * @return A new {@code DoctorBuilder} instance for building {@code Doctor} objects.
     */
    public static DoctorBuilder builder() {
        return new DoctorBuilder();
    }


    /**
     * Retrieves the Medical Council Registration (MCR) number of the doctor.
     *
     * @return The MCR number.
     */
    public String getMcr() {
        return mcr;
    }

    public void printAsAttending() {
        System.out.format("%-20s: %s (ID: %s)%n",
                "Attending Doctor",
                name,
                mcr);
    }


    /**
     * Displays doctor's Medical Council Registration (MCR) number, including the inherited staff details.
     */
    @Override
    public void displayHuman() {
        super.displayHuman();
        System.out.println("\n\nROLE: DOCTOR");
        System.out.println("---------------------------------------------------------------------");
        System.out.format("MCR: %s%n", mcr);
        System.out.println("=====================================================================");
    }
}
