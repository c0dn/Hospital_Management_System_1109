package org.bee.tests;

import java.io.File;
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

/**
 * Tests for serialization and deserialization of Visit objects using JSONHelper.
 * This test class verifies that Visit objects can be properly converted to JSON and back.
 */
public class VisitSerializationTest {

    /**
     * Main method to execute tests for Visit serialization/deserialization.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Testing Visit serialization/deserialization...\n");
            
            // Initialize JSONHelper
            JSONHelper jsonHelper = JSONHelper.getInstance();
            
            // Create a test Visit object
            Visit originalVisit = createTestVisit();
            System.out.println("Created test Visit object with status: " + originalVisit.getStatus());
            
            // Test 1: Serialize to JSON string
            System.out.println("\nTest 1 - Serializing Visit to JSON string:");
            String json = jsonHelper.toJson(originalVisit);
            System.out.println("JSON string length: " + json.length());
            System.out.println("JSON string preview: " + json.substring(0, Math.min(json.length(), 200)) + "...");
            
            // Test 2: Deserialize from JSON string
            System.out.println("\nTest 2 - Deserializing Visit from JSON string:");
            Visit deserializedVisit = jsonHelper.fromJson(json, Visit.class);
            System.out.println("Deserialized Visit status: " + deserializedVisit.getStatus());
            
            // Verify key properties match
            boolean statusMatches = originalVisit.getStatus() == deserializedVisit.getStatus();
            System.out.println("Status matches: " + statusMatches);
            
            // Test 3: Serialize to file and deserialize from file
            System.out.println("\nTest 3 - Serializing Visit to file and deserializing:");
            
            // Create a temporary file
            String tempDir = System.getProperty("java.io.tmpdir");
            String jsonFilePath = tempDir + File.separator + "visit_test.json";
            System.out.println("Using temporary file: " + jsonFilePath);
            
            // Save Visit to JSON file
            jsonHelper.saveToJsonFile(originalVisit, jsonFilePath);
            System.out.println("Saved Visit to JSON file");
            
            // Verify file exists
            File jsonFile = new File(jsonFilePath);
            System.out.println("File exists: " + jsonFile.exists());
            System.out.println("File size: " + jsonFile.length() + " bytes");
            
            // Load Visit from JSON file
            Visit fileDeserializedVisit = jsonHelper.loadFromJsonFile(jsonFilePath, Visit.class);
            System.out.println("Loaded Visit from JSON file");
            
            // Verify key properties match
            boolean fileStatusMatches = originalVisit.getStatus() == fileDeserializedVisit.getStatus();
            System.out.println("Status matches after file deserialization: " + fileStatusMatches);
            
            // Clean up
            jsonFile.delete();
            System.out.println("Deleted temporary file");
            
            // Summary
            System.out.println("\nSerialization/Deserialization Test Summary:");
            System.out.println("- Direct serialization/deserialization: " + (statusMatches ? "PASSED" : "FAILED"));
            System.out.println("- File serialization/deserialization: " + (fileStatusMatches ? "PASSED" : "FAILED"));
            
            if (statusMatches && fileStatusMatches) {
                System.out.println("\nAll tests PASSED! Visit can be properly serialized and deserialized.");
            } else {
                System.out.println("\nSome tests FAILED. Visit serialization/deserialization needs improvement.");
            }
            
        } catch (Exception e) {
            System.err.println("Error during serialization test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a test Visit object with various properties set.
     */
    private static Visit createTestVisit() {
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
        WardStay wardStay = new WardStay(generalWard, now, now.plusDays(3), false);
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
