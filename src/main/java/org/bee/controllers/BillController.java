package org.bee.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.bee.hms.billing.Bill;
import org.bee.hms.billing.BillBuilder;
import org.bee.hms.billing.BillingStatus;
import org.bee.hms.humans.Patient;
import org.bee.hms.medical.Consultation;
import org.bee.hms.medical.Visit;
import org.bee.hms.policy.InsurancePolicy;

/**
 * Controller class for managing bills in the hospital management system.
 * Handles creating, retrieving, and processing bills.
 * Extends BaseController to provide JSON persistence.
 */
public class BillController extends BaseController<Bill> {

    /**
     * Singleton instance of the BillController.
     */
    private static BillController instance;

    // Singleton dependencies
    /**
     * Singleton instance of HumanController
     */
    private static final HumanController humanController = HumanController.getInstance();

    /**
     * Singleton instance of PolicyController
     */
    private static final PolicyController policyController = PolicyController.getInstance();

    /**
     * Singleton instance of VisitController
     */
    private static final VisitController visitController = VisitController.getInstance();

    /**
     * Singleton instance of ConsultationController
     */
    private static final ConsultationController consultationController = ConsultationController.getInstance();

    /**
     * Private constructor to enforce singleton pattern.
     */
    protected BillController() {
        super();
    }

    /**
     * Gets the singleton instance of BillController.
     *
     * @return The singleton BillController instance
     */
    public static synchronized BillController getInstance() {
        if (instance == null) {
            instance = new BillController();
        }
        return instance;
    }

    /**
     * Returns the file path for bills
     *
     * @return A String representing the path to the bills data file
     */
    @Override
    protected String getDataFilePath() {
        return DATABASE_DIR + "/bills.txt";
    }

    /**
     * Returns the Class for the Bill entity.
     *
     * @return The Class for Bill
     */
    @Override
    protected Class<Bill> getEntityClass() {
        return Bill.class;
    }

    /**
     * Generates initial bill data for the healthcare management system
     * This method creates bills for all patients based on their visits and consultations and policies
     */
    @Override
    protected void generateInitialData() {
        System.out.println("Generating initial bill data...");

        List<Patient> patients = humanController.getAllPatients();

        if (patients.isEmpty()) {
            System.err.println("No patients available to generate bills");
            return;
        }

        for (Patient patient : patients) {
            List<InsurancePolicy> policies = policyController.getAllPoliciesForPatient(patient);

            List<Visit> patientVisits = visitController.getVisitsForPatient(patient);
            for (Visit visit : patientVisits) {
                createBillFromVisit(patient, visit, policies);
            }

            List<Consultation> patientConsultations = consultationController.getAllOutpatientCases().stream()
                    .filter(c -> c.getPatient().equals(patient))
                    .toList();

            for (Consultation consultation : patientConsultations) {
                createBillFromConsultation(patient, consultation, policies);
            }
        }

        System.out.println("Generated " + items.size() + " bills.");
    }

    /**
     * Creates a bill from a patient visit and optionally processes an insurance claim.
     *
     * @param patient   The patient associated with the visit
     * @param visit     The visit to create a bill for
     * @param policies  List of patient's insurance policies
     * @return The created Bill
     */
    private Bill createBillFromVisit(Patient patient, Visit visit, List<InsurancePolicy> policies) {
        BillBuilder billBuilder = new BillBuilder()
                .withPatient(patient)
                .withVisit(visit);

        if (!policies.isEmpty()) {
            billBuilder.withInsurancePolicy(policies.getFirst());
        }

        Bill bill = billBuilder.build();


        items.add(bill);
        return bill;
    }

    /**
     * Creates a bill from a patient consultation and optionally processes an insurance claim.
     *
     * @param patient       The patient associated with the consultation
     * @param consultation  The consultation to create a bill for
     * @param policies      List of patient's insurance policies
     * @return The created Bill
     */
    private Bill createBillFromConsultation(Patient patient, Consultation consultation, List<InsurancePolicy> policies) {
        BillBuilder billBuilder = new BillBuilder()
                .withPatient(patient)
                .withConsultation(consultation);

        if (!policies.isEmpty()) {
            billBuilder.withInsurancePolicy(policies.getFirst());
        }

        Bill bill = billBuilder.build();


        items.add(bill);
        return bill;
    }


    /**
     * Finds bills for a specific patient.
     *
     * @param patient The patient to find bills for
     * @return A list of bills for the patient
     */
    public List<Bill> getBillsForPatient(Patient patient) {
        return getAllItems().stream()
                .filter(bill -> bill.getPatient().equals(patient))
                .collect(Collectors.toList());
    }

    /**
     * Finds bills with a specific billing status.
     *
     * @param status The billing status to filter by
     * @return A list of bills with the specified status
     */
    public List<Bill> getBillsByStatus(BillingStatus status) {
        return getAllItems().stream()
                .filter(bill -> bill.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Updates the status of a bill.
     *
     * @param billId The ID of the bill to update
     * @param newStatus The new status to set
     * @return true if the bill was successfully updated, false otherwise
     */
    public boolean updateBillStatus(String billId, BillingStatus newStatus) {
        // TODO: Implement bill ID lookup and status update
        // This might require adding a method to Bill class to get its unique identifier
        return false;
    }

    /**
     * Calculates the total amount of bills for a specific patient.
     *
     * @param patient The patient to calculate total bills for
     * @return The total bill amount for the patient
     */
    public BigDecimal getTotalBillAmountForPatient(Patient patient) {
        return getBillsForPatient(patient).stream()
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}