package org.bee.tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for serialization and deserialization of InsuranceClaim objects using JSONHelper.
 * This test class verifies that InsuranceClaim objects can be properly converted to JSON and back.
 */
public class InsuranceClaimSerializationTest {

    private JSONHelper jsonHelper;
    private InsuranceClaim originalClaim;
    
    @BeforeEach
    void setUp() {
        // Initialize JSONHelper
        jsonHelper = JSONHelper.getInstance();
        
        // Create a test InsuranceClaim object
        originalClaim = createTestClaim();
    }
    
    @Test
    @DisplayName("Test serializing InsuranceClaim to JSON string")
    void testSerializeToJsonString() {
        // Serialize to JSON string
        String json = jsonHelper.toJson(originalClaim);
        
        // Verify JSON string is not empty
        assertNotNull(json);
        assertTrue(json.length() > 0);
    }
    
    @Test
    @DisplayName("Test deserializing InsuranceClaim from JSON string")
    void testDeserializeFromJsonString() {
        // Serialize to JSON string
        String json = jsonHelper.toJson(originalClaim);
        
        // Deserialize from JSON string
        InsuranceClaim deserializedClaim = jsonHelper.fromJson(json, InsuranceClaim.class);
        
        // Verify key properties match
        assertEquals(originalClaim.isDraft(), deserializedClaim.isDraft());
        assertEquals(originalClaim.isSubmitted(), deserializedClaim.isSubmitted());
        assertEquals(originalClaim.isApproved(), deserializedClaim.isApproved());
        assertEquals(originalClaim.getClaimId(), deserializedClaim.getClaimId());
    }
    
    @Test
    @DisplayName("Test serializing InsuranceClaim to file and deserializing")
    void testSerializeToFileAndDeserialize(@TempDir Path tempDir) throws IOException {
        // Create a temporary file path
        String jsonFilePath = tempDir.resolve("claim_test.json").toString();
        
        // Save InsuranceClaim to JSON file
        jsonHelper.saveToJsonFile(originalClaim, jsonFilePath);
        
        // Verify file exists
        File jsonFile = new File(jsonFilePath);
        assertTrue(jsonFile.exists());
        assertTrue(jsonFile.length() > 0);
        
        // Load InsuranceClaim from JSON file
        InsuranceClaim fileDeserializedClaim = jsonHelper.loadFromJsonFile(jsonFilePath, InsuranceClaim.class);
        
        // Verify key properties match
        assertEquals(originalClaim.isDraft(), fileDeserializedClaim.isDraft());
        assertEquals(originalClaim.isSubmitted(), fileDeserializedClaim.isSubmitted());
        assertEquals(originalClaim.isApproved(), fileDeserializedClaim.isApproved());
        assertEquals(originalClaim.getClaimId(), fileDeserializedClaim.getClaimId());
    }
    
    /**
     * Creates a test InsuranceClaim object with various properties set.
     */
    private InsuranceClaim createTestClaim() {
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
        Bill bill = new BillBuilder()
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
