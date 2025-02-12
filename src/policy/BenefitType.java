package policy;

/**
 * Defines the various types of benefits that an insurance policy may cover.
 * <p>
 *     The available benefit types include various medical treatments and services such as inpatient admissions,
 *     surgeries, outpatient treatments, dental care, maternity, critical illnesses, oncology treatments
 * </p>
 * <ul>
 *     <li>{@link #HOSPITALIZATION} - Inpatient admissions.</li>
 *     <li>{@link #SURGERY} - All surgical procedures, including surgeries such as 0-section.</li>
 *     <li>{@link #OUTPATIENT_TREATMENTS} - Clinic visits and treatments such as infusions.</li>
 *     <li>{@link #DENTAL} - Dental care procedures.</li>
 *     <li>{@link #MATERNITY} - Maternity-related treatments and services.</li>
 *     <li>{@link #CRITICAL_ILLNESS} - Diagnoses related to critical illnesses such as cancer (C00-C97).</li>
 *     <li>{@link #ONCOLOGY_TREATMENTS} - Oncology treatments including radiation, nuclear medicine, and chemotherapy.</li>
 *     <li>{@link #DIAGNOSTIC_IMAGING} - Imaging procedures such as Section 5 procedures.</li>
 *     <li>{@link #MEDICATION_ADMIN} - Administration of medications such as drug infusions (Section 3).</li>
 *     <li>{@link #MINOR_SURGERY} - Minor surgical procedures.</li>
 *     <li>{@link #MAJOR_SURGERY} - Major surgical procedures.</li>
 *     <li>{@link #ACCIDENT} - Treatment for accident-related injuries.</li>
 * </ul>
 */
public enum BenefitType {
    /** Inpatient admissions to a hospital. */
    HOSPITALIZATION,

    /** Surgical procedures (0-section). */
    SURGERY,

    /** Outpatient treatments such as clinic visits and infusions. */
    OUTPATIENT_TREATMENTS,

    /** Dental care treatments and procedures. */
    DENTAL,

    /** Maternity-related treatments and services. */
    MATERNITY,

    /** Diagnoses related to critical illnesses (e.g., C00-C97). */
    CRITICAL_ILLNESS,

    /** Oncology treatments such as radiation, nuclear medicine, and chemotherapy. */
    ONCOLOGY_TREATMENTS,

    /** Diagnostic imaging procedures such as Section 5 procedures. */
    DIAGNOSTIC_IMAGING,

    /** Administration of medications like drug infusions. */
    MEDICATION_ADMIN,

    /** Minor surgical procedures. */
    MINOR_SURGERY,

    /** Major surgical procedures. */
    MAJOR_SURGERY,

    /** Treatments related to accident injuries. */
    ACCIDENT
}