package policy;

public enum BenefitType {
    HOSPITALIZATION,      // Inpatient admissions
    SURGERY,             // All surgical procedures (0-section)
    OUTPATIENT_TREATMENTS, // Clinic visits, infusions
    DENTAL,
    MATERNITY,
    CRITICAL_ILLNESS,    // Diagnoses (e.g., C00-C97)
    ONCOLOGY_TREATMENTS, // Radiation(7), Nuclear Med(6), Chemo
    DIAGNOSTIC_IMAGING,  // Section 5 procedures
    MEDICATION_ADMIN,     // Section 3 (drug infusions)
    MINOR_SURGERY,
    MAJOR_SURGERY,
    ACCIDENT,

}
