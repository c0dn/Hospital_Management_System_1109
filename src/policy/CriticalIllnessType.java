package policy;

/**
 *  Represents different types of severe medical conditions covered
 *  by critical illness insurance policies.
 *  These conditions typically have a significant impact on
 *  a person's health and require expensive medical treatments.
 */
public enum CriticalIllnessType {
    /** A heart attack, also known as myocardial infarction. */
    HEART_ATTACK,
    /** A stroke, which occurs due to interrupted blood flow to the brain. */
    STROKE,
    /** Coronary artery bypass surgery, a procedure to restore blood flow to the heart. */
    CORONARY_ARTERY_BYPASS_SURGERY,
    /** Major cancers, including malignant tumors. */
    MAJOR_CANCERS,
    /** Kidney failure, requiring dialysis or transplant. */
    KIDNEY_FAILURE,
    /** Major head trauma, resulting in significant brain injury. */
    MAJOR_HEAD_TRAUMA,
    /** Major organ or bone marrow transplantation. */
    MAJOR_ORGAN_BONE_MARROW_TRANSPLANTATION,
    /** Multiple sclerosis, a chronic autoimmune disease affecting the nervous system. */
    MULTIPLE_SCLEROSIS,
    /** Fulminant hepatitis, a severe form of liver inflammation. */
    FULMINANT_HEPATITIS,
    /** Primary pulmonary hypertension, a type of high blood pressure affecting the lungs. */
    PRIMARY_PULMONARY_HYPERTENSION,
    /** Blindness, the complete or near-complete loss of vision. */
    BLINDNESS,
    /** Alzheimer's disease or severe dementia, causing memory loss and cognitive decline. */
    ALZHEIMERS_DISEASE_SEVERE_DEMENTIA,
    /** Surgery to the aorta, the main artery carrying blood from the heart. */
    SURGERY_TO_THE_AORTA,
    /** Coma, a state of prolonged unconsciousness. */
    COMA,
    /** Deafness, the complete or near-complete loss of hearing. */
    DEAFNESS,
    /** Loss of speech, the inability to speak. */
    LOSS_OF_SPEECH,
    /** Heart valve surgery, a procedure to repair or replace a heart valve. */
    HEART_VALVE_SURGERY,
    /** Major burns, causing significant tissue damage. */
    MAJOR_BURNS,
    /** HIV infection due to blood transfusion or occupational exposure. */
    HIV_BLOOD_TRANSFUSION_OCCUPATIONAL_HIV,
    /** Motor neurone disease, a condition affecting the nervous system. */
    MOTOR_NEURONE_DISEASE,
    /** Parkinson's disease, a neurodegenerative disorder. */
    PARKINSONS_DISEASE,
    /** End-stage liver disease, the final phase of chronic liver damage. */
    END_STAGE_LIVER_DISEASE,
    /** End-stage lung disease, the final phase of chronic lung damage. */
    END_STAGE_LUNG_DISEASE,
    /** Aplastic anaemia, a condition where the body stops producing enough new blood cells. */
    APLASTIC_ANAEMIA,
    /** Muscular dystrophy, a group of diseases causing muscle weakness. */
    MUSCULAR_DYSTROPHY,
    /** Bacterial meningitis, a serious infection of the brain and spinal cord. */
    BACTERIAL_MENINGITIS,
    /** Benign brain tumour, a non-cancerous growth in the brain. */
    BENIGN_BRAIN_TUMOUR,
    /** Viral encephalitis, inflammation of the brain caused by a virus. */
    VIRAL_ENCEPHALITIS,
    /** Angioplasty or invasive coronary artery treatment. */
    ANGIOPLASTY_INVASIVE_CORONARY_ARTERY_TREATMENT,
    /** Poliomyelitis, a viral disease affecting the nervous system. */
    POLIOMYELITIS,
    /** Systemic lupus erythematosus, an autoimmune disease. */
    SYSTEMIC_LUPUS_ERYTHEMATOSUS,
    /** Serious coronary artery disease, a condition affecting the heart's blood vessels. */
    SERIOUS_CORONARY_ARTERY_DISEASE,
    /** Paralysis, the loss of muscle function in part of the body. */
    PARALYSIS,
    /** Apallic syndrome, a condition resulting from severe brain damage. */
    APALLIC_SYNDROME,
    /** Loss of independent existence, the inability to perform daily activities without assistance. */
    LOSS_OF_INDEPENDENT_EXISTENCE,
    /** Progressive scleroderma, a chronic connective tissue disease. */
    PROGRESSIVE_SCLERODERMA
}
