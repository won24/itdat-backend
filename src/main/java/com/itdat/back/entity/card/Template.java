package com.itdat.back.entity.card;

import jakarta.persistence.*;

import java.util.Date;

@Table(name = "template")
@Entity
public class Template {

    @Id
    @Column(name = "template_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int templateId;

    @Column(name = "svg_url",  nullable = false)
    private String svgUrl;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    public Template() {
    }

    public Template(int templateId, String svgUrl, String thumbnailUrl) {
        this.templateId = templateId;
        this.svgUrl = svgUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public String getSvgUrl() {
        return svgUrl;
    }

    public void setSvgUrl(String svgUrl) {
        this.svgUrl = svgUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public String toString() {
        return "Template{" +
                "templateId=" + templateId +
                ", svgUrl='" + svgUrl + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                '}';
    }
}