package org.bee.tests;

import java.math.BigDecimal;

import org.bee.hms.medical.DiagnosticCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A test class for the {@link DiagnosticCode} class.
 * This class verifies the functionality of diagnostic code creation, lookup, and various interface methods.
 */
public class DiagnosticCodeTest {
    
    @Test
    void testBasicCodeCreation() {
        DiagnosticCode code = assertDoesNotThrow(() -> DiagnosticCode.createFromCode("A000"),
                "Should create code without throwing exception");
        
        assertNotNull(code, "Created code should not be null");
        assertEquals("A000", code.getDiagnosisCode(), "Diagnosis code should match input");
    }

    @Test
    void testBillingInterfaceMethods() {
        DiagnosticCode code = DiagnosticCode.createFromCode("A000");
        
        assertNotNull(code.getBillingItemCode(), "Billing item code should not be null");
        assertTrue(code.getUnsubsidisedCharges().compareTo(BigDecimal.ZERO) > 0,
                "Unsubsidised charges should be positive");
        assertNotNull(code.getBillItemDescription(), "Bill item description should not be null");
        assertNotNull(code.getBillItemCategory(), "Bill item category should not be null");
    }

    @Test
    void testClaimableInterfaceMethods() {
        DiagnosticCode code = DiagnosticCode.createFromCode("A000");
        
        assertTrue(code.getCharges().compareTo(BigDecimal.ZERO) > 0,
                "Charges should be positive");
        assertEquals("A000", code.getDiagnosisCode(), "Diagnosis code should match");
        assertNotNull(code.getBenefitDescription(true),
                "Inpatient benefit description should not be null");
        assertNotNull(code.getBenefitDescription(false),
                "Outpatient benefit description should not be null");
    }

    @ParameterizedTest
    @ValueSource(strings = {"O0001", "C000", "K000"})
    void testBenefitTypeResolution(String codeString) {
        DiagnosticCode code = DiagnosticCode.createFromCode(codeString);
        assertNotNull(code.resolveBenefitType(false),
                "Benefit type should be resolved for " + codeString);
    }

    @Test
    void testDirectDescriptionLookup() {
        String description = DiagnosticCode.getDescriptionForCode("A150");
        assertNotNull(description, "Description should not be null");
        assertFalse(description.isEmpty(), "Description should not be empty");
    }

    @Test
    void testRandomCodeGeneration() {
        DiagnosticCode randomCode1 = DiagnosticCode.getRandomCode();
        DiagnosticCode randomCode2 = DiagnosticCode.getRandomCode();
        
        assertNotNull(randomCode1, "First random code should not be null");
        assertNotNull(randomCode2, "Second random code should not be null");
        assertNotEquals(randomCode1.getDiagnosisCode(), randomCode2.getDiagnosisCode(),
                "Random codes should be different");
    }

    @Test
    void testInvalidCode() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> DiagnosticCode.createFromCode("XYZ"),
                "Should throw IllegalArgumentException for invalid code");
        
        assertNotNull(exception.getMessage(), "Exception message should not be null");
    }

    @Test
    void testInpatientVsOutpatientBenefitResolution() {
        DiagnosticCode code = DiagnosticCode.createFromCode("A000");
        
        assertNotNull(code.resolveBenefitType(true),
                "Inpatient benefit type should be resolved");
        assertNotNull(code.resolveBenefitType(false),
                "Outpatient benefit type should be resolved");
        
        // Verify the types are appropriate for the setting
        var inpatientType = code.resolveBenefitType(true);
        var outpatientType = code.resolveBenefitType(false);
        
        assertNotEquals(inpatientType, outpatientType,
                "Inpatient and outpatient benefit types should be different");
    }
}
