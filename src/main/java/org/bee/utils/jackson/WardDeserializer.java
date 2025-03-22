package org.bee.utils.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.bee.hms.wards.Ward;
import org.bee.hms.wards.WardClassType;
import org.bee.hms.wards.WardFactory;

import java.io.IOException;

public class WardDeserializer extends JsonDeserializer<Ward> {
    @Override
    public Ward deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        String name = node.get("name").asText();
        String classTypeStr = node.get("classType").asText();

        WardClassType wardClassType = WardClassType.fromString(classTypeStr);

        return WardFactory.getWard(name, wardClassType);
    }
}