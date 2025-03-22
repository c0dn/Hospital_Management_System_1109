package org.bee.utils.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bee.hms.medical.Medication;

import java.io.IOException;
import java.util.Map;

public class PrescriptionMapSerializer extends JsonSerializer<Map<Medication, Integer>> {
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