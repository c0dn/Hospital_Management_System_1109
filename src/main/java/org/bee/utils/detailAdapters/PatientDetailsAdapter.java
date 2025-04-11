package org.bee.utils.detailAdapters;

import org.bee.hms.humans.Contact;
import org.bee.hms.humans.Patient;
import org.bee.ui.details.IObjectDetailsAdapter;
import org.bee.ui.views.ObjectDetailsView;
import org.bee.utils.ReflectionHelper;

import java.util.List;
import java.util.Optional;

/**
 * Adapter for displaying Patient details.
 * This adapter configures an ObjectDetailsView to show patient information
 * organized into relevant sections.
 */
public class PatientDetailsAdapter implements IObjectDetailsAdapter<Patient> {

    @Override
    public ObjectDetailsView configureView(ObjectDetailsView view, Patient patient) {
        view.setSectionWidth(70);

        ObjectDetailsView.Section personalSection = view.addSection("Personal Information");
        personalSection.addField(view.createField("Name", "name", "Not available"));
        personalSection.addField(view.createField("NRIF/FIN", "nricFin", "Not available"));
        personalSection.addField(view.createField("Date of Birth", "dateOfBirth", "Not available"));
        personalSection.addField(view.createField("Nationality", "nationality", "Not available"));

        ObjectDetailsView.Section demographicSection = view.addSection("Demographic & Residential Information");

        demographicSection.addField(new ObjectDetailsView.Field<Patient>("Personal Phone", p -> {
            Contact contact = p.getContact();
            if (contact == null) return "Not available";
            String phone = contact.getPersonalPhone();
            return phone != null && !phone.isEmpty() ? phone : "Not available";
        }));

        demographicSection.addField(new ObjectDetailsView.Field<Patient>("Home Phone", p -> {
            Contact contact = p.getContact();
            if (contact == null) return "Not available";
            Optional<?> homePhoneOpt = ReflectionHelper.propertyAccessor("homePhone", Optional.empty()).apply(contact);
            return homePhoneOpt.map(Object::toString).orElse("Not available");
        }));

        demographicSection.addField(new ObjectDetailsView.Field<Patient>("Company Phone", p -> {
            Contact contact = p.getContact();
            if (contact == null) return "Not available";
            Optional<?> companyPhoneOpt = ReflectionHelper.propertyAccessor("companyPhone", Optional.empty()).apply(contact);
            return companyPhoneOpt.map(Object::toString).orElse("Not available");
        }));

        demographicSection.addField(new ObjectDetailsView.Field<Patient>("Email", p -> {
            Contact contact = p.getContact();
            if (contact == null) return "Not available";
            Optional<?> emailOpt = ReflectionHelper.propertyAccessor("email", Optional.empty()).apply(contact);
            return emailOpt.map(Object::toString).orElse("Not available");
        }));

        demographicSection.addField(view.createField("Marital Status", "maritalStatus", "Not available"));
        demographicSection.addField(view.createField("Residential Status", "residentialStatus", "Not available"));
        demographicSection.addField(view.createField("Address", "address", "Not available"));

        ObjectDetailsView.Section medicalSection = view.addSection("Medical Information");
        medicalSection.addField(view.createField("Sex", "sex", "Not available"));
        medicalSection.addField(view.createField("Blood Type", "bloodType", "Not available"));
        medicalSection.addField(new ObjectDetailsView.Field<>("Vaccinated", p -> {
            Boolean vaccinated = (Boolean) ReflectionHelper.propertyAccessor("isVaccinated", null).apply(p);
            return vaccinated != null ? (vaccinated ? "Yes" : "No") : "Not available";
        }));

        ObjectDetailsView.Section patientSection = view.addSection("Patient Details");
        patientSection.addField(view.createField("Patient ID", "patientId", "Not available"));
        patientSection.addField(new ObjectDetailsView.Field<>("Height", p -> {
            Double height = (Double) ReflectionHelper.propertyAccessor("height", null).apply(p);
            return height != null ? String.format("%.2f m", height) : "Not available";
        }));
        patientSection.addField(new ObjectDetailsView.Field<>("Weight", p -> {
            Double weight = (Double) ReflectionHelper.propertyAccessor("weight", null).apply(p);
            return weight != null ? String.format("%.2f kg", weight) : "Not available";
        }));
        patientSection.addField(new ObjectDetailsView.Field<>("Drug Allergies", p -> {
            List<?> allergies = (List<?>) ReflectionHelper.propertyAccessor("drugAllergies", null).apply(p);
            if (allergies == null || allergies.isEmpty()) {
                return "No drug allergies";
            }
            return allergies.toString().replace("[", "").replace("]", "");
        }));

        patientSection.addField(view.createField("Occupation", "occupation", "Not specified"));
        patientSection.addField(view.createField("Company Name", "companyName", "Not specified"));
        patientSection.addField(view.createField("Company Address", "companyAddress", "Not specified"));

        ObjectDetailsView.Section nokSection = view.addSection("Next of Kin (NOK) Details");
        nokSection.addField(new ObjectDetailsView.Field<>("Name", p ->
                ReflectionHelper.propertyAccessor("nokName", "Not specified").apply(p)
        ));
        nokSection.addField(new ObjectDetailsView.Field<>("Relationship", p -> {
            Object relation = ReflectionHelper.propertyAccessor("nokRelation", null).apply(p);
            return relation != null ? relation.toString() : "Not specified";
        }));
        nokSection.addField(new ObjectDetailsView.Field<>("Address", p ->
                ReflectionHelper.propertyAccessor("nokAddress", "Not specified").apply(p)
        ));

        return view;
    }

    @Override
    public String getObjectTypeName() {
        return "Patient";
    }
}