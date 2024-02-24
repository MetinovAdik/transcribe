package com.techdragons.transcibe;

public class TranscriptionRequest {

    private String mediaUrl;
    private String mediaType; // "audio" или "video"
    private String password; // Поле для пароля

    // Геттеры и сеттеры для всех полей, включая password
    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "TranscriptionRequest{" +
                "mediaUrl='" + mediaUrl + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}