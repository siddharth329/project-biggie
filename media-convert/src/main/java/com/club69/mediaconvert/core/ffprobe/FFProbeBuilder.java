package com.club69.mediaconvert.core.ffprobe;

import com.club69.mediaconvert.core.FFmpegCommand;
import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FFProbeBuilder {
    private String ffProbePath;
    private String input;
    private boolean showFormats;
    private boolean showStreams;

    public List<String> buildCommand() {
        Preconditions.checkArgument(ffProbePath.endsWith("ffprobe.exe"), "Invalid FFProbe Path");
        Preconditions.checkNotNull(input, "Input file cannot be null");
        FFmpegCommand command = new FFmpegCommand();

        command.addArgument("cmd.exe");
        command.addArgument("/c");
        command.addArgument(ffProbePath);
        command.addArgument("\"" + input + "\"");

        command.addArgument(FFProbeArgs.VERBOSE, "quiet");
        command.addArgument(FFProbeArgs.PRINT_FORMAT, "json");
        if (showFormats) command.addArgument(FFProbeArgs.SHOW_FORMATS);
        if (showStreams) command.addArgument(FFProbeArgs.SHOW_STREAMS);

        return command.build();
    }

    private static class FFProbeArgs {
        public static String VERBOSE = "-v";
        public static String PRINT_FORMAT = "-print_format";
        public static String SHOW_FORMATS = "-show_format";
        public static String SHOW_STREAMS = "-show_streams";
    }
}