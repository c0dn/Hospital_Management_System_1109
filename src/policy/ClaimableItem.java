package policy;

import medical.Medication;

import java.math.BigDecimal;

/**
 * Represents an item that can be claimed under an insurance policy.
 * <p>
 *     This interface defines methods to retrieve various details related to a claimable item, including the charges
 *     associated with the item, its benefit type, and additional details like diagnosis or procedure codes.
 * </p>
 */
public interface ClaimableItem {

    /**
     * Retrieves the charges associated with this claimable item.
     * <p>
     *     This value represents the total amount that will be considered for claim purposes.
     * </p>
     *
     * @return A {@link BigDecimal} representing the charges for this claimable item.
     */
    BigDecimal getCharges();

    /**
     * Resolves the benefit type for this claimable item, based on whether the patient is inpatient or outpatient.
     * <p>
     *     The returned {@link BenefitType} will indicate the type of benefit the item is associated with,
     *     such as hospitalization, surgery, or outpatient treatments.
     * </p>
     *
     * @param isInpatient A flag indicating whether the patient is an inpatient.
     * @return The appropriate {@link BenefitType} based on the item and patient status.
     */
    BenefitType resolveBenefitType(boolean isInpatient);

    /**
     * Retrieves a description of the benefit related to this claimable item, based on the patient's inpatient status.
     * <p>
     *     This provides a textual description of the benefit, which could differ depending on whether the patient
     *     is receiving inpatient or outpatient care.
     * </p>
     *
     * @param isInpatient A flag indicating whether the patient is an inpatient.
     * @return A {@link String} containing the benefit description for this claimable item.
     */
    String getBenefitDescription(boolean isInpatient);

    /**
     * Retrieves the diagnosis code associated with this claimable item, if applicable.
     * <p>
     *     This code corresponds to a specific diagnosis, which is often used in medical insurance claims.
     * </p>
     *
     * @return A {@link String} representing the diagnosis code, or {@code null} if no diagnosis code is provided.
     */
    default String getDiagnosisCode() {
        return null;
    }

    /**
     * Retrieves the procedure code associated with this claimable item, if applicable.
     * <p>
     *     This code corresponds to a specific medical procedure performed, which may be required for insurance claims.
     * </p>
     *
     * @return A {@link String} representing the procedure code, or {@code null} if no procedure code is provided.
     */
    default String getProcedureCode() {
        return null;
    }

    /**
     * Retrieves the medication associated with this claimable item, if applicable.
     * <p>
     *     This can be used when the claimable item relates to medications such as drug infusions or prescriptions.
     * </p>
     *
     * @return A {@link Medication} object representing the medication associated with the item, or {@code null} if not applicable.
     */
    default Medication getMedication() {
        return null;
    }

    /**
     * Retrieves the accident subtype related to this claimable item, if applicable.
     * <p>
     *     This could be useful for items associated with accident-related claims, such as fractures, burns, or other accident types.
     * </p>
     *
     * @return An {@link AccidentType} object representing the subtype of the accident, or {@code null} if not applicable.
     */
    default AccidentType getAccidentSubType() {
        return null;
    }
}

