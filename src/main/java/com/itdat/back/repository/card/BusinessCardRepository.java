package com.itdat.back.repository.card;


import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.card.BusinessCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessCardRepository extends JpaRepository<BusinessCard, Integer> {

    List<BusinessCard> findByUserEmail(String userEmail);
    List<BusinessCard> findByIsPublicTrue();
    void deleteByUserEmail(String email);

    //Optional<BusinessCard> findByUserEmailAndCardNo(String userEmail, int cardNo);

    BusinessCard findByCardNoAndUserEmail(Integer cardNo, String userEmail);

}