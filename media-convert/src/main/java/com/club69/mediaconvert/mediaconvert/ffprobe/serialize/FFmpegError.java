package com.club69.mediaconvert.mediaconvert.ffprobe.serialize;

import lombok.Data;

import java.io.Serializable;

@Data
public class FFmpegError implements Serializable {
  private static final long serialVersionUID = 1L;
  public int code;
  public String string;
}
