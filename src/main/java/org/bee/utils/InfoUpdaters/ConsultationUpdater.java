package org.bee.utils.InfoUpdaters;

import jdk.jshell.Diag;
import org.bee.hms.medical.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConsultationUpdater extends UpdaterBase<Consultation, ConsultationUpdater> {

    private List<DiagnosticCode> diagnosticCodes;
    private List<ProcedureCode> procedureCodes;
    private Map<Medication, Integer> prescriptions;
    private String notes;
    private String medicalHistory;
    private String diagnosis;
    private String visitReason;
    private LocalDateTime followUpDate;
    private String instructions;
    private ArrayList<Treatment> treatments;
    private ArrayList<LabTest> labTests;

    private ConsultationUpdater() {
    }

    public static ConsultationUpdater builder() { return new ConsultationUpdater(); }

    public ConsultationUpdater diagnosticCodes(List<DiagnosticCode> diagnosticCodes) {
        this.diagnosticCodes = diagnosticCodes;
        return this;
    }

    public ConsultationUpdater procedureCodes(List<ProcedureCode> procedureCodes) {
        this.procedureCodes = procedureCodes;
        return this;
    }

    public ConsultationUpdater prescriptions(Map<Medication, Integer> prescriptions) {
        this.prescriptions = prescriptions;
        return this;
    }

    public ConsultationUpdater notes(String notes) {
        this.notes = notes;
        return this;
    }

    public ConsultationUpdater medicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
        return this;
    }

    public ConsultationUpdater diagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
        return this;
    }

    public ConsultationUpdater visitReason(String visitReason) {
        this.visitReason = visitReason;
        return this;
    }

    public ConsultationUpdater followUpDate(LocalDateTime followUpDate) {
        this.followUpDate = followUpDate;
        return this;
    }

    public ConsultationUpdater instructions(String instructions) {
        this.instructions = instructions;
        return this;
    }

    public ConsultationUpdater treatments(ArrayList<Treatment> treatments) {
        this.treatments = treatments;
        return this;
    }

    public ConsultationUpdater labTests(ArrayList<LabTest> labTests) {
        this.labTests = labTests;
        return this;
    }

    @Override
    protected void applySpecificUpdates(Consultation consultation) {
        ifPresent(diagnosticCodes, consultation::setDiagnosticCodes);
        ifPresent(procedureCodes, consultation::setProcedureCodes);
        ifPresent(prescriptions, consultation::setPrescriptions);
        ifPresent(notes, consultation::setNotes);
        ifPresent(medicalHistory, consultation::setMedicalHistory);
        ifPresent(diagnosis, consultation::setDiagnosis);
        ifPresent(visitReason, consultation::setVisitReason);
        ifPresent(followUpDate, consultation::setFollowUpDate);
        ifPresent(instructions, consultation::setInstructions);
        ifPresent(treatments, consultation::setTreatments);
        ifPresent(labTests, consultation::setLabtests);
    }
}
