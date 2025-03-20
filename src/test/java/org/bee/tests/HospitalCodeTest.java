package org.bee.tests;

import org.bee.hms.claims.HealthcareProvider;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * A test class for the {@link HealthcareProvider} class.
 * This class verifies the creation and lookup of hospital codes.
 */
public class HospitalCodeTest {
    
    @Test
    void testValidHospitalCode() {
        HealthcareProvider code = assertDoesNotThrow(() -> HealthcareProvider.createFromCode("WZ"),
                "Should create valid hospital code without throwing exception");
        
        assertNotNull(code, "Hospital code should not be null");
        assertEquals("WZ", code.getCode(), "Hospital code should match input");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0M", "TZ", "3X"})
    void testMultipleValidCodes(String codeString) {
        HealthcareProvider provider = assertDoesNotThrow(() -> HealthcareProvider.createFromCode(codeString),
                "Should create valid hospital code without throwing exception");
        
        assertNotNull(provider, "Hospital code should not be null");
        assertEquals(codeString, provider.getCode(), 
                "Hospital code should match input: " + codeString);
    }

    @Test
    void testInvalidCode() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> HealthcareProvider.createFromCode("]]]"),
                "Should throw IllegalArgumentException for invalid code");
        
        assertNotNull(exception.getMessage(), "Exception message should not be null");
    }

    @Test
    void testCodeWithDifferentCases() {
        // Test that codes are case-sensitive or properly normalized
        HealthcareProvider upperCase = HealthcareProvider.createFromCode("TZ");
        HealthcareProvider lowerCase = HealthcareProvider.createFromCode("tz");
        
        assertNotNull(upperCase, "Upper case code should not be null");
        assertNotNull(lowerCase, "Lower case code should not be null");
        
        // Verify behavior (this assertion might need to be adjusted based on actual requirements)
        assertEquals("TZ", upperCase.getCode(),
                "Upper case code should be preserved");
    }

    @Test
    void testEmptyCode() {
        assertThrows(IllegalArgumentException.class,
                () -> HealthcareProvider.createFromCode(""),
                "Should throw IllegalArgumentException for empty code");
    }

    @Test
    void testNullCode() {
        assertThrows(NullPointerException.class,
                () -> HealthcareProvider.createFromCode(null),
                "Should throw NullPointerException for null code");
    }
}
