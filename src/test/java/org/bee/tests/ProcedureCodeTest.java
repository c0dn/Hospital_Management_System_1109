package org.bee.tests;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.bee.hms.medical.ProcedureCode;
import org.bee.hms.policy.BenefitType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * A test class for the {@link ProcedureCode} class.
 * This class verifies the functionality of procedure code creation, cost calculations, 
 * benefit type resolution, and various code classifications.
 */
public class ProcedureCodeTest {

    @Test
    void testBasicProcedureCodeCreation() {
        ProcedureCode proc1 = ProcedureCode.createFromCode("0016070");
        assertNotNull(proc1, "Procedure code should not be null");
        assertEquals("0016070", proc1.getProcedureCode(), "Procedure code should match input");

        ProcedureCode proc2 = ProcedureCode.createFromCode("0016071");
        assertNotNull(proc2, "Second procedure code should not be null");
        assertNotEquals(proc1, proc2, "Different codes should create different objects");
    }

    @Test
    void testCostCalculations() {
        ProcedureCode proc1 = ProcedureCode.createFromCode("0016070");
        ProcedureCode proc2 = ProcedureCode.createFromCode("0016071");
        
        assertNotNull(proc1.getCharges(), "First procedure charges should not be null");
        assertNotNull(proc2.getCharges(), "Second procedure charges should not be null");
        
        BigDecimal totalCost = proc1.getCharges().add(proc2.getCharges());
        assertTrue(totalCost.compareTo(BigDecimal.ZERO) > 0,
                "Total cost should be positive");
        
        assertEquals(2, totalCost.scale(), 
                "Total cost should have 2 decimal places");
    }

    @Test
    void testInvalidCode() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ProcedureCode.createFromCode("XYZ"),
                "Should throw IllegalArgumentException for invalid code");
        
        assertNotNull(exception.getMessage(), "Exception message should not be null");
    }

    @ParameterizedTest
    @CsvSource({
        "10E0XZZ,true,MATERNITY",
        "3E00XTZ,false,MEDICATION_ADMIN",
        "5A1955Z,true,HOSPITALIZATION",
        "6A0Z0ZZ,false,OUTPATIENT_TREATMENTS",
        "02HK0JZ,true,MAJOR_SURGERY"
    })
    void testBenefitTypeResolution(String code, boolean isInpatient, String expectedType) {
        ProcedureCode proc = ProcedureCode.createFromCode(code);
        BenefitType benefit = proc.resolveBenefitType(isInpatient);
        assertNotNull(benefit, "Benefit type should not be null");
        assertEquals(BenefitType.valueOf(expectedType), benefit,
                "Benefit type should match expected type for " + code);
    }

    @Test
    void testBenefitDescriptions() {
        ProcedureCode surgicalProc = ProcedureCode.createFromCode("0D11074");
        
        String inpatientDesc = surgicalProc.getBenefitDescription(true);
        assertNotNull(inpatientDesc, "Inpatient description should not be null");
        assertFalse(inpatientDesc.isEmpty(), "Inpatient description should not be empty");
        
        String outpatientDesc = surgicalProc.getBenefitDescription(false);
        assertNotNull(outpatientDesc, "Outpatient description should not be null");
        assertFalse(outpatientDesc.isEmpty(), "Outpatient description should not be empty");
    }

    @ParameterizedTest
    @CsvSource({
        "0016070,Medical and Surgical",
        "10D07Z3,Obstetrics",
        "5A1955Z,Extracorporeal or Systemic Assistance and Performance",
        "D0002ZZ,Radiation Therapy"
    })
    void testProcedureSections(String code, String expectedSection) {
        ProcedureCode proc = ProcedureCode.createFromCode(code);
        assertEquals(expectedSection, proc.getProcedureSection(),
                "Procedure section should match expected section");
    }

    @Test
    void testRandomCodeGeneration() {
        ProcedureCode randomCode1 = ProcedureCode.getRandomCode();
        ProcedureCode randomCode2 = ProcedureCode.getRandomCode();
        
        assertNotNull(randomCode1, "First random code should not be null");
        assertNotNull(randomCode2, "Second random code should not be null");
        assertNotEquals(randomCode1.getProcedureCode(), randomCode2.getProcedureCode(),
                "Random codes should be different");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"MATERNITY", "DIAGNOSTIC_IMAGING", "ONCOLOGY_TREATMENTS", "MAJOR_SURGERY", "MINOR_SURGERY"})
    void testRandomCodeForBenefitType(String benefitTypeStr) {
        BenefitType benefitType = BenefitType.valueOf(benefitTypeStr);
        ProcedureCode code = ProcedureCode.getRandomCodeForBenefitType(benefitType);
        
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
        BenefitType benefitType = BenefitType.MATERNITY;
        
        ProcedureCode code1 = ProcedureCode.getRandomCodeForBenefitType(benefitType);
        ProcedureCode code2 = ProcedureCode.getRandomCodeForBenefitType(benefitType);
        ProcedureCode code3 = ProcedureCode.getRandomCodeForBenefitType(benefitType);
        
        // With repeated tests, at least one pair should be different
        // (There's a small chance they could all be the same by random chance,
        // but with a repeated test this becomes very unlikely)
        boolean allSame = code1.getProcedureCode().equals(code2.getProcedureCode()) && 
                          code2.getProcedureCode().equals(code3.getProcedureCode());
        
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
                () -> ProcedureCode.getRandomCodeForBenefitType(BenefitType.SURGERY),
                "Should throw exception when no codes match the benefit type");
    }

    @ParameterizedTest
    @ValueSource(strings = {"00160J0", "0DW640Z", "0UPM0KZ"})
    void testBillingItemMethods(String code) {
        ProcedureCode proc = ProcedureCode.createFromCode(code);
        
        assertNotNull(proc.getBillingItemCode(), "Billing code should not be null");
        assertNotNull(proc.getBillItemCategory(), "Category should not be null");
        
        BigDecimal charges = proc.getUnsubsidisedCharges();
        assertNotNull(charges, "Unsubsidised charges should not be null");
        assertTrue(charges.compareTo(BigDecimal.ZERO) > 0,
                "Unsubsidised charges should be positive");
        assertEquals(2, charges.scale(), "Charges should have 2 decimal places");
    }

    @Test
    void testChargesScaling() {
        ProcedureCode proc = ProcedureCode.createFromCode("0016070");
        BigDecimal charges = proc.getUnsubsidisedCharges();
        
        assertEquals(2, charges.scale(), 
                "Charges should be scaled to 2 decimal places");
        // Verify charges are properly rounded by comparing with rescaled value
        BigDecimal rescaled = charges.setScale(2, RoundingMode.HALF_UP);
        assertEquals(charges, rescaled,
                "Charges should be properly rounded to 2 decimal places");
    }

    @Test
    void testValidCodeFormat() {
        ProcedureCode proc = ProcedureCode.createFromCode("0016070");
        String code = proc.getProcedureCode();
        assertTrue(code.matches("\\d{7}"),
                "Procedure code should be 7 digits");
    }
}
