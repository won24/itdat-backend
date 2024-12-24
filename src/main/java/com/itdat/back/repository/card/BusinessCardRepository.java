package com.itdat.back.repository.card;


import com.itdat.back.entity.card.BusinessCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessCardRepository extends JpaRepository<BusinessCard, Integer> {

    List<BusinessCard> findByUserId(String userId);

}
