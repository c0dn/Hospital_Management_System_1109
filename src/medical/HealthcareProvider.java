package medical;

/**
 * Represents a healthcare provider such as a hospital or clinic.
 * <p>
 *     This class stores details such as:
 * </p>
 * <ul>
 *     <li>The name of the healthcare provider</li>
 *     <li>The hospital code</li>
 * </ul>
 */

public class HealthcareProvider {
    /** The healthcare provider's name. */
    public String healthcareProviderName;
    /** The hospital code.*/
    public String hospitalCode;

    /**
     * Constructs a HealthcareProvider with the specified details.
     *
     * @param healthcareProviderName The name of the healthcare provider.
     * @param hciCode The hospital code.
     */
    public HealthcareProvider(String healthcareProviderName, String hciCode) {
        this.healthcareProviderName = healthcareProviderName;
        this.hospitalCode = hospitalCode;
    }

    /**
     * Retrieves the healthcare provider's name.
     *
     * @return The name of the healthcare provider.
     */
    public String getHealthcareProviderName() { return healthcareProviderName; }

    /**
     * Retrieves the hospital code.
     *
     * @return The hospital code.
     */
    public String getHospitalCode() { return hospitalCode; }
}
