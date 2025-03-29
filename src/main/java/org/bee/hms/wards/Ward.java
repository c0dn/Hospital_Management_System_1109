package org.bee.hms.wards;

import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.bee.utils.JSONSerializable;
import org.bee.utils.jackson.WardDeserializer;
import org.bee.utils.jackson.WardSerializer;

/**
 * Represents a hospital ward with custom JSON serialization capabilities
 * <p>
 * Implementations of this interface will be:
 * <ul>
 * <li>Serialized using {@link WardSerializer}</li>
 * <li>Deserialized using {@link WardDeserializer}</li>
 * </ul>
 *
 */
@JsonSerialize(using = WardSerializer.class)
@JsonDeserialize(using = WardDeserializer.class)
public interface Ward extends JSONSerializable {
    /**
     * Retrieves the name of the ward.
     *
     * @return The ward name.
     */
    String getWardName();
    /**
     * Retrieves the daily rate for staying in this ward.
     *
     * @return The daily rate of the ward.
     */
    double getDailyRate();
    /**
     * Retrieves a map of bed numbers to their corresponding beds.
     *
     * @return A map containing bed numbers and bed objects.
     */
    Map<Integer, Bed> getBeds();
}
