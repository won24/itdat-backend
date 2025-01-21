package com.itdat.back.entity.qna;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "qna-images")
public class QnaImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 ID

    @Column(nullable = false)
    private String fileName; // 원본 파일 이름

    @Column(nullable = false)
    private String fileUrl; // 저장된 이미지의 URL

    @Column(nullable = false)
    private String fileType; // 파일 형식 (예: image/png, image/jpeg)

    @Column(nullable = false)
    private Long fileSize; // 파일 크기 (바이트 단위)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 업로드된 시간

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public QnaImage() {
    }

    public QnaImage(Long id, String fileName, String fileUrl, String fileType, Long fileSize, LocalDateTime createdAt) {
        this.id = id;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "QnaImage{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", createdAt=" + createdAt +
                '}';
    }
}
