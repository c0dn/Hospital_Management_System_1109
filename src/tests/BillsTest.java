package tests;

import billing.Bill;
import billing.BillBuilder;

/**
 * The {@code BillsTest} class tests the creation of bills using the {@code BillBuilder} class.
 */
public class BillsTest {

    /**
     * The main method to execute the billing tests.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        /**
         * Creates a {@code Bill} instance using the {@code BillBuilder}.
         */
        BillBuilder builder = new BillBuilder()
                .withPatientId("P12345");
        Bill bill = builder.build();

    }
}
