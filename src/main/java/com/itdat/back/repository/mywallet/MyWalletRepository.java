package com.itdat.back.repository.mywallet;

import com.itdat.back.entity.nfc.MyWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyWalletRepository extends JpaRepository<MyWallet, Integer> {
    List<MyWallet> findByUserEmail(String myEmail);
}
