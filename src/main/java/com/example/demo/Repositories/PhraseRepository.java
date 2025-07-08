package com.example.demo.Repositories;


import com.example.demo.Models.Phrase;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface PhraseRepository extends JpaRepository<Phrase, Long> {
    List<Phrase> findByOriginalTextContainingIgnoreCase(String search);
    List<Phrase> findByCategoryIdAndOriginalTextContainingIgnoreCase(Long categoryId, String search);
    List<Phrase> findByCategoryId(Long categoryId);  // Add this method
}