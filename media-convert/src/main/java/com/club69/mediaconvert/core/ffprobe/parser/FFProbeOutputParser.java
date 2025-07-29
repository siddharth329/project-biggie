package com.club69.mediaconvert.core.ffprobe.parser;

import com.club69.mediaconvert.adapters.FFmpegPacketsAndFramesAdapter;
import com.club69.mediaconvert.adapters.FFmpegStreamSideDataAdapter;
import com.club69.mediaconvert.adapters.FractionAdapter;
import com.club69.mediaconvert.adapters.LowercaseEnumTypeAdapterFactory;
import com.club69.commons.mediaconvert.ffprobe.serialize.FFmpegFrameOrPacket;
import com.club69.commons.mediaconvert.ffprobe.serialize.FFmpegProbeResult;
import com.club69.commons.mediaconvert.ffprobe.serialize.FFmpegStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.math.Fraction;
import org.springframework.stereotype.Component;

@Component
public class FFProbeOutputParser {

    public FFmpegProbeResult parseOutput(String output) {
        Gson gson = getGson();
        return gson.fromJson(output, FFmpegProbeResult.class);
    }

    private Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory());
        gsonBuilder.registerTypeAdapter(Fraction.class, new FractionAdapter());
        gsonBuilder.registerTypeAdapter(FFmpegFrameOrPacket.class, new FFmpegPacketsAndFramesAdapter());
        gsonBuilder.registerTypeAdapter(FFmpegStream.SideData.class, new FFmpegStreamSideDataAdapter());

        return gsonBuilder.create();
    }
}
