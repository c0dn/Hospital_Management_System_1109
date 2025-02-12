package tests;

import humans.*;
import medical.HealthcareProvider;
import medical.MedicalRecord;
import wards.Ward;
import wards.WardClassType;
import wards.WardFactory;
import wards.Bed;

import java.time.LocalDate;
import java.util.Arrays;

public class MedicalRecordTest {
    public static void main(String[] args) {
        System.out.println("Test 1");
        Ward generalWard = WardFactory.getWard("General Ward", WardClassType.GENERAL_CLASS_C);
        HealthcareProvider healthcareProvider = new HealthcareProvider("SGH", "0M");
        Bed bed = generalWard.getBeds().get(1);
        MedicalRecord medicalRecord = new MedicalRecord("0011M", LocalDate.of(2025, 2, 15), Arrays.asList("Aspirin"),
                Arrays.asList("Penicillin"), Arrays.asList("None"), Patient.builder().withRandomData("P1002").build(), "Doc", "2", generalWard, healthcareProvider,
                Arrays.asList("Nurse1"));
        medicalRecord.displayMedicalRecord();
    }
}
