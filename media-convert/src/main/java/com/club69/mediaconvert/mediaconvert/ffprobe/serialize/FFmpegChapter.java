package com.club69.mediaconvert.mediaconvert.ffprobe.serialize;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class FFmpegChapter {
  @SerializedName("id") public long id;
  @SerializedName("time_base") public String timeBase;
  @SerializedName("start") public long start;
  @SerializedName("start_time") public String startTime;
  @SerializedName("end") public long end;
  @SerializedName("end_time") public String endTime;
  @SerializedName("tags") public FFmpegChapterTag tags;
}
