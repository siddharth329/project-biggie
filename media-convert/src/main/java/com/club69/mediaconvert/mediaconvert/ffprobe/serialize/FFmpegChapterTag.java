package com.club69.mediaconvert.mediaconvert.ffprobe.serialize;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class FFmpegChapterTag {
  @SerializedName("title") public String title;
}
