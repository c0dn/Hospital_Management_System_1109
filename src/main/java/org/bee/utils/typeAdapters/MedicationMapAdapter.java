package org.bee.utils.typeAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bee.hms.medical.Medication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * TypeAdapter for Gson that handles serialization and deserialization of Map&lt;Medication, Integer&gt;.
 * This adapter is necessary because Medication objects are used as map keys, which requires
 * special handling during JSON serialization/deserialization.
 * <p>
 * The adapter serializes the map as an array of objects, each containing:
 * - drugCode: The unique identifier for the medication
 * - quantity: The integer quantity associated with the medication
 * <p>
 * During deserialization, it uses Medication.createFromCode() to retrieve medication instances
 * from the drug registry.
 */
public class MedicationMapAdapter extends TypeAdapter<Map<Medication, Integer>> {

    @Override
    public void write(JsonWriter out, Map<Medication, Integer> value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        out.beginArray();
        for (Map.Entry<Medication, Integer> entry : value.entrySet()) {
            Medication medication = entry.getKey();
            Integer quantity = entry.getValue();

            out.beginObject();
            out.name("drugCode").value(medication.getDrugCode());
            out.name("quantity").value(quantity);
            out.endObject();
        }
        out.endArray();
    }

    @Override
    public Map<Medication, Integer> read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        Map<Medication, Integer> map = new HashMap<>();
        in.beginArray();
        
        while (in.hasNext()) {
            String drugCode = null;
            Integer quantity = null;
            
            in.beginObject();
            while (in.hasNext()) {
                String name = in.nextName();
                if ("drugCode".equals(name)) {
                    drugCode = in.nextString();
                } else if ("quantity".equals(name)) {
                    quantity = in.nextInt();
                } else {
                    in.skipValue();
                }
            }
            in.endObject();
            
            if (drugCode != null && quantity != null) {
                try {
                    Medication medication = Medication.createFromCode(drugCode);
                    map.put(medication, quantity);
                } catch (IllegalArgumentException e) {
                    // Log error or handle invalid drug code
                    System.err.println("Error deserializing medication: " + e.getMessage());
                }
            }
        }
        
        in.endArray();
        return map;
    }
}
