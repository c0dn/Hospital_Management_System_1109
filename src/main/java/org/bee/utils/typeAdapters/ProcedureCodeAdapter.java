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
        out.beginObject();
        out.name("code").value(value.getProcedureCode());
        out.name("description").value(value.getBillItemDescription());
        out.name("price").value(value.getCharges().toString());
        out.endObject();
    }

    @Override
    public ProcedureCode read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        String code = null;
        String description = null;
        BigDecimal price = null;

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "code":
                    code = in.nextString();
                    break;
                case "description":
                    description = in.nextString();
                    break;
                case "price":
                    price = new BigDecimal(in.nextString());
                    break;
            }
        }
        in.endObject();

        // Create a new ProcedureCode object using the parsed values
        ProcedureCode procedureCode = new ProcedureCode(code, description);

        return procedureCode;
    }

}
