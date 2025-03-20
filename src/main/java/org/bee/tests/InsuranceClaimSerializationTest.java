package org.bee.tests;

import java.io.File;
import java.time.LocalDate;

import org.bee.hms.billing.Bill;
import org.bee.hms.billing.BillBuilder;
import org.bee.hms.claims.InsuranceClaim;
import org.bee.hms.humans.Patient;
import org.bee.hms.humans.PatientBuilder;
import org.bee.hms.humans.ResidentialStatus;
import org.bee.hms.insurance.GovernmentProvider;
import org.bee.hms.insurance.InsuranceProvider;
import org.bee.hms.medical.Visit;
import org.bee.hms.medical.VisitStatus;
import org.bee.hms.policy.InsurancePolicy;
import org.bee.utils.DataGenerator;
import org.bee.utils.JSONHelper;

/**
 * Tests for serialization and deserialization of InsuranceClaim objects using JSONHelper.
 * This test class verifies that InsuranceClaim objects can be properly converted to JSON and back.
 */
public class InsuranceClaimSerializationTest {

    /**
     * Main method to execute tests for InsuranceClaim serialization/deserialization.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Testing InsuranceClaim serialization/deserialization...\n");
            
            // Initialize JSONHelper
            JSONHelper jsonHelper = JSONHelper.getInstance();
            
            // Create a test InsuranceClaim object
            InsuranceClaim originalClaim = createTestClaim();
            System.out.println("Created test InsuranceClaim object with status: " + 
                    (originalClaim.isDraft() ? "DRAFT" : 
                     originalClaim.isSubmitted() ? "SUBMITTED" : 
                     originalClaim.isApproved() ? "APPROVED" : "OTHER"));
            
            // Test 1: Serialize to JSON string
            System.out.println("\nTest 1 - Serializing InsuranceClaim to JSON string:");
            String json = jsonHelper.toJson(originalClaim);
            System.out.println("JSON string length: " + json.length());
            System.out.println("JSON string preview: " + json.substring(0, Math.min(json.length(), 200)) + "...");
            
            // Test 2: Deserialize from JSON string
            System.out.println("\nTest 2 - Deserializing InsuranceClaim from JSON string:");
            InsuranceClaim deserializedClaim = jsonHelper.fromJson(json, InsuranceClaim.class);
            System.out.println("Deserialized InsuranceClaim status: " + 
                    (deserializedClaim.isDraft() ? "DRAFT" : 
                     deserializedClaim.isSubmitted() ? "SUBMITTED" : 
                     deserializedClaim.isApproved() ? "APPROVED" : "OTHER"));
            
            // Verify key properties match
            boolean statusMatches = originalClaim.isDraft() == deserializedClaim.isDraft() &&
                                   originalClaim.isSubmitted() == deserializedClaim.isSubmitted() &&
                                   originalClaim.isApproved() == deserializedClaim.isApproved();
            boolean idMatches = originalClaim.getClaimId().equals(deserializedClaim.getClaimId());
            System.out.println("Status matches: " + statusMatches);
            System.out.println("Claim ID matches: " + idMatches);
            
            // Test 3: Serialize to file and deserialize from file
            System.out.println("\nTest 3 - Serializing InsuranceClaim to file and deserializing:");
            
            // Create a temporary file
            String tempDir = System.getProperty("java.io.tmpdir");
            String jsonFilePath = tempDir + File.separator + "claim_test.json";
            System.out.println("Using temporary file: " + jsonFilePath);
            
            // Save InsuranceClaim to JSON file
            jsonHelper.saveToJsonFile(originalClaim, jsonFilePath);
            System.out.println("Saved InsuranceClaim to JSON file");
            
            // Verify file exists
            File jsonFile = new File(jsonFilePath);
            System.out.println("File exists: " + jsonFile.exists());
            System.out.println("File size: " + jsonFile.length() + " bytes");
            
            // Load InsuranceClaim from JSON file
            InsuranceClaim fileDeserializedClaim = jsonHelper.loadFromJsonFile(jsonFilePath, InsuranceClaim.class);
            System.out.println("Loaded InsuranceClaim from JSON file");
            
            // Verify key properties match
            boolean fileStatusMatches = originalClaim.isDraft() == fileDeserializedClaim.isDraft() &&
                                       originalClaim.isSubmitted() == fileDeserializedClaim.isSubmitted() &&
                                       originalClaim.isApproved() == fileDeserializedClaim.isApproved();
            boolean fileIdMatches = originalClaim.getClaimId().equals(fileDeserializedClaim.getClaimId());
            System.out.println("Status matches after file deserialization: " + fileStatusMatches);
            System.out.println("Claim ID matches after file deserialization: " + fileIdMatches);
            
            // Clean up
            jsonFile.delete();
            System.out.println("Deleted temporary file");
            
            // Summary
            System.out.println("\nSerialization/Deserialization Test Summary:");
            System.out.println("- Direct serialization/deserialization: " + 
                ((statusMatches && idMatches) ? "PASSED" : "FAILED"));
            System.out.println("- File serialization/deserialization: " + 
                ((fileStatusMatches && fileIdMatches) ? "PASSED" : "FAILED"));
            
            if (statusMatches && idMatches && fileStatusMatches && fileIdMatches) {
                System.out.println("\nAll tests PASSED! InsuranceClaim can be properly serialized and deserialized.");
            } else {
                System.out.println("\nSome tests FAILED. InsuranceClaim serialization/deserialization needs improvement.");
            }
            
        } catch (Exception e) {
            System.err.println("Error during serialization test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a test InsuranceClaim object with various properties set.
     */
    private static InsuranceClaim createTestClaim() {
        DataGenerator gen = DataGenerator.getInstance();
        
        // Create a test patient
        Patient patient = new PatientBuilder()
                .withRandomBaseData()
                .patientId(gen.generatePatientId())
                .residentialStatus(ResidentialStatus.CITIZEN)
                .dateOfBirth(LocalDate.of(1970, 1, 1))
                .build();
        
        // Create an insurance provider
        InsuranceProvider provider = new GovernmentProvider();
        
        // Get policy for patient
        InsurancePolicy policy = provider.getPatientPolicy(patient)
                .orElseThrow(() -> new AssertionError("Failed to get policy for patient"));
        
        // Create a visit for testing
        Visit visit = Visit.withRandomData();
        visit.updateStatus(VisitStatus.DISCHARGED);
        
        // Create bill using BillBuilder
        Bill bill = new BillBuilder<>()
                .withPatientId(patient.getPatientId())
                .withVisit(visit)
                .build();
        
        // Create claim
        InsuranceClaim claim = InsuranceClaim.createNew(
                bill,
                provider,
                policy,
                patient,
                bill.getTotalAmount()
        );
        
        // Add some supporting documents
        claim.addSupportingDocument("Medical report from Dr. Smith");
        claim.addSupportingDocument("X-ray results from Radiology Department");
        
        // Add comments
        claim.updateComments("Patient has a history of similar conditions");
        
        // Submit the claim to change its status
        claim.submit();
        
        return claim;
    }
}
