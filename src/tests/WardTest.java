package tests;

import humans.*;
import medical.MedicalRecord;
import wardsAmelia.*;

import java.time.LocalDate;
import java.util.List;

public class WardTest {
    public static void main(String[] args) {
        Ward generalWard = WardFactory.getWard("G", "General", WardClass.CLASS_C);
        System.out.println(generalWard.getWardName() + " - Daily Rate: " + generalWard.getDailyRate());
        generalWard.getBeds().forEach((bedNumber, bed) -> System.out.println(bed));

        // Assign a patient to Bed 1 in the Labour Ward
        Bed bed1 = generalWard.getBeds().get(1);
        bed1.assignPatient(new Patient("John Doe",
                LocalDate.of(1985, 4, 23),
                "S1234567D",
                MaritalStatus.MARRIED,
                ResidentialStatus.PERMANENT_RESIDENT,
                "Singaporean",
                "123 Fake Street, Singapore 123456",
                new Contact("+65 9123 4567", "12345578", "87655678", "johndoe@email.com"),
                Sex.MALE,
                BloodType.O_POSITIVE,
                true,
                "P123456789",
                List.of("Penicillin", "Aspirin"),

                "Jane Doe",
                "456 Another Street, Singapore 654321",
                "Spouse",
                1.75,
                75.0,
                "Software Engineer",
                "TechCorp Pte Ltd",
                "789 Business Road, Singapore 789654"));
        System.out.println("Updated " + bed1);


    }
}
