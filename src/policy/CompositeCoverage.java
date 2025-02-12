package policy;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a composite coverage that combines multiple insurance coverages.
 * This implementation uses the Composite pattern to treat multiple coverages as one.
 * Composition logic is beneficial to the customer
 */
public class CompositeCoverage implements Coverage {
    private final List<Coverage> coverages;

    /**
     * Creates a composite coverage from multiple coverage objects.
     *
     * @param coverages the coverage objects to combine
     */
    public CompositeCoverage(Coverage... coverages) {
        this.coverages = Arrays.asList(coverages);
    }

    @Override
    public boolean isItemCovered(ClaimableItem item, boolean isInpatient) {
        // Item is covered if any of the coverages covers it
        return coverages.stream()
                .anyMatch(coverage -> coverage.isItemCovered(item, isInpatient));
    }

    @Override
    public BigDecimal calculateAccidentPayout(AccidentType accidentType) {
        // Sum up all accident payouts from individual coverages
        return coverages.stream()
                .map(coverage -> coverage.calculateAccidentPayout(accidentType))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getDeductibleAmount() {
        return coverages.stream()
                .map(Coverage::getDeductibleAmount)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal calculateCoinsurance(BigDecimal claimAmount) {
        return coverages.stream()
                .map(coverage -> coverage.calculateCoinsurance(claimAmount))
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public CoverageLimit getLimits() {
        return coverages.stream()
                .map(Coverage::getLimits)
                .reduce(new CoverageLimit.Builder().build(),
                        (limit1, limit2) -> new CoverageLimit.Builder()
                                .withAnnualLimit(limit1.getAnnualLimit().add(limit2.getAnnualLimit()))
                                .withLifetimeLimit(limit1.getLifetimeLimit().add(limit2.getLifetimeLimit()))
                                .build());
    }
}