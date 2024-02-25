package com.techdragons.transcibe;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class FileDownloadService {

    private final OkHttpClient httpClient = new OkHttpClient();
    @Async
    public CompletableFuture<Path> downloadMedia(String mediaUrl, String mediaType) throws IOException {
        log.info("Downloading media from URL: {}", mediaUrl);

        Request downloadRequest = new Request.Builder().url(mediaUrl).build();
        try (Response downloadResponse = httpClient.newCall(downloadRequest).execute()) {
            if (!downloadResponse.isSuccessful()) {
                log.error("Failed to download file from {}. Response: {}", mediaUrl, downloadResponse);
                throw new IOException("Failed to download file: " + downloadResponse);
            }

            // Создание временного файла с расширением в зависимости от типа медиа
            Path tempFile = Files.createTempFile(null, mediaType.equals("video") ? ".mp4" : ".oga");
            log.info("Temporary file created: {}", tempFile.toString());

            // Копирование тела ответа во временный файл
            Files.copy(downloadResponse.body().byteStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
            log.info("Download successful. File saved to {}", tempFile);

            return CompletableFuture.completedFuture(tempFile);
        } catch (IOException e) {
            log.error("Error downloading media from {}: {}", mediaUrl, e.getMessage(), e);
            throw e;
        }
    }
}
