package org.bee.utils.jackson;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.bee.hms.medical.Medication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 * Custom deserializer for converting JSON objects into a {@code Map<Medication, Integer>}.
 *
 * This deserializer is designed to handle JSON structures where:
 * - The keys represent medication codes (e.g., "ASPIRIN-500").
 * - The values represent integer quantities of the medications.
 *
 */

public class PrescriptionMapDeserializer extends JsonDeserializer<Map<Medication, Integer>> {
    /**
     * Deserializes a JSON object into a {@code Map<Medication, Integer>}.
     *
     * @param p     The {@link JsonParser} used to read the JSON content.
     * @param ctxt  The {@link DeserializationContext} for deserialization.
     * @return A map where the keys are {@link Medication} objects created from medication codes,
     *         and the values are quantities of those medications.
     * @throws IOException If an error occurs during deserialization or if the JSON structure is invalid.
     */
    @Override
    public Map<Medication, Integer> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Map<Medication, Integer> result = new HashMap<>();

        if (p.currentToken() == JsonToken.VALUE_NULL) {
            return result;
        }

        if (p.currentToken() != JsonToken.START_OBJECT) {
            throw new IOException("Expected START_OBJECT token for medication map, got " + p.currentToken());
        }

        while (p.nextToken() != JsonToken.END_OBJECT) {
            String drugCode = p.getCurrentName();
            p.nextToken();
            int quantity = p.getIntValue();

            Medication medication = Medication.createFromCode(drugCode);
            result.put(medication, quantity);
        }

        return result;
    }
}