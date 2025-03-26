package org.bee.controllers;

import org.bee.hms.humans.Patient;
import org.bee.hms.policy.InsurancePolicy;
import org.bee.hms.insurance.GovernmentProvider;
import org.bee.hms.insurance.PrivateProvider;
import org.bee.hms.insurance.InsuranceProvider;

import java.util.*;

public class PolicyController extends BaseController<InsurancePolicy> {
    private static PolicyController instance;
    private static final HumanController humanController = HumanController.getInstance();

    private static final InsuranceProvider governmentProvider = new GovernmentProvider();
    private static final InsuranceProvider privateProvider = new PrivateProvider();

    private static final Map<String, InsurancePolicy> governmentPolicies = new HashMap<>();
    private static final Map<String, InsurancePolicy> privatePolicies = new HashMap<>();

    protected PolicyController() {
        super();
    }

    public static synchronized PolicyController getInstance() {
        if (instance == null) {
            instance = new PolicyController();
        }
        return instance;
    }

    @Override
    protected String getDataFilePath() {
        return DATABASE_DIR + "/policies.txt";
    }

    @Override
    protected Class<InsurancePolicy> getEntityClass() {
        return InsurancePolicy.class;
    }

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