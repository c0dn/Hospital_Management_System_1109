package policy;

import medical.Medication;

import java.math.BigDecimal;

public interface ClaimableItem {
    BigDecimal getCharges();
    BenefitType resolveBenefitType(boolean isInpatient);
    String getBenefitDescription(boolean isInpatient);

    default String getDiagnosisCode() {
        return null;
    }

    default String getProcedureCode() {
        return null;
    }

    default Medication getMedication() {
        return null;
    }

    default AccidentType getAccidentSubType() {
        return null;
    }

}
