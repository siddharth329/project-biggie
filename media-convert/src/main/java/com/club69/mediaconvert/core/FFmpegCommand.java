package com.club69.mediaconvert.core;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FFmpegCommand {
    private final List<String> commandArgs = new ArrayList<>();

    public void addArgument(String arg) { commandArgs.add(arg); }
    public void addArgument(String arg, String value) { if (value != null) commandArgs.addAll(List.of(arg, value)); }
    public void addArgument(String arg, Integer value) { if (value != null) commandArgs.addAll(List.of(arg, value.toString())); }
    public void addArgument(String arg, Long value) { if (value != null) commandArgs.addAll(List.of(arg, value.toString())); }
    public void addArgument(String arg, Double value) { if (value != null) commandArgs.addAll(List.of(arg, value.toString())); }

    public void addCommands(FFmpegCommand command) { commandArgs.addAll(command.getCommandArgs()); }

    public List<String> build() { return commandArgs; }
}
