package org.bee.hms.medical;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a treatment provided for an outpatient case. A treatment consists of a unique
 * treatment ID, a treatment name, status, start and end dates, notes, cost, and a list of procedures
 * associated with it.
 */
public class Treatment {
    /** Static counter to generate unique treatment IDs. */
    private static int count = 0;

    /** Unique identifier for the treatment. */
    private int treatmentID;

    /** The outpatient case associated with this treatment. */
    private Consultation consultation;

    /** The name of the treatment. */
    private String treatmentName;

    /** The current status of the treatment. */
    private STATUS status;

    /** The start date of the treatment. */
    private Date startDate;

    /** The end date of the treatment. */
    private Date endDate;

    /** Notes regarding the treatment. */
    private String notes;

    /** The cost of the treatment. */
    private Double cost;

    /** The list of procedures performed as part of the treatment. */
    private List<Procedure> procedures;

    /**
     * A static list to keep track of all Treatment instances.
     */
    private static List<Treatment> instances = new ArrayList<>();

    /**
     * Constructs a new Treatment instance with the specified details.
     *
     * @param outpatientCase the outpatient case associated with the treatment.
     * @param treatmentName  the name of the treatment.
     * @param status         the current status of the treatment.
     * @param startDate      the start date of the treatment.
     * @param endDate        the end date of the treatment.
     * @param notes          notes regarding the treatment.
     * @param cost           the cost of the treatment.
     * @param procedures     a list of procedures associated with the treatment; if null,
     *                       an empty list is initialized.
     */
    public Treatment(Consultation consultation, String treatmentName, STATUS status,
                     Date startDate, Date endDate,
                     String notes, Double cost, List<Procedure> procedures) {
        setTreatmentID(count++);
        this.consultation= consultation;
        this.treatmentName = treatmentName;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.notes = notes;
        this.cost = cost;
        this.procedures = (procedures != null) ? procedures : new ArrayList<>();

        instances.add(this);
    }

    /**
     * Returns a formatted string representation of the treatment details.
     *
     * @return a string containing the treatment's details.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n\nTreatment Details:\n");
        stringBuilder.append(String.format("Treatment ID:         %s\n", treatmentID));
        stringBuilder.append(String.format("Name:                 %s\n", treatmentName));
        stringBuilder.append(String.format("Status:               %s\n", status));
        stringBuilder.append(String.format("Start Date:           %s\n", startDate));
        stringBuilder.append(String.format("End Date:             %s\n", endDate));
        stringBuilder.append(String.format("Notes:                %s\n", (notes.equals("") ? "null" : notes)));
        stringBuilder.append(String.format("Costs:                $ %s", cost));

        String string = stringBuilder.toString();
        return string;
    }

    /**
     * Retrieves a list of all Treatment instances.
     *
     * @return a list containing all treatments.
     */
    public static List<Treatment> getAllTreatments() {
        return instances;
    }

    /**
     * Default constructor for Treatment, provided for cases where a Treatment
     * instance needs to be created without initializing its attributes immediately.
     */
    public Treatment() {
    }

    /**
     * Gets the list of procedures associated with this treatment.
     *
     * @return a list of procedures.
     */
    public List<Procedure> getProcedures() {
        return procedures;
    }

    /**
     * Sets the list of procedures for this treatment.
     *
     * @param procedures a list of procedures to be associated with the treatment.
     */
    public void setProcedures(List<Procedure> procedures) {
        this.procedures = procedures;
    }

    /**
     * Gets the outpatient case associated with this treatment.
     *
     * @return the outpatient case.
     */
    public Consultation getConsultation() {
        return consultation;
    }

    /**
     * Attach the outpatient case for this treatment. If the treatment is already associated with an
     * outpatient case, it is removed from that case before being added to the new one.
     *
     * @param outpatientCase the outpatient case to be associated with the treatment.
     */
    public void setOutpatientCase(Consultation consultation) {
        if (this.consultation != null) {
            this.consultation.removeTreatment(this);
        }
        this.consultation = Consultation;
        consultation.addTreatment(this);
    }

    /**
     * Gets the unique identifier for this treatment.
     *
     * @return the treatment ID.
     */
    public int getTreatmentID() {
        return treatmentID;
    }

    /**
     * Sets the unique identifier for this treatment.
     *
     * @param treatmentID the treatment ID to set.
     */
    public void setTreatmentID(int treatmentID) {
        this.treatmentID = treatmentID;
    }

    /**
     * Gets the name of the treatment.
     *
     * @return the treatment name.
     */
    public String getTreatmentName() {
        return treatmentName;
    }

    /**
     * Sets the name of the treatment.
     *
     * @param treatmentName the treatment name to set.
     */
    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    /**
     * Gets the current status of the treatment.
     *
     * @return the treatment status a {@link STATUS} enum.
     */
    public STATUS getStatus() {
        return status;
    }

    /**
     * Sets the current status of the treatment.
     *
     * @param status the treatment status to set.
     */
    public void setStatus(STATUS status) {
        this.status = status;
    }

    /**
     * Gets the start date of the treatment.
     *
     * @return the start date.
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date of the treatment.
     *
     * @param startDate the start date to set.
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date of the treatment.
     *
     * @return the end date.
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date of the treatment.
     *
     * @param endDate the end date to set.
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets the notes associated with the treatment.
     *
     * @return the treatment notes.
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the notes for the treatment.
     *
     * @param notes the treatment notes to set.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Gets the cost of the treatment.
     *
     * @return the treatment cost.
     */
    public Double getCost() {
        return cost;
    }

    /**
     * Sets the cost of the treatment.
     *
     * @param cost the treatment cost to set.
     */
    public void setCost(Double cost) {
        this.cost = cost;
    }

    /**
     * Adds a procedure to the treatment's list of procedures if it is not already present.
     *
     * @param procedure the procedure to add.
     */
    public void addProcedure(Procedure procedure) {
        if (!procedures.contains(procedure)) {
            procedures.add(procedure);
        }
    }

    /**
     * Searches for a treatment by its unique ID.
     *
     * @param id the treatment ID to search for.
     * @return the Treatment with the matching ID, or null if no such treatment exists.
     */
    public static Treatment searchTreatmentByID(int id) {
        for (Treatment treatment : instances) {
            if (treatment.getTreatmentID() == (id)) {
                return treatment;
            }
        }
        return null;
    }

    /**
     * Retrieves all treatments associated with a specific outpatient case.
     *
     * @param consultation the outpatient case for which treatments are to be retrieved.
     * @return a list of treatments linked to the given outpatient case.
     */
    public static List<Treatment> getAllTreatmentsByCase(Consultation consultation) {
        List<Treatment> allTreatments = new ArrayList<>();
        for (Treatment i : instances) {
            if (i.getConsultation().equals(consultation)) {
                allTreatments.add(i);
            }
        }
        return allTreatments;
    }

}
