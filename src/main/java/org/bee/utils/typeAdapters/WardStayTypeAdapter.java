package org.bee.utils.typeAdapters;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

import org.bee.hms.medical.WardStay;
import org.bee.hms.wards.Ward;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * TypeAdapter for Gson that serializes and deserializes WardStay objects.
 */
public class WardStayTypeAdapter implements JsonSerializer<WardStay>, JsonDeserializer<WardStay> {
    private static final String WARD_FIELD = "ward";
    private static final String START_DATE_TIME_FIELD = "startDateTime";
    private static final String END_DATE_TIME_FIELD = "endDateTime";

    @Override
    public JsonElement serialize(WardStay src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        
        try {
            // Use reflection to access private fields
            Field wardField = WardStay.class.getDeclaredField(WARD_FIELD);
            Field startDateTimeField = WardStay.class.getDeclaredField(START_DATE_TIME_FIELD);
            Field endDateTimeField = WardStay.class.getDeclaredField(END_DATE_TIME_FIELD);
            
            // Make the fields accessible
            wardField.setAccessible(true);
            startDateTimeField.setAccessible(true);
            endDateTimeField.setAccessible(true);
            
            // Get the field values
            Ward ward = (Ward) wardField.get(src);
            LocalDateTime startDateTime = (LocalDateTime) startDateTimeField.get(src);
            LocalDateTime endDateTime = (LocalDateTime) endDateTimeField.get(src);
            
            // Serialize the fields
            jsonObject.add(WARD_FIELD, context.serialize(ward));
            jsonObject.add(START_DATE_TIME_FIELD, context.serialize(startDateTime));
            jsonObject.add(END_DATE_TIME_FIELD, context.serialize(endDateTime));
            
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new JsonParseException("Error accessing WardStay fields", e);
        }
        
        return jsonObject;
    }

    @Override
    public WardStay deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        
        // Deserialize the ward
        Ward ward = context.deserialize(jsonObject.get(WARD_FIELD), Ward.class);
        
        // Deserialize the start and end date times
        LocalDateTime startDateTime = context.deserialize(
                jsonObject.get(START_DATE_TIME_FIELD), LocalDateTime.class);
        LocalDateTime endDateTime = context.deserialize(
                jsonObject.get(END_DATE_TIME_FIELD), LocalDateTime.class);
        
        // Create and return a new WardStay instance
        return new WardStay(ward, startDateTime, endDateTime);
    }
}
