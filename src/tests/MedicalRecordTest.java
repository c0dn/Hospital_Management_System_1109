package tests;

import humans.*;
import medical.HealthcareProvider;
import medical.MedicalRecord;
import medical.TypeOfVisit;
import wards.Ward;
import wards.WardClassType;
import wards.WardFactory;
import wards.Bed;

import java.time.LocalDate;
import java.util.Arrays;

/**
 * A test class for the {@link MedicalRecord} class.
 * This class verifies the creation and display of a medical record for a patient.
 * <p>
 * The tests include creating a medical record with various details, such as the patient's
 * prescribed medications, allergies, attending nurse, healthcare provider, and ward information.
 * The test also demonstrates the display of the medical record information.
 * </p>
 */
public class MedicalRecordTest {
    /**
     * The main method to execute tests for medical records.
     * <p>
     * This method runs the following tests:
     * <ul>
     *     <li>Creating a {@link MedicalRecord} with sample data including the patient's medication list,
     *     allergies, and attending nurse.</li>
     *     <li>Displaying the details of the medical record after it is created.</li>
     * </ul>
     * </p>
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            System.out.println("Test 1");

            // Create a ward and healthcare provider, then create a medical record
            Ward generalWard = WardFactory.getWard("General Ward", WardClassType.GENERAL_CLASS_C);
            HealthcareProvider healthcareProvider = new HealthcareProvider("SGH", "0M");
            Bed bed = generalWard.getBeds().get(1);

            // Create medical record for  patient
            MedicalRecord medicalRecord = new MedicalRecord("0011M", LocalDate.of(2025, 2, 15),
                    TypeOfVisit.INPATIENT, Arrays.asList("Aspirin"), Arrays.asList("Penicillin"), Arrays.asList("None"),
                    Patient.builder().withRandomData("P1002").build(), "Doc", "2", generalWard, healthcareProvider, Arrays.asList("Nurse1"));

            // Display created medical record
            medicalRecord.displayMedicalRecord();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}