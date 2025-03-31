/**
 * Provides interfaces for creating adapter components that configure
 * detail views for domain objects.
 * <p>
 * This package contains adapter interfaces that define how to transform
 * and display detailed object information in the UI. These adapters follow
 * the adapter pattern to provide a bridge between domain model objects and
 * view components, separating display logic from data access.
 * <p>The adapter interfaces enable:</p>
 * <ul>
 *   <li>Consistent presentation of domain objects in detail views</li>
 *   <li>Type-safe configuration of view components</li>
 *   <li>Separation of display logic from view implementation</li>
 *   <li>Reusable display patterns for similar object types</li>
 * </ul>
 * <p>The main interfaces in this package are:</p>
 * <ul>
 *   <li>{@link org.bee.ui.details.IDetailsViewAdapter} - For configuring
 *       {@link org.bee.ui.views.DetailsView} instances with object details</li>
 *   <li>{@link org.bee.ui.details.IObjectDetailsAdapter} - For configuring
 *       {@link org.bee.ui.views.ObjectDetailsView} instances with object details</li>
 * </ul>
 */
package org.bee.ui.details;