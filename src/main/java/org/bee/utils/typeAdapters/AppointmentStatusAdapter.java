package org.bee.utils.typeAdapters;

import java.io.IOException;

import org.bee.hms.telemed.AppointmentStatus;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * TypeAdapter for Gson that handles serialization and deserialization of AppointmentStatus enum.
 * This adapter ensures proper handling of the enum values during JSON conversion.
 */
public class AppointmentStatusAdapter extends TypeAdapter<AppointmentStatus> {

    @Override
    public void write(JsonWriter out, AppointmentStatus value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.name());
        }
    }

    @Override
    public AppointmentStatus read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        
        String enumValue = in.nextString();
        try {
            return AppointmentStatus.valueOf(enumValue);
        } catch (IllegalArgumentException e) {
            // Handle case where the enum value might not exist
            System.err.println("Error deserializing AppointmentStatus: " + e.getMessage());
            return null;
        }
    }
}
