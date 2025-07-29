package com.club69.commons.mediaconvert.ffprobe.serialize;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Map;

@Data
public class FFmpegFormat {
  @SerializedName("filename") public String filename;
  @SerializedName("nb_streams") public int nbStreams;
  @SerializedName("nb_programs") public int nbPrograms;
  @SerializedName("nb_stream_groups") public int nbStreamGroups;
  @SerializedName("format_name") public String formatName;
  @SerializedName("format_long_name") public String formatLongName;
  @SerializedName("start_time") public double startTime;
  @SerializedName("duration") public double duration;
  @SerializedName("size") public long size;
  @SerializedName("bit_rate") public long bitRate;
  @SerializedName("probe_score") public int probeScore;
  @SerializedName("String") public Map<String, String> tags;
}
