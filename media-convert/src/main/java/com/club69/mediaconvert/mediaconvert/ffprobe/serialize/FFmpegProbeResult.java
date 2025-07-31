package com.club69.mediaconvert.mediaconvert.ffprobe.serialize;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class FFmpegProbeResult {
  public FFmpegError error;
  public FFmpegFormat format;
  public List<FFmpegStream> streams;
  public List<FFmpegChapter> chapters;
  public List<FFmpegFrameOrPacket> packets_and_frames;

  public boolean hasError() { return this.error != null; }

  public List<FFmpegPacket> getPackets() {
    return this.getPacketOrFrames(FFmpegPacket.class);
  }

  public List<FFmpegFrame> getFrames() {
    return this.getPacketOrFrames(FFmpegFrame.class);
  }

  private <T> List<T> getPacketOrFrames(Class<T> tClass) {
    if (packets_and_frames != null) {
      List<T> tmp = new ArrayList<>();
      for (FFmpegFrameOrPacket packetsAndFrame : packets_and_frames) {
        if (packetsAndFrame != null) {
          tmp.add((T) packetsAndFrame);
        }
      }
      return tmp;
    } else {
      return Collections.emptyList();
    }
  }
}