package org.bee.controllers;

import org.bee.hms.humans.Human;
import org.bee.hms.humans.Patient;
import org.bee.hms.policy.InsurancePolicy;
import org.bee.hms.insurance.GovernmentProvider;
import org.bee.hms.insurance.PrivateProvider;
import org.bee.hms.insurance.InsuranceProvider;

import java.util.*;

/**
 * Manages the storage and retrieval of {@link InsuranceProvider} objects
 * Handles loading, saving, and searching of government and private insurance policies.
 * Implemented as a singleton.
 * Extends BaseController to handle JSON persistence.
 */
public class PolicyController extends BaseController<InsurancePolicy> {

    /**
     * Singleton instance of the PolicyController
     */
    private static PolicyController instance;

    /**
     * Reference to the singleton instance of HumanController
     *
     */
    private static final HumanController humanController = HumanController.getInstance();

    /**
     * Insurance provider for government policies
     */
    private static final InsuranceProvider governmentProvider = new GovernmentProvider();

    /**
     * Insurance provider for private policies
     */
    private static final InsuranceProvider privateProvider = new PrivateProvider();

    /**
     * Stores government insurance policies, by patientId
     */
    private static final Map<String, InsurancePolicy> governmentPolicies = new HashMap<>();

    /**
     * Stores private insurance policies, by patientId
     */
    private static final Map<String, InsurancePolicy> privatePolicies = new HashMap<>();

    /**
     * Protected constructor to prevent direct modification
     * Calls the superclass constructor
     */
    protected PolicyController() {
        super();
    }

    /**
     * Returns the singleton instance of PolicyController
     * Creates the instance if it doesn't exist
     *
     * @return The singleton instance of PolicyController
     */
    public static synchronized PolicyController getInstance() {
        if (instance == null) {
            instance = new PolicyController();
        }
        return instance;
    }

    /**
     * Provides the file path for storing policy data
     *
     * @return String representation of the file path
     */
    @Override
    protected String getDataFilePath() {
        return DATABASE_DIR + "/policies.txt";
    }

    /**
     * Specifies the class type for policy
     *
     * @return Class object for InsurancePolicy
     */
    @Override
    protected Class<InsurancePolicy> getEntityClass() {
        return InsurancePolicy.class;
    }

    /**
     * Generates initial mock policy data for all patients
     * This method is for demonstration purposes only
     */
    @Override
    protected void generateInitialData() {
        System.out.println("Generating initial policy data...");
        System.out.println("This is mock data, in the real world we will not have this.");
        System.out.println("it's likely we will get the policy info directly from the insurance provider");

        List<Patient> patients = humanController.getAllPatients();

        for (Patient patient : patients) {
            governmentProvider.getPatientPolicy(patient).ifPresent(policy -> {
                governmentPolicies.put(patient.getPatientId(), policy);
                items.add(policy);
            });

            privateProvider.getPatientPolicy(patient).ifPresent(policy -> {
                privatePolicies.put(patient.getPatientId(), policy);
                items.add(policy);
            });
        }

        System.out.println("Generated " + items.size() + " policies.");
    }

    /**
     * Gets government policy for a patient
     */
    public Optional<InsurancePolicy> getGovernmentPolicy(Patient patient) {
        return Optional.ofNullable(governmentPolicies.get(patient.getPatientId()));
    }

    /**
     * Gets private policy for a patient
     */
    public Optional<InsurancePolicy> getPrivatePolicy(Patient patient) {
        return Optional.ofNullable(privatePolicies.get(patient.getPatientId()));
    }

    /**
     * Gets all policies for a patient
     */
    public List<InsurancePolicy> getAllPoliciesForPatient(Patient patient) {
        List<InsurancePolicy> patientPolicies = new ArrayList<>();

        Optional.ofNullable(governmentPolicies.get(patient.getPatientId()))
                .ifPresent(patientPolicies::add);

        Optional.ofNullable(privatePolicies.get(patient.getPatientId()))
                .ifPresent(patientPolicies::add);

        return patientPolicies;
    }
}