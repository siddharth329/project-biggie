package com.club69.commons.mediaconvert.serialize;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class FFmpegPacket implements FFmpegFrameOrPacket {
  @SerializedName("codec_type") public String codecType;
  @SerializedName("stream_index") public int streamIndex;
  @SerializedName("pts") public long pts;
  @SerializedName("pts_time") public double ptsTime;
  @SerializedName("dts") public long dts;
  @SerializedName("dts_time") public double dtsTime;
  @SerializedName("duration") public long duration;
  @SerializedName("duration_time") public float durationTime;
  @SerializedName("size") public String size;
  @SerializedName("pos") public String pos;
  @SerializedName("flags") public String flags;

}
