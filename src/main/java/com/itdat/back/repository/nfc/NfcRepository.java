package com.itdat.back.repository.nfc;

import com.itdat.back.entity.nfc.NfcEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NfcRepository extends JpaRepository<NfcEntity, Integer> {
}