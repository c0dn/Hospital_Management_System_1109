package org.bee.hms.policy;

/**
 * Enum representing the various types of benefits available under an insurance policy.
 * <p>
 * Each value in this enum corresponds to a specific category of medical care or treatment that can be covered by a policy.
 * These types are used to classify various medical expenses that may be reimbursed or paid for by an insurance provider.
 * </p>
 */
public enum BenefitType {
    /** Coverage for inpatient admissions to a hospital. */
    HOSPITALIZATION,      // Inpatient admissions

    /** Coverage for all surgical procedures (e.g., surgeries, operations). */
    SURGERY,             // All surgical procedures (0-section)

    /** Coverage for outpatient treatments such as clinic visits or infusions. */
    OUTPATIENT_TREATMENTS, // Clinic visits, infusions

    /** Coverage for dental procedures and treatments. */
    DENTAL,

    /** Coverage related to maternity care, including pregnancy and childbirth. */
    MATERNITY,

    /** Coverage for diagnoses related to critical illnesses, such as cancer or major diseases (e.g., C00-C97). */
    CRITICAL_ILLNESS,    // Diagnoses (e.g., C00-C97)

    /** Coverage for oncology treatments such as chemotherapy, radiation, and nuclear medicine. */
    ONCOLOGY_TREATMENTS, // Radiation(7), Nuclear Med(6), Chemo

    /** Coverage for diagnostic imaging procedures (e.g., X-rays, MRIs, CT scans). */
    DIAGNOSTIC_IMAGING,  // Section 5 procedures

    /** Coverage for medication administration, such as drug infusions and injections. */
    MEDICATION_ADMIN,     // Section 3 (drug infusions)

    /** Coverage for minor surgical procedures that are less complex and invasive. */
    MINOR_SURGERY,

    /** Coverage for major surgical procedures, typically requiring more complex intervention. */
    MAJOR_SURGERY,

    /** Coverage for preventive care, including screenings and vaccinations to prevent illness. */
    PREVENTIVE_CARE,

    /** Coverage for chronic conditions like diabetes, hypertension, and heart disease. */
    CHRONIC_CONDITIONS,

    /** Coverage for acute conditions, including illnesses or conditions that develop suddenly. */
    ACUTE_CONDITIONS,

    /** Coverage for accidents, including injuries resulting from unexpected events. */
    ACCIDENT

}
