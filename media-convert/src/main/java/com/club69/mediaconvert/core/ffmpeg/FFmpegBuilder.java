package com.club69.mediaconvert.core.ffmpeg;

import lombok.*;

@Data
@Builder
public class FFmpegBuilder {
//    private String ffmpegPath;
//    private String input;
//    private List<MediaConversionRequest.Profile> profiles;
//
//    private Boolean hardwareAccelerationEnabled = false;
//
//    public List<String> buildCommand() {
//        Preconditions.checkArgument(ffmpegPath.endsWith("ffmpeg.exe"), "Invalid FFmpeg Path");
//        Preconditions.checkNotNull(input, "Input file cannot be null");
//        FFmpegCommand command = new FFmpegCommand();
//
//        command.addCommands(generateBaseCommand());


//        AtomicInteger index = new AtomicInteger(0);
//        profiles.forEach(profile -> {
//            int currentIndex = index.incrementAndGet();
//
//            FFmpegCommand subCommand = new FFmpegCommand();
//            subCommand.addCommands(mapVideoCommands(profile.videoProfile, currentIndex));
//            subCommand.addCommands(mapAudioCommands(profile.audioProfile, currentIndex));
//
//            subCommand.addArgument(profile.filename);
//            command.addCommands(subCommand);
//        });
//
//        command.addArgument(FFmpegArgs.MOV_FLAGS, profile.movFlags);

//        return command.build();
//    }

//    private FFmpegCommand generateBaseCommand() {
//        FFmpegCommand command = new FFmpegCommand();
//        command.addArgument("cmd.exe");
//        command.addArgument("/c");
//        command.addArgument(ffmpegPath);
//
//        if (hardwareAccelerationEnabled) {
//            command.addArgument(FFmpegArgs.HARDWARE_ACCELERATION, "cuda");
//            command.addArgument(FFmpegArgs.HARDWARE_ACCELERATION_OUTPUT_FORMAT, "cuda");
//        }
//
//        command.addArgument(FFmpegArgs.VERBOSE, "info"); // Setting the verbosity of output to info to parse progress data
//        command.addArgument(FFmpegArgs.OVERRIDE_OUTPUT); // Override the file if already present
//        command.addArgument(FFmpegArgs.INPUT, input); // Setting the input
//
//        return command;
//    }
//
//    private FFmpegCommand mapVideoCommands(MediaConversionRequest.VideoProfile videoProfile) {
//        FFmpegCommand subCommand = new FFmpegCommand();
//
//        subCommand.addArgument(FFmpegArgs.FFmpegVideoArgs.CODEC, videoProfile.videoCodec);
//        subCommand.addArgument(FFmpegArgs.FFmpegVideoArgs.CONSTANT_RATE_FACTOR, videoProfile.crf);
//        subCommand.addArgument(FFmpegArgs.FFmpegVideoArgs.TUNE, videoProfile.tune);
//        subCommand.addArgument(FFmpegArgs.FFmpegVideoArgs.PROFILE, videoProfile.profile);
//        subCommand.addArgument(FFmpegArgs.FFmpegVideoArgs.LEVEL, videoProfile.level);
//        subCommand.addArgument(FFmpegArgs.FFmpegVideoArgs.MAX_RATE, videoProfile.maxRate);
//        subCommand.addArgument(FFmpegArgs.FFmpegVideoArgs.BUFFER_SIZE, videoProfile.bufferSize);
//        subCommand.addArgument(FFmpegArgs.FFmpegVideoArgs.FRAME_RATE, videoProfile.frameRate);
//        subCommand.addArgument(FFmpegArgs.FFmpegVideoArgs.MIN_INTERVAL_BTW_KEYFRAMES, videoProfile.keyIntMin);
//        subCommand.addArgument(FFmpegArgs.FFmpegVideoArgs.GROUP_OF_PICTURES, videoProfile.groupOfPicturesSize);
//        subCommand.addArgument(FFmpegArgs.FFmpegVideoArgs.SCENE_CHANGE_THRESHOLD, videoProfile.scThreshold);
//        subCommand.addArgument(FFmpegArgs.FFmpegVideoArgs.PIXEL_FORMAT, videoProfile.pixelFormat);
//
//        return subCommand;
//    }
//
//    private FFmpegCommand mapAudioCommands(MediaConversionRequest.AudioProfile audioOutput) {
//        FFmpegCommand subCommand = new FFmpegCommand();
//
//        subCommand.addArgument(FFmpegArgs.FFmpegAudioArgs.CODEC, audioOutput.audioCodec);
//        subCommand.addArgument(FFmpegArgs.FFmpegAudioArgs.SAMPLE_RATE, audioOutput.sampleRate);
//        subCommand.addArgument(FFmpegArgs.FFmpegAudioArgs.BITRATE, audioOutput.bitrate);
//        subCommand.addArgument(FFmpegArgs.FFmpegAudioArgs.AUDIO_CHANNEL, audioOutput.audioChannel);
//
//        return subCommand;
//    }
//
//
//    public static class FFmpegArgs {
//        public static String VERBOSE = "-v";
//        public static String OVERRIDE_OUTPUT = "-y";
//        public static String INPUT = "-i";
//        public static String HARDWARE_ACCELERATION = "-hwaccel";
//        public static String HARDWARE_ACCELERATION_OUTPUT_FORMAT = "-hwaccel_output_format";
//        public static String MOV_FLAGS = "-movflags";
//
//        private static class FFmpegVideoArgs {
//            public static String SCALE_VIDEO = "-s";
//            public static String CODEC = "-c:v";
//            public static String CONSTANT_RATE_FACTOR = "-crf";
//            public static String TUNE = "-tune";
//            public static String PROFILE = "-profile:v";
//            public static String LEVEL = "-level:v";
//            public static String MAX_RATE = "-maxrate";
//            public static String BUFFER_SIZE = "-bufsize";
//            public static String FRAME_RATE = "-f";
//            public static String MIN_INTERVAL_BTW_KEYFRAMES = "-keyint_min";
//            public static String GROUP_OF_PICTURES = "-g";
//            public static String SCENE_CHANGE_THRESHOLD = "-sc_threshold";
//            public static String PIXEL_FORMAT = "-pix_format";
//        }
//
//        private static class FFmpegAudioArgs {
//            public static String CODEC = "-c:a";
//            public static String SAMPLE_RATE = "-ar";
//            public static String AUDIO_CHANNEL = "-ac";
//            public static String BITRATE = "-b:a";
//        }
//    }
}