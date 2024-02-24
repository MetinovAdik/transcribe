package com.techdragons.transcibe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class TranscriptionController {

    @Autowired
    private FileDownloadService fileDownloadService;

    @Autowired
    private TranscriptionService transcriptionService;

    @Value("${transcription.service.password}")
    private String servicePassword;

    @PostMapping("/transcribe")
    public ResponseEntity<String> transcribe(@RequestBody TranscriptionRequest request) {
        System.out.println(request.getPassword());
        System.out.println(servicePassword);
        if (!servicePassword.equals(request.getPassword())) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            Path downloadedFile = fileDownloadService.downloadMedia(request.getMediaUrl(), request.getMediaType());
            String transcription = transcriptionService.recognizeSpeechFromMedia(downloadedFile.toString());
            Files.delete(downloadedFile);
            return new ResponseEntity<>(transcription, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error processing the media file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}