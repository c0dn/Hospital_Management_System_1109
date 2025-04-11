/**
 * Contains core insurance policy classes including:
 * <ul>
 *   <li>Policy types ({@link org.bee.hms.policy.BaseInsurancePolicy}, {@link org.bee.hms.policy.HeldInsurancePolicy})</li>
 *   <li>Coverage details ({@link org.bee.hms.policy.BaseCoverage}, {@link org.bee.hms.policy.CompositeCoverage},
 *   {@link org.bee.hms.policy.CoverageLimit})</li>
 *   <li>Claim processing ({@link org.bee.hms.policy.ClaimableItem}, {@link org.bee.hms.policy.InsuranceCoverageResult})</li>
 *   <li>Enums and rules ({@link org.bee.hms.policy.AccidentType}, {@link org.bee.hms.policy.BenefitType},
 *   {@link org.bee.hms.policy.InsuranceStatus}, {@link org.bee.hms.policy.ExclusionCriteria})</li>
 * </ul>
 * <p>
 * These classes manage the insurance policy from creation to claims processing
 * </p>
 */
package org.bee.hms.policy;