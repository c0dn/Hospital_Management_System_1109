package org.bee.utils.InfoUpdaters;

import org.bee.hms.humans.NokRelation;
import org.bee.hms.humans.Patient;

import java.util.List;

/**
 * Specialized updater for Patient entities.
 * Extends the generic UpdaterBase with patient-specific fields.
 */
public class PatientUpdater extends UpdaterBase<Patient, PatientUpdater> {
    private String contact;
    private String address;
    private Double height;
    private Double weight;
    private List<String> drugAllergies;
    private String nokName;
    private NokRelation nokRelation;
    private String nokAddress;

    private PatientUpdater() {
    }

    /**
     * Creates a new PatientUpdater builder.
     * @return A new PatientUpdater instance
     */
    public static PatientUpdater builder() {
        return new PatientUpdater();
    }

    /**
     * Sets the patient's contact information.
     * @param contact The new contact information
     * @return This PatientUpdater instance for chaining
     */
    public PatientUpdater contact(String contact) {
        this.contact = contact;
        return this;
    }

    /**
     * Sets the patient's address.
     * @param address The new address
     * @return This PatientUpdater instance for chaining
     */
    public PatientUpdater address(String address) {
        this.address = address;
        return this;
    }

    /**
     * Sets the patient's height.
     * @param height The new height value
     * @return This PatientUpdater instance for chaining
     */
    public PatientUpdater height(Double height) {
        this.height = height;
        return this;
    }

    /**
     * Sets the patient's weight.
     * @param weight The new weight value
     * @return This PatientUpdater instance for chaining
     */
    public PatientUpdater weight(Double weight) {
        this.weight = weight;
        return this;
    }

    /**
     * Sets the patient's drug allergies.
     * @param drugAllergies The new drug allergies list
     * @return This PatientUpdater instance for chaining
     */
    public PatientUpdater drugAllergies(List<String> drugAllergies) {
        this.drugAllergies = drugAllergies;
        return this;
    }

    /**
     * Sets the patient's next of kin name.
     * @param nokName The new next of kin name
     * @return This PatientUpdater instance for chaining
     */
    public PatientUpdater nokName(String nokName) {
        this.nokName = nokName;
        return this;
    }

    /**
     * Sets the patient's next of kin relation.
     * @param nokRelation The new next of kin relation
     * @return This PatientUpdater instance for chaining
     */
    public PatientUpdater nokRelation(NokRelation nokRelation) {
        this.nokRelation = nokRelation;
        return this;
    }

    /**
     * Sets the patient's next of kin address.
     * @param nokAddress The new next of kin address
     * @return This PatientUpdater instance for chaining
     */
    public PatientUpdater nokAddress(String nokAddress) {
        this.nokAddress = nokAddress;
        return this;
    }


    private void validateContact() {
        if (contact != null) {
            if (contact.trim().isEmpty()) {
                validationErrors.put("contact", "Contact cannot be empty");
            } else if (!contact.matches("\\d+")) {
                validationErrors.put("contact", "Contact should contain only digits");
            } else {
                validationErrors.remove("contact");
            }
        }
    }

    private void validateAddress() {
        if (address != null) {
            if (address.trim().isEmpty()) {
                validationErrors.put("address", "Address cannot be empty");
            } else {
                validationErrors.remove("address");
            }
        }
    }

    private void validateHeight() {
        if (height != null) {
            if (height <= 0 || height > 3.0) {
                validationErrors.put("height", "Height must be between 0 and 3.0 meters");
            } else {
                validationErrors.remove("height");
            }
        }
    }

    private void validateWeight() {
        if (weight != null) {
            if (weight <= 0 || weight > 500.0) {
                validationErrors.put("weight", "Weight must be between 0 and 500.0 kg");
            } else {
                validationErrors.remove("weight");
            }
        }
    }

    private void validateDrugAllergies() {
        if (drugAllergies != null) {
            boolean hasEmptyAllergy = false;
            for (String allergy : drugAllergies) {
                if (allergy.trim().isEmpty()) {
                    hasEmptyAllergy = true;
                    break;
                }
            }

            if (hasEmptyAllergy) {
                validationErrors.put("drugAllergies", "Drug allergies cannot contain empty entries");
            } else {
                validationErrors.remove("drugAllergies");
            }
        }
    }

    private void validateNokName() {
        if (nokName != null) {
            if (nokName.trim().isEmpty()) {
                validationErrors.put("nokName", "Next of kin name cannot be empty");
            } else {
                validationErrors.remove("nokName");
            }
        }
    }

    private void validateNokAddress() {
        if (nokAddress != null) {
            if (nokAddress.trim().isEmpty()) {
                validationErrors.put("nokAddress", "Next of kin address cannot be empty");
            } else {
                validationErrors.remove("nokAddress");
            }
        }
    }


    /**
     * Applies all patient-specific updates to the patient entity.
     * Implemented from the base class template method.
     *
     * @param patient The patient to update
     */
    @Override
    protected void applySpecificUpdates(Patient patient) {
        ifPresent(contact, patient::setContact);
        ifPresent(address, patient::setAddress);
        ifPresent(height, patient::setHeight);
        ifPresent(weight, patient::setWeight);
        ifPresent(drugAllergies, patient::setDrugAllergies);
        ifPresent(nokName, patient::setNokName);
        ifPresent(nokRelation, patient::setNokRelation);
        ifPresent(nokAddress, patient::setNokAddress);
    }
}