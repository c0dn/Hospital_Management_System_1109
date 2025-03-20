package org.bee.tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Nurse;
import org.bee.hms.humans.Patient;
import org.bee.hms.medical.DiagnosticCode;
import org.bee.hms.medical.Medication;
import org.bee.hms.medical.ProcedureCode;
import org.bee.hms.medical.Visit;
import org.bee.hms.medical.VisitStatus;
import org.bee.hms.medical.WardStay;
import org.bee.hms.wards.Ward;
import org.bee.hms.wards.WardClassType;
import org.bee.hms.wards.WardFactory;
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
 * Tests for serialization and deserialization of Visit objects using JSONHelper.
 * This test class verifies that Visit objects can be properly converted to JSON and back.
 */
public class VisitSerializationTest {

    private JSONHelper jsonHelper;
    private Visit originalVisit;
    
    @BeforeEach
    void setUp() {
        // Initialize JSONHelper
        jsonHelper = JSONHelper.getInstance();
        
        // Create a test Visit object
        originalVisit = createTestVisit();
    }
    
    @Test
    @DisplayName("Test serializing Visit to JSON string")
    void testSerializeToJsonString() {
        // Serialize to JSON string
        String json = jsonHelper.toJson(originalVisit);
        
        // Verify JSON string is not empty
        assertNotNull(json);
        assertTrue(json.length() > 0);
    }
    
    @Test
    @DisplayName("Test deserializing Visit from JSON string")
    void testDeserializeFromJsonString() {
        // Serialize to JSON string
        String json = jsonHelper.toJson(originalVisit);
        
        // Deserialize from JSON string
        Visit deserializedVisit = jsonHelper.fromJson(json, Visit.class);
        
        // Verify key properties match
        assertEquals(originalVisit.getStatus(), deserializedVisit.getStatus());
    }
    
    @Test
    @DisplayName("Test serializing Visit to file and deserializing")
    void testSerializeToFileAndDeserialize(@TempDir Path tempDir) throws IOException {
        // Create a temporary file path
        String jsonFilePath = tempDir.resolve("visit_test.json").toString();
        
        // Save Visit to JSON file
        jsonHelper.saveToJsonFile(originalVisit, jsonFilePath);
        
        // Verify file exists
        File jsonFile = new File(jsonFilePath);
        assertTrue(jsonFile.exists());
        assertTrue(jsonFile.length() > 0);
        
        // Load Visit from JSON file
        Visit fileDeserializedVisit = jsonHelper.loadFromJsonFile(jsonFilePath, Visit.class);
        
        // Verify key properties match
        assertEquals(originalVisit.getStatus(), fileDeserializedVisit.getStatus());
    }
    
    /**
     * Creates a test Visit object with various properties set.
     */
    private Visit createTestVisit() {
        DataGenerator gen = DataGenerator.getInstance();
        
        // Create a patient
        Patient patient = Patient.builder()
                .patientId(gen.generatePatientId())
                .withRandomBaseData()
                .build();
        
        // Create a visit
        Visit visit = Visit.createNew(LocalDateTime.now(), patient);
        
        // Add a doctor
        Doctor doctor = Doctor.builder().withRandomBaseData().build();
        visit.assignDoctor(doctor);
        
        // Add a nurse
        Nurse nurse = Nurse.builder().withRandomBaseData().build();
        visit.assignNurse(nurse);
        
        // Add a ward stay
        Ward generalWard = WardFactory.getWard("General Ward A", WardClassType.GENERAL_CLASS_A);
        LocalDateTime now = LocalDateTime.now();
        WardStay wardStay = new WardStay(generalWard, now, now.plusDays(3));
        visit.addWardStay(wardStay);
        
        // Add a procedure
        ProcedureCode procedure = ProcedureCode.getRandomCode();
        visit.procedure(procedure);
        
        // Add a diagnostic code
        DiagnosticCode diagnostic = DiagnosticCode.getRandomCode();
        visit.diagnose(diagnostic);
        
        // Add medications
        for (int i = 0; i < 3; i++) {
            Medication medication = Medication.getRandomMedication();
            int quantity = gen.generateRandomInt(1, 5);
            visit.prescribeMedicine(medication, quantity);
        }
        
        // Update status
        visit.updateStatus(VisitStatus.IN_PROGRESS);
        
        return visit;
    }
}
