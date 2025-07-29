package com.club69.mediaconvert.function;

import com.club69.mediaconvert.exception.ProcessExecutorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ProcessExecutor {

    public ProcessExecutorResponse run(List<String> commands) {
        String output = "";
        int exitCode = -1;
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.redirectErrorStream(true);
        Process p = null;

        try {
            p = pb.start();
            output = this.processOutput(p.getInputStream());
            exitCode = p.waitFor();

        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
            return ProcessExecutorResponse.builder()
                    .exitCode(exitCode)
                    .success(false)
                    .errorMessage(e.getMessage()).build();

        } finally {
            if (p != null) p.destroy();
        }

        return ProcessExecutorResponse.builder()
                .exitCode(exitCode)
                .success(true)
                .output(output).build();
    }

    private String processOutput(InputStream inputStream) throws IOException {
        String output = "";
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            output = reader.lines().collect(Collectors.joining("\n"));

        } finally {
            if (reader != null) reader.close();
            if (inputStream != null) inputStream.close();
        }

        return output;
    }


}
