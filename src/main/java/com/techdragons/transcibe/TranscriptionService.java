package com.techdragons.transcibe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TranscriptionService {

    public String recognizeSpeechFromMedia(String mediaFilePath) throws IOException, InterruptedException {
        log.info("Starting speech recognition for file: {}", mediaFilePath);

        List<String> command = new ArrayList<>();
        command.add("python");
        command.add("-m");
        command.add("whisper");
        command.add(mediaFilePath);
        command.add("--model");
        command.add("medium");
        command.add("--task");
        command.add("translate");

        log.info("Command for process builder: {}", command);

        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        log.info("Process started, reading output...");

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            log.debug("Read line: {}", line);
            output.append(line).append("\n");
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            log.error("Whisper exited with error code: {}", exitCode);
            throw new IOException("Whisper exited with error code: " + exitCode);
        }

        log.info("Speech recognition completed successfully.");
        log.info(String.valueOf(output));
        return output.toString().trim();
    }
}
