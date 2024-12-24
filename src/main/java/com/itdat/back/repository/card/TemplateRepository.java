package com.itdat.back.repository.card;

import com.itdat.back.entity.card.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Integer> {

    Template findById(int id);
}
