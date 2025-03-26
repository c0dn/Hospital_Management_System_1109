package org.bee.tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.bee.hms.billing.Bill;
import org.bee.hms.billing.BillBuilder;
import org.bee.hms.claims.InsuranceClaim;
import org.bee.hms.humans.*;
import org.bee.hms.insurance.GovernmentProvider;
import org.bee.hms.insurance.InsuranceProvider;
import org.bee.hms.medical.Visit;
import org.bee.hms.medical.VisitStatus;
import org.bee.hms.policy.InsuranceCoverageResult;
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

    private InsuranceClaim originalClaim;
    
    @BeforeEach
    void setUp() {
        // Initialize JSONHelper

        // Create a test InsuranceClaim object
        originalClaim = createTestClaim();
    }
    
    @Test
    @DisplayName("Test serializing InsuranceClaim to JSON string")
    void testSerializeToJsonString() {
        // Serialize to JSON string
        String json = JSONHelper.toJson(originalClaim);
        
        // Verify JSON string is not empty
        assertNotNull(json);
        assertTrue(!json.isEmpty());
    }
    
    @Test
    @DisplayName("Test deserializing InsuranceClaim from JSON string")
    void testDeserializeFromJsonString() {
        // Serialize to JSON string
        String json = JSONHelper.toJson(originalClaim);
        
        // Deserialize from JSON string
        InsuranceClaim deserializedClaim = JSONHelper.fromJson(json, InsuranceClaim.class);
        
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
        JSONHelper.saveToJsonFile(originalClaim, jsonFilePath);
        
        // Verify file exists
        File jsonFile = new File(jsonFilePath);
        assertTrue(jsonFile.exists());
        assertTrue(jsonFile.length() > 0);
        
        // Load InsuranceClaim from JSON file
        InsuranceClaim fileDeserializedClaim = JSONHelper.loadFromJsonFile(jsonFilePath, InsuranceClaim.class);
        
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

        Patient patient = new PatientBuilder()
                .withRandomBaseData()
                .patientId(DataGenerator.generatePatientId())
                .residentialStatus(ResidentialStatus.CITIZEN)
                .dateOfBirth(LocalDate.of(1970, 1, 1))
                .build();


        List<Doctor> doctorList = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            Doctor doctor = Doctor.builder().withRandomBaseData().build();
            doctorList.add(doctor);
        }

        List<Nurse> nurseList = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            Nurse nurse = Nurse.builder().withRandomBaseData().build();
            nurseList.add(nurse);
        }

        
        InsuranceProvider provider = new GovernmentProvider();
        
        InsurancePolicy policy = provider.getPatientPolicy(patient)
                .orElseThrow(() -> new AssertionError("Failed to get policy for patient"));
        
        Visit visit = Visit.createCompatibleVisit(policy.getCoverage(), patient, doctorList, nurseList);
        visit.updateStatus(VisitStatus.DISCHARGED);
        
        Bill bill = new BillBuilder()
                .withPatient(patient)
                .withVisit(visit)
                .withInsurancePolicy(policy)
                .build();


        InsuranceCoverageResult coverageResult = bill.calculateInsuranceCoverage();

        InsuranceClaim claim = coverageResult.claim()
                .orElseThrow(() -> new AssertionError("Failed to get claim from coverage result"));


        // Add some supporting documents
        claim.addSupportingDocument("Medical report from Dr. Smith");
        claim.addSupportingDocument("X-ray results from Radiology Department");

        // Add comments
        claim.updateComments("Patient has a history of similar conditions");

        return claim;
    }
}
