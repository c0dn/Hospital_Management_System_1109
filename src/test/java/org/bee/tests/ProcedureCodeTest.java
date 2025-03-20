package org.bee.tests;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.bee.hms.medical.ProcedureCode;
import org.bee.hms.policy.BenefitType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        "1016070,true,MATERNITY",
        "3E033VZ,false,MEDICATION_ADMINISTRATION",
        "5A1955Z,true,DIAGNOSTIC_IMAGING",
        "6A0Z0ZZ,false,ONCOLOGY",
        "0D11074,true,MAJOR_SURGERY"
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
        assertTrue(inpatientDesc.length() > 0, "Inpatient description should not be empty");
        
        String outpatientDesc = surgicalProc.getBenefitDescription(false);
        assertNotNull(outpatientDesc, "Outpatient description should not be null");
        assertTrue(outpatientDesc.length() > 0, "Outpatient description should not be empty");
    }

    @ParameterizedTest
    @CsvSource({
        "0016070,Medical and Surgical",
        "1016070,Obstetrics",
        "5A1955Z,Imaging",
        "7A03X0Z,Radiation Oncology"
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
    @ValueSource(strings = {"0016070", "1016070", "5A1955Z"})
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
