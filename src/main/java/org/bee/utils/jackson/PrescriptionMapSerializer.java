package org.bee.utils.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bee.hms.medical.Medication;

import java.io.IOException;
import java.util.Map;

/**
 * Custom serializer for converting a {@code Map<Medication, Integer>} into JSON format.
 *
 * This serializer is designed to handle maps where:
 * - The keys are {@link Medication} objects.
 * - The values are integer quantities of the medications.
 *
 */
public class PrescriptionMapSerializer extends JsonSerializer<Map<Medication, Integer>> {
    /**
     * Serializes a {@code Map<Medication, Integer>} into JSON format.
     *
     * @param value       The map to be serialized. Keys are {@link Medication} objects, and values are quantities.
     * @param gen         The {@link JsonGenerator} used to write the JSON output.
     * @param serializers The {@link SerializerProvider} that can be used to get serializers for serializing objects.
     * @throws IOException If an error occurs during serialization.
     */
    @Override
    public void serialize(Map<Medication, Integer> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        gen.writeStartObject();
        for (Map.Entry<Medication, Integer> entry : value.entrySet()) {
            gen.writeNumberField(entry.getKey().getDrugCode(), entry.getValue());
        }
        gen.writeEndObject();
    }
}