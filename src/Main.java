import tests.DiagnosticCodeTest;
import tests.ProcedureCodeTest;

/**
 * The main entry point for the insurance system.
 * This class initializes the system and processes user input.
 */

public class Main {
    public static void main(String[] args) {

        Medication med = Medication.createFromCode("D0001");
        if (Objects.equals(med.getCategory(), "Antibiotics")) {
            System.out.println("Yes");
        }
        med.printDrugInformation();


        List<Medication> antibiotics = Medication.getMedicationsByCategory("Antibiotics", 5, true);

    }
}