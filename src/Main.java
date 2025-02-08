import medical.Medication;
import tests.DiagnosticCodeTest;
import tests.ProcedureCodeTest;

import java.util.List;
import java.util.Objects;

/**
 * The main entry point for the insurance system.
 * This class initializes the system and processes user input.
 */

public class Main {
    /**
     * This method demonstrates how to create a medication from its code, check its category,
     * and print information about the medication. It also retrieves a list of medications
     * from a specific category.
     * @param args
     */
    public static void main(String[] args) {

        Medication med = Medication.createFromCode("D0001");
        if (Objects.equals(med.getCategory(), "Antibiotics")) {
            System.out.println("Yes");
        }
        med.printDrugInformation();


        List<Medication> antibiotics = Medication.getMedicationsByCategory("Antibiotics", 5, true);

    }
}