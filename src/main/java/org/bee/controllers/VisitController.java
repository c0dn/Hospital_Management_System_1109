package org.bee.controllers;

import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Nurse;
import org.bee.hms.humans.Patient;
import org.bee.hms.medical.Visit;
import org.bee.hms.medical.VisitStatus;
import org.bee.hms.policy.Coverage;
import org.bee.hms.policy.InsurancePolicy;

import java.util.List;
import java.util.stream.Collectors;

public class VisitController extends BaseController<Visit> {
    private static VisitController instance;
    private static final HumanController humanController = HumanController.getInstance();

    protected VisitController() {
        super();
    }

    public static synchronized VisitController getInstance() {
        if (instance == null) {
            instance = new VisitController();
        }
        return instance;
    }

    @Override
    protected String getDataFilePath() {
        return DATABASE_DIR + "/visits.txt";
    }

    @Override
    protected Class<Visit> getEntityClass() {
        return Visit.class;
    }

    @Override
    protected void generateInitialData() {
        System.out.println("Generating initial visit data...");

        List<Patient> patients = humanController.getAllPatients();
        List<Doctor> doctors = humanController.getAllDoctors();
        List<Nurse> nurses = humanController.getAllNurses();
        PolicyController policyController = PolicyController.getInstance();

        if (patients.isEmpty() || doctors.isEmpty()) {
            System.err.println("No patients or doctors available to generate visits");
            return;
        }

        for (Patient patient : patients) {
            List<InsurancePolicy> policies = policyController.getAllPoliciesForPatient(patient);

            if (!policies.isEmpty()) {
                InsurancePolicy policy = policies.getFirst();
                Coverage coverage = policy.getCoverage();

                Visit visit = Visit.createCompatibleVisit(
                        coverage, patient, doctors, nurses);

                visit.updateStatus(VisitStatus.DISCHARGED);

                items.add(visit);
            } else {
                Visit visit = Visit.withRandomData(patient);
                visit.updateStatus(VisitStatus.IN_PROGRESS);
                items.add(visit);
            }
        }

        System.out.println("Generated " + items.size() + " visits.");
    }


    public List<Visit> getVisitsForPatient(Patient patient) {
        return getAllItems().stream()
                .filter(visit -> visit.getPatient().equals(patient))
                .collect(Collectors.toList());
    }

}