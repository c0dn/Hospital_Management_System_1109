package tests;

import billing.Bill;
import billing.BillBuilder;

public class BillsTest {

    public static void main(String[] args) {
        BillBuilder builder = new BillBuilder()
                .withPatientId("P12345");
        Bill bill = builder.build();

    }
}
