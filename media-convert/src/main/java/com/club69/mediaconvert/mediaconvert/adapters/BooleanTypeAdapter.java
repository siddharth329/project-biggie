package com.club69.mediaconvert.mediaconvert.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class BooleanTypeAdapter implements JsonDeserializer<Boolean> {
    @Override
    public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isBoolean()) {
            return json.getAsBoolean();
        }

        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            String jsonValue = json.getAsString();
            if (jsonValue.equalsIgnoreCase("true")) {
                return true;
            } else if (jsonValue.equalsIgnoreCase("false")) {
                return false;
            } else {
                return null;
            }
        }

        return json.getAsInt() != 0;
    }
}
