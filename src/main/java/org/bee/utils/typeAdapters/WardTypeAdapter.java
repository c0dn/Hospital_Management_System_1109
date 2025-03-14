package org.bee.utils.typeAdapters;

import com.google.gson.*;
import org.bee.hms.wards.Ward;
import org.bee.hms.wards.WardClassType;
import org.bee.hms.wards.WardFactory;

import java.lang.reflect.Type;

public class WardTypeAdapter implements JsonSerializer<Ward>, JsonDeserializer<Ward> {
    private static final String NAME_FIELD = "wardName";
    private static final String CLASS_TYPE_FIELD = "wardClassType";

    @Override
    public JsonElement serialize(Ward src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src);
    }

    @Override
    public Ward deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String wardName = jsonObject.get(NAME_FIELD).getAsString();
        WardClassType wardClassType = context.deserialize(
                jsonObject.get(CLASS_TYPE_FIELD), WardClassType.class);

        return WardFactory.getWard(wardName, wardClassType);
    }
}