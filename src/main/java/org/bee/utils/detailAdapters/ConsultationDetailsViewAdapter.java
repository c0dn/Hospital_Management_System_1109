package org.bee.utils.detailAdapters;

import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Patient;
import org.bee.hms.medical.Consultation;
import org.bee.hms.medical.DiagnosticCode;
import org.bee.hms.medical.ProcedureCode;
import org.bee.ui.details.IDetailsViewAdapter;
import org.bee.ui.views.DetailsView;
import org.bee.utils.ReflectionHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Adapter for displaying Consultation details using DetailsView.
 * This adapter uses reflection to access consultation data fields.
 */
public class ConsultationDetailsViewAdapter implements IDetailsViewAdapter<Consultation> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public DetailsView<Consultation> configureView(DetailsView<Consultation> view, Consultation consultation) {
        // Basic Case Information
        addDetailsFromProperties(view, "Case Information", consultation, new String[][] {
                {"Case ID", "consultationId", "Not available"}
        });

        // Format dates properly
        LocalDateTime appointmentDate = (LocalDateTime) ReflectionHelper.propertyAccessor("appointmentDate", null).apply(consultation);
        if (appointmentDate != null) {
            view.addDetail("Case Information", "Appointment Date", DATE_FORMATTER.format(appointmentDate));
        }

        LocalDateTime consultationTime = (LocalDateTime) ReflectionHelper.propertyAccessor("consultationTime", null).apply(consultation);
        if (consultationTime != null) {
            view.addDetail("Case Information", "Consultation Time", DATE_FORMATTER.format(consultationTime));
        }

        // Handle enum types with proper formatting
        Object typeObj = ReflectionHelper.propertyAccessor("type", null).apply(consultation);
        if (typeObj != null) {
            view.addDetail("Case Information", "Type", typeObj.toString());
        } else {
            view.addDetail("Case Information", "Type", "Not specified");
        }

        Object statusObj = ReflectionHelper.propertyAccessor("status", null).apply(consultation);
        if (statusObj != null) {
            view.addDetail("Case Information", "Status", statusObj.toString());
        } else {
            view.addDetail("Case Information", "Status", "Not set");
        }

        // Visit reason if available
        String visitReason = ReflectionHelper.stringPropertyAccessor("visitReason", null).apply(consultation);
        if (visitReason != null && !visitReason.isEmpty()) {
            view.addDetail("Case Information", "Visit Reason", visitReason);
        }

        // Patient Information - using nested property accessor
        Patient patient = consultation.getPatient();
        if (patient != null) {
            view.addDetail("Patient Information", "Patient ID", patient.getPatientId());
            view.addDetail("Patient Information", "Name", patient.getName());
            view.addDetail("Patient Information", "NRIC/FIN", patient.getNricFin());
            view.addDetail("Patient Information", "Age", String.valueOf(patient.getAge()));
        } else {
            view.addDetail("Patient Information", "Patient", "Not assigned");
        }

        // Medical Details - handling potentially null values
        view.addDetail("Medical Details", "Diagnosis",
                ReflectionHelper.stringPropertyAccessor("diagnosis", "No diagnosis recorded").apply(consultation));

        String medicalHistory = ReflectionHelper.stringPropertyAccessor("medicalHistory", null).apply(consultation);
        if (medicalHistory != null && !medicalHistory.isEmpty()) {
            view.addDetail("Medical Details", "Medical History", medicalHistory);
        }

        // Add diagnostic codes with proper handling for collections
        List<DiagnosticCode> diagnosticCodes = consultation.getDiagnosticCodes();
        if (diagnosticCodes != null && !diagnosticCodes.isEmpty()) {
            StringBuilder codesStr = new StringBuilder();
            for (int i = 0; i < diagnosticCodes.size(); i++) {
                if (i >= 0) codesStr.append("\n    ");
                codesStr.append(i+1).append(". ").append(diagnosticCodes.get(i).getDCode());
            }
            view.addDetail("Medical Details", "Diagnostic Codes", codesStr.toString());
        }

        // Add procedure codes
        List<ProcedureCode> procedureCodes = consultation.getProcedureCodes();
        if (procedureCodes != null && !procedureCodes.isEmpty()) {
            StringBuilder codesStr = new StringBuilder();
            for (int i = 0; i < procedureCodes.size(); i++) {
                if (i >= 0) codesStr.append("\n    ");
                codesStr.append(i+1).append(". ").append(procedureCodes.get(i).getPCode());
            }
            view.addDetail("Medical Details", "Procedure Codes", codesStr.toString());
        }

        // Add prescriptions
        Map<?, Integer> prescriptions = consultation.getPrescriptions();
        if (prescriptions != null && !prescriptions.isEmpty()) {
            StringBuilder rxStr = new StringBuilder();
            int i = 0;
            for (Map.Entry<?, Integer> entry : prescriptions.entrySet()) {
                if (i >= 0) rxStr.append("\n    ");
                String[] parts = entry.getKey().toString().split("\\(")[0].split(":");
                String drugInfo = parts[0].trim() + ": " + parts[1].trim();
                rxStr.append(i+1).append(". ").append(drugInfo)
                        .append(" - Qty: ").append(entry.getValue());
                i++;
            }
            view.addDetail("Medical Details", "Prescriptions", rxStr.toString());
        }

        // Add notes if available
        String notes = ReflectionHelper.stringPropertyAccessor("notes", null).apply(consultation);
        if (notes != null && !notes.isEmpty()) {
            view.addDetail("Medical Details", "Notes", notes);
        }

        String instructions = ReflectionHelper.stringPropertyAccessor("instructions", null).apply(consultation);
        if (instructions != null && !instructions.isEmpty()) {
            view.addDetail("Medical Details", "Instructions", instructions);
        }

        // Follow-up Information
        LocalDateTime followUpDate = (LocalDateTime) ReflectionHelper.propertyAccessor("followUpDate", null).apply(consultation);
        if (followUpDate != null) {
            view.addDetail("Follow-up", "Date", DATE_FORMATTER.format(followUpDate));
        } else {
            view.addDetail("Follow-up", "Status", "No follow-up scheduled");
        }

        // Doctor Information
        Doctor doctor = consultation.getDoctor();
        if (doctor != null) {
            view.addDetail("Doctor Information", "Doctor Name", doctor.getName());
            view.addDetail("Doctor Information", "Department",
                    ReflectionHelper.stringPropertyAccessor("department", "Not specified").apply(doctor));
            view.addDetail("Doctor Information", "Staff ID", doctor.getStaffId());
        } else {
            view.addDetail("Doctor Information", "Doctor", "Not assigned");
        }

        return view;
    }


    @Override
    public String getObjectTypeName() {
        return "Consultation";
    }
}