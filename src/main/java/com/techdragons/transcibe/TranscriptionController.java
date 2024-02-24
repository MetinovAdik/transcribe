package com.techdragons.transcibe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
public class TranscriptionController {

    private static final Logger logger = LoggerFactory.getLogger(TranscriptionController.class);
    @Autowired
    private FileDownloadService fileDownloadService;

    @Autowired
    private TranscriptionService transcriptionService;


    private String servicePassword = "*YnG}5CgT;7[F-HP%N(`At£aj^*8o/e.}lUPO13H='?K~.h3-9";

    @PostMapping("/transcribe")
    public ResponseEntity<String> transcribe(@RequestBody TranscriptionRequest request) {
        logger.info("Получен запрос на транскрипцию для URL: {}", request.getMediaUrl());
        if (!servicePassword.equals(request.getPassword())) {
            logger.warn("Неавторизованный доступ с паролем: {}", request.getPassword());
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            Path downloadedFile = fileDownloadService.downloadMedia(request.getMediaUrl(), request.getMediaType());
            logger.info("Файл успешно скачан и сохранен как: {}", downloadedFile.toString());
            String transcription = transcriptionService.recognizeSpeechFromMedia(downloadedFile.toString());
            logger.info("Транскрипция завершена");
            Files.delete(downloadedFile);
            return new ResponseEntity<>(transcription, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Ошибка при обработке медиафайла", e);
            return new ResponseEntity<>("Error processing the media file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}