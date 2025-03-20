package org.bee.utils.typeAdapters;

import java.io.IOException;

import org.bee.hms.telemed.SessionStatus;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * TypeAdapter for Gson that handles serialization and deserialization of SessionStatus enum.
 * This adapter ensures proper handling of the enum values during JSON conversion.
 */
public class SessionStatusAdapter extends TypeAdapter<SessionStatus> {

    @Override
    public void write(JsonWriter out, SessionStatus value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.name());
        }
    }

    @Override
    public SessionStatus read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        
        String enumValue = in.nextString();
        try {
            return SessionStatus.valueOf(enumValue);
        } catch (IllegalArgumentException e) {
            // Handle case where the enum value might not exist
            System.err.println("Error deserializing SessionStatus: " + e.getMessage());
            return null;
        }
    }
}
