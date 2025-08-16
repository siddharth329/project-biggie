package com.club69.commons.mediaconvert.serialize;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class FFmpegFrame implements FFmpegFrameOrPacket {
  @SerializedName("media_type") public String mediaType;
  @SerializedName("stream_index") public int streamIndex;
  @SerializedName("key_frame") public int keyFrame;
  @SerializedName("pkt_pts") public long pktPts;
  @SerializedName("pkt_pts_time") public double pktPtsTime;
  @SerializedName("pkt_dts") public long pktDts;
  @SerializedName("pkt_dts_time") public double pktDtsTime;
  @SerializedName("best_effort_timestamp") public long bestEffortTimestamp;
  @SerializedName("best_effort_timestamp_time") public float bestEffortTimestampTime;
  @SerializedName("pkt_duration") public long pktDuration;
  @SerializedName("pkt_duration_time") public float pktDurationTime;
  @SerializedName("pkt_pos") public long pktPos;
  @SerializedName("pkt_size") public long pktSize;
  @SerializedName("sample_fmt") public String sampleFmt;
  @SerializedName("nb_samples") public int nbSamples;
  @SerializedName("channels") public int channels;
  @SerializedName("channel_layout") public String channelLayout;
}
