package com.itdat.back.entity.card;

import jakarta.persistence.*;

import lombok.*;

@Entity
public class Template {

    @Id
    @Column(name = "template_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int templateId;

    @Column(name = "svg_url")
    private String svgUrl;


    public int getTemplateId() {
        return templateId;
    }

    public String getSvgUrl() {
        return svgUrl;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public Template() {
    }

    public void setSvgUrl(String svgUrl) {
        this.svgUrl = svgUrl;
    }

    public Template(int templateId, String svgUrl) {
        this.templateId = templateId;
        this.svgUrl = svgUrl;
    }

    @Override
    public String toString() {
        return "Template{" +
                "templateId=" + templateId +
                ", svgUrl='" + svgUrl + '\'' +
                '}';
    }


}
