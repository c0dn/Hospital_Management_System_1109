package org.bee.utils.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.bee.hms.wards.Ward;
import org.bee.hms.wards.WardClassType;
import org.bee.hms.wards.WardFactory;

import java.io.IOException;
/**
 * Custom Jackson deserializer for converting JSON data into {@link Ward} objects.
 *
 * <p>Handles JSON structures containing ward information with the following format:</p>
 *
 *   "name": "Emergency Ward",
 *   "classType": "CLASS_A"
 *
 */
public class WardDeserializer extends JsonDeserializer<Ward> {
    /**
     * Deserializes JSON data into a {@link Ward} object.
     *
     * @param p     The JSON parser containing the input data
     * @param ctxt  Context for deserialization process
     * @return      Configured Ward instance
     * @throws IOException If JSON parsing fails or required fields are missing
     */
    @Override
    public Ward deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        String name = node.get("name").asText();
        String classTypeStr = node.get("classType").asText();

        WardClassType wardClassType = WardClassType.fromString(classTypeStr);

        return WardFactory.getWard(name, wardClassType);
    }
}