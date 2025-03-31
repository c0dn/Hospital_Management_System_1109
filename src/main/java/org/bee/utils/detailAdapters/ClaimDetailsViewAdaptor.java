package org.bee.utils.detailAdapters;

import org.bee.hms.billing.Bill;
import org.bee.hms.claims.ClaimStatus;
import org.bee.hms.claims.InsuranceClaim;
import org.bee.hms.humans.Patient;
import org.bee.hms.policy.InsuranceCoverageResult;
import org.bee.hms.policy.InsurancePolicy;
import org.bee.ui.details.IDetailsViewAdapter;
import org.bee.ui.details.IObjectDetailsAdapter;
import org.bee.ui.views.DetailsView;
import org.bee.ui.views.ObjectDetailsView;
import org.bee.utils.ReflectionHelper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ClaimDetailsViewAdaptor implements IObjectDetailsAdapter<InsuranceClaim> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public ObjectDetailsView configureView(ObjectDetailsView view, InsuranceClaim claim) {
        view.setSectionWidth(80);

        ObjectDetailsView.Section basicSection = view.addSection("Claim Information");
        basicSection.addField(new ObjectDetailsView.Field<InsuranceClaim>("Claim ID", c ->
                ReflectionHelper.propertyAccessor("claimId", "Not available").apply(c)));

        basicSection.addField(new ObjectDetailsView.Field<InsuranceClaim>("Submission Date", c -> {
            LocalDateTime claimSubmissionDate = (LocalDateTime) ReflectionHelper.propertyAccessor("submissionDate", null).apply(c);
            return claimSubmissionDate != null ? DATE_FORMATTER.format(claimSubmissionDate) : "Not available";
        }));

        basicSection.addField(new ObjectDetailsView.Field<InsuranceClaim>("Status", c ->
                Optional.ofNullable(c.getClaimStatus()).map(Object::toString).orElse("Not available")));

        ObjectDetailsView.Section patientSection = view.addSection("Patient Information");
        patientSection.addField(new ObjectDetailsView.Field<InsuranceClaim>("Patient", c -> {
            Patient patient = c.getPatient();
            return patient != null ? patient.getName() + " (ID: " + patient.getPatientId() + ")" : "Not available";
        }));

        patientSection.addField((new ObjectDetailsView.Field<InsuranceClaim>("Residential Status", c -> {
            Patient patient = c.getPatient();
            if (patient == null) return "Not available";

            Object residentialStatus = ReflectionHelper.propertyAccessor("residentialStatus", null).apply(patient);
            return residentialStatus != null ? residentialStatus.toString() : "Not available";
        })));

        ObjectDetailsView.Section insuranceSection = view.addSection("Insurance Information");
        insuranceSection.addField(new ObjectDetailsView.Field<InsuranceClaim>("Insurance Provider", c -> {
            InsurancePolicy policy = c.getBill().getInsurancePolicy();;
            return policy != null ? policy.getPolicyNumber() : "No policy";
        }));

        insuranceSection.addField(new ObjectDetailsView.Field<InsuranceClaim>("Policy Active", c -> {
            Bill b = c.getBill();
            InsurancePolicy policy = b.getInsurancePolicy();
            return policy != null ? (policy.isActive() ? "Yes" : "No") : "No policy";
        }));

        ObjectDetailsView.Section billSection = view.addSection("Bill Information");
        billSection.addField(new ObjectDetailsView.Field<InsuranceClaim>("Grand Total", c -> {
            Bill b = c.getBill();
            return formatCurrency(b.getGrandTotal());
        }));

        billSection.addField(new ObjectDetailsView.Field<InsuranceClaim>("Claim amount", c -> formatCurrency(c.getClaimAmount())));

        billSection.addField(new ObjectDetailsView.Field<InsuranceClaim>("Claim Status", c -> c.getClaimStatus().getDisplayName()));

        if (claim.isApproved()) {
            billSection.addField(new ObjectDetailsView.Field<InsuranceClaim>("Approved Amount", c -> formatCurrency(c.getApprovedAmount())));

        }


        return view;
    }

    @Override
    public String getObjectTypeName() {
        return "InsuranceClaim";
    }

    /**
     * Format a BigDecimal as a currency string
     */
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "$0.00";
        }
        return String.format("$%.2f", amount.doubleValue());
    }
}
