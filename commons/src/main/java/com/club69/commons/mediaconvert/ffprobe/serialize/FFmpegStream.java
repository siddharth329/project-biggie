package com.club69.commons.mediaconvert.ffprobe.serialize;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.apache.commons.lang3.math.Fraction;

import java.util.Map;

@Data
public class FFmpegStream {
  @SerializedName("index") public int index;
  @SerializedName("codec_name") public String codecName;
  @SerializedName("codec_long_name") public String codecLongName;
  @SerializedName("profile") public String profile;
  @SerializedName("codec_type") public String codecType;
  @SerializedName("codec_time_base") public Fraction codecTimeBase;
  @SerializedName("codec_tag_string") public String codecTagString;
  @SerializedName("codec_tag") public String codecTag;
  @SerializedName("width") public int width;
  @SerializedName("height") public int height;
  @SerializedName("has_b_frames") public int hasBFrames;
  @SerializedName("sample_aspect_ratio") public String sampleAspectRatio; // TODO Change to a Ratio/Fraction object
  @SerializedName("display_aspect_ratio") public String displayAspectRatio;
  @SerializedName("pix_fmt") public String pixFmt;
  @SerializedName("level") public int level;
  @SerializedName("chroma_location") public String chromaLocation;
  @SerializedName("refs") public int refs;
  @SerializedName("is_avc") public String isAvc;
  @SerializedName("nal_length_size") public String nalLengthSize;
  @SerializedName("id") public String id;
  @SerializedName("r_frame_rate") public Fraction rFrameRate;
  @SerializedName("avg_frame_rate") public Fraction avgFrameRate;
  @SerializedName("time_base") public Fraction timeBase;
  @SerializedName("start_pts") public long startPts;
  @SerializedName("start_time") public double startTime;
  @SerializedName("duration_ts") public long durationTs;
  @SerializedName("duration") public double duration;
  @SerializedName("bit_rate") public long bitRate;
  @SerializedName("max_bit_rate") public long maxBitRate;
  @SerializedName("bits_per_raw_sample") public int bitsPerRawSample;
  @SerializedName("bits_per_sample") public int bitsPerSample;
  @SerializedName("nb_frames") public long nbFrames;
  @SerializedName("sample_fmt") public String sampleFmt;
  @SerializedName("sample_rate") public int sampleRate;
  @SerializedName("channels") public int channels;
  @SerializedName("channel_layout") public String channelLayout;

  @SerializedName("disposition") public FFmpegDisposition disposition;
  @SerializedName("tags") public Map<String, String> tags;
  @SerializedName("side_data_list") public SideData[] sideDataList;


  @Data
  public static class SideData {
    @SerializedName("side_data_type") public String sideDataType;
    @SerializedName("displaymatrix") public String displaymatrix;
    @SerializedName("rotation") public int rotation;
  }
}
