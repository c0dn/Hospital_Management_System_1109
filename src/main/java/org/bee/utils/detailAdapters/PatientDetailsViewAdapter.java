package org.bee.utils.detailAdapters;

import org.bee.hms.humans.Patient;
import org.bee.ui.details.IDetailsViewAdapter;
import org.bee.ui.views.DetailsView;
import org.bee.utils.ReflectionHelper;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

/**
 * Adapter for displaying Patient details using DetailsView.
 * This adapter uses reflection to access patient data fields.
 */
public class PatientDetailsViewAdapter implements IDetailsViewAdapter<Patient> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public DetailsView<Patient> configureView(DetailsView<Patient> view, Patient patient) {
        addDetailsFromProperties(view, "Personal Information", patient, new String[][] {
                {"Name", "name", "Not available"},
                {"NRIC/FIN", "nricFin", "Not available"},
                {"Patient ID", "patientId", "Not available"},
                {"Nationality", "nationality", "Not specified"}
        });

        view.addDetail("Personal Information", "Age", String.valueOf(patient.getAge()));

        Function<Patient, String> dobFormatter = p ->
                p.getDOB() != null ? p.getDOB().format(DATE_FORMATTER) : "Not available";
        view.addDetail("Personal Information", "Date of Birth", dobFormatter.apply(patient));

        Function<Patient, String> sexFormatter = p ->
                p.getSex() != null ? p.getSex().toString() : "Not specified";
        view.addDetail("Personal Information", "Gender", sexFormatter.apply(patient));

        view.addDetail("Contact Information", "Phone",
                ReflectionHelper.nestedStringPropertyAccessor("contact", "personalPhone", "Not provided").apply(patient));
        view.addDetail("Contact Information", "Address",
                ReflectionHelper.stringPropertyAccessor("address", "No address on file").apply(patient));

        addDetailsFromProperties(view, "Medical Information", patient, new String[][] {
                {"Blood Type", "bloodType", "Unknown"}
        });

        Function<Patient, String> vaccinationFormatter = p -> p.isVaccinated() ? "Yes" : "No";
        view.addDetail("Medical Information", "Vaccinated", vaccinationFormatter.apply(patient));

        double height = patient.getHeight();
        double weight = patient.getWeight();
        if (height > 0) {
            view.addDetail("Medical Information", "Height", String.format("%.2f m", height));
        }
        if (weight > 0) {
            view.addDetail("Medical Information", "Weight", String.format("%.2f kg", weight));
        }

        List<String> allergies = patient.getDrugAllergies();
        if (allergies != null && !allergies.isEmpty()) {
            view.addDetail("Medical Information", "Drug Allergies", String.join(", ", allergies));
        } else {
            view.addDetail("Medical Information", "Drug Allergies", "None recorded");
        }

        String nokName = ReflectionHelper.stringPropertyAccessor("nokName", null).apply(patient);
        if (nokName != null && !nokName.isEmpty()) {
            view.addDetail("Next of Kin", "Name", nokName);
            view.addDetail("Next of Kin", "Relationship",
                    ReflectionHelper.stringPropertyAccessor("nokRelation", "Not specified").apply(patient));
            view.addDetail("Next of Kin", "Address",
                    ReflectionHelper.stringPropertyAccessor("nokAddress", "Not provided").apply(patient));
        }

        String occupation = ReflectionHelper.stringPropertyAccessor("occupation", null).apply(patient);
        if (occupation != null && !occupation.isEmpty()) {
            view.addDetail("Professional Information", "Occupation", occupation);
            view.addDetail("Professional Information", "Company",
                    ReflectionHelper.stringPropertyAccessor("companyName", "Not specified").apply(patient));
            view.addDetail("Professional Information", "Company Address",
                    ReflectionHelper.stringPropertyAccessor("companyAddress", "Not specified").apply(patient));
        }

        return view;
    }

    @Override
    public String getObjectTypeName() {
        return "Patient";
    }
}