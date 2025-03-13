package org.bee.tests;

import java.util.Optional;

import org.bee.humans.Patient;
import org.bee.insurance.PrivateProvider;
import org.bee.policy.InsurancePolicy;
import org.bee.utils.DataGenerator;

public class InsuranceTest {
    public static void main(String[] args) {
        try {
            System.out.println("Testing Insurance functionality...\n");
            PrivateProvider provider = new PrivateProvider();
            DataGenerator gen = DataGenerator.getInstance();
            Patient patient = Patient.builder().withRandomData(gen.generatePatientId()).build();
            Optional<InsurancePolicy> policy = provider.getPatientPolicy(patient);
            policy.ifPresent(p -> System.out.println("Insurance Policy: " + p));

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
