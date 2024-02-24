package com.techdragons.transcibe;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class FileDownloadService {

    private final OkHttpClient httpClient = new OkHttpClient();

    public Path downloadMedia(String mediaUrl, String mediaType) throws IOException {
        Request downloadRequest = new Request.Builder().url(mediaUrl).build();
        try (Response downloadResponse = httpClient.newCall(downloadRequest).execute()) {
            if (!downloadResponse.isSuccessful()) {
                throw new IOException("Failed to download file: " + downloadResponse);
            }

            // Создание временного файла с расширением в зависимости от типа медиа
            Path tempFile = Files.createTempFile(null, mediaType.equals("video") ? ".mp4" : ".oga");

            // Копирование тела ответа во временный файл
            Files.copy(downloadResponse.body().byteStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            return tempFile;
        }
    }

}