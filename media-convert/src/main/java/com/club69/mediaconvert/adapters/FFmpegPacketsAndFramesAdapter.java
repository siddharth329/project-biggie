package com.club69.mediaconvert.adapters;

import com.club69.mediaconvert.mediaconvert.ffprobe.serialize.FFmpegFrame;
import com.club69.mediaconvert.mediaconvert.ffprobe.serialize.FFmpegFrameOrPacket;
import com.club69.mediaconvert.mediaconvert.ffprobe.serialize.FFmpegPacket;
import com.google.gson.*;

import java.lang.reflect.Type;

public class FFmpegPacketsAndFramesAdapter implements JsonDeserializer<FFmpegFrameOrPacket> {
  @Override
  public FFmpegFrameOrPacket deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    if (jsonElement instanceof JsonObject) {
      final String objectType = ((JsonObject) jsonElement).get("type").getAsString();

      if (objectType.equals("packet")) {
        return jsonDeserializationContext.deserialize(jsonElement, FFmpegPacket.class);
      } else {
        return jsonDeserializationContext.deserialize(jsonElement, FFmpegFrame.class);
      }
    }

    return null;
  }
}
