package org.bee.tests;

import java.util.Map;

import org.bee.hms.humans.Patient;
import org.bee.hms.wards.Bed;
import org.bee.hms.wards.Ward;
import org.bee.hms.wards.WardClassType;
import org.bee.hms.wards.WardFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * A test class for the Ward system.
 * This class verifies the functionality of ward creation, bed management, and patient assignment.
 */
public class WardTest {

    @Test
    void testGeneralWardCreation() {
        Ward generalWard = WardFactory.getWard("General Ward", WardClassType.GENERAL_CLASS_C);
        
        assertNotNull(generalWard, "Ward should not be null");
        assertEquals("General Ward", generalWard.getWardName(), "Ward name should match");
        assertTrue(generalWard.getDailyRate() > 0, 
                "Daily rate should be positive");
        assertNotNull(generalWard.getBeds(), "Beds map should not be null");
        assertTrue(generalWard.getBeds().size() > 0, "Ward should have beds");
    }

    @Test
    void testICUWardCreation() {
        Ward icuWard = WardFactory.getWard("ICU", WardClassType.ICU);
        
        assertNotNull(icuWard, "ICU ward should not be null");
        assertEquals("ICU", icuWard.getWardName(), "Ward name should match");
        assertTrue(icuWard.getDailyRate() > 0, 
                "Daily rate should be positive");
        assertNotNull(icuWard.getBeds(), "Beds map should not be null");
        assertTrue(icuWard.getBeds().size() > 0, "Ward should have beds");
    }

    @Test
    void testDaySurgeryWardCreation() {
        Ward daySurgeryWard = WardFactory.getWard("Day Surgery", WardClassType.DAYSURGERY_CLASS_SEATER);
        
        assertNotNull(daySurgeryWard, "Day surgery ward should not be null");
        assertEquals("Day Surgery", daySurgeryWard.getWardName(), "Ward name should match");
        assertTrue(daySurgeryWard.getDailyRate() > 0, 
                "Daily rate should be positive");
        assertNotNull(daySurgeryWard.getBeds(), "Beds map should not be null");
        assertTrue(daySurgeryWard.getBeds().size() > 0, "Ward should have beds");
    }

    @Test
    void testPatientBedAssignment() {
        Ward generalWard = WardFactory.getWard("General Ward", WardClassType.GENERAL_CLASS_C);
        Map<Integer, Bed> beds = generalWard.getBeds();
        assertNotNull(beds, "Beds map should not be null");
        assertTrue(beds.size() > 1, "Ward should have at least 2 beds");
        
        Bed bed = beds.get(1);
        Patient patient = Patient.builder().withRandomData("P1002").build();
        
        // Verify initial state
        assertNotNull(bed, "Bed should not be null");
        assertNotNull(patient, "Patient should not be null");
        assertFalse(bed.isOccupied(), "New bed should not be occupied");
        
        // Assign patient
        bed.assignPatient(patient);
        assertTrue(bed.isOccupied(), "Bed should be occupied after assignment");
    }

    @ParameterizedTest
    @EnumSource(WardClassType.class)
    void testAllWardTypes(WardClassType wardType) {
        Ward ward = WardFactory.getWard("Test Ward", wardType);
        
        assertNotNull(ward, "Ward should not be null for type: " + wardType);
        assertTrue(ward.getDailyRate() > 0,
                "Daily rate should be positive for type: " + wardType);
        assertTrue(ward.getBeds().size() > 0,
                "Ward should have beds for type: " + wardType);
    }

    @Test
    void testInvalidWardClass() {
        assertThrows(IllegalArgumentException.class,
                () -> WardFactory.getWard("Test Ward", null),
                "Should throw IllegalArgumentException for null ward class");
    }

    @Test
    void testBedManagement() {
        Ward ward = WardFactory.getWard("Test Ward", WardClassType.GENERAL_CLASS_A);
        Map<Integer, Bed> beds = ward.getBeds();
        
        // Test initial state
        assertNotNull(beds, "Beds map should not be null");
        assertTrue(beds.size() > 0, "Ward should have beds");
        
        // Test bed properties
        Bed firstBed = beds.values().iterator().next();
        assertNotNull(firstBed, "First bed should not be null");
        assertFalse(firstBed.isOccupied(), "New bed should not be occupied");
    }

    @Test
    void testMultiplePatientAssignments() {
        Ward ward = WardFactory.getWard("Test Ward", WardClassType.GENERAL_CLASS_B1);
        Map<Integer, Bed> beds = ward.getBeds();
        
        // Get first three beds and assign patients
        beds.values().stream().limit(3).forEach(bed -> {
            Patient patient = Patient.builder()
                    .withRandomData(String.format("P%04d", 1000 + bed.hashCode() % 1000))
                    .build();
            
            assertFalse(bed.isOccupied(), "Bed should be unoccupied initially");
            bed.assignPatient(patient);
            assertTrue(bed.isOccupied(), "Bed should be occupied after assignment");
        });
    }
}
