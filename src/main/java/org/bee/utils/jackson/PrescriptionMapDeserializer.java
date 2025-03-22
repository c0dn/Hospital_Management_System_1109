package org.bee.utils.jackson;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.bee.hms.medical.Medication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class PrescriptionMapDeserializer extends JsonDeserializer<Map<Medication, Integer>> {
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