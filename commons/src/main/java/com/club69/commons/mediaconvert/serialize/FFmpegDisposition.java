package com.club69.commons.mediaconvert.serialize;

import com.club69.commons.mediaconvert.adapters.BooleanTypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class FFmpegDisposition {
  @SerializedName("default") @JsonAdapter(BooleanTypeAdapter.class) private boolean isdDefault;
  @SerializedName("dub") @JsonAdapter(BooleanTypeAdapter.class) private boolean dub;
  @SerializedName("original") @JsonAdapter(BooleanTypeAdapter.class) private boolean original;
  @SerializedName("comment") @JsonAdapter(BooleanTypeAdapter.class) private boolean comment;
  @SerializedName("lyrics") @JsonAdapter(BooleanTypeAdapter.class) private boolean lyrics;
  @SerializedName("karaoke") @JsonAdapter(BooleanTypeAdapter.class) private boolean karaoke;
  @SerializedName("forced") @JsonAdapter(BooleanTypeAdapter.class) private boolean forced;
  @SerializedName("hearing_impaired") @JsonAdapter(BooleanTypeAdapter.class) private boolean hearingImpaired;
  @SerializedName("visual_impaired") @JsonAdapter(BooleanTypeAdapter.class) private boolean visualImpaired;
  @SerializedName("clean_effects") @JsonAdapter(BooleanTypeAdapter.class) private boolean cleanEffects;
  @SerializedName("attached_pic") @JsonAdapter(BooleanTypeAdapter.class) private boolean attachedPic;
  @SerializedName("timed_thumbnails") @JsonAdapter(BooleanTypeAdapter.class) private boolean timedThumbnails;
  @SerializedName("non_diegetic") @JsonAdapter(BooleanTypeAdapter.class) private boolean nonDiegetic;
  @SerializedName("captions") @JsonAdapter(BooleanTypeAdapter.class) private boolean captions;
  @SerializedName("descriptions") @JsonAdapter(BooleanTypeAdapter.class) private boolean descriptions;
  @SerializedName("metadata") @JsonAdapter(BooleanTypeAdapter.class) private boolean metadata;
  @SerializedName("dependent") @JsonAdapter(BooleanTypeAdapter.class) private boolean dependent;
  @SerializedName("still_image") @JsonAdapter(BooleanTypeAdapter.class) private boolean stillImage;

}
