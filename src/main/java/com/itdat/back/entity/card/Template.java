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


    public Template() {
    }

    public Template(int templateId, String svgUrl) {
        this.templateId = templateId;
        this.svgUrl = svgUrl;
    }

    public int getTemplateId() {
        return templateId;
    }

    public String getSvgUrl() {
        return svgUrl;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public void setSvgUrl(String svgUrl) {
        this.svgUrl = svgUrl;
    }
}