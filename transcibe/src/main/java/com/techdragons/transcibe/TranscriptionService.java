package com.techdragons.transcibe;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TranscriptionService {

    public String recognizeSpeechFromMedia(String mediaFilePath) throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add("python"); // Замените на полный путь к Python, если он не в PATH
        command.add("-m");
        command.add("whisper");
        command.add(mediaFilePath);
        command.add("--model");
        command.add("small"); // Используем модель medium для баланса между скоростью и точностью
        //command.add("--task");
        //command.add("translate"); // Добавляем задачу перевода на английский

        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder filteredOutput = new StringBuilder();
        String line;
        Pattern pattern = Pattern.compile("\\]\\s+(.+)$"); // Регулярное выражение может потребовать настройки

        while ((line = reader.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                // Добавляем только распознанный и переведенный текст, исключая всё остальное
                filteredOutput.append(matcher.group(1)).append("\n");
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Whisper exited with error code: " + exitCode);
        }

        return filteredOutput.toString().trim(); // Возвращаем фильтрованный и переведенный текст
    }
}