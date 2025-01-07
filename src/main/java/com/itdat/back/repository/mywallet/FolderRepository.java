package com.itdat.back.repository.mywallet;

import com.itdat.back.entity.mywallet.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Integer> {
    Optional<Folder> findByUserEmailAndFolderName(String userEmail, String folderName);

    List<Folder> findByUserEmail(String userEmail);
}