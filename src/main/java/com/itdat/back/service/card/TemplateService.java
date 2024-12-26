package com.itdat.back.service.card;
import com.itdat.back.entity.card.Template;
import com.itdat.back.repository.card.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TemplateService {

    @Autowired
    TemplateRepository templateRepository;


    public List<Template> getAllTemplates() {
        List<Template> templates = templateRepository.findAll();
        return templates.stream()
                .map(template -> new Template(template.getTemplateId(), template.getSvgUrl()))
                .collect(Collectors.toList());
    }


    public Template getTemplateById(int Id){

        Template template = templateRepository.findByTemplateId(Id);
        if(Objects.isNull(template)){
            return null;
        }
        return template;
    }
}