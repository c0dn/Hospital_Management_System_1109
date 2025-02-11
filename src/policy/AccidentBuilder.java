package policy;

/**
 * Builder class for creating instances of {@link AccidentInsurance}.
 * This class follows the builder pattern and allows setting various attributes
 * specific to accident insurance policies.
 *
 * @see AccidentInsurance
 */
public class AccidentBuilder extends InsuranceBuilder<AccidentBuilder> {
    /** The type of accident covered by the insurance policy. */
    private AccidentsType accidents;
    /** The allowance provided for accident-related expenses. */
    private double allowance;
    /** Default constructor for {@code AccidentBuilder}. */
    public AccidentBuilder() {}

    /**
     * Sets the type of accident covered by the insurance policy.
     *
     * @param accidents The type of accident covered.
     * @return This builder instance.
     */
    public AccidentBuilder accidents(AccidentsType accidents) {
        this.accidents = accidents;
        return this;
    }

    /**
     * Sets the allowance amount for accident-related expenses.
     *
     * @param allowance The accident allowance amount.
     * @return This builder instance.
     */
    public AccidentBuilder allowance(double allowance) {
        this.allowance = allowance;
        return this;
    }

    /**
     * Returns this builder instance.
     *
     * @return This {@code AccidentBuilder} instance.
     */
    @Override
    protected AccidentBuilder self() {
        return this;
    }

    /**
     * Builds and returns an {@link AccidentInsurance} instance with the configured attributes.
     *
     * @return A new {@code AccidentInsurance} instance.
     * @throws IllegalStateException If required fields are not set.
     */
   @Override
   public AccidentInsurance build() {
        validateFields();

        return new AccidentInsurance(
                policyId, insuranceProvider, deductible, insuranceStatus, startDate, endDate,
                coInsuranceRate, premiumAmount, insurancePayout, accidents, allowance,
                insuranceName, insuranceDescription
        );
    }
}
