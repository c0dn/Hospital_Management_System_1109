package org.bee.tests;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
     * Uses reflection to access private fields for comprehensive verification.
     */
public class VisitSerializationTest {

    private JSONHelper jsonHelper;
    private Visit originalVisit;
    
    @BeforeEach
    void setUp() {
        // Initialize JSONHelper

        // Create a test Visit object
        originalVisit = createTestVisit();
    }
    
    @Test
    @DisplayName("Test serializing Visit to JSON string")
    void testSerializeToJsonString() {
        // Serialize to JSON string
        String json = JSONHelper.toJson(originalVisit);
        
        // Verify JSON string is not empty
        assertNotNull(json);
        assertTrue(!json.isEmpty());
    }
    
    /**
     * Helper method to access private fields using reflection from the class or any of its superclasses
     */
    private <T> T getPrivateField(Object obj, String fieldName, Class<T> fieldType) throws Exception {
        Class<?> currentClass = obj.getClass();
        while (currentClass != null) {
            try {
                Field field = currentClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                return fieldType.cast(field.get(obj));
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field '" + fieldName + "' not found in class hierarchy of " + obj.getClass().getName());
    }

    @Test
    @DisplayName("Test deserializing Visit from JSON string")
    void testDeserializeFromJsonString() throws Exception {
        // Serialize to JSON string
        String json = JSONHelper.toJson(originalVisit);
        
        // Deserialize from JSON string
        Visit deserializedVisit = JSONHelper.fromJson(json, Visit.class);
        
        // Verify all fields
        verifyFields(originalVisit, deserializedVisit);
    }
    
    @Test
    @DisplayName("Test serializing Visit to file and deserializing")
    void testSerializeToFileAndDeserialize(@TempDir Path tempDir) throws Exception {
        // Create a temporary file path
        String jsonFilePath = tempDir.resolve("visit_test.json").toString();
        
        // Save Visit to JSON file
        JSONHelper.saveToJsonFile(originalVisit, jsonFilePath);
        
        // Verify file exists
        File jsonFile = new File(jsonFilePath);
        assertTrue(jsonFile.exists());
        assertTrue(jsonFile.length() > 0);
        
        // Load Visit from JSON file
        Visit fileDeserializedVisit = JSONHelper.loadFromJsonFile(jsonFilePath, Visit.class);
        
        // Verify all fields match
        verifyFields(originalVisit, fileDeserializedVisit);
    }

    /**
     * Helper method to verify all fields between original and deserialized Visit objects
     */
    private void verifyFields(Visit original, Visit deserialized) throws Exception {
        assertEquals(getPrivateField(original, "visitId", String.class),
                    getPrivateField(deserialized, "visitId", String.class),
                    "Visit ID should match");
        assertEquals(getPrivateField(original, "admissionDateTime", LocalDateTime.class),
                    getPrivateField(deserialized, "admissionDateTime", LocalDateTime.class),
                    "admission Date time should match");
        assertEquals(getPrivateField(original, "dischargeDateTime", LocalDateTime.class),
                    getPrivateField(deserialized, "dischargeDateTime", LocalDateTime.class),
                    "discharge Date Time should match");
        assertEquals(getPrivateField(original, "status", VisitStatus.class),
                    getPrivateField(deserialized, "status", VisitStatus.class),
                    "Visit status should match");

        // Verify lists with complex objects
        verifyList(original, deserialized, "wardStays", WardStay.class, "Ward stays should match");
        verifyList(original, deserialized, "diagnosticCodes", DiagnosticCode.class, "Diagnostic codes should match");
        verifyList(original, deserialized, "inpatientProcedures", ProcedureCode.class, "Procedure codes should match");
        verifyList(original, deserialized, "attendingNurses", Nurse.class, "Attending nurses should match");
        
        // Verify map of prescriptions
        Map<Medication, Integer> originalPrescriptions = getPrivateField(original, "prescriptions", Map.class);
        Map<Medication, Integer> deserializedPrescriptions = getPrivateField(deserialized, "prescriptions", Map.class);
        assertEquals(originalPrescriptions.size(), deserializedPrescriptions.size(), "Prescriptions map size should match");
        
        // Verify Patient, Doctor, and Nurse references
        assertEquals(getPrivateField(original, "patient", Patient.class).getPatientId(),
                    getPrivateField(deserialized, "patient", Patient.class).getPatientId(),
                    "Patient should match");
        
        Doctor originalDoctor = getPrivateField(original, "attendingDoc", Doctor.class);
        Doctor deserializedDoctor = getPrivateField(deserialized, "attendingDoc", Doctor.class);
        if (originalDoctor != null) {
            assertEquals(getPrivateField(originalDoctor, "staffId", String.class),
                        getPrivateField(deserializedDoctor, "staffId", String.class),
                        "Doctor should match");
        }
    }

    /**
     * Helper method to verify lists of objects
     */
    /**
     * Helper method to verify lists of objects with deep comparison of their fields
     */
    private <T> void verifyList(Visit original, Visit deserialized, String fieldName, Class<T> elementType, String message) throws Exception {
        List<T> originalList = getPrivateField(original, fieldName, List.class);
        List<T> deserializedList = getPrivateField(deserialized, fieldName, List.class);
        assertEquals(originalList.size(), deserializedList.size(), message + " (size)");
        
        for (int i = 0; i < originalList.size(); i++) {
            T originalItem = originalList.get(i);
            T deserializedItem = deserializedList.get(i);
            
            if (elementType == DiagnosticCode.class) {
                DiagnosticCode origCode = (DiagnosticCode) originalItem;
                DiagnosticCode deserCode = (DiagnosticCode) deserializedItem;
                assertEquals(getPrivateField(origCode, "fullCode", String.class),
                           getPrivateField(deserCode, "fullCode", String.class),
                           message + " (diagnostic code at " + i + ")");
                assertEquals(getPrivateField(origCode, "fullDescription", String.class),
                           getPrivateField(deserCode, "fullDescription", String.class),
                           message + " (diagnostic fullDescription at " + i + ")");
            }
            else if (elementType == ProcedureCode.class) {
                ProcedureCode origCode = (ProcedureCode) originalItem;
                ProcedureCode deserCode = (ProcedureCode) deserializedItem;
                assertEquals(getPrivateField(origCode, "code", String.class),
                           getPrivateField(deserCode, "code", String.class),
                           message + " (procedure code at " + i + ")");
                assertEquals(getPrivateField(origCode, "description", String.class),
                           getPrivateField(deserCode, "description", String.class),
                           message + " (procedure description at " + i + ")");
            }
            else if (elementType == WardStay.class) {
                WardStay origStay = (WardStay) originalItem;
                WardStay deserStay = (WardStay) deserializedItem;
                assertEquals(getPrivateField(origStay, "startDateTime", LocalDateTime.class),
                           getPrivateField(deserStay, "startDateTime", LocalDateTime.class),
                           message + " (ward stay start time at " + i + ")");
                assertEquals(getPrivateField(origStay, "endDateTime", LocalDateTime.class),
                           getPrivateField(deserStay, "endDateTime", LocalDateTime.class),
                           message + " (ward stay end time at " + i + ")");
                
                Ward origWard = getPrivateField(origStay, "ward", Ward.class);
                Ward deserWard = getPrivateField(deserStay, "ward", Ward.class);
                assertEquals(getPrivateField(origWard, "wardName", String.class),
                           getPrivateField(deserWard, "wardName", String.class),
                           message + " (ward name at " + i + ")");
                assertEquals(getPrivateField(origWard, "wardClassType", WardClassType.class),
                           getPrivateField(deserWard, "wardClassType", WardClassType.class),
                           message + " (ward class type at " + i + ")");
            } else if (elementType == Nurse.class) {
                Nurse origNurse = (Nurse) originalItem;
                Nurse deserNurse = (Nurse) deserializedItem;
                assertEquals(getPrivateField(origNurse, "staffId", String.class),
                        getPrivateField(deserNurse, "staffId", String.class),
                        message + " (nurse staffId at " + i + ")");
            }
            else {
                // For any other types, fall back to equals() method
                assertEquals(originalItem, deserializedItem, 
                           message + " (element at " + i + ")");
            }
        }
    }
    
    /**
     * Creates a test Visit object with various properties set.
     */
    private Visit createTestVisit() {

        // Create a patient
        Patient patient = Patient.builder()
                .patientId(DataGenerator.generatePatientId())
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
            int quantity = DataGenerator.generateRandomInt(1, 5);
            visit.prescribeMedicine(medication, quantity);
        }
        
        // Update status
        visit.updateStatus(VisitStatus.IN_PROGRESS);
        
        return visit;
    }
}
