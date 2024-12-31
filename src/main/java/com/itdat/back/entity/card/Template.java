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

    public Template() {
    }

    public Template(int templateId) {
        this.templateId = templateId;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }



    @Override
    public String toString() {
        return "Template{" +
                "templateId=" + templateId +
                '}';
    }
}