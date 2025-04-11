package org.bee.hms.policy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a composite coverage that combines multiple insurance coverages.
 * <p>
 * Uses the Composite design pattern to treat multiple coverages as a single entity.
 * It allows the customer to manage multiple insurance coverages in a unified way, providing benefits from each individual coverage.
 * </p>
 */
public class CompositeCoverage implements Coverage {
    private final List<Coverage> coverages;

    /**
     * Creates a composite coverage from multiple coverage objects.
     * <p>
     * The composite coverage aggregates multiple coverage objects and treats them
     * as a single entity.
     * This allows operations to be performed across all contained
     * coverages transparently, following the Composite design pattern.
     * </p>
     * <p>
     * Coverage operations like evaluations, validations, or queries will be delegated
     * to all contained coverages according to the specific implementation rules of
     * this composite.
     * </p>
     *
     * @param coverages the coverage objects to combine into this composite
     * @throws IllegalArgumentException if no coverages are provided or if any coverage is null
     */
    @JsonCreator
    public CompositeCoverage(@JsonProperty("coverages") Coverage... coverages) {
        this.coverages = Arrays.asList(coverages);
    }

    /**
     * Checks if a given item is covered by any of the coverages in the composite.
     * <p>
     * The item is considered covered if it is covered by at least one of the individual coverages in the composite.
     * </p>
     *
     * @param item The claimable item to check coverage for.
     * @param isInpatient A flag indicating whether the item is related to an inpatient treatment.
     * @return true if any of the coverages cover the item, false otherwise.
     */
    @Override
    public boolean isItemCovered(ClaimableItem item, boolean isInpatient) {
        // Item is covered if any of the coverages covers it
        return coverages.stream()
                .anyMatch(coverage -> coverage.isItemCovered(item, isInpatient));
    }

    /**
     * Calculates the total accident payout across all coverages in the composite.
     * <p>
     * This method sums up the accident payouts from each individual coverage and returns the total.
     * </p>
     *
     * @param accidentType The type of accident for which the payout is calculated.
     * @return The total accident payout from all coverages.
     */
    @Override
    public BigDecimal calculateAccidentPayout(AccidentType accidentType) {
        // Sum up all accident payouts from individual coverages
        return coverages.stream()
                .map(coverage -> coverage.calculateAccidentPayout(accidentType))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Returns the maximum deductible amount across all coverages in the composite.
     * <p>
     * The deductible for the composite coverage is the highest deductible amount from any individual coverage.
     * </p>
     *
     * @return The maximum deductible amount from all coverages.
     */
    @Override
    public BigDecimal getDeductibleAmount() {
        return coverages.stream()
                .map(Coverage::getDeductibleAmount)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Calculates the coinsurance amount based on the claim amount, using the minimum coinsurance rate
     * from all the coverages in the composite.
     * <p>
     * The co-insurance rate is applied to the claim amount, and the lowest coinsurance rate across all coverages
     * is used to calculate the payout.
     * </p>
     *
     * @param claimAmount The claim amount for which the coinsurance is calculated.
     * @return The coinsurance amount based on the lowest rate from all coverages.
     */
    @Override
    public BigDecimal calculateCoinsurance(BigDecimal claimAmount) {
        return coverages.stream()
                .map(coverage -> coverage.calculateCoinsurance(claimAmount))
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Returns the combined limits of all coverages in the composite.
     * <p>
     * This method adds up the annual and lifetime limits from each individual coverage and returns the total combined limit.
     * </p>
     *
     * @return The combined coverage limits from all coverages in the composite.
     */
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
    
    /**
     * Returns the combined set of benefit types from all coverages in the composite.
     * <p>
     * This method collects all benefit types from each individual coverage and returns
     * the union of these sets, representing all benefit types covered by any of the
     * component coverages.
     * </p>
     *
     * @return The combined set of benefit types from all coverages.
     */
    @Override
    public Set<BenefitType> getCoveredBenefits() {
        return coverages.stream()
                .flatMap(coverage -> coverage.getCoveredBenefits().stream())
                .collect(Collectors.toSet());
    }
}
