package org.bee.utils.typeAdapters;

import java.io.IOException;

import org.bee.hms.medical.VisitStatus;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * TypeAdapter for Gson that handles serialization and deserialization of VisitStatus enum.
 * This adapter ensures proper handling of the enum values during JSON conversion.
 */
public class VisitStatusAdapter extends TypeAdapter<VisitStatus> {

    @Override
    public void write(JsonWriter out, VisitStatus value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.name());
        }
    }

    @Override
    public VisitStatus read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        
        String enumValue = in.nextString();
        try {
            return VisitStatus.valueOf(enumValue);
        } catch (IllegalArgumentException e) {
            // Handle case where the enum value might not exist
            System.err.println("Error deserializing VisitStatus: " + e.getMessage());
            return null;
        }
    }
}
