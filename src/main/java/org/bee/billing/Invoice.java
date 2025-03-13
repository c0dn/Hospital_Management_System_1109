/*
package com.healthcare.models;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.healthcare.billing.Billing;
import com.healthcare.billing.Payment;
import com.healthcare.insurance.Insurance;

*/
/**
 * The {@code Invoice} class represents an invoice for a patient, including
 * details such as billing information, services provided, insurance claims,
 * and payment details.
 *//*

public class Invoice {

    // Invoice attributes
    private String invoiceId;
    private LocalDate billingDate;
    private String billingAddress;
    private String additionalNotes;
    private Patient patient;
    private List<Service> services;
    private Insurance insuranceClaim;
    private Billing billing;
    private Payment payment;

    */
/**
     * Constructs an invoice with the specified details.
     * 
     * @param invoiceId       The unique invoice identifier.
     * @param billingDate     The date the invoice was issued.
     * @param patient         The patient associated with the invoice.
     * @param additionalNotes Any additional notes for the invoice.
     * @param discount        The discount percentage applied.
     * @param taxRate         The tax rate percentage applied.
     *//*

    public Invoice(String invoiceId, LocalDate billingDate, Patient patient, String additionalNotes, int discount, int taxRate) {
        this.invoiceId = invoiceId;
        this.billingDate = billingDate;
        this.patient = patient;
        this.services = new ArrayList<>();
        this.billingAddress = patient.getBillAdd();
        this.additionalNotes = additionalNotes;
        this.billing = new Billing(discount, taxRate);
    }

    */
/**
     * Retrieves the invoice ID.
     * 
     * @return The invoice ID.
     *//*

    public String getInvoiceId() {
        return invoiceId;
    }

    */
/**
     * Sets the invoice ID.
     * 
     * @param invoiceId The new invoice ID.
     *//*

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    */
/**
     * Retrieves the billing date.
     * 
     * @return The billing date.
     *//*

    public LocalDate getBillingDate() {
        return billingDate;
    }

    */
/**
     * Sets the billing date.
     * 
     * @param billingDate The new billing date.
     *//*

    public void setBillingDate(LocalDate billingDate) {
        this.billingDate = billingDate;
    }

    */
/**
     * Retrieves the billing address.
     * 
     * @return The billing address.
     *//*

    public String getBillingAddress() {
        return billingAddress;
    }

    */
/**
     * Sets the billing address.
     * 
     * @param billingAddress The new billing address.
     *//*

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    */
/**
     * Retrieves additional notes associated with the invoice.
     * 
     * @return The additional notes.
     *//*

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    */
/**
     * Sets additional notes for the invoice.
     * 
     * @param additionalNotes The additional notes to be set.
     *//*

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    */
/**
     * Retrieves the patient associated with this invoice.
     * 
     * @return The patient.
     *//*

    public Patient getPatient() {
        return patient;
    }

    */
/**
     * Sets the patient for this invoice.
     * 
     * @param patient The patient to be assigned to the invoice.
     *//*

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    */
/**
     * Retrieves the insurance claim associated with this invoice.
     * 
     * @return The insurance claim, if available.
     *//*

    public Insurance getInsuranceClaim() {
        return insuranceClaim;
    }

    */
/**
     * Sets the insurance claim for this invoice.
     * 
     * @param insuranceClaim The insurance claim to be associated.
     *//*

    public void setInsuranceClaim(Insurance insuranceClaim) {
        this.insuranceClaim = insuranceClaim;
    }

    */
/**
     * Adds a service to the invoice.
     * 
     * @param service The service to be added.
     *//*

    public void addService(Service service) {
        services.add(service);
    }

    */
/**
     * Retrieves the billing details of the invoice.
     * 
     * @return The billing object containing discount and tax rate details.
     *//*

    public Billing getBilling() {
        return billing;
    }

    */
