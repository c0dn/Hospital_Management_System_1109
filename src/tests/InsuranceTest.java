package tests;

import humans.Patient;
import humans.PatientBuilder;
import insurance.PrivateProvider;
import policy.InsurancePolicy;
import utils.DataGenerator;

import java.util.Optional;

public class InsuranceTest {
    public static void main(String[] args) {
        try {
            System.out.println("Testing Insurance functionality...\n");
            PrivateProvider provider = new PrivateProvider();
            DataGenerator gen = DataGenerator.getInstance();
            Patient patient = Patient.builder().withRandomData(gen.generatePatientId()).build();
            Optional<InsurancePolicy> policy = provider.getPatientPolicy(patient);

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}