package org.bee.utils.typeAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bee.hms.medical.ProcedureCode;

import java.io.IOException;
import java.math.BigDecimal;


public class ProcedureCodeAdapter extends TypeAdapter<ProcedureCode> {

    @Override
    public void write(JsonWriter out, ProcedureCode value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        out.value(value.getProcedureCode());
    }

    @Override
    public ProcedureCode read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        if (in.peek() == JsonToken.STRING) {
            String code = in.nextString();
            return ProcedureCode.createFromCode(code);
        }


        throw new IOException("Expected STRING or OBJECT for ProcedureCode, but was " + in.peek());
    }
}
