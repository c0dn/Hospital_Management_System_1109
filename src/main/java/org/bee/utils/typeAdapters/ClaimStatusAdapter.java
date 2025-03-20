package org.bee.utils.typeAdapters;

import java.io.IOException;

import org.bee.hms.claims.ClaimStatus;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * TypeAdapter for serializing and deserializing ClaimStatus enum values.
 */
public class ClaimStatusAdapter extends TypeAdapter<ClaimStatus> {

    @Override
    public void write(JsonWriter out, ClaimStatus value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.name());
        }
    }

    @Override
    public ClaimStatus read(JsonReader in) throws IOException {
        String value = in.nextString();
        try {
            return ClaimStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            return ClaimStatus.DRAFT; // Default value
        }
    }
}
