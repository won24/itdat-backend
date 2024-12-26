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

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    public Template(int templateId, String svgUrl) {
    }

    public Template() {
    }

    public Template(int templateId, String svgUrl, Date createdAt) {
        this.templateId = templateId;
        this.svgUrl = svgUrl;
        this.createdAt = createdAt;
    }

    public int getTemplateId() {
        return templateId;
    }

    public String getSvgUrl() {
        return svgUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public void setSvgUrl(String svgUrl) {
        this.svgUrl = svgUrl;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Template{" +
                "templateId=" + templateId +
                ", svgUrl='" + svgUrl + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
