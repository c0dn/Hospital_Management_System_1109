package org.bee.tests;

import java.math.BigDecimal;

import org.bee.hms.medical.DiagnosticCode;
import org.bee.hms.policy.BenefitType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
    
    @ParameterizedTest
    @ValueSource(strings = {"DENTAL", "MATERNITY", "CRITICAL_ILLNESS", "ACCIDENT", "PREVENTIVE_CARE"})
    void testRandomCodeForBenefitType(String benefitTypeStr) {
        BenefitType benefitType = BenefitType.valueOf(benefitTypeStr);
        DiagnosticCode code = DiagnosticCode.getRandomCodeForBenefitType(benefitType, true);
        
        assertNotNull(code, "Random code for benefit type should not be null");
        
        // Verify the code matches the requested benefit type (check both inpatient and outpatient)
        boolean matchesInpatient = code.resolveBenefitType(true) == benefitType;
        boolean matchesOutpatient = code.resolveBenefitType(false) == benefitType;
        
        assertTrue(matchesInpatient || matchesOutpatient, 
                "Code should match the requested benefit type: " + benefitType);
    }
    
    @RepeatedTest(5)
    void testRandomCodeForBenefitTypeReturnsVariety() {
        // Test that multiple calls return different codes
        BenefitType benefitType = BenefitType.DENTAL;
        
        DiagnosticCode code1 = DiagnosticCode.getRandomCodeForBenefitType(benefitType, false);
        DiagnosticCode code2 = DiagnosticCode.getRandomCodeForBenefitType(benefitType, false);
        DiagnosticCode code3 = DiagnosticCode.getRandomCodeForBenefitType(benefitType, false);
        
        // With repeated tests, at least one pair should be different
        // (There's a small chance they could all be the same by random chance,
        // but with a repeated test this becomes very unlikely)
        boolean allSame = code1.getDiagnosisCode().equals(code2.getDiagnosisCode()) && 
                          code2.getDiagnosisCode().equals(code3.getDiagnosisCode());
        
        assertFalse(allSame, "Multiple calls should return different codes");
    }
    
    @Test
    void testInvalidBenefitTypeThrowsException() {
        // Create a benefit type that likely won't have any matching codes
        // This is a bit of a hack since we can't easily create an invalid enum value
        // Instead we'll use a benefit type that's unlikely to have matching codes
        
        // If this test fails in the future, it might be because codes were added for this type
        // In that case, choose a different benefit type that doesn't have matching codes
        assertThrows(IllegalArgumentException.class, 
                () -> DiagnosticCode.getRandomCodeForBenefitType(BenefitType.SURGERY, false),
                "Should throw exception when no codes match the benefit type");
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