/**
     * Calculates the total amount for the invoice, including discounts and tax.
     *//*

    public void calculateBill() {
        billing.calculateTotalAmount(services);
    }

    */
/**
     * Retrieves the list of services included in the invoice.
     * 
     * @return The list of services.
     *//*

    public List<Service> getServices() {
        return this.services;
    }

    */
/**
     * Sets the payment details for this invoice.
     * 
     * @param payment The payment details.
     *//*

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    */
/**
     * Retrieves the payment details of the invoice.
     * 
     * @return The payment details.
     *//*

    public Payment getPayment() {
        return payment;
    }

    */
/**
     * Prints the formatted invoice details, including patient information,
     * services rendered, discounts, tax, and payment details.
     *//*

    public void printInvoice() {
        System.out.println("=".repeat(75));
        System.out.println(" ".repeat(30) + "Invoice Details" + " ".repeat(30));
        System.out.println("=".repeat(75));
        System.out.printf("Invoice ID: %-20s\n", invoiceId);
        System.out.printf("Bill Date: %-20s\n", billingDate);
        System.out.printf("Billing Address: %-20s\n", billingAddress);
        System.out.printf("Additional Notes: %-20s\n", additionalNotes);
        System.out.println("-".repeat(75));
        System.out.printf("Patient Name: %-20s\n", patient.getName());
        System.out.printf("Phone Number: %-20s\n", patient.getPhoneNum());
        System.out.printf("Nationality: %-20s\n", patient.getNationality());
        System.out.println("-".repeat(75));

        // Display service details
        int index = 0;
        System.out.printf("%-2s %-19s %-19s %-12s %s\n", " ", "Service", "Date of Service", "Quantity", "Amount");
        for (Service service : services) {
            System.out.printf("%d %-21s %-20s %-9s $%,.2f\n", index + 1, service.getServiceDescript(), service.getServiceDate(), service.getQuantity(), service.calculatePrice());
            index++;
        }

        // Calculate and display billing details
        double discountAmount = billing.calculateDiscountAmount();
        double taxAmount = billing.calculateTaxAmount();
        double grandTotal = billing.calculateGrandTotal();
        
        System.out.println("-".repeat(75));
        System.out.printf("Total Amount (Before Discounts and Tax): $%.2f\n", billing.getTotalAmount());
        System.out.printf("Discount (%d%%): -$%.2f\n", billing.getDiscount(), discountAmount);
        System.out.printf("Tax (%d%%): +$%.2f\n", billing.getTaxRate(), taxAmount);
        System.out.printf("Grand Total: $%.2f\n", grandTotal);
        System.out.println("=".repeat(75));

        // Print insurance claim details if applicable
        if (insuranceClaim != null) {
            System.out.println("Insurance Coverage Summary");
            System.out.printf("Policy ID: %s\n", insuranceClaim.getPolicyID());
            System.out.printf("Company: %s\n", insuranceClaim.getInsuranceCompany());
            System.out.printf("Coverage Percentage: %.2f%%\n", insuranceClaim.getCoveragePercentage());
            System.out.printf("Remaining Coverage Limit: $%.2f\n", insuranceClaim.getRemainingCoverage());
            System.out.printf("Covered by Insurance: $%.2f\n", insuranceClaim.getApprovedAmount());
            System.out.printf("Final Amount Payable: $%.2f\n", grandTotal - insuranceClaim.getApprovedAmount());
        } else {
            System.out.println("No Insurance Coverage Applied");
            System.out.printf("Final Amount Payable: $%.2f\n", grandTotal);
        }

        System.out.println("-".repeat(75));
        System.out.printf("Bill Due by: %s\n", payment.getDueDate());
        System.out.printf("Payment Status: %s\n", payment.getPaymentStatus());
        System.out.printf("Payment By: %s\n", payment.getPaymentMethod());

        System.out.println("=".repeat(75));
        System.out.println("");
    }
}
*/
