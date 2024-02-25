package com.techdragons.transcibe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
public class TranscriptionController {

    @Autowired
    private FileDownloadService fileDownloadService;

    @Autowired
    private TranscriptionService transcriptionService;


    private String servicePassword = "*YnG}5CgT;7[F-HP%N(`At£aj^*8o/e.}lUPO13H='?K~.h3-9";

    @PostMapping("/transcribe")
    public CompletableFuture<ResponseEntity<String>> transcribe(@RequestBody TranscriptionRequest request) throws IOException {
        if (!servicePassword.equals(request.getPassword())) {
            return CompletableFuture.completedFuture(new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED));
        }

        return fileDownloadService.downloadMedia(request.getMediaUrl(), request.getMediaType())
                .thenCompose(downloadedFile -> {
                    try {
                        return transcriptionService.recognizeSpeechFromMedia(downloadedFile.toString())
                                .thenApply(transcription -> {
                                    try {
                                        System.out.println("Successfully transcribed file");
                                        System.out.println(transcription);
                                        Files.deleteIfExists(downloadedFile);
                                        return new ResponseEntity<>(transcription, HttpStatus.OK);
                                    } catch (Exception e) {
                                        log.error("Error deleting the file", e);
                                        return new ResponseEntity<>("Error processing the media file", HttpStatus.INTERNAL_SERVER_ERROR);
                                    }
                                });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .handle((response, ex) -> {
                    if (ex != null) {
                        log.error("Error processing the media file", ex);
                        return new ResponseEntity<>("Error processing the media file", HttpStatus.INTERNAL_SERVER_ERROR);
                    } else {
                        return response;
                    }
                });
    }
}