package com.club69.commons.mediaconvert.serialize;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class FFmpegChapterTag {
  @SerializedName("title") public String title;
}
