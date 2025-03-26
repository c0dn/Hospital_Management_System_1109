package org.bee.hms.medical;

import org.bee.hms.humans.Patient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a feedback entry provided by a patient regarding their medical
 * care and service experience.
 * <p>
 * This class stores details about a patient's feedback, including ratings for
 * medical care
 * and service, the date of submission, and any additional remarks.
 * <p>
 * Each feedback entry is uniquely identified by a {@code feedbackID}, which is
 * automatically
 * assigned using a static counter.
 * <p>
 * Instances of this class are stored in a static list {@code instances} for
 * retrieval
 * and management.
 */

public class Feedback {
    private static int count = 0;
    private int feedbackID;
    private Patient patient;
    private int medicalCareRating;
    private int serviceRating;
    private Date dateStamp;
    private String remarks;

    /**
     * Returns a formatted string representation of the feedback details.
     *
     * @return A formatted string containing feedback details.
     */
    @Override
    public String toString() {
        String stringBuilder = String.format("Feedback ID:             %s\n", feedbackID) +
                String.format("Medical Care Rating:     %s\n", medicalCareRating) +
                String.format("Service Rating:          %s\n", serviceRating) +
                String.format("Date:                    %s\n", dateStamp) +
                String.format("Remarks:                 %s\n", remarks) +
                "----------------------------\n";

        return stringBuilder;
    }

    /**
     * A static list that stores all instances of the class
     * The list keeps track of all feedback objects created in the system.
     */
    private static final List<Feedback> instances = new ArrayList<>();

    /**
     * Retrieves a list of all feedback instances.
     *
     * @return A list of all feedback objects.
     */
    public static List<Feedback> getAllFeedbacks() {
        return instances;
    }

    /**
     * Retrieves all feedback provided by a specific patient.
     *
     * @param patient The patient whose feedback is to be retrieved.
     * @return A list of feedback objects associated with the given patient.
     */
    public static List<Feedback> getAllFeedbacksByPatient(Patient patient) {
        List<Feedback> allFeedbacks = new ArrayList<>();
        for (Feedback i : instances) {
            if (i.getPatient().equals(patient)) {
                allFeedbacks.add(i);
            }
        }
        return allFeedbacks;
    }

    /**
     * Constructs a new Feedback instance and adds it to the list of feedbacks.
     *
     * @param patient           The patient who provided the feedback.
     * @param medicalCareRating The rating for medical care (e.g., 1-5).
     * @param serviceRating     The rating for service (e.g., 1-5).
     * @param dateStamp         The date the feedback was given.
     * @param remarks           Additional comments provided by the patient.
     */
    public Feedback(Patient patient, int medicalCareRating, int serviceRating,
                    Date dateStamp, String remarks) {
        setFeedbackID(count++);
        this.patient = patient;
        this.dateStamp = dateStamp;
        this.remarks = remarks;
        this.medicalCareRating = medicalCareRating;
        this.serviceRating = serviceRating;

        instances.add(this);
    }

    /**
     * Searches for a feedback entry by its ID.
     *
     * @param feedbackID The ID of the feedback to search for.
     * @return The feedback object with the matching ID, or null if not found.
     */
    public static Feedback searchFeedbackByID(int feedbackID) {
        for (Feedback feedback : instances) {
            if (feedback.getFeedbackID() == feedbackID) {
                return feedback;
            }
        }
        return null;
    }

    /**
     * Updates the medical care and service ratings of the feedback.
     *
     * @param medicalCareRating The new medical care rating.
     * @param serviceRating     The new service rating.
     */
    public void updateRatings(int medicalCareRating, int serviceRating) {
        this.medicalCareRating = medicalCareRating;
        this.serviceRating = serviceRating;
    }

    /**
     * Updates the remarks/comments of the feedback.
     *
     * @param remarks The new remarks to be updated.
     */
    public void updateComments(String remarks) {
        this.remarks = remarks;
    }

    /**
     * Gets the feedback ID.
     *
     * @return The feedback ID.
     */
    public int getFeedbackID() {
        return feedbackID;
    }

    /**
     * Sets the feedback ID.
     *
     * @param feedbackID The feedback ID to set.
     */
    public void setFeedbackID(int feedbackID) {
        this.feedbackID = feedbackID;
    }

    /**
     * Gets the patient associated with this feedback.
     *
     * @return The patient who provided the feedback.
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Sets the patient associated with this feedback.
     *
     * @param patient The patient to associate with this feedback.
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    /**
     * Gets the medical care rating.
     *
     * @return The medical care rating.
     */
    public int getMedicalCareRating() {
        return medicalCareRating;
    }

    /**
     * Gets the service rating.
     *
     * @return The service rating.
     */
    public int getServiceRating() {
        return serviceRating;
    }

    /**
     * Gets the date when the feedback was provided.
     *
     * @return The date of the feedback.
     */
    public Date getDateStamp() {
        return dateStamp;
    }

    /**
     * Sets the date of the feedback.
     *
     * @param dateStamp The date to set for the feedback.
     */
    public void setDateStamp(Date dateStamp) {
        this.dateStamp = dateStamp;
    }

    /**
     * Gets the remarks/comments provided in the feedback.
     *
     * @return The remarks or comments of the feedback.
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Sets the remarks/comments for the feedback.
     *
     * @param remarks The remarks to set.
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}